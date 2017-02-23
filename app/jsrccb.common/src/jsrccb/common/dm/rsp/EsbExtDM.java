package jsrccb.common.dm.rsp;

import java.util.List;

import cn.com.agree.ab.lib.dm.BasicDM;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class EsbExtDM extends BasicDM {
	private static final long serialVersionUID = 2024421697888146601L;
	@XStreamAlias("OPM-RESP-CODE")
	private String respCode;
	@XStreamImplicit(itemFieldName = "OPM-MSG-GRP")
	private List<OPM_MSG_GRP> msgList;
	@XStreamImplicit(itemFieldName = "OPM-PAGE-CTL")	// 翻页，后台可能会返回多个
	private List<OPM_PAGE_CTL> pageList;
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public List<OPM_MSG_GRP> getMsgList() {
		return msgList;
	}
	public void setMsgList(List<OPM_MSG_GRP> msgList) {
		this.msgList = msgList;
	}
	public List<OPM_PAGE_CTL> getPageList() {
		return pageList;
	}
	public void setPageList(List<OPM_PAGE_CTL> pageList) {
		this.pageList = pageList;
	}
	
	public static class  OPM_MSG_GRP extends BasicDM  {
		private static final long serialVersionUID = 8775119612489685495L;
		@XStreamAlias("OPM-MSG-CODE")
		private String OPM_MSG_CODE;
		@XStreamAlias("OPM-MSG-TXT")
		private String OPM_MSG_TXT;
		
		public String getOPM_MSG_CODE() {
			return OPM_MSG_CODE;
		}
		public void setOPM_MSG_CODE(String oPM_MSG_CODE) {
			OPM_MSG_CODE = oPM_MSG_CODE;
		}
		public String getOPM_MSG_TXT() {
			return OPM_MSG_TXT;
		}
		public void setOPM_MSG_TXT(String oPM_MSG_TXT) {
			OPM_MSG_TXT = oPM_MSG_TXT;
		}
	}
	public static class  OPM_PAGE_CTL extends BasicDM  {
		private static final long serialVersionUID = -594370886512215887L;
		@XStreamAlias("OPM-PAGE-STA-KEY")
		private String OPM_PAGE_STA_KEY;
		@XStreamAlias("OPM-PAGE-END-KEY")
		private String OPM_PAGE_END_KEY;
		@XStreamAlias("OPM-PAGE-NO-DATA")
		private String OPM_PAGE_NO_DATA;
		
		public String getOPM_PAGE_STA_KEY() {
			return OPM_PAGE_STA_KEY;
		}
		public void setOPM_PAGE_STA_KEY(String oPM_PAGE_STA_KEY) {
			OPM_PAGE_STA_KEY = oPM_PAGE_STA_KEY;
		}
		public String getOPM_PAGE_END_KEY() {
			return OPM_PAGE_END_KEY;
		}
		public void setOPM_PAGE_END_KEY(String oPM_PAGE_END_KEY) {
			OPM_PAGE_END_KEY = oPM_PAGE_END_KEY;
		}
		public String getOPM_PAGE_NO_DATA() {
			return OPM_PAGE_NO_DATA;
		}
		public void setOPM_PAGE_NO_DATA(String oPM_PAGE_NO_DATA) {
			OPM_PAGE_NO_DATA = oPM_PAGE_NO_DATA;
		}
	}
}
