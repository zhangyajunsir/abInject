package jsrccb.common.rpc.packet.config.impl;


import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import jsrccb.common.rpc.packet.cbod.CBODPacketREQ;
import jsrccb.common.rpc.packet.cbod.CBODPacketRSP;

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

@AutoBindMapper(baseClass = AbstractXmlConfigParser.class, multiple = true)
@Singleton
@Named("ibm_cbod")
public class XmlConfigParser4CbodPacket extends AbstractXmlConfigParser<PacketEntity<?, ?>> {
	
	@Override
	public String getXsdPath() {
		return "/resources/xsd/ibm_cbod.xsd";
	}
	
	@Override
	public int variableFieldLenLength() {
		return 2;
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
			String headFile = xpath.evaluate("head/@inline", requestNode);
			String optlFile = xpath.evaluate("optl/@inline", requestNode);
			packetConfig.addPacketConfig("head", headFile);	// 不能之间放入报文头解析对象，否则报文头配置文件发生变化时，引用的还是老的
			packetConfig.addPacketConfig("optl", optlFile);
			XPathExpression tranExpr   = xpath.compile("tran");
			Node tranNode = (Node)tranExpr.evaluate(requestNode, XPathConstants.NODE);
			if (tranNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/request/tran节点不存在");
			}
			packetConfig.addPacketConfig("tran", parseUnitMetadata(tranNode, packetConfig));
		} catch (PacketConfigParseException e) {
			throw e;
		} catch (Exception e) {
			throw new PacketConfigParseException(e);
		}
		// 2. 生成PacketEntity
		CBODPacketREQ cbodPacketREQ = new CBODPacketREQ();
		InjectPlugin.getDefault().injectMembers(cbodPacketREQ);	// 给对象注入依赖
		cbodPacketREQ.load(packetConfig);
		return cbodPacketREQ;
	}

	@Override
	protected Packet<?> parseRSP(Document document, PacketConfig packetConfig) {
		// 1. 分析response配置信息添加到PacketConfig
		XPath xpath = XmlConfigUtil.staticXpath();
		try {
			XPathExpression requestExpr = xpath.compile("interfaces/interface/response");
			Node requestNode = (Node)requestExpr.evaluate(document, XPathConstants.NODE);
			if (requestNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/response节点不存在");
			}
			String headFile = xpath.evaluate("head/@inline", requestNode);
			String optlFile = xpath.evaluate("optl/@inline", requestNode);
			packetConfig.addPacketConfig("head", headFile);	// 不能之间放入报文头解析对象，否则报文头配置文件发生变化时，引用的还是老的
			packetConfig.addPacketConfig("optl", optlFile);
			XPathExpression tranExpr   = xpath.compile("tran");
			Node tranNode = (Node)tranExpr.evaluate(requestNode, XPathConstants.NODE);
			if (tranNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/response/tran节点不存在");
			}
			packetConfig.addPacketConfig("tran", parseUnitMetadata(tranNode, packetConfig));
		} catch (PacketConfigParseException e) {
			throw e;
		} catch (Exception e) {
			throw new PacketConfigParseException(e);
		}
		// 2. 生成PacketEntity
		CBODPacketRSP cbodPacketRSP = new CBODPacketRSP();
		InjectPlugin.getDefault().injectMembers(cbodPacketRSP);	// 给对象注入依赖
		cbodPacketRSP.load(packetConfig);
		return cbodPacketRSP;
	}

	@Override
	protected Packet<?> parseEXP(Document document, PacketConfig packetConfig) {
		return null;
	}

}
