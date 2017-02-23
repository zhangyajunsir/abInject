package cn.com.agree.ab.common.rpc.packet.config.delegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.lib.dm.AliasMappingDM;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParser;
import cn.com.agree.inject.annotations.AutoBindMapper;


/**
 * 别名映射解析代理类
 * @author liucheng
 *
 */
@AutoBindMapper(baseClass = PacketConfigParser.class)
@Named("xmlConfigAliasMappingParser")
@Singleton
public class XmlConfigAliasMappingParser implements PacketConfigParser<Document, List<AliasMappingDM>> {

	@Override
	public List<AliasMappingDM> parse(Document document) throws PacketConfigParseException {
		XPath xpath = XmlConfigUtil.staticXpath();
		XPathExpression aliasMappingExpr = null;
		try {
			aliasMappingExpr = xpath.compile("interfaces/alias-mappings");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		Node mappingNode = null;
		try {
			mappingNode = (Node)aliasMappingExpr.evaluate(document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new PacketConfigParseException("载入别名映射失败", e);
		}
		if (mappingNode == null) {
			throw new PacketConfigParseException("/interfaces/alias-mapping节点不存在");
		}
		NodeList aliasNodeSet = mappingNode.getChildNodes();
		return parseMapping(aliasNodeSet);
	}

	/**
	 * 解析别名映射节点
	 * @param nodeSet
	 * @param aliasMap
	 */
	private List<AliasMappingDM> parseMapping(NodeList nodeSet){
		List<AliasMappingDM> list = new ArrayList<AliasMappingDM>();
		for (int i = 0; i < nodeSet.getLength(); i++) {
			Node node = nodeSet.item(i);
			if("mapping".equals(node.getNodeName())){
				NamedNodeMap namedNodeMap = node.getAttributes();
				AliasMappingDM aliasMappingDM = parseAlias(namedNodeMap);
				list.add(aliasMappingDM);
			}
		}
		return list;
	}
	/**
	 * 解析别名属性
	 * @param namedNodeMap
	 * @return
	 */
	private AliasMappingDM parseAlias(NamedNodeMap namedNodeMap){
		Node alias  = namedNodeMap.getNamedItem("alias");
		Node real   = namedNodeMap.getNamedItem("real");
		Node domain = namedNodeMap.getNamedItem("domain");
		AliasMappingDM aliasMappingDM = new AliasMappingDM();
		aliasMappingDM.setAlias(alias.getNodeValue());
		aliasMappingDM.setReal(real.getNodeValue());
		aliasMappingDM.setDomain(domain == null ? "inner" : domain.getNodeValue());
		return aliasMappingDM;
	}
}
