package jsrccb.trade.esb.dm.rsp;

import java.util.List;
import cn.com.agree.ab.lib.dm.BasicDM;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
@XStreamAlias("response")
public class DQ05140000000058DM extends BasicDM {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XStreamAlias("CifSeq")
	private String cifSeq;	// 电子银行客户号
	@XStreamAlias("CifNo")	
	private String cifNo;	// 企业客户号
	@XStreamAlias("CifName")	
	private String cifName;	// 客户名称
	@XStreamAlias("CheckerNum")	
	private String checkerNum;	// 对账操作员个数
	@XStreamAlias("Submitter")
	private String submitter;	// 
	@XStreamAlias("Examiner")
	private String examiner;	//
	@XStreamAlias("ChecksignFlag")
	private String checksignFlag;	//
	
	public String getCifSeq() {
		return cifSeq;
	}
	public String getCifNo() {
		return cifNo;
	}
	public String getCifName() {
		return cifName;
	}
	public String getCheckerNum() {
		return checkerNum;
	}
	public String getSubmitter() {
		return submitter;
	}
	public String getExaminer() {
		return examiner;
	}
	public String getChecksignFlag() {
		return checksignFlag;
	}
	public SignBean getList() {
		return list;
	}

	@XStreamAlias("List")
	private SignBean list;
	public static class SignBean{
		@XStreamImplicit(itemFieldName = "Map")
		private List<SignLable> Map;

		public List<SignLable> getMap() {
			return Map;
		}
		public static class SignLable{
			@XStreamAlias("UserId")
			private String userId;	// 操作员ID
			@XStreamAlias("UserName")
			private String userName;	// 操作员名称
			@XStreamAlias("UserGrpId")
			private String userGrpId;	// 用户级别
			@XStreamAlias("IdType")
			private String idType;	// 证件类型
			@XStreamAlias("IdNo")
			private String idNo;	// 证件号
			@XStreamAlias("Email")
			private String email;	// Email
			@XStreamAlias("Phone")
			private String phone;	// 电话
			@XStreamAlias("OpenDate")
			private String openDate;	// 对账功能开通日期
			@XStreamAlias("UserState")
			private String userState;	// 操作员状态
			@XStreamAlias("UserFlag")
			private String userFlag;	// 操作员的对账角色
			public String getUserId() {
				return userId;
			}
			public String getUserName() {
				return userName;
			}
			public String getUserGrpId() {
				return userGrpId;
			}
			public String getIdType() {
				return idType;
			}
			public String getIdNo() {
				return idNo;
			}
			public String getEmail() {
				return email;
			}
			public String getPhone() {
				return phone;
			}
			public String getOpenDate() {
				return openDate;
			}
			public String getUserState() {
				return userState;
			}
			public String getUserFlag() {
				return userFlag;
			}
			
		}
	}
	

}
