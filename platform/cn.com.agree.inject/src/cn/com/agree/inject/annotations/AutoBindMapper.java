package cn.com.agree.inject.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 参照AutoBindSingleton实现
 */
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface AutoBindMapper
{
    /**
     * 设置父类或接口
     * @return base class/interface to bind to
     */
    Class<?>  value() default AutoBindMapper.class;

    /**
     * 等同value()
     * @return base class/interface to bind to
     */
    Class<?>  baseClass() default AutoBindMapper.class;

    /**
     * 如果为true，代表相同baseClass存在多个子类，将形成baseClass集合，集合元素为子类实例。
     * @return true/false
     */
    boolean multiple() default false;
    
    /**
     * 如果为true，允许对象在注入到Guice容器中时就被创建出来(显然这是针对单例模式才有效)。
     * @return
     */
    boolean eager() default true;
}
