package jsrccb.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**加在事件之前
 * 证件号不为空时，进行客户姓名查询  通讯 EC0390001
 * 入参：IdType 证件类型取值域；
 * 入参：IdNo   证件号取值域；
 * 出参：name   客户名取值域；
 * 出参：sysNo  客户编号取值域；
 * 参数为取值组件的ID或"#"加StoreData的key，
 * 示例：“combo_证件类型”，“text_姓名”，“#客户编号”
 * @author wj
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomerName {
	//证件类型
	String  idType();
	String  idNo();
	String  name();
	String  sysNo();
	
}
