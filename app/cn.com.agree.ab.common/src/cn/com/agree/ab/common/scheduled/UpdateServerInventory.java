package cn.com.agree.ab.common.scheduled;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.biz.ABServerBiz;
import cn.com.agree.ab.common.dm.ABServerDM;
import cn.com.agree.ab.lib.exception.BasicException;

/**
 * 全部ABS服务器都必须进行定时调用，或启动后调用
 * @author zhangyajun
 *
 */
@Singleton
public class UpdateServerInventory implements Runnable {
	private static final Logger	logger	= LoggerFactory.getLogger(UpdateServerInventory.class);
	@Inject
	@Named("abserverBiz")
	private ABServerBiz abserverBiz;

	@Override
	@PostConstruct
	public void run() {
		logger.info("更新AB服务器清单到平台里开始");
		try {
			List<ABServerDM> abserverDMs = abserverBiz.getAllABServer();
			if (abserverDMs == null)
				throw new BasicException("无数据库配置数据");
			StringBuilder sb = new StringBuilder();
			for (ABServerDM abserverDM : abserverDMs) {
				sb.append(abserverDM.getAbsOid()).append(",tcp://").append(abserverDM.getIpv4()).append(':').append(abserverDM.getAbsPort()).append('\n');
			}
			sb.deleteCharAt(sb.length()-1);
			
			/** 采用此方式：1.不能触发 PreferenceChangeListener 2.会在configuration/.setting和workspace/.metadata写入缓存文件*/
//			Properties prop  = new Properties();
//			prop.put("cn.com.agree.ab.communication.cluster/servers", sb.toString());
//			ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
//			prop.store(bos, null);
//			Platform.getPreferencesService().importPreferences(new ByteArrayInputStream(bos.toByteArray()));
			/** 为了能触发PreferenceChangeListener，这里的注入方式稍作修改 modify by xiep 20160911 **/
			ConfigurationScope.INSTANCE.getNode("cn.com.agree.ab.communication.cluster").put("servers", sb.toString());
		} catch (Exception e) {
			logger.error("更新AB服务器清单到平台里异常"+e.getMessage(), e);
		}
		logger.info("更新AB服务器清单到平台里结束");
	}

}
