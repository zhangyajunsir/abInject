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
package cn.com.agree.ab.lib.utils;

import java.util.Arrays;


/**
 * 字节数组工具类
 * @author liucheng
 * 2014-12-8 上午10:46:31
 *
 */
public class ArraysUtil {
	private ArraysUtil(){}
	/**
	 * 进行左对齐，右边填充
	 * @param in 输入的字节数组
	 * @param expectSize 期望大小
	 * @return
	 */
	public static byte[] rfill(byte[] in, int expectSize, byte fill){
		byte[] newbytes = new byte[expectSize];
		System.arraycopy(in, 0, newbytes, 0, in.length);
		//如果输入长度与期望不一致且填充符号不为0
		if(in.length < expectSize && fill != 0){
			Arrays.fill(newbytes,in.length,expectSize, fill);
		}
		return newbytes;
	}
	/**
	 * 进行右对齐，左边填充
	 * @param in 输入的字节数组
	 * @param expectSize 期望大小
	 * @return 
	 */
	public static byte[] lfill(byte[] in, int expectSize, byte fill){
		byte[] newbytes = new byte[expectSize];
		//如果输入长度与期望不一致且填充符号不为0
		if(in.length < expectSize && fill != 0){
			Arrays.fill(newbytes,0,expectSize - in.length , fill);
		}
		System.arraycopy(in, 0 , newbytes, expectSize - in.length, in.length);
		return newbytes;
	}
	/**
	 * 左截断
	 * @param in 输入的字节数组
	 * @param expectSize 期望大小
	 * @return
	 */
	public static byte[] ltruncate(byte[] in, int expectSize){
		if(in.length < expectSize){
			return in;
		}
		byte[] newbytes = new byte[expectSize];
		System.arraycopy(in, in.length - expectSize, newbytes, 0, expectSize);
		return newbytes;
	}
	/**
	 * 右截断
	 * @param in 输入的字节数组
	 * @param expectSize 期望大小
	 * @return
	 */
	public static byte[] rtruncate(byte[] in, int expectSize){
		if(in.length < expectSize){
			return in;
		}
		byte[] newbytes = new byte[expectSize];
		System.arraycopy(in, 0, newbytes, 0, expectSize);
		return newbytes;
	}


}
