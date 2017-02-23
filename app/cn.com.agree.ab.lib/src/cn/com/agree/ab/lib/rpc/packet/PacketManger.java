package cn.com.agree.ab.lib.rpc.packet;

import java.util.Collection;

import cn.com.agree.ab.lib.alias.AliasNameManager;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;

public interface PacketManger {

	public Packet<?> getPacket(String name, PacketType packetType);
	
	public PacketEntity<?, ?> getPacketEntity(String inline, PacketType packetType);
	
	public Collection<String> getPacketNames();
	
	/**
	 * 获取全局别名管理器
	 * @return
	 */
	AliasNameManager getGlobalAliasNameManager();
}
