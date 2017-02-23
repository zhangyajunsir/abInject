package cn.com.agree.ab.common.biz;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.dao.ExpressionDao;
import cn.com.agree.ab.common.dao.entity.ExpressionEntity;
import cn.com.agree.ab.common.dm.ExpressionDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.exception.ExpressionException;
import cn.com.agree.ab.lib.cache.Cache;
import cn.com.agree.ab.lib.cache.CacheManager;
import cn.com.agree.ab.lib.exception.BizException;

/**
 * 此类功能类似工厂模式
 * 不需要预先绑定，但需要添加@Singleton代表单例
 * @author zhangyajun
 */
@Singleton
public class ExpressionBizProvider {
	private static final Logger	logger	= LoggerFactory.getLogger(ExpressionBizProvider.class);
	@Inject
	private Set<ExpressionBiz> expressionBizs;
	@Inject
	@Named("expressionDao")
	private ExpressionDao expressionDao;
	/** 
	 * GuavaCacheManager具备60分钟TTL策略
	 * */
	@Inject
	@Named("guavaCacheManager")
    private CacheManager cacheManager;
	
	
	
	@SuppressWarnings("unchecked")
	public <T> T executeExpression(int expressionID, TradeDataDM tradeDataDM) {
		ExpressionDM expressionDM = null;
		Cache cache = cacheManager.getCache("cache_expression");
		if (cache.get(expressionID) != null) {
			expressionDM = (ExpressionDM)cache.get(expressionID).get();
		} else {
			synchronized (cache) {
				if (cache.get(expressionID) != null) {
					// 二重判断
					expressionDM = (ExpressionDM)cache.get(expressionID).get();
				} else {
					ExpressionEntity expressionEntity = expressionDao.findExpression(expressionID);
					if (expressionEntity == null)
						return null;
					expressionDM = new ExpressionDM();
					expressionDM.cloneValueFrom(expressionEntity);
					cache.put(expressionID, expressionDM);
				}
			}
		}
		ExpressionBiz expressionBiz = null;
		for (ExpressionBiz _expressionBiz_ : expressionBizs) {
			if (_expressionBiz_.type().equals(expressionDM.getExpressionType())) {
				expressionBiz = _expressionBiz_;
				break;
			}
		}
		if (expressionBiz == null)
			return null;
		T res = null;
		try {
			res = (T)expressionBiz.execute(expressionDM, tradeDataDM);
			logger.debug("执行{}脚本:[{}]结果:[{}]", expressionDM.getExpressionType(), expressionDM.getExpression(), res);
		} catch (ExpressionException e) {
			throw new BizException("执行脚本["+expressionDM.getExpression()+"]发生异常", e);
		}
		return res;
	}
	
	
}
