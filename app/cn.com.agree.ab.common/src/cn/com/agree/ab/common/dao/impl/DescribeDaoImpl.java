package cn.com.agree.ab.common.dao.impl;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.DescribeDao;
import cn.com.agree.ab.common.dao.entity.DescribeEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = DescribeDao.class)
@Singleton
@Dao("describeDao")
public class DescribeDaoImpl extends EntityDaoImpl<DescribeEntity> implements DescribeDao {


	@Override
	public DescribeEntity findDescribe(Integer ID) {
		
		WhereSegment ws = Where.and(Where.get("id", ID), Where.get("available", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), ws);
		DescribeEntity describeEntity = get(condition);
		return describeEntity;
	}

}
