package cn.com.agree.ab.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 密码注解
 * @author zhangyajun
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface Pin {
	// 账号栏位
	String  account();
	// 是否必输
	boolean must()  default false;
	// 输入次数
	int     count() default 1;
	// 是否校验简单密码
	boolean check() default false;
	// 是否跳过密码栏位
	boolean skip()  default false;
}  
