package jsrccb.common.dm;

import java.math.BigDecimal;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TradeAuthQuotaDM extends BasicDM {
	private static final long serialVersionUID = -2639525158010460395L;
	
	private Integer id;				//主键ID
	private String  quotaType;		//额度类别;100 对公转账付,101 对公转账收,110 对公现金付,111 对公现金收,200 对私转账付,201 对私转账收,210 对私现金付,211 对私现金收,
	private String  quotaCnyType;	//额度币种
	private BigDecimal  quotaMin;		//最低额度(包含)
	private BigDecimal  quotaMax;		//最高额度(不包含)
	private Integer quotaLevel;		//额度等级
	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getQuotaType() {
		return quotaType;
	}
	public void setQuotaType(String quotaType) {
		this.quotaType = quotaType;
	}
	public Integer getQuotaLevel() {
		return quotaLevel;
	}
	public void setQuotaLevel(Integer quotaLevel) {
		this.quotaLevel = quotaLevel;
	}
	public BigDecimal getQuotaMin() {
		return quotaMin;
	}
	public void setQuotaMin(BigDecimal quotaMin) {
		this.quotaMin = quotaMin;
	}
	public BigDecimal getQuotaMax() {
		return quotaMax;
	}
	public void setQuotaMax(BigDecimal quotaMax) {
		this.quotaMax = quotaMax;
	}
	public String getQuotaCnyType() {
		return quotaCnyType;
	}
	public void setQuotaCnyType(String quotaCnyType) {
		this.quotaCnyType = quotaCnyType;
	}
		
}
