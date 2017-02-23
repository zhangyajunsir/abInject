package cn.com.agree.ab.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注方法使用异步执行
 * @author zhangyajun
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME) 
public @interface Async {

}
