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

import java.io.*;
import java.util.StringTokenizer;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Hex {
	private Hex() {
	}

	public static void write(byte abyte0[]) {
		write(abyte0, ((System.out)));
	}

	public static void write(byte abyte0[], OutputStream out) {
		try {
			out.write(toDisplayString(abyte0).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将字节数组输出为16进制字符串,并且格式化
	 * @param abyte0
	 * @return
	 */
	public static String toDisplayString(byte abyte0[]) {
		return toDitosplayString(abyte0, 0, abyte0.length);
	}
	/**
	 * 将字节数组输出为16进制字符串,并且格式化
	 * @param bytes 带输出字节数组
	 * @param offset 起始偏移量
	 * @param length 输出长度
	 * @return
	 */
	public static String toDitosplayString(byte bytes[], int offset, int length) {
		StringBuffer buffer = new StringBuffer(74);
		buffer.append("--------------------------====[******]====------------------------------");
		buffer.append(System.getProperty("line.separator"));
		buffer.append("-Displace-");
		buffer.append("  ");
		for (int i = 0; i < 16; i++) {
			buffer.append("-").append(Integer.toHexString(i).toUpperCase()).append("-");
		}
		buffer.append(" ");
		buffer.append("---ASCII CODE---");
		buffer.append(System.getProperty("line.separator"));
		for (int i = offset; i < length; i += 16) {
			int splitLen = length - i < 16 ? length - i : 16;
			String position16 = Integer.toHexString(i - offset).toUpperCase();
			String position = Integer.toString(i - offset).toUpperCase();
			//输出左侧的行首偏移地址
			for (int j = position16.length(); j < 4; j++)
				buffer.append('0');

			buffer.append(position16);
			buffer.append("(");
			for (int j = position.length(); j < 4; j++)
				buffer.append('0');
			buffer.append(position);
			
			buffer.append(")");
			buffer.append("  ");
			//输出中间的16进制数据
			for (int j = 0; j < 16; j++) {
				if (j < splitLen) {
					int j1 = bytes[j + i] & 255;
					String s1 = Integer.toHexString(j1).toUpperCase();
					if (s1.length() == 1)
						buffer.append("0");
					buffer.append(s1);
					buffer.append(" ");
				} else {
					buffer.append("   ");
				}
			}

			buffer.append(" ");
			//输出右边的字符数据
			for (int j = 0; j < 16; j++)
				if (j >= splitLen) {
					buffer.append(" ");
				} else {
					char c = (char) (bytes[j + i] & 255);
					if (c >= '!' && c <= '~')
						buffer.append(c);
					else
						buffer.append(".");
				}

			buffer.append(System.getProperty("line.separator"));
		}

		return buffer.toString();
	}
	/**
	 * 读取16进制字符串为字节数组
	 * @param in 输入流
	 * @return 字节数组
	 */
	public static byte[] read(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String line;
		try {
			reader.readLine();
			reader.readLine();
			while ((line = reader.readLine()) != null)
				if (line.length() >= 10) {
					int end = Math.min(59, line.length());
					String innerLine = line.substring(10, end);
					byte bb;
					for (StringTokenizer token = new StringTokenizer(innerLine, " "); token.hasMoreTokens(); out.write(bb)) {
						String b = token.nextToken();
						bb = (byte) Integer.parseInt(b, 16);
					}

				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
	/**
	 * 字节数组显示为16进制字符串
	 * @param data 字节数组
	 * @return
	 */
	public static String toHexString(byte data[]) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			buff.append(HEX_STRING.charAt(data[i] >> 4 & 15));
			buff.append(HEX_STRING.charAt(data[i] & 15));
			buff.append(" ");
		}
		return buff.toString();
	}
	/**
	 * 格式化16进制字符串为字节数组
	 * @param hex 16进制字符串
	 * @return 字节数组
	 */
	public static byte[] fromHexString(String hex) {
		byte ret[] = new byte[hex.length() / 2];
		int i = 0;
		int j = 0;
		while (i < hex.length())
			ret[j++] = (byte) (offset(hex.charAt(i++)) << 4 | offset(hex.charAt(i++)));
		return ret;
	}

	private static int offset(char c) {
		c = Character.toUpperCase(c);
		int i = c < '0' || c > '9' ? c < 'A' || c > 'F' ? -1 : (c - 65) + 10 : c - 48;
		if (i < 0)
			throw new IllegalArgumentException("Invalid Hex Char.");
		else
			return i;
	}

	public static byte[] compress(byte input[]) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		DeflaterOutputStream comp = new DeflaterOutputStream(out);
		comp.write(input);
		return out.toByteArray();
	}

	public static byte[] decompress(byte input[]) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(input);
		InflaterInputStream decomp = new InflaterInputStream(in);
		byte buffer[] = new byte[512];
		int read = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		while ((read = decomp.read(buffer)) != -1)
			out.write(buffer, 0, read);
		return out.toByteArray();
	}

	public static final byte EMPTY[] = new byte[0];
	private static final String HEX_STRING = "0123456789ABCDEF";
}