package jsrccb.common.dao.entity;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "pinkey", primaryKey = {"devid"})
public class PinKeyEntity extends EntityDM {
	private static final long serialVersionUID = 5537732259781373378L;
	
	@Mapping(table = "pinkey", columns = {"devid"}, primaryKey = true)
	private String devId;
	@Mapping(table = "pinkey", columns = {"mainkey"})
	private String mainKey;
	@Mapping(table = "pinkey", columns = {"workkey"})
	private String workKey;
	
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getMainKey() {
		return mainKey;
	}
	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}
	public String getWorkKey() {
		return workKey;
	}
	public void setWorkKey(String workKey) {
		this.workKey = workKey;
	}
	
	
}
