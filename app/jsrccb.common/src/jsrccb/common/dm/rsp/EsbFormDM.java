package jsrccb.common.dm.rsp;

import java.util.List;

import cn.com.agree.ab.lib.dm.BasicDM;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

public class EsbFormDM extends BasicDM {
	private static final long serialVersionUID = -716300176993953721L;
	@XStreamImplicit(itemFieldName="FORM")
	private List<FormDM> formDMs;
	
	public List<FormDM> getFormDMs() {
		return formDMs;
	}
	public void setFormDMs(List<FormDM> formDMs) {
		this.formDMs = formDMs;
	}

	// 既有属性又有值
	@XStreamConverter(value = ToAttributedValueConverter.class, strings={"content"})
	public static class FormDM extends BasicDM {
		private static final long serialVersionUID = -250392369045241708L;
		@XStreamAlias("ID")
		@XStreamAsAttribute
		private String id;
		// node value
		private String content;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
	}
}
