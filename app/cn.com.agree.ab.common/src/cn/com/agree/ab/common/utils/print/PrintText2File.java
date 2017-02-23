package cn.com.agree.ab.common.utils.print;

import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.tools.FileUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 打印文本到文件
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintUtil.class)
@Singleton
@Named("printText2File")
public class PrintText2File extends PrintText {

	
	@Override
	public void print  (Trade trade, String printData) {
		if (printData == null)
			return;
		
		String path = "./tmp/WINTABLE"+trade.getStoreData(ITradeKeys.G_TELLER)+".dat";
		try {
			FileUtil.writeServerFile(path,printData.toString().getBytes("GBK"));
		} catch (Exception e) {
			throw new DevException(e.getMessage(), e);
		}
		
	}
	

}
