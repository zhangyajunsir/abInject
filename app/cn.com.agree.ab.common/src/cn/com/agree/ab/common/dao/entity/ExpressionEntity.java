package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_expression", primaryKey = {"id"})
public class ExpressionEntity extends EntityDM {

	private static final long serialVersionUID = -2926458244202069106L;
	
	@Mapping(table = "ab_expression", columns = {"id"}, primaryKey = true)
	private Integer id  ;  
	@Mapping(table = "ab_expression", columns = {"expression"})
	private String expression; 
	@Mapping(table = "ab_expression", columns = {"expression_info"})
	private String expressionInfo;
	@Mapping(table = "ab_expression", columns = {"expression_type"})
	private String expressionType;
	@Mapping(table = "ab_expression", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_expression", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_expression", columns = {"last_modify_date"})
	private Date lastModifyDate;
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
