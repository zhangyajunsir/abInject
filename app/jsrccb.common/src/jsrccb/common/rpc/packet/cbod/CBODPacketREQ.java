package jsrccb.common.rpc.packet.cbod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.Packet;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.loader.PacketType;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.ext.cbod.impl.utils.CBODEncrypt;

public class CBODPacketREQ extends AbstractPacketEntity<PacketReqDM, byte[]> implements Packet<PacketReqDM>{
	private static final Logger	logger	= LoggerFactory.getLogger(CBODPacketREQ.class);
	private String headFile;
	
	private String optlFile;
	
	private List<UnitMetadata<Object, byte[]>> requestTran;
	
	private final static byte[] EOM = new byte[] { 0x3E };
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		headFile = (String)packetConfig.getPacketConfig("head");
		Preconditions.checkState(headFile != null, "通讯码〖"+name+"〗固定请求报文头配置错误");
		optlFile = (String)packetConfig.getPacketConfig("optl");
		Preconditions.checkState(optlFile != null, "通讯码〖"+name+"〗可选请求报文头配置错误");
		requestTran = (List<UnitMetadata<Object, byte[]>>)packetConfig.getPacketConfig("tran");
		Preconditions.checkState(requestTran != null, "通讯码〖"+name+"〗交易请求报文体配置错误");
	}

	@SuppressWarnings({ "unused"})
	@Override
	public byte[] format(PacketReqDM input) throws TransformException {
		logger.info("请求上送取值对象：{}", JsonUtil.obj2json(input));
		CBODPacketHeadREQ requestHead = (CBODPacketHeadREQ)packetManger.getPacketEntity(headFile, PacketType.REQ);
		CBODPacketHeadREQ requestOptl = (CBODPacketHeadREQ)packetManger.getPacketEntity(optlFile, PacketType.REQ);
		byte[] headByteArray = requestHead.format(input);
		byte[] optlByteArray = requestOptl.format(input);
		ByteBuffer headBuffer = ByteBuffer.wrap(headByteArray);
		ByteBuffer optlBuffer = ByteBuffer.wrap(optlByteArray, 4, optlByteArray.length-4); // byte[],position,limit
		optlBuffer.mark();	// 当前偏移做标记，作为后面处理的起始位置
		// 前8位为BIT_MAP
		byte[] bitMap = new byte[4]; 
		System.arraycopy(optlByteArray, 0, bitMap, 0, 4);
		// 复制BitMap到固定头指定位置
		{
			// INM_OPTIONAL_MAP偏移位置
			int INM_OPTIONAL_MAP_POS = requestHead.getUnitMetadataPosition("INM_OPTIONAL_MAP");
			// 将BIT_MAP复制到INM_OPTIONAL_MAP偏移位置
			headBuffer.position(INM_OPTIONAL_MAP_POS);	// 设置新的偏移
			headBuffer.put(new byte[]{0x00,0x00,0x00,0x00}).put(bitMap);
		}
		// 生成TRANS_DATA，并算账号和密码的偏移
		ByteArrayOutputStream transData = new ByteArrayOutputStream();
		for (int i = 0; i < requestTran.size(); i++) {
			UnitMetadata<Object, byte[]> unitMetadata = requestTran.get(i);
			byte[] field  = unitMetadata.format(input);	// 变长字段前面已经加了长度
			// 写入数据
			try {
				transData.write(field);
			} catch (IOException e) {
				throw new TransformException(e);
			}
		}
		// 复制账密偏移位置到可选头指定位置
		{
			// INM_SEC_CTL偏移位置，核心不校验了
			int INM_SEC_CTL_POS = requestOptl.getUnitMetadataPosition("INM_SEC_CTL", bitMap);
			// END
			
		}
		// 复制报文总长度到固定头指定位置
		{
			// INM_LL偏移位置
			int INM_LL_POS = requestHead.getUnitMetadataPosition("INM_LL");
			// 报文总长度固定头大小+可选头大小+数据体大小+EOM大小
			byte[] allLenBAL = MetadataTypeConverter.integer2ByteArrayH(headByteArray.length+optlByteArray.length-4+transData.size()+EOM.length);
			// 将报文总长度复制到INM_LL偏移位置
			headBuffer.position(INM_LL_POS);	// 设置新的偏移
			headBuffer.put(allLenBAL[2]);headBuffer.put(allLenBAL[3]);
		}
		// 算MAC
		if (transData.size() > 0) {
			ByteBuffer macData = ByteBuffer.allocate(3+4+2+12+transData.size()+EOM.length);
			macData.put(input.getTellerInfo().get(ITradeKeys.G_TTYNO).toString().getBytes());	// 3
			macData.put(input.getCommCodeDMMap().get(input.getCurrentCommCode()).getTransCode().substring(3,9).getBytes());		// 4+2
			macData.put(input.getTellerInfo().get(ITradeKeys.G_TELLER).toString().getBytes());
			macData.put(transData.toByteArray());
			macData.put(EOM);
			byte[] macResult = null;
			try {
				macResult = CBODEncrypt.MACDATA(macData.array(), (String)input.getTellerInfo().get(ITradeKeys.G_MAC_KEY)).getBytes();
			} catch (Exception e) {
				throw new TransformException(e);
			}
			byte[] macLenBAL = MetadataTypeConverter.integer2ByteArrayH((transData.size()+1) < 491 ? transData.size()+1 : 491);
			byte[] macOffset = {0x00, 0x01};
			// INM_MAC_OFFSET偏移位置
			int INM_MAC_OFFSET_POS   = requestHead.getUnitMetadataPosition("INM_MAC_OFFSET");
			// 将MAC复制到固定头INM_MAC_OFFSET偏移位置
			headBuffer.position(INM_MAC_OFFSET_POS);	// 设置新的偏移
			headBuffer.put(macOffset);
			headBuffer.put(macLenBAL[2]);headBuffer.put(macLenBAL[3]);
			headBuffer.put(macResult[0]);headBuffer.put(macResult[1]);headBuffer.put(macResult[2]);headBuffer.put(macResult[3]);
			// INM_N_MAC_OFFSET偏移位置
			int INM_N_MAC_OFFSET_POS = requestOptl.getUnitMetadataPosition("INM_N_MAC_OFFSET", bitMap);
			// 将MAC复制到可选头INM_N_MAC_OFFSET偏移位置
			optlBuffer.position(4+INM_N_MAC_OFFSET_POS);
			optlBuffer.put(macOffset);
			optlBuffer.put(macLenBAL[2]);optlBuffer.put(macLenBAL[3]);
			optlBuffer.put(macResult);
		}
		
		optlBuffer.reset();	// 指针恢复起始偏移位置
		ByteBuffer allData = ByteBuffer.allocate(headByteArray.length+optlByteArray.length-4+transData.size()+EOM.length);
		allData.put(headByteArray);
		allData.put(optlBuffer);	// 从optlBuffer的偏移位置开始
		allData.put(transData.toByteArray());
		allData.put(EOM);
		return allData.array();
	}

}
