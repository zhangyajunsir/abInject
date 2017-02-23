package cn.com.agree.ab.common.biz.impl.event;


import java.lang.reflect.Method;
import java.util.Map;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.biz.ITradeEventBiz;

/**
 * 具体实现通过子类覆盖相应方法
 * @author zhangyajun
 *
 */
public abstract class AbstractTradePropEventBiz implements ITradeEventBiz<AbstractCommonTrade> {

	public void onInit   (AbstractCommonTrade trade) {
	}
	
	public void preCommit(AbstractCommonTrade trade, String message) {
	}
	
	public void onCommit (AbstractCommonTrade trade, String message) {
	}
	
	public void posCommit(AbstractCommonTrade trade, String message) {
	}
	
	public void onMessage(AbstractCommonTrade trade, String app, String message) {
	}
	
	public void onResume (AbstractCommonTrade trade, Map<?,?> suspendResult) {
	}
	
	public void preEvent (AbstractCommonTrade trade, Method eventMethod) {
	}
	
	public void posEvent (AbstractCommonTrade trade, Method eventMethod) {
	}
	
	public void preExit  (AbstractCommonTrade trade) {
	}
	
	public void posExit  () {
	}

}
