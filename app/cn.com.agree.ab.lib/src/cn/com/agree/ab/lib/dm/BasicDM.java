package cn.com.agree.ab.lib.dm;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.lib.utils.ObjectUtil;
import cn.com.agree.ab.lib.utils.ReflectionUtil;

/**
 * 数据模型基础类
 *
 */
public abstract class BasicDM implements Cloneable, Serializable {
	private static final long serialVersionUID = -3707046914855595598L;

	/**
	 * @see java.lang.Object#toString()
	 * 创建日期：2012-11-13
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * 浅层复制(如果属性为引用类型则只复制属性的引用值)当前对象
	 *
	 * @param <T>
	 * @return
	 * 创建日期：2013-1-25
	 */
	public <T> T simpleClone() {
		try {
			@SuppressWarnings("unchecked")
			T ret = (T)clone();
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取指定属性的值集合
	 * @description
	 * @param fieldNames
	 * @return
	 * 创建日期：2013-1-19
	 */
	public List<Object> getFieldValues(List<String> fieldNames) {
		List<Object> list = new ArrayList<Object>();
		if(fieldNames != null && !fieldNames.isEmpty()){
			for (String fieldName : fieldNames) {
				try {
					list.add(new PropertyDescriptor(fieldName, getClass()).getReadMethod().invoke(this));
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
			}
		}
		return list;
	}

	/**
	 * 将当前对象转换成属性和值的映射
	 * @return
	 * 创建日期：2013-1-29
	 */
	public Map<String, Object> toFieldMapping() {
		Map<String, Object> entrys = new HashMap<String, Object>();
		Map<String, Method> readMethodMapping = ObjectUtil.getReadMethodMapping(getClass());
		for (String field : readMethodMapping.keySet()) {
			try {
				entrys.put(field, readMethodMapping.get(field).invoke(this));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return entrys;
	}

	/**
	 * 转成其他类
	 * @param descClass
	 * @return
	 */
	public <T extends BasicDM> T convertTo(Class<T> descClass) {
		return JsonUtil.convert(this, descClass);
	}
	
	/**
	 * 将其他类数据赋予自己
	 * @param domain
	 */
	public <T extends BasicDM> void cloneValueFrom (T dm) {
		if (dm == null)
			return;
	
		BasicDM _dm_ = JsonUtil.convert(dm, this.getClass());
		for (String fieldName : ObjectUtil.getAllFieldNames(this.getClass())) {
			try {
				Object val = ReflectionUtil.invokeGetterMethod(_dm_, fieldName);
				if (val != null) {
					ReflectionUtil.invokeSetterMethod(this, fieldName, val, ReflectionUtil.obtainAccessibleField(this, fieldName).getType());
				}
			} catch (Exception e) {
			}
		}
	}
	
}
