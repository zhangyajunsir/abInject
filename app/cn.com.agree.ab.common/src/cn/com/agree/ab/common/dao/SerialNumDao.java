package cn.com.agree.ab.common.dao;

import cn.com.agree.ab.common.dao.entity.TellerFlowEntity;
import cn.com.agree.ab.lib.dao.BasicDao;

public interface SerialNumDao extends BasicDao {

	public TellerFlowEntity findTellerFlowNum(String teller, String txDate, int flowType);
	
	public void insertTellerFlow(TellerFlowEntity tellerFlowEntity);
	
	public void updateTellerFlow(TellerFlowEntity tellerFlowEntity);
	
}
