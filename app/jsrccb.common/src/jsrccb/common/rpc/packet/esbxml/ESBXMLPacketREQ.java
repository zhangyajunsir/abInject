package jsrccb.common.rpc.packet.esbxml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.lib.utils.StringFreemarker;

import com.google.common.base.Preconditions;

public class ESBXMLPacketREQ extends AbstractPacketEntity<PacketReqDM, byte[]> implements Packet<PacketReqDM>{
	private static final Logger	logger	= LoggerFactory.getLogger(ESBXMLPacketREQ.class);
	private String headFile;
	
	private String extsFile;
	
	private Map<String, UnitMetadata<Object, byte[]>> mappings = new HashMap<String, UnitMetadata<Object, byte[]>>();
	
	private String template;
	@Inject
	private StringFreemarker stringFreemarker;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		List<UnitMetadata<Object, byte[]>> requestMappings = (List<UnitMetadata<Object, byte[]>>)packetConfig.getPacketConfig("mappings");
		Preconditions.checkState(requestMappings != null, "通讯码〖"+name+"〗交易请求报文映射配置错误");
		for (UnitMetadata<Object, byte[]> metadata : requestMappings) {
			mappings.put(metadata.getName(), metadata);
		}
		headFile = (String)packetConfig.getPacketConfig("service_header");
		Preconditions.checkState(headFile != null, "通讯码〖"+name+"〗交易请求service_header配置错误");
		extsFile = (String)packetConfig.getPacketConfig("ext_attributes");
		Preconditions.checkState(extsFile != null, "通讯码〖"+name+"〗交易请求ext_attributes配置错误");
		template = (String)packetConfig.getPacketConfig("template");
		Preconditions.checkState(template != null, "通讯码〖"+name+"〗交易请求req_template配置错误");
	}

	@Override
	public byte[] format(PacketReqDM input) throws TransformException {
		logger.info("请求上送取值对象：{}", JsonUtil.obj2json(input));
		ESBXMLPacketHeadREQ requestHead = (ESBXMLPacketHeadREQ)packetManger.getPacketEntity(headFile, PacketType.REQ);
		ESBXMLPacketHeadREQ requestExts = (ESBXMLPacketHeadREQ)packetManger.getPacketEntity(extsFile, PacketType.REQ);
		input.getInnerArea().put("service_header", requestHead.format(input).trim());
		input.getInnerArea().put("ext_attributes", requestExts.format(input).trim());
		
		Map<String, String> _mappings_ = new HashMap<String, String>();
		for (String key : mappings.keySet()) {
			UnitMetadata<Object, byte[]> metadata = mappings.get(key);
			String _encoding_ = metadata.getEncoding() != null && !metadata.getEncoding().equals("") ? metadata.getEncoding() : "UTF-8";
			String value = (String)MetadataTypeConverter.converter(metadata.format(input), String.class, _encoding_);
			_mappings_.put(key, value);
		}
		input.getInnerArea().put("mappings", _mappings_);
		
		String reqData = stringFreemarker.process(name+"_req_template", template, input.toFieldMapping()).trim();
		logger.info(reqData);
		
		return (byte[])MetadataTypeConverter.converter(reqData, byte[].class, "UTF-8");
	}

}
