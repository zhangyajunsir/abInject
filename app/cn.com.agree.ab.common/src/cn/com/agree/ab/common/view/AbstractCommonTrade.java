package cn.com.agree.ab.common.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.BranchBiz;
import cn.com.agree.ab.common.biz.CommBizProvider;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeTypeEventBiz;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;
import cn.com.agree.ab.lib.exception.ViewException;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.local.TradeBreakException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public abstract class AbstractCommonTrade extends Trade {
	private static final Logger	logger	= LoggerFactory.getLogger(AbstractCommonTrade.class);
	
	/**
	 * Trade类是每次事件都创建一个新的，事件处理前会恢复栏位的内容，事件处理完会将栏位(必须Public，且包含父类)的保存到TradeData里；
	 * 类型为FINAL、STATIC、TRANSIENT、Component子类、Trade子类和cn.com.agree.ab.trade.core包下类的成员变量会排除在外；
	 * 故依赖注入的成员变量不需要保存，所以用了TRANSIENT。
	 */
	@com.google.inject.Inject(optional = true)
	private transient Set<AbstractTradeTypeEventBiz>  allTradeEventBizs = Sets.newHashSet();
	@Inject
	@Named("tradeBiz")
	private transient TradeBiz  tradeBiz;
	@Inject
	@Named("branchBiz")
	private transient BranchBiz branchBiz;
	@Inject
	private transient CommBizProvider commBizProvider;
	@Inject
	@Named("abViewOpenBiz")
	protected transient IViewOpenBiz viewOpenBiz;
	/**
	 * 必须得Public
	 */
	public AbstractTradeTypeEventBiz currentTradeEventBiz;
	/**
	 * 用于存放交易执行过程所需要的对象，这类对象只属于后端处理所需；不适合放入StoreData，因为StoreData内容会被同步到C端
	 * 必须得Public
	 */
	public Map<String, Object> context = new HashMap<String, Object>();
	/**
	 * 用于存放交易事件过程所需要的对象，这类对象只属于后端处理所需；该属性使用了TRANSIENT且不为Public，不会保存到TradeData里
	 */
	private transient Map<String, Object> tempArea = new HashMap<String, Object>();

	/**
	 * 交易公共初始化，不可覆盖
	 * 该方法使用final，不会被代理的
	 */
	public final int init()  throws Exception {
		try {
			TradeCodeDM tradeCodeDM = tradeBiz.findTradeCode(Integer.valueOf(getStoreData(ITradeKeys.T_TRADE_EXPRESSION_ID)), getStoreData(ITradeKeys.T_TRADE_CODE));
			if (tradeCodeDM == null) 
				throw new ViewException("没有交易〖"+getStoreData(ITradeKeys.T_TRADE_CODE)+"〗交易属性");
			putContext(ITradeContextKey.TRADE_CODE_DM, tradeCodeDM);
			
			onInit();
			
			currentTradeEventBiz = getCurrentTradeEvent();
			if (currentTradeEventBiz != null)
				currentTradeEventBiz.onInit(this);
		} catch (BasicRuntimeException bre) {	// 业务异常
			ExceptionLevel exceptionLevel = bre.getLevel();
			if (exceptionLevel == ExceptionLevel.WARN)
				logger.warn ("init发生业务警告:", bre);
			if (exceptionLevel == ExceptionLevel.ERROR || exceptionLevel == ExceptionLevel.FATAL) {
				logger.error("init发生业务错误:", bre);
				pushError(bre.getMessage(), true);
				this.exit(0); // exit内部会抛出TradeBreakException
			}
			throw new TradeBreakException(exceptionLevel.getLevel(), bre.getMessage());
		}  catch (TradeBreakException e) {		// 事件中断
			if (e.getLevel() == ExceptionLevel.ERROR.getLevel() || e.getLevel() == ExceptionLevel.FATAL.getLevel()) {
				logger.error("init发生事件中断:", e);
				pushError(e.getMessage(), true);
				this.exit(0); // exit内部会抛出TradeBreakException
			}
			throw e;
		}  catch (Exception e) {
			logger.error("init发生异常:", e);
			try {
				this.exit(0);
			} catch (TradeBreakException be) {
			}
			throw e;
		} finally {
			ContextHelper.destory();			// 销毁ThreadLocal的上下文
		}
		return 0;
	}
	
	/**
	 * 具体交易个性初始化（由公共流程根据情况调用）
	 * @throws Exception
	 */
	public abstract void onInit() throws Exception;
	
	/**
     * 用户在界面按提交键时, 调用此方法. 开发交易时可在该方法中定义交易逻辑行为.
     * 具体交易个性提交，请覆盖此方法
     */
	public void onCommit() throws Exception {
		onCommit(null);
	}
	public void onCommit(String message) throws Exception {
		if (currentTradeEventBiz != null)
			currentTradeEventBiz.onCommit(this, message);
		// special code overwrite
	}
	
	/**
	 * 函数名称：处理界面发来的非组件消息 
	 * 函数功能：处理界面发来的非组件消息,该方法常常用于复杂的嵌套交易联动，或者特殊快捷键触发交易逻辑
	 * 具体交易个性消息处理，请覆盖此方法
	 */
	public void onMessage(String app, String message) throws Exception {
		if (currentTradeEventBiz != null)
			currentTradeEventBiz.onMessage(this, app, message);
		// special code overwrite
    }
	
	/**
	 * 函数名称：启动嵌套交易 
	 * 函数功能：调用{@link #suspendAndOpenTrade(String, String, Map, String[], String...)}启动嵌套交易, 当嵌套交易结束后, 系统会调用本方法送回子交易结果.
	 * 具体交易个性处理，请覆盖此方法
	 * @param suspendResult 套交易结束后的返回数据
	 */
	@SuppressWarnings("rawtypes")
	public void onResume(Map suspendResult) throws Exception {
		if (currentTradeEventBiz != null)
			currentTradeEventBiz.onResume(this, suspendResult);
		// special code overwrite
    }
	
	/**
	 * 函数名称：退出交易（通知ABC关闭交易画面）
	 * 函数功能：退出交易，供交易子类调用,向ui发送退出操作命令，然后ui请求关闭交易，并且取回交易执行结果
	 * 具体交易个性交易退出前处理，请覆盖此方法
	 * @param type 退出类型，0正常退出，其他是错误码
	 */
    public void exit(int type) {
    	if (currentTradeEventBiz != null) 
    		currentTradeEventBiz.preExit(this);
    	// special code overwrite
        super.exit(type);
    }
    
    /**
     * 函数名称：退出交易清理 （ABC交易画面关闭后，回调关闭ABS端交易时做的清理，故只可做服务端资源的清理）
     * 函数功能：退出交易的时候清理,交易执行exit()的时候会自动调用，需要清理的时候可以覆盖此方法
     * 具体交易个性交易退出后处理，请覆盖此方法
     */
    public void clear() throws Exception {
    	if (currentTradeEventBiz != null)
    		currentTradeEventBiz.posExit();
    	// special code overwrite
    }
	
    /**
     * 特殊交易覆盖该方法，获取特殊交易事件处理
     * @return
     * @throws Exception
     */
	public AbstractTradeTypeEventBiz getCurrentTradeEvent() throws Exception {
    	if (currentTradeEventBiz != null)
    		return currentTradeEventBiz;
    	// 根据类型(包括特殊交易、依赖关系)+机构筛选出适合的交易类型公共事件处理，再在里面根据交易属性以其状态判断
    	List<String> branchs = branchBiz.getSuperBranch(getStoreData(ITradeKeys.G_QBR));
    	branchs.add(0, getStoreData(ITradeKeys.G_QBR));
    	TradeCodeDM  tradeCodeDM  = (TradeCodeDM)getContext(ITradeContextKey.TRADE_CODE_DM);
    	AbstractTradeTypeEventBiz allBranchTradeTypeEventBiz = null;
    	for (AbstractTradeTypeEventBiz tradeEventBiz : allTradeEventBizs) {
    		if (tradeCodeDM.getType().equals(tradeEventBiz.getType())) {
    			if (branchs != null && branchs.size() > 0) {
    				for (String branch : branchs) {
    					if (branch.equals(tradeEventBiz.getBranch())) {
        					currentTradeEventBiz = tradeEventBiz;
        					break;
        				}
    				}
        		}
    			if (tradeEventBiz.getBranch() == null || "".equals(tradeEventBiz.getBranch()))
    				allBranchTradeTypeEventBiz = tradeEventBiz;
    		}
    		if (currentTradeEventBiz != null)
    			break;
    	}
    	if (currentTradeEventBiz == null)
    		currentTradeEventBiz = allBranchTradeTypeEventBiz;
    	return currentTradeEventBiz;
    }

	/**
	 * 获取交易执行过程中需要的上下文，只适用后端逻辑处理
	 */
	public Map<String, Object> getContext() {
		return context;
	}
	/**
	 * 获取交易执行过程中需要的上下文，只适用后端逻辑处理
	 */
	public Object getContext(String key) {
		return context.get(key);
	}
	/**
	 * 设置交易执行过程中需要的上下文，只适用后端逻辑处理
	 */
	public void putContext(String key, Object value) {
		context.put(key, value);
	}
	/**
	 * 获取交易事件过程中需要的临时值，只适用后端逻辑处理
	 */
	public Map<String, Object> getTempArea() {
		return tempArea;
	}
	/**
	 * 获取交易事件过程中需要的临时值，只适用后端逻辑处理
	 */
	public Object getTempArea(String key) {
		return tempArea.get(key);
	}
	/**
	 * 设置交易事件过程中需要的临时值，只适用后端逻辑处理
	 */
	public void putTempArea(String key, Object value) {
		tempArea.put(key, value);
	}
    
	/**
	 * 设置默认上送的通讯码
	 * @param commCode
	 */
	public void setDefaultCommCode(String commCode) {
		TradeCodeDM tradeCodeDM = (TradeCodeDM)getContext(ITradeContextKey.TRADE_CODE_DM);
		boolean isHas = false;
		for (TradePropDM tradePropDM : tradeCodeDM.getTradePropList()) {
			if (tradePropDM.getCommCode().getCommCode().equals(commCode)) {
				isHas = true;
				break;
			}
		}
		if (!isHas)
			throw new ViewException("〖"+getStoreData(ITradeKeys.T_TRADE_CODE)+"〗交易没有上送通讯码〖"+commCode+"〗");
		boolean isPermission = tradeBiz.checkTradePermission(tradeCodeDM.getExpressionid(), getStoreData(ITradeKeys.T_TRADE_CODE), commCode, (String)getTellerInfo().get(ITradeKeys.G_TELLER));
		if (!isPermission)
			throw new ViewException("柜员〖"+getTellerInfo().get(ITradeKeys.G_TELLER)+"〗无权执行通讯码〖"+commCode+"〗的权限");
		logger.info("交易〖{}〗设置默认上送通讯码为〖{}〗", getStoreData(ITradeKeys.T_TRADE_CODE), commCode);
		putStoreData(ITradeKeys.T_COMM_CODE, commCode);	
		changeViewStyle(commCode);
	}
	
	/**
	 * 切换通讯码时，界面样式的变化
	 * @param commCode
	 */
	protected abstract void changeViewStyle(String commCode);
	/**
	 * 提交成功后和交易退出前的交易特殊处理
	 * @param message
	 */
	public void posCommit(String message){
	}
    
    /**
     * 嵌套通讯
     * @param commCode
     */
	public void nestedCommun(String commCode) {
		TradeDataDM tradeDataDM = TradeHelper.getTradeData(this);
		ContextHelper.setContext(ITradeContextKey.TRADE_DATA_DM, tradeDataDM);
		try {
			commBizProvider.commCommit(commCode, tradeDataDM);
		} catch (RpcException e) {
			logger.error("后台通讯报错级别：{} 原因：{} 后台原因：{}", e.getLevel().getDesc(), e.getMessage(), e.getMsg());
			tempArea.put(commCode+"_msg", e.getMsg());
			if (e.getLevel() != ExceptionLevel.DEBUG) {
				OpenViewArgDM OpenViewArgDM = new OpenViewArgDM();
				OpenViewArgDM.setWindow(true);
				OpenViewArgDM.setImportVar(ImmutableMap.of("#msgLevel", e.getLevel().toJsonString(), "#msgInfo", JsonUtil.obj2json(e.getMsg())));
				viewOpenBiz.asycOpenView(this, "msg", OpenViewArgDM);
			}
			if (e.getLevel() == ExceptionLevel.ERROR || e.getLevel() == ExceptionLevel.FATAL) 
				throw new TradeBreakException(e.getLevel().getLevel(), null);
		} finally {
			ContextHelper.removeContext(ITradeContextKey.TRADE_DATA_DM);
		}
		TradeHelper.setTradeData(this, tradeDataDM);
	}
}
