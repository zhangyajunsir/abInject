package cn.com.agree.ab.common.dao.impl;


import java.sql.SQLException;
import java.util.List;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.TellerDao;
import cn.com.agree.ab.common.dao.entity.TellerEntity;
import cn.com.agree.ab.common.dao.entity.TellerPostEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = TellerDao.class)
@Singleton
@Dao("tellerDao")
public class TellerDaoImpl extends EntityDaoImpl<TellerEntity> implements TellerDao {


	@Override
	public TellerEntity getTeller(String tellerCode) {
		WhereSegment ws = Where.and(Where.get("code", tellerCode), Where.get("available", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), ws);
		TellerEntity tellerEntity = get(condition, TellerPostEntity.class);
		return tellerEntity;
	}
	
	public List<TellerPostEntity> getTellerAllPost(String tellerCode) {
		String sql = "SELECT id, teller_code, post_id, available, last_modify_user, last_modify_date FROM ab_teller_post WHERE teller_code = ?";
		
		return search(sql, new RecordMapper<TellerPostEntity>() {
			@Override
			public TellerPostEntity recordRow(Record record, int index) { 
				TellerPostEntity entity;
				try {
					entity = TellerPostEntity.class.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+TellerPostEntity.class.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, tellerCode);
	}

	public void addTellerPost(TellerPostEntity tellerPostEntity) {
		try {
			persistence.insert(tellerPostEntity);
		} catch (SQLException e) {
			throw new DaoException("新增表[ab_teller_post]记录失败", e);
		}
	}
	
	public void updTellerPost(TellerPostEntity tellerPostEntity) {
		try {
			persistence.update(tellerPostEntity);
		} catch (SQLException e) {
			throw new DaoException("更新表[ab_teller_post]记录失败", e);
		}
	}
}
