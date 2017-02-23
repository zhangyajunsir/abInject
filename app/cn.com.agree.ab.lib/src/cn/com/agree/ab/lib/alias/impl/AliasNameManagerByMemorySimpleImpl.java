/*
 *Copyright 1999-2012 Alibaba Group.
 * 
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 *
 * http://code.taobao.org/p/talkingbird/src/
 *
 */
package cn.com.agree.ab.lib.alias.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.com.agree.ab.lib.alias.AliasNameException;
import cn.com.agree.ab.lib.alias.AliasNameManager;

/**
 * 基于内存简单实现的别名管理器
 * @author liucheng
 * 2014-12-30 上午09:09:05
 *
 */
public final class AliasNameManagerByMemorySimpleImpl extends AliasNameManager {
	final Map<String, String> cache = new HashMap<String, String>();
	@Override
	public Collection<String> findAliasName(String real) {
		Set<String> aliasSet = new HashSet<String>();
		Set<String> keySet = cache.keySet();
		for (String key : keySet) {
			String val = cache.get(key);
			if(real.equals(val)){
				aliasSet.add(key);
			}
		}
		return aliasSet;
	}

	@Override
	public String findRealName(String alias) {
		return cache.get(alias);
	}

	@Override
	public void register(String alias, String real) {
		if(alias == null || alias.isEmpty()){
			throw new AliasNameException("输入的别名为空，必须为非空！");
		}
		if(real == null || real.isEmpty()){
			throw new AliasNameException("输入的真实名称为空，必须为非空！");
		}
		if(cache.containsKey(alias)){
			throw new AliasNameException("该别名["+alias+"]已被注册！");
		}
		cache.put(alias, real);
	}

	@Override
	public String remove(String alias) {
		if(alias == null || alias.isEmpty()){
			throw new AliasNameException("输入的别名为空，必须为非空！");
		}
		String val = cache.remove(alias);
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
			String tarRealName = cache.get(aliasName);
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
		return cache.keySet();
	}
	
	

}
