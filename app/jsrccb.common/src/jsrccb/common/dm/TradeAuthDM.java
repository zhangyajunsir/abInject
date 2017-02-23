package jsrccb.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TradeAuthDM extends BasicDM {
	private static final long serialVersionUID = -5288150615193875421L;

	private Integer id;				//主键ID
	private Integer tradeId;		//交易码表ID
	private Integer commId;			//通讯码ID
	private Integer expressionId;	//表达式ID
	private Integer describeId;		//描述ID
	private Integer type;			//授权类别(参照AuthType.getType)
	private Integer level;
	
	
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
	
	
}
