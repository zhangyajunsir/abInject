package cn.com.agree.ab.lib.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 枚举工具类，用于将单个枚举值转化成Map对象，将所有的枚举元素转化成List
 *
 * 创建日期：2013-1-21
 * @author wangk
 */
public class EnumUtil {

	/**
	 * 将单个枚举值转化成Map对象
	 *
	 * @return
	 * 创建日期：2013-1-21
	 * 修改说明：
	 * @author wangk
	 */
	@SuppressWarnings("rawtypes")
	public static <E extends Enum> Map<String, Object> toMap(E en) {
		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> enumClass = en.getClass();
		Map<String, Method> readMethodMapping = ObjectUtil.getReadMethodMapping(enumClass);
		try {
			for (String field : readMethodMapping.keySet()) {
				map.put(field, readMethodMapping.get(field).invoke(en));
			}
			map.put("name", enumClass.getMethod("name").invoke(en));
			map.put("ordinal", enumClass.getMethod("ordinal").invoke(en));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return map;
	}

	/**
	 * 将所有的枚举元素转化成List
	 *
	 * @return
	 * 创建日期：2013-1-21
	 * 修改说明：
	 * @author wangk
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E extends Enum> List<Map<String, Object>> toList(Class<E> enumClass) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		E[] enums = null;
		try {
			enums = (E[])enumClass.getMethod("values").invoke(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (E en : enums) {
			list.add(toMap(en));
		}
		return list;
	}

}
