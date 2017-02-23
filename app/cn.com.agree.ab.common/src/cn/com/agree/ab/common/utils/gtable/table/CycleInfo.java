 /*
 * Copyright(C) 2008 Agree Tech, All rights reserved.
 * 
 * Created on 2008-6-26   by Xu Haibo
 */

package cn.com.agree.ab.common.utils.gtable.table;

 /**
 * <DL><DT><B>
 * 循环报文体的信息
 * </B></DT><p><DD>
 * 该信息提供循环体的数量，是拆包的依据
 * </DD></DL><p>
 * 
 * <DL><DT><B>使用范例</B></DT><p><DD>
 * 使用范例说明
 * </DD></DL><p>
 * 
 * @author 徐海波
 * @author 赞同科技
 * @version $Revision: 1.1.2.1 $date 
 */
public class CycleInfo {
	
	/**
	 * TYPE_UNLIMITED 对循环体报文的数量不限制，一直拆到报文结束
	 */
	public static final String TYPE_UNLIMITED = "type_unlimited";
	
	
	/**
	 * TYPE_LINE_NO 指定字段序号，该字段的值是循环拆包的数量
	 */
	public static final String TYPE_LINE_SEQ = "type_line_no";
	/**
	 * TYPE_CONSTANT o表直接定义拆包数量
	 */
	public static final String TYPE_CONSTANT= "type_constant";
	/**
	 * TYPE_VARIANT 用交易中的storeData指定循环数量
	 */
	public static final String TYPE_VARIANT= "type_variant";

	/**
	 * type 信息类型
	 */
	private String type;
	
	/**
	 * varExpression 变量值
	 */
	private String varExpression;
	
	/**
	 * constant 指定的数量
	 */
	private int constant;
	
	/**
	 * seq 字段序号
	 */
	private int seq;

	/**
	 * <DL><DT><B>
	 * 构造器.
	 * </B></DT><p><DD>
	 * 构造器说明
	 * </DD></DL><p>
	 * @param type
	 * @param varExpression
	 * @param constant
	 * @param seq
	 */
	public CycleInfo(String type, String varExpression, int constant, int seq) {
		super();
		this.type = type;
		this.varExpression = varExpression;
		this.constant = constant;
		this.seq = seq;
	}


	/**
	 * <DL><DT><B>
	 * 返回字段constant的值.
	 * </B></DT><p><DD>
	 * 返回字段constant的值
	 * </DD></DL><p>
	 * @return 返回字段constant的值.
	 */
	public int getConstant() {
		return constant;
	}

	/**
	 * <DL><DT><B>
	 * 返回字段seq的值.
	 * </B></DT><p><DD>
	 * 返回字段seq的值
	 * </DD></DL><p>
	 * @return 返回字段seq的值.
	 */
	public int getSeq() {
		return seq;
	}

	/**
	 * <DL><DT><B>
	 * 返回字段type的值.
	 * </B></DT><p><DD>
	 * 返回字段type的值
	 * </DD></DL><p>
	 * @return 返回字段type的值.
	 */
	public String getType() {
		return type;
	}

	/**
	 * <DL><DT><B>
	 * 返回字段varExpression的值.
	 * </B></DT><p><DD>
	 * 返回字段varExpression的值
	 * </DD></DL><p>
	 * @return 返回字段varExpression的值.
	 */
	public String getVarExpression() {
		return varExpression;
	}


	
}
