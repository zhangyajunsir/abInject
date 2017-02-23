package jsrccb.common.biz.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import jsrccb.common.dm.PostCfgDM;
import jsrccb.common.dm.PostCfgDM.PostCfgItem;
import cn.com.agree.ab.common.dao.entity.TradePostEntity;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Transaction;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.inject.annotations.AutoBindMapper;
import cn.com.agree.inject.annotations.AutoBindMappers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@AutoBindMappers({
	@AutoBindMapper(baseClass = cn.com.agree.ab.common.biz.TradeBiz.class), 
	@AutoBindMapper(baseClass = jsrccb.common.biz.TradeBiz.class)
})
@Singleton
@Biz("jsrccbTradeBiz")
public class TradeBizImpl extends cn.com.agree.ab.common.biz.impl.TradeBizImpl implements jsrccb.common.biz.TradeBiz {

	@Transaction
	public void updateTradePostFromCfg(PostCfgDM postCfgDM) {
		if (postCfgDM == null)
			throw new BizException(ExceptionLevel.ERROR, "postCfgDM is null");
		Map<String, List<PostCfgItem>> tradeCfgPosts = postCfgDM.getTradeCfgPosts();
		for (String tradeCode : tradeCfgPosts.keySet()) {
			List<PostCfgItem>          postCfgItems = tradeCfgPosts.get(tradeCode);
			List<Integer>              defaultPosts = null;
			Map<String, List<Integer>> funcPostsMap = Maps.newHashMap();
			for (PostCfgItem postCfgItem : postCfgItems) {
				if (postCfgItem.getFuncValue().equals("")) {
					defaultPosts = postCfgItem.getPosts();
				}
				funcPostsMap.put(postCfgItem.getFuncValue(), postCfgItem.getPosts());
			}
			
			List<TradeCodeDM> tradeCodeDMs = findTradeCode(tradeCode); // 相同交易码，可能存在多个实际前端交易
			if (tradeCodeDMs == null || tradeCodeDMs.isEmpty()) 
				continue;
			
			for (TradeCodeDM tradeCodeDM : tradeCodeDMs) {
				if (tradeCodeDM.getTradePropList() == null || tradeCodeDM.getTradePropList().isEmpty())
					continue;
				for (TradePropDM tradePropDM : tradeCodeDM.getTradePropList()) {
					String propFuncValue = tradePropDM.getFuncCodeValue();
					if (propFuncValue == null)
						propFuncValue =  "";
					List<Integer> posts  = funcPostsMap.get(propFuncValue);
					if (posts == null)
						posts = defaultPosts;
					
					List<Integer> delPosts = Lists.newArrayList();
					delPosts.addAll(tradePropDM.getTradePosts());
					delPosts.removeAll(posts);
					for (Integer postId : delPosts) {
						TradePostEntity tradePostEntity = new TradePostEntity();
						tradePostEntity.setTradeId(tradePropDM.getTradeId());
						tradePostEntity.setCommId (tradePropDM.getCommId());
						tradePostEntity.setPostId (postId);
						tradeDao.deleteTradePost(tradePostEntity);
					}
					
					List<Integer> addPosts = Lists.newArrayList();
					addPosts.addAll(posts);
					addPosts.remove(tradePropDM.getTradePosts());
					for (Integer postId : addPosts) {
						TradePostEntity tradePostEntity = new TradePostEntity();
						tradePostEntity.setTradeId(tradePropDM.getTradeId());
						tradePostEntity.setCommId (tradePropDM.getCommId());
						tradePostEntity.setPostId (postId);
						tradePostEntity.setAvailable(1);
						tradePostEntity.setLastModifyDate(new Date());
						tradePostEntity.setLastModifyUser("admin");
						tradeDao.saveOrUpdateTradePost(tradePostEntity);
					}
				}
			}
		}
	}
	
	
}
