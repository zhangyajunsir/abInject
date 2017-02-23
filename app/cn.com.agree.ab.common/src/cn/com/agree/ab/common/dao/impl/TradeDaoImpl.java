package cn.com.agree.ab.common.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.TradeDao;
import cn.com.agree.ab.common.dao.entity.CommCodeEntity;
import cn.com.agree.ab.common.dao.entity.TradeCodeEntity;
import cn.com.agree.ab.common.dao.entity.TradePostEntity;
import cn.com.agree.ab.common.dao.entity.TradePropEntity;
import cn.com.agree.ab.common.dao.entity.TradeRelationsEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = TradeDao.class)
@Singleton
@Dao("tradeDao")
public class TradeDaoImpl extends EntityDaoImpl<TradeCodeEntity> implements TradeDao {

	@Override
	public List<TradeCodeEntity> findTradeCode(String tradeCode) {
		List<WhereSegment> wss = new ArrayList<Where.WhereSegment>();
		wss.add(Where.get("code", tradeCode));
		wss.add(Where.get("visiable", 1));
		/* 不能这么写，只能放到@Reference连表条件里 
		wss.add(Where.get("ab_trade_prop_1.available", 1));
		wss.add(Where.get("ab_comm_code_2.available", 1));
		wss.add(Where.get("ab_trade_post_3.available", 1));
		*/
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.and(wss.toArray(new WhereSegment[wss.size()])));
		List<TradeCodeEntity> tradeCodeEntitys = query(condition, TradePropEntity.class, CommCodeEntity.class, TradePostEntity.class);
		return tradeCodeEntitys;

	}
	
	public List<TradeCodeEntity> findAllTradeCode() {
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.get("visiable", 1));
		List<TradeCodeEntity> tradeCodeEntitys = query(condition);
		return tradeCodeEntitys;
	}
	
	public TradeCodeEntity findTradeCode(int id) {
		return get(id, TradePropEntity.class, CommCodeEntity.class, TradePostEntity.class);
	}

	@Override
	public List<TradeRelationsEntity> getRelation(Integer aTradeId, Integer bTradeId) {
		String sql = "select * from ab_trade_relations where (a_trade_id= ? or a_trade_id= ?) and (b_trade_id= ? or b_trade_id= ?) and available = ? order by id";
		
		return search(sql, new RecordMapper<TradeRelationsEntity>() {
			@Override
			public TradeRelationsEntity recordRow(Record record, int index) { 
				TradeRelationsEntity entity;
				try {
					entity = TradeRelationsEntity.class.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+TradeRelationsEntity.class.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, aTradeId, 0, bTradeId, 0, 1);
	}

	public void deleteTradePost(TradePostEntity tradePostEntity) {
		if (tradePostEntity == null)
			throw new DaoException("tradePostEntity is null");
		try {
			if (tradePostEntity.getId() != null && tradePostEntity.getId() != 0) {
				persistence.sql("update ab_trade_post set available = 0 where id = ?", tradePostEntity.getId());
				return;
			}
			if (tradePostEntity.getTradeId() != null && tradePostEntity.getTradeId() != 0
					&& tradePostEntity.getCommId() != null && tradePostEntity.getCommId() != 0
					&& tradePostEntity.getPostId() != null && tradePostEntity.getPostId() != 0) {
				persistence.sql("update ab_trade_post set available = 0 where trade_id = ? and comm_id = ? and post_id = ?", 
						tradePostEntity.getTradeId(), tradePostEntity.getCommId(), tradePostEntity.getPostId());
				return;
			} 
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
		throw new DaoException("tradePostEntity里Id为空或者TradeId、CommId和PostId为空");
	}
	
	public void saveOrUpdateTradePost(TradePostEntity tradePostEntity) {
		if (tradePostEntity == null)
			throw new DaoException("tradePostEntity is null");
		if (tradePostEntity.getTradeId() != null && tradePostEntity.getTradeId() != 0
				&& tradePostEntity.getCommId() != null && tradePostEntity.getCommId() != 0
				&& tradePostEntity.getPostId() != null && tradePostEntity.getPostId() != 0) {
			throw new DaoException("tradePostEntity里TradeId、CommId和PostId为空");
		}
		try {
			List<Record> records = persistence.sqlForList("select * from ab_trade_post where trade_id = ? and comm_id = ? and post_id = ?", 
					tradePostEntity.getTradeId(), tradePostEntity.getCommId(), tradePostEntity.getPostId());
			if (records == null || records.size() == 0) {
				persistence.insert(tradePostEntity);
			} else {
				tradePostEntity.setId((Integer)records.get(0).get("id"));
				persistence.update(tradePostEntity);
			}
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}
	
}
