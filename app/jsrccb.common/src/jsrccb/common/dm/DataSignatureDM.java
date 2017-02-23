package jsrccb.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class DataSignatureDM extends BasicDM {
	private static final long serialVersionUID = -5590830468658035636L;
	
	private String orgCode;
	private String macAddress;
	private String secretSeed;
	private String secretKey;
	private String signType;
	private String numSignname;
	private String vpnNumb;
	private String legalPerson;
	private String reMark1;
	private String reMark2;
	private String reMark3;
	
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getSecretSeed() {
		return secretSeed;
	}
	public void setSecretSeed(String secretSeed) {
		this.secretSeed = secretSeed;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getNumSignname() {
		return numSignname;
	}
	public void setNumSignname(String numSignname) {
		this.numSignname = numSignname;
	}
	public String getVpnNumb() {
		return vpnNumb;
	}
	public void setVpnNumb(String vpnNumb) {
		this.vpnNumb = vpnNumb;
	}
	public String getLegalPerson() {
		return legalPerson;
	}
	public void setLegalPerson(String legalPerson) {
		this.legalPerson = legalPerson;
	}
	public String getReMark1() {
		return reMark1;
	}
	public void setReMark1(String reMark1) {
		this.reMark1 = reMark1;
	}
	public String getReMark2() {
		return reMark2;
	}
	public void setReMark2(String reMark2) {
		this.reMark2 = reMark2;
	}
	public String getReMark3() {
		return reMark3;
	}
	public void setReMark3(String reMark3) {
		this.reMark3 = reMark3;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
