package jsrccb.common.biz.impl.event.cond;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.annotation.CustomerName;
import jsrccb.common.annotation.IdCardCheck;
import jsrccb.common.utils.VerifyDataUtil;

import com.google.common.collect.ImmutableMap;

import cn.com.agree.ab.common.biz.CommBizProvider;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeCondEventBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradePropEventBiz;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.delta.StringDelta;
import cn.com.agree.ab.key.IComponentKeys;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.local.TradeBreakException;
import cn.com.agree.inject.annotations.AutoBindMapper;
import cn.com.agree.inject.annotations.AutoBindMappers;

@AutoBindMappers({
		@AutoBindMapper(baseClass = AbstractTradePropEventBiz.class, multiple = true),
		@AutoBindMapper(baseClass = AbstractTradeCondEventBiz.class, multiple = true) })
@Singleton
@Biz("customerEventBizImpl")
public class CustomerEventBizImpl extends AbstractTradeCondEventBiz {
	@Inject
	@Named("tradeBiz")
	protected TradeBiz tradeBiz;
	@Inject
	protected CommBizProvider commBizProvider;
	@Inject
	@Named("abViewOpenBiz")
	protected IViewOpenBiz viewOpenBiz;

	public boolean dynamic() {
		return true;
	}

	public void onResume(AbstractCommonTrade trade, Map<?, ?> suspendResult) {
		TradeCodeDM bTradeCodeDM = (TradeCodeDM)suspendResult.get("bTradeCodeDM");
		if (bTradeCodeDM!=null&&"cxkhmc".equals(bTradeCodeDM.getCode())) {
			String custNameKey = (String) trade.getStoreData("custNameKey");
			String custSysNoKey = (String) trade.getStoreData("custSysNoKey");
			if (custNameKey.startsWith("#")) {
				trade.putStoreData(custNameKey.substring(1),(String) suspendResult.get("#CUSTOM_NAME"));
			} else {
				trade.delta(new StringDelta(custNameKey, IComponentKeys.TEXT,(String) suspendResult.get("#CUSTOM_NAME")));
			}
			if (custSysNoKey.startsWith("#")) {
				trade.putStoreData(custSysNoKey.substring(1),(String) suspendResult.get("#CUSTOM_NO"));
			} else {
				trade.delta(new StringDelta(custSysNoKey, IComponentKeys.TEXT,(String) suspendResult.get("#CUSTOM_NO")));
			}
			try {
				//回调原方法，执行交易该方法内逻辑
				TradeHelper.callTradeEventMethod(trade, (String)suspendResult.get("#eventMethod"));
			} catch (Throwable e) {
				throw new BizException("调用"+suspendResult.get("#eventMethod")+"失败，原因："+e.getMessage(), e);
			}
		}
		if (bTradeCodeDM!=null&&"70901".equals(bTradeCodeDM.getCode())) {
			if (!"true".equals((String)suspendResult.get("#result"))) {
				throw new BizException("核查未通过！");
			}else{
				String idNoKey = (String) trade.getStoreData("idNoKey");
				if (idNoKey.startsWith("#")) {
					trade.putStoreData(idNoKey.substring(1),(String) suspendResult.get("#IdNo"));
				} else {
					trade.delta(new StringDelta(idNoKey, IComponentKeys.TEXT,(String) suspendResult.get("#IdNo")));
				}
			}
		}
		try {
			//回调原方法，执行交易该方法内逻辑
			TradeHelper.callTradeEventMethod(trade, (String)suspendResult.get("#eventMethod"));
		} catch (Throwable e) {
			throw new BizException("调用"+suspendResult.get("#eventMethod")+"失败，原因："+e.getMessage(), e);
		}
	}

	public void preEvent(AbstractCommonTrade trade, Method eventMethod) {
		if (eventMethod.isAnnotationPresent(IdCardCheck.class)) {
			trade.putStoreData("eventMethod",eventMethod.getName());
			IdCardCheck icc = eventMethod.getAnnotation(IdCardCheck.class);
			String idType = "";
			if (icc.idType().startsWith("#")) {
				idType = trade.getStoreData(icc.idType().substring(1)).split("-")[0];
			} else {
				idType = ((String) trade.getProperty(icc.idType(),IComponentKeys.TEXT)).split("-")[0];
			}
			String idNo = "";
			if (icc.idNo().startsWith("#")) {
				idNo = trade.getStoreData(icc.idNo().substring(1));
			} else {
				idNo = (String) trade.getProperty(icc.idNo(),IComponentKeys.TEXT);
			}
			idNo=idNo.trim();
			if ("A".equals(idType)) {
				if ( (idNo.length() != 15)&& (idNo.length() != 18) ) {
					throw new BizException("身份证号只允许15位或18位");
				}
				if ((idNo.length() == 15||idNo.length() == 18)&& ("x".equals(idNo.substring(idNo.length()-1))) ) {
					idNo = idNo.substring(0,idNo.length()-1)+ "X";
				}
				//校验身份证号是否合法
				if(VerifyDataUtil.IdCardCheck(idNo)){
					boolean suspendflag = trade.isUserConfirmed("是否联动核查身份证?");
					if (suspendflag == true) {
						trade.putStoreData("idNoKey", icc.idNo());
						OpenViewArgDM openViewArg = new OpenViewArgDM();
						openViewArg.setWindow(true);
						openViewArg.setImportVar(ImmutableMap.of("#suspendflag","yes","#IdNo",idNo));
						openViewArg.setExportNames(new String[] { "#result","#IdNo","#eventMethod"});
						viewOpenBiz.syncOpenView(trade, "70901", openViewArg);
					}
				}
			}
		}
		// 客户信息查询
		if (eventMethod.isAnnotationPresent(CustomerName.class)) {
			trade.putStoreData("eventMethod",eventMethod.getName());
			CustomerName cust = eventMethod.getAnnotation(CustomerName.class);
			String idType = "";
			if (cust.idType().startsWith("#")) {
				idType = trade.getStoreData(cust.idType().substring(1)).split("-")[0];
			} else {
				idType = ((String) trade.getProperty(cust.idType(),IComponentKeys.TEXT)).split("-")[0];
			}
			String idno = "";
			if (cust.idNo().startsWith("#")) {
				idno = trade.getStoreData(cust.idNo().substring(1));
			} else {
				idno = (String) trade.getProperty(cust.idNo(),IComponentKeys.TEXT);
			}
			if (!"".equals(idno)) {
				trade.putStoreData("custNameKey", cust.name());
				trade.putStoreData("custSysNoKey", cust.sysNo());
				trade.putStoreData("TFT_CER_TY", idType);
				trade.putStoreData("TFT_CER_NO", idno.trim());
				TradeDataDM tradeDataDM = TradeHelper.getTradeData(trade);
				try {
					commBizProvider.commCommit("EC0390001", tradeDataDM);
				} catch (RpcException e) {
					if (e.getLevel() != ExceptionLevel.DEBUG) {
						OpenViewArgDM OpenViewArgDM = new OpenViewArgDM();
						OpenViewArgDM.setWindow(true);
						OpenViewArgDM.setImportVar(ImmutableMap.of("#msgLevel", e.getLevel().toJsonString(), "#msgInfo", JsonUtil.obj2json(e.getMsg())));
						viewOpenBiz.asycOpenView(trade, "msg", OpenViewArgDM);
					}
					if (e.getLevel() == ExceptionLevel.ERROR || e.getLevel() == ExceptionLevel.FATAL) 
						throw new TradeBreakException(e.getLevel().getLevel(), null);
				}
				@SuppressWarnings("unchecked")
				ArrayList<String[]> a = (ArrayList<String[]>) trade.getTempArea().get("FORM");
				if (!"".equals(a.get(1)[0])) {
					OpenViewArgDM openViewArg = new OpenViewArgDM();
					openViewArg.setWindow(true);
					openViewArg.setImportVar(ImmutableMap.of("#list",JsonUtil.obj2json(a)));
					openViewArg.setExportNames(new String[] { "#CUSTOM_NAME","#CUSTOM_NO" ,"#eventMethod"});
					viewOpenBiz.syncOpenView(trade, "cxkhmc", openViewArg);
				} else {
					if (cust.name().startsWith("#")) {
						trade.putStoreData(cust.name().substring(1),a.get(0)[0]);
					} else {
						trade.delta(new StringDelta(cust.name(),IComponentKeys.TEXT, a.get(0)[0]));
					}
					if (cust.sysNo().startsWith("#")) {
						trade.putStoreData(cust.sysNo().substring(1),a.get(0)[1]);
					} else {
						trade.delta(new StringDelta(cust.sysNo(),IComponentKeys.TEXT, a.get(0)[1]));
					}
				}
			}
		}
	}
}
