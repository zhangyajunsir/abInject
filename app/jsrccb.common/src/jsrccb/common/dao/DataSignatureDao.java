package jsrccb.common.dao;

import jsrccb.common.dao.entity.DataSignatureEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface DataSignatureDao extends EntityDao<DataSignatureEntity> {
	
	public DataSignatureEntity findDataSignature(String orgCode);
	
}
