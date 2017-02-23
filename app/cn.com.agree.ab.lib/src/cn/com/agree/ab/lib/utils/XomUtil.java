package cn.com.agree.ab.lib.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.lib.utils.converter.xstream.KVMapConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class XomUtil {
	private static final Logger	logger	= LoggerFactory.getLogger(XomUtil.class);
	
	/**
	 * JAXB_JavaBean转换成xml 默认编码UTF-8
	 * 
	 * @param obj
	 * @param writer
	 * @return
	 */
	public static String jaxbBeanConvertToXml(Object obj) {
		return jaxbBeanConvertToXml(obj, "UTF-8");
	}

	/**
	 * JAXB_JavaBean转换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String jaxbBeanConvertToXml(Object obj, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			result = writer.toString();
		} catch (Exception e) {
			logger.error("JAXB转XML失败", e);
			throw new RuntimeException("JAXB转XML失败", e);
		}

		return result;
	}

	/**
	 * xml转换成JAXB_JavaBean
	 * 
	 * @param xml
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T converyToJaxbBean(String xml, Class<T> c) {
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			logger.error("XML转JAXB失败", e);
			throw new RuntimeException("XML转JAXB失败", e);
		}

		return t;
	}

    /**
     * xstream_bean转换成xml
     * @Title: xstreamBeanConvertToXml 
     * @param obj 对象实例
     * @return String xml字符串
     */
    public static String xstreamBeanConvertToXml(Object obj) {
    	/*
		XStream xstream = new XStream();	// 采用xpp来解析
		XStream xstream = new XStream(new DomDriver("utf-8")); // 指定编码解析器,直接用jaxp dom来解释
    	XStream xstream = new XStream(new Dom4JDriver());	// 采用dom4j来解析
    	*/
    	XStream xstream = new XStream(new StaxDriver());	// 将采用默认的javax.xml.stream.* 
    	// 最后参数代表转换器级别，以便对Map优先执行该转换器
    	xstream.registerConverter(new KVMapConverter(xstream.getMapper()), XStream.PRIORITY_VERY_HIGH);
        // 如果没有这句，xml中的根元素会是<包.类名>；或者说：注解根本就没生效，所以的元素名就是类的属性
        xstream.processAnnotations(obj.getClass()); // 通过注解方式的，一定要有这句话
        return xstream.toXML(obj);
    }
    
    /**
     *  将传入xml文本转换成xstream_bean
     * @Title: converyToXstreamBean 
     * @param xmlStr
     * @param cls  xml对应的class类
     * @return T   xml对应的class类的实例对象
     * 
     * 调用的方法实例：PersonBean person=XmlUtil.toBean(xmlStr, PersonBean.class);
     */
    public static <T> T  converyToXstreamBean(String xmlStr, Class<?>[] cls, Map<String, Class<?>> aliasMap) {
        XStream xstream = new XStream(new StaxDriver());
        // 忽略掉未知的XML要素
    	xstream.ignoreUnknownElements();
    	// 设置ClassLoader
    	xstream.setClassLoader(new ABClassLoader());
    	// 别名
    	if (aliasMap != null) {
    		for (String key : aliasMap.keySet()) {
    			xstream.alias(key, aliasMap.get(key));
    		}
    	}
        // 最后参数代表转换器级别，以便对Map优先执行该转换器
    	xstream.registerConverter(new KVMapConverter(xstream.getMapper()), XStream.PRIORITY_VERY_HIGH);
        // 如果没有这句，xml中的根元素会是<包.类名>；或者说：注解根本就没生效，所以的元素名就是类的属性
        xstream.processAnnotations(cls);
        @SuppressWarnings("unchecked")
		T obj = (T)xstream.fromXML(xmlStr);
        return obj;
    }
    
    public static String xstreamMapConvertToXml(Map<String,Object> rootMap , String rootName) {
    	XStream xstream = new XStream(new StaxDriver());
    	xstream.alias(rootName, Map.class);
    	// 最后参数代表转换器级别，以便对Map优先执行该转换器
    	xstream.registerConverter(new KVMapConverter(xstream.getMapper()), XStream.PRIORITY_VERY_HIGH); 
    	// 解析所有java bean中的annotation
    	xstream.autodetectAnnotations(true);	
    	return xstream.toXML(rootMap);
    }
    
    public static Map<String,Object> converyToXstreamMap(String xmlStr) {
    	XStream xstream = new XStream(new StaxDriver());
    	// 忽略掉未知的XML要素
    	xstream.ignoreUnknownElements();
    	// 设置ClassLoader
    	xstream.setClassLoader(new ABClassLoader());
    	// 最后参数代表转换器级别，以便对Map优先执行该转换器
    	xstream.registerConverter(new KVMapConverter(xstream.getMapper(), HashMap.class), XStream.PRIORITY_VERY_HIGH); 
    	// 解析所有java bean中的annotation
    	xstream.autodetectAnnotations(true);
    	@SuppressWarnings("unchecked")
		Map<String,Object> obj = (Map<String,Object>)xstream.fromXML(xmlStr);
        return obj;
    }
    
	public static void main(String[] args) {
		String xmlStr="<person class='map'>"+
		  "<firstName>chen</firstName>"+
		  "<lastName>youlong</lastName>"+
		  "<telphone class='map'>"+
		    "<code>137280</code>"+
		    "<number>137280968</number>"+
		  "</telphone>"+
		  "<faxphone class='java.util.HashMap'>"+
		    "<code>20</code>"+
		    "<number>020221327</number>"+
		  "</faxphone>"+
		  "<friends class='list'>"+
		    "<name class='string'>A1</name>"+
		    "<name class='string'>A2</name>"+
		    "<name class='string'>A3</name>"+
		  "</friends>"+
		  "<pets class='java.util.ArrayList'>"+
		    "<pet class='java.util.HashMap'>"+
		      "<name>doly</name>"+
		      "<age>2</age>"+
		    "</pet>"+
		    "<pet class='java.util.HashMap'>"+
		      "<name>Ketty</name>"+
		      "<age>2</age>"+
		    "</pet>"+
		  "</pets>"+
		"</person>";
	
		Map<String,Object> mapxml = XomUtil.converyToXstreamMap(xmlStr);
		System.out.println(mapxml);
	}
	
}
