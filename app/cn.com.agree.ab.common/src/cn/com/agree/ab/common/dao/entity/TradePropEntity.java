package cn.com.agree.ab.common.dao.entity;

import java.util.Date;
import java.util.List;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;
import cn.com.agree.ab.trade.ext.persistence.Reference;

@PojoMapping(table = "ab_trade_prop", primaryKey = {"id"})
public class TradePropEntity extends EntityDM {
	private static final long serialVersionUID = -1726102948856963724L;
	
	@Mapping(table = "ab_trade_prop", columns = {"id"}, primaryKey = true)
	private Integer id;
	@Mapping(table = "ab_trade_prop", columns = {"trade_id"}, requisite = true)
	private Integer tradeId;
	@Mapping(table = "ab_trade_prop", columns = {"comm_id"}, requisite = true)
	private Integer commId;
	@Mapping(table = "ab_trade_prop", columns = {"business_type"})
	private String businessType;
	@Mapping(table = "ab_trade_prop", columns = {"check_flag"})
	private Integer checkFlag;
	@Mapping(table = "ab_trade_prop", columns = {"multiple_query_flag"})
	private Integer multipleQueryFlag;
	@Mapping(table = "ab_trade_prop", columns = {"reverse_flag"})
	private Integer reverseFlag;
	@Mapping(table = "ab_trade_prop", columns = {"elog_flag"})
	private Integer elogFlag;
	@Mapping(table = "ab_trade_prop", columns = {"auth_type"})
	private Integer authType;
	@Mapping(table = "ab_trade_prop", columns = {"auth_level"})
	private Integer authLevel;
	@Mapping(table = "ab_trade_prop", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_trade_prop", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_trade_prop", columns = {"last_modify_date"})
	private Date lastModifyDate;
	@Mapping(table = "ab_trade_prop", columns = {"func_code_name"})
	private String funcCodeName;
	@Mapping(table = "ab_trade_prop", columns = {"func_code_value"})
	private String funcCodeValue;
	@Mapping(table = "ab_trade_prop", columns = {"auto_print_flag"})
	private Integer autoPrintFlag;

	@Reference(currentColumn = "comm_id", targetClass = CommCodeEntity.class, targetColumn = "id",targetCondition={"available","1"})
	private CommCodeEntity commCode;
	@Reference(currentColumn = {"trade_id","comm_id"}, targetClass = TradePostEntity.class, targetColumn = {"trade_id","comm_id"},targetCondition={"available","1"})
	private List<TradePostEntity> tradePostList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTradeId() {
		return tradeId;
	}
	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}
	public Integer getCommId() {
		return commId;
	}
	public void setCommId(Integer commId) {
		this.commId = commId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public Integer getCheckFlag() {
		return checkFlag;
	}
	public void setCheckFlag(Integer checkFlag) {
		this.checkFlag = checkFlag;
	}
	public Integer getMultipleQueryFlag() {
		return multipleQueryFlag;
	}
	public void setMultipleQueryFlag(Integer multipleQueryFlag) {
		this.multipleQueryFlag = multipleQueryFlag;
	}
	public Integer getReverseFlag() {
		return reverseFlag;
	}
	public void setReverseFlag(Integer reverseFlag) {
		this.reverseFlag = reverseFlag;
	}
	public Integer getElogFlag() {
		return elogFlag;
	}
	public void setElogFlag(Integer elogFlag) {
		this.elogFlag = elogFlag;
	}
	public Integer getAuthType() {
		return authType;
	}
	public void setAuthType(Integer authType) {
		this.authType = authType;
	}
	public Integer getAuthLevel() {
		return authLevel;
	}
	public void setAuthLevel(Integer authLevel) {
		this.authLevel = authLevel;
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
	public CommCodeEntity getCommCode() {
		return commCode;
	}
	public void setCommCode(CommCodeEntity commCode) {
		this.commCode = commCode;
	}
	public List<TradePostEntity> getTradePostList() {
		return tradePostList;
	}
	public void setTradePostList(List<TradePostEntity> tradePostList) {
		this.tradePostList = tradePostList;
	}
	public String getFuncCodeName() {
		return funcCodeName;
	}
	public void setFuncCodeName(String funcCodeName) {
		this.funcCodeName = funcCodeName;
	}
	public String getFuncCodeValue() {
		return funcCodeValue;
	}
	public void setFuncCodeValue(String funcCodeValue) {
		this.funcCodeValue = funcCodeValue;
	}
	public Integer getAutoPrintFlag() {
		return autoPrintFlag;
	}
	public void setAutoPrintFlag(Integer autoPrintFlag) {
		this.autoPrintFlag = autoPrintFlag;
	}
	
}
