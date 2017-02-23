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
package cn.com.agree.ab.lib.alias;

import java.util.Collection;

import cn.com.agree.ab.lib.alias.impl.AliasNameManagerByMemorySimpleImpl;

/**
 * 别名管理器接口<br>
 * 别名映射模型为<br>
 * 1.一个别名只允许映射唯一的真实名称<br>
 * 2.一个真实名称可以被多个别名映射<br>
 * @author liucheng
 * 2014-12-30 上午09:02:43
 *
 */
public abstract class AliasNameManager {
	/**
	 * 注册别名<br>
	 * 一个别名只能注册到一个真实名称,如果发生一个别名注册到多个真实名称则抛出异常
	 * @param alias 别名
	 * @param real 真实名称
	 */
	public abstract void register(String alias, String real);
	/**
	 * 移除别名
	 * @param alias 别名
	 * @return 真实名称
	 */
	public abstract String remove(String alias);
	/**
	 * 根据别名获取真实的名称
	 * @param alias 别名
	 * @return 真实名称
	 */
	public abstract String findRealName(String alias);
	/**
	 * 根据真实名称获取别名集
	 * @param real 真实名称
	 * @return 别名集
	 */
	public abstract Collection<String> findAliasName(String real);
	/**
	 * 获取所有别名
	 * @return
	 */
	public abstract Collection<String> aliasName();

	/**
	 * 合并管理器
	 * @param source 来源管理器
	 * @return
	 */
	public abstract void merge(AliasNameManager source);
	
	/**
	 * 获取默认实现的别名管理器
	 * @return
	 */
	public static AliasNameManager getSimpleManager(){
		return new AliasNameManagerByMemorySimpleImpl();
	}
}
