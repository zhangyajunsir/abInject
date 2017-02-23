package jsrccb.common.biz.dev;

import jsrccb.common.dm.dev.AccountDM;
import cn.com.agree.ab.trade.core.Trade;

public interface MsfBiz {
	// 读磁条3磁道
	public AccountDM readMSF3 (Trade trade, boolean must);
	// 读磁条23磁道
	public AccountDM readMSF23(Trade trade, boolean must);
	// 读柜员卡（磁条2磁道）
	public AccountDM readTellerCard(Trade trade, boolean must);
	// 写磁条23磁道
	public void writeMSF23(Trade trade, boolean must, AccountDM accountDM);
	// 写柜员卡（磁条2磁道）
	public void writeTellerCard(Trade trade, boolean must, AccountDM accountDM);
	
	
}
