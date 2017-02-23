package cn.com.agree.ab.lib.rpc.packet.config;

import java.util.Map;

import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;


public interface PacketConfig {
	
	public void setName(String name);
	
	public String getName();

	public AliasNameManager getInnerAliasNameManager();

	public void setInnerAliasNameManager(AliasNameManager innerAliasNameManager);
	
	public void setMetadatasPool(Map<String, VariableFieldMetadata> metadatasPool);
	
	public Map<String, VariableFieldMetadata> getMetadatasPool();
	
	public void addPacketConfig(String name, Object value);
	
	public Object getPacketConfig(String name);
}
