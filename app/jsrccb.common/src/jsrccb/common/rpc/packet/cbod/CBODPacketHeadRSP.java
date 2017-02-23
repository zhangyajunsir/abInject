package jsrccb.common.rpc.packet.cbod;

import java.nio.ByteBuffer;
import java.util.List;

import com.google.common.base.Preconditions;

import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.metadata.ObjectMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;

public class CBODPacketHeadRSP extends AbstractPacketEntity<PacketRspDM, byte[]> {
	
	private List<UnitMetadata<Object, byte[]>> responseConfigList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		responseConfigList = (List<UnitMetadata<Object, byte[]>>)packetConfig.getPacketConfig("response");
		Preconditions.checkState(responseConfigList != null, "报文响应头〖"+name+"〗配置错误");
	}

	@Override
	public PacketRspDM parse(byte[] input) throws TransformException {
		if ("OPTL".equalsIgnoreCase(name)) {
			// OPTL处理
			return parseOptl(input);
		} else {
			// HEAD处理
			return parseHead(input);
		}
	}
	
	private PacketRspDM parseHead(byte[] input) throws TransformException {
		PacketRspDM headRspDM = PacketRspDM.newInstance();
		int position = 0;
		for (UnitMetadata<Object, byte[]> unitMetadata : responseConfigList) {
			VariableFieldMetadata field = (VariableFieldMetadata)unitMetadata;		// 均为FixedFieldMetadata
			byte[] byteArray = new byte[field.getMinLength()];
			System.arraycopy(input, position, byteArray, 0, field.getMinLength());
			try {
				Object o = field.parse(byteArray);
				if (o instanceof PacketRspDM) {
					ObjectMergeUtil.merge(headRspDM, (PacketRspDM)o);
				} else {
					headRspDM.getInnerArea().put(field.getName(), o);
				}
			} catch (Exception e) {
				throw new TransformException("报文响应固定头〖"+field.getName()+"〗Parse错误", e);
			}
			position += field.getMinLength();
		}
		return headRspDM;
	}
	
	private PacketRspDM parseOptl(byte[] input) throws TransformException {
		int[] optlBitMap = new int[1];	// 一整型占4个字节
		optlBitMap[0] = MetadataTypeConverter.byteArrayH2Integer(new byte[]{input[0],input[1],input[2],input[3]});
		PacketRspDM optlRspDM  = PacketRspDM.newInstance();
		//
		ByteBuffer optlBuffer = ByteBuffer.wrap(input, 4, input.length-4);
		optlBuffer.mark();	// 在bitMap后面加个标记
		for (int i = 0; i<responseConfigList.size(); i++) {	// 均为FixedFieldMetadata或由FixedFieldMetadata组成的ObjectMetadata
			UnitMetadata<Object, byte[]> unitMetadata = responseConfigList.get(i);
			if ((optlBitMap[i>>5]&(1<<(31-i%32))) != 0) {
				Object out = null;
				if (unitMetadata instanceof ObjectMetadata) {
					byte[] data = new byte[((ObjectMetadata)unitMetadata).getMinLength()];
					optlBuffer.get(data);
					out = unitMetadata.parse(data);
				}
				if (unitMetadata instanceof VariableFieldMetadata) {
					byte[] data = new byte[((VariableFieldMetadata)unitMetadata).getMinLength()];
					optlBuffer.get(data);
					out = unitMetadata.parse(data);
				}
				if (out instanceof PacketRspDM) {
					ObjectMergeUtil.merge(optlRspDM, (PacketRspDM)out);
				} else {
					optlRspDM.getInnerArea().put(unitMetadata.getName(), out);
				}
			}
		}
		
		return optlRspDM;
	}

	
	public int getLength(byte... bitMap) {
		Preconditions.checkState(bitMap == null || bitMap.length == 0 || bitMap.length == 4, "BitMap字节大小不为4");
		int[] optlBitMap = new int[1];	// 一整型占4个字节
		if (bitMap != null && bitMap.length == 4) {
			optlBitMap[0] = MetadataTypeConverter.byteArrayH2Integer(new byte[]{bitMap[0],bitMap[1],bitMap[2],bitMap[3]});
		}
		int length = 0;
		for (int i = 0; i<responseConfigList.size(); i++) {	// 均为FixedFieldMetadata或由FixedFieldMetadata组成的ObjectMetadata
			UnitMetadata<Object, byte[]> unitMetadata = responseConfigList.get(i);
			if (bitMap == null || bitMap.length == 0 || (optlBitMap[i>>5]&(1<<(31-i%32))) != 0) {
				if (unitMetadata instanceof ObjectMetadata) {
					length += ((ObjectMetadata)unitMetadata).getMinLength();
				}
				if (unitMetadata instanceof VariableFieldMetadata) {
					length += ((VariableFieldMetadata)unitMetadata).getMinLength();
				}
			}
		}
		return length;
	}
	
	
}
