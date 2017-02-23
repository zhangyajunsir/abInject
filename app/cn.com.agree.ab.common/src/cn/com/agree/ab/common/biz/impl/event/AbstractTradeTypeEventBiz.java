package cn.com.agree.ab.common.biz.impl.event;


import com.google.common.base.Preconditions;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.biz.ITradeEventBiz;
import cn.com.agree.ab.lib.exception.BizException;

public abstract class AbstractTradeTypeEventBiz implements ITradeEventBiz<AbstractCommonTrade> {

	public abstract String getType();
	
	public abstract String getBranch();
	
	protected TradeCodeDM getCurrentTradeCodeDM(AbstractCommonTrade trade) {
		TradeCodeDM tradeCodeDM = (TradeCodeDM)trade.getContext(ITradeContextKey.TRADE_CODE_DM);
		Preconditions.checkState(tradeCodeDM != null, "交易上下文对象没有TradeCodeDM");
		return tradeCodeDM;
	}
	
	public void onInit(AbstractCommonTrade trade) {
		TradeCodeDM tradeCodeDM = getCurrentTradeCodeDM(trade);
		if (tradeCodeDM.getTradePropList() == null || tradeCodeDM.getTradePropList().size() == 0) {
			throw new BizException("交易〖"+tradeCodeDM.getCode()+"〗未配置交易属性");
		}
		if (trade.getStoreData(ITradeKeys.T_COMM_CODE).equals(""))
			trade.setDefaultCommCode(tradeCodeDM.getTradePropList().get(0).getCommCode().getCommCode());
		else
			trade.setDefaultCommCode(trade.getStoreData(ITradeKeys.T_COMM_CODE));
		
	}
	
}
