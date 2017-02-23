package cn.com.agree.ab.common.rpc.packet.config.delegate;

import javax.inject.Named;
import javax.inject.Singleton;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.com.agree.ab.common.rpc.packet.metadata.FixedFieldImpl;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParser;
import cn.com.agree.ab.lib.rpc.packet.metadata.FixedFieldMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 解析定长字段解析器代理类
 *
 */
@AutoBindMapper(baseClass = PacketConfigParser.class)
@Named("xmlConfigFixedFieldParser")
@Singleton
public class XmlConfigFixedFieldParser implements PacketConfigParser<Node, FixedFieldMetadata> {

	@Override
	public FixedFieldMetadata parse(Node node) throws PacketConfigParseException {
		NamedNodeMap map = node.getAttributes();
		FixedFieldMetadata field = new FixedFieldImpl();
		Node nameNode      = map.getNamedItem("name");
		Node aliasNode     = map.getNamedItem("alias");
		Node descNode      = map.getNamedItem("desc");
		Node encodingNode  = map.getNamedItem("encoding");
		Node clazzNode     = map.getNamedItem("class");
		Node valueNode     = map.getNamedItem("value");
		Node valueModeNode = map.getNamedItem("value-mode");
		Node lenNode       = map.getNamedItem("len");
		Node fillStyleNode = map.getNamedItem("fill-style");
		Node formatNode    = map.getNamedItem("format");
		Node fillCharNode  = map.getNamedItem("fill-char");
		Node scaleNode     = map.getNamedItem("scale");
		
		field.setName     (nameNode      == null ? "" : nameNode.getNodeValue());
		field.setAlias    (aliasNode     == null ? "" : aliasNode.getNodeValue());
		field.setDesc     (descNode      == null ? "" : descNode.getNodeValue());
		field.setEncoding (encodingNode  == null ? "" : encodingNode.getNodeValue());
		field.setLength   (Integer.valueOf(lenNode == null ? "" : lenNode.getNodeValue()));
		field.setValue    (valueNode     == null ? "" : valueNode.getNodeValue());
		field.setValueMode(valueModeNode == null ? ValueMode.USER : ValueMode.valueOf(valueModeNode.getNodeValue().toUpperCase()));
		field.setRightFill("right".equals(fillStyleNode == null ? "" : fillStyleNode.getNodeValue()));
		field.setClazzName(clazzNode     == null ? "" : clazzNode.getNodeValue());
		field.setDataFormat((formatNode  == null ? "" : formatNode.getNodeValue()));
		field.setFillChar (fillCharNode  == null ? "" : fillCharNode.getNodeValue());
		field.setScale    (scaleNode     == null ? 0  : Integer.valueOf(scaleNode.getNodeValue()));
		return field;
	}

}
