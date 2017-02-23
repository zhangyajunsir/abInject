package jsrccb.common.biz.impl.event.type;

import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.impl.event.AbstractTradeTypeEventBiz;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractTradeTypeEventBiz.class, multiple = true)
@Singleton
@Biz("oldTradeEventBizImpl")	
public class OldTradeEventBizImpl extends AbstractTradeTypeEventBiz {

	@Override
	public void onInit(AbstractCommonTrade trade) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCommit(AbstractCommonTrade trade, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(AbstractCommonTrade trade, String app, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume(AbstractCommonTrade trade, Map<?, ?> suspendResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preEvent(AbstractCommonTrade trade, Method eventMethod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posEvent(AbstractCommonTrade trade, Method eventMethod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preExit(AbstractCommonTrade trade) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posExit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getType() {
		return "old";	//  老交易使用
	}

	@Override
	public String getBranch() {
		return "";		// 全法人
	}

}
