package jsrccb.common.dao.impl;

import javax.inject.Singleton;

import jsrccb.common.dao.DataSignatureDao;
import jsrccb.common.dao.entity.DataSignatureEntity;

import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.ab.trade.ext.persistence.Where.WhereSegment;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = DataSignatureDao.class)
@Singleton
@Dao("dataSignatureDao")
public class DataSignatureDaoImpl extends EntityDaoImpl<DataSignatureEntity> implements DataSignatureDao {

	@Override
	public DataSignatureEntity findDataSignature(String orgCode) {
		WhereSegment ws = Where.and(Where.get("orgcode", orgCode), Where.get("available", 1));
		Where condition = new Where(entityRecordTemplate.getTableName(), ws);
		DataSignatureEntity DataSignatureEntity = get(condition);
		return DataSignatureEntity;
	}
	
	
}
