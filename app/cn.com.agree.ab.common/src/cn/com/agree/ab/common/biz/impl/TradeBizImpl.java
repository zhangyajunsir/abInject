package cn.com.agree.ab.common.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.TellerBiz;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.dao.CommDao;
import cn.com.agree.ab.common.dao.TradeDao;
import cn.com.agree.ab.common.dao.entity.CommCodeEntity;
import cn.com.agree.ab.common.dao.entity.TradeCodeEntity;
import cn.com.agree.ab.common.dao.entity.TradePostEntity;
import cn.com.agree.ab.common.dao.entity.TradePropEntity;
import cn.com.agree.ab.common.dao.entity.TradeRelationsEntity;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.TellerDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.common.dm.TradeRelationsDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = TradeBiz.class)
@Singleton
@Biz("tradeBiz")
public class TradeBizImpl implements TradeBiz {
	@Inject
	@Named("tradeDao")
	protected TradeDao tradeDao;
	@Inject
	@Named("tellerBiz")
	protected TellerBiz tellerBiz;
	@Inject
	@Named("commDao")
	protected CommDao commDao;

	@Override
	@Cacheable
	public List<TradeCodeDM> findTradeCode(String tradeCode) { 
		if (tradeCode == null || "".equals(tradeCode))
			throw new BizException(ExceptionLevel.ERROR, "交易码不可为空");
		List<TradeCodeEntity> tradeCodeEntitys = tradeDao.findTradeCode(tradeCode);	
		if(tradeCodeEntitys == null)
			return null;
		List<TradeCodeDM> tradeCodeDMs = new ArrayList<TradeCodeDM>();
		for (TradeCodeEntity tradeCodeEntity : tradeCodeEntitys) {
			TradeCodeDM tradeCodeDM = new TradeCodeDM();
			tradeCodeDM.cloneValueFrom(tradeCodeEntity);// 只会复制相同名称的栏位（支持深度复制）
			// 手动解析，解决tradeCodeDM中List<Integer>与tradeCodeEntity中List<Bean>的类型不匹配的解析问题
			List<TradePropEntity> propEntityList = tradeCodeEntity.getTradePropList();
			for(int i=0; i<propEntityList.size(); i++) {
				List<Integer> tradePosts = new ArrayList<Integer>();
				for(TradePostEntity tradePostEntity : propEntityList.get(i).getTradePostList()) {
					tradePosts.add(tradePostEntity.getPostId());
				}
				tradeCodeDM.getTradePropList().get(i).setTradePostLists(tradePosts);
			}
			tradeCodeDMs.add(tradeCodeDM);
		}
		
		return tradeCodeDMs;
	}

	@Override
	public TradeCodeDM findTradeCode(int expressionID, String tradeCode) {
		if (tradeCode == null || "".equals(tradeCode))
			throw new BizException(ExceptionLevel.ERROR, "交易码不可为空");
		List<TradeCodeDM> tradeCodeDMs = findTradeCode(tradeCode);
		if (tradeCodeDMs == null)
			return null;
		for (TradeCodeDM tradeCodeDM : tradeCodeDMs) {
			if (tradeCodeDM.getExpressionid() == expressionID)
				return tradeCodeDM;
		}
		return null;
	}
	
	/**
	 * 只有当前表的数据，不会连表查询
	 */
	@Cacheable
	public Map<Integer, TradeCodeDM> findAllTradeCode() {
		Map<Integer, TradeCodeDM> tradeCodeDMs = new HashMap<Integer, TradeCodeDM>();
		List<TradeCodeEntity> tradeCodeEntitys = tradeDao.findAllTradeCode();
		if(tradeCodeEntitys == null)
			return null;
		for (TradeCodeEntity tradeCodeEntity : tradeCodeEntitys) {
			TradeCodeDM tradeCodeDM = new TradeCodeDM();
			tradeCodeDM.cloneValueFrom(tradeCodeEntity);
			tradeCodeDMs.put(tradeCodeDM.getId(), tradeCodeDM);
		}
		return tradeCodeDMs;
	}
	 
	public TradeCodeDM findTradeCode(int id) {
		return findAllTradeCode().get(id);
	}

	@Override
	public TradePropDM findTradeProp(int expressionID, String tradeCode, String commCode) {
		if (commCode == null || "".equals(commCode))
			throw new BizException(ExceptionLevel.ERROR, "通讯码不可为空");
		for (TradePropDM tradePropDM : findTradeCode(expressionID, tradeCode).getTradePropList()) {
			if (commCode.equals(tradePropDM.getCommCode().getCommCode()))
				return tradePropDM;
		}
		return null;
	}

	@Override
	public boolean checkTradePermission(int expressionID, String tradeCode, String commCode, String teller) {
		boolean isPermission = true;
		if (teller == null || "".equals(teller))
			return isPermission;
		//获取TellerPostEntity中需要的属性，在getTeller()方法中做list<E>的手动解析
		TellerDM tellerDM = tellerBiz.getTeller(teller);
		List<Integer> tellerPost = null;
		if (tellerDM != null)
			tellerPost = tellerDM.getTellerPosts();
		if (tellerPost != null && tellerPost.size() > 0) {
			TradeCodeDM tradeCodeDM = findTradeCode(expressionID, tradeCode);
			if (tradeCodeDM == null)
				return isPermission;
			Set<Integer> allTradePost = new HashSet<Integer>();
			for (TradePropDM tradePropDM : tradeCodeDM.getTradePropList()) {
				if (commCode != null) {
					if (commCode.equals(tradePropDM.getCommCode().getCommCode()))
						allTradePost.addAll(tradePropDM.getTradePosts());
				} else
					allTradePost.addAll(tradePropDM.getTradePosts());
			}
			if (allTradePost.size() > 0) {
				isPermission = false;
				for (int postId : tellerPost) {
					if (allTradePost.contains(postId)) {
						isPermission = true;
						break;
					}
				}
			}
		}
		return isPermission;
	}

	@Override
	@Cacheable
	public CommCodeDM findCommCode(String commCode) {
		if (commCode == null || "".equals(commCode))
			throw new BizException(ExceptionLevel.ERROR, "通讯码不可为空");
		CommCodeEntity commCodeEntity = commDao.findCommCode(commCode);
		if (commCodeEntity == null) {
			return null;
		}
		
		CommCodeDM commCodeDM = new CommCodeDM();
		commCodeDM.cloneValueFrom(commCodeEntity);
		return commCodeDM;
	}

	@Override
	@Cacheable
	public List<TradeRelationsDM> getRelationMapping(Integer aTradeID, Integer bTradeID) {
		List<TradeRelationsEntity> tradeRelationsEntitys = tradeDao.getRelation(aTradeID, bTradeID);	
		if(tradeRelationsEntitys == null)
			return null;
		List<TradeRelationsDM> tradeRelationsDMs = new ArrayList<TradeRelationsDM>();
		for (TradeRelationsEntity tradeRelationsEntity : tradeRelationsEntitys) {
			TradeRelationsDM tradeRelationsDM = new TradeRelationsDM();
			tradeRelationsDM.cloneValueFrom(tradeRelationsEntity);
			tradeRelationsDM.setATradeId(tradeRelationsEntity.getATradeId());
			tradeRelationsDM.setATradeComps(tradeRelationsEntity.getATradeComps());
			tradeRelationsDM.setBTradeId(tradeRelationsEntity.getBTradeId());
			tradeRelationsDM.setBTradeComps(tradeRelationsEntity.getBTradeComps());
			tradeRelationsDMs.add(tradeRelationsDM);
		}
		return tradeRelationsDMs;
	}

}
