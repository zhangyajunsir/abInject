package jsrccb.common.biz;

import cn.com.agree.ab.common.dm.TradeDataDM;
import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthType;

/**
 * 授权检查
 * @author SSA
 *
 */
public interface AuthCheckBiz {
	
	public AuthDM   check(TradeDataDM tradeDataDM);
	
	public AuthType getAuthType();
}
