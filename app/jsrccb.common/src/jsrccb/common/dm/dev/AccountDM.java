package jsrccb.common.dm.dev;

import cn.com.agree.ab.lib.dm.BasicDM;

public class AccountDM  extends BasicDM {

	private static final long serialVersionUID = 876857586671054439L;

	// 账号/卡号/柜员号
	private String account;
	// 印刷号/员工号
	private String bvCode;
	// DVV
	private String dvv;
	// 册号/序号
	private String bookNo;
	// 支取方式
	private String payType;
	// 失效日期
	private String ineffectiveDate;
	// CVV
	private String cvv;
	// CVV2
	private String cvv2;
	// CVV3
	private String cvv3;
	// 55区
	private String area55;
	// 所有数据
	private String allData;
	
	private String remark1;
	private String remark2;
	private String remark3;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getBvCode() {
		return bvCode;
	}
	public void setBvCode(String bvCode) {
		this.bvCode = bvCode;
	}
	public String getDvv() {
		return dvv;
	}
	public void setDvv(String dvv) {
		this.dvv = dvv;
	}
	public String getBookNo() {
		return bookNo;
	}
	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getIneffectiveDate() {
		return ineffectiveDate;
	}
	public void setIneffectiveDate(String ineffectiveDate) {
		this.ineffectiveDate = ineffectiveDate;
	}
	public String getCvv() {
		return cvv;
	}
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getCvv3() {
		return cvv3;
	}
	public void setCvv3(String cvv3) {
		this.cvv3 = cvv3;
	}
	public String getArea55() {
		return area55;
	}
	public void setArea55(String area55) {
		this.area55 = area55;
	}
	public String getAllData() {
		return allData;
	}
	public void setAllData(String allData) {
		this.allData = allData;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public String getRemark2() {
		return remark2;
	}
	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	public String getRemark3() {
		return remark3;
	}
	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}
	
}
