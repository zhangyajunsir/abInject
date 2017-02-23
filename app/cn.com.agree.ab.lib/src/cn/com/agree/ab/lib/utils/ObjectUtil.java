package cn.com.agree.ab.lib.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @author 对象工具类
 * @since simulation-platform 
 * @create 2012-7-2 上午10:56:57
 */
public class ObjectUtil {

	/**
	 * 判断clazz是否是superClass或其子类
	 * @param clazz
	 * @param superClass
	 * @return
	 */
	public static boolean isExtends(Class<?> clazz, Class<?> superClass) {
		if(clazz == superClass) {
			return true;
		}
		Class<?> _class = clazz.getSuperclass();
		while (_class != null) {
			if(_class == superClass) {
				return true;
			}
			_class = _class.getSuperclass();
		}
		return false;
	}
	
	/**
	 * 判断clazz是否实现或继承interfaceClass
	 * @param clazz
	 * @param interfaceClass
	 * @return
	 */
	public static boolean isImplement(Class<?> clazz, Class<?> interfaceClass) {
		if(!interfaceClass.isInterface()) {
			return false;
		}
		if(clazz == interfaceClass) {
			return true;
		}
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		if(clazz.isInterface()) {
			interfaces.addAll(getSuperInterfaces(clazz));
		} else {
			interfaces.addAll(getClassInterfaces(clazz));
		}
		return interfaces.contains(interfaceClass);
	}
	
	/**
	 * @description 获得类的所有属性
	 * @param clazz
	 * @return List<Field>
	 */
	public static List<Field> getAllField(Class<?> clazz) {
		List<Field> list = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			list.add(field);
		}
		Class<?> _clazz = clazz.getSuperclass();
		while(_clazz != null) {
			list.addAll(getAllField(_clazz));
			_clazz = _clazz.getSuperclass();
		}
		return list;
	}
	
	/**
	 * 获取一个Class对象的所有字段名
	 * @param clazz
	 * @return List<String>
	 */
	public static List<String> getAllFieldNames(Class<?> clazz){
		List<String> list = new ArrayList<String>();
		List<Field> fields = getAllDeclareFields(clazz);
		for (Field field : fields) {
			list.add(field.getName());
		}
		return list;
	}
	
	/**
	 * 获取当前类的所有字段
	 * @param clazz
	 * @return List<Field>
	 */
	public static List<Field> getAllDeclareFields(Class<?> clazz){
		List<Field> list = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {
			list.add(field);
		}
		return list;
	}
	
	/**
	 * 获得类的所有标注了指定的atClass类型注解的属性
	 * @param clazz
	 * @param atClass
	 * @return
	 */
	public static List<Field> getFieldsByAnnotation(Class<?> clazz, Class<? extends Annotation> atClass) {
		List<Field> list = getAllField(clazz);
		List<Field> ret = new ArrayList<Field>();
		for (Field field : list) {
			if(field.getAnnotation(atClass) != null) {
				ret.add(field);
			}
		}
		return ret;
	}

	/**
	 * 获得类的属性和读方法的映射关系
	 * @param clazz
	 * @return Map<String, Method> key: 属性名，value： 读方法反射对象
	 */
	public static Map<String, Method> getReadMethodMapping(Class<?> clazz) {
		Map<String, Method> getLowerMapping = new HashMap<String, Method>();
		Map<String, Method> getUpperMapping = new HashMap<String, Method>();
		Map<String, Method> isLowerMapping  = new HashMap<String, Method>();
		Map<String, Method> isUpperMapping  = new HashMap<String, Method>();
		for (Method method : clazz.getMethods()) {
			if(method.getParameterTypes().length > 0) {
				continue;
			}
			String methodName = method.getName();
			if(method.getReturnType() == boolean.class && methodName.startsWith("is")) {
				if(methodName.matches("^is[a-z_].*$")) {
					isLowerMapping.put(methodName.substring(2), method);
				} else if(methodName.matches("^is[A-Z].*$")) {
					isUpperMapping.put(methodName.substring(2, 3).toLowerCase() + methodName.substring(3), method);
				}
			} else {
				if(methodName.matches("^get[a-z_].*$")) {
					getLowerMapping.put(methodName.substring(3), method);
				} else if(methodName.matches("^get[A-Z].*$")) {
					getUpperMapping.put(methodName.substring(3, 4).toLowerCase() + methodName.substring(4), method);
				}
			}
		}
		for (String field : getUpperMapping.keySet()) {
			getLowerMapping.put(field, getUpperMapping.get(field));
		}
		for (String field : isLowerMapping.keySet()) {
			getLowerMapping.put(field, isLowerMapping.get(field));
		}
		for (String field : isUpperMapping.keySet()) {
			getLowerMapping.put(field, isUpperMapping.get(field));
		}
		return getLowerMapping;
	}

	/**
	 * @description 将Map对象转换成clazz指定的类型对象
	 * @param map
	 * @param clazz
	 * @return Object
	 */
	public static <T> T toBean(Map<String, Object> map, Class<T> clazz) {
		return JsonUtil.convert(map, clazz);
	}
	
	/**
	 * @description 序列化对象
	 * @param obj
	 * @return byte[]
	 */
	public static byte[] serialize(Object obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
		    oos.flush();
		    oos.close();
		    return baos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	    
	}

	/**
	 * @description 反序列化方法
	 * @param bytes
	 * @return Object
	 */
	public static Object unSerialize(byte[] bytes) {
		Object obj = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);	
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			obj = ois.readObject();
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		 
	}
	
	/**
	 * @description 对象的深层复制，obj的实例类型必须实现序列化接口，否则将抛出异常
	 * @param obj
	 * @return T
	 */
	public static <T> T clone(T obj) {
		@SuppressWarnings("unchecked")
		T ret = (T) unSerialize(serialize(obj));
		return ret; 
	}

	/**
	 * 获得指定类型的转换器，不存在返回null
	 * @param type
	 * @return
	 */
/*	public static Converter<String, ? extends Object> getConverter(Class<? extends Object> type) {
		for (Converter<String, ? extends Object> converter : CONVERTERS) {
			ParameterizedType parameterizedType =  (ParameterizedType)converter.getClass().getGenericInterfaces()[0];
			Type[] params = parameterizedType.getActualTypeArguments();
			@SuppressWarnings("unchecked")
			Class<? extends Object> _type = (Class<? extends Object>)params[1];
			if(isExtends(type, _type)) {
				return converter;
			}
		}
		return null;
	}*/

	/**
	 * 将value值转换成type指定的类型
	 * @param <T>
	 * @param value
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
/*	public static <T> Object convert(Object value, Type type) {
		Object _value = value;
		Class<? extends Object> _type = null;
		if(type instanceof Class) {
			_type = (Class<? extends Object>)type;
		} else {
			_type = (Class<? extends Object>)((ParameterizedType)type).getRawType();
		}
		if(_value != null) {
			if(!_type.isInstance(_value)) {
				if(getConverter(_type) != null) {
					_value = getConverter(_type).convert(_value.toString());
				} else if (_value instanceof Map) {
					_value = parse((Map<String, Object>)_value, _type);
				} else {
					try {
						_value = _type.getConstructor(String.class).newInstance(_value.toString());
					} catch (Exception e) {
					}
				}
			} else {
				if(_value instanceof List) {
					List<T> __value = new ArrayList<T>();
					Type itemType = null;
					try {
						itemType = ((ParameterizedType)type).getActualTypeArguments()[0];
					} catch (Exception e) {
						itemType = Object.class;
					}
					for (Object item : (List<Object>)_value) {	
						T t = (T) convert(item, itemType);
						__value.add(t);
					}
					_value = __value;
				}
			}
		}
		return _value;
	}*/

	/**
	 * 使用转换器将map对象解析成clazz指定类型的对象 
	 * @param <T>
	 * @param map Map的value值类型只能是基本数据类型、String、List、Map且List的元素值、子Map的value值类型取值范围同上。
	 * @param clazz
	 * @return
	 */
/*	public static <T> T parse(Map<String, Object> map, Class<T> clazz) {
		Set<String> keys = map.keySet();
		List<Field> fields = getAllField(clazz);
		Map<String, Object> _map = new HashMap<String, Object>();
		Map<String, Object> __map = new HashMap<String, Object>();
		for (Field field : fields) {
			String fieldName = field.getName();
			if(!keys.contains(fieldName)) {
				continue;
			}
			try {
				new PropertyDescriptor(fieldName, clazz).getWriteMethod();
			} catch (Exception e) {
				continue;
			}
			Object value = map.get(fieldName);
			Type type = field.getGenericType();			
			Object _value = convert(value, type);
			if(_value == value || !field.getType().isInstance(_value)) {
				_map.put(fieldName, _value);
			} else {
				__map.put(fieldName, _value);
			}
		}
		T t = toBean(_map, clazz);
		for (String field : __map.keySet()) {
			try {
				new PropertyDescriptor(field, clazz).getWriteMethod().invoke(t, __map.get(field));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return t;
	}*/

	/**
	 * 递归获取类的实现接口
	 * @param clazz 指定类类型
	 * @return
	 */
	private static List<Class<?>> getClassInterfaces(Class<?> clazz) {
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		for (Class<?> interface0 : clazz.getInterfaces()) {
			interfaces.addAll(getSuperInterfaces(interface0));
		}
		Class<?> class0 = clazz.getSuperclass();
		while (class0 != null) {
			interfaces.addAll(getClassInterfaces(class0));
			class0 = class0.getSuperclass();
		}
		return interfaces;
	}

	/**
	 * 递归获取指定接口及其继承的接口
	 * @param interface0 指定接口
	 * @return
	 */
	private static List<Class<?>> getSuperInterfaces(Class<?> interface0) {
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		interfaces.add(interface0);
		for (Class<?> interface1 : interface0.getInterfaces()) {
			interfaces.addAll(getSuperInterfaces(interface1));
		}
		return interfaces;
	}

	/**
	 * 从spring-core的ObjectUtils.nullSafeEquals
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}
}
