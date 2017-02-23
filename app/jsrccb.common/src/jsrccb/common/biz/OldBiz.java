package jsrccb.common.biz;


public interface OldBiz {

	public void clearSummon(String teller, String date);
	
	public void clearTLmsg (String teller);
	
	public void updateTellerLogin(String teller, String hostTTY);
	
	public void clearOrgNum(String orgcode , String date);
	
	public boolean crownWordNumberSwitch(String LVL_BRH_ID, String BRANCH); 
}
