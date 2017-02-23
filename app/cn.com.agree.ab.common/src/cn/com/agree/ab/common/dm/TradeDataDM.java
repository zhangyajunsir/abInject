package cn.com.agree.ab.common.dm;

import java.util.Map;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TradeDataDM extends BasicDM {
	private static final long serialVersionUID = 2642364672470905362L;
	
	/** Component数据 */
	private Map<String, Object> uiData;
	/** StoreData数据 */
	private Map<String, Object> storeData;
	/** TellerInfo数据 */
	private Map<String, Object> tellerInfo;
	/** AbstractCommonTrade的TempArea数据 */
	private Map<String, Object> tempArea;
	/** 上下文容器 */
	private Map<String, Object> context;

	public Map<String, Object> getUiData() {
		return uiData;
	}

	public void setUiData(Map<String, Object> uiData) {
		this.uiData = uiData;
	}

	public Map<String, Object> getStoreData() {
		return storeData;
	}

	public void setStoreData(Map<String, Object> storeData) {
		this.storeData = storeData;
	}

	public Map<String, Object> getTellerInfo() {
		return tellerInfo;
	}

	public void setTellerInfo(Map<String, Object> tellerInfo) {
		this.tellerInfo = tellerInfo;
	}

	public Map<String, Object> getTempArea() {
		return tempArea;
	}

	public void setTempArea(Map<String, Object> tempArea) {
		this.tempArea = tempArea;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}
}
