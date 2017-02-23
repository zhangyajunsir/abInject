package jsrccb.common.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.agree.ab.lib.dm.BasicDM;

public class AuthDM extends BasicDM {
	private static final long serialVersionUID = -1914559271886087101L;

	/** 授权状态 */
	public static enum AuthStatus {
		NO_AUTH(0, "未授权"), NEED_AUTH(1, "待授权"), PASS_AUTH(2, "通过授权"), REJECT_AUTH(3, "拒绝授权"), CANCEL_AUTH(4, "取消授权");
		
		private Integer status;
		private String desc;

		AuthStatus(Integer status, String desc) {
			this.status = status;
			this.desc = desc;
		}

		public Integer getStatus() {
			return status;
		}

		public String getDesc() {
			return desc;
		}

		public String toString() {
			return desc;
		}
	}

	/** 授权方式 ：1.本地授权 2.同机构同步远程授权 3.跨机构异步远程授权 4.授权中心异步远程授权 */
	public static enum AuthWay {
		LOCAL_AUTH(1, "本地授权"), ORG_REMOTE_AUTH(2, "同机构远程授权"), SUPER_ORG_REMOTE_AUTH(3, "跨机构远程授权"), AUTH_CENTER_REMOTE_AUTH(4, "授权中心远程授权");
		private Integer way;
		private String name;

		AuthWay(Integer way, String name) {
			this.setWay(way);
			this.setName(name);
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setWay(Integer way) {
			this.way = way;
		}

		public Integer getWay() {
			return way;
		}
	}

	/** 授权类型：1.强制授权 2.条件授权 3.金额授权 4.执行等级授权 9.动态授权 */
	public static enum AuthType {
		FORCE_AUTH  (1, "ForceAuth",   "强制授权"), 
		COND_AUTH   (2, "CondAuth",    "条件授权"),
		MONEY_AUTH  (3, "MoneyAuth",   "金额授权"), 
		LEVEL_AUTH  (4, "LevelAuth",   "执行等级授权"), 
		DYNAMIC_AUTH(9, "DynamicAuth", "动态授权");
		
		private Integer type;
		private String name;
		private String desc;

		AuthType(Integer type, String name, String desc) {
			this.type = type;
			this.name = name;
			this.desc = desc;
		}

		public Integer getType() {
			return type;
		}

		public String getDesc() {
			return desc;
		}

		public String getName() {
			return name;
		}
	}

	/** 授权等级 */
	public static enum AuthLevel {
		B(1, "B级主管授权"), A(2, "A级主管授权"), AB(3, "A级+B级主管授权");
		private Integer level;
		private String  desc;

		AuthLevel(Integer level, String desc) {
			this.level = level;
			this.desc = desc;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

		public Integer getLevel() {
			return level;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
		
		public static AuthLevel getAuthLevel(Integer level) {
			for (AuthLevel authLevel : AuthLevel.values()) {
				if (level == authLevel.level) {
					return authLevel;
				}
			}
			return null;
		}
		public static AuthLevel getMinAuthLevel() {
			AuthLevel minAuthLevel = null;
			for (AuthLevel authLevel : AuthLevel.values()) {
				if (minAuthLevel == null || authLevel.getLevel() < minAuthLevel.getLevel()) {
					minAuthLevel = authLevel;
				}
			}
			return minAuthLevel;
		}
		public static AuthLevel getMaxAuthLevel() {
			AuthLevel minAuthLevel = null;
			for (AuthLevel authLevel : AuthLevel.values()) {
				if (minAuthLevel == null || authLevel.getLevel() > minAuthLevel.getLevel()) {
					minAuthLevel = authLevel;
				}
			}
			return minAuthLevel;
		}
	}

	private AuthStatus authStatus;

	public AuthStatus getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(AuthStatus authStatus) {
		this.authStatus = authStatus;
	}
	
	private List<String> authMSG = new ArrayList<String>();

	public List<String> getAuthMSG() {
		return authMSG;
	}

	private AuthLevel authLevel;

	public void setAuthLevel(AuthLevel authLevel) {
		this.authLevel = authLevel;
	}

	public AuthLevel getAuthLevel() {
		return authLevel;
	}
	
	private List<AuthType> authType = new ArrayList<AuthType>();

	public List<AuthType> getAuthType() {
		return authType;
	}
	
	private AuthWay authWay;
	
	public void setAuthWay(AuthWay authWay) {
		this.authWay = authWay;
	}

	public AuthWay getAuthWay() {
		return authWay;
	}
	
	private String authTellerA;		//授权柜员A
	private String authTellerB;		//授权柜员B
	
	private String authTellerAPWD;	//授权柜员A密码
	private String authTellerBPWD;	//授权柜员B密码
	
	private List<String> reason ;
	public String getAuthTellerA() {
		return authTellerA;
	}

	public void setAuthTellerA(String authTellerA) {
		this.authTellerA = authTellerA;
	}

	public String getAuthTellerB() {
		return authTellerB;
	}

	public void setAuthTellerB(String authTellerB) {
		this.authTellerB = authTellerB;
	}

	public String getAuthTellerAPWD() {
		return authTellerAPWD;
	}

	public void setAuthTellerAPWD(String authTellerAPWD) {
		this.authTellerAPWD = authTellerAPWD;
	}

	public String getAuthTellerBPWD() {
		return authTellerBPWD;
	}

	public void setAuthTellerBPWD(String authTellerBPWD) {
		this.authTellerBPWD = authTellerBPWD;
	}

	private Map<String, Object> authContext = new HashMap<String, Object>();

	public Map<String, Object> getAuthContext() {
		return authContext;
	}

	public List<String> getReason() {
		return reason;
	}

	public void setReason(List<String> reason) {
		this.reason = reason;
	}

	
	
}
