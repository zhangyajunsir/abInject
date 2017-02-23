package jsrccb.common.dao.impl;

import java.sql.SQLException;

import javax.inject.Singleton;

import jsrccb.common.dao.PinKeyDao;
import jsrccb.common.dao.entity.PinKeyEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = PinKeyDao.class)
@Singleton
@Dao("pinKeyDao")
public class PinKeyDaoImpl  extends EntityDaoImpl<PinKeyEntity> implements PinKeyDao {

	public PinKeyEntity getPinKey(String devId) {
		/* devId带点号
		return get(new Where(entityRecordTemplate.getTableName(), Where.get("devid", devId)));
		*/
		return  get("SELECT devid, mainkey, workkey FROM pinkey WHERE devid = ?", devId);
	}
		
	
	public void updatePinKey(PinKeyEntity pinKeyEntity) {
		if (pinKeyEntity.getDevId() == null) {
			throw new DaoException("设备ID号为空");
		}
		/* devId带点号，造成生成的sql没有where条件
		 * update(pinKeyEntity);
		 */
		String updateSql = "UPDATE pinkey SET workkey = ? WHERE devid = ?";
		try {
			persistence.sql(updateSql, pinKeyEntity.getWorkKey(), pinKeyEntity.getDevId());
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	};
}
