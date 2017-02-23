package cn.com.agree.ab.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class CommCodeDM extends BasicDM {
	private static final long serialVersionUID = -4398269665402513339L;
	
	private Integer id;
	private String commCode;
	private String systemCode;
	private String transCode;
	private String channelCode;
	private String itable;
	private String otable;
	private Integer flag;
	private Integer timeOut;
	private String remark;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCommCode() {
		return commCode;
	}
	public void setCommCode(String commCode) {
		this.commCode = commCode;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getTransCode() {
		return transCode;
	}
	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getItable() {
		return itable;
	}
	public void setItable(String itable) {
		this.itable = itable;
	}
	public String getOtable() {
		return otable;
	}
	public void setOtable(String otable) {
		this.otable = otable;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Integer getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
