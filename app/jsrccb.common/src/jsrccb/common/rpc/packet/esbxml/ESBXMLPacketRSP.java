package jsrccb.common.rpc.packet.esbxml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsrccb.common.dm.rsp.EsbFormDM;
import jsrccb.common.dm.rsp.EsbRspDM;
import ognl.Ognl;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.rpc.packet.metadata.ByteBeanObjectMetadata;
import cn.com.agree.ab.common.rpc.packet.metadata.ListFormObjectMetadata;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.lib.utils.XomUtil;

import com.google.common.base.Preconditions;

public class ESBXMLPacketRSP extends AbstractPacketEntity<PacketRspDM, byte[]> implements Packet<PacketRspDM> {
	private static final Logger	logger	= LoggerFactory.getLogger(ESBXMLPacketRSP.class);
	private String wrapFile;
	
	private String extsFile;
	
	private Map<String, UnitMetadata<Object, Object>> mappings = new HashMap<String, UnitMetadata<Object, Object>>();
	
	private Class<?> wrapClazz;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		List<UnitMetadata<Object, Object>> requestMappings = (List<UnitMetadata<Object, Object>>)packetConfig.getPacketConfig("mappings");
		Preconditions.checkState(requestMappings != null, "通讯码〖"+name+"〗交易响应报文映射配置错误");
		for (UnitMetadata<Object, Object> metadata : requestMappings) {
			mappings.put(metadata.getName(), metadata);
		}
		wrapFile = (String)packetConfig.getPacketConfig("service");
		Preconditions.checkState(wrapFile != null, "通讯码〖"+name+"〗交易响应service配置错误");
		extsFile = (String)packetConfig.getPacketConfig("ext_attributes");
		Preconditions.checkState(extsFile != null, "通讯码〖"+name+"〗交易响应ext_attributes配置错误");
		wrapClazz = (Class<?>)packetConfig.getPacketConfig("rsp_wrap_class");
		Preconditions.checkState(wrapClazz != null, "通讯码〖"+name+"〗交易响应rsp_wrap_class配置错误");
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PacketRspDM parse(byte[] input) throws TransformException {
		ESBXMLPacketHeadRSP rspWrap = (ESBXMLPacketHeadRSP)packetManger.getPacketEntity(wrapFile, PacketType.RSP);
		Class<?> rspWrapClazz       = rspWrap.getWrapClazz();
		ESBXMLPacketHeadRSP rspExts = (ESBXMLPacketHeadRSP)packetManger.getPacketEntity(extsFile, PacketType.RSP);
		Class<?> extAttrWrapClass   = rspExts.getWrapClazz();
		String   xmlStr             = (String)MetadataTypeConverter.converter(input, String.class, "UTF-8");
		logger.info(xmlStr);
		
		EsbRspDM esbRspDM           = null;
		Map<String, Class<?>> aliasMap = new HashMap<String, Class<?>>();
		aliasMap.put("ext_attributes", extAttrWrapClass);
		aliasMap.put("response",       wrapClazz);
		esbRspDM = (EsbRspDM)XomUtil.converyToXstreamBean(xmlStr, new Class<?>[]{rspWrapClazz, extAttrWrapClass, wrapClazz}, aliasMap);
		if (esbRspDM == null)
			throw new TransformException("反序列化XML失败");
		
		PacketRspDM packetRspDM1    = rspWrap.parse(esbRspDM);
		PacketRspDM packetRspDM2    = rspExts.parse(esbRspDM);
		PacketRspDM packetRspDM     = PacketRspDM.newInstance();
		// 合并
		ObjectMergeUtil.merge(packetRspDM, packetRspDM1, packetRspDM2);
		// 
		packetRspDM.getTempArea().put(name+"_esbRspDM", esbRspDM);
		// mappings赋值
		Object response = null;
		try {
			response = Ognl.getValue("serviceBody.response", esbRspDM);
		} catch (OgnlException e) {
			throw new TransformException("根据表达式serviceBody.response取值失败", e);
		}
		if (response != null)
			for (String name : mappings.keySet()) {
				Object value;
				try {
					value = Ognl.getValue(name, response);
				} catch (OgnlException e) {
					throw new TransformException("根据表达式"+name+"取值失败", e);
				}
				if (value == null) {
					logger.warn("根据[{}]取到对象为NULL", name);
					continue;
				}
				UnitMetadata metadata = mappings.get(name);
				if ((metadata instanceof VariableFieldMetadata || metadata instanceof ByteBeanObjectMetadata) && value instanceof String) {
					String _encoding_ = metadata.getEncoding() != null && !metadata.getEncoding().equals("") ? metadata.getEncoding() : "UTF-8";
					value = metadata.parse((byte[])MetadataTypeConverter.converter(value, byte[].class, _encoding_));
				}
				if (metadata instanceof ListFormObjectMetadata && value instanceof List) {
					value = metadata.parse((List)value);
				}
				if (value == null || !(value instanceof PacketRspDM)) {
					logger.warn("根据[{}]赋值对象非PacketRspDM类型", metadata.getDesc());
					continue;
				}
				ObjectMergeUtil.merge(packetRspDM, (PacketRspDM)value);
			}
		// 每种报文都需要将响应体按此格式放入临时区
		if (response instanceof EsbFormDM) {
			if (((EsbFormDM)response).getFormDMs() != null) {
				Map<String, String> formMap = new HashMap<String, String>();
				for (EsbFormDM.FormDM formDM : ((EsbFormDM)response).getFormDMs()) {
					formMap.put(formDM.getId(), formDM.getContent());
				}
				response = formMap;
			}
		}
		packetRspDM.getTempArea().put(name+"_RSP", response);
		logger.info("解析总报文返回对象：{}", JsonUtil.obj2json(packetRspDM));
		return packetRspDM;
	}



}
