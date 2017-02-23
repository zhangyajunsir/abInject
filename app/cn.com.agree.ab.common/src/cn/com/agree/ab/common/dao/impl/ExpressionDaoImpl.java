package cn.com.agree.ab.common.dao.impl;

import javax.inject.Singleton;
import cn.com.agree.ab.common.dao.ExpressionDao;
import cn.com.agree.ab.common.dao.entity.ExpressionEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ExpressionDao.class)
@Singleton
@Dao("expressionDao")
public class ExpressionDaoImpl extends EntityDaoImpl<ExpressionEntity> implements ExpressionDao {

	@Override
	public ExpressionEntity findExpression(Integer ID) {
		
		WhereSegment ws = Where.and(Where.get("id", ID), Where.get("available", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), ws);
		ExpressionEntity expressionEntity = get(condition);
		return expressionEntity;
	}

}
