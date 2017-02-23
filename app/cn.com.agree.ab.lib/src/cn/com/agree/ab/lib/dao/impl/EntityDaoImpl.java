package cn.com.agree.ab.lib.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import cn.com.agree.ab.lib.dao.EntityDao;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.RecordUtils;
import cn.com.agree.ab.lib.dm.DataStore;
import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.lib.dm.PagingParameter;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.lib.utils.ObjectUtil;
import cn.com.agree.ab.lib.utils.ReflectionUtil;
import cn.com.agree.ab.trade.ext.persistence.Association.Relation;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.ab.trade.ext.persistence.Reference;
import cn.com.agree.ab.trade.ext.persistence.ReferenceInfo;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.JoinOperator;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;

public class EntityDaoImpl<E extends EntityDM> extends BasicDaoImpl implements EntityDao<E> {
	private static final Logger	logger	= LoggerFactory.getLogger(EntityDaoImpl.class);
	/** 条件关键字集合 */
	private static final List<String> CONDITION_KEYWORDS  = new ArrayList<String>();
	/** 条件运算符（正则表达式形式）集合 */
	private static final List<String> CONDITION_OPERATORS = new ArrayList<String>();
	
	static {
		/** 初始化条件关键字集合 */
		CONDITION_KEYWORDS.add("IS");
		CONDITION_KEYWORDS.add("NOT");
		CONDITION_KEYWORDS.add("NULL");
		CONDITION_KEYWORDS.add("LIKE");
		CONDITION_KEYWORDS.add("BETWEEN");
		CONDITION_KEYWORDS.add("AND");
		CONDITION_KEYWORDS.add("OR");
		CONDITION_KEYWORDS.add("IN");
		CONDITION_KEYWORDS.add("ASC");
		CONDITION_KEYWORDS.add("DESC");
		/** 初始化条件运算符集合（= < > + - * / % ( ) , ?），组合符号（<> <= >=）需要特殊处理 */
		CONDITION_OPERATORS.add("=");
		CONDITION_OPERATORS.add("<");
		CONDITION_OPERATORS.add(">");
		CONDITION_OPERATORS.add("\\+");
		CONDITION_OPERATORS.add("\\-");
		CONDITION_OPERATORS.add("\\*");
		CONDITION_OPERATORS.add("/");
		CONDITION_OPERATORS.add("%");
		CONDITION_OPERATORS.add("\\(");
		CONDITION_OPERATORS.add("\\)");
		CONDITION_OPERATORS.add("\\,");
		CONDITION_OPERATORS.add("\\?");
	}
	/** 实体类类型 */
	private Class<E> entityClass = null;
	
	protected Record entityRecordTemplate = null;
	
	/**
	 * 构造方法  通过反射初始化entityClass
	 */
	@SuppressWarnings("unchecked")
	public EntityDaoImpl() {
		// 获得EntityDao包含泛型实参的类型
		ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericSuperclass();
		Type[] params = parameterizedType.getActualTypeArguments();
		this.entityClass = (Class<E>)params[0];
		E entity;
		try {
			entity = this.entityClass.newInstance();
		} catch (Exception e1) {
			throw new DaoException("["+entityClass.getName()+"]默认构造器实例化失败");
		}
		this.entityRecordTemplate = MappingAccessor.getInstance().parseRecord(entity, null, false, false);
	}

	@Override
	public <K extends Number> E get(K id, Class<?>... associationLink) {
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.get(entityRecordTemplate.getKeyColumn(), id.toString()));
		if(associationLink == null || associationLink.length == 0) {
			Record resultRecord = null;
			try {
				resultRecord = persistence.querySingle(entityRecordTemplate, condition);
			} catch (SQLException e) {
				logger.error("查询表["+entityRecordTemplate.getTableName()+"]失败", e);
				throw new DaoException("查询表["+entityRecordTemplate.getTableName()+"]失败", e);
			}
			if (resultRecord == null)
				return null;
			E entity;
			try {
				entity = entityClass.newInstance();
			} catch (Exception e1) {
				throw new DaoException("["+entityClass.getName()+"]默认构造器实例化失败");
			}
			MappingAccessor.getInstance().fillRecord(entity, null, resultRecord);
			return entity;
		}
		return get(condition, associationLink);
	}

	@Override
	public E get(String sql, Object... params) {
		List<E> result = query(sql, params);
		if (!result.isEmpty()) {
            return result.get(0);
        }
		return null;
	}
	@Override
	public E get(String sql, List<Object> params) {
		return get(sql, params.toArray());
	}
	@Override
	public E get(String sql, Map<String, Object> params) {
		List<E> result = query(sql, params);
		if (!result.isEmpty()) {
            return result.get(0);
        }
		return null;
	}

	@Override
	public E get(Where condition, Class<?>... associationLink) {
		List<E> result = query(condition, associationLink);
		if (result != null && !result.isEmpty()) {
            return result.get(0);
        }   
		return null;
	}

	@Override
	public <K extends Number> void delete(K id) {
		Record paramRecord = null;
		try {
			paramRecord = entityRecordTemplate.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("Record复制失败", e);
			throw new DaoException("Record复制失败", e);
		}
		paramRecord.setKey(String.valueOf(id));
		try {
			persistence.delete(paramRecord);
		} catch (SQLException e) {
			logger.error("根据ID["+id+"]删除表["+paramRecord.getTableName()+"]失败", e);
			throw new DaoException("根据ID["+id+"]删除表["+paramRecord.getTableName()+"]失败", e);
		}
	}

	@Override
	public void save(E entity) {
		try {
			persistence.insert(entity);
		} catch (SQLException e) {
			logger.error("新增表["+entityRecordTemplate.getTableName()+"]记录失败", e);
			throw new DaoException("新增表["+entityRecordTemplate.getTableName()+"]记录失败", e);
		}
		/*
		Where w = MappingAccessor.getInstance().parseWhere(entity, null);
		Record resultRecord = null;
		try {
			resultRecord = persistence.querySingle(entityRecordTemplate, w);
		} catch (SQLException e) {
			logger.error("新增表["+entityRecordTemplate.getTableName()+"]记录后查询ID失败", e);
			throw new DaoException("新增表["+entityRecordTemplate.getTableName()+"]记录后查询ID失败", e);
		}
		return Integer.valueOf(resultRecord.getKeys().get(0).toString());
		*/
	}

	@Override
	public <K extends Number> void save(E entity, K id) {
		Record paramRecord = MappingAccessor.getInstance().parseRecord(entity, null, false, true);
		paramRecord.setKey(id);
		try {
			persistence.insert(paramRecord);
		} catch (SQLException e) {
			logger.error("新增表["+paramRecord.getTableName()+"]ID["+id+"]记录失败", e);
			throw new DaoException("新增表["+paramRecord.getTableName()+"]ID["+id+"]记录失败", e);
		}
	}

	@Override
	public void update(E entity) {
		try {
			persistence.update(entity);
		} catch (SQLException e) {
			logger.error("更新表["+entityRecordTemplate.getTableName()+"]记录失败", e);
			throw new DaoException("更新表["+entityRecordTemplate.getTableName()+"]记录失败", e);
		}
	}

	@Override
	public void saveOrUpdate(E entity) {
		if(entity.isTransient()) {
			save(entity);
		} else {
			update(entity);
		}
	}

	@Override
	public List<E> query(String sql, Object... params) {
		return search(sql, new RecordMapper<E>() {
			@Override
			public E recordRow(Record record, int index) {
				E entity;
				try {
					entity = entityClass.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+entityClass.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, params);
	}
	@Override
	public List<E> query(String sql, List<Object> params) {
		return query(sql, params.toArray());
	}
	@Override
	public List<E> query(String sql, Map<String, Object> params) {
		return search(sql, new RecordMapper<E>() {
			@Override
			public E recordRow(Record record, int index) {
				E entity;
				try {
					entity = entityClass.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+entityClass.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, params);
	}

	@Override
	public List<E> query(Where condition, Class<?>... associationLink) {
		String orders = null;
		return query(condition, orders, associationLink);
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<E> query(Where condition, String orders, Class<?>... associationLink) {
		Map<String, Object> resMap = buildAssociationSql(condition, orders, associationLink);
		List<Record> results = null;
		try {
			results = persistence.queryList((Record)resMap.get("joinRecord"), condition);
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		List<E> list = fillAssociationRecord(results, associationLink, (Record[])resMap.get("assoRecordLink"), (List<List<Object>>)resMap.get("associationFlags"));
		return list;
	}

	@Override
	public DataStore<E> query(String sql, PagingParameter paging, Object... params) {
		try {
			List<Record> records = persistence.sqlForList(pagingSqlBuilder.getCountSql(sql), params);
			int count = Integer.valueOf(records.get(0).get("RECORD_COUNT").toString());
			if(count < 0) {
				return null;
			}
			if(count == 0) {
				return new DataStore<E>(count, new ArrayList<E>());
			}
			return new DataStore<E>(count, query(pagingSqlBuilder.getPagingSql(sql, paging), params));
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	@Override
	public DataStore<E> query(String sql, PagingParameter paging, List<Object> params) {
		return query(sql, paging, params.toArray());
	}
	@Override
	public DataStore<E> query(String sql, PagingParameter paging, Map<String, Object> params) {
		try {
			List<Record> records = persistence.sqlForList(pagingSqlBuilder.getCountSql(sql), params);
			int count = Integer.valueOf(records.get(0).get("RECORD_COUNT").toString());
			if(count < 0) {
				return null;
			}
			if(count == 0) {
				return new DataStore<E>(count, new ArrayList<E>());
			}
			return new DataStore<E>(count, query(pagingSqlBuilder.getPagingSql(sql, paging), params));
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public DataStore<E> query(Where condition, PagingParameter paging, Class<?>... associationLink) {
		String orders = null;
		return query(condition, orders, paging, associationLink);
	}
	@SuppressWarnings("unchecked")
	@Override
	public DataStore<E> query(Where condition, String orders, PagingParameter paging, Class<?>... associationLink) {
		Map<String, Object> resMap = buildAssociationSql(condition, orders, associationLink);
		List<Record> results = null;
		try {
			results = persistence.queryList((Record)resMap.get("joinRecord"), condition, paging.getStart(), paging.getLimit());
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		List<E> list = fillAssociationRecord(results, associationLink, (Record[])resMap.get("assoRecordLink"), (List<List<Object>>)resMap.get("associationFlags"));
		return new DataStore<E>(list.size(), list);
	}

	@Override
	public List<E> queryAll(Class<?>... associationLink) {
		String orders = null;
		return queryAll(orders, associationLink);
	}
	@Override
	public List<E> queryAll(String orders, Class<?>... associationLink) {
		return query(new Where(entityRecordTemplate.getTableName(), null), orders, associationLink);
	}

	@Override
	public DataStore<E> queryAll(PagingParameter paging, Class<?>... associationLink) {
		String orders = null;
		return queryAll(orders, paging, associationLink);
	}
	@Override
	public DataStore<E> queryAll(String orders, PagingParameter paging, Class<?>... associationLink) {
		return query(new Where(entityRecordTemplate.getTableName(), null), orders, paging, associationLink);
	}

	@Override
	public int count() {
		return count(new Where(entityRecordTemplate.getTableName(), null));
	}
	@Override
	public int count(Where condition) {
		String sql = "SELECT count(*) AS RECORD_COUNT FROM " + condition.getTableName() + condition.toPreparedStatementSql();
		logger.debug("执行的查询总数SQL:["+sql+"]");
		int count ;
		try {
			Object[] params = condition.getValueList().toArray();
			List<Record> records = persistence.sqlForList(sql, params);
			count = Integer.valueOf(records.get(0).get("RECORD_COUNT").toString());
		} catch (Exception e) {
			throw new DaoException(e);
		}
		return count;
	}

	@Override
	public <K extends Number> void deletes(List<K> ids) {
		try {
			persistence.batchBegin();
			for (K id : ids) {
				delete(id);
			}
			persistence.batchExecute();
		} catch (SQLException e) {
			throw new DaoException("批量删除失败", e);
		}
	}

	@Override
	public <K extends Number> void saves(List<E> entitys, K... ids) {
		if (ids != null && ids.length > 0 && ids.length != entitys.size()) 
			throw new DaoException("实体类数量与ID数量不一致");
		try {
			persistence.batchBegin();
			int index = 0;
			for (E entity : entitys) {
				if (ids != null && ids.length > 0) {
					save(entity, ids[index++]);
				} else {
					save(entity);
				}
			}
			persistence.batchExecute();
		} catch (SQLException e) {
			throw new DaoException("批量保存失败", e);
		}
	}

	@Override
	public void updates(List<E> entitys) {
		try {
			persistence.batchBegin();
			for (E entity : entitys) {
				update(entity);
			}
			persistence.batchExecute();
		} catch (SQLException e) {
			throw new DaoException("批量更新失败", e);
		}
	}

	@Override
	public void saveOrUpdates(List<E> entitys) {
		if(CollectionUtils.isEmpty(entitys)) {
			return;
		}
		List<E> saves = new ArrayList<E>();
		List<E> updates = new ArrayList<E>();
		for (E entity : entitys) {
			if(entity.isTransient()) {
				saves.add(entity);
			} else {
				updates.add(entity);
			}
		}
		saves(saves);
		updates(updates);
	}

	@Override
	public E getReferenced(E entity) {
		
		return null;
	}
	@Override
	public E getReferenced(Object referenceValue) {
		
		return null;
	}

	@Override
	public <R extends EntityDM> R getReferenced(E entity, Class<R> referencedClass) {
		
		return null;
	}
	@Override
	public <R extends EntityDM> R getReferenced(Object referenceValue, Class<R> referencedClass) {
		
		return null;
	}

	@Override
	public <R extends EntityDM> List<E> queryReferences(R referenced) {
		
		return null;
	}
	@Override
	public <R extends EntityDM> List<E> queryReferences(Class<R> referencedClass, Object referencedValue) {
		
		return null;
	}

	@Override
	public <R extends EntityDM, S extends EntityDM> List<S> queryReferences(R referenced, Class<S> referenceClass) {
		
		return null;
	}
	@Override
	public <R extends EntityDM, S extends EntityDM> List<S> queryReferences(Class<R> referencedClass, Object referencedValue, Class<S> referenceClass) {
		
		return null;
	}

	@Override
	public <R extends EntityDM> DataStore<E> queryReferences(R referenced, PagingParameter paging) {
		
		return null;
	}
	@Override
	public <R extends EntityDM> DataStore<E> queryReferences(Class<R> referencedClass, Object referencedValue, PagingParameter paging) {
		
		return null;
	}
	
	@Override
	public <R extends EntityDM, S extends EntityDM> DataStore<S> queryReferences(R referenced, Class<S> referenceClass, PagingParameter paging) {
		
		return null;
	}
	@Override
	public <R extends EntityDM, S extends EntityDM> DataStore<S> queryReferences(Class<R> referencedClass, Object referencedValue, Class<S> referenceClass, PagingParameter paging) {
		
		return null;
	}
	
	/**
	 * 构建关联查询SQL语句
	 * @param condition       查询条件
	 * @param orders          排序对象
	 * @param associationLink 关联链
	 * @return Record         关联查询Record
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildAssociationSql(Where condition, String orders, Class<?>[] associationLink) {
		String tableName = entityRecordTemplate.getTableName() + "_0";
		condition.setTableName(entityRecordTemplate.getTableName() + " AS " + tableName);
		
		Record joinRecord = new Record(entityRecordTemplate.getTableName());
		for (String col : entityRecordTemplate.getColumnList()) {
			String _col_ = tableName +  "." + col + " AS " +  tableName +  "_" + col;
			joinRecord.set(_col_, entityRecordTemplate.get(col));
		}
		
		StringBuilder whereClause = new StringBuilder();
		List<Object>  values      = new ArrayList<Object>();
		Record[] assoRecordLink   = new Record[associationLink.length];
		// 关联类型三标志(被关联类型下标、被关联类型、关联类型)集合
		List<List<Object>> associationFlags = new ArrayList<List<Object>>();
		for (int i = 0; i < associationLink.length; i++) {
			if (!ObjectUtil.isExtends(associationLink[i], EntityDM.class))
				throw new DaoException("Association " + associationLink[i].getName() + " type error for " + entityClass.getName() + ", the type must extends Entity!");
			Class<? extends EntityDM> assoClazz = (Class<? extends EntityDM>)associationLink[i];
			EntityDM assoEntity;
			try {
				assoEntity = assoClazz.newInstance();
			} catch (Exception e1) {
				throw new DaoException("["+assoClazz.getName()+"]默认构造器实例化失败");
			}
			Record assoRecord = MappingAccessor.getInstance().parseRecord(assoEntity, null, false, false);
			assoRecordLink[i] = assoRecord;
			
			Record _assoRecord_ = null;
			Class<? extends EntityDM> _assoClazz_ = null;
			ReferenceInfo referenceInfo = null;
			int j = -1;
			for (; j < i; j++) {
				if(j >= 0) {
					_assoRecord_ = assoRecordLink[j];
					_assoClazz_  = (Class<? extends EntityDM>)associationLink[j];
				} else {
					_assoRecord_ = entityRecordTemplate;
					_assoClazz_  = entityClass;
				}
				for (ReferenceInfo _referenceInfo_ :_assoRecord_.getReferenceInfo()) {
					if (_referenceInfo_.getTargetClass() == assoClazz) {
						referenceInfo = _referenceInfo_;
						break;
					}
				}
				if (referenceInfo != null) {
					List<Object> associationFlag = new ArrayList<Object>();
					associationFlag.add(j + 1);
					associationFlag.add(_assoClazz_);
					associationFlag.add(assoClazz);
					if(!associationFlags.contains(associationFlag)) {
						//检查指定的关联类型对应的引用属性是否存在
						associationFlags.add(associationFlag);
						break;
					}
				}
			}
			if(j == i) {
				throw new DaoException("Annotation " + Reference.class.getName() + " not found for association " + assoClazz.getName() + " of " + entityClass.getName());
			}
			
			String tableName2 = assoRecord.getTableName() + "_" + (i + 1);
			for (String col : assoRecord.getColumnList()) {
				String _col_ = tableName2 +  "." + col + " AS " +  tableName2 +  "_" + col;
				joinRecord.set(_col_, assoRecord.get(col));
			}
			String tableName1 = _assoRecord_.getTableName() + "_" + (j + 1);
			// 由于需要对查询表名进行特殊化处理，未使用Where里的putJoinTable方式
			List<WhereSegment> whereSegments  = new ArrayList<WhereSegment>(); 
			for (int k=0; k<referenceInfo.getCurrentColumn().length; k++) {
				// 带点号的值，平台会直接拼接
				whereSegments.add(Where.get(tableName1+"."+referenceInfo.getCurrentColumn()[k], tableName2+"."+referenceInfo.getTargetColumn()[k]));
			}
			if (referenceInfo.getTargetCondition() != null && referenceInfo.getTargetCondition().length%2 == 0) {
				for (int k=0; k<referenceInfo.getTargetCondition().length; k=k+2) {
					// 不带点号的值，平台会直接会采用问号
					whereSegments.add(Where.get(tableName2+"."+referenceInfo.getTargetCondition()[k], referenceInfo.getTargetCondition()[k+1]));
					values.add(referenceInfo.getTargetCondition()[k+1]);
				}
			}
			whereClause.append(" ").append(JoinOperator.LEFT.toString()).append(" ").append(assoRecord.getTableName()).append(" AS ").append(tableName2);
			whereClause.append(" ON ").append(Where.and(whereSegments.toArray(new WhereSegment[whereSegments.size()])).toPreparedStatementSql());
		}
		whereClause.append(" WHERE ").append(handleSqlColumnPrefix(condition.getFinalSegment().toPreparedStatementSql(), assoRecordLink));
		if (orders != null) {
			whereClause.append(" ORDER BY ").append(handleSqlColumnPrefix(orders, assoRecordLink));
		}
		condition.setWhereClause(whereClause.toString());
		values.addAll(condition.getValueList());
		condition.setValueClause(values.toArray(new Object[values.size()]));
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("joinRecord", joinRecord);
		resMap.put("assoRecordLink", assoRecordLink);
		resMap.put("associationFlags", associationFlags);
		return resMap;
	}
	
	/**
	 * 处理SQL子句的列名前缀，以区分来自不同的表
	 * 	1. 没有前缀则加上：entityClass#tableName_0.
	 *  2. 有前缀但没有下标则加上下标，如entityClass#tableName.处理成entityClass#tableName_0.
	 *     (关联链中有两个相同的类型时如果没有下标则加上第一次出现的下标，要指定后面类型的列必须带下标)
	 *  3. entityClass下标为0，associationLink的第一个元素下标为1，依次类批(即数组下标加一)
	 *  4. 出现的表名不能以'_数字'结尾
	 *
	 * @param sql              条件SQL语句
	 * @param associationLink  关联链
	 * @return
	 * 创建日期：2012-12-5
	 * 修改说明：
	 * @author wangk
	 */
	private String handleSqlColumnPrefix(String sql, Record[] associationLink) {
		if(sql == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int start = 0;
		int end = 0;
		for (int i = 0; i < sql.length(); i++) {
			char ch = sql.charAt(i);
			if(ch == '\'') {
				end = i;
				sb.append(handleSubSqlColumnPrefix(sql.substring(start, end), associationLink));
				start = end;
				for (i++; i < sql.length(); i++) {
					if(sql.charAt(i) == ch) {
						end = i + 1;
						sb.append(sql.substring(start, end));
						start = end;
						break;
					}
				}
			} else if(ch == '(' && sql.substring(i+1).trim().toUpperCase().startsWith("SELECT")) {
				end = i;
				sb.append(handleSubSqlColumnPrefix(sql.substring(start, end), associationLink));
				start = end;
				Stack<Character> stack = new Stack<Character>();
				stack.push(ch);
				for (i++; i < sql.length(); i++) {
					char ch2 = sql.charAt(i);
					if(ch2 == ch) {
						stack.push(ch);
					} else if(ch2 == ')') {
						stack.pop();
					}
					if(stack.isEmpty()) {
						end = i + 1;
						sb.append(sql.substring(start, end));
						start = end;
						break;
					}
				}
			}
		}
		end = sql.length();
		sb.append(handleSubSqlColumnPrefix(sql.substring(start, end), associationLink));
		return sb.toString().trim();
	}
	
	/**
	 * 处理一段SQL子句的列名前缀，不包含子查询和字符串数据
	 *
	 * @param subSql          一段SQL子句
	 * @param associationLink 关联链
	 * @return String         处理结果，结尾带空格
	 * 创建日期：2012-12-6
	 * 修改说明：
	 * @author wangk
	 */
	private String handleSubSqlColumnPrefix(String subSql, Record[] associationLink) {
		//处理组合符号（= -> #, < -> @, > -> &）
		subSql = subSql.replaceAll("\\s*<>\\s*", " @& ").replaceAll("\\s*<=\\s*", " @# ").replaceAll("\\s*>=\\s*", " &# ");
		for (String operator : CONDITION_OPERATORS) {
			//处理单个符号
			subSql = subSql.replaceAll("\\s*" + operator + "\\s*", " " + operator + " ");
		}
		subSql = subSql.replaceAll("@&", "<>").replaceAll("@#", "<=").replaceAll("&#", ">=");
		String[] strings = subSql.split("\\s+");
		StringBuilder sb = new StringBuilder();
		for (String string : strings) {
			if(string.matches("^[A-Za-z_][\\w]*(\\.\\w+)?$") && !CONDITION_KEYWORDS.contains(string.toUpperCase())) {
				sb.append(handleColumnPrefix(string, associationLink));
			} else {
				sb.append(string);
			}
			sb.append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * 处理列名前缀
	 *
	 * @param column           列名
	 * @param associationLink  关联链
	 * @return String          带前缀的列名
	 * 创建日期：2012-12-5
	 * 修改说明：
	 * @author wangk
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String handleColumnPrefix(String column, Record[] associationLink) {
		if(column.indexOf(".") < 0) {
			return entityRecordTemplate.getTableName() + "_0." + column;
		}
		String[] strings = column.split("\\.");
		String tableName = strings[0];
		if(!tableName.matches("^.*_\\d+$")) {
			int index = 0;
			if(!tableName.equalsIgnoreCase(entityRecordTemplate.getTableName())) {
				int i = 0;
				for (; i < associationLink.length; i++) {
					if(tableName.equalsIgnoreCase(associationLink[i].getTableName())) {
						break;
					}
				}
				index = i + 1;
			}
			return tableName + "_" + index + "." + strings[1];
		}
		return column;
	}

	/**
	 * 构建关联查询结果
	 * @param resultRecords
	 * @param assoRecordLink
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<E> fillAssociationRecord(List<Record> resultRecords, Class<?>[] associationLink, Record[] assoRecordLink, List<List<Object>> associationFlags) {
		if (resultRecords == null || resultRecords.size() == 0)
			return null;
		List<Record> templateRecords = new ArrayList<Record>();
		templateRecords.add(entityRecordTemplate);
		templateRecords.addAll(Arrays.asList(assoRecordLink));
		List<Class<?>> templateClass = new ArrayList<Class<?>>();
		templateClass.add(entityClass);
		templateClass.addAll(Arrays.asList(associationLink));
		// 1.分析结果
		List<List<EntityDM>> resultBeanColl = new ArrayList<List<EntityDM>>();
		List<List<Record>> resultRecordColl = new ArrayList<List<Record>>();
		int index = 0;
		for (Record templateRecord : templateRecords) {
			List<EntityDM> resultBeans = new ArrayList<EntityDM>();
			List<Record> uniqueResultRecords = new ArrayList<Record>();
			Record lastRecord = null;
			for (Record resultRecord : resultRecords) {
				Record record = new Record(templateRecord.getTableName());
				record.setKeyColumns(templateRecord.getKeyColumns());
				for (String column : templateRecord.getColumnList()) {
					record.set(column, resultRecord.get(templateRecord.getTableName()+"_"+index+"_"+column));
				}
				if (lastRecord == null || (lastRecord != null && !lastRecord.getKeys().containsAll(record.getKeys()))) {
					Class<? extends EntityDM> clazz = (Class<? extends EntityDM>)templateClass.get(index);
					EntityDM entity;
					try {
						entity = clazz.newInstance();
					} catch (Exception e1) {
						throw new DaoException("["+clazz.getName()+"]默认构造器实例化失败");
					}
					MappingAccessor.getInstance().fillRecord(entity, "", record);
					resultBeans.add(entity);
					uniqueResultRecords.add(record);
				}
				lastRecord = record;
			}
			resultBeanColl.add(resultBeans);
			resultRecordColl.add(uniqueResultRecords);
			index++;
		}
		// 2.构建关联
		index = 1;
		for (List<Object> associationFlag : associationFlags) {
			Integer _index_ = (Integer)associationFlag.get(0);
			if (templateClass.get(_index_) != associationFlag.get(1)) {
				throw new DaoException("结果集["+_index_+"]类型与被关联类型不一致");
			}
			ReferenceInfo referenceInfo = null;
			for (ReferenceInfo _referenceInfo_ : templateRecords.get(_index_).getReferenceInfo()) {
				if (_referenceInfo_.getTargetClass() == associationFlag.get(2)) {
					referenceInfo = _referenceInfo_;
					break;
				}
			}
			if (referenceInfo == null) 
				throw new DaoException("没有关联信息");
			
			for (int i=0; i<resultRecordColl.get(_index_).size(); i++) {
				List<EntityDM> subResultBeans = new ArrayList<EntityDM>();
				for (int j=0; j<resultRecordColl.get(index).size(); j++) {
					boolean isAss = true;
					for (int k=0; k<referenceInfo.getCurrentColumn().length; k++) {
						Object resultvalue1 = resultRecordColl.get(_index_).get(i).get(referenceInfo.getCurrentColumn()[k]);
						Object resultvalue2 = resultRecordColl.get(index).get(j).get(referenceInfo.getTargetColumn()[k]);
						if (resultvalue1 != null && resultvalue2 == null || resultvalue2 != null && resultvalue1 == null || resultvalue1 != null && !resultvalue1.equals(resultvalue2)) {
							isAss = false;
							break;
						}
					}
					if (isAss == true) {
						if (referenceInfo.getRelation() == Relation.OneToOne || referenceInfo.getRelation() == Relation.ManyToOne) {
							String fieldName = RecordUtils.getReferenceField((Class<? extends EntityDM>)templateClass.get(_index_), (Class<? extends EntityDM>)templateClass.get(index)).getName();
							ReflectionUtil.invokeSetterMethod(resultBeanColl.get(_index_).get(i), fieldName, resultBeanColl.get(index).get(j));
							break;
						}
						if (referenceInfo.getRelation() == Relation.OneToMany || referenceInfo.getRelation() == Relation.ManyToMany) {
							subResultBeans.add(resultBeanColl.get(index).get(j));
						}
					}
				}
				if (referenceInfo.getRelation() == Relation.OneToMany || referenceInfo.getRelation() == Relation.ManyToMany) {
					Field field = RecordUtils.getReferenceField((Class<? extends EntityDM>)templateClass.get(_index_), (Class<? extends EntityDM>)templateClass.get(index));
					if (field.getType().isArray()) {
						ReflectionUtil.invokeSetterMethod(resultBeanColl.get(_index_).get(i), field.getName(), subResultBeans.toArray());
					}
					if (List.class.isAssignableFrom(field.getType())) {
						ReflectionUtil.invokeSetterMethod(resultBeanColl.get(_index_).get(i), field.getName(), Lists.newArrayList(subResultBeans),List.class);
					}
					subResultBeans.clear();
				}
			}
			index++;
		}
		
		return (List<E>) resultBeanColl.get(0);
	}
	
	
	
}
