package cn.com.agree.ab.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 卡注解
 * @author zhangyajun
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface Card {
	// 是否必输
	boolean must()  default false;
	// 0-不校验，1-IC卡校验1，2-IC卡校验2
	int     check() default 0;
	// 组件名:栏位名
	String[] result() default {};
}  
