package cn.com.agree.ab.lib.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jodd.props.Props;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import cn.com.agree.ab.lib.cache.Cache;
import cn.com.agree.ab.lib.cache.Cache.ValueWrapper;
import cn.com.agree.ab.lib.cache.CacheManager;
import cn.com.agree.ab.lib.exception.ConfigException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.resource.IResourceChangeListener;
import cn.com.agree.ab.resource.ResourcePlugin;
import cn.com.agree.commons.csv.CsvUtil;

/**
 * 配置文件管理器
 * 不需要预先绑定，但需要添加@Singleton代表单例
 * @author zhangyajun
 *
 */
@Singleton
public class ConfigManager {
	private static final Logger	logger	= LoggerFactory.getLogger(ConfigManager.class);
	private final static String cacheKey = "ConfigCaches";
	
	private final static String utilPath = "/resources/config/util.ini";
	
	private Cache cache;
	/**ResourcePlugin插件里定时监控了各个工程下的class、resources和dsr(新增)目录下的各个文件变化*/
	private IResourceChangeListener resourceChangeListener = new IResourceChangeListener(){
		@Override
		// 处理更新后的文件列表，更新缓存
		public synchronized void  handle(String csv) {
			Map<String, String> updateCache = CsvUtil.csvToMap(csv);
			logger.debug("有配置文件发生变化：{}", updateCache);
			for (String path : updateCache.keySet()) {
				if (cache.get(path) != null && configLoadMap.get(path) != null) {
					refreshCache(path, configLoadMap.get(path), true);
				}
			}
		}
	};
	
	private Map<String, ConfigLoad<?>> configLoadMap = new ConcurrentHashMap<String, ConfigLoad<?>>();
	
	private String[] allProjectName = null;
	
	/** 
	 * concurrentMapCacheManager的缓存不具备失效功能，失效通过ResourceChange事件处理 
	 * 
	 * */
	@Inject
	public ConfigManager(@Named("concurrentMapCacheManager")CacheManager cacheManager){
		cache = cacheManager.getCache(cacheKey);
		ResourcePlugin.getDefault().addResourceChangeListener(resourceChangeListener);
	}
	
	/**
	 * 解决此类对象因为会被ResourcePlugin引用住，所以热加载该类，会造成老的对象不会释放的问题
	 */
	@PreDestroy 
	public void destroy(){
		ResourcePlugin.getDefault().removeResourceChangeListener(resourceChangeListener);
	}
	
	/**
	 * 读取workspace下的Properties文件，且做缓存处理
	 * @param path workspace目录下的路径
	 * @return
	 */
	public Properties getGBKProperties(String path) {
		Properties prop = cache.get(path, Properties.class);
		if (prop != null)
			return prop;
		
		ConfigLoad<Properties> cl = new ConfigLoad<Properties>() {
			@Override
			public Properties load(URL url, InputStream is) throws Exception {
				Properties prop = new GBKProperties();
				prop.load(is);
				return prop;
			}
		};
		
		return refreshCache(path, cl, false);
	}
	
	/**
	 * 读取workspace下的INI文件，且做缓存处理
	 * @param path workspace目录下的路径
	 * @return
	 */
	public Props getGBKIni(String path) {
		Props ini = cache.get(path, Props.class);
		if (ini != null)
			return ini;
		
		ConfigLoad<Props> cl = new ConfigLoad<Props>() {
			@Override
			public Props load(URL url, InputStream is) throws Exception {
				Props prop = new Props();
				prop.load(is, "GBK");
				return prop;
			}
		};
		
		return refreshCache(path, cl, false);
	}
	
	public Props getUtilIni() {
		String path = getAbsConfigPath(utilPath);
		if (path == null)
			throw new ConfigException("无配置文件【"+utilPath+"】。");
		return getGBKIni(path);
	}
	
	/**
	 * @param path workspace目录下的路径
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getConfigObject(String path, ConfigLoad<T> cl) {
		Object o = cache.get(path);
		if (o != null) {
			if (o instanceof ValueWrapper)
				return (T)((ValueWrapper)o).get();
			else
				return (T)o;
		}
		return refreshCache(path, cl, false);
	}
	
	private synchronized <T> T refreshCache(String path, ConfigLoad<T> cl, boolean realRefresh) {
		InputStream is = null;
		T t = null;
		try {
			is = ResourcePlugin.getDefault().getResourceStream(path);
			URL url = ResourcePlugin.getDefault().getResourceURL(path);
			t = cl.load(url, is);
		} catch (FileNotFoundException e) {
			throw new ConfigException(ExceptionLevel.ERROR, "配置文件："+path+"未找到");
		} catch (Exception e) {
			throw new ConfigException(ExceptionLevel.ERROR, "配置文件："+path+"载入异常", e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
		if (realRefresh) {
			cache.evict(path);
		} else {
			configLoadMap.put(path, cl);
		}
		cache.put(path, t);
		return t;
	}
	
	public String[] getAllProjectName() {
		if (allProjectName != null)
			return allProjectName;
		synchronized(this) {
			if (allProjectName != null)
				return allProjectName;
			IProject[] projects = ResourcePlugin.getDefault().getDefaultProjects();
			allProjectName = new String[projects.length];
			for (int i=0; i<projects.length; i++) {
				allProjectName[i] = projects[i].getName();
			}
		}
		return allProjectName;
	}
	
	public String getAbsConfigPath(String path) {
		Preconditions.checkState(path != null && !path.equals(""), "路径不可为空");
		if (!path.startsWith("/"))
			path = "/" + path;
		String absPath = null;
		String[] allProjectName = getAllProjectName();
		for (String projectName : allProjectName) {
			String _path_ =  "/"+projectName+path;
			try {
				IFile file = ResourcePlugin.getDefault().getResourceFile(_path_);
				if (file == null || !file.exists())
					continue;
			} catch (FileNotFoundException e) {
				continue;
			}
			absPath = _path_;
			break;
		}
		return absPath;
	}
}

