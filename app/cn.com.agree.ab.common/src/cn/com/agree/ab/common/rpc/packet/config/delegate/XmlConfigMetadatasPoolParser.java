package cn.com.agree.ab.common.rpc.packet.config.delegate;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParser;
import cn.com.agree.ab.lib.rpc.packet.metadata.FixedFieldMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * Xml元信息池解析代理类
 *
 */
@AutoBindMapper(baseClass = PacketConfigParser.class)
@Named("xmlConfigMetadatasPoolParser")
@Singleton
public class XmlConfigMetadatasPoolParser implements PacketConfigParser<Document, Map<String, VariableFieldMetadata>> {
	@Inject
	@Named("xmlConfigFixedFieldParser")
	private PacketConfigParser<Node, FixedFieldMetadata> fixedFieldParser;
	@Inject
	@Named("xmlConfigVariableFieldParser")
	private PacketConfigParser<Node, VariableFieldMetadata> variableFieldParser;
	
	@Override
	public Map<String, VariableFieldMetadata> parse(Document document) throws PacketConfigParseException {
		XPath xpath = XmlConfigUtil.staticXpath();
		XPathExpression metadataPoolExpr = null;
		XPathExpression fixedFieldExpr   = null;
		XPathExpression variableFieldExpr= null;
		try {
			metadataPoolExpr  = xpath.compile("interfaces/metadatas-pool");
			fixedFieldExpr    = xpath.compile("fixed-field");
			variableFieldExpr = xpath.compile("variable-field");
		} catch (XPathExpressionException e1) {
			throw new PacketConfigParseException(e1);
		}
		Node poolNode = null;
		try {
			poolNode = (Node) metadataPoolExpr.evaluate(document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new PacketConfigParseException("载入元信息池失败", e);
		}
		if (poolNode == null) {
			throw new PacketConfigParseException("/interfaces/metadatas-pool 节点不存在");
		}
		NodeList fixedPoolNodeSet = null;
		try {
			fixedPoolNodeSet = (NodeList) fixedFieldExpr.evaluate(poolNode, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new PacketConfigParseException(e);
		}
		NodeList variablePoolNodeSet = null;
		try {
			variablePoolNodeSet = (NodeList) variableFieldExpr.evaluate(poolNode, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new PacketConfigParseException(e);
		}
		Map<String, VariableFieldMetadata> metadatasPool = new HashMap<String, VariableFieldMetadata>();
		parseFixedFields(fixedPoolNodeSet, metadatasPool);
		parseVariableFields(variablePoolNodeSet, metadatasPool);
		return metadatasPool;
	}

	private void parseFixedFields(NodeList poolNodeSet,Map<String, VariableFieldMetadata> metadatasPool) throws PacketConfigParseException {
		for (int i = 0; i < poolNodeSet.getLength(); i++) {
			Node fieldNode  = poolNodeSet.item(i);
			FixedFieldMetadata field = fixedFieldParser.parse(fieldNode);
			metadatasPool.put(field.getName(), field);
		}
	}
	
	private void parseVariableFields(NodeList poolNodeSet,Map<String, VariableFieldMetadata> metadatasPool) throws PacketConfigParseException {
		for (int i = 0; i < poolNodeSet.getLength(); i++) {
			Node fieldNode  = poolNodeSet.item(i);
			VariableFieldMetadata field = variableFieldParser.parse(fieldNode);
			metadatasPool.put(field.getName(), field);
		}
	}
}
