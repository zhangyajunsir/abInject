/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.governator.guice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.ScopeAnnotation;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.MoreTypes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.netflix.governator.annotations.AutoBind;
import com.netflix.governator.annotations.AutoBindSingleton;
import com.netflix.governator.guice.lazy.LazySingletonScope;
import com.netflix.governator.lifecycle.ClasspathScanner;
//modify by zyj package private -> public
public class InternalAutoBindModule extends AbstractModule
{
    private static final Logger LOG = LoggerFactory.getLogger(InternalAutoBindModule.class);
    
    protected final Set<Class<?>> ignoreClasses;
    protected final Injector injector;
    protected final ClasspathScanner classpathScanner;
	//modify by zyj package private -> public
    public InternalAutoBindModule(Injector injector, ClasspathScanner classpathScanner, Collection<Class<?>> ignoreClasses)
    {
        this.injector = injector;
        this.classpathScanner = classpathScanner;
        Preconditions.checkNotNull(ignoreClasses, "ignoreClasses cannot be null");

        this.ignoreClasses = ImmutableSet.copyOf(ignoreClasses);
    }

    @Override
    protected void configure()
    {
        bindAutoBindSingletons();
        bindAutoBindConstructors();
        bindAutoBindMethods();
        bindAutoBindFields();
    }

    private void bindAutoBindFields()
    {
        for ( Field field : classpathScanner.getFields() )
        {
            if ( ignoreClasses.contains(field.getDeclaringClass()) )
            {
                continue;
            }

            bindAnnotations(field.getDeclaredAnnotations());
        }
    }

    private void bindAutoBindMethods()
    {
        for ( Method method : classpathScanner.getMethods() )
        {
            if ( ignoreClasses.contains(method.getDeclaringClass()) )
            {
                continue;
            }

            bindParameterAnnotations(method.getParameterAnnotations());
        }
    }

    private void bindAutoBindConstructors()
    {
        for ( Constructor constructor : classpathScanner.getConstructors() )
        {
            if ( ignoreClasses.contains(constructor.getDeclaringClass()) )
            {
                continue;
            }

            bindParameterAnnotations(constructor.getParameterAnnotations());
        }
    }

    private void bindParameterAnnotations(Annotation[][] parameterAnnotations)
    {
        for ( Annotation[] annotations : parameterAnnotations )
        {
            bindAnnotations(annotations);
        }
    }

    private void bindAnnotations(Annotation[] annotations)
    {
        for ( Annotation annotation : annotations )
        {
            AutoBindProvider autoBindProvider = getAutoBindProvider(annotation);
            if ( autoBindProvider != null )
            {
                //noinspection unchecked
                autoBindProvider.configure(binder(), annotation);
            }
        }
    }

    private AutoBindProvider getAutoBindProvider(Annotation annotation)
    {
        AutoBindProvider autoBindProvider = null;
        if ( annotation.annotationType().isAnnotationPresent(AutoBind.class) )
        {
            ParameterizedType parameterizedType = Types.newParameterizedType(AutoBindProvider.class, annotation.annotationType());
            autoBindProvider = (AutoBindProvider<?>)injector.getInstance(Key.get(TypeLiteral.get(parameterizedType)));
        }
        else if ( annotation.annotationType().isAssignableFrom(AutoBind.class) )
        {
            autoBindProvider = injector.getInstance(Key.get(new TypeLiteral<AutoBindProvider<AutoBind>>()
            {
            }));
        }
        return autoBindProvider;
    }

    @SuppressWarnings("unchecked")
    private void bindAutoBindSingletons()
    {
        for ( Class<?> clazz : classpathScanner.getClasses() )
        {
            if ( ignoreClasses.contains(clazz) || !clazz.isAnnotationPresent(AutoBindSingleton.class) )
            {
                continue;
            }

            AutoBindSingleton annotation = clazz.getAnnotation(AutoBindSingleton.class);
            if ( javax.inject.Provider.class.isAssignableFrom(clazz) )
            {
                Preconditions.checkState(annotation.value() == AutoBindSingleton.class, "@AutoBindSingleton value cannot be set for Providers");
                Preconditions.checkState(annotation.baseClass() == AutoBindSingleton.class, "@AutoBindSingleton value cannot be set for Providers");
                Preconditions.checkState(!annotation.multiple(), "@AutoBindSingleton(multiple=true) value cannot be set for Providers");

                LOG.info("Installing @AutoBindSingleton " + clazz.getName());
                ProviderBinderUtil.bind(binder(), (Class<javax.inject.Provider>)clazz, Scopes.SINGLETON);
            }
            else if ( Module.class.isAssignableFrom(clazz) )
            {
                // Modules are handled by {@link InteranlAutoBindModuleBootstrapModule}
                continue;
            }
            else
            {
                bindAutoBindSingleton(annotation, clazz);
            }
        }
    }
	//modify by zyj private -> protected
    protected void bindAutoBindSingleton(AutoBindSingleton annotation, Class<?> clazz)
    {
        LOG.info("Installing @AutoBindSingleton " + clazz.getName());
        
        Class<?> annotationBaseClass = getAnnotationBaseClass(annotation);
        if ( annotationBaseClass != AutoBindSingleton.class )    // AutoBindSingleton.class is used as a marker to mean "default" because annotation defaults cannot be null
        {
            Object foundBindingClass = searchForBaseClass(clazz, annotationBaseClass, Sets.newHashSet());
            if ( foundBindingClass == null ) {
                throw new IllegalArgumentException(String.format("AutoBindSingleton class %s does not implement or extend %s", clazz.getName(), annotationBaseClass.getName()));
            }

            if ( foundBindingClass instanceof Class ) {
                if ( annotation.multiple() ) {
                	//注入成集合
                    Multibinder<?> multibinder = Multibinder.newSetBinder(binder(), (Class)foundBindingClass);
                    //noinspection unchecked
                    multibinder.addBinding().to((Class)clazz).in(Scopes.SINGLETON);
                }/* else {*/
                	//add by zyj 添加Named方式
                	Annotation named = null;
                    if (named == null && clazz.isAnnotationPresent(Named.class)) {
                    	named = clazz.getAnnotation(Named.class);
                    } else 
                    if (named == null && clazz.isAnnotationPresent(javax.inject.Named.class)) {
                    	named = clazz.getAnnotation(javax.inject.Named.class);
                    } else
                    if (named == null) {
                    	// 迭代当前类上的所有注解(不包括父类)
                    	Annotation[] anns = clazz.getDeclaredAnnotations();
                    	for ( Annotation ann : anns ) {
                    		Class<? extends Annotation> annType = ann.annotationType();
                    		if (annType.isAnnotationPresent(BindingAnnotation.class)) {
                    			String value = null;
								try {
									value = (String)(annType.getDeclaredMethod("value").invoke(ann));
								} catch (Exception e) {
									LOG.error("注解"+annType.getName()+"不包含value()", e);
								}
								if (value != null) {
	                    			named = Names.named(value);
	                    			break;
								}
                    		}
                    	}
                    }
                    if (named != null) {
                    	//noinspection unchecked
                        applyScope(binder().withSource(getCurrentStackElement()).bind((Class)foundBindingClass).annotatedWith(named).to(clazz), clazz, annotation);
                    } else {
                    	//noinspection unchecked
                        applyScope(binder().withSource(getCurrentStackElement()).bind((Class)foundBindingClass).to(clazz), clazz, annotation);
                    }
                /*}*/
            } else if ( foundBindingClass instanceof Type ) {
                TypeLiteral typeLiteral = TypeLiteral.get((Type)foundBindingClass);
                if ( annotation.multiple() ) {
                	//注入成集合
                    Multibinder<?> multibinder = Multibinder.newSetBinder(binder(), typeLiteral);
                    //noinspection unchecked
                    multibinder.addBinding().to((Class)clazz).in(Scopes.SINGLETON);
                }/* else {*/
                	//add by zyj 添加Named方式
	            	Annotation named = null;
	                if (named == null && clazz.isAnnotationPresent(Named.class)) {
	                	named = clazz.getAnnotation(Named.class);
	                } else 
	                if (named == null && clazz.isAnnotationPresent(javax.inject.Named.class)) {
	                	named = clazz.getAnnotation(javax.inject.Named.class);
	                } else
	                if (named == null) {
	                	// 迭代当前类上的所有注解(不包括父类)
                    	Annotation[] anns = clazz.getDeclaredAnnotations();
                    	for ( Annotation ann : anns ) {
                    		Class<? extends Annotation> annType = ann.annotationType();
                    		if (annType.isAnnotationPresent(BindingAnnotation.class)) {
                    			String value = null;
								try {
									value = (String)(annType.getDeclaredMethod("value").invoke(ann));
								} catch (Exception e) {
									LOG.error("注解"+annType.getName()+"不包含value()", e);
								}
								if (value != null) {
	                    			named = Names.named(value);
	                    			break;
								}
                    		}
                    	}
                    }
	                if (named != null) {
                    	//noinspection unchecked
	                	applyScope(binder().withSource(getCurrentStackElement()).bind(typeLiteral).annotatedWith(named).to(clazz), clazz, annotation);
                    } else {
                    	//noinspection unchecked
                    	applyScope(binder().withSource(getCurrentStackElement()).bind(typeLiteral).to(clazz), clazz, annotation);
                    }
                /*}*/
            } else {
                throw new RuntimeException("Unexpected binding class: " + foundBindingClass);
            }
        }
        else
        {
            Preconditions.checkState(!annotation.multiple(), "@AutoBindSingleton(multiple=true) must have either value or baseClass set");
            applyScope(binder().withSource(getCurrentStackElement()).bind(clazz), clazz, annotation);
        }
    }
    
    private StackTraceElement getCurrentStackElement() {
        return Thread.currentThread().getStackTrace()[1];
    }

    private void applyScope(ScopedBindingBuilder builder, Class<?> clazz, AutoBindSingleton annotation) {
        if (hasScopeAnnotation(clazz)) {
            // 优先使用类上的范围注解，注解自动被Guice框架所执行：
        	// 1.@Singleton单例模式，但对于是否立即在注入到Guice容器中时就被创建出来取决于启动模式：
        	//	TOOL描述的是带有IDE等插件的运行模式；
        	//	DEVELOPMENT是指在开发阶段只加载自己需要的功能（对于非立即初始化单例对象采用延后加载），这样来降低加载不需要功能的时间；
        	//	PRODUCTION模式是指完全加载所有功能（对于单例对象采用立即加载方式）。
        	// 2.@LazySingleton懒单例模式（自定义），单例对象注入到Guice容器中时暂不创建出来，第一次使用时创建。
        	// 3.@FineGrainedLazySingleton（自定义），
        }
        else if (annotation.eager())
    	{	// Guice也允许对象在注入到Guice容器中时就被创建出来(显然这是针对单例模式才有效)。
    		builder.asEagerSingleton();
    	}
    	else 
    	{	// 非单例对象，每次使用时都创建新的实例。
    	    builder.in(Scopes.NO_SCOPE);
    	}
    }
    
    private boolean hasScopeAnnotation(Class<?> clazz) {
    	Annotation scopeAnnotation = null;
    	for (Annotation annot : clazz.getAnnotations()) {
    		if (annot.annotationType().isAnnotationPresent(ScopeAnnotation.class) 
    				|| annot.annotationType().isAnnotationPresent(javax.inject.Scope.class)) {
    			Preconditions.checkState(scopeAnnotation == null, "Multiple scopes not allowed");
    			scopeAnnotation = annot;
    		}
    	}
    	return scopeAnnotation != null;
    }
    
    private Class<?> getAnnotationBaseClass(AutoBindSingleton annotation)
    {
        Class<?> annotationValue = annotation.value();
        Class<?> annotationBaseClass = annotation.baseClass();
        Preconditions.checkState((annotationValue == AutoBindSingleton.class) || (annotationBaseClass == AutoBindSingleton.class), "@AutoBindSingleton cannot have both value and baseClass set");

        return (annotationBaseClass != AutoBindSingleton.class) ? annotationBaseClass : annotationValue;
    }

    private Object searchForBaseClass(Class<?> clazz, Class<?> annotationBaseClass, Set<Object> usedSet)
    {
        if ( clazz == null )
        {
            return null;
        }

        if ( clazz.equals(annotationBaseClass) )
        {
            return clazz;
        }

        if ( !usedSet.add(clazz) )
        {
            return null;
        }

        for ( Type type : clazz.getGenericInterfaces() )
        {
            if ( MoreTypes.getRawType(type).equals(annotationBaseClass) )
            {
                return type;
            }
        }

        if ( clazz.getGenericSuperclass() != null )
        {
            if ( MoreTypes.getRawType(clazz.getGenericSuperclass()).equals(annotationBaseClass) )
            {
                return clazz.getGenericSuperclass();
            }
        }

        for ( Class<?> interfaceClass : clazz.getInterfaces() )
        {
            Object foundBindingClass = searchForBaseClass(interfaceClass, annotationBaseClass, usedSet);
            if ( foundBindingClass != null )
            {
                return foundBindingClass;
            }
        }

        return searchForBaseClass(clazz.getSuperclass(), annotationBaseClass, usedSet);
    }
}
