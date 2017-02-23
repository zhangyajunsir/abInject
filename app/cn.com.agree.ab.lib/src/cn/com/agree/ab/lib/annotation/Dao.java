package cn.com.agree.ab.lib.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * 持久Bean注解<br>
 * 用于持久实现类
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BindingAnnotation
public @interface Dao {

    /**
     * 持久Bean名称
     */
    String value();
    
}
