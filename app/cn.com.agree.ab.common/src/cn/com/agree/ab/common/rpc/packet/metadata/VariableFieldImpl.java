package cn.com.agree.ab.common.rpc.packet.metadata;

import java.nio.ByteBuffer;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.common.utils.OgnlClassResolver;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.ab.lib.utils.ArraysUtil;
import cn.com.agree.ab.lib.utils.Hex;

public class VariableFieldImpl extends AbstractFieldMetadata {
	/**
	 * 最大长度
	 */
	protected int maxLength;
	/**
	 * 最小长度
	 */
	protected int minLength;
	/**
	 * 代表长度的字节大小
	 */
	protected int lenLength = 0;

	@Override
	public int getMaxLength() {
		return lenLength+maxLength;
	}

	@Override
	public int getMinLength() {
		return lenLength+minLength;
	}
	
	@Override
	public int getLenLength() {
		return lenLength;
	}

	@Override
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public void setLenLength(int lenLength) {
		this.lenLength = lenLength;
	}

	@Override
	public byte[] format(Object input) throws TransformException {
		//如果字段为常量模式,则使用常量值
		if (valueMode == ValueMode.CONSTANT) {
			input = value;
		}
		//如果字段为默认模式,同时输入为null,则使用默认值
		if (valueMode == ValueMode.DEFAULT && (input == null || input instanceof PacketReqDM)) {
			input = value;
		}
		//如果字段为内部模式,
		if (valueMode == ValueMode.INNER) {
			ByteBuffer buffer = ByteBuffer.allocate(lenLength+minLength);
			buffer.put(ArraysUtil.ltruncate((byte[])MetadataTypeConverter.converter(minLength, byte[].class), lenLength));
			return buffer.array();
		}
		//如果字段为用户模式
		if (valueMode == ValueMode.USER && !"".equals(value) && input instanceof PacketReqDM) {
			try {
				input = Ognl.getValue(value, input);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+value+"取值失败", e);
			}
		}
		if ((input == null || input instanceof PacketReqDM) && allowNull){
			return new byte[lenLength];	// 只给长度字节数组，默认值为0
		}
		if ((input == null || input instanceof PacketReqDM) && !allowNull) {
			throw new TransformException("由于字段[" + name + "]["+ desc+"]输入数据为null,导致无法处理");
		} 
		
		String _encoding_ = encoding != null && !encoding.equals("")   ? encoding   : "UTF-8";
		String _format_ = dataFormat != null && !dataFormat.equals("") ? dataFormat : "yyyyMMdd";
		if (input.getClass() != clazz) {
			input = MetadataTypeConverter.converter(input, clazz, _encoding_, _format_, getScale() == null ? "0" : ""+getScale());
		}
		byte[] msgs = (byte[])MetadataTypeConverter.converter(input, byte[].class, _encoding_, _format_);
		if (msgs.length < minLength || msgs.length > maxLength) {
			if(minLength == maxLength){
				throw new TransformException("字段[" + name + "]["+ desc+"]实际长度[" + msgs.length + "]不等于元信息定义的[" + minLength + "]！");				
			}else{				
				throw new TransformException("字段[" + name + "]["+ desc+"]实际长度[" + msgs.length + "]不在元信息定义的[" + minLength + "," + maxLength + "]区间范围内！");
			}
		}
		ByteBuffer buffer = ByteBuffer.allocate(lenLength+msgs.length);
		buffer.put(ArraysUtil.ltruncate((byte[])MetadataTypeConverter.converter(msgs.length, byte[].class), lenLength));
		buffer.put(msgs);
		return buffer.array();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object parse(byte[] input) throws TransformException {
		if (input == null)
			return null;
		if (input.length < minLength+lenLength || input.length > maxLength+lenLength) {
			throw new TransformException("字段[" + name + "]["+ desc+"]实际长度[" + input.length + "]不在[" + (minLength+lenLength) + "," + (maxLength+lenLength) + "]区间里！");
		}
		int len = 0;
		if (lenLength > 0) {
			byte[] lenByte = new byte[lenLength];
			System.arraycopy(input, 0, lenByte, 0, lenLength);
			len = (Integer)MetadataTypeConverter.converter(lenByte, Integer.class);
			if (input.length-lenLength != len) {
				throw new TransformException("字段[" + name + "]["+ desc+"]实际长度[" + input.length + "]但前[" + lenLength + "]字节代表的长度为[" + len + "]！");
			}
		} else
			len = input.length;
		byte[] datByte = new byte[len];
		System.arraycopy(input, lenLength, datByte, 0, len);
		
		String _encoding_ = encoding   != null && !encoding.equals("")   ? encoding   : "UTF-8";
		String _format_   = dataFormat != null && !dataFormat.equals("") ? dataFormat : "yyyyMMdd";
		Object msg = null;
		try {
			msg = MetadataTypeConverter.converter(datByte, clazz, _encoding_, _format_, getScale() == null ? "0" : ""+getScale());
		} catch (Exception e) {
			throw new TransformException("字段[" + name + "]["+ desc+"]转换时发生异常,["+ Hex.toHexString(datByte) +"]", e);
		}
		if (valueMode == ValueMode.USER && !"".equals(value)) {
			PacketRspDM fieldRspDM = PacketRspDM.newInstance();
			try {
				Map context = Ognl.createDefaultContext(fieldRspDM, new OgnlClassResolver());
				Ognl.setValue(value.toString(), context, fieldRspDM, msg);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+value+"赋值失败", e);
			}
			return fieldRspDM;
		} else
			return msg;
	}

}
