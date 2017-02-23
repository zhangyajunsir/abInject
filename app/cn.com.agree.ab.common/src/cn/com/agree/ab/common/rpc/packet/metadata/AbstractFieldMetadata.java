package cn.com.agree.ab.common.rpc.packet.metadata;

import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;

public abstract class AbstractFieldMetadata implements VariableFieldMetadata {
	/**
	 * 该元信息的名称
	 */
	protected String name;
	/**
	 * 字段描述
	 */
	protected String desc;
	/**
	 * 该元信息的别名
	 */
	protected String alias;
	/**
	 * 该元信息所对应值对象的类名
	 */
	protected String clazzName;
	protected Class<?> clazz;
	
	/**
	 * 编码集
	 */
	protected String encoding;
	/**
	 * 设置的值，一般用于初始化值、固定值或取值表达式
	 */
	protected Object value;
	/**
	 * 值模式
	 */
	protected ValueMode valueMode;
	/**
	 * 数据格式
	 */
	protected String dataFormat;
	/**
	 * 允许Null
	 */
	protected boolean allowNull;
	/**
	 * 小数位数
	 */
	protected Integer scale;
	
	public void vildate(){
	}
	
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
	public String getClazzName() {
		return clazzName;
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
	
	@Override
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public ValueMode getValueMode() {
		return valueMode;
	}
	
	@Override
	public void setValueMode(ValueMode valueMode) {
		this.valueMode = valueMode;
	}
	
	@Override
	public String getDataFormat() {
		return dataFormat;
	}
	
	@Override
	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}
	
	public void setClazzName(String clazz) {
		this.clazzName = clazz;
	}

	@Override
	public boolean isAllowNull() {
		return allowNull;
	}

	@Override
	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

}
