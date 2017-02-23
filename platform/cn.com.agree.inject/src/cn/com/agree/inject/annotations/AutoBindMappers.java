package cn.com.agree.inject.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于同一个类上添加多个AutoBindMapper
 * @author zhangyajun
 *
 */
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface AutoBindMappers {
	AutoBindMapper[] value();
}
