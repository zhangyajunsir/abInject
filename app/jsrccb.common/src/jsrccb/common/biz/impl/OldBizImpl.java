package jsrccb.common.biz.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.OldBiz;
import jsrccb.common.dao.OldDao;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = OldBiz.class)
@Singleton
@Biz("oldBiz")
public class OldBizImpl implements OldBiz {
	@Inject
	@Named("oldDao")
	private OldDao oldDao;

	@Override
	public void clearSummon(String teller, String date) {
		oldDao.clearSummon(teller, date);
	}

	@Override
	public void clearTLmsg (String teller) {
		oldDao.clearTLmsg(teller);
	}

	@Override
	public void updateTellerLogin(String teller, String hostTTY) {
		String hostName = "";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
		oldDao.updateTellerLogin(teller, hostTTY, hostName, new Date());
	}
	
	@Override
	public void clearOrgNum(String orgcode , String date){
		oldDao.clearOrgNum(orgcode, date);
	}

	public boolean crownWordNumberSwitch(String LVL_BRH_ID, String BRANCH) {
		return oldDao.crownWordNumberSwitch(LVL_BRH_ID, BRANCH);
	}
}
