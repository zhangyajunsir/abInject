package cn.com.agree.ab.lib.utils.converter.xstream;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * 提供XStream中Map与XML之间的转换
 * Map.key <-> XML的节点名
 * Map.val <-> XML的节点值
 * @author zhangyajun
 *
 */
public class KVMapConverter extends MapConverter {

	public KVMapConverter(Mapper mapper) {
		super(mapper);
	}
	
	/**
	 * 
	 * @param mapper
	 * @param type - 自定义Map子类
	 */
	public KVMapConverter(Mapper mapper, Class<?> type) {
		super(mapper, type);
	}
	
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String,Object>) source;
		for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			 Entry<String,Object> entry = iterator.next();
			 ExtendedHierarchicalStreamWriterHelper.startNode(writer, entry.getKey(), String.class);	// 创建节点，最后那个参数没用
			 if (entry.getValue() instanceof String) {
				 writer.setValue((String)entry.getValue());
			 } else {
				 context.convertAnother(entry.getValue());
			 }
			 writer.endNode(); 
		}
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target) {
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String key = reader.getNodeName();
			Object value = null;
			String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper());
			if (classAttribute != null && classAttribute.length() > 0)	/** reader.hasMoreChildren()判断后会丢失节点属性 */
				value = readItem(reader, context, map);
			else {
				value = reader.getValue();
			}
			reader.moveUp();
			target.put(key, value);
		}
	}
}
