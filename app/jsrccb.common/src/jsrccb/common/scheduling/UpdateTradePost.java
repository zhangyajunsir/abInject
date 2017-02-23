package jsrccb.common.scheduling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.dm.PostCfgDM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.resource.ResourcePlugin;

/**
 * 根据TLRPOST.TXT刷新到数据库
 * 只能一台ABS服务器进行定时调用
 * @author zhangyajun
 */
@Singleton
public class UpdateTradePost implements Runnable {
	private static final Logger	logger	= LoggerFactory.getLogger(UpdateTradePost.class);
	private static final String cfg     = "TLRPOST.TXT";
	@Inject
	private ConfigManager configManager;
	@Inject
	@Named("jsrccbTradeBiz")
	private jsrccb.common.biz.TradeBiz tradeBiz;

	@Override
	public void run() {
		logger.info("更新权限配置文件到数据库开始");
		String cfgPath = configManager.getAbsConfigPath(cfg);
		if (cfgPath == null || cfgPath.isEmpty()) {
			logger.error("权限配置文件{}不存在", cfg);
			return;
		}
		PostCfgDM postCfgDM = new PostCfgDM();
		InputStream is = null;
		try {
			is = ResourcePlugin.getDefault().getResourceStream(cfgPath);
			postCfgDM.init(is);
		} catch (FileNotFoundException e) {
			logger.error("权限配置文件{}不存在", cfgPath, e);
			return;
		} catch (IOException e) {
			logger.error("权限配置文件{}读取异常", cfgPath, e);
			return;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
		try {
			tradeBiz.updateTradePostFromCfg(postCfgDM);
		} catch (Exception e) {
			logger.info("更新权限配置文件到数据库失败", e);
		}
		
		logger.info("更新权限配置文件到数据库结束");
	}
	

}
