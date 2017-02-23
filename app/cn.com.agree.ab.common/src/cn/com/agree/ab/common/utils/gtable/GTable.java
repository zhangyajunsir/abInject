package cn.com.agree.ab.common.utils.gtable;

import javax.inject.Singleton;

import cn.com.agree.ab.common.utils.gtable.table.Table;
import cn.com.agree.ab.common.utils.gtable.table.TableManager;

@Singleton
public class GTable {
	
	public String[] unpackAll(String oTableName, String messageText) throws Exception {
		return unpackAll(oTableName, messageText, 0, null);
	}

	public String[] unpackAll(String oTableName, String messageText, int loopTarget, Object context) throws Exception {
		if (oTableName == null || messageText == null) {
			throw new IllegalArgumentException("拆包提供的参数不能为null");
		}
		
		// 1. unpack the messageText
		return getTable(oTableName).unpackAll(context, messageText);
	}

	
	private Table getTable(String fullname) throws Exception {
		Table table = TableManager.getDefault().getTable(fullname);
		// G表
		if (table == null) {
			throw new Exception("映射表文件无法找到，请检查文件：" + fullname);
		}
		return table;
	}


}
