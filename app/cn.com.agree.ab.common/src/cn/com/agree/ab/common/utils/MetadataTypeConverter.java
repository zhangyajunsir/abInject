package cn.com.agree.ab.common.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;

import cn.com.agree.ab.common.dm.MoneyDM;
import cn.com.agree.ab.lib.utils.ArraysUtil;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.lib.utils.ObjectUtil;
import cn.com.agree.ab.trade.ext.cbod.impl.ebcdic.AS400EbcdicTool;

public class MetadataTypeConverter {
	
	private MetadataTypeConverter() {}
	
	/**
	 * 通用类型转换
	 * @param source
	 * @param targetClazz
	 * @param params 1.编码方式，2.数据格式化，3.小数位数，4.是否trim(1或true或无此参数时为真)
	 * @return
	 */
	public static Object converter(Object source, Class<?> targetClazz, String... params) {
		if (source == null)
			return null;
		if (source.getClass() == targetClazz) 
			return source;
		if (ObjectUtil.isExtends(source.getClass(), targetClazz) || ObjectUtil.isImplement(source.getClass(), targetClazz))
			return source;
		// params : encoding dataformat 
		if (source instanceof byte[]) {
			if (targetClazz == String.class) {
				if (params == null || params.length == 0) {
					throw new IllegalArgumentException("未传入编码参数");
				}
				boolean isTrim = true;
				if (params.length >= 4 && !"1".equals(params[3]) && !"true".equalsIgnoreCase(params[3]))
					isTrim = false;
				return isTrim ? byteArray2String((byte[])source, params[0]).trim() : byteArray2String((byte[])source, params[0]);
			}
			if (targetClazz == Integer.class) {
				return byteArrayH2Integer((byte[])source);
			}	
			if (targetClazz == Date.class) {
				if (params == null || params.length < 2) {
					throw new IllegalArgumentException("未传入日期格式参数");
				}
				return byteArray2Date((byte[])source, params[1]);
			}
			if (targetClazz == BigDecimal.class) {
				return new BigDecimal(new String((byte[])source));
			}
			if (targetClazz == ByteBuffer.class) {
				return ByteBuffer.wrap((byte[])source);
			}
			if (targetClazz == MoneyDM.class) {
				if (params == null || params.length < 3) {
					throw new IllegalArgumentException("未传入小数位数");
				}
				String value = (String)converter(source, String.class, params[0]);
				return new MoneyDM().from(value, Integer.valueOf(params[2]));
			}
		}
		if (source instanceof String) {
			if (targetClazz == byte[].class) {
				if (params == null || params.length == 0) {
					throw new IllegalArgumentException("未传入编码参数");
				}
				return string2ByteArray((String)source, params[0]);
			}
			if (targetClazz == Integer.class) {
				return Integer.valueOf((String)source);
			}
			if (targetClazz == Date.class) {
				if (params == null || params.length < 2) {
					throw new IllegalArgumentException("未传入日期格式参数");
				}
				try {
					return DateUtil.convertStringToDate((String)source, params[1]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (targetClazz == BigDecimal.class) {
				return new BigDecimal((String)source);
			}
			if (targetClazz == MoneyDM.class) {
				if (params == null || params.length < 3) {
					throw new IllegalArgumentException("未传入小数位数");
				}
				return new MoneyDM().from((String)source, Integer.valueOf(params[2]));
			}
		}
		if (source instanceof Integer) {
			if (targetClazz == byte[].class) {
				return integer2ByteArrayH((Integer)source);
			}
			if (targetClazz == String.class) {
				return String.valueOf((Integer)source);
			}
		}
		if (source instanceof Date) {
			if (params == null || params.length < 2) {
				throw new IllegalArgumentException("未传入日期格式参数");
			}
			if (targetClazz == String.class) {
				return DateUtil.dateToDateString((Date)source, params[1]);
			}
			if (targetClazz == byte[].class) {
				return date2ByteArray((Date)source, params[1]);
			}
		}
		if (source instanceof BigDecimal) {
			if (targetClazz == String.class) {
				return source.toString();
			}
			if (targetClazz == byte[].class) {
				return source.toString().getBytes();
			}
		}
		if (source instanceof ByteBuffer) {
			if (targetClazz == byte[].class) {
				return ((ByteBuffer)source).array();
			}
		}
		if (source instanceof MoneyDM) {
			if (targetClazz == String.class) {
				return ((MoneyDM)source).to();
			}
			if (targetClazz == byte[].class) {
				return ((MoneyDM)source).to().getBytes();
			}
		}
		throw new IllegalArgumentException("不支持类型为〖"+source.getClass()+"〗到〖"+targetClazz.getName()+"〗转换");
	}
	
	/**
	 * 字符串解码成字节数组
	 * @param source
	 * @param encoding
	 * @return
	 */
	public static byte[] string2ByteArray(String source, String encoding) {
		byte[] bytes = null;
		if ("ebcdic".equalsIgnoreCase(encoding)) {
			try {
				bytes = AS400EbcdicTool.StringToEBCDIC(source);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			Charset charset = Charset.forName(encoding);
			bytes = source.getBytes(charset);
		}
		return bytes;
	}
	
	/**
	 * 字节数组编码成字符串
	 * @param source
	 * @param encoding
	 * @return
	 */
	public static String byteArray2String(byte[] source, String encoding) {
		String str = null;
		if ("ebcdic".equalsIgnoreCase(encoding)) {
			// AS400EbcdicTool.ebcdicToString会改变字节数组内容，所以使用COPY
			byte[] _source_ = new byte[source.length];
			System.arraycopy(source, 0, _source_, 0, source.length);
			try {
				str = AS400EbcdicTool.ebcdicToString(_source_).replaceAll("\\x03", " ");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			Charset charset = Charset.forName(encoding);
			str = new String(source, charset);
		}
		return str;
	}
	
	/**
	 * 整形转成高位优先的字节数组
	 * @param source
	 * @return
	 */
	public static byte[]  integer2ByteArrayH(Integer source) {
		/* 
		 * 内存最低的地址存放高位字节,称为高位优先,最低的地址存放低位字节,成为低位优先
		 * 在Java中，当我们要将int转换为byte数组时，一个int就需要长度为4个字节的数组来存放，其中一次从数组下标为[0]开始存放int的高位到低位。
		 * */
		byte[] result = new byte[4];   
        //由高位到低位
        result[0] = (byte)((source >> 24) & 0xFF);
        result[1] = (byte)((source >> 16) & 0xFF);
        result[2] = (byte)((source >> 8)  & 0xFF); 
        result[3] = (byte)( source        & 0xFF);
        return result;
	}
	
	/**
	 * 高位优先的字节数组转成整形
	 * @param source
	 * @return
	 */
	public static Integer byteArrayH2Integer(byte[] source) {
		if (source.length < 4) {
			source = ArraysUtil.lfill(source, 4, (byte)0x00);
		}
		int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(source[i] & 0x000000FF) << shift;//往高位游
        }
		return value;
	}
	
	/**
	 * 整形转成低位优先的字节数组
	 * @param source
	 * @return
	 */
	public static byte[]  integer2ByteArrayL(Integer source) {
		byte[] result = new byte[4];   
        // 由低位到高位
        result[0] = (byte)( source        & 0xFF);
        result[1] = (byte)((source >> 8)  & 0xFF); 
        result[2] = (byte)((source >> 16) & 0xFF);
        result[3] = (byte)((source >> 24) & 0xFF);
        return result;
	}
	
	/**
	 * 低位优先的字节数组转成整形
	 * @param source
	 * @return
	 */
	public static Integer byteArrayL2Integer(byte[] source) {
		int value = 0;
		// 由低位到高位
		for (int i = 0; i < 4 && i < source.length; i++) {  
            value += (source[i] & 0xFF) << (8 * i);  
        } 
		return value;
	}
	
	/**
	 * 日期转字节数组
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static byte[] date2ByteArray(Date source, String pattern) {
		return DateUtil.dateToDateString(source, pattern).getBytes();
	}
	
	/**
	 * 字节数组转日期
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static Date byteArray2Date(byte[] source, String pattern) {
		Date date = null;
		try {
			date = DateUtil.convertStringToDate(new String(source).trim(), pattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	

}
