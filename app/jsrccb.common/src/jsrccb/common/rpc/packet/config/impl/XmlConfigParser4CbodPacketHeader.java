package jsrccb.common.rpc.packet.config.impl;


import java.nio.ByteBuffer;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import jsrccb.common.rpc.packet.cbod.CBODPacketHeadREQ;
import jsrccb.common.rpc.packet.cbod.CBODPacketHeadRSP;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cn.com.agree.ab.common.rpc.packet.config.impl.AbstractXmlConfigParser;
import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.lib.rpc.packet.PacketEntity;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractXmlConfigParser.class, multiple = true)
@Singleton
@Named("ibm_cbod_header")
public class XmlConfigParser4CbodPacketHeader extends AbstractXmlConfigParser<PacketEntity<?, ?>> {

	@Override
	public String getXsdPath() {
		return "/resources/xsd/ibm_cbod_header.xsd";
	}
	
	@Override
	public int variableFieldLenLength() {
		return 2;
	}

	@Override
	protected PacketEntity<?, byte[]> parseREQ(Document document, PacketConfig packetConfig) {
		// 1. 分析request配置信息添加到PacketConfig
		XPath xpath = XmlConfigUtil.staticXpath();
		try {
			XPathExpression requestExpr = xpath.compile("interfaces/interface/request");
			Node requestNode = (Node)requestExpr.evaluate(document, XPathConstants.NODE);
			if (requestNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/request节点不存在");
			}
			packetConfig.addPacketConfig("request", parseUnitMetadata(requestNode, packetConfig));
			
		} catch (PacketConfigParseException e) {
			throw e;
		} catch (Exception e) {
			throw new PacketConfigParseException(e);
		}
		// 2. 生成PacketEntity
		CBODPacketHeadREQ cbodPacketHeadREQ = new CBODPacketHeadREQ();
		cbodPacketHeadREQ.load(packetConfig);
		return cbodPacketHeadREQ;
	}

	@Override
	protected PacketEntity<?, byte[]> parseRSP(Document document, PacketConfig packetConfig) {
		// 1. 分析response配置信息添加到PacketConfig
		XPath xpath = XmlConfigUtil.staticXpath();
		try {
			XPathExpression requestExpr = xpath.compile("interfaces/interface/response");
			Node requestNode = (Node)requestExpr.evaluate(document, XPathConstants.NODE);
			if (requestNode == null) {
				throw new PacketConfigParseException("/interfaces/interface/response节点不存在");
			}
			packetConfig.addPacketConfig("response", parseUnitMetadata(requestNode, packetConfig));
			
		} catch (PacketConfigParseException e) {
			throw e;
		} catch (Exception e) {
			throw new PacketConfigParseException(e);
		}
		// 2. 生成PacketEntity
		CBODPacketHeadRSP cbodPacketHeadRSP = new CBODPacketHeadRSP();
		cbodPacketHeadRSP.load(packetConfig);
		return cbodPacketHeadRSP;
	}

	@Override
	protected PacketEntity<?, ByteBuffer> parseEXP(Document document, PacketConfig packetConfig) {
		return null;
	}
	
	

}
