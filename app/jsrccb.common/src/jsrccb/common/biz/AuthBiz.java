package jsrccb.common.biz;

import java.util.List;

import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthType;
import jsrccb.common.dm.TradeAuthDM;
import jsrccb.common.dm.TradeAuthQuotaDM;
import cn.com.agree.ab.common.dm.TradeDataDM;

public interface AuthBiz {
	
	public AuthDM getLocalAuth(TradeDataDM tradeDataDM);
	
	public List<TradeAuthQuotaDM> getAllTradeAuthQuota();
	
	public List<TradeAuthDM> findTradeAuthDM(Integer tradeId, Integer commId);
	
	public List<TradeAuthDM> findTradeAuthDM(Integer tradeId, Integer commId, AuthType authType);
}
