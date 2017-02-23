package cn.com.agree.ab.lib.rpc.packet;

import java.util.Map;

import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.rpc.packet.loader.Loader;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;

public interface PacketEntity<T,K> extends Loader, Formatter<T,K>, Parser<K,T> {

	public AliasNameManager getInnerAliasNameManager();
	
	public Map<String, VariableFieldMetadata> getMetadatasPool();
	
	public String getName();
	
}
