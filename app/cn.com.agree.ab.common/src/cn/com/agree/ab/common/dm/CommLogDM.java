package cn.com.agree.ab.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class CommLogDM extends BasicDM {
	private static final long serialVersionUID = 3555747131541260214L;
	private String uuid;
	private String tranDate;
	private String tellerCode;
	private String tranSeq;
	private String commCode;
	private String systemCode;
	private String frontSeq;
	private String frontCode;
	private String frontName;
	private Long   tranTime;
	private Long   commTime;
	private String orgCode;
	private String authTellerCode;
	private String businessType;
	private Integer rspStatus;
	private String accDate;
	private String accTime;
	private String rspSeq;
	private String rspMsg;
	private byte[] reqData;
	private String sourceSeq;
	private String debitAccountNum;
	private String debitAccountName;
	private String debitAccountSubNum;
	private String debitAccountSeq;
	private String creditAccountNum;
	private String creditAccountName;
	private Double money;
	private String currType;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getTellerCode() {
		return tellerCode;
	}
	public void setTellerCode(String tellerCode) {
		this.tellerCode = tellerCode;
	}
	public String getTranSeq() {
		return tranSeq;
	}
	public void setTranSeq(String tranSeq) {
		this.tranSeq = tranSeq;
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
	public String getFrontSeq() {
		return frontSeq;
	}
	public void setFrontSeq(String frontSeq) {
		this.frontSeq = frontSeq;
	}
	public String getFrontCode() {
		return frontCode;
	}
	public void setFrontCode(String frontCode) {
		this.frontCode = frontCode;
	}
	public String getFrontName() {
		return frontName;
	}
	public void setFrontName(String frontName) {
		this.frontName = frontName;
	}
	public Long getTranTime() {
		return tranTime;
	}
	public void setTranTime(Long tranTime) {
		this.tranTime = tranTime;
	}
	public Long getCommTime() {
		return commTime;
	}
	public void setCommTime(Long commTime) {
		this.commTime = commTime;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getAuthTellerCode() {
		return authTellerCode;
	}
	public void setAuthTellerCode(String authTellerCode) {
		this.authTellerCode = authTellerCode;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public Integer getRspStatus() {
		return rspStatus;
	}
	public void setRspStatus(Integer rspStatus) {
		this.rspStatus = rspStatus;
	}
	public String getAccDate() {
		return accDate;
	}
	public void setAccDate(String accDate) {
		this.accDate = accDate;
	}
	public String getAccTime() {
		return accTime;
	}
	public void setAccTime(String accTime) {
		this.accTime = accTime;
	}
	public String getRspSeq() {
		return rspSeq;
	}
	public void setRspSeq(String rspSeq) {
		this.rspSeq = rspSeq;
	}
	public String getRspMsg() {
		return rspMsg;
	}
	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}
	public byte[] getReqData() {
		return reqData;
	}
	public void setReqData(byte[] reqData) {
		this.reqData = reqData;
	}
	public String getSourceSeq() {
		return sourceSeq;
	}
	public void setSourceSeq(String sourceSeq) {
		this.sourceSeq = sourceSeq;
	}
	public String getDebitAccountNum() {
		return debitAccountNum;
	}
	public void setDebitAccountNum(String debitAccountNum) {
		this.debitAccountNum = debitAccountNum;
	}
	public String getDebitAccountName() {
		return debitAccountName;
	}
	public void setDebitAccountName(String debitAccountName) {
		this.debitAccountName = debitAccountName;
	}
	public String getDebitAccountSubNum() {
		return debitAccountSubNum;
	}
	public void setDebitAccountSubNum(String debitAccountSubNum) {
		this.debitAccountSubNum = debitAccountSubNum;
	}
	public String getDebitAccountSeq() {
		return debitAccountSeq;
	}
	public void setDebitAccountSeq(String debitAccountSeq) {
		this.debitAccountSeq = debitAccountSeq;
	}
	public String getCreditAccountNum() {
		return creditAccountNum;
	}
	public void setCreditAccountNum(String creditAccountNum) {
		this.creditAccountNum = creditAccountNum;
	}
	public String getCreditAccountName() {
		return creditAccountName;
	}
	public void setCreditAccountName(String creditAccountName) {
		this.creditAccountName = creditAccountName;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public String getCurrType() {
		return currType;
	}
	public void setCurrType(String currType) {
		this.currType = currType;
	}
	
	
}
