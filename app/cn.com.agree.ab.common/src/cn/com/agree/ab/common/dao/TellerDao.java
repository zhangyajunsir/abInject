package cn.com.agree.ab.common.dao;

import java.util.List;

import cn.com.agree.ab.common.dao.entity.TellerEntity;
import cn.com.agree.ab.common.dao.entity.TellerPostEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface TellerDao extends EntityDao<TellerEntity> {

	public TellerEntity getTeller(String tellerCode);
	
	public List<TellerPostEntity> getTellerAllPost(String tellerCode);
	
	public void addTellerPost(TellerPostEntity tellerPostEntity);
	
	public void updTellerPost(TellerPostEntity tellerPostEntity);
}
