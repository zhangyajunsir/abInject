package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_trade_relations", primaryKey = {"id"})
public class TradeRelationsEntity extends EntityDM {
	private static final long serialVersionUID = 1L;
	@Mapping(table = "ab_trade_relations", columns = {"id"}, primaryKey = true)
	private Integer id;
	@Mapping(table = "ab_trade_relations", columns = {"a_trade_id"})
	private String aTradeId;
	@Mapping(table = "ab_trade_relations", columns = {"a_trade_comps"})
	private String aTradeComps;
	@Mapping(table = "ab_trade_relations", columns = {"b_trade_id"})
	private String bTradeId;
	@Mapping(table = "ab_trade_relations", columns = {"b_trade_comps"})
	private String bTradeComps;
	@Mapping(table = "ab_trade_relations", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_trade_relations", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_trade_relations", columns = {"last_modify_date"})
	private Date lastModifyDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getATradeId() {
		return aTradeId;
	}
	public void setATradeId(String aTradeId) {
		this.aTradeId = aTradeId;
	}
	public String getATradeComps() {
		return aTradeComps;
	}
	public void setATradeComps(String aTradeComps) {
		this.aTradeComps = aTradeComps;
	}
	public String getBTradeId() {
		return bTradeId;
	}
	public void setBTradeId(String bTradeId) {
		this.bTradeId = bTradeId;
	}
	public String getBTradeComps() {
		return bTradeComps;
	}
	public void setBTradeComps(String bTradeComps) {
		this.bTradeComps = bTradeComps;
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
