package jsrccb.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_trade_auth", primaryKey = {"id"})
public class TradeAuthEntity extends EntityDM{
	
	private static final long serialVersionUID = 9020918565602020291L;
	
	@Mapping(table = "ab_trade_auth", columns = {"id"}, primaryKey = true)
	private Integer id;				//主键ID
	@Mapping(table = "ab_trade_auth", columns = {"trade_id"}, requisite = true)
	private Integer tradeId;		//交易码表ID
	@Mapping(table = "ab_trade_auth", columns = {"comm_id"})
	private Integer commId;			//通讯码ID
	@Mapping(table = "ab_trade_auth", columns = {"expression_id"})
	private Integer expressionId;	//表达式ID
	@Mapping(table = "ab_trade_auth", columns = {"describe_id"})
	private Integer describeId;		//描述ID
	@Mapping(table = "ab_trade_auth", columns = {"type"})
	private Integer type;			//授权类别((参照AuthType.getType))
	@Mapping(table = "ab_trade_auth", columns = {"level"})
	private Integer level;
	@Mapping(table = "ab_trade_auth", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_trade_auth", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_trade_auth", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
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
	public Integer getExpressionId() {
		return expressionId;
	}
	public void setExpressionId(Integer expressionId) {
		this.expressionId = expressionId;
	}
	public Integer getDescribeId() {
		return describeId;
	}
	public void setDescribeId(Integer describeId) {
		this.describeId = describeId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
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
