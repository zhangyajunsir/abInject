package jsrccb.common.biz.dev;

import cn.com.agree.ab.trade.core.Trade;

public interface OutClearBiz {
	/**
	 * 柜外清显示内容带按钮
	 * 显示内容，内部使用|连接
	 * @param trade
	 * @param must
	 * @param displayInfos
	 * @return
	 */
	public boolean displayWithButton(Trade trade, boolean must, String... displayInfos);
	/**
	 * 柜外清显示内容不带按钮
	 * 显示内容，内部使用|连接
	 * @param trade
	 * @param must
	 * @param displayInfos
	 */
	public void    displayNoButton  (Trade trade, boolean must, String... displayInfos);
	
	public static class OutClearConfig {
		private boolean configSwitch;
		private String  coins;
		private String  amount;
		public boolean isConfigSwitch() {
			return configSwitch;
		}
		public void setConfigSwitch(boolean configSwitch) {
			this.configSwitch = configSwitch;
		}
		public String getCoins() {
			return coins;
		}
		public void setCoins(String coins) {
			this.coins = coins;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
	}
	
	public OutClearConfig getOutClearConfig(String LVL_BRH_ID, String BRANCH);
}
