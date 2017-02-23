package cn.com.agree.ab.common.rpc.packet.metadata;

import java.lang.reflect.Array;
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
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.lib.utils.ObjectUtil;


public class ListFormObjectMetadata extends AbstractObjectMetadata<List<?>> {
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

	@SuppressWarnings("unchecked")
	@Override
	public List<?> format(Object input) throws TransformException {
		if (input == null) {
			throw new TransformException("对象元信息[" + name + "][" + desc + "]描述的输入为空指针!");
		}
		if (!List.class.isAssignableFrom(input.getClass()) && PacketReqDM.class != input.getClass()) {
			throw new TransformException("对象元信息[" + name + "][" + desc + "]非类[" + List.class.getName() + "]和实际数据类[" + input.getClass() + "]不一致");
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
							input = _value_;
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
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object parse(List<?> input) throws TransformException {
		if (input == null) {
			throw new TransformException("元信息[" + name + "][" + desc + "]描述的输入报文不能为空指针!");
		}
		List<Object> list = new ArrayList<Object>();
		for (int i=0; i<input.size(); i++) {
			Object listItem = list.get(i);
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
			for (UnitMetadata unitMetadata : unitMetadatas) {
				if (unitMetadata instanceof VariableFieldMetadata) {
					VariableFieldMetadata fieldMetadata = (VariableFieldMetadata)unitMetadata;
					Object _input_ = null;
					try {
						_input_ = Ognl.getValue((String)fieldMetadata.get(), listItem);
					} catch (OgnlException e) {
						throw new TransformException("根据表达式"+unitMetadata.get()+"取值失败", e);
					}
					try {
						Map context = Ognl.createDefaultContext(wrapObject, new OgnlClassResolver());
						Ognl.setValue(unitMetadata.getName(), context, wrapObject, _input_);
					} catch (OgnlException e) {
						throw new TransformException("给类["+wrapClazzName+"]的属性["+unitMetadata.getName()+"]赋值失败", e);
					}
				} else
					throw new TransformException("对象元信息[" + name + "][" + desc + "]不支持[" + unitMetadata.getName() + "][" + unitMetadata.getDesc() + "]");
			}
			list.add(wrapObject);
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
						try {
							Ognl.setValue(mapping.getValue(), context, formRspDM, list);
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

}
