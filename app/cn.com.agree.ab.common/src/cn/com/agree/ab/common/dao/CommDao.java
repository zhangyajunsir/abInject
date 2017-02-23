package cn.com.agree.ab.common.dao;

import cn.com.agree.ab.common.dao.entity.CommCodeEntity;
import cn.com.agree.ab.common.dao.entity.CommLogEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface CommDao extends EntityDao<CommCodeEntity> {
	//查询通讯码表
	public CommCodeEntity findCommCode(String commCode);
	//新增通讯码表
	public void insertCommCode(CommCodeEntity CommCodeEntity);
	//删除通讯码表
	public void deleteCommCode(CommCodeEntity CommCodeEntity);
	//修改通讯码表
	public void updateCommCode(CommCodeEntity CommCodeEntity);
	
	
	
	
	// 新增通讯流水日志
	public void insertCommLog(CommLogEntity commLogEntity);

	
}
