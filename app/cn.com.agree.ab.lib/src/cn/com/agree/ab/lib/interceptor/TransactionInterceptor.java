package cn.com.agree.ab.lib.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Transaction;
import cn.com.agree.ab.trade.ext.persistence.impl.PersistenceAdapterFactory;
import cn.com.agree.inject.AopMatcher;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 用于业务层事务的拦截器
 * @author zhangyajun
 */
@AutoBindMapper	/*添加@AutoBindMapper，该拦截器可自动被扫描并添加到Guice框架中*/
public class TransactionInterceptor implements MethodInterceptor, AopMatcher {
	private static final Logger	logger	= LoggerFactory.getLogger(TransactionInterceptor.class);
			
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		// 嵌套事务处理，平台TransactionManagerDelegate已经做了些相关控制，请详读
		Object obj = null;  
        try {  
        	PersistenceAdapterFactory.getPersistence().startTransaction();
        	logger.debug("事务开始");
            obj = mi.proceed();  
            PersistenceAdapterFactory.getPersistence().commitTransaction();
            logger.debug("事务提交");  
        } catch (Exception e) {
        	// TODO 回滚
        	
        	throw e;
        } finally {  
        	PersistenceAdapterFactory.getPersistence().endTransaction();
        	logger.debug("事务结束");  
        }  
        return obj;  
	}

	@Override
	public Matcher<? super Class<?>> getClassMatcher() {
		return Matchers.annotatedWith(Biz.class);			//添加Biz注解的类
	}

	@Override
	public Matcher<? super Method> getMethodMatcher() {
		return Matchers.annotatedWith(Transaction.class);	//添加Transaction注解的方法
	}

}
