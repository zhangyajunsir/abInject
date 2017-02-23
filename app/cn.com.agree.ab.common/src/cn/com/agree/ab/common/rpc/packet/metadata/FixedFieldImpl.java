package cn.com.agree.ab.common.rpc.packet.metadata;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.common.utils.OgnlClassResolver;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.metadata.FixedFieldMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.ab.lib.utils.ArraysUtil;
import cn.com.agree.ab.lib.utils.Hex;

public class FixedFieldImpl extends AbstractFieldMetadata implements FixedFieldMetadata {
	/**
	 * 长度
	 */
	protected int length;
	/**
	 * 是否进行右边填充
	 */
	protected boolean rightFill;
	
	protected String fillChar;
	/**
	 * 代表长度的字节大小
	 */
	private static int lenLength = 0;
	
	@Override
	public int getMaxLength() {
		return length;
	}

	@Override
	public int getMinLength() {
		return length;
	}
	
	@Override
	public int getLenLength() {
		return lenLength;
	}

	@Override
	public void setMaxLength(int maxLength) {
		this.length = maxLength;
	}

	@Override
	public void setMinLength(int minLength) {
		this.length = minLength;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public boolean isRightFill() {
		return rightFill;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public void setRightFill(boolean rightFill) {
		this.rightFill = rightFill;
	}
	
	public String getFillChar() {
		return fillChar;
	}

	public void setFillChar(String fillChar) {
		this.fillChar = fillChar;
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
			return new byte[length];
		}
		//如果字段为用户模式
		if (valueMode == ValueMode.USER && !"".equals(value) && input instanceof PacketReqDM) {
			try {
				input = Ognl.getValue(value, input);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+value+"取值失败", e);
			}
		}
		if ((input == null || input instanceof PacketReqDM) && clazz == String.class) {
			input = "";
		} 
		if ((input == null || input instanceof PacketReqDM) && clazz == Integer.class) {
			input = 0;
		} 
		if ((input == null || input instanceof PacketReqDM) && clazz == BigDecimal.class) {
			input = BigDecimal.ZERO;
		}
		if ((input == null || input instanceof PacketReqDM) && clazz == Date.class) {
			input = new Date();
		}
		if ((input == null || input instanceof PacketReqDM) && clazz == ByteBuffer.class) {
			input = ByteBuffer.allocate(length);
		}
		String _encoding_ = encoding   != null && !encoding.equals("")   ? encoding   : "UTF-8";
		String _format_   = dataFormat != null && !dataFormat.equals("") ? dataFormat : "yyyyMMdd";
		if (input.getClass() != clazz) {
			input = MetadataTypeConverter.converter(input, clazz, _encoding_, _format_, getScale() == null ? "0" : ""+getScale());
		}
		
		byte[] msgs = (byte[])MetadataTypeConverter.converter(input, byte[].class, _encoding_, _format_);
		if (msgs.length < length) {
			byte fillByte = 0x0;
			if (clazz == String.class) {
				if (fillChar.isEmpty())
					fillChar = " ";
				fillByte = ((byte[])MetadataTypeConverter.converter(fillChar, byte[].class, _encoding_, _format_))[0];
			}
			if (rightFill) {
				msgs = ArraysUtil.rfill(msgs, length, fillByte);
			} else {
				msgs = ArraysUtil.lfill(msgs, length, fillByte);
			}
		}
		if (msgs.length > length) {
			msgs = ArraysUtil.rtruncate(msgs, length);
		}
		return msgs;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object parse(byte[] input) throws TransformException {
		if (input == null)
			return null;
		if (input.length != length) {
			throw new TransformException("字段[" + name + "]["+ desc+"]实际长度[" + input.length + "]不足获取元信息定义长度[" + length + "]的数据！");
		}
		String _encoding_ = encoding   != null && !encoding.equals("")   ? encoding   : "UTF-8";
		String _format_   = dataFormat != null && !dataFormat.equals("") ? dataFormat : "yyyyMMdd";
		Object msg = null;
		try {
			msg = MetadataTypeConverter.converter(input, clazz, _encoding_, _format_, getScale() == null ? "0" : ""+getScale());
		} catch (Exception e) {
			throw new TransformException("字段[" + name + "]["+ desc+"]转换时发生异常,["+ Hex.toHexString(input) +"]", e);
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
