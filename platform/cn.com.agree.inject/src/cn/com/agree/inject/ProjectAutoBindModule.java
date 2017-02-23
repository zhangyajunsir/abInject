package cn.com.agree.inject;

import java.lang.reflect.Method;
import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.inject.annotations.AutoBindMapper;
import cn.com.agree.inject.annotations.AutoBindMappers;
import cn.com.agree.inject.lifecycle.AutoBindMapper2AutoBindSingleton;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.netflix.governator.annotations.AutoBindSingleton;
import com.netflix.governator.guice.InternalAutoBindModule;
import com.netflix.governator.guice.ProviderBinderUtil;
import com.netflix.governator.lifecycle.ClasspathScanner;

public class ProjectAutoBindModule extends InternalAutoBindModule {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectAutoBindModule.class);

	public ProjectAutoBindModule(Injector injector, ClasspathScanner classpathScanner, Collection<Class<?>> ignoreClasses) {
		super(injector, classpathScanner, ignoreClasses);
	}
	
	protected void configure() {
		// 只支持类上面的AutoBindMappers或AutoBindMapper注解
		bindAutoBindMapperSingletons();
	}
	
	private void bindAutoBindMapperSingletons() {
        for ( Class<?> clazz : classpathScanner.getClasses() ) {
            if ( ignoreClasses.contains(clazz) || (!clazz.isAnnotationPresent(AutoBindMapper.class)  && !clazz.isAnnotationPresent(AutoBindMappers.class)) ) {
                continue;
            }
            
            if (clazz.isAnnotationPresent(AutoBindMappers.class)) {
            	AutoBindMappers mappers = clazz.getAnnotation(AutoBindMappers.class);
            	for (AutoBindMapper mapper : mappers.value()) {
            		AutoBindSingleton autoBindSingleton = new AutoBindMapper2AutoBindSingleton(
            				mapper.value(), mapper.baseClass(), mapper.multiple(), mapper.eager()
            				);
            		bindAutoBindSingleton(autoBindSingleton, clazz);
            	}
            } else {
            	AutoBindMapper mapper = clazz.getAnnotation(AutoBindMapper.class);
            	AutoBindSingleton autoBindSingleton = new AutoBindMapper2AutoBindSingleton(
        				mapper.value(), mapper.baseClass(), mapper.multiple(), mapper.eager()
        				);
        		bindAutoBindSingleton(autoBindSingleton, clazz);
            }
        }
    }
	
	protected void bindAutoBindSingleton(AutoBindSingleton annotation, Class<?> clazz) {
		if ( javax.inject.Provider.class.isAssignableFrom(clazz) ) {	// 支持应用工程中Provider子类
            Preconditions.checkState(annotation.value() == AutoBindSingleton.class, "@AutoBindMapper value cannot be set for Providers");
            Preconditions.checkState(annotation.baseClass() == AutoBindSingleton.class, "@AutoBindMapper value cannot be set for Providers");
            Preconditions.checkState(!annotation.multiple(), "@AutoBindMapper(multiple=true) value cannot be set for Providers");
            LOG.info("Installing @AutoBindMapper " + clazz.getName());
            ProviderBinderUtil.bind(binder(), (Class<javax.inject.Provider>)clazz, Scopes.SINGLETON);
        } else if ( MethodInterceptor.class.isAssignableFrom(clazz) ) {
        	try {
	        	Object interceptor = clazz.newInstance();
	        	requestInjection(interceptor);	// 支持对拦截器注入依赖
	        	Matcher<? super Class<?>> classMatcher = Matchers.any();
	        	Matcher<? super Method> methodMatcher  = Matchers.any(); 
	        	if (AopMatcher.class.isAssignableFrom(clazz)) {
	        		AopMatcher aopMatcher = (AopMatcher)interceptor;
	        		if (aopMatcher.getClassMatcher()  != null) classMatcher  = aopMatcher.getClassMatcher();
	        		if (aopMatcher.getMethodMatcher() != null) methodMatcher = aopMatcher.getMethodMatcher();
	        	}
				binder().bindInterceptor(classMatcher, methodMatcher, new MethodInterceptor[]{(MethodInterceptor)interceptor});
			} catch (Exception e) {
				LOG.error("设置拦截器["+clazz.getName()+"]失败", e);
			}
        } else if ( Module.class.isAssignableFrom(clazz) ) {
            // Modules are handled by {@link InteranlAutoBindModuleBootstrapModule}
            return;
        } else {
            super.bindAutoBindSingleton(annotation, clazz);
        }
	}

	
}
