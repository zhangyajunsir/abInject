package jsrccb.common.rpc.packet.cbod;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.rpc.packet.metadata.AbstractObjectMetadata;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.utils.ArraysUtil;
import cn.com.agree.ab.lib.utils.Hex;
import cn.com.agree.ab.lib.utils.JsonUtil;

import com.google.common.base.Preconditions;

public class CBODPacketRSP extends AbstractPacketEntity<PacketRspDM, byte[]> implements Packet<PacketRspDM> {
	private static final Logger	logger	= LoggerFactory.getLogger(CBODPacketRSP.class);
	private String headFile;
	
	private String optlFile;
	
	private List<UnitMetadata<Object, byte[]>> responseTrans;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		headFile = (String)packetConfig.getPacketConfig("head");
		Preconditions.checkState(headFile != null, "通讯码〖"+name+"〗固定响应报文头配置错误");
		optlFile = (String)packetConfig.getPacketConfig("optl");
		Preconditions.checkState(optlFile != null, "通讯码〖"+name+"〗可选响应报文头配置错误");
		responseTrans = (List<UnitMetadata<Object, byte[]>>)packetConfig.getPacketConfig("tran");
		Preconditions.checkState(responseTrans != null, "通讯码〖"+name+"〗交易响应报文体配置错误");
	}
	

	@Override
	public PacketRspDM parse(byte[] input) throws TransformException {
		CBODPacketHeadRSP rspHead = (CBODPacketHeadRSP)packetManger.getPacketEntity(headFile, PacketType.RSP);
		CBODPacketHeadRSP rspOptl = (CBODPacketHeadRSP)packetManger.getPacketEntity(optlFile, PacketType.RSP);
		PacketRspDM packetRspDM  = PacketRspDM.newInstance();
		// 解析固定头
		int    headLength       = rspHead.getLength();
		byte[] headByteArray    = new byte[headLength];
		System.arraycopy(input, 0, headByteArray, 0, headLength);
		PacketRspDM headRspDM   = rspHead.parse(headByteArray);
		logger.debug("解析固定头返回对象：{}", JsonUtil.obj2json(headRspDM));
		int    allLength        = (Integer)   headRspDM.getInnerArea().get("ALL_LEN");
		logger.info("OPM_LL：{}", allLength);
		// 解析可选头
		ByteBuffer bitMapBuffer = (ByteBuffer)headRspDM.getInnerArea().get("OPTIONAL_MAP");
		logger.info("OPM_OPTIONAL_MAP：{}", Hex.toHexString(bitMapBuffer.array()));
		int    optlLength       = rspOptl.getLength(ArraysUtil.ltruncate(bitMapBuffer.array(), 4));
		byte[] optlByteArray    = new byte[optlLength+4];	// 加上4字节的bitMap，即bitMapBuffer的后4位
		System.arraycopy(input, headLength-4, optlByteArray, 0, optlLength+4);
		PacketRspDM optlRspDM   = rspOptl.parse(optlByteArray);
		logger.debug("解析可选头返回对象：{}", JsonUtil.obj2json(optlRspDM));
		// 合并固定头、可选头信息
		ObjectMergeUtil.merge(packetRspDM, headRspDM, optlRspDM);
		//解析FormData
		ByteBuffer buffer = ByteBuffer.wrap(input, headLength+optlLength, allLength-headLength-optlLength);
		Map<String, Object> formDataMap = new HashMap<String, Object>();
		while (true) {
			buffer.mark(); 	// 当前偏移做个标记
			if (buffer.position() > buffer.limit() || buffer.get() == 0x3E) {
				break;
			}
			buffer.reset(); // 恢复到刚才的偏移
			byte[] byteA = new byte[2];
			buffer.get(byteA);
			int lenLength  = (Integer)MetadataTypeConverter.converter(byteA, Integer.class);
			if (buffer.remaining() < lenLength-1) {
				// 剩余大小小于(Form总长度减去代表长度的2位再加上0x3E的1位)
				break;
			}
			byte[] byteB = new byte[8];
			buffer.get(byteB);
			String formID  = (String) MetadataTypeConverter.converter(byteB, String.class, "EBCDIC");
			byte[] byteC = new byte[lenLength-10];
			buffer.get(byteC);
			String formData= (String) MetadataTypeConverter.converter(byteC, String.class, "EBCDIC", null, null, "false");
			logger.info("解析FORMLEN：{} FORMID：{} FORMDATA：{}", lenLength, formID, formData);
			formDataMap.put(formID, formData);
			// 根据配置将指定的formID进行赋值操作
			for (UnitMetadata<Object, byte[]> responseTran : responseTrans) {
				if (responseTran.getName().equals(formID)) {
					Object form = responseTran.parse(byteC);
					if (form instanceof PacketRspDM) {
						ObjectMergeUtil.merge(packetRspDM, (PacketRspDM)form);
						try {
							form = Ognl.getValue(((AbstractObjectMetadata)responseTran).getValueRex(), form);
							formDataMap.put(formID, form);
						} catch (OgnlException e) {
						}
					} else
						formDataMap.put(formID, form);
					break;
				}
			}
		}
		
		if (buffer.position() != allLength)
			throw new TransformException("核心下送报文解析失败");
		// 每种报文都需要将响应体按此格式放入临时区
		packetRspDM.getTempArea().put(name+"_RSP", formDataMap);
		logger.info("解析总报文返回对象：{}", JsonUtil.obj2json(packetRspDM));
		return packetRspDM;
	}



}
