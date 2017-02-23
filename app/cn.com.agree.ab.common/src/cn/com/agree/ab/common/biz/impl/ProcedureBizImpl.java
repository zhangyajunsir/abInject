package cn.com.agree.ab.common.biz.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import cn.com.agree.ab.common.biz.ProcedureBiz;
import cn.com.agree.ab.common.dao.ProcedureDao;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ProcedureBiz.class)
@Singleton
@Biz("procedureBiz")
public class ProcedureBizImpl implements ProcedureBiz {
	@Inject
	@Named("procedureDao")
	private ProcedureDao procedureDao;

	@Override
	public void dayEndSvr(String date, Integer step) {
		Preconditions.checkState(date != null && !date.equals("") && date.length() == 8, "日期不正确");
		if (step == null)
			step =  0;
		procedureDao.dayEndSvr(date, step);
	}

}
