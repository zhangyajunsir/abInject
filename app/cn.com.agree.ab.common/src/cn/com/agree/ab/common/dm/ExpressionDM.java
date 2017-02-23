package cn.com.agree.ab.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class ExpressionDM extends BasicDM {

	private static final long serialVersionUID = 963820498989796558L;
	
	private Integer id;
	private String 	expression;
	private String 	expressionInfo;
	private String expressionType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getExpressionInfo() {
		return expressionInfo;
	}
	public void setExpressionInfo(String expressionInfo) {
		this.expressionInfo = expressionInfo;
	}
	public String getExpressionType() {
		return expressionType;
	}
	public void setExpressionType(String expressionType) {
		this.expressionType = expressionType;
	}
	
	

}
