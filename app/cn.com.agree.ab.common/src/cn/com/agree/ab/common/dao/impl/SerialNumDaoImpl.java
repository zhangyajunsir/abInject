package cn.com.agree.ab.common.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.SerialNumDao;
import cn.com.agree.ab.common.dao.entity.TellerFlowEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.impl.BasicDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = SerialNumDao.class)
@Singleton
@Dao("serialNumDao")
public class SerialNumDaoImpl extends BasicDaoImpl implements SerialNumDao {
	
	public TellerFlowEntity findTellerFlowNum(String teller, String txDate, int flowType) {
		String sql = "SELECT id, teller_code, trade_date, flow_type, serial_num, available, last_modify_user, last_modify_date FROM ab_teller_flow WHERE teller_code = ? AND trade_date = ? AND flow_type = ? AND available = ?";
		List<TellerFlowEntity> tellerFlowEntitys =  search(sql, new RecordMapper<TellerFlowEntity>() {
			@Override
			public TellerFlowEntity recordRow(Record record, int index) { 
				TellerFlowEntity entity;
				try {
					entity = TellerFlowEntity.class.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+TellerFlowEntity.class.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, teller, txDate, flowType, 1);
		if (tellerFlowEntitys != null && tellerFlowEntitys.size() > 0)
			return tellerFlowEntitys.get(0);
		return null;
	}
	
	public void insertTellerFlow(TellerFlowEntity tellerFlowEntity) {
		try {
			persistence.insert(tellerFlowEntity);
		} catch (SQLException e) {
			throw new DaoException("新增表[ab_teller_flow]记录失败", e);
		}
	}
	
	public void updateTellerFlow(TellerFlowEntity tellerFlowEntity) {
		try {
			persistence.update(tellerFlowEntity);
		} catch (SQLException e) {
			throw new DaoException("更新表[ab_teller_flow]记录失败", e);
		}
	}
	
}
