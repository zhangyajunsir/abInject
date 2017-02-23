package jsrccb.common.dm.rsp;

import cn.com.agree.ab.lib.dm.BasicDM;
import cn.com.agree.ab.lib.utils.converter.xstream.UseSubTypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("Service")
public class EsbRspDM extends BasicDM {
	private static final long serialVersionUID = -4688317090313204076L;
	@XStreamAlias("Service_Header")
	private ServiceHead serviceHead;
	@XStreamAlias("Service_Body")
	private ServiceBody serviceBody;
	
	public ServiceHead getServiceHead() {
		return serviceHead;
	}
	public void setServiceHead(ServiceHead serviceHead) {
		this.serviceHead = serviceHead;
	}
	public ServiceBody getServiceBody() {
		return serviceBody;
	}
	public void setServiceBody(ServiceBody serviceBody) {
		this.serviceBody = serviceBody;
	}
	
	public static class ServiceHead extends BasicDM {
		private static final long serialVersionUID = -7126259021846139163L;
		@XStreamAlias("name")
		private String name;
		@XStreamAlias("service_response")
		private RspStatus rspStatus;
		 
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public RspStatus getRspStatus() {
			return rspStatus;
		}
		public void setRspStatus(RspStatus rspStatus) {
			this.rspStatus = rspStatus;
		}

		public static class RspStatus extends BasicDM {
			private static final long serialVersionUID = 313900146041189170L;
			@XStreamAlias("code")
			private String code;
			@XStreamAlias("desc")
			private String desc;
			@XStreamAlias("status")
			private String status;
			
			public String getCode() {
				return code;
			}
			public void setCode(String code) {
				this.code = code;
			}
			public String getDesc() {
				return desc;
			}
			public void setDesc(String desc) {
				this.desc = desc;
			}
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
		}
	}
	
	@XStreamConverter(value = UseSubTypeConverter.class, strings={"ext_attributes,extAttributes\nresponse,response"})
	public static class ServiceBody extends BasicDM {
		private static final long serialVersionUID = -3536571576926646870L;
		
		private BasicDM extAttributes;
		
		private Object response;

		public BasicDM getExtAttributes() {
			return extAttributes;
		}
		public void setExtAttributes(BasicDM extAttributes) {
			this.extAttributes = extAttributes;
		}
		public Object getResponse() {
			return response;
		}
		public void setResponse(Object response) {
			this.response = response;
		}
	}
	
}
