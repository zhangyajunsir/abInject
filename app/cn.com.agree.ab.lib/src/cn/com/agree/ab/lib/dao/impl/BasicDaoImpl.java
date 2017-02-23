package cn.com.agree.ab.lib.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.lib.dao.BasicDao;
import cn.com.agree.ab.lib.dao.DBType;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.RecordUtils;
import cn.com.agree.ab.lib.dm.DataStore;
import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.lib.dm.PagingParameter;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.IPersistence;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.ab.trade.ext.persistence.Reference;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.JoinOperator;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.ab.trade.ext.persistence.impl.PersistenceAdapterFactory;
import cn.com.agree.ab.trade.ext.persistence.impl.PersistenceDataSource;



public abstract class BasicDaoImpl implements BasicDao {

	/** 日志对象 */
	private static final Logger	logger	= LoggerFactory.getLogger(BasicDaoImpl.class);
	/** 持久操作对象 */
	protected IPersistence persistence;
	/** 数据库类型 */
	protected DBType dataBaseType;
	/** 分页SQL语句创建对象 */
	protected PagingSqlBuilder pagingSqlBuilder;
	
	public BasicDaoImpl() {
		persistence = PersistenceAdapterFactory.getPersistence();
		String jdbcDriver = ((PersistenceDataSource) persistence.getDataSource()).getJdbcDriver();
		String jdbcUrl    = ((PersistenceDataSource) persistence.getDataSource()).getJdbcUrl();
		if (jdbcDriver.indexOf(DBType.MYSQL.getValue()) > 0)
			dataBaseType = DBType.MYSQL;
		else if (jdbcDriver.indexOf(DBType.ORACLE.getValue()) > 0 || jdbcUrl.indexOf(DBType.ORACLE.getValue()) > 0)
			dataBaseType = DBType.ORACLE;
		else if (jdbcDriver.indexOf(DBType.DB2.getValue()) > 0 || jdbcUrl.indexOf(DBType.DB2.getValue()) > 0)
			dataBaseType = DBType.DB2;
		else if (jdbcDriver.indexOf(DBType.SQLSERVER.getValue()) > 0 || jdbcUrl.indexOf(DBType.SQLSERVER.getValue()) > 0)
			dataBaseType = DBType.SQLSERVER;
		pagingSqlBuilder = new PagingSqlBuilder(dataBaseType);
	}
	
	@Override
	public List<Map<String, Object>> search(String sql, Object... params) {
		return search(sql, new RecordMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> recordRow(Record record, int index) {
				Map<String, Object> map_ = new HashMap<String, Object>();
				for (String column : record.getColumnList()) {
					map_.put(column, record.get(column));
				}
				return map_;
			}
		}, params);
	}

	@Override  
	public List<Map<String, Object>> search(String sql, List<Object> params) {
		return search(sql, params.toArray());
	}

	@Override
	public List<Map<String, Object>> search(String sql, Map<String, Object> params) {
		return search(sql, new RecordMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> recordRow(Record record, int index) {
				Map<String, Object> map_ = new HashMap<String, Object>();
				for (String column : record.getColumnList()) {
					map_.put(column, record.get(column));
				}
				return map_;
			}
		}, params);
	}

	@Override
	public <R> List<R> search(String sql, RecordMapper<R> recordRowMapper, Object... params) {
		List<R> ret = new ArrayList<R>();
		try {
			List<Record> records = persistence.sqlForList(sql, params);
			int cursor = 1;
			for (Record record : records) {
				R r = recordRowMapper.recordRow(record, cursor++);
				ret.add(r);
			}
		} catch (SQLException e) {
			logger.error("执行sql语句["+sql+"]失败", e);
			throw new DaoException("执行sql语句["+sql+"]失败", e);
		}
		return ret;
	}

	@Override
	public <R> List<R> search(String sql, RecordMapper<R> recordRowMapper, List<Object> params) {
		return search(sql, recordRowMapper, params.toArray());
	}

	@Override
	public <R> List<R> search(String sql, RecordMapper<R> recordRowMapper, Map<String, Object> params) {
		List<R> ret = new ArrayList<R>();
		try {
			List<Record> records = persistence.sqlForList(sql, params);
			int cursor = 1;
			for (Record record : records) {
				R r = recordRowMapper.recordRow(record, cursor++);
				ret.add(r);
			}
		} catch (SQLException e) {
			logger.error("执行sql语句["+sql+"]失败", e);
			throw new DaoException("执行sql语句["+sql+"]失败", e);
		}
		return ret;
	}
	
	@Override
	public DataStore<Map<String, Object>> search(String sql,PagingParameter paging, Object... params) {
		return search(sql, new RecordMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> recordRow(Record record, int index) {
				Map<String, Object> map_ = new HashMap<String, Object>();
				for (String column : record.getColumnList()) {
					map_.put(column, record.get(column));
				}
				return map_;
			}
		}, paging, params);
	}

	@Override
	public DataStore<Map<String, Object>> search(String sql, PagingParameter paging, List<Object> params) {
		return search(sql, paging, params.toArray());
	}

	@Override
	public DataStore<Map<String, Object>> search(String sql, PagingParameter paging, Map<String, Object> params) {
		return search(sql, new RecordMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> recordRow(Record record, int index) {
				Map<String, Object> map_ = new HashMap<String, Object>();
				for (String column : record.getColumnList()) {
					map_.put(column, record.get(column));
				}
				return map_;
			}
		}, paging, params);
	}

	@Override
	public <R> DataStore<R> search(String sql, RecordMapper<R> recordRowMapper, PagingParameter paging, Object... params) {
		try {
			List<Record> records = persistence.sqlForList(pagingSqlBuilder.getCountSql(sql), params);
			int count = Integer.valueOf(records.get(0).get("RECORD_COUNT").toString());
			if(count < 0) {
				return null;
			}
			if(count == 0) {
				return new DataStore<R>(count, new ArrayList<R>());
			}
			return new DataStore<R>(count, search(pagingSqlBuilder.getPagingSql(sql, paging), recordRowMapper, params));
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public <R> DataStore<R> search(String sql, RecordMapper<R> recordRowMapper, PagingParameter paging, List<Object> params) {
		return search(sql, recordRowMapper, paging, params.toArray());
	}

	@Override
	public <R> DataStore<R> search(String sql, RecordMapper<R> recordRowMapper, PagingParameter paging, Map<String, Object> params) {
		try {
			List<Record> records = persistence.sqlForList(pagingSqlBuilder.getCountSql(sql), params);
			int count = Integer.valueOf(records.get(0).get("RECORD_COUNT").toString());
			if(count < 0) {
				return null;
			}
			if(count == 0) {
				return new DataStore<R>(count, new ArrayList<R>());
			}
			return new DataStore<R>(count, search(pagingSqlBuilder.getPagingSql(sql, paging), recordRowMapper, params));
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public List<Map<String, Object>> join(Where condition, Class<?>... classLink) {
		String orders = null;
		return join(condition, orders, classLink);
	}

	@Override
	public List<Map<String, Object>> join(Where condition, String orders, Class<?>... classLink) {
		RecordMapper<Map<String, Object>> mapRowMapper = new RecordMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> recordRow(Record record, int index) {
				Map<String, Object> map_ = new HashMap<String, Object>();
				for (String column : record.getColumnList()) {
					map_.put(column, record.get(column));
				}
				return map_;
			}
		};
		return join(condition, orders, mapRowMapper, classLink);
	}
	
	@Override
	public <R> List<R> join(Where condition, RecordMapper<R> recordRowMapper, Class<?>... classLink) {
		String orders = null;
		return join(condition, orders, recordRowMapper, classLink);
	}
	@Override
	public <R> List<R> join(Where condition, String orders, RecordMapper<R> recordRowMapper, Class<?>... classLink) {
		Record requestRecord = buildJoinSql(condition, orders, classLink);
		List<Record> results = null;
		try {
			results = persistence.queryList(requestRecord, condition);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		List<R> list = new ArrayList<R>();
		int cursor = 1;
		for (Record record : results) {
			list.add(recordRowMapper.recordRow(record, cursor++));
		}
		return list;
	}

	@Override
	public DataStore<Map<String, Object>> join(Where condition, PagingParameter paging, Class<?>... classLink) {
		String orders = null;
		return join(condition, orders, paging, classLink);
	}
	@Override
	public DataStore<Map<String, Object>> join(Where condition, String orders, PagingParameter paging, Class<?>... classLink) {
		RecordMapper<Map<String, Object>> recordRowMapper = new RecordMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> recordRow(Record record, int index) {
				Map<String, Object> map_ = new HashMap<String, Object>();
				for (String column : record.getColumnList()) {
					map_.put(column, record.get(column));
				}
				return map_;
			}
		};
		return join(condition, orders, recordRowMapper, paging, classLink);
	}

	@Override
	public <R> DataStore<R> join(Where condition, RecordMapper<R> recordRowMapper, PagingParameter paging, Class<?>... classLink) {
		String orders = null;
		return join(condition, orders, recordRowMapper, paging, classLink);
	}
	@Override
	public <R> DataStore<R> join(Where condition, String orders, RecordMapper<R> recordRowMapper, PagingParameter paging, Class<?>... classLink) {
		Record requestRecord = buildJoinSql(condition, orders, classLink);
		List<Record> results = null;
		try {
			results = persistence.queryList(requestRecord, condition, paging.getStart(), paging.getLimit());
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		List<R> list = new ArrayList<R>();
		int cursor = 1;
		for (Record record : results) {
			list.add(recordRowMapper.recordRow(record, cursor++));
		}
		return new DataStore<R>(list.size(), list);
	}
	
	@SuppressWarnings("unchecked")
	private Record buildJoinSql(Where condition, String orders, Class<?>... classLink) throws DaoException {
		Record joinRecord = new Record(condition.getTableName());
		for (int i = 0;i < classLink.length;i++) {
			Class<? extends EntityDM> clazz = (Class<? extends EntityDM>)classLink[i];
			EntityDM entity;
			try {
				entity = clazz.newInstance();
			} catch (Exception e1) {
				throw new DaoException("["+clazz+"]默认构造器实例化失败");
			}
			Record record = MappingAccessor.getInstance().parseRecord(entity, null, false, false);
			for (String column : record.getColumnList()) {
				String _column_ = record.getTableName() +  "." + column + " AS " +  record.getTableName() +  "_" + column;
				joinRecord.set(_column_, record.get(column));
			}
			if(i == classLink.length - 1) {
				break;
			}
			
			Class<? extends EntityDM> rClass = (Class<? extends EntityDM>)classLink[i+1];
			EntityDM entity2;
			try {
				entity2 = clazz.newInstance();
			} catch (Exception e1) {
				throw new DaoException("["+rClass+"]默认构造器实例化失败");
			}
			Record record2 = MappingAccessor.getInstance().parseRecord(entity2, null, false, false);
			
			Reference reference = RecordUtils.getReferenceField(clazz, rClass).getAnnotation(Reference.class);
			String[] currentColumn = reference.currentColumn();
			if (currentColumn == null || currentColumn.length == 0) {
				currentColumn = record.getKeyColumns().toArray(new String[record.getKeyColumns().size()]);
			}
			String[] targetColumn  = reference.targetColumn();
			if (targetColumn == null || targetColumn.length == 0) {
				targetColumn = record2.getKeyColumns().toArray(new String[record2.getKeyColumns().size()]);
			}
			if (currentColumn.length != targetColumn.length)
				throw new DaoException("["+clazz+"]"+"["+rClass+"]引用列不一致");
			WhereSegment[] whereSegments  = new WhereSegment[currentColumn.length]; 
			for (int j=0; j<currentColumn.length; j++) {
				whereSegments[j] = Where.get(record.getTableName()+"."+currentColumn[j], record2.getTableName()+"."+targetColumn[j]);
			}
			
			condition.putJoinTable(JoinOperator.INNER, record2.getTableName(), Where.and(whereSegments));
		}
		
		if (orders != null)
			condition.putOrder(orders);
		return joinRecord;
	}

}
