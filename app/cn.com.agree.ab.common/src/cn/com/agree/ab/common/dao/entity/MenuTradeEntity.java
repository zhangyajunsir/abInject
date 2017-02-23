package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_menu_trade", primaryKey = {"id"})
public class MenuTradeEntity extends EntityDM {

	private static final long serialVersionUID = -2300259410634568261L;
	
	@Mapping(table = "ab_menu_trade", columns = {"id"}, primaryKey = true)
	private Integer id  ;  
	@Mapping(table = "ab_menu_trade", columns = {"menu_id"})
	private Integer menuId;
	@Mapping(table = "ab_menu_trade", columns = {"trade_id"})
	private Integer tradeId;
	@Mapping(table = "ab_menu_trade", columns = {"expression_id"})
	private Integer expressionId;
	@Mapping(table = "ab_menu_trade", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_menu_trade", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_menu_trade", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMenuId() {
		return menuId;
	}
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	public Integer getTradeId() {
		return tradeId;
	}
	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}
	public Integer getExpressionId() {
		return expressionId;
	}
	public void setExpressionId(Integer expressionId) {
		this.expressionId = expressionId;
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
