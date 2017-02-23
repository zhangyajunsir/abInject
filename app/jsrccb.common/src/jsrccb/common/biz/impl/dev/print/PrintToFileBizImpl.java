package jsrccb.common.biz.impl.dev.print;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.PrintBiz;

import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;



/**
 * 打印到文件
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintBiz.class, multiple = true)
@Singleton
@Biz("printText2FileBiz")
public class PrintToFileBizImpl extends PrintBizImpl {
	@Inject
	@Named("printText2File")
	private   PrintUtil<String> printUtil;

	protected PrintUtil<String> printUtil() {
		return printUtil;
	}
	
	@Override
	public String type() {
		return "text2File";
	}
}
