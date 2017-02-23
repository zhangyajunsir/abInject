package cn.com.agree.ab.lib.biz;

import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.trade.core.Trade;

/**
 * 打开视图业务逻辑
 * @author zhangyajun
 */
public interface IViewOpenBiz {
	//	异步打开
	public static int ASYC_OPEN_TYPE = 1;
	//	退出打开
	public static int EXIT_OPEN_TYPE = 2;
	//	同步打开
	public static int SYNC_OPEN_TYPE = 3;
	//	挂起打开
	public static int SUSP_OPEN_TYPE = 4;
	//  交易容器打开
	public static int COMP_OPEN_TYPE = 5;
	
	/**
	 * 用于公共处理逻辑( 复核处理、授权处理、多页式处理、综合类处理等)中，准备数据
	 * @author zhangyajun
	 */
	public interface BeforeViewOpen {
		
		public void prepare(OpenViewArgDM openViewArg);
		
	}
	
	/**
	 * 打开视图
	 * 采用异步打开或交易容器打开
	 */
	public void asycOpenView(Trade trade, String tradeCode, OpenViewArgDM openViewArg, BeforeViewOpen... beforeViewOpens);
	
	/**
	 * 退出打开视图
	 */
	public void exitOpenView(Trade trade, String tradeCode, OpenViewArgDM openViewArg, BeforeViewOpen... beforeViewOpens);
	
	/**
	 * 同步打开视图
	 * 采用挂起打开方式，若使用同步打开方式，会造成事件方法调用处等待过长
	 */
	public void syncOpenView(Trade trade, String tradeCode, OpenViewArgDM openViewArg, BeforeViewOpen... beforeViewOpens);
	
}
