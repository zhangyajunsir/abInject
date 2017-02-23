package jsrccb.common.dm.dev;

import cn.com.agree.ab.lib.dm.BasicDM;

public class ICCardDM extends BasicDM {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 2177585555739064752L;
	
/*	0- 账号|
	1- IC卡序列号|
	2- 姓名|
	3- 证件类型|
	4- 证件号码|
	5- 二磁道信息|
	6- 余额 |
	7- 余额上限|
	8- 失效日期
	9- 55域数据
	10- 55域长度（3位）+55域数据+IC卡buffer变量
	11- IC卡buffer数据
	*/
	String account;
	String icCardseq;
	String name;
	String idType;
	String idNo;
	String info;
	String balance;
	String balceiling;
	String ineffectiveDate;
	String ps55data;
	String psAll55date;
	String icCardBuffer;
	public String getIcCardBuffer() {
		return icCardBuffer;
	}
	public void setIcCardBuffer(String icCardBuffer) {
		this.icCardBuffer = icCardBuffer;
	}
	public String getPs55data() {
		return ps55data;
	}
	public String getPsAll55date() {
		return psAll55date;
	}
	public void setPsAll55date(String psAll55date) {
		this.psAll55date = psAll55date;
	}
	public void setPs55data(String pa55data) {
		this.ps55data = pa55data;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getIcCardseq() {
		return icCardseq;
	}
	public void setIcCardseq(String icCardseq) {
		this.icCardseq = icCardseq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getBalceiling() {
		return balceiling;
	}
	public void setBalceiling(String balceiling) {
		this.balceiling = balceiling;
	}
	public String getIneffectiveDate() {
		return ineffectiveDate;
	}
	public void setIneffectiveDate(String ineffectiveDate) {
		this.ineffectiveDate = ineffectiveDate;
	}

}
