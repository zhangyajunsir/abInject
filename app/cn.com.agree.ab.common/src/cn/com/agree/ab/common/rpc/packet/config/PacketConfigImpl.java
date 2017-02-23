package cn.com.agree.ab.common.rpc.packet.config;

import java.util.HashMap;
import java.util.Map;

import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;

public class PacketConfigImpl implements PacketConfig {
	
	private AliasNameManager innerAliasNameManager;
	
	private Map<String, VariableFieldMetadata> metadatasPool;
	
	private Map<String, Object> otherConfigMap = new HashMap<String, Object>();
	
	private String name;


	public AliasNameManager getInnerAliasNameManager() {
		return innerAliasNameManager;
	}

	public void setInnerAliasNameManager(AliasNameManager innerAliasNameManager) {
		this.innerAliasNameManager = innerAliasNameManager;
	}

	@Override
	public void setMetadatasPool(Map<String, VariableFieldMetadata> metadatasPool) {
		this.metadatasPool = metadatasPool;
	}

	@Override
	public Map<String, VariableFieldMetadata> getMetadatasPool() {
		return metadatasPool;
	}

	@Override
	public void addPacketConfig(String name, Object value) {
		otherConfigMap.put(name, value);
	}

	@Override
	public Object getPacketConfig(String name) {
		return otherConfigMap.get(name);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
