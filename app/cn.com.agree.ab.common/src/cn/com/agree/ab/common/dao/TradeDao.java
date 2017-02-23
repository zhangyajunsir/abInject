package cn.com.agree.ab.common.dao;

import java.util.List;

import cn.com.agree.ab.common.dao.entity.TradeCodeEntity;
import cn.com.agree.ab.common.dao.entity.TradePostEntity;
import cn.com.agree.ab.common.dao.entity.TradeRelationsEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface TradeDao extends EntityDao<TradeCodeEntity> {

	public List<TradeCodeEntity> findTradeCode(String tradeCode);
	
	public List<TradeCodeEntity> findAllTradeCode();
	
	public TradeCodeEntity findTradeCode(int id);
	
	public List<TradeRelationsEntity> getRelation(Integer aTradeId, Integer bTradeId);
	
	public void deleteTradePost(TradePostEntity tradePostEntity);
	
	public void saveOrUpdateTradePost(TradePostEntity tradePostEntity);
}
