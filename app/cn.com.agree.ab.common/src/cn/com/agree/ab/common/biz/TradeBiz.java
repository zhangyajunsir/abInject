package cn.com.agree.ab.common.biz;

import java.util.List;
import java.util.Map;

import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.common.dm.TradeRelationsDM;

public interface TradeBiz {
	/**
	 * 查询交易码对象列表
	 * @param tradeCode
	 * @return
	 */
	public List<TradeCodeDM> findTradeCode(String tradeCode);
	/**
	 * 查询具体交易信息
	 * @param expressionID 表达式id
	 * @param tradeCode    交易码
	 * @return
	 */
	public TradeCodeDM findTradeCode    (int expressionID, String tradeCode);
	/**
	 * 查询交易信息（不连表查询）
	 * @return
	 */
	public Map<Integer, TradeCodeDM> findAllTradeCode();
	public TradeCodeDM findTradeCode    (int id);
	/**
	 * 查询具体交易属性信息
	 * @param expressionID 表达式id
	 * @param tradeCode    交易码
	 * @param commCode     通讯码
	 * @return
	 */
	public TradePropDM findTradeProp    (int expressionID, String tradeCode, String commCode); 
	/**
	 * 查询具体通讯信息
	 * @param commCode     通讯码
	 * @return
	 */
	public CommCodeDM  findCommCode     (String commCode);
	/**
	 * 校验权限
	 * @param expressionID 表达式id
	 * @param tradeCode    交易码
	 * @param commCode     通讯码
	 * @param teller       柜员号
	 * @return
	 */
	public boolean checkTradePermission (int expressionID, String tradeCode, String commCode, String teller);
	/**
	 * 交易间关联配置
	 * @param aTradeID     A交易ID
	 * @param bTradeID     B交易ID
	 * @return
	 */
	public List<TradeRelationsDM> getRelationMapping(Integer aTradeID, Integer bTradeID);
	
}
