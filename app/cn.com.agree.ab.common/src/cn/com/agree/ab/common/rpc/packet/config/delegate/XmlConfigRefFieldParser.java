package cn.com.agree.ab.common.rpc.packet.config.delegate;

import javax.inject.Named;
import javax.inject.Singleton;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.com.agree.ab.common.rpc.packet.metadata.ReferenceFieldImpl;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParser;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 解析引用字段解析器代理类
 *
 */
@AutoBindMapper(baseClass = PacketConfigParser.class)
@Named("xmlConfigRefFieldParser")
@Singleton
public class XmlConfigRefFieldParser implements PacketConfigParser<Node, ReferenceFieldImpl> {

	@Override
	public ReferenceFieldImpl parse(Node node) throws PacketConfigParseException {
		NamedNodeMap map = node.getAttributes();
		ReferenceFieldImpl field = new ReferenceFieldImpl();
		Node nameNode      = map.getNamedItem("name");
		Node aliasNode     = map.getNamedItem("alias");
		Node descNode      = map.getNamedItem("desc");
		Node valueNode     = map.getNamedItem("value");
		Node valueModeNode = map.getNamedItem("value-mode");
		
		field.setName     (nameNode      == null ? "" : nameNode.getNodeValue());
		field.setAlias    (aliasNode     == null ? "" : aliasNode.getNodeValue());
		field.setDesc     (descNode      == null ? "" : descNode.getNodeValue());
		field.setValue    (valueNode     == null ? "" : valueNode.getNodeValue());
		field.setValueMode(valueModeNode == null ? ValueMode.USER : ValueMode.valueOf(valueModeNode.getNodeValue().toUpperCase()));
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node mappingNode = nodeList.item(i);
			if("mapping".equals(mappingNode.getNodeName())){
				NamedNodeMap mappingAttr = mappingNode.getAttributes();
				ReferenceFieldImpl.Mapping mapping = new ReferenceFieldImpl.Mapping(
						mappingAttr.getNamedItem("express-id") == null ? "0" : mappingAttr.getNamedItem("express-id").getNodeValue(),
						mappingAttr.getNamedItem("trade-code") == null ? ""  : mappingAttr.getNamedItem("trade-code").getNodeValue(),
						mappingAttr.getNamedItem("value")      == null ? ""  : mappingAttr.getNamedItem("value").getNodeValue(),
						mappingAttr.getNamedItem("value-mode") == null ? ValueMode.USER : ValueMode.valueOf(mappingAttr.getNamedItem("value-mode").getNodeValue().toUpperCase())	
				);
				field.addMapping(mapping);
			}
		}
		return field;
	}

}
