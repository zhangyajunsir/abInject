package cn.com.agree.ab.common.dm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketRspDM extends TradeDataDM {
	private static final long serialVersionUID = 8886651110481348785L;
	
	/** 通讯业务逻辑内部数据区 */
	private Map<String, Object> innerArea  = new HashMap<String, Object>();
	/** 通讯后台返回信息 */
	private List<Map<String,Object>> msgList;
	
	public static PacketRspDM newInstance() {
		PacketRspDM packetRspDM = new PacketRspDM();
		packetRspDM.setContext   (new HashMap<String, Object>());
		packetRspDM.setStoreData (new HashMap<String, Object>());
		packetRspDM.setTellerInfo(new HashMap<String, Object>());
		packetRspDM.setTempArea  (new HashMap<String, Object>());
		packetRspDM.setUiData    (new HashMap<String, Object>());
		return packetRspDM;
	}
	
	public Map<String, Object> getInnerArea() {
		return innerArea;
	}

	public void setInnerArea(Map<String, Object> innerArea) {
		this.innerArea = innerArea;
	}

	public List<Map<String,Object>> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<Map<String,Object>> msgList) {
		this.msgList = msgList;
	}




}
