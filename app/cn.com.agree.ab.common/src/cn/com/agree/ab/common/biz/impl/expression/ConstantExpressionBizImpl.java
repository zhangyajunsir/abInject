package cn.com.agree.ab.common.biz.impl.expression;

import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.ExpressionBiz;
import cn.com.agree.ab.common.dm.ExpressionDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.exception.ExpressionException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ExpressionBiz.class, multiple = true)
@Singleton
@Biz("constantExpressionBizImpl")
public class ConstantExpressionBizImpl implements ExpressionBiz {

	@Override
	public String type() {
		return "constant";
	}

	@Override
	public Object execute(ExpressionDM expressionDM, TradeDataDM context) throws ExpressionException {
		return expressionDM.getExpression();
	}

}
