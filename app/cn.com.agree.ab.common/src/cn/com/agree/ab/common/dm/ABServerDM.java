package cn.com.agree.ab.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class ABServerDM extends BasicDM {
	private static final long serialVersionUID = 395225038270107446L;
	private String hostName;
	private String absOid;
	private String ipv4;
	private String ipv6;
	private Integer absPort;
	
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
}
