package cn.com.agree.ab.common.biz.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.DescribeBiz;
import cn.com.agree.ab.common.dao.DescribeDao;
import cn.com.agree.ab.common.dao.entity.DescribeEntity;
import cn.com.agree.ab.common.dm.DescribeDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = DescribeBiz.class)
@Singleton
@Biz("describeBiz")
public class DescribeBizImpl implements DescribeBiz {
	@Inject
	@Named("describeDao")
	private DescribeDao describeDao;

	@Override
	@Cacheable
	public DescribeDM findDescribe(Integer ID) {
		DescribeEntity describeEntity = describeDao.findDescribe(ID);
		if (describeEntity == null)
			return null;
		DescribeDM describeDM = new DescribeDM();
		describeDM.cloneValueFrom(describeEntity);
		return describeDM;
	}

}
