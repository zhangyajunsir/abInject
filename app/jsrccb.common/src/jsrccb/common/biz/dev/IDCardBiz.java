package jsrccb.common.biz.dev;

import jsrccb.common.dm.dev.IDCardDM;
import cn.com.agree.ab.trade.core.Trade;

public interface IDCardBiz {
	
	public IDCardDM read(Trade trade, boolean must);

}
