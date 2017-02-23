package jsrccb.common.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import jsrccb.common.dao.TerminalDeviceDao;
import jsrccb.common.dao.entity.TerminalDeviceEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = TerminalDeviceDao.class)
@Singleton
@Dao("terminalDeviceDao")
public class TerminalDeviceDaoIpml extends EntityDaoImpl<TerminalDeviceEntity> implements TerminalDeviceDao {

	@Override
	public List<TerminalDeviceEntity> findTerminalDevice(String termLtty) {
		String sql = "SELECT term_ltty, term_type, term_value FROM terminaldevice WHERE term_ltty = ?";
		
		return search(sql, new RecordMapper<TerminalDeviceEntity>() {
			@Override
			public TerminalDeviceEntity recordRow(Record record, int index) {
				TerminalDeviceEntity entity;
				try {
					entity = TerminalDeviceEntity.class.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+TerminalDeviceEntity.class.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, termLtty);
		
		/* termLtty可能为ip带了点号，造成平台生成sql异常
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.get("term_ltty", termLtty));
		List<TerminalDeviceEntity> terminalDeviceEntitys = query(condition);
		return terminalDeviceEntitys;
		*/
	}
	
	public TerminalDeviceEntity findTerminalDevice(String termLtty, String termType) {
		String sql = "SELECT term_ltty, term_type, term_value FROM terminaldevice WHERE term_ltty = ? AND term_type = ?";
		List<TerminalDeviceEntity> terminalDeviceEntitys =  search(sql, new RecordMapper<TerminalDeviceEntity>() {
							@Override
							public TerminalDeviceEntity recordRow(Record record, int index) {
								TerminalDeviceEntity entity;
								try {
									entity = TerminalDeviceEntity.class.newInstance();
								} catch (Exception e1) {
									throw new DaoException("["+TerminalDeviceEntity.class.getName()+"]默认构造器实例化失败");
								}
								MappingAccessor.getInstance().fillRecord(entity, null, record);
								return entity;
							}
						}, termLtty, termType);
		if (terminalDeviceEntitys == null || terminalDeviceEntitys.size() == 0)
			return null;
		return terminalDeviceEntitys.get(0);
	}

	public List<String> specialTerminalTeller(String ip) {
		String sql = "SELECT teller FROM specialterminal WHERE term_ip = ? ";
		List<Map<String, Object>> res = search(sql, ip);
		if (res == null || res.isEmpty())
			return null;
		List<String> tellers = new ArrayList<String>();
		for (Map<String, Object> map : res) {
			tellers.add(map.get("teller").toString());
		}
		return tellers;
	}

}
