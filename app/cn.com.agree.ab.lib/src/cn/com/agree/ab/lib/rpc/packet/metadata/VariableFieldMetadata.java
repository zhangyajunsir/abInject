package cn.com.agree.ab.lib.rpc.packet.metadata;


/**
 * 不定长字段
 * @author zhangyajun
 *
 */
public interface VariableFieldMetadata extends UnitMetadata<Object, byte[]> {

	/**
	 * 获取元信息描述的字段最大长度
	 * @return
	 */
	public int getMaxLength();
	/**
	 * 获取元信息描述的字段最小长度
	 * @return
	 */
	public int getMinLength();
	/**
	 * 获取字段对应的类型
	 * @return
	 */
	public String getClazzName();
	/**
	 * 是否在编排时允许为null
	 * @return
	 */
	public boolean isAllowNull();
	/**
	 * 获取值模式
	 * @return
	 */
	public ValueMode getValueMode();
	/**
	 * 获取数据格式掩码
	 * @return
	 */
	public String getDataFormat();
	/**
	 * 设置元信息描述的字段最大长度
	 * @param maxLength
	 */
	public void setMaxLength(int maxLength);
	/**
	 * 设置元信息描述的字段最小长度
	 * @param minLength
	 */
	public void setMinLength(int minLength);
	/**
	 * 设置在编排时是否允许为null
	 * @param allowNull
	 */
	public void setAllowNull(boolean allowNull);
	/**
	 * 设置默认值或者固定值
	 * @param value
	 */
	public void setValue(Object value);
	/**
	 * 设置取值模式
	 * @param valueMode
	 */
	public void setValueMode(ValueMode valueMode);
	/**
	 * 设置数据格式
	 * @param dataFormat
	 */
	public void setDataFormat(String dataFormat);
	
	public void setClazzName(String clazz);
	
	public Class<?> getClazz();

	public void setClazz(Class<?> clazz);
	/**
	 * 获取长度字节数组的长度
	 * @return
	 */
	public int getLenLength();

	public Integer getScale();
	
	public void setScale(Integer scale);
}
