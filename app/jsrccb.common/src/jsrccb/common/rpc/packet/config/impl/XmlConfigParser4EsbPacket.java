package jsrccb.common.rpc.packet.config.impl;


import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import jsrccb.common.rpc.packet.esbxml.ESBXMLPacketREQ;
import jsrccb.common.rpc.packet.esbxml.ESBXMLPacketRSP;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cn.com.agree.ab.common.rpc.packet.config.impl.AbstractXmlConfigParser;
import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.PacketEntity;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.inject.InjectPlugin;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * ESB Xml报文的Xml配置解析器实现
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = AbstractXmlConfigParser.class, multiple = true)
@Singleton
@Named("ibm_xml")
public class XmlConfigParser4EsbPacket extends AbstractXmlConfigParser<PacketEntity<?, ?>> {
	
	@Override
	public String getXsdPath() {
		return "/resources/xsd/ibm_xml.xsd";
	}

	@Override
	protected Packet<?> parseREQ(Document document, PacketConfig packetConfig) {
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
			String headFile = xpath.evaluate("service_header/@inline", requestNode);
			String extsFile = xpath.evaluate("ext_attributes/@inline", requestNode);
			packetConfig.addPacketConfig("service_header", headFile);	// 不能之间放入报文头解析对象，否则报文头配置文件发生变化时，引用的还是老的
			packetConfig.addPacketConfig("ext_attributes", extsFile);
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
		ESBXMLPacketREQ esbxmlPacketREQ = new ESBXMLPacketREQ();
		InjectPlugin.getDefault().injectMembers(esbxmlPacketREQ);	// 给对象注入依赖
		esbxmlPacketREQ.load(packetConfig);
		return esbxmlPacketREQ;
	}

	@Override
	protected Packet<?> parseRSP(Document document, PacketConfig packetConfig) {
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
			String wrapFile = xpath.evaluate("service/@inline",        responseNode);
			String extsFile = xpath.evaluate("ext_attributes/@inline", responseNode);
			packetConfig.addPacketConfig("service",        wrapFile);	// 不能之间放入报文头解析对象，否则报文头配置文件发生变化时，引用的还是老的
			packetConfig.addPacketConfig("ext_attributes", extsFile);
			String wrapAlias = xpath.evaluate("rsp_wrap_class/@alias", responseNode);
			packetConfig.addPacketConfig("rsp_wrap_class", findClassByAlias(wrapAlias, packetConfig.getInnerAliasNameManager()));
		} catch (PacketConfigParseException e) {
			throw e;
		} catch (Exception e) {
			throw new PacketConfigParseException(e);
		}
		// 2. 生成PacketEntity
		ESBXMLPacketRSP esbxmlPacketRSP = new ESBXMLPacketRSP();
		InjectPlugin.getDefault().injectMembers(esbxmlPacketRSP);	// 给对象注入依赖
		esbxmlPacketRSP.load(packetConfig);
		return esbxmlPacketRSP;
	}

	@Override
	protected Packet<?> parseEXP(Document document, PacketConfig packetConfig) {
		return null;
	}


}
