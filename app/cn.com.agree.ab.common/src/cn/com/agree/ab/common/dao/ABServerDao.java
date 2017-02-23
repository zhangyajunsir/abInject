package cn.com.agree.ab.common.dao;

import java.util.List;

import cn.com.agree.ab.common.dao.entity.ABServerEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface ABServerDao extends EntityDao<ABServerEntity>{

	public ABServerEntity findABServer(String hostName);
	
	public List<ABServerEntity> queryAllABServer();
}
