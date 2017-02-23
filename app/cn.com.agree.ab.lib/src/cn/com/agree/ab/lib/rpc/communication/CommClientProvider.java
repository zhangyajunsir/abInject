package cn.com.agree.ab.lib.rpc.communication;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Maps;

import jodd.props.Props;

import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.ConfigException;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.resource.ResourcePlugin;

/**
 * 此类功能类似工厂模式
 * 不需要预先绑定，但需要添加@Singleton代表单例
 * @author zhangyajun
 */
@Singleton
public class CommClientProvider {
	private static String commIniPath = "/resources/config/comm.ini";
	@Inject
	private ConfigManager configManager;
	
	private String commIniAbsPath;
	private Map<String, String>     mapper  = Maps.newConcurrentMap();
	private Map<String, CommClient> clients = Maps.newConcurrentMap();
	
	/** 调用构造器且已完成依赖注入后 */
	@PostConstruct
	public void init() {
		String commIniAbsPath = configManager.getAbsConfigPath(commIniPath);
		if (commIniAbsPath == null) {
			throw new ConfigException("无通讯配置文件【"+commIniPath+"】。");
		}
		this.commIniAbsPath = commIniAbsPath;
	}
	
	/**
	 * 当GUICE框架发生卸载时，会调用加此注解的方法
	 * 具体详见ProjectLifecycleInjector类里的lifecycleManager.close()
	 * */
	@PreDestroy 
	public void destroy(){
		for ( String name : clients.keySet()) {
			clients.remove(name).destroy();
		}
		mapper.clear();
	}
	
	public byte[] transform(String channelCode, String orgCode, String timeout, byte[] input){
		long time = System.currentTimeMillis();
		Props commIni = configManager.getGBKIni(commIniAbsPath);
		if (orgCode == null)
			orgCode = "";
		String profile = orgCode;
		String client = commIni.getValue(channelCode+".CLIENT", profile);
		if (client == null) {
			profile = "all";
			client  = commIni.getValue(channelCode+".CLIENT", profile);
		}
		String isLong = commIni.getValue(channelCode+".IS_LONG", profile);
		if (client == null || isLong == null)
			throw new ConfigException("通讯配置文件【"+commIniPath+"】无渠道号【"+channelCode+"】机构号【"+orgCode+"】的配置。");
		Map<String, String> configMap = Maps.newHashMap();
		commIni.extractSubProps(configMap, new String[]{profile}, null, channelCode);
		if (timeout != null && !"".equals(timeout))
			configMap.put(channelCode+".TIMEOUT", timeout);
		
		byte[] ret = null;
		if ("true".equals(isLong)) {
			CommClient commClient = null;
			if (mapper.get(channelCode+"<"+orgCode+">") != null && clients.get(mapper.get(channelCode+"<"+orgCode+">")) != null) {
				commClient = clients.get(mapper.get(channelCode+"<"+orgCode+">"));
			} else {
				synchronized (mapper) {
					if (mapper.get(channelCode+"<"+orgCode+">") != null && clients.get(mapper.get(channelCode+"<"+orgCode+">")) != null) {
						commClient = clients.get(mapper.get(channelCode+"<"+orgCode+">"));
					} else {
						commClient = clients.get(channelCode+"<"+profile+">");
						if (commClient == null) {
							try {
								commClient = (CommClient) ResourcePlugin.getDefault().loadClass(client).newInstance();
							} catch (InstantiationException e) {
								throw new BasicRuntimeException("无法实例化【"+client+"】", e);
							} catch (IllegalAccessException e) {
								throw new BasicRuntimeException("无法实例化【"+client+"】", e);
							} catch (ClassNotFoundException e) {
								throw new BasicRuntimeException("无法实例化【"+client+"】", e);
							}
							commClient.initialize(configMap);
							clients.put(channelCode+"<"+profile+">", commClient);
						}
						mapper.put(channelCode+"<"+orgCode+">", channelCode+"<"+profile+">");
					}
				}
			}
			try {
				ret = commClient.transform(input);
			} catch (TransformException e) {
				throw new BasicRuntimeException("通讯发生异常", e);
			}
		} else {
			CommClient commClient = null;
			try {
				commClient = (CommClient) ResourcePlugin.getDefault().loadClass(client).newInstance();
			} catch (InstantiationException e) {
				throw new BasicRuntimeException("无法实例化【"+client+"】", e);
			} catch (IllegalAccessException e) {
				throw new BasicRuntimeException("无法实例化【"+client+"】", e);
			} catch (ClassNotFoundException e) {
				throw new BasicRuntimeException("无法实例化【"+client+"】", e);
			}
			commClient.initialize(configMap);
			try {
				ret = commClient.transform(input);
			} catch (TransformException e) {
				throw new BasicRuntimeException("通讯发生异常", e);
			}
			commClient.destroy();
		}
		ContextHelper.setContext("socketTime", System.currentTimeMillis()-time);
		return ret;
	}
	
}
