/*
 * Copyright(C) 2006 Agree Tech, All rights reserved.
 * 
 * Created on 2006-6-26 by Xu Haibo
 */

package cn.com.agree.ab.common.utils.gtable.table;


import groovy.lang.GroovyShell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.control.CompilationFailedException;




/**
 * <DL>
 * <DT><B> i表或者o表 </B></DT>
 * <p>
 * <DD> 详细介绍 </DD>
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
 * @version $Revision: 1.6.2.4 $ $Date: 2008/11/05 15:13:35 $
 */
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class Table {

	private static final String ENCODING = "GBK";

	/**
	 * filename 文件名
	 */
	private String filename;

	/**
	 * fieldSettings 字段设置
	 */
	private List fieldSettings = new ArrayList();

	/**
	 * flatFieldsLength 非循环字段的长度总合
	 */
	private int flatFieldsLength = -1;

	/**
	 * seqNormalize seq正规化标志, 正规化后settings的按照seq的大小进行排序
	 */
	private boolean seqNormalized = false;

	/**
	 * cycleInfo 如果此表是循环块，cycleInfo存储解析得到的循环信息
	 */
	private CycleInfo cycleInfo;

	public void addFieldSetting(FieldSetting fieldSetting) {
		fieldSettings.add(fieldSetting);
	}

	public int getTableSize() {
		return fieldSettings.size();
	}
	

	/**
	 * <DL>
	 * <DT><B> 返回字段filename的值. </B></DT>
	 * <p>
	 * <DD> 返回字段filename的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @return 返回字段filename的值.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * <DL>
	 * <DT><B> 设置字段filename的值. </B></DT>
	 * <p>
	 * <DD> 设置字段filename的值 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param filename
	 *            用以设置字段filename的值.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * <DL>
	 * <DT><B> 标题. </B></DT>
	 * <p>
	 * <DD> 详细介绍 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param trade
	 *            交易类
	 * @return 执行groovy脚本的shell
	 */
	private GroovyShell getGroovyShell(Object trade) {
		GroovyShell gshell = new GroovyShell();
		gshell.setVariable("trade", trade);
		return gshell;
	}


	public String[] unpack(Object trade, String messageText)
			throws  Exception {
		byte[] bytes = messageText.getBytes(ENCODING);// 拆解成bytes已避免中文造成的计数错误
		return unpack(trade, bytes, true);

	}


	public String[] unpack(Object trade, byte[] bytes, boolean setValue) throws Exception
			 {
		// 0. 正规化seq
		normalizeSeq();
		
		List result = new ArrayList();

		// 2. uppack field according to o table
		FieldSetting[] settings = (FieldSetting[]) fieldSettings.toArray(new FieldSetting[0]);
		byte[] buf;
		int unpackLength;
		int pointer = 0;
		for (int i = 0; i < settings.length; i++) {
			unpackLength = settings[i].getUnpackLength(trade,this, result);
			// 该字段为循环字段，按照此循环结构处理全部报文
			if(unpackLength == -1){
				buf = new byte[bytes.length - pointer];
				fillFieldBuf(bytes, pointer, buf);
				pointer = bytes.length;
				// TODO WARNING INFO
//				if(i!= settings.length -1){
//					System.err.println("非法定义的o表");
//				}
			}else {
				buf = new byte[unpackLength];
				fillFieldBuf(bytes, pointer, buf);
				pointer += unpackLength;
			}
			
			String[] fieldResult = settings[i].unpack(buf, trade, this, result);
			result.addAll(Arrays.asList(fieldResult));
			
			// handle setValue, cycle block will do set value in the cycle unpack step
			if (setValue&&fieldResult.length==1)
				setValue(trade, fieldResult[0], settings[i].getDefaultScript());
		}

		return (String[]) result.toArray(new String[0]);
	}

	/**
	 * <DL>
	 * <DT><B> 填充 </B></DT>
	 * <p>
	 * <DD> 长度不足补空格,一定保证dest被充满 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param src
	 * @param srcPos
	 * @param dest
	 */
	private void fillFieldBuf(byte[] src, int srcPos, byte[] dest) {
		if (srcPos + dest.length <= src.length) {
			System.arraycopy(src, srcPos, dest, 0, dest.length);
			return;
		}

		if (srcPos < src.length) {
			for (int i = 0; i < dest.length; i++) {
				if (srcPos + i < src.length) {
					dest[i] = src[srcPos + i];
				} else {
					dest[i] = (byte) ' ';
				}
			}
			return;
		}

		Arrays.fill(dest, (byte) ' ');
	}

	/**
	 * <DL>
	 * <DT><B> 根据o表对交易内数据赋值 </B></DT>
	 * <p>
	 * <DD> 详细介绍 </DD>
	 * </DL>
	 * <p>
	 * 
	 * @param trade
	 * @param string
	 * @param script
	 * @throws CompilationFailedException 
	 * @throws CompilationFailedException
	 */
	private void setValue(Object trade, String value, String script) throws CompilationFailedException
			 {
		// 0.
		if (script == null)
			return;
		if (script.length() == 0)
			return;
		if (script.charAt(0) == '#')
			return;
		if (trade == null)
			return;

		// 1. prepare groovy to read the var values
		GroovyShell gshell = new GroovyShell(getClass().getClassLoader());
		gshell.setVariable("trade", trade);
		gshell.setVariable("field", value);
//		System.out.println("*****script:'" + script + "'");
		// 2. set component value
		gshell.evaluate(script);

	}


	/**
	 * <DL>
	 * <DT><B> 非循环字段的总长度 </B></DT>
	 * <p>
	 * <DD> 详细介绍 </DD>
	 * </DL>
	 * <p>
	 * @throws Exception 
	 */
	public int getFlatFieldsLength(Object trade, Table currentTable, List currentResult) throws Exception {
		if (flatFieldsLength < 0) {
			flatFieldsLength = 0;
			for (Iterator iter = fieldSettings.iterator(); iter.hasNext();) {
				FieldSetting setting = (FieldSetting) iter.next();
				flatFieldsLength += setting.getUnpackLength(trade, currentTable, currentResult);
			}
		}
		return flatFieldsLength;
	}

	

	/**
	 * <DL>
	 * <DT><B> 正规化seq </B></DT>
	 * <p>
	 * <DD> 详细介绍 </DD>
	 * </DL>
	 * <p>
	 */
	private void normalizeSeq() {
		if (seqNormalized) {
			return;
		}

		FieldSetting[] array = (FieldSetting[]) fieldSettings
				.toArray(new FieldSetting[0]);
//		Arrays.sort(array); //ww
		
		//  保留原有序号
		fieldSettings = Arrays.asList(array);
		
		seqNormalized = true;
	}

	public String[] unpackAll(Object trade, String messageText) throws  Exception {
		return unpack(trade, messageText);
	}


	 /**
	 * <DL><DT><B>
	 * 表的seq
	 * </B></DT><p><DD>
	 * 定义为首个字段的seq
	 * </DD></DL><p>
	 * @return 首个字段的seq
	 */
	public int getSeq() {
		FieldSetting first = (FieldSetting) fieldSettings.get(0);
		return first.getSeq();
	}

	public CycleInfo getCycleInfo() {
		return cycleInfo;
	}

	public void setCycleInfo(CycleInfo cycleInfo) {
		this.cycleInfo = cycleInfo;
	}

	public List getFieldSettings() {
		return fieldSettings;
	}
	

}
