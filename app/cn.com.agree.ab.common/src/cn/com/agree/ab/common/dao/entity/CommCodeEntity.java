package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_comm_code", primaryKey = {"id"})
public class CommCodeEntity extends EntityDM {
	private static final long serialVersionUID = -2726472988805346107L;
	
	@Mapping(table = "ab_comm_code", columns = {"id"}, primaryKey = true)
	private Integer id;
	@Mapping(table = "ab_comm_code", columns = {"comm_code"}, requisite = true)
	private String commCode;
	@Mapping(table = "ab_comm_code", columns = {"system_code"})
	private String systemCode;
	@Mapping(table = "ab_comm_code", columns = {"trans_code"})
	private String transCode;
	@Mapping(table = "ab_comm_code", columns = {"channel_code"})
	private String channelCode;
	@Mapping(table = "ab_comm_code", columns = {"itable"})
	private String itable;
	@Mapping(table = "ab_comm_code", columns = {"otable"})
	private String otable;
	@Mapping(table = "ab_comm_code", columns = {"flag"})
	private Integer flag;
	@Mapping(table = "ab_comm_code", columns = {"time_out"})
	private Integer timeOut;
	@Mapping(table = "ab_comm_code", columns = {"remark"})
	private String remark;
	@Mapping(table = "ab_comm_code", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_comm_code", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_comm_code", columns = {"last_modify_date"})
	private Date lastModifyDate;

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
