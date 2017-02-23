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
package cn.com.agree.ab.lib.rpc.packet.loader;
/**
 * 包样式
 * @author liucheng
 * 2014-12-18 下午04:51:43
 *
 */
public enum PacketType {
	/**
	 * 请求样式
	 */
	REQ("REQ"),
	/**
	 * 应答样式
	 */
	RSP("RSP"),
	/**
	 * 异常样式
	 */
	EXP("EXP"),
	/**
	 * 格式化和解析
	 */
	ALL("ALL");
	private String val;
	private PacketType(String val){
		this.val = val;
	}
	@Override
	public String toString() {
		return this.val;
	}
}
