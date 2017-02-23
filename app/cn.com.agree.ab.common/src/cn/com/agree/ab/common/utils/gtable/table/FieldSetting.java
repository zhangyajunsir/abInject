/*
 * Copyright(C) 2006 Agree Tech, All rights reserved.
 * 
 * Created on 2006-6-26   by Xu Haibo
 */

package cn.com.agree.ab.common.utils.gtable.table;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ognl.Ognl;

import cn.com.agree.ab.trade.core.Trade;

/**
 * <DL>
 * <DT><B> 对字段的设置 </B></DT>
 * <p>
 * <DD> 定义特殊的类型“t“,表示循环字段,该字段的seq等同与其第一个field的seq </DD>
 * </DL>
 * <p>
 * 
 * <DL>
 * <DT><B>使用范例</B></DT>
 * <p>
 * <DD> 使用范例说明 </DD>
 * </DL>
 * <p>
 * 
 * @author 徐海波
 * @author 赞同科技
 * @version $Revision: 1.4.2.4 $ $Date: 2008/12/28 13:36:04 $
 */
@SuppressWarnings("rawtypes")
public class FieldSetting implements Comparable {
	private static final String ENCODING = "GBK";

	/**
	 * seq 序号
	 */
	private int seq;

	/**
	 * length 长度
	 */
	private int length;

	/**
	 * type 类型
	 */
	private String type;

	/**
	 * defaultName 默认名称（实际上是赋值脚本）
	 */
	private String defaultScript;
	
	/**
	 * var 变量
	 */
	private String var;
	
	/**
	 * cycleTable 循环字段
	 */
	private Table cycleTable;
	

	public String getDefaultScript() {
		return defaultScript;
	}

	public void setDefaultScript(String defaultScript) {
		this.defaultScript = defaultScript;
	}

	/**
	 * <DL>
	 * <DT><B> 返回字段length的值. </B></DT>
	 * <p>
	 * <DD> 返回字段length的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @return 返回字段length的值.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * <DL>
	 * <DT><B> 返回拆包时的数据长度 </B></DT>
	 * <p>
	 * <DD> C/M/S类型的返回数据比o表中写明的长度多1字节 ,t类型返回该循环报文总长度（如果无穷则返回-1）</DD>
	 * </DL>
	 * <p>
	 * @param trade 需要拆包的交易对象
	 * @param currentTable 因为循环拆包的次数可能由报文决定，所以字段需要知道自己位于整个拆包过程的控制信息，也就是Table的对象
	 * @param currentResult 因为循环拆包的次数可能由报文决定，所以字段需要知道已经拆包得到的内容，以便返回合适的循环次数
	 * @throws Exception 
	 */
	public int getUnpackLength(Object trade, Table currentTable, List currentResult) throws Exception {
		if ("c".equalsIgnoreCase(type) || "m".equalsIgnoreCase(type) || "s".equalsIgnoreCase(type)) {
			return Math.abs(length);//by ww
		}
		
		if( "t".equalsIgnoreCase(type)){
			int cycleCount = getCycleCount(trade, cycleTable.getCycleInfo(), currentTable, currentResult);
			if(cycleCount==-1){
				return -1;
			}else {
				
				return cycleTable.getFlatFieldsLength(trade, currentTable, currentResult) * cycleCount;
			}
		}
		
		return Math.abs(length);
	}
	
	/**
	 * <DL><DT><B>
	 * 返回循环报文的循环次数
	 * </B></DT><p><DD>
	 * 如果不是循环报文，返回1
	 * 如果是不限制的，返回-1
	 * </DD></DL><p>
	 * @param trade 交易实例
	 * @param cycleInfo 循环块的信息
	 * @param currentTable 因为循环拆包的次数可能由报文决定，所以字段需要知道自己位于整个拆包过程的控制信息，也就是Table的对象
	 * @param currentResult 因为循环拆包的次数可能由报文决定，所以字段需要知道已经拆包得到的内容，以便返回合适的循环次数
	 * @return 循环块的次数
	 * @throws Exception 
	 */
	public int getCycleCount(Object trade, CycleInfo cycleInfo, Table currentTable, List currentResult) throws Exception{
		// 非循环块定义
		if(cycleInfo==null){
			return 1;
		}
		
		// 根据实际情况计算循环次数
		if(CycleInfo.TYPE_CONSTANT.equals(cycleInfo.getType())){
			return cycleInfo.getConstant();
		}else if (CycleInfo.TYPE_UNLIMITED.equals(cycleInfo.getType())){
			return -1;
		}else if (CycleInfo.TYPE_LINE_SEQ.equals(cycleInfo.getType())){
			// 从报文字段中获得
			int seqInTable = 0;
			boolean found = false;
			for (Iterator iter = currentTable.getFieldSettings().iterator(); iter.hasNext();) {
				FieldSetting setting = (FieldSetting) iter.next();
				if(cycleInfo.getSeq()==setting.seq){
					found =true;
					break;
				}
				seqInTable ++;
			}
			if(!found){
				throw new Exception("无法找到定义的循环字段，请检查循环定义！");
			}
			String value = (String) currentResult.get(seqInTable);
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new Exception("返回报文提供的循环次数有误，根据O表拆得的字符串信息是:[" + value + "]");
			}
			
		}else if (CycleInfo.TYPE_VARIANT.equals(cycleInfo.getType())){
			// 从交易的storeData中获得
			String countStr = "";
			if (trade instanceof Trade)
				countStr = ((Trade)trade).getStoreData(cycleInfo.getVarExpression());
			else
				countStr = (String)Ognl.getValue(cycleInfo.getVarExpression(), trade);
			try {
				return Integer.parseInt(countStr);
			} catch (NumberFormatException e) {
				throw new Exception("交易中没有提供循环体的循环次数。循环次数关键字：[" + cycleInfo.getVarExpression() +"]" );
			}
			
		}
		 
		// default return 1
		return 1;
	}

	/**
	 * <DL>
	 * <DT><B> 设置字段length的值. </B></DT>
	 * <p>
	 * <DD> 设置字段length的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param length
	 *            用以设置字段length的值.
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * <DL>
	 * <DT><B> 返回字段seq的值. </B></DT>
	 * <p>
	 * <DD> 返回字段seq的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @return 返回字段seq的值.
	 */
	public int getSeq() {
		if(type.equalsIgnoreCase("t")){
			return cycleTable.getSeq();
		}
		return seq;
	}

	/**
	 * <DL>
	 * <DT><B> 设置字段seq的值. </B></DT>
	 * <p>
	 * <DD> 设置字段seq的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param seq
	 *            用以设置字段seq的值.
	 */
	public void setSeq(int seq) {
		this.seq = seq;
	}

	/**
	 * <DL>
	 * <DT><B> 返回字段type的值. </B></DT>
	 * <p>
	 * <DD> 返回字段type的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @return 返回字段type的值.
	 */
	public String getType() {
		return type;
	}

	/**
	 * <DL>
	 * <DT><B> 设置字段type的值. </B></DT>
	 * <p>
	 * <DD> 设置字段type的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param type
	 *            用以设置字段type的值.
	 */
	public void setType(String type) {
		this.type = type;
	}

	public int compareTo(Object arg) {
		if (arg instanceof FieldSetting) {
			FieldSetting setting = (FieldSetting) arg;

			return (seq < setting.getSeq()) ? -1 : (seq > setting.getSeq() ? 1 : 0);

		} else {
			return 0;
		}
	}

	/**
	 * <DL>
	 * <DT><B> 根据不同的类型进行拆包解码 </B></DT>
	 * <p>
	 * <DD> 详细介绍 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param buf
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String[] unpack(byte[] buf, Object trade, Table currentTable, List currentResult) throws Exception {
		// 处理循环字段
		if ("t".equalsIgnoreCase(type) ){
			
			int cycleLength = cycleTable.getFlatFieldsLength(trade, currentTable, currentResult); 
			int cycleCount = getCycleCount(trade, cycleTable.getCycleInfo(), currentTable, currentResult);
			// 全部是循环块的情况
			if(cycleCount== -1){
				cycleCount = buf.length/cycleLength;
			}
			byte[] buffer = new byte[cycleLength];
			List cycleResult = new ArrayList();
			for (int i = 0; i < cycleCount; i++) {
				System.arraycopy(buf, i*cycleLength, buffer, 0, cycleLength);
				String[] partResult = cycleTable.unpack(trade, buffer, true);
				cycleResult.addAll(Arrays.asList(partResult));
			}
			
			return (String[]) cycleResult.toArray(new String[0]);
			
		}
		
		
		// 处理普通字段
		String value = "";
		if ("c".equalsIgnoreCase(type) || "m".equalsIgnoreCase(type) || "s".equalsIgnoreCase(type)) {
			// 这些类型的字段会多1个字节
//			byte[] content = new byte[buf.length - 1];
//			System.arraycopy(buf, 0, content, 0, content.length);
//by ww
			byte [] bb=new byte[buf.length];
			int start=0;
			for (int i = 0; i < buf.length; i++) {
				if (0x03==buf[i])
					continue;
				bb[start]=buf[i];
				start+=1;
			}
			byte [] buf1=new byte[start];
			System.arraycopy(bb, 0, buf1, 0, start);		
			try {
				value = new String(buf1, ENCODING);
			} catch (UnsupportedEncodingException e) {
			}
//by ww
		} else {
			try {
				value = new String(buf, ENCODING);
			} catch (UnsupportedEncodingException e) {
			}
		}

		// translate
		if ("s".equalsIgnoreCase(type)) {
			value = transS(value);
		} else if ("m".equalsIgnoreCase(type)) {
			value = transM(value);
		} else if ("c".equalsIgnoreCase(type)) {
			value = transC(value);
		} else if ("x".equalsIgnoreCase(type)) {
			value = transX(value);
		} else if ("9".equals(type)) {
			value = trans9(value);
		} else {
			value = transX(value);
		}
		return new String[] {value};
	}

	private String transX(String value) {
		int index = value.length();
		while (index > 0 && value.charAt(index-1) == ' ') {
			index--;
		}
		return value.substring(0, index);
	}

	private String transC(String value) {
		return value.trim();//by ww
	}

	private String transM(String value) {
		return value.trim();
	}

	private String trans9(String value) {
		// 1. remove space at left and right side
		value = value.trim();
		// 1.5 if "" return "0"
		if (value.length() == 0) {
			return "0";
		}

		// 2. +,- char
		boolean positive = true;
		if (value.charAt(0) == '-') {
			positive = false;
			value = value.substring(1);
		} else if (value.charAt(0) == '+') {
			value = value.substring(1);
		}

		// 3. trim 0 and translate
		char flag = value.charAt(value.length() - 1);// 统一用大写
		char endChar = '0';
		if (flag == '}' || ('J' <= flag && 'R' >= flag)) {
			positive = false;
			if ('J' <= flag && 'R' >= flag) {
				endChar = (char) ('1' + flag - 'J');
			}
		} else if (flag == '{' || ('A' <= flag && 'I' >= flag)) {
			if ('A' <= flag && 'I' >= flag) {
				endChar = (char) ('1' + flag - 'A');
			}
		} else if ('p' <= flag && 'y' >= flag) {
			positive = false;
			endChar = (char) (flag - 0x40);
		} else if ('0' <= flag && '9' >= flag) {
			endChar = flag;
		}

		String valueWithoutEnd = value.substring(0, value.length() - 1);
		// 将所有非数字字符转换为0
		value.replaceAll("\\D", "0");

		// 获得最后一个字符前面的数字
		try {
			valueWithoutEnd=valueWithoutEnd.replaceAll("[.]", "");
			valueWithoutEnd = String.valueOf(Long.parseLong(valueWithoutEnd));
		} catch (NumberFormatException e) {
//			valueWithoutEnd = "";
		}
		if("0".equals(valueWithoutEnd)){
			valueWithoutEnd = "";
		}

		// 拼接字符串
		if (positive) {
			return valueWithoutEnd + endChar;
		} else {
			return "-" + valueWithoutEnd + endChar;
		}
	}

	/**
	 * <DL>
	 * <DT><B> s型的转码 </B></DT>
	 * <p>
	 * <DD> 规则: 1. 有符号数转码： { 0 A 1 B 2 C 3 D 4 E 5 F 6 G 7 H 8 I 9 J -1 K -2 L
	 * -3 M -4 N -5 O -6 P -7 Q -8 R -9 } -0 <br>
	 * 2. 无符号数不处理
	 * 
	 * </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param value
	 */
	private String transS(String value) {
		// 1. remove space at left and right side
		value = value.trim();
		// 1.5 if "" return "0"
		if (value.length() == 0) {
			return "0";
		}

		boolean positive = true;

		// 2. trim 0 and translate
		char flag = value.charAt(value.length() - 1);
		char endChar = '0';
		if (flag == '}' || ('J' <= flag && 'R' >= flag)) {
			positive = false;
			if ('J' <= flag && 'R' >= flag) {
				endChar = (char) ('1' + flag - 'J');
			}
		} else if (flag == '{' || ('A' <= flag && 'I' >= flag)) {
			if ('A' <= flag && 'I' >= flag) {
				endChar = (char) ('1' + flag - 'A');
			}
		} else if ('p' <= flag && 'y' >= flag) {
			positive = false;
			endChar = (char) (flag - 0x40);
		} else if ('0' <= flag && '9' >= flag) {
			endChar = flag;
		}

		String valueWithoutEnd = value.substring(0, value.length() - 1);
		// 将所有非数字字符转换为0
		value.replaceAll("\\D", "0");

		// 获得最后一个字符前面的数字
		try {
			valueWithoutEnd = String.valueOf(Long.parseLong(valueWithoutEnd));
		} catch (NumberFormatException e) {
			valueWithoutEnd = "";
		}
		if("0".equals(valueWithoutEnd)){
			valueWithoutEnd = "";
		}

		// 拼接字符串
		if (positive) {
			return valueWithoutEnd + endChar;
		} else {
			return "-" + valueWithoutEnd + endChar;
		}
	}
	
	public static void main(String[] args) {
		// s;m;c get one more byte length
//		System.out.println("[ 029 \u0001".trim()+"]");			// trim del 0x01 0x02
		FieldSetting fs = new FieldSetting();

		System.out.println("["+fs.transS("1000k\u0001")+"]");	// to long to del '0'
		System.out.println("["+fs.trans9("00011.11")+"]");
//		System.out.println("["+fs.transX("  hello ")+"]");		// only del right ' '
		// no D and default type, overall it works
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * <DL><DT><B>
	 * 返回字段cycleTable的值.
	 * </B></DT><p><DD>
	 * 返回字段cycleTable的值
	 * </DD></DL><p>
	 * @return 返回字段cycleTable的值.
	 */
	public Table getCycleTable() {
		return cycleTable;
	}

	/**
	 * <DL><DT><B>
	 * 设置字段cycleTable的值.
	 * </B></DT><p><DD>
	 * 设置字段cycleTable的值
	 * </DD></DL><p>
	 * @param cycleTable 用以设置字段cycleTable的值.
	 */
	public void setCycleTable(Table cycleTable) {
		this.cycleTable = cycleTable;
	}

}
