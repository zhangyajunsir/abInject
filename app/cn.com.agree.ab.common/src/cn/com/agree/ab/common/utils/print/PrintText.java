package cn.com.agree.ab.common.utils.print;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IPrinter;
import cn.com.agree.ab.trade.ext.ftable.IFTableAssembler;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.common.collect.Maps;

/**
 * 打印文本到终端打印机
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintUtil.class)
@Singleton
@Named("printText")
public class PrintText implements PrintUtil<String> {
	private static final Logger	logger	=  LoggerFactory.getLogger(PrintText.class);
	@Override
	public void print(Trade trade, String printData) {
		if (printData == null || !(printData instanceof String))
			return;
		
		IPrinter prt = trade.getDeviceManager().getLprinter();
		try {
			prt.init ();
			prt.print(printData.toString());
			prt.eject();
		} catch (IOException e) {
			throw new DevException(e.getMessage(), e);
		}
	}

	@Override
	public String format(Trade trade, String formatFile,  Map<String, Object> context, String[] arrayData) {
		logger.debug("log4j{[]}"+JsonUtil.obj2json(context));
		if (formatFile == null)
			return null;
		
		if (context   == null)
			context   =  Maps.newHashMap();
		if (arrayData == null)
			arrayData =  new String[]{};
		
		// F表里起始序号为1
		String[] _arraydata_ = new String[arrayData.length + 1]; _arraydata_[0] = "";
		System.arraycopy(arrayData, 0, _arraydata_, 1, arrayData.length);
		
		String buffer = null;
		try {
			IFTableAssembler fTableAssembler = (IFTableAssembler)trade.getAdapter(IFTableAssembler.class);
			buffer = fTableAssembler.winSayData(formatFile, _arraydata_, context);
			buffer = fTableAssembler.convertToPrint("^0" + buffer);
		} catch (Exception e) {
			throw new BasicRuntimeException(e.getMessage(), e);
		}
		return buffer;
	}

	@Override
	public void preview(Trade trade, String printData) {
		if (printData == null || !(printData instanceof String))
			return;
		try {
			// 后期改造展示方式，让其更美观
			// wait为true时，弹框未关闭时，该方法一直是阻塞
			trade.pushPrompt(Trade.PROMPT_INFO, "", (String)printData, true);
		} catch (IOException e) {
		}
	}

	@Override
	public String joinWithFF(Trade trade, List<String> printDatas) {
		StringBuilder buffer = new StringBuilder();
		for (int i=0; ; ) {
			buffer.append(printDatas.get(i));
			i++;
			if (i == printDatas.size()){
				buffer.append('\f');
				break;
			}
			buffer.append('\f');
		}
		return buffer.toString();
	}

}
