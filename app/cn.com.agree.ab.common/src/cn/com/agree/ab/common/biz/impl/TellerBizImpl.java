package cn.com.agree.ab.common.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.TellerBiz;
import cn.com.agree.ab.common.dao.TellerDao;
import cn.com.agree.ab.common.dao.entity.TellerEntity;
import cn.com.agree.ab.common.dao.entity.TellerPostEntity;
import cn.com.agree.ab.common.dm.TellerDM;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.CacheEvict;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.ab.lib.annotation.Transaction;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.DaoException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = TellerBiz.class)
@Singleton
@Biz("tellerBiz")
public class TellerBizImpl implements TellerBiz {
	@Inject
	@Named("tellerDao")
	private TellerDao tellerDao;

	@Override
	@Transaction
	@CacheEvict(method = "getTeller", key="${p0.code}")
	public void addOrUpdateTeller(TellerDM tellerDM) {
		if (tellerDM == null)
			throw new BizException(ExceptionLevel.ERROR, "柜员信息不可为空");
		if (tellerDM.getCode() == null || tellerDM.getCode().equals(""))
			throw new BizException(ExceptionLevel.ERROR, "柜员号码不可为空");
		boolean isUpdate = false;
		TellerDM _tellerDM_ = getTeller(tellerDM.getCode());
		if (_tellerDM_ == null)
			_tellerDM_ = tellerDM;
		else {
			ObjectMergeUtil.merge(_tellerDM_, tellerDM, true, true);
			isUpdate = true;
		}
		// 更新柜员信息表
		TellerEntity tellerEntity = _tellerDM_.convertTo(TellerEntity.class);
		tellerEntity.setAvailable(1);
		tellerEntity.setLastModifyUser(tellerDM.getCode());
		tellerEntity.setLastModifyDate(new Date());
		try {
			if (isUpdate)
				tellerDao.update(tellerEntity);
			else
				tellerDao.save(tellerEntity);
		} catch (DaoException e) {
			throw new BizException(ExceptionLevel.ERROR, "新增或更新柜员表失败", e.getCause());
		}
		// 更新柜员权限表
		try {
			Set<Integer> tellerPostIds = new HashSet<Integer>();
			if (tellerDM.getTellerPosts() != null)
				tellerPostIds.addAll(tellerDM.getTellerPosts());
			List<TellerPostEntity> tellerPostEntitys = tellerDao.getTellerAllPost(tellerDM.getCode());
			if (tellerPostEntitys != null) {
				for (TellerPostEntity tellerPostEntity : tellerPostEntitys) {
					if (tellerPostIds.contains(tellerPostEntity.getPostId())) {
						if (tellerPostEntity.getAvailable() != 1) {
							// 更新为可用状态
							tellerPostEntity.setAvailable(1);
							tellerDao.updTellerPost(tellerPostEntity);
						}
						tellerPostIds.remove(tellerPostEntity.getPostId());
					} else {
						if (tellerPostEntity.getAvailable() == 1) {
							// 更新为不可用状态
							tellerPostEntity.setAvailable(0);
							tellerDao.updTellerPost(tellerPostEntity);
						}
					}
				}
			}
			for (Integer tellerPostId : tellerPostIds) {
				TellerPostEntity tellerPostEntity = new TellerPostEntity();
				tellerPostEntity.setTellerCode(tellerDM.getCode());
				tellerPostEntity.setPostId(tellerPostId);
				tellerPostEntity.setAvailable(1);
				tellerPostEntity.setLastModifyUser(tellerDM.getCode());
				tellerPostEntity.setLastModifyDate(new Date());
				tellerDao.addTellerPost(tellerPostEntity);
			}
		} catch (DaoException e) {
			throw new BizException(ExceptionLevel.ERROR, "新增或更新柜员权限表失败", e.getCause());
		}
		
	}

	@Override
	@Cacheable
	public TellerDM getTeller(String tellerCode) {
		if (tellerCode == null || "".equals(tellerCode))
			throw new BizException(ExceptionLevel.ERROR, "柜员号不可为空");
		TellerEntity tellerEntity = null;
		try {
			tellerEntity = tellerDao.getTeller(tellerCode);
		} catch (DaoException e) {
			throw new BizException(ExceptionLevel.ERROR, "获取柜员失败", e.getCause());
		}
		TellerDM tellerDM = null;
		if (tellerEntity != null) {
			tellerDM = new TellerDM();
			tellerDM.cloneValueFrom(tellerEntity);
			if (tellerEntity.getTellerPostList() != null) {
				List<Integer> tellerPostList = new ArrayList<Integer>();
				for (TellerPostEntity tellerPostEntity : tellerEntity.getTellerPostList()) {
					tellerPostList.add(tellerPostEntity.getPostId());
				}
				tellerDM.setTellerPosts(tellerPostList);
			}
		}
		return tellerDM;
	}

}
