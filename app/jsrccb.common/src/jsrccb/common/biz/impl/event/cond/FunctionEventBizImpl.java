package jsrccb.common.biz.impl.event.cond;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeCondEventBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradePropEventBiz;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.delta.StringArrayDelta;
import cn.com.agree.ab.key.IComponentKeys;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;
import cn.com.agree.inject.annotations.AutoBindMappers;

import com.google.common.collect.Lists;

@AutoBindMappers({
	@AutoBindMapper(baseClass = AbstractTradePropEventBiz.class, multiple = true),
	@AutoBindMapper(baseClass = AbstractTradeCondEventBiz.class, multiple = true) })
@Singleton
@Biz("functionEventBizImpl")
public class FunctionEventBizImpl extends AbstractTradeCondEventBiz {
	@Inject
	@Named("tradeBiz")
	private TradeBiz  tradeBiz;
	
	public boolean dynamic() {
		return true;
	}
	
	public void onInit   (AbstractCommonTrade trade) {
		TradeCodeDM tradeCodeDM = (TradeCodeDM)trade.getContext(ITradeContextKey.TRADE_CODE_DM);
		Map<String, List<TradePropDM>> funcNameMap = new HashMap<String, List<TradePropDM>>();
		for (TradePropDM tradePropDM : tradeCodeDM.getTradePropList()) {
			String _funcName_ = tradePropDM.getFuncCodeName();
			if (_funcName_ == null)
				_funcName_ =  "";
			List<TradePropDM> _tradePropDMs_ = funcNameMap.get(_funcName_);
			if (_tradePropDMs_ == null) {
				_tradePropDMs_ = Lists.newArrayList();
				funcNameMap.put(_funcName_, _tradePropDMs_);
			}
			_tradePropDMs_.add(tradePropDM);
		}
		trade.putContext("funcNameMap", funcNameMap);
		
		for (String funcName : funcNameMap.keySet()) {
			if (funcName.startsWith("combo_")) {
				List<String> items = Arrays.asList((String[])trade.getProperty(funcName, IComponentKeys.COMBO_ITEMS));
				for (TradePropDM tradePropDM : funcNameMap.get(funcName)) {
					String _funcValue_ = tradePropDM.getFuncCodeValue();
					if (_funcValue_ == null)
						_funcValue_ =  "";
					boolean isPermission = tradeBiz.checkTradePermission(tradeCodeDM.getExpressionid(), tradeCodeDM.getCode(), tradePropDM.getCommCode().getCommCode(), (String)trade.getTellerInfo().get(ITradeKeys.G_TELLER));
					if (!isPermission) {	// 删除没有权限的列表项
						for (String _item_ : items) {
							if (_item_.startsWith(_funcValue_+" ")) 
								items.remove(_item_);
						}
					}
				}
				trade.delta(new StringArrayDelta(funcName, IComponentKeys.COMBO_ITEMS, items.toArray(new String[items.size()])));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void preEvent (AbstractCommonTrade trade, Method eventMethod) {
		Map<String, List<TradePropDM>> funcNameMap = (Map<String, List<TradePropDM>>)trade.getContext("funcNameMap");
		String componentName = eventMethod.getName().substring(0, eventMethod.getName().lastIndexOf('_'));
		String eventName     = eventMethod.getName().substring(eventMethod.getName().lastIndexOf('_')+1);
		if (funcNameMap.keySet().contains(componentName) && "OnBlur".equals(eventName)) {
			String text = (String)trade.getProperty(componentName, IComponentKeys.TEXT);
	        if (text == null)
	            text = "";
	        if (componentName.startsWith("combo_") && !text.equals("")) {
	        	String splitor = (String) trade.getProperty(componentName, IComponentKeys.COMBO_ITEMS_SPLITOR);
	            if (splitor == null || splitor.length() == 0)
	                splitor = "-";
	            text = StringUtils.substringBefore(text, splitor).trim();
	        }
	        for (TradePropDM tradePropDM : funcNameMap.get(componentName)) {
				String _funcValue_ = tradePropDM.getFuncCodeValue();
				if (_funcValue_ == null)
					_funcValue_ =  "";
				if (_funcValue_.equals(text)) {
					trade.setDefaultCommCode(tradePropDM.getCommCode().getCommCode());
					break;
				}
	        }
		}
	}

}
