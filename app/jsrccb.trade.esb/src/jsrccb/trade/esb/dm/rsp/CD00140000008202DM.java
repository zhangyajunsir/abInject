package jsrccb.trade.esb.dm.rsp;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import cn.com.agree.ab.lib.dm.BasicDM;

public class CD00140000008202DM extends BasicDM {
	private static final long serialVersionUID = 8958449832807234798L;
	@XStreamAlias("icoll")
	private Map<String, String> icoll;

	public Map<String, String> getIcoll() {
		return icoll;
	}

	public void setIcoll(Map<String, String> icoll) {
		this.icoll = icoll;
	}
	

}
