package cn.com.agree.ab.common.rpc.packet.config.impl;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.com.agree.ab.common.rpc.packet.config.PacketConfigImpl;
import cn.com.agree.ab.common.rpc.packet.metadata.ReferenceFieldImpl;
import cn.com.agree.ab.common.rpc.packet.metadata.VariableFieldImpl;
import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.dm.AliasMappingDM;
import cn.com.agree.ab.lib.exception.ConfigException;
import cn.com.agree.ab.lib.rpc.packet.PacketEntity;
import cn.com.agree.ab.lib.rpc.packet.PacketManger;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParser;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.ab.lib.rpc.packet.metadata.FixedFieldMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.ObjectMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.ab.resource.ResourcePlugin;

/**
 * 抽象的Xml配置文件解析器实现
 */
public abstract class AbstractXmlConfigParser<T extends PacketEntity<?,?>> implements PacketConfigParser<Document, Map<PacketType, PacketEntity<?,?>>> {
	private static final Logger logger	= LoggerFactory.getLogger(AbstractXmlConfigParser.class);
	private String packetCompany;
	@Inject
	@Named("xmlConfigAliasMappingParser")
	private PacketConfigParser<Document, List<AliasMappingDM>> xmlConfigAliasMappingParser;
	@Inject
	@Named("xmlConfigMetadatasPoolParser")
	private PacketConfigParser<Document, Map<String, VariableFieldMetadata>> xmlConfigMetadatasPoolParser;
	@Inject
	protected PacketManger packetManger;
	@Inject
	protected ConfigManager configManager;
	@Inject
	@Named("xmlConfigFixedFieldParser")
	protected PacketConfigParser<Node, FixedFieldMetadata> fixedFieldParser;
	@Inject
	@Named("xmlConfigVariableFieldParser")
	protected PacketConfigParser<Node, VariableFieldMetadata> variableFieldParser;
	@Inject
	@Named("xmlConfigRefFieldParser")
	protected PacketConfigParser<Node, ReferenceFieldImpl> referenceFieldParser;
	@SuppressWarnings("rawtypes")
	@Inject
	@Named("xmlConfigObjectParser")
	protected PacketConfigParser<Node, ObjectMetadata> objectParser;

	@Override
	public Map<PacketType, PacketEntity<?,?>> parse(Document document) throws PacketConfigParseException {
		
		AliasNameManager innerAliasNameManager = registerAliasMapping(xmlConfigAliasMappingParser.parse(document));
		Map<String, VariableFieldMetadata> metadatasPool = xmlConfigMetadatasPoolParser.parse(document);
		for (VariableFieldMetadata variableFieldMetadata : metadatasPool.values()) {
			Class<?> clazz;
			try {
				clazz = findClassByAlias(variableFieldMetadata.getClazzName(), innerAliasNameManager);
			} catch (ClassNotFoundException e) {
				logger.error("〖"+variableFieldMetadata.getClazzName()+"〗未找到真实类型或未找到对应类");
				continue;
			}
			variableFieldMetadata.setClazz(clazz);
			if (variableFieldMetadata instanceof VariableFieldImpl) {
				((VariableFieldImpl)variableFieldMetadata).setLenLength(variableFieldLenLength());
			}
		}
		
		PacketConfig packetConfig = new PacketConfigImpl();
		packetConfig.setName(getInterfaceName(document));
		packetConfig.setInnerAliasNameManager(innerAliasNameManager);
		packetConfig.setMetadatasPool(metadatasPool);
		
		Map<PacketType, PacketEntity<?,?>> packets = new HashMap<PacketType, PacketEntity<?,?>>();
		packets.put(PacketType.REQ, parseREQ(document, packetConfig));
		packets.put(PacketType.RSP, parseRSP(document, packetConfig));
		packets.put(PacketType.EXP, parseEXP(document, packetConfig));
		return packets;
	}
	
	public void vildate(URL xmlURL) {
		String xsdPath = configManager.getAbsConfigPath(getXsdPath());
		URL xsdURL = null;
		try {
			xsdURL = ResourcePlugin.getDefault().getResourceURL(xsdPath);
		} catch (FileNotFoundException e) {
			throw new PacketConfigParseException("Schema文件〖"+xsdPath+"〗不存在", e);
		}
		XmlConfigUtil.vildateSchema(xmlURL, xsdURL);
	}
	
	public String getPacketCompany() {
		if (packetCompany == null) {
			packetCompany = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(getXsdPath(), "/"), ".");
		}
		return packetCompany;
	}
	
	private String getInterfaceName(Document document) {
		XPath xpath = XmlConfigUtil.staticXpath();
		Node interfaceNode = null;
		try {
			interfaceNode = (Node) xpath.evaluate("interfaces/interface", document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new PacketConfigParseException("/interfaces/interface节点不存在");
		}
		Node nameNode = interfaceNode.getAttributes().getNamedItem("name");
		if (nameNode == null) {
			throw new PacketConfigParseException("/interfaces/interface节点的属性name不存在");
		}
		return nameNode.getNodeValue();
	}
	
	private AliasNameManager registerAliasMapping(List<AliasMappingDM> aliasMappingDMs) {
		AliasNameManager innerAliasNameManager = AliasNameManager.getSimpleManager();
		for (AliasMappingDM aliasMappingDM : aliasMappingDMs) {
			if ("inner".equals(aliasMappingDM.getDomain())) {
				String realName = innerAliasNameManager.findRealName(aliasMappingDM.getAlias());
				if(realName != null && !realName.equals(aliasMappingDM.getReal())){
					throw new ConfigException("局部别名管理器中已经注册过别名["+aliasMappingDM.getAlias()+"],已注册映射到["+realName+"],将注册映射到["+aliasMappingDM.getReal()+"],因此发生冲突!");
				}
				if(realName != null && realName.equals(aliasMappingDM.getReal())){
					logger.warn("局部别名管理器中已经注册过别名["+aliasMappingDM.getAlias()+"],已注册映射到["+realName+"],发生重复注册,跳过!\n");
				}
				if(realName == null){				
					innerAliasNameManager.register(aliasMappingDM.getAlias(), aliasMappingDM.getReal());
				}
			} else {
				AliasNameManager globalAliasNameManager = packetManger.getGlobalAliasNameManager();
				String realName = globalAliasNameManager.findRealName(aliasMappingDM.getAlias());
				if(realName != null && !realName.equals(aliasMappingDM.getReal())){
					throw new ConfigException("全局别名管理器中已经注册过别名["+aliasMappingDM.getAlias()+"],已注册映射到["+realName+"],将注册映射到["+aliasMappingDM.getReal()+"],因此发生冲突!");
				}
				if(realName != null && realName.equals(aliasMappingDM.getReal())){
					continue;
				}
				if(realName == null){				
					globalAliasNameManager.register(aliasMappingDM.getAlias(), aliasMappingDM.getReal());
				}
			}
		}
		return innerAliasNameManager;
	}
	
	protected Class<?> findClassByAlias(String alias, AliasNameManager innerAliasNameManager) throws ClassNotFoundException {
		String realClazz = null;
		if (realClazz == null && innerAliasNameManager != null) {
			realClazz = innerAliasNameManager.findRealName(alias);
		}
		if (realClazz == null && packetManger.getGlobalAliasNameManager() != null) {
			realClazz = packetManger.getGlobalAliasNameManager().findRealName(alias);
		}
		if(realClazz == null){
			realClazz = alias;
		}
		Class<?> clazz = null;
		if (realClazz.trim().startsWith("[L")) {
			clazz = ResourcePlugin.getDefault().loadClass(realClazz.trim().substring(2));
			Object o = Array.newInstance(clazz, 0);
			clazz = o.getClass();
		} else
			clazz = ResourcePlugin.getDefault().loadClass(realClazz.trim());
		return clazz;
	}
	
	@SuppressWarnings("rawtypes")
	protected List<UnitMetadata<?, ?>> parseUnitMetadata(Node node, PacketConfig packetConfig) { 
		NodeList nodeList = node.getChildNodes();
		List<UnitMetadata<?, ?>> unitMetadataList = new ArrayList<UnitMetadata<?, ?>>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node metadataNode = nodeList.item(i);
			if ("fixed-field"   .equals(metadataNode.getNodeName())) {
				FixedFieldMetadata fixedFieldMetadata = fixedFieldParser.parse(metadataNode);
				try {
					fixedFieldMetadata.setClazz(findClassByAlias(fixedFieldMetadata.getClazzName(), packetConfig.getInnerAliasNameManager()));
				} catch (ClassNotFoundException e) {
					throw new PacketConfigParseException("〖"+fixedFieldMetadata.getClazzName()+"〗未找到真实类型或未找到对应类");
				}
				unitMetadataList.add(fixedFieldMetadata);
			}
			if ("variable-field".equals(metadataNode.getNodeName())) {
				VariableFieldMetadata variableFieldMetadata = variableFieldParser.parse(metadataNode);
				((VariableFieldImpl)variableFieldMetadata).setLenLength(variableFieldLenLength());
				try {
					variableFieldMetadata.setClazz(findClassByAlias(variableFieldMetadata.getClazzName(), packetConfig.getInnerAliasNameManager()));
				} catch (ClassNotFoundException e) {
					throw new PacketConfigParseException("〖"+variableFieldMetadata.getClazzName()+"〗未找到真实类型或未找到对应类");
				}
				unitMetadataList.add(variableFieldMetadata);
			}
			if ("field"         .equals(metadataNode.getNodeName())) {
				ReferenceFieldImpl    referenceFieldImpl = referenceFieldParser.parse(metadataNode);
				VariableFieldMetadata realFieldMetadata  = packetConfig.getMetadatasPool().get(referenceFieldImpl.getAlias());
				if (realFieldMetadata == null)
					throw new PacketConfigParseException("元信息池没有〖"+referenceFieldImpl.getAlias()+"〗栏位");
				referenceFieldImpl.setRealFieldMetadata(realFieldMetadata);
				unitMetadataList.add(referenceFieldImpl);
			}
			if ("object"        .equals(metadataNode.getNodeName())) {
				ObjectMetadata objectMetadata = objectParser.parse(metadataNode);
				dealObjectMetadata(objectMetadata, packetConfig);
				unitMetadataList.add(objectMetadata);
			}
		}
		return unitMetadataList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void dealObjectMetadata(ObjectMetadata objectMetadata, PacketConfig packetConfig ) {
		try {
			objectMetadata.setWrapClazz(findClassByAlias(objectMetadata.getWrapClazzName(), packetConfig.getInnerAliasNameManager()));
		} catch (ClassNotFoundException e) {
			throw new PacketConfigParseException("〖"+objectMetadata.getWrapClazzName()+"〗未找到真实类型或未找到对应类");
		}
		for (int i=0; i<objectMetadata.getUnitMetadatas().size(); i++) {
			UnitMetadata unitMetadata = (UnitMetadata)objectMetadata.getUnitMetadatas().get(i);
			if (unitMetadata instanceof ReferenceFieldImpl) {
				VariableFieldMetadata realFieldMetadata  = packetConfig.getMetadatasPool().get(unitMetadata.getAlias());
				if (realFieldMetadata == null)
					throw new PacketConfigParseException("元信息池没有〖"+unitMetadata.getAlias()+"〗栏位");
				((ReferenceFieldImpl)unitMetadata).setRealFieldMetadata(realFieldMetadata);
			} 
			if (unitMetadata instanceof VariableFieldMetadata && !(unitMetadata instanceof ReferenceFieldImpl)) {
				VariableFieldMetadata variableFieldMetadata = (VariableFieldMetadata)unitMetadata;
				try {
					variableFieldMetadata.setClazz(findClassByAlias(variableFieldMetadata.getClazzName(), packetConfig.getInnerAliasNameManager()));
				} catch (ClassNotFoundException e) {
					throw new PacketConfigParseException("〖"+variableFieldMetadata.getClazzName()+"〗未找到真实类型或未找到对应类");
				}
				if (variableFieldMetadata instanceof VariableFieldImpl) {
					((VariableFieldImpl)variableFieldMetadata).setLenLength(variableFieldLenLength());
				}
			} 
			if (unitMetadata instanceof ObjectMetadata) {
				dealObjectMetadata((ObjectMetadata)unitMetadata, packetConfig);
			}
		}
	}
	
	/** 变长栏位代表长度的大小 */
	protected int variableFieldLenLength() {
		return 0;
	}
	protected abstract String getXsdPath();
	protected abstract T parseREQ(Document document, PacketConfig packetConfig);
	protected abstract T parseRSP(Document document, PacketConfig packetConfig);
	protected abstract T parseEXP(Document document, PacketConfig packetConfig);

}
