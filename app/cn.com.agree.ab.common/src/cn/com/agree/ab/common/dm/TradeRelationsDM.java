package cn.com.agree.ab.common.dm;

import cn.com.agree.ab.lib.dm.BasicDM;

public class TradeRelationsDM extends BasicDM {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String aTradeId;
	private String aTradeComps;
	private String bTradeId;
	private String bTradeComps;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getATradeId() {
		return aTradeId;
	}
	public void setATradeId(String aTradeId) {
		this.aTradeId = aTradeId;
	}
	public String getATradeComps() {
		return aTradeComps;
	}
	public void setATradeComps(String aTradeComps) {
		this.aTradeComps = aTradeComps;
	}
	public String getBTradeId() {
		return bTradeId;
	}
	public void setBTradeId(String bTradeId) {
		this.bTradeId = bTradeId;
	}
	public String getBTradeComps() {
		return bTradeComps;
	}
	public void setBTradeComps(String bTradeComps) {
		this.bTradeComps = bTradeComps;
	}
	

}
