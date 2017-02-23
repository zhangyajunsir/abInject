package cn.com.agree.ab.common.dao.impl;

import java.util.List;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.ABServerDao;
import cn.com.agree.ab.common.dao.entity.ABServerEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ABServerDao.class)
@Singleton
@Dao("abserverDao")
public class ABServerDaoImpl extends EntityDaoImpl<ABServerEntity> implements ABServerDao {
	
	public ABServerEntity findABServer(String hostName) {
		WhereSegment ws = Where.and(Where.get("host_name", hostName), Where.get("available", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), ws);
		return get(condition);
	}
	
	public List<ABServerEntity> queryAllABServer() {
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.get("available", 1));
		return query(condition);
	}
	
}
