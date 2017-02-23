package jsrccb.common.rpc.packet.esbxml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.utils.StringFreemarker;

import com.google.common.base.Preconditions;

public class ESBXMLPacketHeadREQ extends AbstractPacketEntity<PacketReqDM, String> {
	private Map<String, UnitMetadata<Object, byte[]>> mappings = new HashMap<String, UnitMetadata<Object, byte[]>>();
	
	private String template;
	@Inject
	private StringFreemarker stringFreemarker;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		List<UnitMetadata<Object, byte[]>> requestMappings = (List<UnitMetadata<Object, byte[]>>)packetConfig.getPacketConfig("mappings");
		Preconditions.checkState(requestMappings != null, "〖"+name+"〗请求报文映射配置错误");
		for (UnitMetadata<Object, byte[]> metadata : requestMappings) {
			mappings.put(metadata.getName(), metadata);
		}
		template = (String)packetConfig.getPacketConfig("template");
		Preconditions.checkState(template != null, "〖"+name+"〗请求req_template配置错误");
	}

	@Override
	public String format(PacketReqDM input) throws TransformException {
		Map<String, String> _mappings_ = new HashMap<String, String>();
		for (String key : mappings.keySet()) {
			UnitMetadata<Object, byte[]> metadata = mappings.get(key);
			String _encoding_ = metadata.getEncoding() != null && !metadata.getEncoding().equals("") ? metadata.getEncoding() : "UTF-8";
			String value = (String)MetadataTypeConverter.converter(metadata.format(input), String.class, _encoding_);
			_mappings_.put(key, value);
		}
		input.getInnerArea().put(name+"_mappings", _mappings_);
		
		return stringFreemarker.process(name+"_req_template", template, input.toFieldMapping());
	}


	
}
