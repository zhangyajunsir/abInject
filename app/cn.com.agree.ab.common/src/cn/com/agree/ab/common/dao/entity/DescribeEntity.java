package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_describe", primaryKey = {"id"})
public class DescribeEntity extends EntityDM {

	private static final long serialVersionUID = -2300259410634568261L;
	
	@Mapping(table = "ab_describe", columns = {"id"}, primaryKey = true)
	private Integer id  ;  
	@Mapping(table = "ab_describe", columns = {"code"})
	private String code; 
	@Mapping(table = "ab_describe", columns = {"description"})
	private String description;
	@Mapping(table = "ab_describe", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_describe", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_describe", columns = {"last_modify_date"})
	private Date lastModifyDate;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
