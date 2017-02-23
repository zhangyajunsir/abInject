package cn.com.agree.ab.lib.alias.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.lib.alias.AliasNameException;
import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.cache.Cache;
import cn.com.agree.ab.lib.cache.CacheManager;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 基于缓存实现的单例别名管理器
 *
 */
@AutoBindMapper(baseClass = AliasNameManager.class)
@Singleton
public final class AliasNameManagerByCacheSingleImpl extends AliasNameManager {
	
	private final static String cacheKey = "AliasCaches";
	
	private Cache cache;
	
	@Inject
	public AliasNameManagerByCacheSingleImpl(@Named("concurrentMapCacheManager")CacheManager cacheManager) {
		cache = cacheManager.getCache(cacheKey);
	}
	
	@Override
	public Collection<String> findAliasName(String real) {
		Set<String> aliasSet = new HashSet<String>();
		Collection<String> keySet = aliasName();
		for (String key : keySet) {
			String val = cache.get(key, String.class);
			if(real.equals(val)){
				aliasSet.add(key);
			}
		}
		return aliasSet;
	}

	@Override
	public String findRealName(String alias) {
		return cache.get(alias, String.class);
	}

	@Override
	public void register(String alias, String real) {
		if(alias == null || alias.isEmpty()){
			throw new AliasNameException("输入的别名为空，必须为非空！");
		}
		if(real == null || real.isEmpty()){
			throw new AliasNameException("输入的真实名称为空，必须为非空！");
		}
		if(cache.get(alias) != null){
			throw new AliasNameException("该别名["+alias+"]已被注册！");
		}
		cache.put(alias, real);
	}

	@Override
	public String remove(String alias) {
		if(alias == null || alias.isEmpty()){
			throw new AliasNameException("输入的别名为空，必须为非空！");
		}
		String val = cache.get(alias, String.class);
		cache.evict(alias);
		return val;
	}

	@Override
	public String toString() {
		return "AliasNameManagerByMemorySimpleImpl [cache=" + cache + "]";
	}

	@Override
	public void merge(AliasNameManager source) {
		Collection<String> aliasNames = source.aliasName();
		for (String aliasName : aliasNames) {
			String tarRealName = cache.get(aliasName, String.class);
			String srcRealName = source.findRealName(aliasName);
			if(tarRealName != null && tarRealName.equals(srcRealName)){
				System.out.println("不需要合并");
			}
			if(tarRealName != null && !tarRealName.equals(srcRealName)){
				throw new AliasNameException("合并信息存在冲突,无法合并.["+ aliasName +"]在来源注册为["+srcRealName +"],目标为["+ tarRealName +"]");
			}
			if(tarRealName == null){
				cache.put(aliasName, srcRealName);
			}
		}
	}

	@Override
	public Collection<String> aliasName() {
		Set<String> aliasSet = new HashSet<String>();
		for (Object key : cache.keySet()) {
			aliasSet.add((String)key);
		}
		return aliasSet;
	}
	
	

}
