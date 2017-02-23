package cn.com.agree.ab.lib.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.lib.dao.DBType;
import cn.com.agree.ab.lib.dm.PagingParameter;
import cn.com.agree.ab.lib.exception.DaoException;



/**
 * 分页SQL语句构建工具类
 */
public class PagingSqlBuilder {
	/** log4j对象 */
	private static final Logger	log	= LoggerFactory.getLogger(PagingSqlBuilder.class);
	
	/** 数据库的类型 */
	private DBType dataBaseType;

	public PagingSqlBuilder(DBType dataBaseType) {
		this.dataBaseType = dataBaseType;
	}

	/**
	 * 获得计算总记录数的SQL语句
	 *
	 * @param rawSql
	 * @return
	 */
	public String getCountSql(String rawSql) {
		String countSql = "SELECT COUNT(*) AS RECORD_COUNT " + rawSql.substring(rawSql.toUpperCase().indexOf("FROM"));
		int orderIndex = countSql.toUpperCase().lastIndexOf("ORDER");
		if(orderIndex >= 0) {
			countSql = countSql.substring(0, orderIndex).trim();
		}
		log.debug("总记录数的SQL语句："+countSql);
		return countSql;
	}
	
	/**
	 * 获得计算分组总记录数的SQL语句
	 *
	 * @param rawSql
	 * @return
	 */
	public String getGroupCountSql(String rawSql) {
		String groupCountSql =  "SELECT SUM(RECORD_COUNT) FROM (" + getCountSql(rawSql) + ") AS T";
		log.debug("分组总记录数的SQL语句："+groupCountSql);
		return groupCountSql;
	}
	
	/**
	 * 获得分页SQL语句
	 *
	 * @param rawSql
	 * @param paging
	 * @return
	 */
	public String getPagingSql(String rawSql, PagingParameter paging) {
		if(paging == null || paging.isInvalid()) {
			log.debug(rawSql);
			return rawSql;
		}
		int rows  = paging.getLimit();
		int start = paging.getStart();
		int end = start + rows;
		if(dataBaseType == DBType.ORACLE) {
			String pagingSql = "SELECT T.*, ROWNUM AS ROW_NUM FROM (" + rawSql + ") AS T WHERE ROWNUM < " + end;
			if(start == 0) {
				log.debug(pagingSql);
				return pagingSql;
			}
			pagingSql = "SELECT * FROM (" + pagingSql + ") AS T_O WHERE ROW_NUM >= " + start;
			log.debug(pagingSql);
			return pagingSql;
		}
		if(dataBaseType == DBType.MYSQL) {
			String pagingSql = rawSql + " LIMIT " + start + ", " + rows;
			log.debug(pagingSql);
			return pagingSql;
		}
		if(dataBaseType == DBType.DB2) {
			// TODO
			
		}
		if(dataBaseType == DBType.SQLSERVER) {
			String pagingSql = "SELECT TOP " + end + rawSql.trim().substring(6);
			if(start == 0) {
				log.debug(pagingSql);
				return pagingSql;
			}
			String orders = rawSql.substring(rawSql.toUpperCase().lastIndexOf("ORDER"));
			int subIndex  = rawSql.toUpperCase().indexOf("FROM") - 1;
			pagingSql = "SELECT * FROM (" + rawSql.substring(0, subIndex) + ", ROW_NUMBER() OVER(" + orders + 
					") AS ROW_NUM " + rawSql.substring(subIndex, rawSql.toUpperCase().lastIndexOf("ORDER")).trim() + 
					") AS T WHERE ROW_NUM >= " + start + " AND ROW_NUM < " + end;
			log.debug(pagingSql);
			return pagingSql;
		}
		throw new DaoException("Unknown type of database!");
	}

}
