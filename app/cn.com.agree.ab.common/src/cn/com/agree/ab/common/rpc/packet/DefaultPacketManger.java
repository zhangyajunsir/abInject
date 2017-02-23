package cn.com.agree.ab.common.rpc.packet;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.w3c.dom.Document;

import cn.com.agree.ab.common.rpc.packet.config.impl.AbstractXmlConfigParser;
import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.config.ConfigLoad;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.ConfigException;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.PacketEntity;
import cn.com.agree.ab.lib.rpc.packet.PacketManger;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfigParseException;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = PacketManger.class)
@Singleton
public class DefaultPacketManger implements PacketManger, ConfigLoad<Map<PacketType, PacketEntity<?,?>>> {
	
	private static final String pathPrefix = "/resources/interfaces/";
	private static final String pathSuffix = "_interface.xml";
	
	private Map<String, String> packetPath = new ConcurrentHashMap<String, String>();
	@Inject
	private ConfigManager configManager;
	@Inject
	private AliasNameManager globalAliasNameManager;
	@Inject
	private Set<AbstractXmlConfigParser<PacketEntity<?,?>>> packetConfigParsers;

	@Override
	public Packet<?> getPacket(String name, PacketType packetType) {
		String path = packetPath.get(name);
		if (path == null) {
			synchronized (packetPath) {
				path = packetPath.get(name);
				if (path == null) {
					path = configManager.getAbsConfigPath(pathPrefix+name+pathSuffix);
					if (path == null) {
						throw new ConfigException("无后台交易〖"+name+"〗的报文配置。");
					}
					packetPath.put(name, path);
				}
			}
		}
		Map<PacketType, PacketEntity<?,?>> packets = configManager.getConfigObject(path, this);
		PacketEntity<?,?> packet = packets.get(packetType);
		if (packet == null) {
			throw new ConfigException("无后台交易〖"+name+"〗的"+packetType+"报文配置。");
		}
		if (packet instanceof Packet) {
			return (Packet<?>)packet;
		}
		throw new ConfigException("无后台交易〖"+name+"〗的"+packetType+"报文配置。");
	}
	
	@Override
	public PacketEntity<?, ?> getPacketEntity(String inline, PacketType packetType) {
		String path = packetPath.get(inline);
		if (path == null) {
			synchronized (packetPath) {
				path = packetPath.get(inline);
				if (path == null) {
					path = configManager.getAbsConfigPath(pathPrefix+inline);
					if (path == null) {
						throw new ConfigException("无〖"+inline+"〗的报文配置。");
					}
					packetPath.put(inline, path);
				}
			}
		}
		Map<PacketType, PacketEntity<?,?>> packets = configManager.getConfigObject(path, this);
		PacketEntity<?,?> packet = packets.get(packetType);
		if (packet == null) {
			throw new ConfigException("无内部引用〖"+inline+"〗的"+packetType+"报文配置。");
		}
		return packet;
	}

	@Override
	public Collection<String> getPacketNames() {
		return packetPath.keySet();
	}
	
	@Override
	public Map<PacketType, PacketEntity<?,?>> load(URL url, InputStream is) throws Exception {
		Document document = XmlConfigUtil.initDoc(is);
		String packetCompany = XmlConfigUtil.getPacketCompany(document);
		if (packetCompany == null)
			throw new PacketConfigParseException("解析报文配置里的Schema失败");
		
		AbstractXmlConfigParser<PacketEntity<?,?>> packetConfigParser = null;
		for (AbstractXmlConfigParser<PacketEntity<?,?>> _packetConfigParser_ : packetConfigParsers) {
			if (packetCompany.equals(_packetConfigParser_.getPacketCompany())) {
				packetConfigParser = _packetConfigParser_;
			}
		}
		if (packetConfigParser == null)
			throw new PacketConfigParseException("未找到〖"+packetCompany+"〗报文配置的解析器");
		packetConfigParser.vildate(url);
		return packetConfigParser.parse(document);
	}

	@Override
	public AliasNameManager getGlobalAliasNameManager() {
		if (globalAliasNameManager.aliasName().size() > 0)
			return globalAliasNameManager;
		synchronized (globalAliasNameManager) {
			if (globalAliasNameManager.aliasName().size() > 0)
				return globalAliasNameManager;
			globalAliasNameManager.register("String",     "java.lang.String");
			globalAliasNameManager.register("Date",       "java.util.Date");
			globalAliasNameManager.register("BigDecimal", "java.math.BigDecimal");
			globalAliasNameManager.register("Integer",    "java.lang.Integer");
			globalAliasNameManager.register("Byte",       "java.lang.Byte");
			globalAliasNameManager.register("ByteBuffer", "java.nio.ByteBuffer");
			globalAliasNameManager.register("StringArray","[Ljava.lang.String");
			globalAliasNameManager.register("ObjectArray","[Ljava.lang.Object");
			globalAliasNameManager.register("List",       "java.util.ArrayList");
			globalAliasNameManager.register("Map",        "java.util.HashMap");
			globalAliasNameManager.register("Money",      "cn.com.agree.ab.common.dm.MoneyDM");
			return globalAliasNameManager;
		}
	}

	

}
