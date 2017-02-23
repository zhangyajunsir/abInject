package jsrccb.common.biz.impl.dev.print;

import javax.inject.Singleton;

import jsrccb.common.biz.PrintBiz;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.inject.annotations.AutoBindMapper;



/**
 * 终端冲正打印
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintBiz.class, multiple = true)
@Singleton
@Biz("reversePrintTextBiz")
public class ReversePrintBizImpl extends PrintBizImpl {

	protected String format(Trade trade, String template, TradeDataDM tradeDataDM, String[] data) {
		String printData  = super.format(trade, template, tradeDataDM, data);
		StringBuilder buffer = new StringBuilder(printData);
		String reverseSeq = (String)tradeDataDM.getStoreData().get(ITradeKeys.G_REVERSE_SEQNO);
		if (reverseSeq != null && !reverseSeq.isEmpty()) {
			buffer.insert(buffer.indexOf("\n")-1, "[冲正" + reverseSeq + "] ");
		}
		return buffer.toString();
	}

}
