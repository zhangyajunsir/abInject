package cn.com.agree.ab.common.biz;

public interface SerialNumBiz {
	
	public String generateFrontSerialNum(String teller, Integer tradeId);

	public String generateTellerSerialNum(String teller, String txDate, TellerFlowType tellerFlowType);
	
	public static enum TellerFlowType {
		ACCOUNT(1, "账务类"), MANAGE(2, "管理类"), CHECK(3, "复核类"), REVERSE(4, "冲正类");
		
		private Integer status;
		private String desc;

		TellerFlowType(Integer type, String desc) {
			this.status = type;
			this.desc = desc;
		}

		public Integer getType() {
			return status;
		}

		public String getDesc() {
			return desc;
		}

		public String toString() {
			return desc;
		}
	}
	
	
}
