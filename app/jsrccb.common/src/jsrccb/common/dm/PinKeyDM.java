package jsrccb.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class PinKeyDM extends BasicDM {

	private static final long serialVersionUID = -5155552220754923106L;

	private String devId;
	private String gmDevId;
	private String mainKey;
	private String workKey;
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getMainKey() {
		return mainKey;
	}
	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}
	public String getWorkKey() {
		return workKey;
	}
	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}
	public String getGmDevId() {
		return gmDevId;
	}
	public void setGmDevId(String gmDevId) {
		this.gmDevId = gmDevId;
	}
}
