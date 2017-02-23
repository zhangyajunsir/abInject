package cn.com.agree.ab.common.utils.print;

import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 打印文本到流水打印机
 * 流水打印都是到远程流水服务器上打印，但打印文件在本地服务器上生成
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintUtil.class)
@Singleton
@Named("printText2LP")
public class PrintText2LP extends PrintText2File {

	@Override
	public void print(Trade trade, String printData) {
		if (printData == null)
			return;
		super.print(trade, printData);
		
		print2LP(trade, "./tmp/WINTABLE"+trade.getStoreData("G_TELLER")+".dat");
	}

	public void print2LP(Trade trade, String filePath) {
		
	}

}
