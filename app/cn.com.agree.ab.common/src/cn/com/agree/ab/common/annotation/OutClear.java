package cn.com.agree.ab.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.thoughtworks.xstream.security.InterfaceTypePermission;

@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface OutClear {
	// 是否确认
	boolean confirm() default false;
	
}
