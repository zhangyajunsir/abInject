package cn.com.agree.ab.common.rpc.packet.config.delegate;

import javax.inject.Named;
import javax.inject.Singleton;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.com.agree.ab.common.rpc.packet.metadata.VariableFieldImpl;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParser;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 解析可变字段解析器代理类
 *
 */
@AutoBindMapper(baseClass = PacketConfigParser.class)
@Named("xmlConfigVariableFieldParser")
@Singleton
public class XmlConfigVariableFieldParser implements PacketConfigParser<Node, VariableFieldMetadata> {
	@Override
	public VariableFieldMetadata parse(Node node) throws PacketConfigParseException {
		NamedNodeMap map = node.getAttributes();
		VariableFieldMetadata field = new VariableFieldImpl();
		Node nameNode      = map.getNamedItem("name");
		Node aliasNode     = map.getNamedItem("alias");
		Node descNode      = map.getNamedItem("desc");
		Node maxlenNode    = map.getNamedItem("max-len");
		Node minlenNode    = map.getNamedItem("min-len");
		Node allowNullNode = map.getNamedItem("allow-null");
		Node encodingNode  = map.getNamedItem("encoding");
		Node clazzNode     = map.getNamedItem("class");
		Node valueNode     = map.getNamedItem("value");
		Node valueModeNode = map.getNamedItem("value-mode");
		Node formatNode    = map.getNamedItem("format");
		Node scaleNode     = map.getNamedItem("scale");

		field.setName     (nameNode      == null ? "" : nameNode.getNodeValue());
		field.setAlias    (aliasNode     == null ? "" : aliasNode.getNodeValue());
		field.setDesc     (descNode      == null ? "" : descNode.getNodeValue());
		field.setMaxLength(Integer.valueOf(maxlenNode  == null ? "" : maxlenNode.getNodeValue()));
		field.setMinLength(Integer.valueOf(minlenNode  == null ? "" : minlenNode.getNodeValue()));
		field.setEncoding (encodingNode  == null ? "" : encodingNode.getNodeValue());
		field.setAllowNull(Boolean.valueOf(allowNullNode  == null ? "" : allowNullNode.getNodeValue()));
		field.setValue    (valueNode     == null ? "" : valueNode.getNodeValue());
		field.setValueMode(valueModeNode == null ? ValueMode.USER : ValueMode.valueOf(valueModeNode.getNodeValue().toUpperCase()));
		field.setClazzName(clazzNode     == null ? "" : clazzNode.getNodeValue());
		field.setDataFormat((formatNode  == null ? "" : formatNode.getNodeValue()));
		field.setScale    (scaleNode     == null ? 0  : Integer.valueOf(scaleNode.getNodeValue()));
		return field;
	}

}
