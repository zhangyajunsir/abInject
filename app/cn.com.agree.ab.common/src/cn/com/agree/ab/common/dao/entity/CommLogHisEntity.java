package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_comm_log_his", primaryKey = {"uuid"})
public class CommLogHisEntity extends EntityDM {

	private static final long serialVersionUID = 1265467287068134617L;
	@Mapping(table = "ab_comm_log_his", columns = {"uuid"}, primaryKey = true)
	private String uuid;
	@Mapping(table = "ab_comm_log_his", columns = {"tran_date"}, requisite = true)
	private String tranDate;
	@Mapping(table = "ab_comm_log_his", columns = {"teller_code"}, requisite = true)
	private String tellerCode;
	@Mapping(table = "ab_comm_log_his", columns = {"tran_seq"}, requisite = true)
	private String tranSeq;
	@Mapping(table = "ab_comm_log_his", columns = {"comm_code"}, requisite = true)
	private String commCode;
	@Mapping(table = "ab_comm_log_his", columns = {"system_code"})
	private String systemCode;
	@Mapping(table = "ab_comm_log_his", columns = {"front_seq"})
	private String frontSeq;
	@Mapping(table = "ab_comm_log_his", columns = {"front_code"})
	private String frontCode;
	@Mapping(table = "ab_comm_log_his", columns = {"front_name"})
	private String frontName;
	@Mapping(table = "ab_comm_log_his", columns = {"tran_time"})
	private Integer tranTime;
	@Mapping(table = "ab_comm_log_his", columns = {"comm_time"})
	private Integer commTime;
	@Mapping(table = "ab_comm_log_his", columns = {"org_code"})
	private String orgCode;
	@Mapping(table = "ab_comm_log_his", columns = {"auth_teller_code"})
	private String authTellerCode;
	@Mapping(table = "ab_comm_log_his", columns = {"business_type"})
	private String businessType;
	@Mapping(table = "ab_comm_log_his", columns = {"rsp_status"})
	private Integer rspStatus;
	@Mapping(table = "ab_comm_log_his", columns = {"acc_date"})
	private String accDate;
	@Mapping(table = "ab_comm_log_his", columns = {"acc_time"})
	private String accTime;
	@Mapping(table = "ab_comm_log_his", columns = {"rsp_seq"})
	private String rspSeq;
	@Mapping(table = "ab_comm_log_his", columns = {"rsp_msg"})
	private String rspMsg;
	@Mapping(table = "ab_comm_log_his", columns = {"req_data"})
	private byte[] reqData;
	@Mapping(table = "ab_comm_log_his", columns = {"source_seq"})
	private String sourceSeq;
	@Mapping(table = "ab_comm_log_his", columns = {"debit_account_num"})
	private String debitAccountNum;
	@Mapping(table = "ab_comm_log_his", columns = {"debit_account_name"})
	private String debitAccountName;
	@Mapping(table = "ab_comm_log_his", columns = {"debit_account_sub_num"})
	private String debitAccountSubNum;
	@Mapping(table = "ab_comm_log_his", columns = {"debit_account_seq"})
	private String debitAccountSeq;
	@Mapping(table = "ab_comm_log_his", columns = {"credit_account_num"})
	private String creditAccountNum;
	@Mapping(table = "ab_comm_log_his", columns = {"credit_account_name"})
	private String creditAccountName;
	@Mapping(table = "ab_comm_log_his", columns = {"money"})
	private Double money;
	@Mapping(table = "ab_comm_log_his", columns = {"currType"})
	private String currType;
	@Mapping(table = "ab_comm_log_his", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_comm_log_his", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_comm_log_his", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	
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
	public Integer getTranTime() {
		return tranTime;
	}
	public void setTranTime(Integer tranTime) {
		this.tranTime = tranTime;
	}
	public Integer getCommTime() {
		return commTime;
	}
	public void setCommTime(Integer commTime) {
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
	public Integer getAvailable() {
		return available;
	}
	public void setAvailable(Integer available) {
		this.available = available;
	}
	public String getLastModifyUser() {
		return lastModifyUser;
	}
	public void setLastModifyUser(String lastModifyUser) {
		this.lastModifyUser = lastModifyUser;
	}
	public Date getLastModifyDate() {
		return lastModifyDate;
	}
	public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

}
