package jsrccb.common.biz.impl.auth.check;

import javax.inject.Inject;
import javax.inject.Named;

import jsrccb.common.biz.AuthCheckBiz;
import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthLevel;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthType;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.TellerBiz;
import cn.com.agree.ab.common.dm.TellerDM;
import cn.com.agree.ab.common.dm.TradeDataDM;

public class LevelAuthCheckBizImpl implements AuthCheckBiz {
	@Inject
	@Named("tellerBiz")
	private TellerBiz tellerBiz;

	@Override
	public AuthType getAuthType() {
		return AuthType.LEVEL_AUTH;
	}

	@Override
	public AuthDM check(TradeDataDM tradeDataDM) {
		TellerDM tellerDM = tellerBiz.getTeller((String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_TELLER));
		AuthLevel authLevel = (AuthLevel)tradeDataDM.getTempArea().get("defaultAuthLevel");
		if (authLevel == null)
			authLevel = AuthLevel.getMinAuthLevel();
		
		AuthDM authDM = new AuthDM();
		if (tellerDM != null && tellerDM.getLevel() < authLevel.getLevel()) {
			authDM.setAuthStatus(AuthStatus.NEED_AUTH);
			authDM.setAuthLevel(authLevel);
			authDM.getAuthType().add(AuthType.LEVEL_AUTH);
			authDM.getAuthMSG() .add("〖等级授权〗通讯码["+tradeDataDM.getStoreData().get(ITradeKeys.T_COMM_CODE)+"]需要"+authLevel.getDesc()+"授权");
		} else {
			authDM.setAuthStatus(AuthStatus.NO_AUTH);
		}
		
		return authDM;
	}

}
