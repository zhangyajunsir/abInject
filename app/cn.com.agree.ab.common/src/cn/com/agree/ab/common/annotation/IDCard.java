package cn.com.agree.ab.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 身份证注解
 * @author zhangyajun
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface IDCard {
	// 是否必输
	boolean must()  default false;
	// 组件名:栏位名
	String[] result() default {};
}  
