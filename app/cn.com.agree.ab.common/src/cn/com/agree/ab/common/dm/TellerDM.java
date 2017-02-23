package cn.com.agree.ab.common.dm;

import java.util.Date;
import java.util.List;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TellerDM extends BasicDM {
	private static final long serialVersionUID = 657936443878486428L;
	
	private String code;
	private String password; 
	private String employeeCode;
	private String employeePassword;
	private String orgCode;
	private String departmentNo;
	private String department;
	private String name;
	private String englishName;
	private Integer sex;
	private String telphone;
	private String mobile;
	private String IdCard;
	private Date passwordChangeDate;
	private Date passwordFailureDate;
	private Integer passwordLockNum;
	private String post;
	private Integer type;
	private Integer level;
	private String roleId;
	private String boxNo;
	private String tempType;
	private String tempLevel;
	private Date tempFailureDate;
	private String tellerAttribute;
	private String status;
	private Integer tellerCardFlag;
	private String tellerCardNo;
	private String tellerCardStatus;
	private String authenCode;
	private String customerManagerNo;
	private String loginOid;
	private String loginIp;
	private Date startDate;
	private Date endDate;
	private String salaryAccNo;
	private Integer coreSynchFlag;
	private Integer coreSynchStatus;
	
	private List<Integer> tellerPosts;
	
	
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
	
	public List<Integer> getTellerPosts() {
		return tellerPosts;
	}
	public void setTellerPosts(List<Integer> tellerPostList) {
		this.tellerPosts = tellerPostList;
	}

	
}
