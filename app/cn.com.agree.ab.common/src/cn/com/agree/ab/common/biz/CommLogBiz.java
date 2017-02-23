package cn.com.agree.ab.common.biz;

import cn.com.agree.ab.common.dm.CommLogDM;
import cn.com.agree.ab.common.dm.TradeDataDM;

public interface CommLogBiz {

	public void addCommLog(CommLogDM commLogDM);
	
	public CommLogDM initCommLog(String commCode, TradeDataDM tradeDataDM);
}
