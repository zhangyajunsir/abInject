package jsrccb.common.biz.impl.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;

import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthType;
import jsrccb.common.dm.rsp.AfaExtDM;
import jsrccb.common.dm.rsp.EsbRspDM;
import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.biz.CommBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = CommBiz.class, multiple = true)
@Singleton
@Biz("afaCommBiz") 
public class CommBizAFAImpl extends CommBizESBImpl {
	
	@Override
	public String systemCode() {
		return "afa";
	}
	
	protected void packESBData(CommCodeDM commCodeDM, PacketReqDM packetReqDM) {
		super.packESBData(commCodeDM, packetReqDM);
		Map<String, Object> innerArea = packetReqDM.getInnerArea();
		AuthDM authDM = (AuthDM)packetReqDM.getContext().get(ITradeContextKey.AUTH_DM);
		if (authDM != null && authDM.getAuthType().contains(AuthType.DYNAMIC_AUTH))
			innerArea.put("channelSeq", authDM.getAuthContext().get("lastServiceSN"));
		else
			innerArea.put("channelSeq", innerArea.get("serviceSN"));
		
	}

	protected void unpackESBData(CommCodeDM commCodeDM, PacketReqDM packetReqDM, PacketRspDM packetRspDM) {
		super.unpackESBData(commCodeDM, packetReqDM, packetRspDM);
		EsbRspDM esbRspDM = (EsbRspDM)packetRspDM.getTempArea().get(commCodeDM.getCommCode()+"_esbRspDM");
		AfaExtDM afaExtDM = (AfaExtDM)esbRspDM.getServiceBody().getExtAttributes();
		// 前置警告信息(也可以通过xml配置里的mappings实现)
		if (afaExtDM.getAfaInfo() != null && afaExtDM.getAfaInfo().getInfo() != null && !afaExtDM.getAfaInfo().getInfo().trim().equals("")) {
			List<Map<String,Object>> msgList = packetRspDM.getMsgList();
			if (msgList == null) {
				msgList = new ArrayList<Map<String,Object>>();
				packetRspDM.setMsgList(msgList);
			}
			
			msgList.add(ImmutableMap.of("afaInfo", (Object)afaExtDM.getAfaInfo().getInfo()));
		}
	}
}
