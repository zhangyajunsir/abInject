package cn.com.agree.ab.common.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.BranchBiz;
import cn.com.agree.ab.common.dao.OrgDao;
import cn.com.agree.ab.common.dao.entity.OrgEntity;
import cn.com.agree.ab.common.dm.OrgDM;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.CacheEvict;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.ab.lib.annotation.Transaction;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = BranchBiz.class)
@Singleton
@Biz("branchBiz")
public class BranchBizImpl implements BranchBiz {

	@Inject
	@Named("orgDao") 
	private OrgDao orgDao;
	
	@Override
	@Cacheable
	public OrgDM getBranchInfo(String orgCode) {
		if (orgCode == null || orgCode.equals(""))
			return null;
		OrgEntity orgEntity = orgDao.findOrg(orgCode);
		if (orgEntity == null)
			return null;
		OrgDM orgDM = new OrgDM();
		orgDM.cloneValueFrom(orgEntity);
		return orgDM;
	}

	/**通过分支机构号查询全部父级机构号（全部上级）*/
	@Override
	public List<String> getSuperBranch(String orgCode) {
		List<String> superBranch = new ArrayList<String>();
		while (true) {
			OrgDM orgDM = getBranchInfo(orgCode);
			if(orgDM == null || orgDM.getParentManageOrg() == null || orgDM.getParentManageOrg().trim().equals("")){
				break;
			}
			orgCode = orgDM.getParentManageOrg();
			superBranch.add(orgCode);
		}
		
		return superBranch;
	}
	
	
	
	/**添加或更新机构表中的机构信息 */
	
	@Override
	@Transaction
	@CacheEvict(method = "getBranchInfo", key="${p0.code}")
	public void addOrUpdateOrgInfo(OrgDM orgDM){
		if(orgDM == null){
			throw new BizException(ExceptionLevel.ERROR, "机构信息不可为空");
		}
		if("".equals(orgDM.getCode()) || orgDM.getCode() == null){
			throw new BizException(ExceptionLevel.ERROR, "机构号不可为空");
		}
		
		boolean isUpdate = false;
		OrgDM _orgDM_ = getBranchInfo(orgDM.getCode());
		if(_orgDM_ == null){
			_orgDM_ = orgDM ;
		}
		else{
			ObjectMergeUtil.merge(_orgDM_, orgDM, true ,true);
			isUpdate = true ;
		}
		//更新机构信息表AB_ORG
		OrgEntity orgEntity = _orgDM_.convertTo(OrgEntity.class) ;
		orgEntity.setAvailable(1);
		orgEntity.setLastModifyDate(new Date());
		try{
			if(isUpdate){
				orgDao.update(orgEntity);
			}
			else{
				orgDao.save(orgEntity);
			}
		}catch(DaoException e){
			throw new BizException(ExceptionLevel.ERROR, "新增或更新机构信息表失败", e.getCause());
		}		
		
	}

}
