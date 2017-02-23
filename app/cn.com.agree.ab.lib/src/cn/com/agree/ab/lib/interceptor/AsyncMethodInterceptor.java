package cn.com.agree.ab.lib.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import cn.com.agree.ab.lib.annotation.Async;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.task.TaskPlugin;
import cn.com.agree.inject.AopMatcher;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

@AutoBindMapper	/*添加@AutoBindMapper，该拦截器可自动被扫描并添加到Guice框架中*/
public class AsyncMethodInterceptor implements MethodInterceptor, AopMatcher {
	private static final Logger	logger	= LoggerFactory.getLogger(AsyncMethodInterceptor.class);
	@Override
	public Matcher<? super Class<?>> getClassMatcher() {
		return Matchers.annotatedWith(Biz.class);				//添加Biz注解的类
	}

	@Override
	public Matcher<? super Method> getMethodMatcher() {
		return Matchers.annotatedWith(Async.class);				//添加Async注解的方法，注意该注解不能与其它拦截器注解混用，只能手动使用
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object invoke(final MethodInvocation mi) throws Throwable {
		final Map mdcMap = MDC.getCopyOfContextMap();
		Future<?> result = TaskPlugin.getDefault().submit(
				new Callable<Object>() {
					public Object call() throws Exception {
						// 放在业务代码之前清理（选择性清理），是因为线程池初创新进程会复制父进程里的变量
						ContextHelper.destory();
						MDC.setContextMap(mdcMap);
						// end
						Object result  = null;
						try {
							result = mi.proceed();
							// 支持目标方法返回类型Future包装，请使用org.apache.commons.lang3.concurrent.ConcurrentUtils.constantFuture方法进行封装
							if (result instanceof Future) {
								return ((Future<?>) result).get();
							}
						} catch (Throwable ex) {
							logger.error("异步执行"+mi.getThis().getClass()+"的"+mi.getMethod().getName()+"方法发生异常", ex);
						}
						return result;
					}
				});

		if (Future.class.isAssignableFrom(mi.getMethod().getReturnType())) {
			return result;
		} else {
			return null;
		}
	}

}
