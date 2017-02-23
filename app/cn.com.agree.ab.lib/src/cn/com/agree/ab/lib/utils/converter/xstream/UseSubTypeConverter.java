package cn.com.agree.ab.lib.utils.converter.xstream;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cn.com.agree.commons.csv.CsvUtil;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.Mapper;

public class UseSubTypeConverter implements Converter {
	private final Class<?> type;
	private final Mapper mapper;
	private final ReflectionProvider    reflectionProvider;
	private final ConverterLookup       lookup;
	private final Map<String, Field>    aliasFieldMap;
	private final Map<String, Class<?>> aliasClassMap;
	private final ClassAliasingMapper   classAliasingMapper;
	
	
	public UseSubTypeConverter(
	        final Class<?> type, final Mapper mapper, final ReflectionProvider reflectionProvider,
	        final ConverterLookup lookup, final String aliasFieldMapping) {
		this.type   = type;
		this.mapper = mapper;
		this.lookup = lookup;
		this.aliasFieldMap = new HashMap<String, Field>();
		this.aliasClassMap = new HashMap<String, Class<?>>();
		this.reflectionProvider  = reflectionProvider;
		this.classAliasingMapper = (ClassAliasingMapper)this.mapper.lookupMapperOfType(ClassAliasingMapper.class);
		
		if (aliasFieldMapping != null) {
			@SuppressWarnings("unchecked")
			Map<String,String> aliasMap = CsvUtil.csvToMap(aliasFieldMapping);
			if (aliasMap != null) {
				for (String alias : aliasMap.keySet()) {
					Field field = null;
		            try {
		                field = type.getDeclaredField(aliasMap.get(alias));
		                if (!field.isAccessible()) {
		                    field.setAccessible(true);
		                }
		            } catch (NoSuchFieldException e) {
		                throw new IllegalArgumentException(e.getMessage() + ": " + aliasMap.get(alias));
		            }
		            aliasFieldMap.put(alias, field);
					//
		            Class<?> warpClass = classAliasingMapper.realClass(alias);
		            aliasClassMap.put(alias, warpClass);
				}
			}
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type) {
		return this.type == type;
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		// 无需实现

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		final Object result = reflectionProvider.newInstance(context.getRequiredType());
        final Class<?> resultType = result.getClass();
        
        while (reader.hasMoreChildren()) {
        	reader.moveDown();
			String key = reader.getNodeName();
			Object sub = null;
			if (aliasFieldMap.keySet().contains(key) && aliasClassMap.keySet().contains(key) && aliasClassMap.get(key) != null) {
				Class<?> subClass = aliasClassMap.get(key);
				Field    field    = aliasFieldMap.get(key);
				if (field.getType().isAssignableFrom(subClass)) {
					sub = context.convertAnother(result, subClass);	// 子节点与JAVA类型已预先通过别名的方式注册到xStream框架
					if (sub != null) {
						reflectionProvider.writeField(result, field.getName(), sub, resultType);
					}
				}
			}
			if (sub == null) {
				// TODO 其他通过注解方式的处理
				
			}
			reader.moveUp();
        }
		
		return result;
	}
	
}
