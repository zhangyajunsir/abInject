package jsrccb.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TerminalDeviceDM extends BasicDM {
	private static final long serialVersionUID = 2361058016248129169L;
	
	private String termLtty;
	private String termType;
	private String termValue;
	
	public String getTermLtty() {
		return termLtty;
	}
	public void setTermLtty(String termLtty) {
		this.termLtty = termLtty;
	}
	public String getTermType() {
		return termType;
	}
	public void setTermType(String termType) {
		this.termType = termType;
	}
	public String getTermValue() {
		return termValue;
	}
	public void setTermValue(String termValue) {
		this.termValue = termValue;
	}
	
	

}
