package jsrccb.common.rpc.packet.esbxml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ognl.Ognl;
import ognl.OgnlException;

import jsrccb.common.dm.rsp.EsbRspDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;

import com.google.common.base.Preconditions;

public class ESBXMLPacketHeadRSP extends AbstractPacketEntity<PacketRspDM, EsbRspDM> {
	private static final Logger	logger	= LoggerFactory.getLogger(ESBXMLPacketHeadRSP.class);
	private Map<String, UnitMetadata<Object, byte[]>> mappings = new HashMap<String, UnitMetadata<Object, byte[]>>();
	
	private Class<?> wrapClazz;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		List<UnitMetadata<Object, byte[]>> requestMappings = (List<UnitMetadata<Object, byte[]>>)packetConfig.getPacketConfig("mappings");
		Preconditions.checkState(requestMappings != null, "〖"+name+"〗响应报文映射配置错误");
		for (UnitMetadata<Object, byte[]> metadata : requestMappings) {
			mappings.put(metadata.getName(), metadata);
		}
		wrapClazz = (Class<?>)packetConfig.getPacketConfig("rsp_wrap_class");
		Preconditions.checkState(wrapClazz != null, "通讯码〖"+name+"〗响应rsp_wrap_class配置错误");
	}

	@Override
	public PacketRspDM parse(EsbRspDM input) throws TransformException {
		PacketRspDM packetRspDM     = PacketRspDM.newInstance();
		// mappings赋值
		for (String name : mappings.keySet()) {
			Object value;
			try {
				value = Ognl.getValue(name, input);
			} catch (OgnlException e) {
				throw new TransformException("根据表达式"+name+"取值失败", e);
			}
			if (value == null || !(value instanceof String)) {
				logger.warn("根据[{}]取到对象非String类型", name);
				continue;
			}
			UnitMetadata<Object, byte[]> metadata = mappings.get(name);
			String _encoding_ = metadata.getEncoding() != null && !metadata.getEncoding().equals("") ? metadata.getEncoding() : "UTF-8";
			value = metadata.parse((byte[])MetadataTypeConverter.converter(value, byte[].class, _encoding_));
			if (value == null || !(value instanceof PacketRspDM)) {
				logger.warn("根据[{}]赋值对象非PacketRspDM类型", metadata.getDesc());
				continue;
			}
			ObjectMergeUtil.merge(packetRspDM, (PacketRspDM)value);
		}
		
		return packetRspDM;
	}
	
	public Class<?> getWrapClazz() {
		return wrapClazz;
	}
	
}
