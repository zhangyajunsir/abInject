package cn.com.agree.ab.lib.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class AliasMappingDM extends BasicDM {

	private static final long serialVersionUID = 715297784232389880L;
	
	private String alias;
	
	private String real;
	
	private String domain;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getReal() {
		return real;
	}

	public void setReal(String real) {
		this.real = real;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
