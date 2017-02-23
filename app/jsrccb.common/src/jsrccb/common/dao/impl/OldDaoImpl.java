package jsrccb.common.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import jsrccb.common.dao.OldDao;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.BasicDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.ConditionOperator;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = OldDao.class)
@Singleton
@Dao("oldDao")
public class OldDaoImpl extends BasicDaoImpl implements OldDao {
	
	public void clearSummon(String teller, String date) {
		try {
			persistence.delete(new Where("SUMMONS", Where.and(Where.get("INPUTTELLERID", teller), Where.get("TXDATE", ConditionOperator.NE, date))));
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	public void clearTLmsg (String teller) {
		try {
			persistence.delete(new Where("TLMSG", Where.get("FROMTL", teller)));
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	public void updateTellerLogin(String teller, String hostTTY, String hostName, Date time) {
		String timeStr = DateUtil.dateToDateString(time, DateUtil.TIME_STR_FORMAT);
		try {
			Record record = new Record("TELLERLOGIN");
			record.setKeyColumn("TELLER");
			record.set("TELLER",   teller);
			record.set("TTYNO",    hostTTY);
			record.set("HOSTNAME", hostName);
			record.set("STIME",    timeStr);
			persistence.insert(record);
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	public boolean outClearSwitch(String LVL_BRH_ID, String BRANCH) {
		String sql = "SELECT SMARTPIN_FLAG  FROM FT_SMARTPIN_INFO WHERE LVL_BRH_ID = ? AND QBRANCH = ? ";
		List<Map<String, Object>> res = search(sql, LVL_BRH_ID, BRANCH);
		if (res == null || res.isEmpty())
			return false;
		if ("1".equals(res.get(0).get("SMARTPIN_FLAG")))
			return true;
		return false;
	}
	
	public String  outClearAmount(String LVL_BRH_ID) {
		String sql = "SELECT COINS, AMOUNT  FROM FT_SMARTPIN_LIMIT_AMOUNT WHERE LVL_BRH_ID = ?";
		List<Map<String, Object>> res = search(sql, LVL_BRH_ID);
		if (res == null || res.isEmpty())
			return null;
		return res.get(0).get("AMOUNT").toString();
	}
	
	public void clearOrgNum(String orgcode , String date){
//		String SqlDetOrg = "DELETE FROM orgNum WHERE orgcode = '" + orgcode + "' AND datNo != '" + date + "'";
		try {
			persistence.delete(new Where("ORGNUM", Where.and(Where.get("ORGCODE", orgcode), Where.get("DATNO", ConditionOperator.NE, date))));
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	public boolean crownWordNumberSwitch(String LVL_BRH_ID, String BRANCH) {
		String sql = "SELECT GZH_FLAG  FROM BANK_GZH_INFO WHERE GZH_BRH = ? AND GZH_QBRANCH = ? ";
		List<Map<String, Object>> res = search(sql, LVL_BRH_ID, BRANCH);
		if (res == null || res.isEmpty())
			return false;
		if ("1".equals(res.get(0).get("GZH_FLAG")))
			return true;
		return false;
	}
}
