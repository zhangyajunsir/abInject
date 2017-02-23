package cn.com.agree.ab.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class MenuDM extends BasicDM {
	private static final long serialVersionUID = -8533651729945071630L;

	private Integer id;
	private Integer expressionId;
	private Integer parentId;
	private String  name;
	private String  nameFullSpell;
	private String  nameShortSpell;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getExpressionId() {
		return expressionId;
	}
	public void setExpressionId(Integer expressionId) {
		this.expressionId = expressionId;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
	
}
