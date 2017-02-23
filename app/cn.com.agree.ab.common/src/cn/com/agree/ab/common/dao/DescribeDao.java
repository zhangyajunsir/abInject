package cn.com.agree.ab.common.dao;

import cn.com.agree.ab.common.dao.entity.DescribeEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface DescribeDao extends EntityDao<DescribeEntity> {

	public DescribeEntity findDescribe(Integer ID);
}
