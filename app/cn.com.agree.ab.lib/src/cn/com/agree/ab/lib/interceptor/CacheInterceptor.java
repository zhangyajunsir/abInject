package cn.com.agree.ab.lib.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.CacheEvict;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.ab.lib.cache.Cache;
import cn.com.agree.ab.lib.cache.CacheManager;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.utils.ReflectionUtil;
import cn.com.agree.ab.lib.utils.StringFreemarker;
import cn.com.agree.inject.AopMatcher;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

/** 
 * 参照spring cache提供的注解功能实现
 * 缓存失效策略主要为TTL(Time To Live)存活期，即从缓存中创建时间点开始直到它到期的一个时间段，具体查看GuavaCacheManager里60分钟失效
 * 后期考虑提供@CachePut和@CacheEvict用于信息变更和删除时及时更新缓存
 * @author zhangyajun
 */
@AutoBindMapper	/*添加@AutoBindMapper，该拦截器可自动被扫描并添加到Guice框架中*/
public class CacheInterceptor implements MethodInterceptor, AopMatcher {
	private static final Logger	logger	= LoggerFactory.getLogger(CacheInterceptor.class);
	
	/** 
	 * GuavaCacheManager具备60分钟TTL策略
	 * */
	@Inject
	@Named("guavaCacheManager")
    private CacheManager cacheManager;
	@Inject
	private StringFreemarker stringFreemarker;
	
	private Map<String,String> idReflectByArgs = new ConcurrentHashMap<String,String>();

	@Override
	public Matcher<? super Class<?>> getClassMatcher() {
		return Matchers.annotatedWith(Biz.class);				//添加Biz注解的类
	}

	@Override
	public Matcher<? super Method> getMethodMatcher() {
		return Matchers.annotatedWith(Cacheable.class).or(Matchers.annotatedWith(CacheEvict.class));			//添加Cache注解的方法
	}

	//以原查询方法(@Cacheable)做参数调用该方法获取并读取缓存
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object o = null;
		if (mi.getMethod().isAnnotationPresent(CacheEvict.class)) {
			o = updAndEvictCache(mi);
		}
		if (mi.getMethod().isAnnotationPresent(Cacheable.class)) {
			o = getAndUpdatCache(mi);
		}
		return o;
	}
	
	private Object updAndEvictCache(MethodInvocation mi) throws Throwable {
		Object o = mi.proceed();
		String bizName = mi.getMethod().getDeclaringClass().getAnnotation(Biz.class).value();
		
		boolean allEntries = mi.getMethod().getAnnotation(CacheEvict.class).allEntries();
		String  methodName = mi.getMethod().getAnnotation(CacheEvict.class).method();
		String  keyRegx    = mi.getMethod().getAnnotation(CacheEvict.class).key();
		Cache cache = cacheManager.getCache("cache_"+bizName);
		if (allEntries && "".equals(methodName)) {
			logger.info("清空缓存【"+bizName+"】类的所有缓存");
			cache.clear();
			idReflectByArgs.clear();
			return o;
		}
		
		Method[] methods  = mi.getMethod().getDeclaringClass().getMethods();
		Method method     = null;
		for (Method _method_ : methods) {
			if (_method_.isAnnotationPresent(Cacheable.class) && _method_.getName().equals(methodName)) {
				method = _method_;
				break;	// 只处理一个方法，注意重载方法
			}
		}
		if (method != null) {
			String keyName  = method.getAnnotation(Cacheable.class).key();
			if (allEntries) {
				logger.info("清空缓存【"+bizName+"】类的【"+methodName+"】方法的缓存");
				for (Object cacheKey : cache.keySet()) {
					if (cacheKey.toString().startsWith(methodName)) {
						if (keyName == null || "".equals(keyName)) {
							cache.evict(cacheKey);
						} else {
							String _key_ = idReflectByArgs.remove(cacheKey);
							cache.evict(_key_);
						}
					}
				}
			} else {
				Map<String, Object> data = new HashMap<String, Object>();
				int idx = 0;
				for (Object arg : mi.getArguments()) {
					data.put("p"+idx, arg);
					idx++;
				}
				String paraKey = stringFreemarker.process(keyRegx, keyRegx, data);
				logger.debug("清空缓存【"+bizName+"】类的【"+methodName+"】方法且参数为【"+paraKey+"】的缓存");
				if (keyName == null || "".equals(keyName)) {
					cache.evict(methodName+"_"+paraKey);
				} else {
					String _key_ = idReflectByArgs.remove(methodName+"_"+paraKey);
					cache.evict(_key_);
				}
			}
		}
		return o;
	}
	
	private Object getAndUpdatCache(MethodInvocation mi) throws Throwable {
		String bizName = mi.getMethod().getDeclaringClass().getAnnotation(Biz.class).value();
		String keyName = mi.getMethod().getAnnotation(Cacheable.class).key();
		Cache cache = cacheManager.getCache("cache_"+bizName);
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append(mi.getMethod().getName());
		int argIndex = 0;
		for (Object arg : mi.getArguments()) {
			argIndex++;
			if (arg != null && arg.toString().startsWith(arg.getClass().getName()+"@"))
				throw new BizException(mi.getMethod().getDeclaringClass().getName()+"类的"+mi.getMethod().getName()+"方法的第"+argIndex+"参数未覆盖toString()");
			cacheKey.append("_").append(arg);
		}
		Object o = null;
		if (keyName == null || "".equals(keyName)) {
			o = cache.get(cacheKey.toString(), mi.getMethod().getReturnType());
			if (o != null){
				logger.debug("已从方法缓存【cache_"+bizName+"】Args【"+cacheKey+"】获取到对象，直接返回该对象");
				return o;
			}
		} else {
			String _key_ = idReflectByArgs.get(cacheKey);
			if (_key_ != null && !_key_.isEmpty()) {
				o = cache.get(_key_, mi.getMethod().getReturnType());
				if (o != null) {
					logger.debug("已从方法缓存【cache_"+bizName+"】Args【"+cacheKey+"】Key【"+_key_+"】获取到对象，直接返回该对象");
					return o;
				}
			}
		}
		
		synchronized (cache) {
			if (keyName == null || "".equals(keyName)) {
				o = cache.get(cacheKey.toString(), mi.getMethod().getReturnType());
				if (o != null){
					logger.debug("已从方法缓存【cache_"+bizName+"】Args【"+cacheKey+"】获取到对象，直接返回该对象");
					return o;
				}
			} else {
				String _key_ = idReflectByArgs.get(cacheKey);
				if (_key_ != null && !_key_.isEmpty()) {
					o = cache.get(_key_, mi.getMethod().getReturnType());
					if (o != null) {
						logger.debug("已从方法缓存【cache_"+bizName+"】Args【"+cacheKey+"】Key【"+_key_+"】获取到对象，直接返回该对象");
						return o;
					}
				}
			}
			o = mi.proceed();
			if(o == null)
				return null;
			if (keyName == null || "".equals(keyName)) {
				cache.put(cacheKey.toString(), o);
				logger.debug("从目标方法获取对象并缓存到【cache_"+bizName+"】Args【"+cacheKey+"】");
			} else {
				String _key_ = ReflectionUtil.invokeGetterMethod(o,keyName).toString();
				idReflectByArgs.put(cacheKey.toString(), _key_);
				if (cache.get(_key_, mi.getMethod().getReturnType()) == null) {
					cache.put(_key_, o);
					logger.debug("从目标方法获取对象并缓存到【cache_"+bizName+"】Args【"+cacheKey+"】Key【"+_key_+"】");
				}
			}
		}
		
		return o;
	}
}
