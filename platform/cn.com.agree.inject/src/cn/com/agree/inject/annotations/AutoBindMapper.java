package cn.com.agree.inject.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * ����AutoBindSingletonʵ��
 */
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface AutoBindMapper
{
    /**
     * ���ø����ӿ�
     * @return base class/interface to bind to
     */
    Class<?>  value() default AutoBindMapper.class;

    /**
     * ��ͬvalue()
     * @return base class/interface to bind to
     */
    Class<?>  baseClass() default AutoBindMapper.class;

    /**
     * ���Ϊtrue��������ͬbaseClass���ڶ�����࣬���γ�baseClass���ϣ�����Ԫ��Ϊ����ʵ����
     * @return true/false
     */
    boolean multiple() default false;
    
    /**
     * ���Ϊtrue�����������ע�뵽Guice������ʱ�ͱ���������(��Ȼ������Ե���ģʽ����Ч)��
     * @return
     */
    boolean eager() default true;
}
