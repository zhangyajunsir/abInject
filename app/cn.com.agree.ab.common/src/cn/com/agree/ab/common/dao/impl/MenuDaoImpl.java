package cn.com.agree.ab.common.dao.impl;

import java.util.List;

import javax.inject.Singleton;

import cn.com.agree.ab.common.dao.MenuDao;
import cn.com.agree.ab.common.dao.entity.MenuEntity;
import cn.com.agree.ab.common.dao.entity.MenuTradeEntity;
import cn.com.agree.ab.lib.annotation.Dao;
import cn.com.agree.ab.lib.dao.RecordMapper;
import cn.com.agree.ab.lib.dao.impl.EntityDaoImpl;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;
import cn.com.agree.ab.trade.ext.persistence.Where;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = MenuDao.class)
@Singleton
@Dao("menuDao")
public class MenuDaoImpl extends EntityDaoImpl<MenuEntity> implements MenuDao {

	public List<MenuEntity> findAllMenu() {
		Where condition = new Where(entityRecordTemplate.getTableName(), Where.get("available", 1));
		List<MenuEntity> menuEntitys = query(condition, "parent_id,sort,id");
		return menuEntitys;
	}
	
	public List<MenuTradeEntity> findAllMenuTradeMapping() {
		String sql = "SELECT id, menu_id, trade_id, expression_id, available, last_modify_user, last_modify_date FROM ab_menu_trade WHERE available = ? order by menu_id,sort,id";
		
		return search(sql, new RecordMapper<MenuTradeEntity>() {
			@Override
			public MenuTradeEntity recordRow(Record record, int index) { 
				MenuTradeEntity entity;
				try {
					entity = MenuTradeEntity.class.newInstance();
				} catch (Exception e1) {
					throw new DaoException("["+MenuTradeEntity.class.getName()+"]默认构造器实例化失败");
				}
				MappingAccessor.getInstance().fillRecord(entity, null, record);
				return entity;
			}
		}, 1);
	}
	
	
}
