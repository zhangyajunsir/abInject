package jsrccb.common.biz.dev;

import cn.com.agree.ab.trade.core.Trade;

public interface FingerBiz {
	
	public boolean checkTellerFinger  (Trade trade, boolean must, String... param);
	
	public boolean checkCustomerFinger(Trade trade, boolean must, String... param);
	
	public String  readFinger(Trade trade, boolean must);

}
