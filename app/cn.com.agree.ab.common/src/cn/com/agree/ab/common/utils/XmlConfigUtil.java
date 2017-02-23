package cn.com.agree.ab.common.utils;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;

public class XmlConfigUtil {
	
	private XmlConfigUtil(){
	}
	
	private static XPathFactory  xfactory = XPathFactory.newInstance();
	
	public static void vildateSchema(URL xmlURL, URL xsdURL) throws PacketConfigParseException {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(xsdURL);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlURL.toURI().toASCIIString()));
		} catch (Exception e) {
			throw new PacketConfigParseException("校验接口配置文件发生异常!",e);
		}
	}

	/**
	 * 返回不带命名空间的Document
	 * @param is
	 * @return
	 * @throws PacketConfigParseException
	 */
	public static Document initDoc(InputStream is) throws PacketConfigParseException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			return document;
		} catch (Exception e) {
			throw new PacketConfigParseException("加载接口配置文件发生异常!",e);
		}
	}
	

	/**
	 * 没有设置命名空间的XPath
	 * @return
	 * @throws PacketConfigParseException
	 */
	public static XPath staticXpath() throws PacketConfigParseException {
		XPath xPath = xfactory.newXPath();
		return xPath;
	}
	
	public static String getPacketCompany(Document document) throws PacketConfigParseException {
		Element root = document.getDocumentElement();
		String schemaLocation = root.getAttribute("xsi:schemaLocation");
		if (schemaLocation == null || schemaLocation.length() == 0) 
			return null;
		return StringUtils.substringBeforeLast(StringUtils.substringAfterLast(schemaLocation, "/"), ".");
	}
}
