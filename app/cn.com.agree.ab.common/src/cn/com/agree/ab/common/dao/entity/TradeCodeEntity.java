package cn.com.agree.ab.common.dao.entity;

import java.util.Date;
import java.util.List;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;
import cn.com.agree.ab.trade.ext.persistence.Reference;

@PojoMapping(table = "ab_trade_code", primaryKey = {"id"})
public class TradeCodeEntity extends EntityDM {
	private static final long serialVersionUID = -2591881808093132274L;
	
	@Mapping(table = "ab_trade_code", columns = {"id"}, primaryKey = true)
	private Integer id;
	@Mapping(table = "ab_trade_code", columns = {"expression_id"})
	private Integer expressionid;
	@Mapping(table = "ab_trade_code", columns = {"code"}, requisite = true)
	private String code;
	@Mapping(table = "ab_trade_code", columns = {"name"})
	private String name;
	@Mapping(table = "ab_trade_code", columns = {"name_full_spell"})
	private String nameFullSpell;
	@Mapping(table = "ab_trade_code", columns = {"name_short_spell"})
	private String nameShortSpell;
	@Mapping(table = "ab_trade_code", columns = {"type"})
	private String type;
	@Mapping(table = "ab_trade_code", columns = {"system"})
	private String system;
	@Mapping(table = "ab_trade_code", columns = {"path"})
	private String path;
	@Mapping(table = "ab_trade_code", columns = {"visiable"})
	private Integer visiable;
	@Mapping(table = "ab_trade_code", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_trade_code", columns = {"last_modify_date"})
	private Date lastModifyDate;
	@Mapping(table = "ab_trade_code", columns = {"window_xsize"})
	private Integer windowXsize;
	@Mapping(table = "ab_trade_code", columns = {"window_ysize"})
	private Integer windowYsize;
	
	@Reference(currentColumn = "id", targetClass = TradePropEntity.class, targetColumn = "trade_id" ,targetCondition={"available","1"})
	private List<TradePropEntity> tradePropList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getExpressionid() {
		return expressionid;
	}
	public void setExpressionid(Integer expression_id) {
		this.expressionid = expression_id;
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
	public Integer getVisiable() {
		return visiable;
	}
	public void setVisiable(Integer visiable) {
		this.visiable = visiable;
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
	public List<TradePropEntity> getTradePropList() {
		return tradePropList;
	}
	public void setTradePropList(List<TradePropEntity> tradePropList) {
		this.tradePropList = tradePropList;
	}
	
	
	
}
