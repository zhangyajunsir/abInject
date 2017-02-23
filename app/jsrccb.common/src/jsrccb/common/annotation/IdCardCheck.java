package jsrccb.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**在域后事件执行之前
 * 身份证校验和核查 
 * 入参：IdType 证件类型取值域，值为A才进行校验；
 * 入/出参：IdNo   证件号取值域；
 * 示例：“combo_证件类型”，“text_证件号码”，“#客户编号”
 * @author wj
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)
public @interface IdCardCheck {
	//证件类型
	String  idType();
	String  idNo();
	
}
