package cn.com.agree.ab.lib.dao;

public enum DBType {
	ORACLE("oracle"), MYSQL("mysql"), SQLSERVER("sqlserver"), DB2("db2");
	
	private String dbType;
	
	private DBType(String dbType){
		this.dbType = dbType;
	}
	
	public String getValue() {
		return dbType;
	}
}
