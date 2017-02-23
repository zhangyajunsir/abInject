package cn.com.agree.ab.common.dm;

import java.util.List;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TradePropDM extends BasicDM {
	private static final long serialVersionUID = 3070420884544471074L;
	
	private Integer id;
	private Integer tradeId;				//交易码ID
	private Integer commId;					//通讯码ID
	private String  businessType;			//后台交易类型（01：账务类，02：查询类，03：非账务非查询类,04:本地）
	private Integer checkFlag;				//复核标识（0-不复核，1-复核）
	private Integer multipleQueryFlag;		//多页查询标识（0-否，1-是）
	private Integer reverseFlag;			//能否冲正标识（0-否，1-是）
	private Integer elogFlag;				//电子日志标识（0-否，1-是）
	private Integer authType;				//以字节位由右至左：第1位为1前端强制授权；第2位为1前端条件授权；第3位为1前端金额授权；第4位为1执行等级授权；
	private Integer authLevel;				//授权等级
	private String  funcCodeName;
	private String  funcCodeValue;
	private Integer autoPrintFlag;
	
	private CommCodeDM commCode;
	private List<Integer> tradePosts;

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
	public CommCodeDM getCommCode() {
		return commCode;
	}
	public void setCommCode(CommCodeDM commCode) {
		this.commCode = commCode;
	}
	public List<Integer> getTradePosts() {
		return tradePosts;
	}
	public void setTradePostLists(List<Integer> tradePosts) {
		this.tradePosts = tradePosts;
	}

}
