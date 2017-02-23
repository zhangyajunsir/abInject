package cn.com.agree.ab.common.utils.print;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 终端打印PDF
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintUtil.class)
@Singleton
@Named("printPdf")
public class PrintPdf implements PrintUtil<URL> {

	@Override
	public void print  (Trade trade, URL printData) {
		// 1.将printData的FileURL转成httpserver提供的web访问的HttpURL
		// 2.调用command命令，并将HttpURL作为参数
		// 3.C端接受到command命令，请求HttpURL下载Pdf，如何打印Pdf文件待定

	}

	@Override
	public URL format  (Trade trade, String formatFile,  Map<String, Object> context, String[] arrayData) {
		// 1.将formatFile通过freemark生成完整的html内容
		// 2.通过flying-saucer-pdf-itext5将html转成pdf，并持久化到临时文件
		// 3.将临时文件通过resource插件返回此文件的URL
		
		return null;
	}

	@Override
	public void preview(Trade trade, URL printData) {
		// 1.将printData的FileURL转成httpserver提供的web访问的HttpURL
		// 2.调用command命令，并将HttpURL作为参数
		// 3.C端接受到command命令，请求HttpURL下载Pdf，并C端利用开源组件（JPview、PDFViewer、Pdf4Eclipse）来显示该Pdf
		
	}

	@Override
	public URL joinWithFF(Trade trade, List<URL> printDatas) {
		
		return null;
	}

}
