package jsrccb.common.biz.impl.dev.print;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import jsrccb.common.biz.PrintBiz;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.dm.TellerDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 终端正常打印
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintBiz.class, multiple = true)
@Singleton
@Biz("printTextBiz")
public class PrintBizImpl implements PrintBiz {
	private static final Logger	logger	= LoggerFactory.getLogger(PrintBizImpl.class);
	@Inject
	@Named("printText")
	private   PrintUtil<String> printUtil;
	
	protected PrintUtil<String> printUtil() {
		return printUtil;
	}
	
	protected Map<String, Object> getPrintContext(TradeDataDM tradeDataDM) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("uiData",     tradeDataDM.getUiData());
		context.put("storeData",  tradeDataDM.getStoreData());
		context.put("tellerInfo", tradeDataDM.getTellerInfo());
		context.put("tempArea",   tradeDataDM.getTempArea());
		context.put("context",    tradeDataDM.getContext());
		// 兼容老凭证
		context.putAll(tradeDataDM.getTellerInfo());
		context.put("G_FTIME",        tradeDataDM.getStoreData().get("G_FTIME"));
		context.put("G_SERIALNO",     tradeDataDM.getStoreData().get("G_SERIALNO"));
		context.put("G_TRADE_CODE",   tradeDataDM.getStoreData().get(ITradeKeys.T_TRADE_CODE));
		context.put("G_TRADE_NAME",   tradeDataDM.getStoreData().get(ITradeKeys.T_TRADE_NAME));
		context.put("G_DXE_NO",       tradeDataDM.getStoreData().get("G_DXE_NO"));
		context.put("G_AIB_NO",       tradeDataDM.getStoreData().get("G_AIB_NO"));
		context.put("G_AUTHTELLER",   tradeDataDM.getStoreData().get("G_AUTHTELLER"));
		context.put("G_CHECK_TELLER", tradeDataDM.getStoreData().get("G_CHECK_TELLER"));
		logger.debug("柜员信息"+tradeDataDM.getTellerInfo());
		logger.debug("授权柜员信息"+tradeDataDM.getTempArea().get("OPR_NO_FIX"));
		/** 老交易里使用的变量，迁移到新的需要修改相应凭证
			String TRADE_PRINT_VAR = trade.getStoreData("TRADE_PRINT_VAR");
			if (!"".equals(TRADE_PRINT_VAR)) {
				retMap.putAll(CsvUtil.csvToMap(TRADE_PRINT_VAR));
			}
		*/
		return context;
	}
	
	@Override
	public String type() {
		return "text";
	}
	
	protected String format(Trade trade, String template, TradeDataDM tradeDataDM, String[] data) {
		return printUtil().format(trade, template, getPrintContext(tradeDataDM), data);
	}

	@Override
	public void print(Trade trade, String template, TradeDataDM tradeDataDM, String[] data, boolean preview, boolean reprint) {
		String printData = format(trade, template, tradeDataDM, data);
		if (printData == null)
			return;
		if (preview)
			printUtil().preview(trade, printData);
		while (true) {
			try {
				printUtil().print(trade, printData);
			} catch (DevException e) {
				logger.error("打印异常", e);
				if (trade.isUserConfirmed("打印机未准备充分，是否继续打印？"+"\n错误信息："+e.getMessage(), "确定", "取消", false))
					continue;
			}
			break;
		}
		if (reprint) {
			int reprintCount  = 1;
			String confirmMsg = "是否重新打印？";
			while (trade.isUserConfirmed(confirmMsg, "确定", "取消", false)) { 
				StringBuilder buffer = new StringBuilder(printData);
				buffer.insert(buffer.indexOf("\n")-1, "[补" + reprintCount + "] ");
				try {
					printUtil().print(trade, buffer.toString());
				} catch (DevException e) {
					logger.error("打印异常", e);
					confirmMsg = "打印机未准备充分，是否继续打印？"+"\n错误信息："+e.getMessage();
				}
				reprintCount++;
			}
		}
	}

	@Override
	public void printGrid(Trade trade, String template, TradeDataDM tradeDataDM, List<String[]> data, int pageSize, boolean preview) {
		if (data == null || data.size() == 0)
			return;
		List<String>  pages  = Lists.newArrayList();
		StringBuilder buffer = new StringBuilder();
		for (int i=0; ; ) {
			buffer.append(format(trade, template, tradeDataDM, data.get(i)));
			i++;
			if (i == data.size() || (pageSize > 0 && i%pageSize == 0)) {
				pages.add(buffer.toString());
				buffer.setLength(0);
			}
			if (i == data.size())
				break;
		}
		String printData = printUtil().joinWithFF(trade, pages);
		if (preview)	// 这里也可以修改成每一页分别预览
			printUtil().preview(trade, printData);
		while (true) {
			try {
				printUtil().print(trade, printData);
			} catch (DevException e) {
				logger.error("打印异常", e);
				if (trade.isUserConfirmed("打印机未准备充分，是否继续打印？"+"\n错误信息："+e.getMessage(), "确定", "取消", false))
					continue;
			}
			break;
		}
	}

	@Override
	public void printReport(Trade trade, String headTemplate, String bodyTemplate, String footTemplate, TradeDataDM tradeDataDM, List<String[]> data, int pageSize, boolean preview) {
		if (data == null || data.size() == 0)
			return;
		tradeDataDM.getTempArea().put("printDate", DateUtil.getDateToString().substring(0, 8));
		tradeDataDM.getTempArea().put("printTime", DateUtil.getDateToString().substring(8, 14));
		tradeDataDM.getTempArea().put("recordCounts", data.size());			// 总记录数
		int pageCounts = 0;
		if (pageSize <= 0)
			pageCounts = 1;
		else
			pageCounts =  data.size()%pageSize == 0 ? data.size()/pageSize : data.size()/pageSize+1;
		tradeDataDM.getTempArea().put("pageCounts",  pageCounts);			// 总页数
		
		List<String>  pages  = Lists.newArrayList();
		StringBuilder buffer = new StringBuilder();
		for (int i=0; ; ) {
			buffer.append(format(trade, bodyTemplate, tradeDataDM, data.get(i))).append('\n');
			i++;
			if (i == data.size() || (pageSize > 0 && i%pageSize == 0)) {
				tradeDataDM.getTempArea().put("currentPage",  pages.size()+1);	// 当前页
				String head = format(trade, headTemplate, tradeDataDM, null);	// 可能页眉信息里部分内容，每页都不一样
				if (head == null)
					head =  "";
				String foot = format(trade, footTemplate, tradeDataDM, null);	// 可能页脚信息里部分内容，每页都不一样
				if (foot == null)
					foot =  "";
				pages.add(head+"\n"+buffer.toString()+foot);
				buffer.setLength(0);
			}
			if (i == data.size())
				break;
		}
		String printData = printUtil().joinWithFF(trade, pages);
		if (preview && "".equals(printData)  && printData.length() < 1024 )	// 这里也可以修改成每一页分别预览
			printUtil().preview(trade, printData);
		while (true) {
			try {
				printUtil().print(trade, printData);
			} catch (DevException e) {
				logger.error("打印异常", e);
				if (trade.isUserConfirmed("打印机未准备充分，是否继续打印？"+"\n错误信息："+e.getMessage(), "确定", "取消", false))
					continue;
			}
			break;
		}
	}


}
