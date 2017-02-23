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
import cn.com.agree.ab.common.utils.OgnlClassResolver;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.lib.utils.ObjectUtil;

public class ByteFormObjectMetadata extends ByteBeanObjectMetadata {
	/**
	 * 默认行数，可以是表达式
	 */
	protected String  count;
	protected boolean isCountRex;
	/**
	 * 列模式（crosswise-横向，vertical-竖向）
	 */
	protected String columnMode;
	

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
		try {
			Integer.valueOf(count);
			this.isCountRex = false;
		} catch (Throwable e) {
			this.isCountRex = true;
		}
	}
	
	public boolean isCountRex() {
		return isCountRex;
	}

	public String getColumnMode() {
		return columnMode;
	}

	public void setColumnMode(String columnMode) {
		this.columnMode = columnMode;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public byte[] format(Object input) throws TransformException {
		if (input == null) {
			throw new TransformException("对象元信息[" + name + "][" + desc + "]描述的输入为空指针!");
		}
		if (!List.class.isAssignableFrom(input.getClass()) && PacketReqDM.class != input.getClass()) {
			throw new TransformException("对象元信息[" + name + "][" + desc + "]非类[" + List.class.getName() + "]和实际数据类[" + input.getClass() + "]不一致");
		}
		
		if (isCountRex() && input instanceof PacketReqDM) { 
			int _count_ = 0;
			try {
				_count_ = (Integer)Ognl.getValue(getCount(), input);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+getCount()+"取值失败", e);
			}
			ContextHelper.setContext(name+"_realCount", _count_);
		}
		// 1.使用Mapping配置获取的JAVA BEAN
		if (input instanceof PacketReqDM) {
			TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
			TradeCodeDM tradeCodeDM = null;
			if (tradeDataDM != null && tradeDataDM.getContext() != null) {
				tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
				for (Mapping mapping : mappings) {
					if (mapping.getExpID().equals(tradeCodeDM.getExpressionid().toString()) && (mapping.getTradeCode().equals(tradeCodeDM.getCode()) || mapping.getTradeCode().equals("*"))) {
						if (!mapping.getValue().isEmpty()) {
							Object  _value_ = null;
							try {
								_value_ = Ognl.getValue(mapping.getValue(), input);
							} catch (OgnlException e) {
								throw new TransformException("根据表达式"+mapping.getValue()+"取值失败", e);
							}
							if (_value_ == null || !List.class.isAssignableFrom(_value_.getClass())) {
								throw new TransformException("对象元信息[" + name + "][" + desc + "]非类[" + List.class.getName() + "]，实际根据expressId["+mapping.getExpID()+"]tradeCode["+mapping.getTradeCode()+"]的表达式["+mapping.getValue()+"]获取的实际数据类[" + _value_.getClass() + "]");
							}
							// TODO 根据itemBit格式值整理后List赋值给input
							input = _value_;
							
							
							// END
						}
						break;
					}
				}
			}
		}
		// 2.使用value的表达式
		if (input instanceof PacketReqDM && !value.toString().isEmpty()) {
			try {
				Object  _value_ = Ognl.getValue(value.toString(), input);
				if (_value_ == null || !List.class.isAssignableFrom(_value_.getClass())) {
					throw new TransformException("对象元信息[" + name + "][" + desc + "]非类[" + List.class.getName() + "]，实际根据表达式["+value.toString()+"]获取的实际数据类[" + _value_.getClass() + "]");
				}
				input = _value_;
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+value.toString()+"取值失败", e);
			}
		}
		// 3.校验input此时是否为List
		if (!List.class.isAssignableFrom(input.getClass())) {
			throw new TransformException("对象元信息[" + name + "][" + desc + "]未获取到类[" + List.class.getName() + "]对象");
		}
		// 4.FORM不适用内部组件定义个性化取值方式
		List<Object> list = (List<Object>)input;
		// 5.校验类型
		for (Object obj : list) {
			if (!ObjectUtil.isExtends(obj.getClass(), wrapClazz) && !ObjectUtil.isImplement(obj.getClass(), wrapClazz))
				throw new TransformException("对象元信息[" + name + "][" + desc + "]描述的包装类[" + wrapClazzName + "]和实际数据类[" + obj.getClass() + "]不一致");
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(getMaxLength());
		// TODO 固定大小需要根据实际大小来缩小或补足行数
		if ("vertical".equals(columnMode)) {
			// 列模式
			for (UnitMetadata unitMetadata : unitMetadatas) {
				for (Object obj : list) {
					// obj直接是wrapClass类型，只通过组成单元的name取值
					Object _input_ = null;
					try {
						_input_ = Ognl.getValue(unitMetadata.getName(), obj);
					} catch (OgnlException e) {
						throw new TransformException("根据表达式"+unitMetadata.getName()+"取值失败", e);
					}
					byte[] bytes = (byte[]) unitMetadata.format(_input_);
					buffer.put(bytes);
				}
			}
		} else {
			// 行模式
			for (Object obj : list) {
				for (UnitMetadata unitMetadata : unitMetadatas) {
					// obj直接是wrapClass类型，只通过组成单元的name取值
					Object _input_ = null;
					try {
						_input_ = Ognl.getValue(unitMetadata.getName(), obj);
					} catch (OgnlException e) {
						throw new TransformException("根据表达式"+unitMetadata.getName()+"取值失败", e);
					}
					byte[] bytes = (byte[]) unitMetadata.format(_input_);
					buffer.put(bytes);
				}
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
		if (input.length < getMinLength()) {
			throw new TransformException("元信息[" + name + "][" + desc + "]描述的输入报文必须大于最小报文长度!");
		}
		
		List<Object> list = new ArrayList<Object>();
		ByteBuffer buffer = ByteBuffer.wrap(input);
		int position = 0;
		byte[] remainingByte = new byte[buffer.remaining()];
		int _count_ = getRealCount();
		if ("vertical".equals(columnMode)) {
			// 列模式
			if (_count_ < 0)
				throw new TransformException("元信息[" + name + "][" + desc + "]列模式需要配置具体笔数!");
			for (int i=0; i<_count_; i++) {
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
				list.add(wrapObject);
			}
			for (UnitMetadata unitMetadata : unitMetadatas) {
				for (int i=0; i<_count_; i++) {
					Object out = parseItem(buffer, unitMetadata);
					if (out instanceof PacketRspDM) 
						throw new TransformException("元信息[" + name + "][" + desc + "]为表格处理，不支持组件个性化赋值!");
					Object wrapObject = list.get(i);
					try {
						Map context = Ognl.createDefaultContext(wrapObject, new OgnlClassResolver());
						Ognl.setValue(unitMetadata.getName(), context, wrapObject, out);
					} catch (OgnlException e) {
						throw new TransformException("给类["+wrapClazzName+"]的属性["+unitMetadata.getName()+"]赋值失败", e);
					}
				}
			}
		} else {
			// 行模式
			int  count  = _count_ < 0 ? 10 : _count_;	// RealCount小于0时，代表全部大小，默认最高为10笔记录大小
			for (int i=0; i<count; i++) {
				int realLen = super.getRealLength(remainingByte);
				if (realLen < 0) {
					if (_count_ < 0)
						break;
					else
						throw new TransformException("元信息[" + name + "][" + desc + "]处理第[" + i + "]记录失败!");
				}
				
				byte[] realByte = new byte[realLen];
				buffer.get(realByte);
				Object o = super.parse(realByte);
				if (o instanceof PacketRspDM) 
					throw new TransformException("元信息[" + name + "][" + desc + "]为表格处理，不支持组件个性化赋值!");
				list.add(o);
				
				position += realLen;
				buffer.position(position);
				buffer.mark(); // 标记
				remainingByte = new byte[buffer.remaining()];
				buffer.get(remainingByte);
				buffer.reset();// 恢复标记
			}
		}

		PacketRspDM formRspDM = PacketRspDM.newInstance();
		Map context = Ognl.createDefaultContext(formRspDM, new OgnlClassResolver());
		boolean isOgnlSet = false; 
		TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
		TradeCodeDM tradeCodeDM = null;
		if (tradeDataDM != null && tradeDataDM.getContext() != null) {
			tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
			for (Mapping mapping : mappings) {
				if (mapping.getExpID().equals(tradeCodeDM.getExpressionid().toString()) && (mapping.getTradeCode().equals(tradeCodeDM.getCode()) || mapping.getTradeCode().equals("*"))) {
					if (!mapping.getValue().isEmpty()) {
						List<Object> _list_ = list;
						if (mapping.getItemBit() != null) {
							// 根据itemBit格式值整理List并赋值给_list_
							_list_ = new ArrayList<Object>();
							for (Object item : list) {
								if (!(item instanceof String[])) 
									throw new TransformException("元信息[" + name + "][" + desc + "]包装类非StringArray，不能使用itemBit格式整理!");
								String[] row = new String[mapping.getItemBit().length];
								for (int i=0; i<row.length; i++) {
									row[i] = ((String[])item)[mapping.getItemBit()[i]];
								}
								_list_.add(row);
							}
						}
						try {
							Ognl.setValue(mapping.getValue(), context, formRspDM, _list_);
						} catch (OgnlException e) {
							throw new TransformException("根据表达式"+value+"赋值失败", e);
						}
						isOgnlSet = true;
					}
				}
			}
		}
		if (get() != null && !get().toString().isEmpty()) {
			try {
				Ognl.setValue(get().toString(), context, formRspDM, list);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+value+"赋值失败", e);
			}
			isOgnlSet = true;
		}
		
		if (isOgnlSet) {
			return formRspDM;
		} else {
			return list;
		}
	}
	
	public int getRealCount() {
		int _count_ = 0;
		if (isCountRex) {
			if (ContextHelper.getContext(name+"_realCount") != null)
				_count_ = (Integer)ContextHelper.getContext(name+"_realCount");
		} else
			_count_ =  Integer.valueOf(count);
		
		return _count_;
	}
	
	public int getMinLength() {
		return super.getMinLength() * getRealCount();
	}
	
	public int getMaxLength() {
		return super.getMaxLength() * getRealCount();
	}
	
	public int getRealLength(byte[] input) {
		int _count_ = getRealCount();
		if (_count_ < 0) { 
			if ("vertical".equals(columnMode)) {
				if (_count_ < 0)
					throw new BasicRuntimeException("元信息[" + name + "][" + desc + "]列模式需要配置具体笔数!");
			} else	// RealCount小于0时，代表全部大小，默认最高为10笔记录大小
				return input.length;
		}
		ByteBuffer buffer = ByteBuffer.wrap(input);
		int realLen = 0;
		if ("vertical".equals(columnMode)) {
			for (UnitMetadata<?,?> unitMetadata : unitMetadatas) {
				for (int i=0; i<_count_; i++) {
					int _realLen = getItemRealLength(buffer, unitMetadata);
					if (_realLen < 0) {
						return -1;
					}
					realLen += _realLen;
					buffer.position(realLen);
				}
			}
		} else {
			for (int i=0; i<_count_; i++) {
				int _realLen_ = super.getRealLength(input);
				if (_realLen_ < 0)
					return -1;
				realLen += _realLen_;
				buffer.position(realLen);
				input = new byte[buffer.remaining()];
				buffer.get(input);
			}
		}
		return realLen;
	}
	
	protected boolean singleValueRexSwitch() {
		return false;
	}

}
