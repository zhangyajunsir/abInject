package cn.com.agree.ab.common.dao.entity;

import java.util.Date;
import java.util.List;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;
import cn.com.agree.ab.trade.ext.persistence.Reference;

@PojoMapping(table = "ab_teller", primaryKey = {"code"})
public class TellerEntity extends EntityDM {
	private static final long serialVersionUID = 350723327471635787L;
	
	@Mapping(table = "ab_teller", columns = {"code"}, primaryKey = true, requisite = true)
	private String code;
	@Mapping(table = "ab_teller", columns = {"password"})
	private String password; 
	@Mapping(table = "ab_teller", columns = {"employee_code"})
	private String employeeCode;
	@Mapping(table = "ab_teller", columns = {"employee_password"})
	private String employeePassword;
	@Mapping(table = "ab_teller", columns = {"org_code"})
	private String orgCode;
	@Mapping(table = "ab_teller", columns = {"department_no"})
	private String departmentNo;
	@Mapping(table = "ab_teller", columns = {"department"})
	private String department;
	@Mapping(table = "ab_teller", columns = {"name"})
	private String name;
	@Mapping(table = "ab_teller", columns = {"english_name"})
	private String englishName;
	@Mapping(table = "ab_teller", columns = {"sex"})
	private Integer sex;
	@Mapping(table = "ab_teller", columns = {"telphone"})
	private String telphone;
	@Mapping(table = "ab_teller", columns = {"mobile"})
	private String mobile;
	@Mapping(table = "ab_teller", columns = {"Id_card"})
	private String IdCard;
	@Mapping(table = "ab_teller", columns = {"password_change_date"})
	private Date passwordChangeDate;
	@Mapping(table = "ab_teller", columns = {"password_failure_date"})
	private Date passwordFailureDate;
	@Mapping(table = "ab_teller", columns = {"password_lock_num"})
	private Integer passwordLockNum;
	@Mapping(table = "ab_teller", columns = {"post"})
	private String post;
	@Mapping(table = "ab_teller", columns = {"type"})
	private Integer type;
	@Mapping(table = "ab_teller", columns = {"level"})
	private Integer level;
	@Mapping(table = "ab_teller", columns = {"role_id"})
	private String roleId;
	@Mapping(table = "ab_teller", columns = {"box_no"})
	private String boxNo;
	@Mapping(table = "ab_teller", columns = {"temp_type"})
	private String tempType;
	@Mapping(table = "ab_teller", columns = {"temp_level"})
	private String tempLevel;
	@Mapping(table = "ab_teller", columns = {"temp_failure_date"})
	private Date tempFailureDate;
	@Mapping(table = "ab_teller", columns = {"teller_attribute"})
	private String tellerAttribute;
	@Mapping(table = "ab_teller", columns = {"status"})
	private String status;
	@Mapping(table = "ab_teller", columns = {"teller_card_flag"})
	private Integer tellerCardFlag;
	@Mapping(table = "ab_teller", columns = {"teller_card_no"})
	private String tellerCardNo;
	@Mapping(table = "ab_teller", columns = {"teller_card_status"})
	private String tellerCardStatus;
	@Mapping(table = "ab_teller", columns = {"authen_code"})
	private String authenCode;
	@Mapping(table = "ab_teller", columns = {"customer_manager_no"})
	private String customerManagerNo;
	@Mapping(table = "ab_teller", columns = {"login_oid"})
	private String loginOid;
	@Mapping(table = "ab_teller", columns = {"login_ip"})
	private String loginIp;
	@Mapping(table = "ab_teller", columns = {"start_date"})
	private Date startDate;
	@Mapping(table = "ab_teller", columns = {"end_date"})
	private Date endDate;
	@Mapping(table = "ab_teller", columns = {"salary_acc_no"})
	private String salaryAccNo;
	@Mapping(table = "ab_teller", columns = {"core_synch_flag"})
	private Integer coreSynchFlag;
	@Mapping(table = "ab_teller", columns = {"core_synch_status"})
	private Integer coreSynchStatus;
	@Mapping(table = "ab_teller", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_teller", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_teller", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	@Reference(currentColumn = {"code"}, targetClass = TellerPostEntity.class, targetColumn = {"teller_code"},targetCondition={"available","1"})
	private List<TellerPostEntity> tellerPostList;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getEmployeePassword() {
		return employeePassword;
	}
	public void setEmployeePassword(String employeePassword) {
		this.employeePassword = employeePassword;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getDepartmentNo() {
		return departmentNo;
	}
	public void setDepartmentNo(String departmentNo) {
		this.departmentNo = departmentNo;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIdCard() {
		return IdCard;
	}
	public void setIdCard(String idCard) {
		IdCard = idCard;
	}
	public Date getPasswordChangeDate() {
		return passwordChangeDate;
	}
	public void setPasswordChangeDate(Date passwordChangeDate) {
		this.passwordChangeDate = passwordChangeDate;
	}
	public Date getPasswordFailureDate() {
		return passwordFailureDate;
	}
	public void setPasswordFailureDate(Date passwordFailureDate) {
		this.passwordFailureDate = passwordFailureDate;
	}
	public Integer getPasswordLockNum() {
		return passwordLockNum;
	}
	public void setPasswordLockNum(Integer passwordLockNum) {
		this.passwordLockNum = passwordLockNum;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
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
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getTempType() {
		return tempType;
	}
	public void setTempType(String tempType) {
		this.tempType = tempType;
	}
	public String getTempLevel() {
		return tempLevel;
	}
	public void setTempLevel(String tempLevel) {
		this.tempLevel = tempLevel;
	}
	public Date getTempFailureDate() {
		return tempFailureDate;
	}
	public void setTempFailureDate(Date tempFailureDate) {
		this.tempFailureDate = tempFailureDate;
	}
	public String getTellerAttribute() {
		return tellerAttribute;
	}
	public void setTellerAttribute(String tellerAttribute) {
		this.tellerAttribute = tellerAttribute;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getTellerCardFlag() {
		return tellerCardFlag;
	}
	public void setTellerCardFlag(Integer tellerCardFlag) {
		this.tellerCardFlag = tellerCardFlag;
	}
	public String getTellerCardNo() {
		return tellerCardNo;
	}
	public void setTellerCardNo(String tellerCardNo) {
		this.tellerCardNo = tellerCardNo;
	}
	public String getTellerCardStatus() {
		return tellerCardStatus;
	}
	public void setTellerCardStatus(String tellerCardStatus) {
		this.tellerCardStatus = tellerCardStatus;
	}
	public String getAuthenCode() {
		return authenCode;
	}
	public void setAuthenCode(String authenCode) {
		this.authenCode = authenCode;
	}
	public String getCustomerManagerNo() {
		return customerManagerNo;
	}
	public void setCustomerManagerNo(String customerManagerNo) {
		this.customerManagerNo = customerManagerNo;
	}
	public String getLoginOid() {
		return loginOid;
	}
	public void setLoginOid(String loginOid) {
		this.loginOid = loginOid;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
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
	public String getSalaryAccNo() {
		return salaryAccNo;
	}
	public void setSalaryAccNo(String salaryAccNo) {
		this.salaryAccNo = salaryAccNo;
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
	public List<TellerPostEntity> getTellerPostList() {
		return tellerPostList;
	}
	public void setTellerPostList(List<TellerPostEntity> tellerPostList) {
		this.tellerPostList = tellerPostList;
	}
	
}
