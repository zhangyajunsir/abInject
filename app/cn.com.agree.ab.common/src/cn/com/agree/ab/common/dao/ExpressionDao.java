package cn.com.agree.ab.common.dao;

import cn.com.agree.ab.common.dao.entity.ExpressionEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface ExpressionDao extends EntityDao<ExpressionEntity> {
	
	public ExpressionEntity findExpression(Integer ID);
}
