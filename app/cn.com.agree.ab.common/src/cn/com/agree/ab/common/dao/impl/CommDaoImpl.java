package cn.com.agree.ab.common.dao.impl;

import java.sql.SQLException;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.CommDao;
import cn.com.agree.ab.common.dao.entity.CommCodeEntity;
import cn.com.agree.ab.common.dao.entity.CommLogEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = CommDao.class)
@Singleton
@Dao("commDao")
public class CommDaoImpl extends EntityDaoImpl<CommCodeEntity> implements CommDao {
	@Override
	public CommCodeEntity findCommCode(String commCode) {
		WhereSegment ws = Where.and(Where.get("comm_code", commCode), Where.get("available", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), ws);
		CommCodeEntity CommCodeEntity = get(condition);
		return CommCodeEntity;
	}
	@Override
	public void insertCommCode(CommCodeEntity CommCodeEntity) {
		  save(CommCodeEntity);
	}
	@Override
	public void deleteCommCode(CommCodeEntity CommCodeEntity) {
		  delete(CommCodeEntity.getId());
	}
	@Override
	public void updateCommCode(CommCodeEntity CommCodeEntity) {
			update(CommCodeEntity);
	}
	public void insertCommLog(CommLogEntity commLogEntity) {
		/* 高并发情况下，性能低
		try {
			persistence.insert(commLogEntity);
		} catch (SQLException e) {
			throw new DaoException("新增表[ab_comm_log]记录失败", e);
		}
		*/
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ab_comm_log (");
//		sql.append("uuid,");
		sql.append("tran_date,");
		sql.append("teller_code,");
		sql.append("tran_seq,");
		sql.append("comm_code,");
		sql.append("system_code,");
		sql.append("front_seq,");
		sql.append("front_code,");
//		sql.append("front_name,");
		sql.append("tran_time,");
		sql.append("comm_time,");
		sql.append("org_code,");
//		sql.append("auth_teller_code,");
//		sql.append("business_type,");
		sql.append("rsp_status,");
		sql.append("acc_date,");
		sql.append("acc_time,");
		sql.append("rsp_seq,");
		sql.append("rsp_msg,");
//		sql.append("req_data,");
		sql.append("source_seq,");
//		sql.append("debit_account_num,");
//		sql.append("debit_account_name,");
//		sql.append("debit_account_sub_num,");
//		sql.append("debit_account_seq,");
//		sql.append("credit_account_num,");
//		sql.append("credit_account_name,");
//		sql.append("money,");
//		sql.append("curr_type,");
		sql.append("available,");
		sql.append("last_modify_user,");
		sql.append("last_modify_date");
		sql.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		try {
			persistence.sql(sql.toString(), 
//					commLogEntity.getUuid(),
					commLogEntity.getTranDate(),
					commLogEntity.getTellerCode(),
					commLogEntity.getTranSeq(),
					commLogEntity.getCommCode(),
					commLogEntity.getSystemCode()         == null ? "" : commLogEntity.getSystemCode(),
					commLogEntity.getFrontSeq()           == null ? "" : commLogEntity.getFrontSeq(),
					commLogEntity.getFrontCode()          == null ? "" : commLogEntity.getFrontCode(),
//					commLogEntity.getFrontName()          == null ? "" : commLogEntity.getFrontName(),
					commLogEntity.getTranTime()           == null ? 0  : commLogEntity.getTranTime(),
					commLogEntity.getCommTime()           == null ? 0  : commLogEntity.getCommTime(),
					commLogEntity.getOrgCode()            == null ? "" : commLogEntity.getOrgCode(),
//					commLogEntity.getAuthTellerCode()     == null ? "" : commLogEntity.getAuthTellerCode(),
//					commLogEntity.getBusinessType()       == null ? "" : commLogEntity.getBusinessType(),
					commLogEntity.getRspStatus()          == null ? -1 : commLogEntity.getRspStatus(),
					commLogEntity.getAccDate()            == null ? "" : commLogEntity.getAccDate(),
					commLogEntity.getAccTime()            == null ? "" : commLogEntity.getAccTime(),
					commLogEntity.getRspSeq()             == null ? "" : commLogEntity.getRspSeq(),
					commLogEntity.getRspMsg()             == null ? "" : commLogEntity.getRspMsg(),
//					commLogEntity.getReqData(),
					commLogEntity.getSourceSeq()          == null ? "" : commLogEntity.getSourceSeq(),
//					commLogEntity.getDebitAccountNum()    == null ? "" : commLogEntity.getDebitAccountNum(),
//					commLogEntity.getDebitAccountName()   == null ? "" : commLogEntity.getDebitAccountName(),
//					commLogEntity.getDebitAccountSubNum() == null ? "" : commLogEntity.getDebitAccountSubNum(),
//					commLogEntity.getDebitAccountSeq()    == null ? "" : commLogEntity.getDebitAccountSeq(),
//					commLogEntity.getCreditAccountNum()   == null ? "" : commLogEntity.getCreditAccountNum(),
//					commLogEntity.getCreditAccountName()  == null ? "" : commLogEntity.getCreditAccountName(),
//					commLogEntity.getMoney()              == null ? 0  : commLogEntity.getMoney(),
//					commLogEntity.getCurrType()           == null ? "" : commLogEntity.getCurrType(),
					commLogEntity.getAvailable(),
					commLogEntity.getLastModifyUser(),
					commLogEntity.getLastModifyDate()
			);
		} catch (SQLException e) {
			throw new DaoException("新增表[ab_comm_log]记录失败", e);
		}
	}


}
