package cn.com.agree.ab.common.biz;

import cn.com.agree.ab.common.dm.TellerDM;

public interface TellerBiz {

	public void addOrUpdateTeller(TellerDM tellerDM);
	
	public TellerDM getTeller(String tellerCode);
	
}
