package jsrccb.common.dao.entity;

import java.math.BigDecimal;
import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_trade_auth_quota", primaryKey = {"id"})
public class TradeAuthQuotaEntity extends EntityDM{
	
	private static final long serialVersionUID = 5079094066634190627L;
	
	@Mapping(table = "ab_trade_auth_quota", columns = {"id"}, primaryKey = true)
	private Integer id;	//主键ID
	@Mapping(table = "ab_trade_auth_quota", columns = {"quota_type"}, requisite = true)
	private String quotaType;//额度类别;100 对公转账付,101 对公转账收,110 对公现金付,111 对公现金收,200 对私转账付,201 对私转账收,210 对私现金付,211 对私现金收,
	@Mapping(table = "ab_trade_auth_quota", columns = {"quota_cnytype"})
	private String quotaCnyType;//额度币种
	@Mapping(table = "ab_trade_auth_quota", columns = {"quota_min"})
	private BigDecimal quotaMin;//最低额度(包含)
	@Mapping(table = "ab_trade_auth_quota", columns = {"quota_max"})
	private BigDecimal quotaMax;//最高额度(不包含)
	@Mapping(table = "ab_trade_auth_quota", columns = {"quota_level"})
	private Integer quotaLevel;//额度等级
	@Mapping(table = "ab_trade_auth_quota", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_trade_auth_quota", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_trade_auth_quota", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
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
