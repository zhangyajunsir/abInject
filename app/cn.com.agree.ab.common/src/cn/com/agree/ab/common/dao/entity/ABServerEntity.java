package cn.com.agree.ab.common.dao.entity;

import java.util.Date;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "ab_server", primaryKey = {"host_name"})
public class ABServerEntity extends EntityDM {
	private static final long serialVersionUID = -5739413514882485546L;
	@Mapping(table = "ab_server", columns = {"host_name"}, primaryKey = true)
	private String hostName;
	@Mapping(table = "ab_server", columns = {"abs_oid"}, requisite = true)
	private String absOid;
	@Mapping(table = "ab_server", columns = {"ipv4"}, requisite = true)
	private String ipv4;
	@Mapping(table = "ab_server", columns = {"ipv6"})
	private String ipv6;
	@Mapping(table = "ab_server", columns = {"abs_port"})
	private Integer absPort;
	@Mapping(table = "ab_server", columns = {"available"})
	private Integer available;
	@Mapping(table = "ab_server", columns = {"last_modify_user"})
	private String lastModifyUser;
	@Mapping(table = "ab_server", columns = {"last_modify_date"})
	private Date   lastModifyDate;
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getAbsOid() {
		return absOid;
	}
	public void setAbsOid(String absOid) {
		this.absOid = absOid;
	}
	public String getIpv4() {
		return ipv4;
	}
	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}
	public String getIpv6() {
		return ipv6;
	}
	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}
	public Integer getAbsPort() {
		return absPort;
	}
	public void setAbsPort(Integer absPort) {
		this.absPort = absPort;
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
