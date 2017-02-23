/*
 * Copyright(C) 2006 Agree Tech, All rights reserved.
 * 
 * Created on 2006-6-26   by Xu Haibo
 */

package cn.com.agree.ab.common.utils.gtable.table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TableMaker {
	/**
	 * Logger for this class
	 */
//	private static final Log logger = LogFactory.getLog(TableMaker.class);

	/**
	 * tableCorrupt 表格式损坏标志
	 */
	private boolean tableCorrupt = false;
	
	/**
	 * cycleFlag 读取循环报文的标志, true表示表中有循环字段
	 */
	private boolean cycleFlag = false;
	
	/**
	 * readSettingFlag 是否可以读取内容,为了跳过第一行数据特别设定
	 */
	private boolean readSettingFlag = false;
	
	/**
	 * cycleLevel 循环层次
	 */
	private int cycleLevel = 0;
	
	/**
	 * cycleTableMaker 循环报表的maker
	 */
	private TableMaker cycleTableMaker;
	
	/**
	 * cycleMakerStack 循环报表的maker的栈，与cycleLevel配合，处理循环报文体嵌套的情况
	 */
	private List cycleMakerStack = new ArrayList();
	
	/**
	 * cycleInfo 循环头信息
	 */
	private CycleInfo cycleInfo;

	private Table table;

	/**
	 * <DL>
	 * <DT><B> 构造器. </B></DT>
	 * <p>
	 * <DD> 构造器说明 </DD>
	 * </DL>
	 * <p>
	 */
	public TableMaker() {
		table = new Table();
	}

	public void addLine(String line) throws Exception {
		// 0. table corrupt
		if (tableCorrupt)
			return;

		// 1. skip blank line and comment line
		line = line.trim();
		if (line.length() == 0)
			return;
		if (line.charAt(0) == '#')
			return;
		// 1.1 skip the first line to keep the old style compatible
		if (!readSettingFlag){
			readSettingFlag = true;
			return;
		}
		
		// 2. is reading cycle table part
		// 2.1 处理循环头标志
		if(line.toLowerCase().startsWith("forbegin")){
			if(cycleLevel>0){
				// 处理循环块嵌套情况
				// 保留当前的maker
				cycleMakerStack.add(cycleTableMaker);
			} else{
				cycleFlag = true;
			}
			
			cycleTableMaker =  new TableMaker();
			cycleTableMaker.setReadSettingFlag(true);// cycleTableMaker do not need to skip the first line
			cycleLevel++;
			// 2.1.1 获得循环信息
			CycleInfo info = parseCycleInfo(line);
			cycleTableMaker.setCycleInfo(info);
			return;
		}
		// 2.2 处理循环尾标志。循环头和尾在level>1的情况下都交给cycleTableMaker处理
		if(line.toLowerCase().startsWith("forend")){
			// finish current cycle maker work
			FieldSetting cycleSetting = new FieldSetting();
			cycleSetting.setType("t");
			cycleSetting.setCycleTable(cycleTableMaker.getTable());
			table.addFieldSetting(cycleSetting);
			// TODO 循环表fieldsetting的length等值的设定和使用
			
			// handle cycle flag
			cycleLevel--;
			if(cycleLevel==0){
				cycleFlag = false;
			}else{
				cycleTableMaker = (TableMaker) cycleMakerStack.remove(cycleMakerStack.size()-1);
			}
			
			// 此行数据处理完成
			return;
		}
		// 2.3 转交循环报文
		if(cycleFlag){
			cycleTableMaker.addLine(line);
			return;
		}
		

		// 3. get the content
		String[] content = line.split("(\\s)+");
		if (content.length < 3) {
//			logger.error("表格内容错误, 字段数少于3个:" + line);
			corrupt();
			return;
		}
		// 2.1 read content
		FieldSetting setting = new FieldSetting();
		try {
			setting.setSeq(Integer.parseInt(content[0]));
			setting.setLength(Integer.parseInt(content[1]));
		} catch (NumberFormatException e) {
			String errorMsg = "表格内容错误, 解析数字发生错误:" + line;
//			logger.error(errorMsg);
			corrupt();
			throw new Exception(errorMsg);
		}
		setting.setType(content[2]);
		
		// 后面的内容可有可无，可能形如“$tia[0] trade.field #test”
		int offSet = 0;
		if (content.length > 3 && content[3].charAt(0) == '$') {
			setting.setVar(content[3].substring(1));
			offSet = 1;
		}else{
			setting.setVar("");
		}
		
		if (content.length > 3+offSet && content[3+offSet].charAt(0) != '#') {
			setting.setDefaultScript(content[3+offSet]);
		} else {
			setting.setDefaultScript("");
		}
		table.addFieldSetting(setting);
	}

	private void setCycleInfo(CycleInfo info) {
		cycleInfo = info;
	}

	private void corrupt() {
		table = null;
		tableCorrupt = true;
	}

	/**
	 * <DL>
	 * <DT><B> 返回字段table的值. </B></DT>
	 * <p>
	 * <DD> 保证返回有效的table </DD>
	 * </DL>
	 * <p>
	 * 
	 * @return 返回字段table的值.
	 */
	public Table getTable() {
		table.setCycleInfo(cycleInfo);
		return table;
	}

	/**
	 * <DL><DT><B>
	 * 设置字段readSettingFlag的值.
	 * </B></DT><p><DD>
	 * 设置字段readSettingFlag的值
	 * </DD></DL><p>
	 * @param readSettingFlag 用以设置字段readSettingFlag的值.
	 */
	public void setReadSettingFlag(boolean readSettingFlag) {
		this.readSettingFlag = readSettingFlag;
	}
	
	protected CycleInfo parseCycleInfo(String forbeginStr) throws Exception{
		String rest = forbeginStr.substring("forbegin".length()).trim();
		if(rest.equals("")){
			
			return new CycleInfo(CycleInfo.TYPE_UNLIMITED , null, -1, -1);
		}else if(rest.startsWith("$")){
			int seq = -1;
			try {
				seq = Integer.parseInt(rest.substring(1).trim());
			} catch (NumberFormatException e) {
				throw new Exception("无法解析的字段顺序号，字段定义内容：[" + forbeginStr + "]");
			}
			return new CycleInfo(CycleInfo.TYPE_LINE_SEQ, null, -1, seq);
		}else {
			Pattern p = Pattern.compile("[\\d]*");
			Matcher m = p.matcher(rest);
			if(m.matches()){
				int cycleCount = Integer.parseInt(rest);
				return new CycleInfo(CycleInfo.TYPE_CONSTANT, null, cycleCount, -1);
			}else {
				return new CycleInfo(CycleInfo.TYPE_VARIANT, rest, -1, -1);
			}
			
		}
	}
}
