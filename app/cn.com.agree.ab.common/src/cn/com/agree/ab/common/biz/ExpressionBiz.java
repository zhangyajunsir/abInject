package cn.com.agree.ab.common.biz;

import cn.com.agree.ab.common.dm.ExpressionDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.exception.ExpressionException;

public interface ExpressionBiz {

	public String type();
	
	public Object execute(ExpressionDM expressionDM, TradeDataDM context) throws ExpressionException;
}
