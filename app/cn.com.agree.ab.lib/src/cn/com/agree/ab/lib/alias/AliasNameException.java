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
/**
	 * 别名异常
	 * @author liucheng
	 * 2014-12-30 上午09:36:09
	 *
	 */
	public class AliasNameException extends RuntimeException{
		private static final long serialVersionUID = 7344694303775219358L;

		public AliasNameException() {
			super();
		}

		public AliasNameException(String message, Throwable cause) {
			super(message, cause);
		}

		public AliasNameException(String message) {
			super(message);
		}

		public AliasNameException(Throwable cause) {
			super(cause);
		}
		
	}