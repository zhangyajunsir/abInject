package jsrccb.common.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import jsrccb.common.dao.TradeAuthDao;
import jsrccb.common.dao.entity.TradeAuthEntity;
import jsrccb.common.dao.entity.TradeAuthQuotaEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = TradeAuthDao.class)
@Singleton
@Dao("tradeAuthDao")
public class TradeAuthDaoImpl extends EntityDaoImpl<TradeAuthEntity> implements TradeAuthDao {
	@Override
	public TradeAuthEntity findTradeAuth(Integer tradeId, Integer commId, String authType, Integer expressionId) {
		List<WhereSegment> wss = new ArrayList<Where.WhereSegment>();
		wss.add(Where.get("trade_id", tradeId));
		wss.add(Where.get("comm_id", commId));
		wss.add(Where.get("type", authType));
		wss.add(Where.get("expression_id", expressionId));
		wss.add(Where.get("visiable", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.and(wss.toArray(new WhereSegment[wss.size()])));
		return get(condition);
	}
	
	@Override
	public List<TradeAuthEntity> findTradeAuths(Integer tradeId, Integer commId) {
		List<WhereSegment> wss = new ArrayList<Where.WhereSegment>();
		wss.add(Where.get("trade_id", tradeId));
		wss.add(Where.get("comm_id", commId));
		wss.add(Where.get("visiable", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.and(wss.toArray(new WhereSegment[wss.size()])));
		return query(condition);
	}
	
	@Override
	public List<TradeAuthQuotaEntity> findAllTradeAuthQuota() {
		String sql = "SELECT id, quota_type, quota_cny, quota_min, quota_max, quota_level, available, last_modify_user, last_modify_date FROM ab_trade_auth_quota WHERE available = ?";
		
		return search(sql, new RecordMapper<TradeAuthQuotaEntity>() {
			@Override
			public TradeAuthQuotaEntity recordRow(Record record, int index) {
				TradeAuthQuotaEntity entity;
				try {
					entity = TradeAuthQuotaEntity.class.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+TradeAuthQuotaEntity.class.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, 1);
	}
	
}
