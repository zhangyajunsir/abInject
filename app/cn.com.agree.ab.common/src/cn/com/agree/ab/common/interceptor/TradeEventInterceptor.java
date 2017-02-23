package cn.com.agree.ab.common.interceptor;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.component.common.Component;
import cn.com.agree.ab.trade.local.TradeBreakException;
import cn.com.agree.inject.AopMatcher;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

@AutoBindMapper	/*添加@AutoBindMapper，该拦截器可自动被扫描并添加到Guice框架中*/
public class TradeEventInterceptor  implements MethodInterceptor, AopMatcher  {
	private static final Logger	logger	= LoggerFactory.getLogger(TradeEventInterceptor.class);

	@Override
	public Matcher<? super Class<?>> getClassMatcher() {
		// 拦截所有AbstractCommonTrade类的子类
		return Matchers.subclassesOf(Trade.class); 
	}

	@Override
	public Matcher<? super Method> getMethodMatcher() {
		// 拦截AbstractCommonTrade类的子类里所有事件方法 
		return new TradeEventMatcher(".*_On.*", "^on.*", "exit", "clear");
	}

	/**
	 * 对于拦截链暂时没想到好的办法，就将所有的拦截逻辑写到一个拦截器中，在该拦截器中来控制拦截逻辑的顺序以及条件
	 */
	@Override
	public  Object invoke (MethodInvocation mi) throws Throwable {
		return invoke0(mi);
	}

	private Object invoke0(MethodInvocation mi) throws Throwable {
		Object obj = null; 
		try {
			logger.debug("拦截"+mi.getMethod().getName()+"开始");
			obj = invoke1(mi);
		} catch (BasicRuntimeException bre) {	// 业务异常
			ExceptionLevel exceptionLevel = bre.getLevel();
			if (exceptionLevel == ExceptionLevel.WARN)
				logger.warn (mi.getMethod().getName()+"发生业务警告:", bre);
			if (exceptionLevel == ExceptionLevel.ERROR || exceptionLevel == ExceptionLevel.FATAL) {
				logger.error(mi.getMethod().getName()+"发生业务错误:", bre);
				autoFocus(mi);
			}
			throw new TradeBreakException(exceptionLevel.getLevel(), bre.getMessage());
		}  catch (TradeBreakException e) {		// 事件中断
			if (e.getLevel() == ExceptionLevel.ERROR.getLevel() || e.getLevel() == ExceptionLevel.FATAL.getLevel()) {
				autoFocus(mi);
			}
			throw e;
		}  catch (Throwable e) {
			autoFocus(mi);
			logger.error(mi.getMethod().getName()+"发生异常:", e);
			throw e;
		} finally {
			ContextHelper.destory();			// 销毁ThreadLocal的上下文
			logger.debug("拦截"+mi.getMethod().getName()+"结束");
		}
		return obj;
	}
	
	private Object invoke1(MethodInvocation mi) throws Throwable {
		if (Pattern.matches(".*_On.*", mi.getMethod().getName()) && mi.getThis() instanceof AbstractCommonTrade) {
			AbstractCommonTrade abstractCommonTrade = (AbstractCommonTrade) mi.getThis();
			if (abstractCommonTrade.getCurrentTradeEvent() != null) {
				Object o = null;
				abstractCommonTrade.getCurrentTradeEvent().preEvent(abstractCommonTrade, mi.getMethod());
				o = mi.proceed();
				abstractCommonTrade.getCurrentTradeEvent().posEvent(abstractCommonTrade, mi.getMethod());
				return o;
			}
		}
		return mi.proceed();
	}
	
	@SuppressWarnings("deprecation")
	private void autoFocus(MethodInvocation mi) throws Throwable {
		Trade abstractCommonTrade = (Trade) mi.getThis();
		if (Pattern.matches(".*_On.*", mi.getMethod().getName())) {
			Component component = TradeHelper.getComponent(abstractCommonTrade, StringUtils.substringBeforeLast(mi.getMethod().getName(),"_On"));
			if (component == null)
				return;
			if (Pattern.matches(".*_OnFocus", mi.getMethod().getName()))
				abstractCommonTrade.traverseFocus(component, false); // 将焦点设置当前组件的前一个组件
			else
				abstractCommonTrade.setFocus(component);
		}
	}

}
