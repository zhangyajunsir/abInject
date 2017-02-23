package cn.com.agree.ab.common.dao.impl;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.OrgDao;
import cn.com.agree.ab.common.dao.entity.OrgEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = OrgDao.class)
@Singleton
@Dao("orgDao")
public class OrgDaoImpl extends EntityDaoImpl<OrgEntity> implements OrgDao {
	
	public OrgEntity findOrg(String orgCode){
		if(orgCode == null){
			return null;
		}
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.and(Where.get("code", orgCode), Where.get("available", 1)));
		OrgEntity orgEntity = get(condition);
		return orgEntity;
	}
}
