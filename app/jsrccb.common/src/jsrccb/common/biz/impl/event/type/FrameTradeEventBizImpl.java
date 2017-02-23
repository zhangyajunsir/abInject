package jsrccb.common.biz.impl.event.type;

import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Singleton;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeTypeEventBiz;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.local.TradeBreakException;
import cn.com.agree.commons.csv.CsvUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractTradeTypeEventBiz.class, multiple = true)
@Singleton
@Biz("frameTradeEventBizImpl")	
public class FrameTradeEventBizImpl extends CommonTradeEventBizImpl {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onInit(AbstractCommonTrade trade) {
		super.onInit(trade);
		try {
			Map tellerInfo = trade.getTellerInfo();
			tellerInfo.put("FRAME_EXPRESSION_ID", trade.getStoreData(ITradeKeys.T_TRADE_EXPRESSION_ID));
			tellerInfo.put("FRAME_TRADE_CODE",    trade.getStoreData(ITradeKeys.T_TRADE_CODE));
			trade.updateTellerInfo(tellerInfo);
			
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用主交易初始化方法发生错误：" + e.getMessage(), e);
		}
	}

	@Override
	public void onCommit(AbstractCommonTrade trade, String message) {
		super.onCommit(trade, message);
		try {
			
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用主交易提交方法发生错误：" + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void onMessage(AbstractCommonTrade trade, String app, String message) {
		super.onMessage(trade, app, message);
		
		if ("CompositeOpenTrade".equals(app)) {
			Map cont = CsvUtil.csvToMap(message);
			// TODO exportNames 应该返给实际交易，而非主交易
			trade.componentCommand("multiTradeComposite", "startTrade", (String)cont.get("tradeCode"), (String)cont.get("title"), (String)cont.get("importVar"), (String)cont.get("exportNames"));
		}
	}

	@Override
	public void onResume(AbstractCommonTrade trade, Map suspendResult) {
		super.onResume(trade, suspendResult);
		
	}

	@Override
	public void preEvent(AbstractCommonTrade trade, Method eventMethod) {
		super.preEvent(trade, eventMethod);
		try {
			
			
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用" + eventMethod.getName() + "方法发生错误：" + e.getMessage(), e);
		}
	}

	@Override
	public void posEvent(AbstractCommonTrade trade, Method eventMethod) {
		try {
			

		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用" + eventMethod.getName() + "方法发生错误：" + e.getMessage(), e);
		}
		super.posEvent(trade, eventMethod);
	}

	@Override
	public void preExit(AbstractCommonTrade trade) {
		super.preExit(trade);
		
	}

	@Override
	public void posExit() {


		super.posExit();
	}

	@Override
	public String getType() {
		return "frame";	// 主框架类型
	}

	@Override
	public String getBranch() {
		return "";		// 全法人
	}

}
