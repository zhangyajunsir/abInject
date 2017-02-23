package cn.com.agree.ab.common.biz;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.beanutils.BeanUtils;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.biz.impl.AbstractLocalBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.CommLogDM;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;
import cn.com.agree.ab.lib.utils.ContextHelper;

import com.google.common.base.Preconditions;

/**
 * 此类功能类似工厂模式
 * 不需要预先绑定，但需要添加@Singleton代表单例
 * @author zhangyajun
 */
@Singleton
public class CommBizProvider {
	@Inject
	private Set<CommBiz> commBizs;
	@Inject
	private Set<AbstractLocalBiz> localBizs;
	@Inject
	@Named("tradeBiz")
	private TradeBiz tradeBiz;
	
	/**
	 * 通讯提交
	 * @param trade     交易对象
	 * @param commCode  通讯码
	 */
	public void commCommit(String commCode, TradeDataDM tradeDataDM) {
		long time = System.currentTimeMillis();
		try {
			// 请求数据
			PacketReqDM packetReqDM = new PacketReqDM();
			BeanUtils.copyProperties(packetReqDM, tradeDataDM);	// 目标对象，源对象
			// 通讯逻辑
			PacketRspDM packetRspDM = commCommit(commCode, packetReqDM);
			// 清空上送用的画面数据
			tradeDataDM.setUiData(null);
			// 将结果非NULL值覆盖到交易
			ObjectMergeUtil.merge(tradeDataDM, packetRspDM);
			// 返回是否有警告
			if (packetRspDM != null && packetRspDM.getMsgList() != null && packetRspDM.getMsgList().size() > 0) {
				RpcException rpcException = new RpcException(ExceptionLevel.WARN, "后台警告");
				rpcException.setMsg(packetRspDM.getMsgList());
				throw rpcException;
			}
		} catch (IllegalAccessException e) {
			throw new BizException(e);
		} catch (InvocationTargetException e) {
			throw new BizException(e);
		} finally {
			CommLogDM commLogDM = (CommLogDM)ContextHelper.getContext(ITradeContextKey.COMM_LOG_DM);
			if (commLogDM != null) {
				commLogDM.setTranTime(System.currentTimeMillis()-time);
				commLogDM.setCommTime((Long)ContextHelper.getContext("socketTime"));
			}
		}
	}
	
	/**
	 * 通讯提交
	 * @param commCode    通讯码
	 * @param packetReqDM 请求数据
	 * @return
	 */
	public PacketRspDM commCommit(String commCode, PacketReqDM packetReqDM) {
		CommCodeDM commCodeDM = tradeBiz.findCommCode(commCode);
		Preconditions.checkState(commCodeDM != null, "查找不到通讯码〖"+commCode+"〗对象");
		CommBiz commBiz = getCommBiz(commCodeDM.getSystemCode(), commCode);
		Preconditions.checkState(commBiz != null, "查找不到通讯码〖"+commCode+"〗业务逻辑〖"+commCodeDM.getSystemCode()+"〗对象");
		
		if (commCodeDM.getTransCode() == null || "".equals(commCodeDM.getTransCode())) {
			commCodeDM.setTransCode(commCodeDM.getCommCode());
		}
		// 设置通讯配置信息
		packetReqDM.setCurrentCommCode(commCode);
		packetReqDM.getCommCodeDMMap().put(commCode, commCodeDM);
		
		// 本地逻辑或者通讯逻辑
		PacketRspDM packetRspDM = null;
		packetRspDM = commBiz.exchange(commCodeDM, packetReqDM);
		return packetRspDM;
	}
	
	private CommBiz getCommBiz(String systemCode, String commCode) {
		CommBiz commBiz = null;
		if ("local".equalsIgnoreCase(systemCode)) {
			for (AbstractLocalBiz _localBiz_ : localBizs) {
				if (commCode.equalsIgnoreCase(_localBiz_.commCode())) {
					commBiz = _localBiz_;
					break;
				}
			}
		} else {
			for (CommBiz _commBiz_ : commBizs) {
				if (systemCode.equalsIgnoreCase(_commBiz_.systemCode())) {
					commBiz = _commBiz_;
					break;
				}
			}
		}
		return commBiz;
	}

}
