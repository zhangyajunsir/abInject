package cn.com.agree.ab.common.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.ProcedureDao;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.BasicDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ProcedureDao.class)
@Singleton
@Dao("procedureDao")
public class ProcedureDaoImpl  extends BasicDaoImpl implements ProcedureDao {

	public void dayEndSvr(String date, Integer step) {
		Connection conn = null;
		try {
			conn = persistence.getDataSource().getConnection();
			CallableStatement c = conn.prepareCall("{call SP_PUB_DAYENDSVR(?,?,?,?)}");  
			//给存储过程的第一、二个入参设置值  
			c.setString(1, date);
			c.setInt   (2, step);
			//注册存储过程的第三、四个出参  
			c.registerOutParameter(3, java.sql.Types.VARCHAR);  
			c.registerOutParameter(4, java.sql.Types.VARCHAR);
			//执行存储过程  
			c.execute();  
			//得到存储过程的输出参数值  
			String RETCODE = c.getString(3);
			String RETMSG  = c.getString(4);
			if (!"1".equals(RETCODE)) {
				throw new DaoException("执行存储过程[SP_PUB_DAYENDSVR]未完成：\n"+RETMSG);
			}
		} catch (SQLException e) {
			throw new DaoException("执行存储过程[SP_PUB_DAYENDSVR]发生异常", e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
			
		}
	}
	
}
