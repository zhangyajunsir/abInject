package cn.com.agree.ab.common.dm;

import java.util.HashMap;
import java.util.Map;

public class PacketReqDM extends TradeDataDM {
	private static final long serialVersionUID = 8886651110481348785L;
	
	/** 通讯业务逻辑内部数据区 */
	private Map<String, Object> innerArea  = new HashMap<String, Object>();
	/** 通讯配置信息 */
	private Map<String, CommCodeDM> commCodeDMMap = new HashMap<String, CommCodeDM>();
	/** 当前通讯码 */
	private String currentCommCode;
	

	public Map<String, Object> getInnerArea() {
		return innerArea;
	}

	public Map<String, CommCodeDM> getCommCodeDMMap() {
		return commCodeDMMap;
	}

	public String getCurrentCommCode() {
		return currentCommCode;
	}

	public void setCurrentCommCode(String currentCommCode) {
		this.currentCommCode = currentCommCode;
	}


}
