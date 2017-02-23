package cn.com.agree.ab.common.biz.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.SerialNumBiz;
import cn.com.agree.ab.common.dao.SerialNumDao;
import cn.com.agree.ab.common.dao.entity.TellerFlowEntity;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Transaction;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.trade.core.tools.StringUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.common.base.Preconditions;

@AutoBindMapper(baseClass = SerialNumBiz.class)
@Singleton
@Biz("serialNumBiz")
public class SerialNumBizImpl implements SerialNumBiz {
	@Inject
	@Named("serialNumDao")
	private SerialNumDao serialNumDao;

	@Override
	public String generateFrontSerialNum(String teller, Integer tradeId) {
		StringBuilder sb = new StringBuilder();
		if (teller == null || teller.length() == 0)
			sb.append("anonymous");
		else
			sb.append(teller);
		sb.append(tradeId);
		sb.append(DateUtil.getDateToString().substring(8));
		return sb.toString();
	}

	@Override
	@Transaction
	public String generateTellerSerialNum(String teller, String txDate, TellerFlowType tellerFlowType) {
		Preconditions.checkState(teller != null && !teller.equals(""), "柜员号为空");
		Preconditions.checkState(txDate != null && !txDate.equals("") && txDate.length() == 8, "会计日期不正确");
		Preconditions.checkState(tellerFlowType != null, "柜员流水类型未输入");
		int serialNum = 0;
		TellerFlowEntity tellerFlowEntity = serialNumDao.findTellerFlowNum(teller, txDate, tellerFlowType.getType());
		if (tellerFlowEntity == null) {
			serialNum++;
			tellerFlowEntity = new TellerFlowEntity();
			tellerFlowEntity.setTellerCode(teller);
			tellerFlowEntity.setTxData(txDate);
			tellerFlowEntity.setFlowType(tellerFlowType.getType());
			tellerFlowEntity.setSerialNum(serialNum);
			tellerFlowEntity.setAvailable(1);
			tellerFlowEntity.setLastModifyUser(teller);
			tellerFlowEntity.setLastModifyDate(new Date());
			serialNumDao.insertTellerFlow(tellerFlowEntity);
		} else {
			serialNum = tellerFlowEntity.getSerialNum();
			serialNum++;
			tellerFlowEntity.setSerialNum(serialNum);
			tellerFlowEntity.setLastModifyUser(teller);
			tellerFlowEntity.setLastModifyDate(new Date());
			serialNumDao.updateTellerFlow(tellerFlowEntity);
		}
		return tellerFlowType.getType()+StringUtil.fixFill(""+serialNum, "0", 8);
	}

}
