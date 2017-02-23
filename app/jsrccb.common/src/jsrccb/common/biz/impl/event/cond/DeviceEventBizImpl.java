package jsrccb.common.biz.impl.event.cond;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.PrintBizProvider;
import jsrccb.common.biz.dev.EstimatorBiz;
import jsrccb.common.biz.dev.ICCardBiz;
import jsrccb.common.biz.dev.IDCardBiz;
import jsrccb.common.biz.dev.MsfBiz;
import jsrccb.common.biz.dev.OutClearBiz;
import jsrccb.common.biz.dev.PinBiz;
import jsrccb.common.dm.PinDM;
import jsrccb.common.dm.dev.AccountDM;
import jsrccb.common.dm.dev.ICCardDM;
import jsrccb.common.dm.dev.IDCardDM;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.annotation.Card;
import cn.com.agree.ab.common.annotation.IDCard;
import cn.com.agree.ab.common.annotation.Msf;
import cn.com.agree.ab.common.annotation.Pin;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeCondEventBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradePropEventBiz;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.delta.StringDelta;
import cn.com.agree.ab.key.IComponentKeys;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.dm.BasicDM;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.trade.core.component.common.Component;
import cn.com.agree.inject.annotations.AutoBindMapper;
import cn.com.agree.inject.annotations.AutoBindMappers;

@AutoBindMappers({
	@AutoBindMapper(baseClass = AbstractTradePropEventBiz.class, multiple = true),
	@AutoBindMapper(baseClass = AbstractTradeCondEventBiz.class, multiple = true)
})
@Singleton
@Biz("deviceEventBizImpl")
public class DeviceEventBizImpl  extends AbstractTradeCondEventBiz {
	@Inject
	@Named("pinBiz")
	protected PinBiz pinBiz;
	@Inject
	@Named("msfBiz")
	protected MsfBiz msfBiz;
	@Inject
	@Named("idCardBiz")
	protected IDCardBiz idCardBiz;
	@Inject
	@Named("estimatorBiz")
	protected EstimatorBiz estimatorBiz;
	@Inject
	@Named("outClearBiz")
	protected OutClearBiz outClearBiz;
	@Inject
	@Named("icCardBiz")
	protected ICCardBiz icCardBiz;
	@Inject
	protected PrintBizProvider printBizProvider;
	
	public boolean dynamic() {
		return true;
	}

	@SuppressWarnings("deprecation")
	public void preEvent (AbstractCommonTrade trade, Method eventMethod) {
		if (eventMethod.isAnnotationPresent(Pin.class)) {
			Pin pin = eventMethod.getAnnotation(Pin.class);
			String account = "";
			if (pin.account().startsWith(IComponentKeys.TEXT)) {
				account = (String)trade.getProperty(pin.account(), IComponentKeys.TEXT);
			} else 
				account = pin.account();
			PinDM pinDM = pinBiz.readPasswd(trade, account, pin.must(), pin.count(), pin.check());
			trade.delta(new StringDelta(StringUtils.substringBefore(eventMethod.getName(), "_On"), IComponentKeys.TEXT,  pinDM == null||pinDM.getGG16MW() == null ? "" : pinDM.getGG16MW()));
			if (pin.skip()) {
				try {
					Component component = TradeHelper.getComponent(trade, StringUtils.substringBeforeLast(eventMethod.getName(), "_On"));
					if (component != null)
						trade.traverseFocus(component, true);
				} catch (Exception e) {
			}
		}

		}
		if (eventMethod.isAnnotationPresent(Msf.class)) {
			Msf msf = eventMethod.getAnnotation(Msf.class);
			if (msf.type() != 2 && msf.type() != 3 && msf.type() != 23)
				throw new BizException(eventMethod.getName()+"配置Msf注解磁道类型错误");
			AccountDM accountDM = null;
			if (msf.type() == 2)
				accountDM = msfBiz.readTellerCard(trade, msf.must());
			if (msf.type() == 3)
				accountDM = msfBiz.readMSF3(trade, msf.must());
			if (msf.type() == 23)
				accountDM = msfBiz.readMSF23(trade, msf.must());
			if (accountDM != null) {
				trade.getTempArea().put("accountDM", accountDM);
				for (String config : msf.result()) {
					if (config.indexOf(':') > 0) {
						String value = null;
						try {
							value = BeanUtils.getProperty(accountDM, StringUtils.substringAfter(config, ":"));
						} catch (Exception e) {
							continue;
						}
						trade.delta(new StringDelta(StringUtils.substringBefore(config, ":"), IComponentKeys.TEXT, value == null ? "" : value));
					}
				}
			}
		}
		if (eventMethod.isAnnotationPresent(Card.class)) {
			Card card = eventMethod.getAnnotation(Card.class);
			String type = null;
			try {
				if (card.must())
					type = trade.pushComboInputDialog("刷卡方式", "请选择刷卡方式", new String[]{"1 磁卡输入","2 IC卡输入"}, "2");
				else
					type = trade.pushComboInputDialog("刷卡方式", "请选择刷卡方式", new String[]{"0 手动输入","1 磁卡输入","2 IC卡输入"}, "0");
			} catch (IOException e) {
				throw new BizException(e.getMessage(), e);
			}
			AccountDM accountDM = null;
			if ("1".equals(type)) {
				accountDM = msfBiz.readMSF23(trade, true);
			}
			if (accountDM != null) {
				trade.getTempArea().put("accountDM", accountDM);
				for (String config : card.result()) {
					if (config.indexOf(':') > 0) {
						String value = null;
						try {
							value = BeanUtils.getProperty(accountDM, StringUtils.substringAfter(config, ":"));
						} catch (Exception e) {
							continue;
						}
						trade.delta(new StringDelta(StringUtils.substringBefore(config, ":"), IComponentKeys.TEXT, value == null ? "" : value));
					}
				}
			}
			ICCardDM icCardDM = null;
			if ("2".equals(type)) {
				// IC读取全部信息 TODO
				icCardDM = icCardBiz.readICCard(trade, true);
			}
			if (icCardDM != null) {
				trade.getTempArea().put("icCardDM", icCardDM);
				for (String config : card.result()) {
					if (config.indexOf(':') > 0) {
						String value = null;
						try {
							value = BeanUtils.getProperty(icCardDM, StringUtils.substringAfter(config, ":"));
						} catch (Exception e) {
							continue;
						}
						trade.delta(new StringDelta(StringUtils.substringBefore(config, ":"), IComponentKeys.TEXT, value == null ? "" : value));
					}
				}
			}

		}
		if (eventMethod.isAnnotationPresent(IDCard.class)) {
			IDCard card = eventMethod.getAnnotation(IDCard.class);
			IDCardDM idCardDM = idCardBiz.read(trade, card.must());
			if (idCardDM != null) {
				trade.getTempArea().put("idCardDM", idCardDM);
				for (String config : card.result()) {
					if (config.indexOf(':') > 0) {
						String value = null;
						try {
							value = BeanUtils.getProperty(idCardDM, StringUtils.substringAfter(config, ":"));
						} catch (Exception e) {
							continue;
						}
						trade.delta(new StringDelta(StringUtils.substringBefore(config, ":"), IComponentKeys.TEXT, value == null ? "" : value));
					}
				}
			}
		}
		
		
	}
	
	public void posEvent (AbstractCommonTrade trade, Method eventMethod) {
		
	}
	
	@SuppressWarnings("unused")
	@Override
	public void preCommit(AbstractCommonTrade trade, String message)     {
		if (false) {
			// 柜外清调用（TRADE_PROP进行总开关） TODO
			/*boolean confirm = outClearBiz.displayWithButton(trade, true, message);
			try {
				if(!confirm){
					trade.pushInfo("请修正！", true);
				}
				trade.pushInfo("客户已确认信息，请确认提交", true);
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void posCommit(AbstractCommonTrade trade, String message)     {
		TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
		if (tradeDataDM == null)
			return;
		TradePropDM tradePropDM = (TradePropDM)ContextHelper.getContext(ITradeContextKey.TRADE_PROP_DM);
		// 提交后打印处理（TRADE_PROP进行总开关）
		if (tradePropDM != null && tradePropDM.getAutoPrintFlag() == 1) {
			Map<String, Object> tempAreaMap = trade.getTempArea();
			for (String key  :  tempAreaMap.keySet()) {	// 某些主通讯逻辑里会发起了多个后端交易
				if (key.endsWith("_RSP")) {
					Object commitRSP = trade.getTempArea(key);
					if (commitRSP instanceof Map || commitRSP instanceof BasicDM) {
						if (commitRSP instanceof BasicDM) 
							commitRSP = ((BasicDM)commitRSP).toFieldMapping();
						printBizProvider.autoPrint(trade, tradeDataDM, StringUtils.substringBefore(key, "_RSP"), (Map<String, Object>)commitRSP);
					}
				}
			}
		}
		// 评价器调用（TRADE_PROP进行总开关）(内部再根据配置是否进行评价，评价应该结合叫好一次性进行评价)
//		estimatorBiz.assess(trade, true);
	}
}
