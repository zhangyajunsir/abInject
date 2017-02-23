package jsrccb.common.dao;

import java.util.Date;

import cn.com.agree.ab.lib.dao.BasicDao;

public interface OldDao extends BasicDao {

	public void clearSummon(String teller, String date);
	
	public void clearTLmsg (String teller);
	
	public void updateTellerLogin(String teller, String hostTTY, String hostName, Date time);
	
	public boolean outClearSwitch(String LVL_BRH_ID, String BRANCH);
	
	public String  outClearAmount(String LVL_BRH_ID);
	
	public void clearOrgNum(String orgcode , String date);
	
	public boolean crownWordNumberSwitch(String LVL_BRH_ID, String BRANCH); 
}
