package jsrccb.common.dm.rsp;

import cn.com.agree.ab.lib.dm.BasicDM;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class AfaExtDM extends EsbExtDM {
	private static final long serialVersionUID = 261600718482143144L;
	@XStreamAlias("information")
	private AfaInfo afaInfo; // 可能会没下送

	public AfaInfo getAfaInfo() {
		return afaInfo;
	}
	public void setAfaInfo(AfaInfo afaInfo) {
		this.afaInfo = afaInfo;
	}

	public static class AfaInfo extends BasicDM {
		private static final long serialVersionUID = 440085175747380374L;
		@XStreamAlias("front-info")
		private String info;

		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
	}
}
