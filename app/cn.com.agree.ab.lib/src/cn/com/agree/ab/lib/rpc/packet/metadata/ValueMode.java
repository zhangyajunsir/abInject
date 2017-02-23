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
 * 值模式<br>
 * 用于设定编排和反编排时的取值
 * @author liucheng
 *
 */
public enum ValueMode {
	/**
	 * 固定值（常量）模式
	 */
	CONSTANT("CONSTANT"),
	/**
	 * 缺省值模式
	 */
	DEFAULT("DEFAULT"),
	/**
	 * 用户数据模式
	 */
	USER("USER"),
	/**
	 * 内部处理模式
	 */
	INNER("INNER");
	
	private String val;
	private ValueMode(String val){
		this.val = val;
	}
	@Override
	public String toString() {
		return this.val;
	}
}
