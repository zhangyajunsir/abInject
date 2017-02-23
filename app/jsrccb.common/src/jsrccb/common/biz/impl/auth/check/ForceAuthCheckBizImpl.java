package jsrccb.common.biz.impl.auth.check;

import javax.inject.Singleton;

import jsrccb.common.biz.AuthCheckBiz;
import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthLevel;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthType;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AuthCheckBiz.class, multiple = true)
@Singleton
@Biz("forceAuthCheckBiz")
public class ForceAuthCheckBizImpl implements AuthCheckBiz {

	@Override
	public AuthType getAuthType() {
		return AuthType.FORCE_AUTH;
	}

	@Override
	public AuthDM check(TradeDataDM tradeDataDM) {
		AuthDM authDM = new AuthDM();
		AuthLevel authLevel = (AuthLevel)tradeDataDM.getTempArea().get("defaultAuthLevel");
		authDM.setAuthLevel (authLevel == null ? AuthLevel.getMinAuthLevel() : authLevel);
		authDM.setAuthStatus(AuthStatus.NEED_AUTH);
		authDM.getAuthType().add(AuthType.FORCE_AUTH);
		authDM.getAuthMSG() .add("〖强制授权〗通讯码["+tradeDataDM.getStoreData().get(ITradeKeys.T_COMM_CODE)+"]需要强制授权");
		return authDM;
	}

}
