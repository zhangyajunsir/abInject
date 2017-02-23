package cn.com.agree.ab.common.utils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import cn.com.agree.ab.lib.dm.BasicDM;
 
/**
 * 对象合并工具类
 * 
 */
public class ObjectMergeUtil {
 
    public static <T extends BasicDM> void merge(T dest, T... sources) {
    	if (sources == null || sources.length == 0)
    		return;
    	for (T source : sources) {
    		merge(dest, source, true, true);
    	}
    }
     
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends BasicDM> void merge(T dest, T source, boolean overwrite, boolean ignoreNull) {
    	// 如果源对象为空，则直接返回null
    	if(null == source || null == dest) 
    		return ;
    	
    	// 获取源对象所有Get方法
		Map<String, Method> getterMethods = new HashMap<String, Method>();
		for (Method method : source.getClass().getMethods()) {
		    String name = method.getName();
		    if(name.startsWith("get") && method.getParameterTypes().length == 0){
		    	getterMethods.put(name.substring(3), method);
		    }
		}
		// 获取目对象所有Set方法
		Map<String, Method> setterMethods = new HashMap<String, Method>();
		for (Method method : dest.getClass().getMethods()) {
		    String name = method.getName();
		    if(name.startsWith("set") && method.getParameterTypes().length == 1){
		    	setterMethods.put(name.substring(3), method);
		    }
		}
		for (Map.Entry<String,Method> entry : setterMethods.entrySet()) {
			try {
				if (getterMethods.get(entry.getKey()) == null) {
					continue;
				}
				if (getterMethods.get(entry.getKey()).getReturnType() != entry.getValue().getParameterTypes()[0]) {
					continue;
				}
				Object sourceV = getterMethods.get(entry.getKey()).invoke(source);
				if (sourceV == null && ignoreNull){
					continue;
				}
				Object destV = dest.getClass().getMethod("get"+entry.getKey()).invoke(dest);
				if (destV != null && !overwrite) {
					continue;
				}
				if (sourceV != null && destV != null && sourceV .equals(destV)) {
					continue;
				}
				if (destV != null && destV instanceof BasicDM) {
					merge((BasicDM)destV, (BasicDM)sourceV, overwrite, ignoreNull);
					continue;
				}
				if (destV != null && destV instanceof Map) {
					merge((Map)destV, (Map)sourceV, overwrite, ignoreNull);
					continue;
				}
				if (destV != null && destV instanceof Collection) {
					((Collection)destV).addAll((Collection)sourceV);
					continue;
				}
				entry.getValue().invoke(dest, sourceV);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void merge(Map dest, Map source, boolean overwrite, boolean ignoreNull) {
    	if(null == source || null == dest) {
    		return ;
    	}
    	Iterator<Map.Entry> sourceKeys = source.entrySet().iterator(); 
    	while(sourceKeys.hasNext()) { 
    		Map.Entry entry =(Map.Entry)sourceKeys.next(); 
    		if (dest instanceof Hashtable && (entry.getKey() == null ||entry.getValue() == null)) {
    			continue;
    		}
    		if (dest.get(entry.getKey()) == null) {
    			dest.put(entry.getKey(), entry.getValue());
    			continue;
    		}
    		if (dest.get(entry.getKey()) != null && !overwrite) {
    			continue;
    		}
    		if (dest.get(entry.getKey()) != null && entry.getValue() != null && dest.get(entry.getKey()) == entry.getValue()) {
				continue;
			}
    		if (dest.get(entry.getKey()) instanceof BasicDM) {
    			if (entry.getValue() instanceof BasicDM) 
    				merge((BasicDM)dest.get(entry.getKey()), (BasicDM)entry.getValue(), overwrite, ignoreNull);
    			continue;	
    		}
    		if (dest.get(entry.getKey()) instanceof Map) {
    			if (entry.getValue() instanceof Map) 
    				merge((Map)dest.get(entry.getKey()), (Map)entry.getValue(), overwrite, ignoreNull);
    			continue;	
    		}
    		if (dest.get(entry.getKey()) instanceof Collection) {
    			if (entry.getValue() instanceof Collection) 
    				((Collection)dest.get(entry.getKey())).addAll((Collection)entry.getValue());
    			continue;	
    		}
    		dest.put(entry.getKey(), entry.getValue());
		} 
    	
    }
    
}