package cn.com.agree.ab.common.biz.impl.event;


/**
 * 具体条件判断，由子类的公共事件方法逻辑里，自行进行
 * 条件之间，均平等
 * @author zhangyajun
 *
 */
public abstract class AbstractTradeCondEventBiz extends AbstractTradePropEventBiz {
	
	/**
	 * 是否可由CommonTradeEventBizImpl动态的调用，子类覆盖该方法
	 * @return
	 */
	public boolean dynamic() {
		return false;
	}
	
	
}
