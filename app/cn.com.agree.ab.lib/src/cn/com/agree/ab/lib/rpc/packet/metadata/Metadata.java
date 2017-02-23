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
package cn.com.agree.ab.lib.rpc.packet.metadata;

/**
 * 报文元信息
 * @author liucheng
 * 2014-11-24 下午01:37:10
 *
 */
public interface Metadata<T> {
	/**
	 * 元信息名称<br>
	 * 对每一个报文的逻辑单元都有一个具名元信息<br>
	 * @return
	 */
	public String getName();
	
	/**
	 * 设置元信息名称
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * 获取报文元对象
	 * @return
	 */
	public T get();
}
