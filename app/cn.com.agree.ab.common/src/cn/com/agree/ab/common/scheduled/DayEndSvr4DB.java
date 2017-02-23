package cn.com.agree.ab.common.scheduled;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.biz.ProcedureBiz;
import cn.com.agree.ab.lib.utils.DateUtil;

/**
 * 只能一台ABS服务器进行定时调用
 * @author zhangyajun
 *
 */
@Singleton
public class DayEndSvr4DB implements Runnable {
	private static final Logger	logger	= LoggerFactory.getLogger(DayEndSvr4DB.class);
	@Inject
	@Named("procedureBiz")
	private ProcedureBiz procedureBiz;
	
	@Override
	public void run() {
		logger.info("执行存储过程[SP_PUB_DAYENDSVR]开始");
		try {
			procedureBiz.dayEndSvr(DateUtil.getDateToString().substring(0, 8), 0);
		} catch (Exception e) {
			logger.error("执行存储过程[SP_PUB_DAYENDSVR]异常", e);
		}
		logger.info("执行存储过程[SP_PUB_DAYENDSVR]结束");
	}

}
