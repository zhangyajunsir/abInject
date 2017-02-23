package cn.com.agree.ab.common.rpc.packet;

import java.util.Map;

import javax.inject.Inject;

import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.PacketEntity;
import cn.com.agree.ab.lib.rpc.packet.PacketManger;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;

public abstract class AbstractPacketEntity<T, K> implements PacketEntity<T, K> {
	protected AliasNameManager innerAliasNameManager;
	/** 元信息池 */
	protected Map<String, VariableFieldMetadata> metadatasPool;
	/** 标识名称 */
	protected String name;
	@Inject
	protected PacketManger packetManger;

	public void load(PacketConfig packetConfig) {
		innerAliasNameManager = packetConfig.getInnerAliasNameManager();
		metadatasPool         = packetConfig.getMetadatasPool();
		name                  = packetConfig.getName();
		loadInterface(packetConfig);
	}
	
	public AliasNameManager getInnerAliasNameManager() {
		return innerAliasNameManager;
	}
	
	public Map<String, VariableFieldMetadata> getMetadatasPool() {
		return  metadatasPool;
	}
	
	/**
	 * 获取标识名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 载入interface配置
	 * @param packetConfig
	 */
	protected abstract void loadInterface(PacketConfig packetConfig);
	
	
	/**
	 * 默认空实现，由具体子类覆盖实现
	 */
	@Override
	public K format(T input) throws TransformException {
		return null;
	}

	/**
	 * 默认空实现，由具体子类覆盖实现
	 */
	@Override
	public T parse(K input) throws TransformException {
		return null;
	}
}
