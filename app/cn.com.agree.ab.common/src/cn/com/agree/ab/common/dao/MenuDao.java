package cn.com.agree.ab.common.dao;

import java.util.List;

import cn.com.agree.ab.common.dao.entity.MenuEntity;
import cn.com.agree.ab.common.dao.entity.MenuTradeEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface MenuDao extends EntityDao<MenuEntity> {

	public List<MenuEntity> findAllMenu();

	public List<MenuTradeEntity> findAllMenuTradeMapping();
}
