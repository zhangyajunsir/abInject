package jsrccb.common.biz.impl.event.prop;

import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.impl.event.AbstractTradePropEventBiz;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractTradePropEventBiz.class, multiple = true)
@Singleton
@Biz("chargeEventBiz")
public class ChargeEventBizImpl extends AbstractTradePropEventBiz {

	public void preCommit(AbstractCommonTrade trade, String message) {
		
	}
	
}
