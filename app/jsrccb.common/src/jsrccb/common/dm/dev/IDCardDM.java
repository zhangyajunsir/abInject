package jsrccb.common.dm.dev;

import cn.com.agree.ab.lib.dm.BasicDM;

public class IDCardDM extends BasicDM {

	private static final long serialVersionUID = 5791401011927288165L;
	
	private String name;
	private String sex;
	private String nation;
	private String birthday;
	private String address;
	private String idNo;
	private String org;
	private String ineffectiveDate;
	private String photo;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getIneffectiveDate() {
		return ineffectiveDate;
	}
	public void setIneffectiveDate(String ineffectiveDate) {
		this.ineffectiveDate = ineffectiveDate;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	
}
