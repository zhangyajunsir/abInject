package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_org", primaryKey = {"code"})
public class OrgEntity extends EntityDM{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6616573780058073593L;
	
	@Mapping(table = "ab_org", columns = {"code"}, primaryKey = true, requisite = true)
	private String code;
	@Mapping(table = "ab_org", columns = {"name"})
	private String name;
	@Mapping(table = "ab_org", columns = {"short_name_1"})
	private String shortName1; 
	@Mapping(table = "ab_org", columns = {"short_name_2"})
	private String shortName2;
	@Mapping(table = "ab_org", columns = {"address"})
	private String address;
	@Mapping(table = "ab_org", columns = {"phone"})
	private String phone;
	@Mapping(table = "ab_org", columns = {"city_code"})
	private String cityCode;
	@Mapping(table = "ab_org", columns = {"zip_code"})
	private String zipCode;
	@Mapping(table = "ab_org", columns = {"area_code"})
	private String areaCode;
	@Mapping(table = "ab_org", columns = {"type"})
	private Integer type;
	@Mapping(table = "ab_org", columns = {"level"})
	private Integer level;
	@Mapping(table = "ab_org", columns = {"selbal"})
	private Integer selbal;
	@Mapping(table = "ab_org", columns = {"accounting_code"})
	private String accountingCode;
	@Mapping(table = "ab_org", columns = {"business_code"})
	private String businessCode;  
	@Mapping(table = "ab_org", columns = {"branch_code"})
	private String branchCode;
	@Mapping(table = "ab_org", columns = {"accounting_system_code"})
	private String accountingSystemCode;
	@Mapping(table = "ab_org", columns = {"exchange_bills_code"})
	private String exchangeBillsCode;
	@Mapping(table = "ab_org", columns = {"exchange_foreign_code"})
	private String exchangeForeignCode;
	@Mapping(table = "ab_org", columns = {"remittance_unanswered_code"})
	private String remittanceUnansweredCode;
	@Mapping(table = "ab_org", columns = {"start_date"})
	private Date startDate;
	@Mapping(table = "ab_org", columns = {"end_date"})
	private Date endDate;
	@Mapping(table = "ab_org", columns = {"core_synch_flag"})
	private Integer coreSynchFlag;
	@Mapping(table = "ab_org", columns = {"core_synch_status"})
	private Integer coreSynchStatus;
	@Mapping(table = "ab_org", columns = {"manage_level"})
	private Integer ManageLevel;
	@Mapping(table = "ab_org", columns = {"auth_type"})
	private String authType;
	@Mapping(table = "ab_org", columns = {"check_money"})
	private Double checkMoney;
	@Mapping(table = "ab_org", columns = {"status"})
	private String status;
	@Mapping(table = "ab_org", columns = {"accounting_level"})
	private String accountingLevel;
	@Mapping(table = "ab_org", columns = {"parent_manage_org"})
	private String parentManageOrg;
	@Mapping(table = "ab_org", columns = {"parent_clear_org"})
	private String parentClearOrg;
	@Mapping(table = "ab_org", columns = {"parent_total_org"})
	private String parentTotalOrg;
	@Mapping(table = "ab_org", columns = {"parent_recipients_org_1"})
	private String parentRecipientsOrg1;
	@Mapping(table = "ab_org", columns = {"parent_recipients_org_2"})
	private String parentRecipientsOrg2;
	@Mapping(table = "ab_org", columns = {"total_level"})
	private Integer totalLevel;
	@Mapping(table = "ab_org", columns = {"clear_level"})
	private Integer clearLevel;
	@Mapping(table = "ab_org", columns = {"parent_clear_acc"})
	private String parentClearAcc;
	@Mapping(table = "ab_org", columns = {"local_clear_acc"})
	private String localClearAcc;
	@Mapping(table = "ab_org", columns = {"max_clear_level"})
	private Integer maxClearLevel;
	@Mapping(table = "ab_org", columns = {"start_teller"})
	private String startTeller;
	@Mapping(table = "ab_org", columns = {"end_teller"})
	private String endTeller;
	@Mapping(table = "ab_org", columns = {"foreign_date"})
	private Date foreignDate;
	@Mapping(table = "ab_org", columns = {"financial_license"})
	private String financialLicense;
	@Mapping(table = "ab_org", columns = {"mac_key"})
	private String macKey;
	@Mapping(table = "ab_org", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_org", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_org", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName1() {
		return shortName1;
	}
	public void setShortName1(String shortName1) {
		this.shortName1 = shortName1;
	}
	public String getShortName2() {
		return shortName2;
	}
	public void setShortName2(String shortName2) {
		this.shortName2 = shortName2;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getSelbal() {
		return selbal;
	}
	public void setSelbal(Integer selbal) {
		this.selbal = selbal;
	}
	public String getAccountingCode() {
		return accountingCode;
	}
	public void setAccountingCode(String accountingCode) {
		this.accountingCode = accountingCode;
	}
	public String getBusinessCode() {
		return businessCode;
	}
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getAccountingSystemCode() {
		return accountingSystemCode;
	}
	public void setAccountingSystemCode(String accountingSystemCode) {
		this.accountingSystemCode = accountingSystemCode;
	}
	public String getExchangeBillsCode() {
		return exchangeBillsCode;
	}
	public void setExchangeBillsCode(String exchangeBillsCode) {
		this.exchangeBillsCode = exchangeBillsCode;
	}
	public String getExchangeForeignCode() {
		return exchangeForeignCode;
	}
	public void setExchangeForeignCode(String exchangeForeignCode) {
		this.exchangeForeignCode = exchangeForeignCode;
	}
	public String getRemittanceUnansweredCode() {
		return remittanceUnansweredCode;
	}
	public void setRemittanceUnansweredCode(String remittanceUnansweredCode) {
		this.remittanceUnansweredCode = remittanceUnansweredCode;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getCoreSynchFlag() {
		return coreSynchFlag;
	}
	public void setCoreSynchFlag(Integer coreSynchFlag) {
		this.coreSynchFlag = coreSynchFlag;
	}
	public Integer getCoreSynchStatus() {
		return coreSynchStatus;
	}
	public void setCoreSynchStatus(Integer coreSynchStatus) {
		this.coreSynchStatus = coreSynchStatus;
	}
	public Integer getManageLevel() {
		return ManageLevel;
	}
	public void setManageLevel(Integer manageLevel) {
		ManageLevel = manageLevel;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public Double getCheckMoney() {
		return checkMoney;
	}
	public void setCheckMoney(Double checkMoney) {
		this.checkMoney = checkMoney;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAccountingLevel() {
		return accountingLevel;
	}
	public void setAccountingLevel(String accountingLevel) {
		this.accountingLevel = accountingLevel;
	}
	public String getParentManageOrg() {
		return parentManageOrg;
	}
	public void setParentManageOrg(String parentManageOrg) {
		this.parentManageOrg = parentManageOrg;
	}
	public String getParentClearOrg() {
		return parentClearOrg;
	}
	public void setParentClearOrg(String parentClearOrg) {
		this.parentClearOrg = parentClearOrg;
	}
	public String getParentTotalOrg() {
		return parentTotalOrg;
	}
	public void setParentTotalOrg(String parentTotalOrg) {
		this.parentTotalOrg = parentTotalOrg;
	}
	public String getParentRecipientsOrg1() {
		return parentRecipientsOrg1;
	}
	public void setParentRecipientsOrg1(String parentRecipientsOrg1) {
		this.parentRecipientsOrg1 = parentRecipientsOrg1;
	}
	public String getParentRecipientsOrg2() {
		return parentRecipientsOrg2;
	}
	public void setParentRecipientsOrg2(String parentRecipientsOrg2) {
		this.parentRecipientsOrg2 = parentRecipientsOrg2;
	}
	public Integer getTotalLevel() {
		return totalLevel;
	}
	public void setTotalLevel(Integer totalLevel) {
		this.totalLevel = totalLevel;
	}
	public Integer getClearLevel() {
		return clearLevel;
	}
	public void setClearLevel(Integer clearLevel) {
		this.clearLevel = clearLevel;
	}
	public String getParentClearAcc() {
		return parentClearAcc;
	}
	public void setParentClearAcc(String parentClearAcc) {
		this.parentClearAcc = parentClearAcc;
	}
	public String getLocalClearAcc() {
		return localClearAcc;
	}
	public void setLocalClearAcc(String localClearAcc) {
		this.localClearAcc = localClearAcc;
	}
	public Integer getMaxClearLevel() {
		return maxClearLevel;
	}
	public void setMaxClearLevel(Integer maxClearLevel) {
		this.maxClearLevel = maxClearLevel;
	}
	public String getStartTeller() {
		return startTeller;
	}
	public void setStartTeller(String startTeller) {
		this.startTeller = startTeller;
	}
	public String getEndTeller() {
		return endTeller;
	}
	public void setEndTeller(String endTeller) {
		this.endTeller = endTeller;
	}
	public Date getForeignDate() {
		return foreignDate;
	}
	public void setForeignDate(Date foreignDate) {
		this.foreignDate = foreignDate;
	}
	public String getFinancialLicense() {
		return financialLicense;
	}
	public void setFinancialLicense(String financialLicense) {
		this.financialLicense = financialLicense;
	}
	public String getMacKey() {
		return macKey;
	}
	public void setMacKey(String macKey) {
		this.macKey = macKey;
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
