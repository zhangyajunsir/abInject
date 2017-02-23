package jsrccb.common.biz.impl.comm;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthLevel;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthType;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.CommBiz;
import cn.com.agree.ab.common.biz.impl.AbstractCommBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.CommLogDM;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.exception.AgainRpcException;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.communication.CommClientProvider;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.PacketManger;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.trade.core.tools.PictureUtil;
import cn.com.agree.ab.trade.core.tools.StringUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = CommBiz.class, multiple = true)
@Singleton
@Biz("cbodCommBiz") 
public class CommBizCBODImpl extends AbstractCommBiz {
	@Inject
	private PacketManger packetManger;
	@Inject
	private CommClientProvider commClientProvider;
	
	@Override
	public PacketRspDM exchange(CommCodeDM commCodeDM, PacketReqDM packetReqDM) {
		byte[] input = packCBODData(commCodeDM, packetReqDM);
		byte[] ouput = commClientProvider.transform(
				commCodeDM.getChannelCode(), 
				packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR).toString(), 
				commCodeDM.getTimeOut().toString(), 
				input);
		
		if (0x00 != ouput[0] || 0x00 != ouput[1]) {
			try {
				throw new BizException("交易网关失败〖"+new String(ouput, "GBK")+"〗");
			} catch (UnsupportedEncodingException e) {
			}
		}
		byte[] cbodResponseBuff = new byte[ouput.length - 2];
		System.arraycopy(ouput, 2, cbodResponseBuff, 0, ouput.length - 2);
		return unpackCBODData(commCodeDM, packetReqDM, cbodResponseBuff);
	}

	@Override
	public String systemCode() {
		return "host";
	}

	@SuppressWarnings({ "unchecked" })
	private byte[] packCBODData(CommCodeDM commCodeDM, PacketReqDM packetReqDM) {
		Map<String, Object> innerArea = packetReqDM.getInnerArea();
		
		if (packetReqDM.getContext().get(ITradeContextKey.AUTH_DM) != null || packetReqDM.getContext().get(ITradeContextKey.PAGING_DM) != null) {
			innerArea.put("MSG_STATUS", 36864);
		}
		if ("CM0999900".equals(commCodeDM.getTransCode())) {
			innerArea.put("MSG_STATUS", 0);
		}
		if (packetReqDM.getTempArea().get("TX_TYP") != null && "1".equals(packetReqDM.getTempArea().get("TX_TYP"))) {
			// 冲正时，不上送前端流水
			innerArea.put("OFFLINE_TX_NO", "");
		} else if (ContextHelper.getContext(ITradeContextKey.COMM_LOG_DM) != null) {
			CommLogDM commLogDM = (CommLogDM)ContextHelper.getContext(ITradeContextKey.COMM_LOG_DM);
			if (commLogDM != null && commLogDM.getTranSeq() != null) {
				innerArea.put("OFFLINE_TX_NO", commLogDM.getTranSeq());
			}
		}
		// 授权处理 SPV_PWD AUT_REQ_NO
		if (packetReqDM.getContext().get(ITradeContextKey.AUTH_DM) != null) {
			AuthDM authDM = (AuthDM)packetReqDM.getContext().get(ITradeContextKey.AUTH_DM);
			if (authDM.getAuthStatus() == AuthStatus.PASS_AUTH) {
				innerArea.put("SPV_PWD", StringUtil.fixFill(authDM.getAuthTellerAPWD() == null ? "" : authDM.getAuthTellerAPWD() , " ", -16) + StringUtil.fixFill(authDM.getAuthTellerBPWD() == null ? "" : authDM.getAuthTellerBPWD() , " ", -16));
				if (authDM.getAuthTellerA() != null && authDM.getAuthTellerA().length() > 9 && !authDM.getAuthTellerA().substring(0, 9).equals(packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR))) {
					innerArea.put("AUT_REQ_NO", "9");
				}
				if (authDM.getAuthTellerB() != null && authDM.getAuthTellerB().length() > 9 && !authDM.getAuthTellerB().substring(0, 9).equals(packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR))) {
					innerArea.put("AUT_REQ_NO", "9");
				}
			}
		}
		
		// Format上送字节数组
		Packet<PacketReqDM> reqPacket = (Packet<PacketReqDM>)packetManger.getPacket(commCodeDM.getCommCode(), PacketType.REQ);
		byte[] reqByteArray = null;
		try {
			reqByteArray = reqPacket.format(packetReqDM);
		} catch (TransformException e) {
			throw new BizException(e);
		}
		// 加上网关需要的报文头(2位不包含自身的总大小+6位核心交易码+9位机构号+12柜员号)
		ByteBuffer reqByteBuffer = ByteBuffer.allocate(29+reqByteArray.length);
		byte[] allLenBAL = MetadataTypeConverter.integer2ByteArrayH(27+reqByteArray.length);
		reqByteBuffer.put(allLenBAL[2]);
		reqByteBuffer.put(allLenBAL[3]);
		reqByteBuffer.put(commCodeDM.getTransCode().substring(3,9).getBytes());
		reqByteBuffer.put(packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR)   .toString().getBytes());
		reqByteBuffer.put(packetReqDM.getTellerInfo().get(ITradeKeys.G_TELLER).toString().getBytes());
		reqByteBuffer.put(reqByteArray);
		return reqByteBuffer.array();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PacketRspDM unpackCBODData(CommCodeDM commCodeDM, PacketReqDM packetReqDM, byte[] ouput) {
		// Parser下送字节数组
		Packet<PacketRspDM> rspPacket = (Packet<PacketRspDM>)packetManger.getPacket(commCodeDM.getCommCode(), PacketType.RSP);
		PacketRspDM packetRspDM;
		try {
			packetRspDM = rspPacket.parse(ouput);
		} catch (TransformException e) {
			throw new BizException(e);
		}
		Map<String, Object> innerArea = packetRspDM.getInnerArea();
		if ("CM0999900".equals(commCodeDM.getTransCode())) {
			String G_DATE = innerArea.get("G_DATE").toString();
			if (G_DATE != null && !G_DATE.equals("")) {
				packetRspDM.getTellerInfo().put("G_FDATE",           PictureUtil.format("\"9999/99/99\"", G_DATE));
				packetRspDM.getTellerInfo().put(ITradeKeys.G_DATE,   G_DATE);
				packetRspDM.getTellerInfo().put(ITradeKeys.G_YEAR,   G_DATE.substring(0, 4));
				packetRspDM.getTellerInfo().put(ITradeKeys.G_MONTH,  G_DATE.substring(4, 6));
				packetRspDM.getTellerInfo().put(ITradeKeys.G_DAY,    G_DATE.substring(6, 8));
			}
		}
		if (innerArea.get("HOST_PROC_TIME") != null && !innerArea.get("HOST_PROC_TIME").equals(""))
			packetRspDM.getStoreData().put("G_FTIME", PictureUtil.format("\"99:99:99\"", innerArea.get("HOST_PROC_TIME").toString().substring(0, 6)));
		if (packetRspDM.getContext().get("TX_LOG_NO") != null) {
			packetRspDM.getStoreData().put("G_SERIALNO",  packetRspDM.getContext().get("TX_LOG_NO").toString());
			packetRspDM.getStoreData().put("G_POSTSERNO", packetRspDM.getContext().get("TX_LOG_NO").toString().substring(12, 18));
		}
		CommLogDM commLogDM = (CommLogDM)ContextHelper.getContext(ITradeContextKey.COMM_LOG_DM);
		if (commLogDM != null) {
			commLogDM.setAccDate((String)innerArea.get("G_DATE"));
			commLogDM.setAccDate((String)innerArea.get("HOST_PROC_TIME"));
			if (packetRspDM.getContext().get("TX_LOG_NO") != null)
				commLogDM.setRspSeq(packetRspDM.getContext().get("TX_LOG_NO").toString());
		}
		Map<String, String> formDataMap = (Map<String, String>)packetRspDM.getTempArea().get(commCodeDM.getCommCode()+"_RSP");
		int iFlag = 0;
		for (String formID : formDataMap.keySet()) {
			if (formID.equals("FFFFFFFF")) {

			} else if (formID.equals("99999999")) { // 批量输入
				iFlag = 4;
			} else if (formID.equals("99999998")) { // 错误信息
				
			} else if (formID.equals("99999997")) { // 大量输入
				iFlag = 2;
			} else if (formID.equals("99999996")) { // 状态清除

			} else if (formID.equals("99999995")) { // 大量输出
				iFlag = 3;
			} else if (formID.equals("SFEEFRM0")) { // 费用输出
				iFlag = 5;
			} else { // 正常交易
				iFlag = 1;
			}
		}
		if (innerArea.get("TX_STATUS").equals("0")) { // 后台返回处理成功
			if (iFlag == 3) { // 大量输出标志（备用）
			}
			if (iFlag == 1) { // 正常交易标志
				if (innerArea.get("MSG_STATUS").equals(36864)) {// 如果是联线报表下载交易, 重新发送交易数据.
				}
			}
			if (iFlag == 2) { // 大量输入标志（备用）
			}
			if (iFlag == 5) { // 费用工厂
				// TODO 费用工厂
				
				// END
			}
		} 
		if (innerArea.get("TX_STATUS").equals("1")) { // 后台返回处理失败
			if (commLogDM != null) {
				commLogDM.setRspStatus(0);
				commLogDM.setRspMsg(packetRspDM.getMsgList().toString());
			}
			if (innerArea.get("RESP_CODE").equals("3")) {
				boolean needConfirm = false;
				List<Map> msgList = (List<Map>)innerArea.get("msgList");
				if(msgList != null){
					for (Map msg : msgList) {
						if (msg.get("msgCode").toString().startsWith("C")) {
							needConfirm = true;
							break;
						}
					}
				}
				if (needConfirm) {
					// 交易预警
					packetRspDM.getTempArea().put("CONFIRM_FLG", "Y");
					// END
					RpcException rpcException = new AgainRpcException(ExceptionLevel.WARN, "交易预警");
					rpcException.setMsg(packetRspDM.getMsgList());
					throw rpcException;
				} else if (!innerArea.get("AUT_REQ_NO") .toString().trim().equals("")
						&& !innerArea.get("AUT_FIX_BRH").toString().trim().equals("")
						&& !innerArea.get("AUT_FIX_BRH").equals(packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR))) {
					// TODO 跨机构授权（远程授权）
					
					// END
					RpcException rpcException = new AgainRpcException(ExceptionLevel.DEBUG, "远程授权");	// DEBUG的RPC不会提示
					rpcException.setMsg(packetRspDM.getMsgList());
					throw rpcException;
				} else {
					// 交易模式为主机要求授权（动态授权）
					/**参照原规授权则翻译 2016年4月9日15:15:56 by qgy*/
					AuthDM authDM = (AuthDM)packetReqDM.getContext().get(ITradeContextKey.AUTH_DM);
					if (authDM == null) {
						authDM =  new AuthDM();
						packetReqDM.getContext().put(ITradeContextKey.AUTH_DM, authDM);
					}
					// 计算动态授权等级
					AuthLevel authLevel = null;
					StringBuilder authMsg = new StringBuilder("〖主机授权〗");
					for (Object msg : packetRspDM.getMsgList()) {
						Map cbodMsg = (Map)msg;
						String cbodAuthLevel = cbodMsg.get("msgCode").toString().substring(0, 1);
						if ("B".equals(cbodAuthLevel) && (authLevel == null || authLevel.getLevel() < AuthLevel.B.getLevel())) {
							authLevel = AuthLevel.B;
						}
						if ("A".equals(cbodAuthLevel) && (authLevel == null || authLevel.getLevel() < AuthLevel.A.getLevel())) {
							if (authLevel == AuthLevel.B)
								authLevel =  AuthLevel.AB;
							else
								authLevel =  AuthLevel.A;
						}
						if ("X".equals(cbodAuthLevel)) {
							authLevel = AuthLevel.AB;
						}
						authMsg.append("[").append(cbodMsg.get("msgCode")).append("]").append(cbodMsg.get("msgTxt")).append(cbodMsg.get("msgDbTxt")).append(",");
					}
					authMsg.deleteCharAt(authMsg.length()-1); 
					if (authLevel != null) {
						authDM.setAuthStatus(AuthStatus.NEED_AUTH);
						if (authDM.getAuthLevel() == null || authDM.getAuthLevel().getLevel() < authLevel.getLevel()) {
							authDM.setAuthLevel(authLevel);
						}
						authDM.getAuthType().add(AuthType.DYNAMIC_AUTH);
						authDM.getAuthMSG() .add(authMsg.toString());
					}
					// END
					RpcException rpcException = new AgainRpcException(ExceptionLevel.DEBUG, "动态授权");// DEBUG的RPC不会提示
					rpcException.setMsg(packetRspDM.getMsgList());
					throw rpcException;
				}
			} else {
				// HOST ERROR
				RpcException rpcException = new RpcException(ExceptionLevel.ERROR, "后台报错");
				rpcException.setMsg(packetRspDM.getMsgList());
				throw rpcException;
			}
		}
		if (commLogDM != null) {
			commLogDM.setRspStatus(1);
		}
		return packetRspDM;
	}
	
	
	
}
