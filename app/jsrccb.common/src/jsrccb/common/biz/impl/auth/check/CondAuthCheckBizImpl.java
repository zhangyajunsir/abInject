package jsrccb.common.biz.impl.auth.check;

import java.util.List;

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
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AuthCheckBiz.class, multiple = true)
@Singleton
@Biz("condAuthCheckBiz")
public class CondAuthCheckBizImpl implements AuthCheckBiz {
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
		return AuthType.COND_AUTH;
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
		StringBuilder authMsg = new StringBuilder("〖条件授权〗通讯码["+tradeDataDM.getStoreData().get(ITradeKeys.T_COMM_CODE)+"]");
		List<TradeAuthDM> tradeAuthDMs = authBiz.findTradeAuthDM(tradeCodeDM.getId(), commCodeDM.getId(), AuthType.COND_AUTH);
		for (TradeAuthDM tradeAuthDM : tradeAuthDMs) {
			Boolean bo = expressionBizProvider.executeExpression(tradeAuthDM.getExpressionId(), tradeDataDM);
			if (bo != null && bo) {
				DescribeDM describeDM = describeBiz.findDescribe(tradeAuthDM.getDescribeId());
				authMsg.append(describeDM == null || describeDM.getDescription() == null ? "" : describeDM.getDescription()).append(",");
				if (authLevel == null || authLevel.getLevel() < tradeAuthDM.getLevel()) {
					authLevel = AuthLevel.getAuthLevel(tradeAuthDM.getLevel());
				}
			}
		}
		authMsg.deleteCharAt(authMsg.length()-1); 
		if (authLevel == null) 
			authDM.setAuthStatus(AuthStatus.NO_AUTH);
		else {
			authDM.setAuthStatus(AuthStatus.NEED_AUTH);
			authDM.setAuthLevel(authLevel);
			authDM.getAuthType().add(AuthType.COND_AUTH);
			authDM.getAuthMSG() .add(authMsg.toString());
		}
		return authDM;
	}

}
