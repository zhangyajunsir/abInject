package cn.com.agree.ab.lib.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 * Date: 13-6-3
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author SRH
 * @version 1.0
 */
public class JsonUtil {

	private static final Logger	logger	= LoggerFactory.getLogger(JsonUtil.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		// 如果抛出org.codehaus.jackson.map.exc.UnrecognizedPropertyException:错误
		// 则需要配置下面的属性
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);	 // JSON->OBJECT设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性  
		mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true); // JSON->OBJECT接受强制非数组值到Java集合类型，解决NULL值不能赋给集合属性
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// Making JSON Output More Readable
		mapper.writerWithDefaultPrettyPrinter().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 将对象转换为json格式的字符串
	 * @param obj
	 * @return String
	 */
	public static String obj2json(Object obj) {
		String json = "";

		try {
			json = mapper.writeValueAsString(obj);
		} catch (IOException e) {
			//System.out.println(e);
			logger.debug("对象转换为json字符串时出现异常：{}\n" , e.toString());
			throw new RuntimeException("转化为json时出错" , e);
			
			//return "";
		}
		return json;
	}

	/**
	 * 将json字符串转换为对应的java对象
	 * @param json
	 * @param clazz
	 * @return T
	 */
	public static <T> T json2Obj(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * 将字符串转换为列表对象
	 * @param json
	 * @return List<Map<String, Object>>
	 */
	public static List<Map<String, Object>> json2List(String json) {
		try {
			return mapper.readValue(json, List.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}
	
	/**
	 * 
	 * @param jsonString
	 * @return JsonNode
	 */
	public static JsonNode string2JsonNode(String jsonString){
		JsonNode jsonNode = null;
		// since 2.1 use mapper.getFactory() instead
		JsonFactory factory = mapper.getJsonFactory(); 
		JsonParser jp = null;
		try {
			jp = factory.createJsonParser(jsonString);
			jsonNode = mapper.readTree(jp);
		} catch (JsonParseException e) {
			throw new RuntimeException("解析json错误", e);
		} catch (IOException e) {
			throw new RuntimeException("解析json错误", e);
		}
		return jsonNode;
	}
	
	/**
	 * 将java对象转换为json节点对象
	 * @param bean
	 * @return JsonNode
	 */
	public static JsonNode bean2Json(Object bean) {
		try {
			if (bean == null) {
				return null;
			}
			TokenBuffer buffer = new TokenBuffer(mapper);
			mapper.writeValue(buffer, bean);
			JsonNode node = mapper.readTree(buffer.asParser());
			return node;
		} catch (Exception e) {
			throw new RuntimeException("对象转换错误", e);
		}
	}

	/**
	 * 创建一个节点数组对象
	 * @return ArrayNode
	 */
	public static ArrayNode newArrayNode() {
		return mapper.createArrayNode();
	}


	/**
	 * 将json字符串转换为json节点数组
	 * @param json
	 * @return ArrayNode
	 */
	public static ArrayNode toArrayNode(String json) {
		try {
			return mapper.readValue(json, ArrayNode.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}
	
	/**
	 * 判断字符串是否是JSON对象字符串
	 * @Date:2013-6-13
	 * @author wangk
	 * @param str
	 * @return
	 * @Description:
	 * @return boolean
	 */
	public static boolean isJsonObjectString(String str) {
		return str != null && str.matches("^\\{.*\\}$");
	}
	
	/**
	 * 判断字符串是否是JSON数组字符串
	 * @Date:2013-6-13
	 * @author wangk
	 * @param str
	 * @return
	 * @Description:
	 * @return boolean
	 */
	public static boolean isJsonArrayString(String str) {
		return str != null && str.matches("^\\[.*\\]$");
	}
	
	/**
	 * JSON字符串递归转成JAVA Map对象
	 * @Date:2013-6-13
	 * @author wangk
	 * @param json
	 * @return
	 * @Description:
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> parseMap(String json) {
		if(!isJsonObjectString(json)){
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		JsonNode jsonNode = string2JsonNode(json);
		Iterator<String> keys = jsonNode.fieldNames();
		while(keys.hasNext()){
			String key = keys.next();
			JsonNode value = jsonNode.get(key);
			if(value == null ){
				continue;
			}
			if(value.isNull() ){
				map.put(key, null);
				continue;
			}
			String _value = obj2json(value);
			if(isJsonObjectString(_value)){
				map.put(key, parseMap(_value));
			}else if(isJsonArrayString(_value)){
				map.put(key, parseList(_value));
			}else{
				map.put(key, value.asText());
			}
		}
		return map;
	}
	
	/**
	 * JSON字符串递归转成JAVA List对象
	 * @Date:2013-6-13
	 * @author wangk
	 * @param jsonstr
	 * @return
	 * @Description:
	 * @return List<Object>
	 */
	public static List<Object> parseList(String jsonstr) {
		if(!isJsonArrayString(jsonstr)) {
			return null;
		}
		ArrayNode array = toArrayNode(jsonstr);
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			JsonNode value = array.get(i);			
			if(value == null) {
				list.add(null);
				continue;
			}
			String _value = obj2json(value);
			if(isJsonArrayString(_value)) {
				list.add(parseList(_value));
			} else if(isJsonObjectString(_value)) {
				list.add(parseMap(_value));
			} else {
				list.add(value.asText());
			}
		}
		return list;
	}
	
	/**
	 * 将bean对象转换成descClass指定类型的对象
	 * @Date:2013-6-13
	 * @author wangk
	 * @param bean
	 * @param descClass
	 * @return
	 * @Description:
	 * @return T
	 */
	public static <T> T convert(Object bean, Class<T> descClass) {
		try {
			Map<String, Object> map = parseMap(obj2json(bean));
			List<String> fieldNames = ObjectUtil.getAllFieldNames(descClass);
			Map<String, Object> targetMap = new HashMap<String, Object>();
			for (String key : map.keySet()) {
				for (String temp : fieldNames) {
					if(key.equalsIgnoreCase(temp)){
						Object value = targetMap.get(temp);
						if(value == null){
							targetMap.put(temp, map.get(key));
						}
					}
				}
			}
			return mapper.readValue(bean2Json(targetMap).traverse(), descClass);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    public static <T> boolean putList(List<T> list)throws Exception{
        String jsonVal= mapper.writeValueAsString(list);
        return jsonVal== null ? false : true;
    }

    public static <T> List<T> getList(String jsonVal,Class<?> clazz)throws Exception{
        TypeFactory t = TypeFactory.defaultInstance();
        // 指定容器结构和类型（这里是ArrayList和clazz）
        List<T> list = mapper.readValue(jsonVal,t.constructCollectionType(ArrayList.class,clazz));
        // 如果T确定的情况下可以使用下面的方法：
        // List<T> list = objectMapper.readValue(jsonVal, new TypeReference<List<T>>() {});
        return list;
    }
}
