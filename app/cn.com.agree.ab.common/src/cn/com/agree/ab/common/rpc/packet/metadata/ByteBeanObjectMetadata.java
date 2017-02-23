package cn.com.agree.ab.common.rpc.packet.metadata;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;
import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.common.utils.OgnlClassResolver;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.metadata.ObjectMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.ab.lib.utils.ContextHelper;

public class ByteBeanObjectMetadata extends AbstractObjectMetadata<byte[]> {


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public byte[] format(Object input) throws TransformException {
		if (input == null) {
			throw new TransformException("对象元信息[" + name + "][" + desc + "]描述的输入为空指针!");
		}
		if (wrapClazz != input.getClass() && PacketReqDM.class != input.getClass()) {
			throw new TransformException("对象元信息[" + name + "][" + desc + "]描述的包装类[" + wrapClazzName + "]和实际数据类[" + input.getClass() + "]不一致");
		}
		// 优先使用Mapping和value配置获取的JAVA BEAN
		if (input instanceof PacketReqDM) {
			TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
			TradeCodeDM tradeCodeDM = null;
			if (tradeDataDM != null && tradeDataDM.getContext() != null) {
				tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
				for (Mapping mapping : mappings) {
					if (mapping.getExpID().equals(tradeCodeDM.getExpressionid().toString()) && (mapping.getTradeCode().equals(tradeCodeDM.getCode()) || mapping.getTradeCode().equals("*"))) {
						if (!mapping.getValue().isEmpty()) {
							try {
								Object  _value_ = Ognl.getValue(mapping.getValue(), input);
								if (_value_ != null && wrapClazz != _value_.getClass()) {
									throw new TransformException("对象元信息[" + name + "][" + desc + "]描述的包装类[" + wrapClazzName + "]和根据expressId["+mapping.getExpID()+"]tradeCode["+mapping.getTradeCode()+"]的表达式["+mapping.getValue()+"]获取的实际数据类[" + _value_.getClass() + "]不一致");
								}
								input = _value_;
							} catch (OgnlException e) {
								throw new TransformException("根据表达式"+mapping.getValue()+"取值失败", e);
							}
						}
						break;
					}
				}
			}
			if (input instanceof PacketReqDM && get() != null && !"".equals(get())) {
				try {
					Object  _value_ = Ognl.getValue((String)get(), input);
					if (_value_ != null && wrapClazz != _value_.getClass()) {
						throw new TransformException("对象元信息[" + name + "][" + desc + "]描述的包装类[" + wrapClazzName + "]表达式["+get()+"]获取的实际数据类[" + _value_.getClass() + "]不一致");
					}
					input = _value_;
				} catch (OgnlException e) {
					// ★不处理，可以通过☆再处理
				}
			}
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(getMaxLength());
		for (UnitMetadata unitMetadata : unitMetadatas) {
			if (input instanceof PacketReqDM) {
				byte[] bytes = null;
				if (unitMetadata instanceof ObjectMetadata) {
					bytes = (byte[]) unitMetadata.format(input);
				} else {
					VariableFieldMetadata fieldMetadata = (VariableFieldMetadata)unitMetadata;
					Object _input_  = null;
					String valueRex = null;
					// 1~3步只需关心取值表达式逻辑（根据RefField->VarField->Object顺序取值），常量或默认值直接交给RefField或VarField处理
					// 1.先根据Field配置的表达式
					if (fieldMetadata instanceof ReferenceFieldImpl) {
						valueRex = ((ReferenceFieldImpl)fieldMetadata).getValueRex();
					} else {
						if (fieldMetadata.getValueMode() == ValueMode.USER) {
							valueRex = fieldMetadata.get().toString();
						} 
					}
					// 2.☆再根据Object配置加当前元素名组成表达式（★处理失败情况下）
					if (valueRex == null || valueRex.isEmpty())
						if (get() != null && !"".equals(get()))
							valueRex = get()+".(#this == null ? null : #this."+fieldMetadata.getName()+")";	// 使用object的value+field的name
					// 3.表达式存在，根据表达式取值
					if (valueRex != null && !valueRex.isEmpty()) {
						try {
							_input_ = Ognl.getValue(valueRex, input);
						} catch (OgnlException e) {
							throw new TransformException("根据表达式"+valueRex+"取值失败", e);
						}
					} else {
						_input_ = input;
					}
					bytes = fieldMetadata.format(_input_);
				}
				buffer.put(bytes);
			} else {
				// input直接是wrapClass类型或Null时，只通过组成单元的name取值
				Object _input_ = null;
				if (input != null) {
					try {
						_input_ = Ognl.getValue(unitMetadata.getName(), input);
					} catch (OgnlException e) {
						throw new TransformException("根据表达式"+unitMetadata.getName()+"取值失败", e);
					}
				}
				byte[] bytes = (byte[]) unitMetadata.format(_input_);	// TODO 嵌套对象类型可能会抛非NULL异常
				buffer.put(bytes);
			}
		}
		byte[] bytes = new byte[buffer.position()];
		buffer.flip();	// 恢复指针
		buffer.get(bytes);
		return bytes;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object parse(byte[] input) throws TransformException {
		if (input == null) {
			throw new TransformException("元信息[" + name + "][" + desc + "]描述的输入报文不能为空指针!");
		}
		if (input.length < super.getMinLength()) {
			throw new TransformException("元信息[" + name + "][" + desc + "]描述的输入报文必须大于最小报文长度!");
		}
		
		PacketRspDM objectRspDM = PacketRspDM.newInstance();
		Object wrapObject = null;
		try {
			if (wrapClazz.isArray()) {
				wrapObject = Array.newInstance(wrapClazz.getComponentType(), unitMetadatas.size());
			} else
				wrapObject = wrapClazz.newInstance();
		} catch (Exception e) {
			throw new TransformException(e);
		}
		if (wrapObject == null)
			throw new TransformException("不能实例化类["+wrapClazzName+"]");
		
		List<String> valueRexList = new ArrayList<String>();
		if (singleValueRexSwitch()) {
			TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
			TradeCodeDM tradeCodeDM = null;
			if (tradeDataDM != null && tradeDataDM.getContext() != null) {
				tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
				for (Mapping mapping : mappings) {
					if (mapping.getExpID().equals(tradeCodeDM.getExpressionid().toString()) && (mapping.getTradeCode().equals(tradeCodeDM.getCode()) || mapping.getTradeCode().equals("*"))) {
						if (!mapping.getValue().isEmpty()) {
							valueRexList.add(mapping.getValue());
						}
					}
				}
			}
			if (get() != null && !get().toString().isEmpty()) {
				valueRexList.add(get().toString());
			}
		}
		
		ByteBuffer buffer = ByteBuffer.wrap(input);
		for (UnitMetadata unitMetadata : unitMetadatas) {
			Object o   = null;
			if (unitMetadata instanceof ByteFormObjectMetadata) {
				// 后台下送的指定大小
				ByteFormObjectMetadata formObjectMetadata = (ByteFormObjectMetadata)unitMetadata;
				if (formObjectMetadata.isCountRex()) {
					Object count  = null;
					try {
						count = Ognl.getValue(formObjectMetadata.getCount(), wrapObject);
					} catch (OgnlException e) {
						throw new TransformException("根据表达式"+formObjectMetadata.getCount()+"取值失败", e);
					}
					ContextHelper.setContext(formObjectMetadata.getName()+"_realCount", count);
				}
			}
			Object out = parseItem(buffer, unitMetadata);
			if (out instanceof PacketRspDM) {
				if (valueRexList.size() == 0) {
					throw new TransformException("元信息[" + name + "][" + desc + "]没有赋值表达式或不支持子元信息有赋值表达式，但子元信息[" + unitMetadata.getName() + "][" + unitMetadata.getDesc() + "]存在赋值表达式!");
				}
				try {
					if (unitMetadata instanceof ReferenceFieldImpl)
						// out为PacketRspDM，说明getValueRex()不可能为空
						o = Ognl.getValue(((ReferenceFieldImpl)unitMetadata).getValueRex(), out);
					else if (unitMetadata instanceof AbstractObjectMetadata)
						// out为PacketRspDM，说明getValueRex()不可能为空
						o = Ognl.getValue(((AbstractObjectMetadata)unitMetadata).getValueRex(), out);
					else
						o = Ognl.getValue(unitMetadata.get().toString(), out);
				} catch (OgnlException e) {
					String echo = "";
					if (unitMetadata instanceof ReferenceFieldImpl)
						echo = ((ReferenceFieldImpl)unitMetadata).getValueRex();
					else if (unitMetadata instanceof AbstractObjectMetadata)
						echo = ((AbstractObjectMetadata)unitMetadata).getValueRex();
					else
						echo = unitMetadata.get().toString();
					throw new TransformException("根据表达式"+ echo +"取值失败", e);
				}
				ObjectMergeUtil.merge(objectRspDM, (PacketRspDM)out);
			} else
				o = out;
			
			try {
				Map context = Ognl.createDefaultContext(wrapObject, new OgnlClassResolver());
				Ognl.setValue(unitMetadata.getName(), context, wrapObject, o);
			} catch (OgnlException e) {
				throw new TransformException("给类["+wrapClazzName+"]的属性["+unitMetadata.getName()+"]赋值失败", e);
			}
		}
		
		if (valueRexList.size() > 0) {
			Map context = Ognl.createDefaultContext(objectRspDM, new OgnlClassResolver());
			for (String setRex : valueRexList) {
				try {
					Ognl.setValue(setRex, context, objectRspDM, wrapObject);
				} catch (OgnlException e) {
					throw new TransformException("根据表达式"+setRex+"赋值失败", e);
				}
			}
			return objectRspDM;
		} else
			return wrapObject;
	}
	
	/**
	 * 根据剩余的报文以及配置来算出真实的需要的报文长度
	 * 返回-1 代表计算失败
	 */
	public int getRealLength(byte[] input) {
		int realLen = 0;
		ByteBuffer buffer = ByteBuffer.wrap(input);
		for (UnitMetadata<?,?> unitMetadata : unitMetadatas) {
			int _realLen = getItemRealLength(buffer, unitMetadata);
			if (_realLen < 0) {
				// 计算失败
				return -1;
			}
			realLen += _realLen;
			buffer.position(realLen);
		}
		return realLen;
	}
	
	protected int getItemRealLength(ByteBuffer buffer, UnitMetadata<?,?> unitMetadata) {
		if (unitMetadata instanceof ByteBeanObjectMetadata) {
			ByteBeanObjectMetadata objectMetadata = (ByteBeanObjectMetadata)unitMetadata;
			if (buffer.remaining() < objectMetadata.getMinLength()) {
				// 剩余大小已不足要求的最小长度
				return -1;
			}
			byte[] remainingByte = new byte[buffer.remaining()];
			buffer.get(remainingByte);
			int _realLen = objectMetadata.getRealLength(remainingByte);
			if (_realLen < 0) {
				// 计算失败
				return -1;
			}
			return _realLen;
		} else {
			VariableFieldMetadata fieldMetadata = (VariableFieldMetadata)unitMetadata;
			if (buffer.remaining() < fieldMetadata.getMinLength()) {
				// 剩余大小已不足要求的最小长度
				return -1;
			}
			if (fieldMetadata.getLenLength() > 0) {
				// 变长长栏位
				byte[] lenByte = new byte[fieldMetadata.getLenLength()];
				buffer.get(lenByte);
				int length  = (Integer)MetadataTypeConverter.converter(lenByte, Integer.class);
				if (buffer.remaining() < length ) {
					// 剩余大小已不足实际长度
					return -1;
				}
				if (fieldMetadata.getLenLength()+length < fieldMetadata.getMinLength() || fieldMetadata.getLenLength()+length > fieldMetadata.getMaxLength()) {
					// 实际长度超出了最小和最大之间的范围
					return -1;
				}
				return (fieldMetadata.getLenLength()+length);
			} else {
				// 定长栏位
				return fieldMetadata.getMinLength();
			}
		}
	}
	
	protected Object parseItem(ByteBuffer buffer, UnitMetadata<Object, byte[]> unitMetadata) throws TransformException {
		Object out = null;
		if (unitMetadata instanceof ByteBeanObjectMetadata) {
			ByteBeanObjectMetadata objectMetadata = (ByteBeanObjectMetadata)unitMetadata;
			byte[] remainingByte = new byte[buffer.remaining()];
			buffer.mark();	// 记录当前指针位置
			buffer.get(remainingByte);
			int realLen = objectMetadata.getRealLength(remainingByte);
			if (realLen < 0) {
				throw new TransformException("元信息[" + name + "][" + desc + "]子元信息[" + objectMetadata.getName() + "][" + objectMetadata.getDesc() + "]计算实际长度失败!");
			}
			buffer.reset(); // 指针返回之前打记号的位置
			byte[] realByte = new byte[realLen];
			buffer.get(realByte);
			out = objectMetadata.parse(realByte);
		} else {
			VariableFieldMetadata fieldMetadata = (VariableFieldMetadata)unitMetadata;
			if (buffer.remaining() < fieldMetadata.getMinLength()) {
				// 剩余大小已不足要求的最小长度
				throw new TransformException("剩余大小已不足元信息[" + name + "][" + desc + "]子元信息[" + fieldMetadata.getName() + "][" + fieldMetadata.getDesc() + "]要求的最小长度!");
			}
			if (fieldMetadata.getLenLength() > 0) {
				// 变长栏位
				byte[] lenByte = new byte[fieldMetadata.getLenLength()];
				buffer.get(lenByte);
				int lenLength  = (Integer)MetadataTypeConverter.converter(lenByte, Integer.class);
				if (buffer.remaining() < lenLength ) {
					// 剩余大小已不足实际长度
					throw new TransformException("剩余大小已不足元信息[" + name + "][" + desc + "]子元信息[" + fieldMetadata.getName() + "][" + fieldMetadata.getDesc() + "]实际长度[" + lenLength + "]!");
				}
				if (fieldMetadata.getLenLength()+lenLength < fieldMetadata.getMinLength() || fieldMetadata.getLenLength()+lenLength > fieldMetadata.getMaxLength()) {
					// 实际长度超出了最小和最大之间的范围
					throw new TransformException("元信息[" + name + "][" + desc + "]子元信息[" + fieldMetadata.getName() + "][" + fieldMetadata.getDesc() + "]实际长度超出了最小和最大之间的范围!");
				}
				byte[] fieldByte = new byte[lenLength];
				buffer.get(fieldByte);
				out = fieldMetadata.parse(fieldByte);
			} else {
				// 定长栏位
				byte[] fieldByte = new byte[fieldMetadata.getMinLength()];
				buffer.get(fieldByte);
				out = fieldMetadata.parse(fieldByte);
			}
		}
		return out;
	}
	
	protected boolean singleValueRexSwitch() {
		return true;
	}
	

}
