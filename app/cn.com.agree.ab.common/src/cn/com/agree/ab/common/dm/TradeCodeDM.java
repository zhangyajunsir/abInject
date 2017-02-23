package cn.com.agree.ab.common.dm;

import java.util.List;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TradeCodeDM extends BasicDM {
	private static final long serialVersionUID = 642121057313231319L;
	
	private Integer id;
	private Integer expressionid;
	private String code;
	private String name;
	private String nameFullSpell;
	private String nameShortSpell;
	private String type;
	private String system;
	private String path;
	private Integer windowXsize;
	private Integer windowYsize;
	
	private List<TradePropDM> tradePropList;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getExpressionid() {
		return expressionid;
	}
	public void setExpressionid(Integer expressionid) {
		this.expressionid = expressionid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public Integer getWindowXsize() {
		return windowXsize;
	}
	public void setWindowXsize(Integer windowXsize) {
		this.windowXsize = windowXsize;
	}
	public Integer getWindowYsize() {
		return windowYsize;
	}
	public void setWindowYsize(Integer windowYsize) {
		this.windowYsize = windowYsize;
	}
	public List<TradePropDM> getTradePropList() {
		return tradePropList;
	}
	public void setTradePropList(List<TradePropDM> tradePropList) {
		this.tradePropList = tradePropList;
	}

}
