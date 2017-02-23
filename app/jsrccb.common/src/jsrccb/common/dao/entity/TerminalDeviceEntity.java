package jsrccb.common.dao.entity;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "terminaldevice", primaryKey = {})
public class TerminalDeviceEntity extends EntityDM {
	private static final long serialVersionUID = 5537732259781373378L;
	
	@Mapping(table = "terminaldevice", columns = {"term_ltty"})
	private String termLtty;
	@Mapping(table = "terminaldevice", columns = {"term_type"})
	private String termType;
	@Mapping(table = "terminaldevice", columns = {"term_value"})
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
