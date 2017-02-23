package jsrccb.common.dao.entity;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.trade.ext.persistence.Mapping;
import cn.com.agree.ab.trade.ext.persistence.PojoMapping;

@PojoMapping(table = "data_signature", primaryKey = {"macaddress"})
public class DataSignatureEntity extends EntityDM {
	private static final long serialVersionUID = -1492290279210412749L;

	@Mapping(table = "data_signature", columns = {"orgcode"})
	private Integer orgcode;
	@Mapping(table = "data_signature", columns = {"macaddress"}, primaryKey = true)
	private String macaddress;
	@Mapping(table = "data_signature", columns = {"secretseed"})
	private String secretseed;
	@Mapping(table = "data_signature", columns = {"secretkey"})
	private String secretkey;
	@Mapping(table = "data_signature", columns = {"signtype"})
	private String signtype;
	@Mapping(table = "data_signature", columns = {"numsignname"})
	private String numsignname;
	@Mapping(table = "data_signature", columns = {"vpnnumb"})
	private Integer vpnnumb;
	@Mapping(table = "data_signature", columns = {"legalperson"})
	private Integer legalperson;
	@Mapping(table = "data_signature", columns = {"remark1"})
	private String remark1;
	@Mapping(table = "data_signature", columns = {"remark2"})
	private Integer remark2;
	@Mapping(table = "data_signature", columns = {"remark3"})
	private String remark3;
	
	public Integer getOrgcode() {
		return orgcode;
	}
	public void setOrgcode(Integer orgcode) {
		this.orgcode = orgcode;
	}
	public String getMacaddress() {
		return macaddress;
	}
	public void setMacaddress(String macaddress) {
		this.macaddress = macaddress;
	}
	public String getSecretseed() {
		return secretseed;
	}
	public void setSecretseed(String secretseed) {
		this.secretseed = secretseed;
	}
	public String getSecretkey() {
		return secretkey;
	}
	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}
	public String getSigntype() {
		return signtype;
	}
	public void setSigntype(String signtype) {
		this.signtype = signtype;
	}
	public String getNumsignname() {
		return numsignname;
	}
	public void setNumsignname(String numsignname) {
		this.numsignname = numsignname;
	}
	public Integer getVpnnumb() {
		return vpnnumb;
	}
	public void setVpnnumb(Integer vpnnumb) {
		this.vpnnumb = vpnnumb;
	}
	public Integer getLegalperson() {
		return legalperson;
	}
	public void setLegalperson(Integer legalperson) {
		this.legalperson = legalperson;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public Integer getRemark2() {
		return remark2;
	}
	public void setRemark2(Integer remark2) {
		this.remark2 = remark2;
	}
	public String getRemark3() {
		return remark3;
	}
	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}
}
