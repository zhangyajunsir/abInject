package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_menu", primaryKey = {"id"})
public class MenuEntity extends EntityDM {

	private static final long serialVersionUID = -2300259410634568261L;
	
	@Mapping(table = "ab_menu", columns = {"id"}, primaryKey = true)
	private Integer id  ;  
	@Mapping(table = "ab_menu", columns = {"name"})
	private String name;
	@Mapping(table = "ab_menu", columns = {"name_full_spell"})
	private String nameFullSpell;
	@Mapping(table = "ab_menu", columns = {"name_short_spell"})
	private String nameShortSpell;
	@Mapping(table = "ab_menu", columns = {"parent_id"})
	private Integer parentId;
	@Mapping(table = "ab_menu", columns = {"expression_id"})
	private Integer expressionId;
	@Mapping(table = "ab_menu", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_menu", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_menu", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameFullSpell() {
		return nameFullSpell;
	}
	public void setNameFullSpell(String nameFullSpell) {
		this.nameFullSpell = nameFullSpell;
	}
	public String getNameShortSpell() {
		return nameShortSpell;
	}
	public void setNameShortSpell(String nameShortSpell) {
		this.nameShortSpell = nameShortSpell;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
