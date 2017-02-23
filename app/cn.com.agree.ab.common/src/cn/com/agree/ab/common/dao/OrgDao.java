package cn.com.agree.ab.common.dao;

import cn.com.agree.ab.common.dao.entity.OrgEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface OrgDao extends EntityDao<OrgEntity>{
	
	public OrgEntity findOrg(String orgcode);
	
}
