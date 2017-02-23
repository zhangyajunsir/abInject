package jsrccb.common.biz.impl.auth.check;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.AuthBiz;
import jsrccb.common.biz.AuthCheckBiz;
import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthLevel;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthType;
import jsrccb.common.dm.TradeAuthDM;
import jsrccb.common.dm.TradeAuthQuotaDM;
import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.DescribeBiz;
import cn.com.agree.ab.common.biz.ExpressionBizProvider;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.DescribeDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AuthCheckBiz.class, multiple = true)
@Singleton
@Biz("quotaAuthCheckBiz")
public class QuotaAuthCheckBizImpl implements AuthCheckBiz {
	@Inject
	@Named("authBiz")
	private AuthBiz authBiz;
	@Inject
	@Named("tradeBiz")
	private TradeBiz tradeBiz;
	@Inject
	private ExpressionBizProvider expressionBizProvider;
	@Inject
	@Named("describeBiz")
	private DescribeBiz describeBiz;
	

	@Override
	public AuthType getAuthType() {
		return AuthType.MONEY_AUTH;
	}

	@Override
	public AuthDM check(TradeDataDM tradeDataDM) {
		TradeCodeDM tradeCodeDM = (TradeCodeDM) tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
		CommCodeDM  commCodeDM = tradeBiz.findCommCode((String)tradeDataDM.getStoreData().get(ITradeKeys.T_COMM_CODE));
		AuthDM authDM = new AuthDM();
		if (tradeCodeDM == null || commCodeDM == null) {
			authDM.setAuthStatus(AuthStatus.NO_AUTH);
			return authDM;
		}
		
		AuthLevel authLevel = null;
		StringBuilder authMsg = new StringBuilder("〖限额授权〗通讯码["+tradeDataDM.getStoreData().get(ITradeKeys.T_COMM_CODE)+"]");
		List<TradeAuthDM> tradeAuthDMs = authBiz.findTradeAuthDM(tradeCodeDM.getId(), commCodeDM.getId(), AuthType.MONEY_AUTH);
		for (TradeAuthDM tradeAuthDM : tradeAuthDMs) {
			String expression = expressionBizProvider.executeExpression(tradeAuthDM.getExpressionId(), tradeDataDM);
			if (expression != null) {
				AuthLevel _authLevel_ = filterQuotaAuthLevel(expression, tradeDataDM);
				if (_authLevel_ != null) {
					DescribeDM describeDM = describeBiz.findDescribe(tradeAuthDM.getDescribeId());
					authMsg.append(describeDM == null || describeDM.getDescription() == null ? "" : describeDM.getDescription()).append(",");
					if (authLevel == null || authLevel.getLevel() < _authLevel_.getLevel()) {
						authLevel = _authLevel_;
					}
				}
			}
		}
		authMsg.deleteCharAt(authMsg.length()-1); 
		if (authLevel == null) 
			authDM.setAuthStatus(AuthStatus.NO_AUTH);
		else {
			authDM.setAuthStatus(AuthStatus.NEED_AUTH);
			authDM.setAuthLevel(authLevel);
			authDM.getAuthType().add(AuthType.MONEY_AUTH);
			authDM.getAuthMSG() .add(authMsg.toString());
		}
		return authDM;
	}
	
	
	private AuthLevel filterQuotaAuthLevel(String expression, TradeDataDM tradeDataDM) {
		Map<String, Object> uiData = tradeDataDM.getUiData();
		/**
		 * 金额授权expression规则: UI KEY包含|
		 * 对公对私,现金转账,借贷标志,币种,金额字段
		 * 其中除金额字段配置小于4时即为默认值,否则从交易取值
		 */
		String fields[] = expression.split(",", -1);
		if (fields.length < 5){
			throw new BizException("该交易金额授权表达式配置错误");
		}
		
		String tradeType = fields[0];	//对公对私
		if (null == tradeType || "".equals(tradeType)) {
			throw new BizException("该交易金额授权表中对公对私标志未配置");
		} 
		if (tradeType.length() > 4){
			if (uiData.containsKey(tradeType)){
				tradeType = (String)uiData.get(tradeType);
			} else {
				throw new BizException("未取到交易字段["+tradeType+"]值");
			}
		} else {
			tradeType = "*";
		}
		String cashType = fields[1];	//现金转账
		if (null == cashType || "".equals(cashType)) {
			throw new BizException("该交易金额授权表中现转标志未配置");
		} 
		if (cashType.length() > 4){
			if (uiData.containsKey(cashType+"|prefix")){
				cashType = (String) uiData.get(cashType+"|prefix");
			} else {
				throw new BizException("未取到交易字段["+cashType+"]值");
			}
		} else {
			cashType = "*";
		}
		String dcType  = fields[2];		//借贷标志
		if (null == dcType || "".equals(dcType)) {
			throw new BizException("该交易金额授权表中借贷标志未配置");
		} 
		if (dcType.length() > 4){
			if (uiData.containsKey(dcType)){
				dcType = (String) uiData.get(dcType);
			} else {
				throw new BizException("未取到交易字段["+dcType+"]值");
			}
		} else {
			dcType = "*";
		}
		// TODO 需要转义
		String quotaType = tradeType+cashType+dcType;
		// END
		String currencyType = fields[3];	//币种
		if (null == currencyType || "".equals(currencyType)) {
			throw new BizException("该交易金额授权表中币种未配置");
		} 
		if (currencyType.length() > 4){
			if (uiData.containsKey(currencyType)){
				currencyType = (String) uiData.get(currencyType);
			} else {
				throw new BizException("未取到交易字段["+currencyType+"]值");
			}
		} else {
			currencyType = "RMB";
		}
		String moneyName = fields[4];	//金额字段
		if (null == moneyName || "".equals(moneyName)) {
			throw new BizException("该交易金额授权表中金额字段未配置");
		}
		if (uiData.containsKey(moneyName)){
			moneyName = (String)uiData.get(moneyName);
		} else {
			throw new BizException("未取到交易字段["+moneyName+"]值");
		}
		//2.查询该条件下交易限额配置的等级
		AuthLevel authLevel = null;
		for (TradeAuthQuotaDM tradeAuthQuotaDM : authBiz.getAllTradeAuthQuota()) {
			if (quotaType.equals(tradeAuthQuotaDM.getQuotaType()) && currencyType.equals(tradeAuthQuotaDM.getQuotaCnyType())) {
				BigDecimal money = new BigDecimal(moneyName);
				if ((tradeAuthQuotaDM.getQuotaMin().compareTo(money) == -1 || tradeAuthQuotaDM.getQuotaMin().compareTo(money) == 0) && tradeAuthQuotaDM.getQuotaMax().compareTo(money) == 1) {
					if (authLevel == null || authLevel.getLevel() < tradeAuthQuotaDM.getQuotaLevel()) 
						authLevel = AuthLevel.getAuthLevel(tradeAuthQuotaDM.getQuotaLevel());
				}
			}
		}
		
		return authLevel;
	}

}
