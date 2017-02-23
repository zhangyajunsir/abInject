package jsrccb.common.biz.impl.event.prop;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.AuthBiz;
import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthWay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradePropEventBiz;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractTradePropEventBiz.class, multiple = true)
@Singleton
@Biz("authorizeEventBiz")
public class AuthorizeEventBizImpl extends AbstractTradePropEventBiz {
	private static final Logger	logger	= LoggerFactory.getLogger(AuthorizeEventBizImpl.class);
	@Inject
	@Named("authBiz")
	private AuthBiz authBiz;
	@Inject
	@Named("abViewOpenBiz")
	private IViewOpenBiz viewOpenBiz;
	
	public void onInit   (AbstractCommonTrade trade) {
		// 远程授权（授权方）界面初始化
		
	}
	
	public void preCommit(AbstractCommonTrade trade, String message) {
		AuthDM authDM = (AuthDM)trade.getContext().get(ITradeContextKey.AUTH_DM);
		// 1.判断是否需要前端授权
		if (authDM == null) {
			authDM = authBiz.getLocalAuth((TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM));
			if (authDM == null || authDM.getAuthStatus() != AuthStatus.NEED_AUTH) {
				logger.debug("通讯前检查无需柜面授权!");
				return;
			}
			trade.putContext(ITradeContextKey.AUTH_DM, authDM);
		}
		// 2.通讯前有授权传入授权要素后调起授权对话框
		if(authDM.getAuthStatus() != null && authDM.getAuthStatus() == AuthStatus.NEED_AUTH){
			if (authDM.getAuthWay() == null || authDM.getAuthWay() == AuthWay.LOCAL_AUTH || authDM.getAuthWay() == AuthWay.ORG_REMOTE_AUTH) {
				OpenViewArgDM OpenViewArgDM = new OpenViewArgDM();
				OpenViewArgDM.setWindow(true);
				viewOpenBiz.syncOpenView(trade, "auth", OpenViewArgDM);	//调起后在CommonTradeEventBizImpl的onResume返回
			}
		}
	}
	
	public void onCommit (AbstractCommonTrade trade, String message) {
		AuthDM authDM = (AuthDM)trade.getContext().get(ITradeContextKey.AUTH_DM);
		if (authDM == null || authDM.getAuthStatus() != AuthStatus.NEED_AUTH 
				|| (authDM.getAuthWay() != AuthWay.SUPER_ORG_REMOTE_AUTH && authDM.getAuthWay() != AuthWay.AUTH_CENTER_REMOTE_AUTH)) {
			return;
		}
		// 远程异步授权（当前交易是要关闭的），创建任务
		
		// 结束交易
		
	}
	
	public void posCommit(AbstractCommonTrade trade, String message) {
		AuthDM authDM = (AuthDM)trade.getContext().get(ITradeContextKey.AUTH_DM);
		if (authDM != null && (authDM.getAuthStatus() == AuthStatus.CANCEL_AUTH || authDM.getAuthStatus() == AuthStatus.REJECT_AUTH))
			trade.getContext().remove(ITradeContextKey.AUTH_DM);
		
		
	}

	public void onResume (AbstractCommonTrade trade, Map<?, ?> suspendResult) {
		AuthDM authDM = (AuthDM)trade.getContext().get(ITradeContextKey.AUTH_DM);
		if (authDM == null) {
			return;
		}
		if (authDM.getAuthStatus() == null || authDM.getAuthStatus() == AuthStatus.NO_AUTH)
			return;
		if (authDM.getAuthStatus() == AuthStatus.NEED_AUTH)
			trade.interrupt();
		if (authDM.getAuthStatus() == AuthStatus.CANCEL_AUTH) {
			trade.getContext().remove(ITradeContextKey.AUTH_DM);
			throw new BizException("授权被取消");
		}
		if (authDM.getAuthStatus() == AuthStatus.REJECT_AUTH) {
			trade.getContext().remove(ITradeContextKey.AUTH_DM);
			throw new BizException("授权被拒绝");
		}
		if (authDM.getAuthStatus() == AuthStatus.PASS_AUTH) {
			try {
				trade.onCommit();
			} catch (Exception e) {
				throw new BizException("授权后再次提交失败", e);
			}
		}
	}
}
