 /*
 * Copyright(C) 2006 Agree Tech, All rights reserved.
 * 
 * Created on 2006-6-26   by Xu Haibo
 */

package cn.com.agree.ab.common.utils.gtable.table;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.resource.ResourcePlugin;
import cn.com.agree.inject.InjectPlugin;

public class TableManager {
	
	private Boolean debugMode;
	
	/**
	 * tableContainer 缓存容器
	 */
	private Map<String, Table> tableContainer;
	
	private static TableManager instance;
	
	public static  TableManager getDefault(){
		if(instance==null){
			instance = new TableManager();
		}
		return instance;
	}
	
	private TableManager(){
		tableContainer = new HashMap<String, Table>();
	}

	
	private boolean isDebugMode(){
		if (null == debugMode) {
			String debugModeStr = "false";
			debugMode = new Boolean("true".equals(debugModeStr.trim()));
		}
		return debugMode.booleanValue();
	}
	
	
	private synchronized Table loadTableFromProject(String filename) throws Exception{
		InputStream ret;
		filename = InjectPlugin.getDefault().getInstance(ConfigManager.class).getAbsConfigPath(filename);
		if(filename == null) 
			return null;
		
		try {
			ret = ResourcePlugin.getDefault().getResourceStream(filename);
		} catch (FileNotFoundException e) {
			ret = null;
		}
		if(ret == null) 
			return null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(ret));
		TableMaker tableMaker = new TableMaker();
		try {
			String line = reader.readLine();
			while(line!=null){
				tableMaker.addLine(line);
				line = reader.readLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if( reader != null)
				reader.close();
		}
		
		Table table = tableMaker.getTable();
		table.setFilename(filename);
		if(table!=null&&!isDebugMode()){
			tableContainer.put(filename, table);
		}
		return table;
	}
	
	
	public Table getTable(String filename) throws Exception{
		// 1. 从缓存读取
		if(tableContainer.containsKey(filename)){
			return (Table) tableContainer.get(filename);
		}
		
		// 2. 从交易所在工程目录的/dsr/读取
		Table table = loadTableFromProject(filename);
		if(table!=null){
			return table;
		}
		
		throw new Exception("无法找到必要的通讯辅助文件:" + filename);
	}


}
