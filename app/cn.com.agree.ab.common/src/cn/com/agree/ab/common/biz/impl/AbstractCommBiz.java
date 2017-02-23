package cn.com.agree.ab.common.biz.impl;

import java.util.Date;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cn.com.agree.ab.common.biz.CommBiz;
import cn.com.agree.ab.common.dao.CommDao;
import cn.com.agree.ab.common.dao.entity.CommCodeEntity;
import cn.com.agree.ab.common.dm.CommCodeDM;

import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.CacheEvict;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.ab.lib.annotation.Transaction;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;


@Biz("abstractCommBiz")
public abstract class AbstractCommBiz implements CommBiz{
	@Inject
	@Named("commDao")
	private CommDao commDao;
	@Override
	@Cacheable
	public CommCodeDM  findCommCode(String commCode) {
		CommCodeEntity commcodeEntity = commDao.findCommCode(commCode);
		if(commcodeEntity == null){
			return null;
		}
		CommCodeDM commcodeDM = new CommCodeDM();
		commcodeDM.cloneValueFrom(commcodeEntity);
		return commcodeDM;
		
	}
	@Override
	@Transaction
	@CacheEvict(method="findCommCode",key="${p0.commCode}")
	public void addOrUpdateCommCode(CommCodeDM commCodeDM) {
		boolean isUpdate = false;
		CommCodeDM _CommCodeDM_ = findCommCode(commCodeDM.getCommCode());
		if(_CommCodeDM_ == null){
			_CommCodeDM_=commCodeDM;
		}else {
			isUpdate = true;
			_CommCodeDM_=commCodeDM;
		}
		CommCodeEntity commcodeEntity = _CommCodeDM_.convertTo(CommCodeEntity.class);
		try
		   {
		if(isUpdate){
			commcodeEntity.setId(commCodeDM.getId());
			commDao.updateCommCode(commcodeEntity);
		}else {
			commcodeEntity.setAvailable(1);
			commcodeEntity.setLastModifyDate(new Date());
			commcodeEntity.setLastModifyUser("admin");
			commcodeEntity.setTransCode(commcodeEntity.getCommCode());
			if(commcodeEntity.getSystemCode().equals("host")){
				commcodeEntity.setChannelCode("00");
			}else {
				commcodeEntity.setChannelCode("01");
			}
			commDao.insertCommCode(commcodeEntity);
		}
		  }catch (Exception e) {
			  throw new BizException(ExceptionLevel.ERROR, "新增或更新通讯码表数据失败", e.getCause());
		}
	}
	@Override
	@Transaction
	@CacheEvict(method="findCommCode",key="${p0}")
	public void delCommCode(String commCode) {
		CommCodeEntity commcodeEntity = commDao.findCommCode(commCode);
		if(commcodeEntity == null){
			return ;
		}
		try{
			commDao.deleteCommCode(commcodeEntity);
		}catch (Exception e) {
			throw new BizException(ExceptionLevel.ERROR, "删除通讯码表数据失败");
		}
	}
}
