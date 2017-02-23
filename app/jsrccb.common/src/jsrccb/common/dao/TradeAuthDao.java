package jsrccb.common.dao;

import java.util.List;

import jsrccb.common.dao.entity.TradeAuthEntity;
import jsrccb.common.dao.entity.TradeAuthQuotaEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface TradeAuthDao extends EntityDao<TradeAuthEntity> {
	
	public TradeAuthEntity findTradeAuth(Integer tradeId, Integer commId, String authType, Integer expressionId);
	
	public List<TradeAuthEntity> findTradeAuths(Integer tradeId, Integer commId);
	
	public List<TradeAuthQuotaEntity> findAllTradeAuthQuota();
}
