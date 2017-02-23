package jsrccb.common.rpc.packet.config.impl;


import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import jsrccb.common.dm.rsp.EsbRspDM;
import jsrccb.common.rpc.packet.esbxml.ESBXMLPacketHeadREQ;
import jsrccb.common.rpc.packet.esbxml.ESBXMLPacketHeadRSP;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cn.com.agree.ab.common.rpc.packet.config.impl.AbstractXmlConfigParser;
import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.lib.rpc.packet.PacketEntity;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.inject.InjectPlugin;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractXmlConfigParser.class, multiple = true)
@Singleton
@Named("ibm_xml_header")
public class XmlConfigParser4EsbPacketHeader extends AbstractXmlConfigParser<PacketEntity<?, ?>> {
	

	@Override
	public String getXsdPath() {
		return "/resources/xsd/ibm_xml_header.xsd";
	}

	@Override
	protected PacketEntity<?, String> parseREQ(Document document, PacketConfig packetConfig) {
		// 1. 分析request配置信息添加到PacketConfig
		XPath xpath = XmlConfigUtil.staticXpath();
		try {
			XPathExpression requestExpr = xpath.compile("interfaces/interface/request");
			Node requestNode = (Node)requestExpr.evaluate(document, XPathConstants.NODE);
			if (requestNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/request节点不存在");
			}
			XPathExpression mappingsExpr   = xpath.compile("mappings");
			Node mappingsNode = (Node)mappingsExpr.evaluate(requestNode, XPathConstants.NODE);
			if (mappingsNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/request/mappings节点不存在");
			}
			packetConfig.addPacketConfig("mappings", parseUnitMetadata(mappingsNode, packetConfig));
			XPathExpression templateExpr   = xpath.compile("req_template");
			Node templateNode = (Node)templateExpr.evaluate(requestNode, XPathConstants.NODE);
			if (templateNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/request/template节点不存在");
			}
			packetConfig.addPacketConfig("template", templateNode.getTextContent());
		} catch (PacketConfigParseException e) {
			throw e;
		} catch (Exception e) {
			throw new PacketConfigParseException(e);
		}
		// 2. 生成PacketEntity
		ESBXMLPacketHeadREQ esbxmlPacketREQ = new ESBXMLPacketHeadREQ();
		InjectPlugin.getDefault().injectMembers(esbxmlPacketREQ);	// 给对象注入依赖
		esbxmlPacketREQ.load(packetConfig);
		return esbxmlPacketREQ;
		
	}

	@Override
	protected PacketEntity<?, EsbRspDM> parseRSP(Document document, PacketConfig packetConfig) {
		// 1. 分析response配置信息添加到PacketConfig
		XPath xpath = XmlConfigUtil.staticXpath();
		try {
			XPathExpression responseExpr = xpath.compile("interfaces/interface/response");
			Node responseNode = (Node)responseExpr.evaluate(document, XPathConstants.NODE);
			if (responseNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/response节点不存在");
			}
			XPathExpression mappingsExpr   = xpath.compile("mappings");
			Node mappingsNode = (Node)mappingsExpr.evaluate(responseNode, XPathConstants.NODE);
			if (mappingsNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/response/mappings节点不存在");
			}
			packetConfig.addPacketConfig("mappings", parseUnitMetadata(mappingsNode, packetConfig));
			String wrapAlias = xpath.evaluate("rsp_wrap_class/@alias", responseNode);
			packetConfig.addPacketConfig("rsp_wrap_class", findClassByAlias(wrapAlias, packetConfig.getInnerAliasNameManager()));
		} catch (PacketConfigParseException e) {
			throw e;
		} catch (Exception e) {
			throw new PacketConfigParseException(e);
		}
		// 2. 生成PacketEntity
		ESBXMLPacketHeadRSP esbxmlPacketRSP = new ESBXMLPacketHeadRSP();
		InjectPlugin.getDefault().injectMembers(esbxmlPacketRSP);	// 给对象注入依赖
		esbxmlPacketRSP.load(packetConfig);
		return esbxmlPacketRSP;
	}

	@Override
	protected PacketEntity<?, String> parseEXP(Document document, PacketConfig packetConfig) {
		return null;
	}

}
