package cn.com.agree.ab.common.dao;

import cn.com.agree.ab.lib.dao.BasicDao;

public interface ProcedureDao extends BasicDao {
	
	public void dayEndSvr(String date, Integer step);
	
}
