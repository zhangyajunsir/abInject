package jsrccb.common.biz.impl.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import jsrccb.common.biz.AuthBiz;
import jsrccb.common.biz.AuthCheckBiz;
import jsrccb.common.dao.TradeAuthDao;
import jsrccb.common.dao.entity.TradeAuthEntity;
import jsrccb.common.dao.entity.TradeAuthQuotaEntity;
import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthLevel;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthType;
import jsrccb.common.dm.TradeAuthDM;
import jsrccb.common.dm.TradeAuthQuotaDM;
import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AuthBiz.class)
@Singleton
@Biz("authBiz") 
public class AuthBizImpl implements AuthBiz {
	@Inject
	@Named("tradeAuthDao")
	private TradeAuthDao tradeAuthDao;
	@Inject
	@Named("tradeBiz")
	private TradeBiz tradeBiz;
	@Inject
	private Set<AuthCheckBiz> authCheckBizs;
	
	public AuthDM getLocalAuth(TradeDataDM tradeDataDM) {
		TradeCodeDM tradeCodeDM = (TradeCodeDM) tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
		Preconditions.checkState(tradeCodeDM != null, "查找不到交易码〖"+tradeDataDM.getStoreData().get(ITradeKeys.T_TRADE_CODE)+"〗对象");
		CommCodeDM commCodeDM = tradeBiz.findCommCode((String)tradeDataDM.getStoreData().get(ITradeKeys.T_COMM_CODE));
		Preconditions.checkState(commCodeDM  != null, "查找不到通讯码〖"+tradeDataDM.getStoreData().get(ITradeKeys.T_COMM_CODE)+"〗对象");
		int authType = 0;
		AuthLevel defaultAuthLevel = null;
		for (TradePropDM tradePropDM : tradeCodeDM.getTradePropList()) {
			if (tradePropDM.getCommId() == commCodeDM.getId()) {
				authType = tradePropDM.getAuthType();
				defaultAuthLevel = AuthLevel.getAuthLevel(tradePropDM.getAuthLevel());
				break;
			}
		}
		if (authType == 0) {
			return null;
		}
		AuthDM authDM = new AuthDM();
		authDM.setAuthStatus(AuthStatus.NO_AUTH);
		tradeDataDM.getTempArea().put("defaultAuthLevel", defaultAuthLevel);
		//以字节位由右至左：第1位为1前端强制授权；第2位为1前端条件授权；第3位为1前端金额授权；第4位为1执行等级授权；
		//http://www.cnblogs.com/zhuawang/p/3948344.html
		for (AuthCheckBiz authCheckBiz : authCheckBizs) {
			if ((authType & (1 << (authCheckBiz.getAuthType().getType()-1))) > 0) {
				AuthDM _authDM_ = authCheckBiz.check(tradeDataDM);
				if (_authDM_ != null && _authDM_.getAuthStatus() == AuthStatus.NEED_AUTH) {
					authDM.setAuthStatus(AuthStatus.NEED_AUTH);
					authDM.getAuthType().addAll(_authDM_.getAuthType());
					authDM.getAuthMSG() .addAll(_authDM_.getAuthMSG());
					if (authDM.getAuthLevel() == null || authDM.getAuthLevel().getLevel() < _authDM_.getAuthLevel().getLevel()) {
						authDM.setAuthLevel(_authDM_.getAuthLevel());
					}
				}
			}
		}
		return authDM;
	}
	
	@Cacheable
	public List<TradeAuthQuotaDM> getAllTradeAuthQuota() {
		List<TradeAuthQuotaEntity> tradeAuthQuotaEntitys = tradeAuthDao.findAllTradeAuthQuota();
		List<TradeAuthQuotaDM> allTradeAuthQuota = new ArrayList<TradeAuthQuotaDM>();
		for (TradeAuthQuotaEntity tradeAuthQuotaEntity : tradeAuthQuotaEntitys) {
			TradeAuthQuotaDM authQuotaDM = new TradeAuthQuotaDM();
			authQuotaDM.cloneValueFrom(tradeAuthQuotaEntity);
			allTradeAuthQuota.add(authQuotaDM);
		}
		return allTradeAuthQuota;
	}

	@Override
	@Cacheable
	public List<TradeAuthDM> findTradeAuthDM(Integer tradeId, Integer commId) {
		List<TradeAuthEntity> tradeAuthEntityList = tradeAuthDao.findTradeAuths(tradeId, commId);
		if (tradeAuthEntityList == null || tradeAuthEntityList.size() == 0)
			return null;
		List<TradeAuthDM> tradeAuthDMs = new ArrayList<TradeAuthDM>();
		for (TradeAuthEntity tradeAuthEntity : tradeAuthEntityList) {
			TradeAuthDM tradeAuthDM = new TradeAuthDM();
			tradeAuthDM.cloneValueFrom(tradeAuthEntity);
			tradeAuthDMs.add(tradeAuthDM);
		}
		return tradeAuthDMs;
	}
	
	@Override
	public List<TradeAuthDM> findTradeAuthDM(Integer tradeId, Integer commId, AuthType authType) {
		if (authType == null)
			return null;
		List<TradeAuthDM> allTradeAuthDMs = findTradeAuthDM(tradeId, commId);
		if (allTradeAuthDMs == null)
			return null;
		List<TradeAuthDM> tradeAuthDMs = new ArrayList<TradeAuthDM>();
		for (TradeAuthDM tradeAuthDM : allTradeAuthDMs) {
			if (tradeAuthDM.getType() == authType.getType()) {
				tradeAuthDMs.add(tradeAuthDM);
			}
		}
		return tradeAuthDMs;
	}

	public static void main(String[] args) {
		int i = 0;
		System.out.println(i);
		i |= 1<<0;
		System.out.println(i);
		i |= 1<<1;
		System.out.println(i);
		i |= 1<<2;
		System.out.println(i);
	}

}
