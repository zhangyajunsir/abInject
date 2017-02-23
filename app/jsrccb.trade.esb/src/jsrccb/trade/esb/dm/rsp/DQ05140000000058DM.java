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
	private String cifSeq;	// �������пͻ���
	@XStreamAlias("CifNo")	
	private String cifNo;	// ��ҵ�ͻ���
	@XStreamAlias("CifName")	
	private String cifName;	// �ͻ�����
	@XStreamAlias("CheckerNum")	
	private String checkerNum;	// ���˲���Ա����
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
			private String userId;	// ����ԱID
			@XStreamAlias("UserName")
			private String userName;	// ����Ա����
			@XStreamAlias("UserGrpId")
			private String userGrpId;	// �û�����
			@XStreamAlias("IdType")
			private String idType;	// ֤������
			@XStreamAlias("IdNo")
			private String idNo;	// ֤����
			@XStreamAlias("Email")
			private String email;	// Email
			@XStreamAlias("Phone")
			private String phone;	// �绰
			@XStreamAlias("OpenDate")
			private String openDate;	// ���˹��ܿ�ͨ����
			@XStreamAlias("UserState")
			private String userState;	// ����Ա״̬
			@XStreamAlias("UserFlag")
			private String userFlag;	// ����Ա�Ķ��˽�ɫ
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
