package cn.com.agree.ab.common.biz.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jodd.props.Props;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.CommLogBiz;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.dao.CommDao;
import cn.com.agree.ab.common.dao.entity.CommLogEntity;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.CommLogDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.lib.annotation.Async;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.common.base.Preconditions;

@AutoBindMapper(baseClass = CommLogBiz.class)
@Singleton
@Biz("commLogBiz")
public class CommLogBizImpl implements CommLogBiz {
	@SuppressWarnings("unused")
	private static final Logger	logger	= LoggerFactory.getLogger(CommLogBizImpl.class);
	@Inject
	@Named("commDao")
	private CommDao commDao;
	@Inject
	@Named("tradeBiz")
	private TradeBiz tradeBiz;
	@Inject
	private ConfigManager configManager;

	@Override
	@Async
	public void addCommLog(CommLogDM commLogDM) {
		Props iniCfg = configManager.getUtilIni();
		String log = iniCfg.getValue("MAIN.COMMLOG");
		if (!"1".equals(log)) {
			return;
		}
		/**
		 * 此方法调用并发量大，需要对对象拷贝和ORM进行优化
		 */
		if (commLogDM == null)
			return;
		Preconditions.checkNotNull(commLogDM.getTranDate(),   "交易日期为空");
		Preconditions.checkNotNull(commLogDM.getTellerCode(), "柜员号为空");
		Preconditions.checkNotNull(commLogDM.getTranSeq(),    "交易流水为空");
		Preconditions.checkNotNull(commLogDM.getCommCode(),   "通讯码为空");
		
//		CommLogEntity commLogEntity = commLogDM.convertTo(CommLogEntity.class);
		CommLogEntity commLogEntity = new CommLogEntity();
		try {
			BeanUtils.copyProperties(commLogEntity, commLogDM);
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}// 目标对象，源对象
		commLogEntity.setAvailable(1);
		commLogEntity.setLastModifyUser(commLogDM.getTellerCode());
		commLogEntity.setLastModifyDate(new Date());
		commDao.insertCommLog(commLogEntity);
	}

	public CommLogDM initCommLog(String commCode, TradeDataDM tradeDataDM) {
		Preconditions.checkNotNull(commCode,   "通讯码为空");
		TradeCodeDM tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
		CommCodeDM  commCodeDM  = tradeBiz.findCommCode(commCode);
		TradePropDM tradePropDM = null;
		for (TradePropDM _tradePropDM_ : tradeCodeDM.getTradePropList()) {
			if (_tradePropDM_ != null && _tradePropDM_.getCommCode() != null && commCode.equals(_tradePropDM_.getCommCode().getCommCode())) {
				tradePropDM = _tradePropDM_;
				break;
			}
		}
		
		CommLogDM commLogDM = new CommLogDM();
		commLogDM.setUuid(UUID.randomUUID().toString().replaceAll("-", "").replaceAll("_", ""));
		commLogDM.setTranDate((String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_DATE));
		commLogDM.setTellerCode((String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_TELLER));
		commLogDM.setCommCode(commCode);
		if (commCodeDM != null)
			commLogDM.setSystemCode(commCodeDM.getSystemCode());
		commLogDM.setFrontSeq((String)tradeDataDM.getStoreData().get(ITradeKeys.T_SEQNO));
		commLogDM.setFrontCode(tradeCodeDM.getCode());
		commLogDM.setFrontName(tradeCodeDM.getName());
		commLogDM.setOrgCode((String)tradeDataDM.getTellerInfo().get(ITradeKeys.G_QBR));
		if (tradePropDM != null)
			commLogDM.setBusinessType(tradePropDM.getBusinessType());
		commLogDM.setRspStatus(-1);
		
		return commLogDM;
	}
	
}
