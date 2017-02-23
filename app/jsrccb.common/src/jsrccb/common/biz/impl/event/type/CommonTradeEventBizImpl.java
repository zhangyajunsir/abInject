package jsrccb.common.biz.impl.event.type;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthWay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.CommBizProvider;
import cn.com.agree.ab.common.biz.CommLogBiz;
import cn.com.agree.ab.common.biz.SerialNumBiz;
import cn.com.agree.ab.common.biz.SerialNumBiz.TellerFlowType;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeCondEventBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradePropEventBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeTypeEventBiz;
import cn.com.agree.ab.common.dm.CommLogDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.common.exception.AgainRpcException;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.delta.BooleanDelta;
import cn.com.agree.ab.key.IComponentKeys;
import cn.com.agree.ab.key.ITradeConstants;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.dm.Status;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.local.TradeBreakException;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.common.collect.ImmutableMap;

@AutoBindMapper(baseClass = AbstractTradeTypeEventBiz.class, multiple = true)
@Singleton
@Biz("commonTradeEventBiz") 
public class CommonTradeEventBizImpl extends AbstractTradeTypeEventBiz {
	private static final Logger	logger	= LoggerFactory.getLogger(CommonTradeEventBizImpl.class);
	@Inject
	@Named("reviewEventBiz")
	protected AbstractTradePropEventBiz reviewEventBiz;
	@Inject
	@Named("authorizeEventBiz")
	protected AbstractTradePropEventBiz authorizeEventBiz;
	@Inject
	@Named("multiPageEventBiz")
	protected AbstractTradePropEventBiz multiPageEventBiz;
	@Inject
	@Named("chargeEventBiz")
	protected AbstractTradePropEventBiz chargeEventBiz;
	@Inject
	protected CommBizProvider commBizProvider;
	@Inject
	protected Set<AbstractTradeCondEventBiz> tradeCondEventBizs;
	@Inject
	@Named("commLogBiz")
	protected CommLogBiz commLogBiz;
	@Inject
	@Named("serialNumBiz")
	protected SerialNumBiz serialNumBiz;
	@Inject
	@Named("tradeBiz")
	protected TradeBiz  tradeBiz;
	@Inject
	@Named("abViewOpenBiz")
	protected IViewOpenBiz viewOpenBiz;
	

	@Override
	public void onInit(AbstractCommonTrade trade) {
		super.onInit(trade);
		
		try {
			trade.registerHotKey("F4",              "onCommit");
			trade.registerHotKey("NUMPAD_SUBTRACT", "onCommit");
			trade.command(ITradeConstants.COMMAND_ADD_FOCUS_GREEDY_COMPONENTS, "button_exit");
			trade.putStoreData(ITradeKeys.T_TRADE_STATUS, Status.UNKNOWN.toString());
			
			/** 授权初始化处理 */
			authorizeEventBiz.onInit(trade);
			/** 复核初始化处理*/
			reviewEventBiz   .onInit(trade);
			/** 多页初始化处理*/
			multiPageEventBiz.onInit(trade);
			
			/** 条件初始化处理*/
			for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
				if (tradeCondEventBiz.dynamic())
					tradeCondEventBiz.onInit(trade);
			}
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用交易初始化方法发生错误："+e.getMessage(), e);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onCommit (AbstractCommonTrade trade, String message) {
		if (message != null && !JsonUtil.isJsonObjectString(message))
			throw new BizException("交易提交方法参数非JSON字串");
		TradeCodeDM tradeCodeDM = getCurrentTradeCodeDM(trade);
		TradePropDM tradePropDM = null;
		for (TradePropDM _tradePropDM_ : tradeCodeDM.getTradePropList()) {
			if (_tradePropDM_ != null && _tradePropDM_.getCommCode() != null && trade.getStoreData(ITradeKeys.T_COMM_CODE).equals(_tradePropDM_.getCommCode().getCommCode())) {
				tradePropDM = _tradePropDM_;
				break;
			}
		}
		if (tradePropDM != null)
			ContextHelper.setContext(ITradeContextKey.TRADE_PROP_DM, tradePropDM);
		TradeDataDM tradeDataDM = TradeHelper.getTradeData(trade);
		ContextHelper.setContext(ITradeContextKey.TRADE_DATA_DM, tradeDataDM);
		/** 条件提交前处理*/
		for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
			if (tradeCondEventBiz.dynamic())
				tradeCondEventBiz.preCommit(trade, message);
		}
		int againCount = 0;
		boolean needAgain   = false;
		boolean needCommLog = false;
		while (true) {	// 支持多次通讯
			try {
				// 1.提交前处理
				/** 授权提交前处理 */
				authorizeEventBiz.preCommit(trade, message);
				/** 复核提交前处理 */
				reviewEventBiz   .preCommit(trade, message);
				/** 多页提交前处理 */
				multiPageEventBiz.preCommit(trade, message);
				/** 收费提交前处理 */
				chargeEventBiz   .preCommit(trade, message);
				/** 确认提交前处理 */
				if (tradeDataDM.getTempArea().get("CONFIRM_FLG") != null && tradeDataDM.getTempArea().get("CONFIRM_FLG").equals("Y")) {
					if (!trade.isUserConfirmed("〖交易预警〗请柜员和客户确认对于此交易的操作"))
						throw new TradeBreakException();
				}
				// 2.提交处理
				// 授权远程提交
				AuthDM authDM = (AuthDM)trade.getContext().get(ITradeContextKey.AUTH_DM);
				if (authDM != null && authDM.getAuthStatus() == AuthStatus.NEED_AUTH 
						&& (authDM.getAuthWay() == AuthWay.SUPER_ORG_REMOTE_AUTH || authDM.getAuthWay() == AuthWay.AUTH_CENTER_REMOTE_AUTH)) {
					authorizeEventBiz.onCommit(trade, message);
					break;
				} 
				// 复核数据提交
				if (false) {	
					reviewEventBiz   .onCommit(trade, message);
					break;
				} 
				// 正常通讯提交
				{
					String tellerSerialNum = null;
					if (tradePropDM != null && "01".equals(tradePropDM.getBusinessType()))	// 账务流水
						tellerSerialNum = serialNumBiz.generateTellerSerialNum((String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_TELLER), (String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_DATE), TellerFlowType.ACCOUNT);
					if (tradePropDM != null && "03".equals(tradePropDM.getBusinessType()))  // 管理流水
						tellerSerialNum = serialNumBiz.generateTellerSerialNum((String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_TELLER), (String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_DATE), TellerFlowType.MANAGE);
					if (tellerSerialNum != null) {
						CommLogDM commLogDM = (CommLogDM)ContextHelper.getContext(ITradeContextKey.COMM_LOG_DM);
						if (commLogDM == null) {
							commLogDM = commLogBiz.initCommLog(trade.getStoreData(ITradeKeys.T_COMM_CODE), tradeDataDM);
							ContextHelper.setContext(ITradeContextKey.COMM_LOG_DM, commLogDM);
						}
						commLogDM.setTranSeq(tellerSerialNum);
						needCommLog = true;
					}
					Map<String, Object>   oldUiData = tradeDataDM.getUiData();
					String infoId = null;
					try {
						infoId = trade.pushInfoWithoutButton("正在下传数据...请等待,约1-3分钟后将超时退出!");
						commBizProvider.commCommit(trade.getStoreData(ITradeKeys.T_COMM_CODE), tradeDataDM);
					} catch (RpcException e) {
						if (e.getLevel() != ExceptionLevel.ERROR && e.getLevel() != ExceptionLevel.FATAL) {
							TradeHelper.setTradeData (trade,  tradeDataDM);	// 此时tradeDataDM.getUiData()只包含了通讯后赋值的ui数据
							ObjectMergeUtil.merge(oldUiData,  tradeDataDM.getUiData(), true, true); // 新UiData合并到老UiData里
							tradeDataDM.setUiData(oldUiData);
						}
						throw e;
					}finally {
						trade.closeInfo(infoId);
					}
					TradeHelper.setTradeData (trade,  tradeDataDM);	// 此时tradeDataDM.getUiData()只包含了通讯后赋值的ui数据
					ObjectMergeUtil.merge(oldUiData,  tradeDataDM.getUiData(), true, true); // 新UiData合并到老UiData里
					tradeDataDM.setUiData(oldUiData);
					break;
				}
			} catch (AgainRpcException e) {
				logger.info("触发再次通讯级别：{} 原因：{} 后台原因：{}", e.getLevel().getDesc(), e.getMessage(), e.getMsg());
				if (e.getLevel() != ExceptionLevel.DEBUG) {
					OpenViewArgDM OpenViewArgDM = new OpenViewArgDM();
					OpenViewArgDM.setWindow(true);
					OpenViewArgDM.setImportVar(ImmutableMap.of("#msgLevel", e.getLevel().toJsonString(), "#msgInfo", JsonUtil.obj2json(e.getMsg())));
					viewOpenBiz.asycOpenView(trade, "msg", OpenViewArgDM);
				}
				againCount++;
				needAgain = true;
				if (againCount > 3) {
					throw new BasicRuntimeException("触发再次通讯已超过三次", e);
				}
				continue;
			} catch (RpcException e) {
				logger.error("后台通讯报错级别：{} 原因：{} 后台原因：{}", e.getLevel().getDesc(), e.getMessage(), e.getMsg());
				if (e.getLevel() != ExceptionLevel.DEBUG) {
					OpenViewArgDM OpenViewArgDM = new OpenViewArgDM();
					OpenViewArgDM.setWindow(true);
					OpenViewArgDM.setImportVar(ImmutableMap.of("#msgLevel", e.getLevel().toJsonString(), "#msgInfo", JsonUtil.obj2json(e.getMsg())));
					viewOpenBiz.asycOpenView(trade, "msg", OpenViewArgDM);
				}
				if (e.getLevel() == ExceptionLevel.ERROR || e.getLevel() == ExceptionLevel.FATAL) {
					trade.putStoreData(ITradeKeys.T_TRADE_STATUS, Status.FAIL.toString());
					throw new TradeBreakException(e.getLevel().getLevel(), null);
				}
			} catch (BasicRuntimeException e) {
				throw e;
			} catch (TradeBreakException e) {
				throw e;
			} catch (Exception e) {
				throw new BizException("调用交易提交方法发生错误："+e.getMessage(), e);
			} finally {
				if (needCommLog) {
					CommLogDM commLogDM = (CommLogDM)ContextHelper.getContext(ITradeContextKey.COMM_LOG_DM);
					if (commLogDM != null)
						commLogBiz.addCommLog(commLogDM);	// 异步执行新增柜员通讯日志
					ContextHelper.removeContext(ITradeContextKey.COMM_LOG_DM);
					if (needAgain) {
						CommLogDM _commLogDM_ = commLogBiz.initCommLog(trade.getStoreData(ITradeKeys.T_COMM_CODE), tradeDataDM);
						_commLogDM_.setSourceSeq(commLogDM.getTranSeq());
						ContextHelper.setContext(ITradeContextKey.COMM_LOG_DM, _commLogDM_);
					}
				} else
					needCommLog = false;
				if (!needAgain) {
					// 3.提交后处理（包括异常退出，状态清理等）
					/** 授权提交后处理 */
					authorizeEventBiz.posCommit(trade, message);
					/** 复核提交后处理 */
					reviewEventBiz   .posCommit(trade, message);
					/** 收费提交后处理 */
					chargeEventBiz   .posCommit(trade, message);
					/** 确认提交后处理 */
					tradeDataDM.getTempArea().remove("CONFIRM_FLG");
				} else
					needAgain = false;
			}
			break;
		}
		// 4.提交后处理（限定提交成功情况）
		/** 多页提交后处理 */
		multiPageEventBiz.posCommit(trade, message);
		/** 条件提交后处理*/
		for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
			if (tradeCondEventBiz.dynamic())
				tradeCondEventBiz.posCommit(trade, message);
		}
		/** 完成后画面动作 */
		posCommit(trade, message);
	}

	@Override
	public void onMessage(AbstractCommonTrade trade, String app, String message) {
		try {
			/** 授权消息处理 */
			if ("auth"     .equals(app)) {
				authorizeEventBiz.onMessage(trade, app, message);		
			}
			/** 复核消息处理 */
			if ("review"   .equals(app)) {
				reviewEventBiz   .onMessage(trade, app, message);
			}
			/** 多页消息处理 */
			if ("multiPage".equals(app)) {
				multiPageEventBiz.onMessage(trade, app, message);
			}
			
			/** 条件消息处理*/
			for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
				if (tradeCondEventBiz.dynamic())
					tradeCondEventBiz.onMessage(trade, app, message);
			}
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用交易消息处理方法发生错误："+e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onResume(AbstractCommonTrade trade, Map suspendResult) {
		try {
			Integer aTradeId = ((TradeCodeDM)trade.getContext(ITradeContextKey.TRADE_CODE_DM)).getId();
			String  bTradeId = (String)suspendResult.get("#"+ITradeKeys.T_TRADE_ID);
			Map<String,Map<String, String>> allMapping = (Map)trade.getContext(ITradeContextKey.RELATION_MAPPING);
			if (allMapping != null) {
				Map<String, String> mapping = allMapping.remove(aTradeId+"_"+bTradeId);
				if (mapping != null) {
					Map<String, Object> uiData = new HashMap<String, Object>();
					for (Map.Entry<String, String> entry : mapping.entrySet()) {
						String keyOfA = entry.getKey();
						String keyOfB = entry.getValue();
						if (suspendResult.containsKey(keyOfB)) {
							if (keyOfA.startsWith("#")) {
								trade.putStoreData(keyOfA.substring(1), (String)suspendResult.get(keyOfB));
							}
							if (keyOfA.startsWith("$")) {
								uiData.put(keyOfA.substring(1), suspendResult.get(keyOfB));
							}
						}
					}
					try {
						TradeHelper.setComponentData(trade, uiData);
					} catch (Exception e) {
						logger.warn("赋值UI出现异常", e);
					}
				}
			}
			TradeCodeDM bTradeCodeDM = tradeBiz.findTradeCode(Integer.valueOf(bTradeId));
			suspendResult.put("bTradeCodeDM", bTradeCodeDM);
			if ("auth".equals(bTradeCodeDM.getCode()))
				authorizeEventBiz.onResume(trade, suspendResult);
			
			/** 条件恢复处理*/
			for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
				if (tradeCondEventBiz.dynamic())
					tradeCondEventBiz.onResume(trade, suspendResult);
			}
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用交易恢复处理方法发生错误："+e.getMessage(), e);
		}
	}

	@Override
	public void preEvent (AbstractCommonTrade trade, Method eventMethod) {
		try {
			/** 条件事件前处理*/
			for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
				if (tradeCondEventBiz.dynamic())
					tradeCondEventBiz.preEvent(trade, eventMethod);
			}
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用交易事件前处理方法发生错误："+e.getMessage(), e);
		}
	}

	@Override
	public void posEvent (AbstractCommonTrade trade, Method eventMethod) {
		try {
			/** 条件事件后处理*/
			for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
				if (tradeCondEventBiz.dynamic())
					tradeCondEventBiz.posEvent(trade, eventMethod);
			}
		
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用交易事件后处理方法发生错误："+e.getMessage(), e);
		}
	}

	@Override
	public void preExit  (AbstractCommonTrade trade) {
		try {
			/** 条件退出前处理*/
			for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
				if (tradeCondEventBiz.dynamic())
					tradeCondEventBiz.preExit(trade);
			}
		
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用交易退出前处理方法发生错误："+e.getMessage(), e);
		}
	}

	@Override
	public void posExit() {
		try {
			/** 条件退出后处理*/
			for (AbstractTradeCondEventBiz tradeCondEventBiz : tradeCondEventBizs) {
				if (tradeCondEventBiz.dynamic())
					tradeCondEventBiz.posExit();
			}
		
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用交易退出后处理方法发生错误："+e.getMessage(), e);
		}
	}

	@Override
	public String getType() {
		return "common";	// 普通类型
	}

	@Override
	public String getBranch() {
		return "";			// 全法人
	}

	protected void posCommit(AbstractCommonTrade trade, String message) {
		trade.posCommit(message);
		TradePropDM tradePropDM = (TradePropDM)ContextHelper.getContext(ITradeContextKey.TRADE_PROP_DM);
		try {
			trade.putStoreData(ITradeKeys.T_TRADE_STATUS, Status.SUCCESS.toString());
			if ("01".equals(tradePropDM.getBusinessType()) || "03".equals(tradePropDM.getBusinessType())) {
				trade.pushInfo("交易成功", true);
				trade.exit(0);
			}
			if ("02".equals(tradePropDM.getBusinessType())) {
				trade.delta(new BooleanDelta("button_submit", IComponentKeys.ENABLE, false));
			}
		} catch (IOException e) {
		}
	}
}
