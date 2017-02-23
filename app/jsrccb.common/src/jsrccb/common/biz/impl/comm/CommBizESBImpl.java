package jsrccb.common.biz.impl.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;

import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthLevel;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthType;
import jsrccb.common.dm.rsp.EsbExtDM;
import jsrccb.common.dm.rsp.EsbRspDM;
import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.CommBiz;
import cn.com.agree.ab.common.biz.impl.AbstractCommBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.exception.AgainRpcException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.communication.CommClientProvider;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.PacketManger;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.trade.core.tools.StringUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = CommBiz.class, multiple = true)
@Singleton
@Biz("esbCommBiz") 
public class CommBizESBImpl extends AbstractCommBiz {
	@Inject
	protected PacketManger packetManger;
	@Inject
	protected CommClientProvider commClientProvider;
	
	@SuppressWarnings("unchecked")
	@Override
	public PacketRspDM exchange(CommCodeDM commCodeDM, PacketReqDM packetReqDM) {
		packESBData(commCodeDM, packetReqDM);
		// Format上送字节数组
		Packet<PacketReqDM> reqPacket = (Packet<PacketReqDM>)packetManger.getPacket(commCodeDM.getCommCode(), PacketType.REQ);
		byte[] input = null;
		try {
			input = reqPacket.format(packetReqDM);
		} catch (TransformException e) {
			throw new BizException(e);
		}
		// end
		byte[] ouput = commClientProvider.transform(
				commCodeDM.getChannelCode(), 
				packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR).toString(), 
				commCodeDM.getTimeOut().toString(), 
				input);
		// Parser下送字节数组
		Packet<PacketRspDM> rspPacket = (Packet<PacketRspDM>)packetManger.getPacket(commCodeDM.getCommCode(), PacketType.RSP);
		PacketRspDM packetRspDM;
		try {
			packetRspDM = rspPacket.parse(ouput);
		} catch (TransformException e) {
			throw new BizException(e);
		}
		// end
		unpackESBData(commCodeDM, packetReqDM, packetRspDM);
		return packetRspDM;
	}

	@Override
	public String systemCode() {
		return "esb";
	}

	protected void packESBData(CommCodeDM commCodeDM, PacketReqDM packetReqDM) {
		Map<String, Object> innerArea = packetReqDM.getInnerArea();
		AuthDM authDM = (AuthDM)packetReqDM.getContext().get(ITradeContextKey.AUTH_DM);
		// 授权处理 SPV_PWD
		if (authDM != null && authDM.getAuthStatus() == AuthStatus.PASS_AUTH) {
			innerArea.put("SPV_PWD", StringUtil.fixFill(authDM.getAuthTellerAPWD() == null ? "" : authDM.getAuthTellerAPWD() , " ", -16) + StringUtil.fixFill(authDM.getAuthTellerBPWD() == null ? "" : authDM.getAuthTellerBPWD() , " ", -16));
		}
		if (authDM != null && authDM.getAuthType().contains(AuthType.DYNAMIC_AUTH))
			// 动态授权
			innerArea.put("serviceSN", "11"+((String)packetReqDM.getTellerInfo().get(ITradeKeys.G_TELLER)).substring(0,  8)+"9"+DateUtil.getDateToString().substring(8, 14));
		else
			innerArea.put("serviceSN", "11"+((String)packetReqDM.getTellerInfo().get(ITradeKeys.G_TELLER)).substring(0, 10)+"9"+DateUtil.getDateToString().substring(8, 14));
		innerArea.put("serviceTime", packetReqDM.getTellerInfo().get(ITradeKeys.G_DATE)+DateUtil.getDateToString().substring(8,14));
		// 翻页上送已模板里处理
		
	}
	
	protected void unpackESBData(CommCodeDM commCodeDM, PacketReqDM packetReqDM, PacketRspDM packetRspDM) {
		EsbRspDM esbRspDM = (EsbRspDM)packetRspDM.getTempArea().get(commCodeDM.getCommCode()+"_esbRspDM");
		EsbExtDM esbExtDM = (EsbExtDM)esbRspDM.getServiceBody().getExtAttributes();
		if (esbRspDM.getServiceHead().getRspStatus().getStatus().equals("FAIL")) {
			if ("3".equals(esbExtDM.getRespCode())) {
				// 动态授权
				AuthDM authDM = (AuthDM)packetReqDM.getContext().get(ITradeContextKey.AUTH_DM);
				if (authDM == null) {
					authDM =  new AuthDM();
					packetReqDM.getContext().put(ITradeContextKey.AUTH_DM, authDM);
				}
				// 计算动态授权等级
				AuthLevel authLevel = null;
				StringBuilder authMsg = new StringBuilder("〖"+systemCode()+"授权〗");
				EsbExtDM.OPM_MSG_GRP esbMsg = (EsbExtDM.OPM_MSG_GRP)esbExtDM.getMsgList().get(0);
				String esbAuthLevel = esbMsg.getOPM_MSG_CODE().substring(0,1);
				if ("B".equals(esbAuthLevel) && (authLevel == null || authLevel.getLevel() < AuthLevel.B.getLevel())) {
					authLevel = AuthLevel.B;
				}
				if ("A".equals(esbAuthLevel) && (authLevel == null || authLevel.getLevel() < AuthLevel.A.getLevel())) {
					if (authLevel == AuthLevel.B)
						authLevel =  AuthLevel.AB;
					else
						authLevel =  AuthLevel.A;
				}
				if ("X".equals(esbAuthLevel)) {
					authLevel = AuthLevel.AB;
				}
				authMsg.append("[").append(esbMsg.getOPM_MSG_CODE()).append("]").append(esbMsg.getOPM_MSG_TXT());
				if (authLevel != null) {
					authDM.setAuthStatus(AuthStatus.NEED_AUTH);
					if (authDM.getAuthLevel() == null || authDM.getAuthLevel().getLevel() < authLevel.getLevel()) {
						authDM.setAuthLevel(authLevel);
					}
					authDM.getAuthType().add(AuthType.DYNAMIC_AUTH);
					authDM.getAuthMSG() .add(authMsg.toString());
				}
				authDM.getAuthContext().put("lastServiceSN", packetReqDM.getInnerArea().get("serviceSN"));
				// END
				RpcException rpcException = new AgainRpcException(ExceptionLevel.DEBUG, "动态授权");// DEBUG的RPC不会提示
				rpcException.setMsg(packetRspDM.getMsgList());
				throw rpcException;
			} else {
				// 后台报错
				List<Map<String,Object>> msgList = packetRspDM.getMsgList();
				if (msgList == null) {
					msgList = new ArrayList<Map<String,Object>>();
					packetRspDM.setMsgList(msgList);
				}
				msgList.add(ImmutableMap.of(esbRspDM.getServiceHead().getRspStatus().getCode(), (Object)esbRspDM.getServiceHead().getRspStatus().getDesc()));
				RpcException rpcException = new RpcException(ExceptionLevel.ERROR, "后台报错");
				rpcException.setMsg(packetRspDM.getMsgList());
				throw rpcException;
			}
		}
		// 翻页下送处理 
		if (esbExtDM.getPageList() != null && esbExtDM.getPageList().size() > 0) {
			EsbExtDM.OPM_PAGE_CTL pageCtl = esbExtDM.getPageList().get(0);
			// 参照 @link{jsrccb.common.biz.impl.event.prop.MultiPageEventBizImpl.posCommit}，并仿造核心赋值
			packetRspDM.getTempArea().put("pageContext", ImmutableMap.of("staKey",pageCtl.getOPM_PAGE_STA_KEY(),"endKey",pageCtl.getOPM_PAGE_END_KEY(),"noData",pageCtl.getOPM_PAGE_NO_DATA()));
		}
		// TODO 费用工厂处理
		
		// END
	}
	
}
