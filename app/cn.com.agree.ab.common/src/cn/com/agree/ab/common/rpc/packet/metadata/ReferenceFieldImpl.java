package cn.com.agree.ab.common.rpc.packet.metadata;

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
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.common.utils.OgnlClassResolver;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.ab.lib.utils.ContextHelper;

public class ReferenceFieldImpl  extends AbstractFieldMetadata {
	
	private List<Mapping> mappings = new ArrayList<Mapping>();
	
	private VariableFieldMetadata realFieldMetadata;
	
	public static class Mapping {
		private String expID;
		private String tradeCode;
		private String value;
		private ValueMode valueMode;
		
		public Mapping(String expID, String tradeCode, String value, ValueMode valueMode) {
			this.expID     = expID;
			this.tradeCode = tradeCode;
			this.value     = value;
			this.valueMode = valueMode;
		}

		public String getExpID() {
			return expID;
		}

		public String getTradeCode() {
			return tradeCode;
		}

		public String getValue() {
			return value;
		}

		public ValueMode getValueMode() {
			return valueMode;
		}
		
	}
	
	public String getValueRex() {
		String valueRex = null;
		TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
		TradeCodeDM tradeCodeDM = null;
		if (tradeDataDM != null && tradeDataDM.getContext() != null)
			tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
		if (tradeCodeDM != null) {
			for (Mapping mapping : mappings) {
				if (mapping.expID.equals(tradeCodeDM.getExpressionid().toString()) && (mapping.tradeCode.equals(tradeCodeDM.getCode()) || mapping.tradeCode.equals("*"))) {
					if (mapping.valueMode == ValueMode.USER && !mapping.value.isEmpty()) {
						valueRex = mapping.value;
						break;
					}
				}
			}
		}
		if (valueRex == null || "".equals(valueRex))
			if (valueMode == ValueMode.USER && !get().toString().isEmpty())
				valueRex = get().toString();
		if (valueRex == null || "".equals(valueRex))
			if (realFieldMetadata.getValueMode() == ValueMode.USER && !realFieldMetadata.get().equals("")) 
				valueRex = realFieldMetadata.get().toString();
		
		return valueRex;
	}
	
	@Override
	public byte[] format(Object input) throws TransformException {
		Object  value = null;
		boolean isGet = false;
		TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
		TradeCodeDM tradeCodeDM = null;
		if (tradeDataDM != null && tradeDataDM.getContext() != null)
			tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
		//1.交易特定配置处理
		if (tradeCodeDM != null) {
			for (Mapping mapping : mappings) {
				if (mapping.expID.equals(tradeCodeDM.getExpressionid().toString()) && (mapping.tradeCode.equals(tradeCodeDM.getCode()) || mapping.tradeCode.equals("*"))) {
					if (mapping.valueMode == ValueMode.CONSTANT) {
						value = mapping.value;
						isGet = true;
					} 
					if (mapping.valueMode == ValueMode.DEFAULT && (input == null || input instanceof PacketReqDM)) {
						value = mapping.value;
						isGet = true;
					} 
					if (mapping.valueMode == ValueMode.USER  && input instanceof PacketReqDM && !mapping.value.isEmpty()) {
						try {
							value = Ognl.getValue(mapping.value, input);
						} catch (OgnlException e) {
							throw new TransformException("根据表达式"+mapping.value+"取值失败", e);
						}
						isGet = true;
					}
					break;
				}
			}
		}
		//2.Field公共配置处理
		if (!isGet) {
			if (valueMode == ValueMode.CONSTANT) {
				value = get();
				isGet = true;
			} 
			if (valueMode == ValueMode.DEFAULT && (input == null || input instanceof PacketReqDM)) {
				value = get();
				isGet = true;
			} 
			if (valueMode == ValueMode.USER && input instanceof PacketReqDM && !get().equals("")) {
				try {
					value = Ognl.getValue(get().toString(), input);
				} catch (OgnlException e) {
					throw new TransformException("根据表达式"+get()+"取值失败", e);
				}
				isGet = true;
			}
		}
		//3.交给真实的定长或非定长Field处理
		if (!isGet)
			value = input;

		return realFieldMetadata.format(value);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object parse(byte[] input) throws TransformException {
		Object out = realFieldMetadata.parse(input);
		Object o   = null;
		if (out instanceof PacketRspDM) {
			try {
				o = Ognl.getValue(realFieldMetadata.get().toString(), out);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+realFieldMetadata.get().toString()+"取值失败", e);
			}
		} else
			o = out;
		
		boolean isOgnlSet = false;
		PacketRspDM fieldRspDM = PacketRspDM.newInstance();
		// 1.给符合的交易进行表达式赋值
		TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
		TradeCodeDM tradeCodeDM = null;
		if (tradeDataDM != null && tradeDataDM.getContext() != null)
			tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
		if (tradeCodeDM != null) {
			for (Mapping mapping : mappings) {
				if (mapping.expID.equals(tradeCodeDM.getExpressionid().toString()) && (mapping.tradeCode.equals(tradeCodeDM.getCode()) || mapping.tradeCode.equals("*"))) {
					if (mapping.valueMode == ValueMode.USER && !mapping.value.isEmpty()) {
						try {
							Map context = Ognl.createDefaultContext(fieldRspDM, new OgnlClassResolver());
							Ognl.setValue(mapping.value, context, fieldRspDM, o);
						} catch (OgnlException e) {
							throw new TransformException("根据表达式"+mapping.value+"赋值失败", e);
						}
						isOgnlSet = true;
					}
				}
			}
		}
		// 2.给当前引用栏位进行表达式赋值
		if (valueMode == ValueMode.USER && !get().toString().isEmpty()) {
			try {
				Map context = Ognl.createDefaultContext(fieldRspDM, new OgnlClassResolver());
				Ognl.setValue(get().toString(), context, fieldRspDM, o);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+value+"赋值失败", e);
			}
			isOgnlSet = true;
		}
		// 3.合并
		if (out instanceof PacketRspDM) {
			ObjectMergeUtil.merge(fieldRspDM, (PacketRspDM)out);
			isOgnlSet = true;
		}
		// 返回
		if (isOgnlSet)
			return fieldRspDM;
		else
			return o;
	}
	
	public void addMapping(Mapping mapping) {
		this.mappings.add(mapping);
	}

	@Override
	public int getMaxLength() {
		return realFieldMetadata.getMaxLength();
	}

	@Override
	public int getMinLength() {
		return realFieldMetadata.getMinLength();
	}

	@Override
	public void setMaxLength(int maxLength) {
	}

	@Override
	public void setMinLength(int minLength) {
	}
	
	public void setRealFieldMetadata(VariableFieldMetadata realFieldMetadata) {
		this.realFieldMetadata = realFieldMetadata;
	}
	
	public VariableFieldMetadata getRealFieldMetadata() {
		return realFieldMetadata;
	}
 
	@Override
	public ValueMode getValueMode() {
		return realFieldMetadata.getValueMode();
	}
	

	public List<Mapping> getMappings() {
		return mappings;
	}
	
	@Override
	public int getLenLength() {
		return realFieldMetadata.getLenLength();
	}
	
	@Override
	public String getEncoding() {
		return realFieldMetadata.getEncoding();
	}
}
