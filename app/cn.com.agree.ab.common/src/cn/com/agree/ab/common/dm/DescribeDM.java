package cn.com.agree.ab.common.dm;

import java.util.Date;

import cn.com.agree.ab.lib.dm.BasicDM;

public class DescribeDM extends BasicDM {

	private static final long serialVersionUID = -4199298613722510343L;
	
	private Integer id  ;  
	private String code; 
	private String description;
	private Integer available;
	private String lastModifyUser;
	private Date   lastModifyDate;
	
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
