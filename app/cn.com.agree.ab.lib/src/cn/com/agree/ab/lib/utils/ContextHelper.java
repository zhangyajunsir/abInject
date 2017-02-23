package cn.com.agree.ab.lib.utils;

import java.util.HashMap;
import java.util.Map;

public class ContextHelper {
	
	private static ThreadLocal<Map<String, Object>> contextThreadLocal = new ThreadLocal<Map<String, Object>>();
	
	/**
	 * 设置上下文
	 * @param key
	 * @param value
	 */
	public static void setContext(String key, Object value) {
		Map<String, Object> context = contextThreadLocal.get();
		if (context == null) {
			context =  new HashMap<String, Object>();
			contextThreadLocal.set(context);
		}
		context.put(key, value);
	}
	
	/**
	 * 获取上下文
	 * @param key
	 * @return
	 */
	public static Object getContext(String key) {
		Map<String, Object> context = contextThreadLocal.get();
		if (context == null) {
			return null;
		}
		return context.get(key);
	}
	
	/**
	 * 删除上下文
	 * @param key
	 */
	public static void removeContext(String key) {
		Map<String, Object> context = contextThreadLocal.get();
		if (context != null) {
			context.remove(key);
		}
	}
	
	/**
	 * 销毁整个上下文
	 */
	public static void destory() {
		Map<String, Object> context = contextThreadLocal.get();
		if (context != null) {
			contextThreadLocal.remove();
		}
	}
	
}
