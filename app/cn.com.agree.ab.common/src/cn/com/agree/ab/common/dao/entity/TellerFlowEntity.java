package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_teller_flow", primaryKey = {"id"})
public class TellerFlowEntity extends EntityDM {
	private static final long serialVersionUID = -6686487080756300860L;

	@Mapping(table = "ab_teller_flow", columns = {"id"}, primaryKey = true)
	private Integer id;
	@Mapping(table = "ab_teller_flow", columns = {"teller_code"}, requisite = true)
	private String tellerCode;
	@Mapping(table = "ab_teller_flow", columns = {"trade_date"}, requisite = true)
	private String txData;
	@Mapping(table = "ab_teller_flow", columns = {"flow_type"}, requisite = true)
	private Integer flowType;
	@Mapping(table = "ab_teller_flow", columns = {"serial_num"})
	private Integer serialNum;
	@Mapping(table = "ab_teller_flow", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_teller_flow", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_teller_flow", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTellerCode() {
		return tellerCode;
	}
	public void setTellerCode(String tellerCode) {
		this.tellerCode = tellerCode;
	}
	public String getTxData() {
		return txData;
	}
	public void setTxData(String txData) {
		this.txData = txData;
	}
	public Integer getFlowType() {
		return flowType;
	}
	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}
	public Integer getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(Integer serialNum) {
		this.serialNum = serialNum;
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
