package cn.com.agree.ab.common.rpc.packet.metadata;

import java.util.ArrayList;
import java.util.List;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.lib.rpc.packet.metadata.ObjectMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.ab.lib.utils.ContextHelper;

/**
 * K为报文类型，可以为常用的byte[]，Json(Xml)String，Json(Xml)Object；也可以为其它对象类型。
 * @author zhangyajun
 *
 * @param <K>
 */
public abstract class AbstractObjectMetadata<K> implements ObjectMetadata<K> {
	/**
	 * 该元信息的名称
	 */
	protected String name;
	/**
	 * 对象描述
	 */
	protected String desc;
	/**
	 * 该对象的别名
	 */
	protected String alias;
	/**
	 * 该对象所对应值对象的包装类名
	 */
	protected String wrapClazzName;
	protected Class<?> wrapClazz;
	
	/**
	 * 编码集
	 */
	protected String encoding;
	/**
	 * 设置的值，取值表达式
	 */
	protected Object value;
	
	/**
	 * 该对象定义的组成单元
	 */
	protected final List<UnitMetadata<Object, Object>> unitMetadatas = new ArrayList<UnitMetadata<Object, Object>>();
	
	/**
	 * 映射表
	 */
	protected final List<Mapping> mappings = new ArrayList<Mapping>();
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getAlias() {
		return alias;
	}
	
	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public String getWrapClazzName() {
		return wrapClazzName;
	}

	@Override
	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getDesc() {
		return this.desc;
	}
	
	@Override
	public Object get() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public void setWrapClazzName(String clazzName) {
		this.wrapClazzName = clazzName;
	}

	@Override
	public Class<?> getWrapClazz() {
		return wrapClazz;
	}

	@Override
	public void setWrapClazz(Class<?> clazz) {
		this.wrapClazz = clazz;
	}
	
	public List<UnitMetadata<Object, Object>> getUnitMetadatas() {
		return unitMetadatas;
	}
	
	public List<Mapping> getMappings() {
		return mappings;
	}
	
	public void addMapping(Mapping mapping) {
		this.mappings.add(mapping);
	}

	public UnitMetadata<Object, Object> getUnitMetadata(String name) {
		for (UnitMetadata<Object, Object> unitMetadata : unitMetadatas) {
			if (unitMetadata.getName().equals(name)) {
				return unitMetadata;
			}
		}
		return null;
	}

	public void addUnitMetadata(UnitMetadata<Object, Object> unitMetadata) {
		unitMetadatas.add(unitMetadata);
	}
	
	@SuppressWarnings("rawtypes")
	public int getMinLength() {
		int len = 0;
		for (UnitMetadata<?,?> unitMetadata : unitMetadatas) {
			if (unitMetadata instanceof ObjectMetadata) {
				len += ((ObjectMetadata)unitMetadata).getMinLength();
			} else {
				len += ((VariableFieldMetadata)unitMetadata).getMinLength();
			}
		}
		return len;
	}
	
	@SuppressWarnings("rawtypes")
	public int getMaxLength() {
		int len = 0;
		for (UnitMetadata<?,?> unitMetadata : unitMetadatas) {
			if (unitMetadata instanceof ObjectMetadata) {
				len += ((ObjectMetadata)unitMetadata).getMaxLength();
			} else {
				len += ((VariableFieldMetadata)unitMetadata).getMaxLength();
			}
		}
		return len;
	}
	
	public String getValueRex() {
		String valueRex = null;
		TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
		TradeCodeDM tradeCodeDM = null;
		if (tradeDataDM != null && tradeDataDM.getContext() != null) {
			tradeCodeDM = (TradeCodeDM)tradeDataDM.getContext().get(ITradeContextKey.TRADE_CODE_DM);
			for (Mapping mapping : mappings) {
				if (mapping.getExpID().equals(tradeCodeDM.getExpressionid().toString()) && (mapping.getTradeCode().equals(tradeCodeDM.getCode()) || mapping.getTradeCode().equals("*"))) {
					if (!mapping.getValue().isEmpty()) {
						valueRex = mapping.getValue();
						break;
					}
				}
			}
		}
		if (valueRex == null || "".equals(valueRex))
			if (get() != null && !get().toString().isEmpty())
				valueRex = get().toString();
		
		return valueRex;
	}
}
