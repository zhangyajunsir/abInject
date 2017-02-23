package cn.com.agree.ab.common.rpc.packet.config.delegate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.com.agree.ab.common.rpc.packet.metadata.ByteBeanObjectMetadata;
import cn.com.agree.ab.common.rpc.packet.metadata.ByteFormObjectMetadata;
import cn.com.agree.ab.common.rpc.packet.metadata.ListFormObjectMetadata;
import cn.com.agree.ab.common.rpc.packet.metadata.ReferenceFieldImpl;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParser;
import cn.com.agree.ab.lib.rpc.packet.metadata.FixedFieldMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.ObjectMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 解析报文对象解析器代理类
 *
 */
@SuppressWarnings("rawtypes")
@AutoBindMapper(baseClass = PacketConfigParser.class)
@Named("xmlConfigObjectParser")
@Singleton
public class XmlConfigObjectParser implements PacketConfigParser<Node, ObjectMetadata> {
	@Inject
	@Named("xmlConfigFixedFieldParser")
	private PacketConfigParser<Node, FixedFieldMetadata> fixedFieldParser;
	@Inject
	@Named("xmlConfigVariableFieldParser")
	private PacketConfigParser<Node, VariableFieldMetadata> variableFieldParser;
	@Inject
	@Named("xmlConfigRefFieldParser")
	private PacketConfigParser<Node, ReferenceFieldImpl> referenceFieldParser;
	
	@SuppressWarnings("unchecked")
	@Override
	public ObjectMetadata parse(Node node) throws PacketConfigParseException {
		NamedNodeMap map = node.getAttributes();
		Node nameNode      = map.getNamedItem("name");
		Node aliasNode     = map.getNamedItem("alias");
		Node descNode      = map.getNamedItem("desc");
		Node encodingNode  = map.getNamedItem("encoding");
		Node clazzNode     = map.getNamedItem("wrap-class");
		Node valueNode     = map.getNamedItem("value");
		Node typeNode      = map.getNamedItem("type");
		Node countNode     = map.getNamedItem("count");
		Node colModeNode   = map.getNamedItem("col-mode");

		String objectType  = typeNode == null ? "" : typeNode.getNodeValue();
		ObjectMetadata objectMetadata =  null;
		if ("bean".equals(objectType)) {
			objectMetadata = new ByteBeanObjectMetadata();
		} else if ("form".equals(objectType)) {
			ByteFormObjectMetadata byteFormObjectMetadata = new ByteFormObjectMetadata();
			byteFormObjectMetadata.setCount     (countNode   == null ? "" : countNode.getNodeValue());
			byteFormObjectMetadata.setColumnMode(colModeNode == null ? "crosswise" : colModeNode.getNodeValue());
			objectMetadata = byteFormObjectMetadata;
		} else if ("list".equals(objectType)) {
			ListFormObjectMetadata ListFormObjectMetadata = new ListFormObjectMetadata();
			ListFormObjectMetadata.setCount     (countNode   == null ? "" : countNode.getNodeValue());
			ListFormObjectMetadata.setColumnMode(colModeNode == null ? "crosswise" : colModeNode.getNodeValue());
			objectMetadata = ListFormObjectMetadata;
		} else {
			return null;
		}
		objectMetadata.setName         (nameNode      == null ? "" : nameNode.getNodeValue());
		objectMetadata.setAlias        (aliasNode     == null ? "" : aliasNode.getNodeValue());
		objectMetadata.setDesc         (descNode      == null ? "" : descNode.getNodeValue());
		objectMetadata.setEncoding     (encodingNode  == null ? "" : encodingNode.getNodeValue());
		objectMetadata.setWrapClazzName(clazzNode     == null ? "" : clazzNode.getNodeValue());
		objectMetadata.setValue        (valueNode     == null ? "" : valueNode.getNodeValue());
		
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node metadataNode = nodeList.item(i);
			if ("fixed-field"   .equals(metadataNode.getNodeName())) {
				VariableFieldMetadata fieldMetadata = fixedFieldParser.parse(metadataNode);
				if(fieldMetadata.getEncoding() == null || fieldMetadata.getEncoding().isEmpty()){
					fieldMetadata.setEncoding(objectMetadata.getEncoding());
				}
				objectMetadata.addUnitMetadata(fieldMetadata);
			}
			if ("variable-field".equals(metadataNode.getNodeName())) {
				VariableFieldMetadata fieldMetadata = variableFieldParser.parse(metadataNode);
				if(fieldMetadata.getEncoding() == null || fieldMetadata.getEncoding().isEmpty()){
					fieldMetadata.setEncoding(objectMetadata.getEncoding());
				}
				objectMetadata.addUnitMetadata(fieldMetadata);
			}
			if ("field"         .equals(metadataNode.getNodeName())) {
				VariableFieldMetadata fieldMetadata = referenceFieldParser.parse(metadataNode);
				objectMetadata.addUnitMetadata(fieldMetadata);
			}
			if ("object"        .equals(metadataNode.getNodeName())) {
				ObjectMetadata _objectMetadata_ = parse(metadataNode);
				objectMetadata.addUnitMetadata(_objectMetadata_);
			}
			if ("mapping"       .equals(metadataNode.getNodeName())) {
				NamedNodeMap mappingAttr = metadataNode.getAttributes();
				ObjectMetadata.Mapping mapping = new ObjectMetadata.Mapping(
						mappingAttr.getNamedItem("express-id") == null ? "0" : mappingAttr.getNamedItem("express-id").getNodeValue(),
						mappingAttr.getNamedItem("trade-code") == null ? ""  : mappingAttr.getNamedItem("trade-code").getNodeValue(),
						mappingAttr.getNamedItem("value")      == null ? ""  : mappingAttr.getNamedItem("value").getNodeValue(),
						mappingAttr.getNamedItem("item-bit")   == null ? ""  : mappingAttr.getNamedItem("item-bit").getNodeValue()	
				);
				objectMetadata.addMapping(mapping);
			}
		}

		return objectMetadata;
	}

}
