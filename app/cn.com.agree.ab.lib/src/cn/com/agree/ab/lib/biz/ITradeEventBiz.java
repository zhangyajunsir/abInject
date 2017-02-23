package cn.com.agree.ab.lib.biz;

import java.lang.reflect.Method;
import java.util.Map;

import cn.com.agree.ab.trade.core.Trade;

/**
 * 公共事件业务逻辑
 * @author zhangyajun
 */
public interface ITradeEventBiz<T extends Trade> {

	public void onInit   (T trade);
	
	public void onCommit (T trade, String message);
	
	public void onMessage(T trade, String app, String message);
	
	public void onResume (T trade, Map<?,?> suspendResult);
	
	public void preEvent (T trade, Method eventMethod);
	
	public void posEvent (T trade, Method eventMethod);
	
	public void preExit  (T trade);
	
	public void posExit  ();
	
}
