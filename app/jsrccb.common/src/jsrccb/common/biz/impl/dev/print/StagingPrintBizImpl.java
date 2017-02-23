package jsrccb.common.biz.impl.dev.print;

import java.util.List;

import javax.inject.Singleton;

import jsrccb.common.biz.PrintBiz;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.inject.annotations.AutoBindMapper;



/**
 * 暂存打印内容
 * 将打印的入参信息保存起来，注意一个前端交易应该只保存一份tradeDataDM，所有需要主附表设计
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintBiz.class)
@Singleton
@Biz("stagingPrintBiz")
public class StagingPrintBizImpl implements PrintBiz {

	@Override
	public void print      (Trade trade, String template, TradeDataDM tradeDataDM, String[] data, boolean preview, boolean reprint) {
		
	}

	@Override
	public void printGrid  (Trade trade, String template, TradeDataDM tradeDataDM, List<String[]> data, int pageSize, boolean preview) {
		
	}

	@Override
	public void printReport(Trade trade, String headTemplate, String bodyTemplate, String footTemplate, TradeDataDM tradeDataDM, List<String[]> data, int pageSize, boolean preview) {
		
	}

	@Override
	public String type() {
		return "stagingPrint";
	}


}
