package jsrccb.common.rpc.packet.cbod;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.rpc.packet.AbstractPacketEntity;
import cn.com.agree.ab.common.utils.MetadataTypeConverter;
import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.packet.config.PacketConfig;
import cn.com.agree.ab.lib.rpc.packet.metadata.ObjectMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.UnitMetadata;
import cn.com.agree.ab.lib.rpc.packet.metadata.ValueMode;
import cn.com.agree.ab.lib.rpc.packet.metadata.VariableFieldMetadata;
import cn.com.agree.ab.lib.utils.Hex;

public class CBODPacketHeadREQ extends AbstractPacketEntity<PacketReqDM, byte[]> {
	private static final Logger	logger	= LoggerFactory.getLogger(CBODPacketHeadREQ.class);
	private List<UnitMetadata<Object, byte[]>> requestConfigList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void loadInterface(PacketConfig packetConfig) {
		requestConfigList = (List<UnitMetadata<Object, byte[]>>)packetConfig.getPacketConfig("request");
		Preconditions.checkState(requestConfigList != null, "报文请求头〖"+name+"〗配置错误");
	}

	@Override
	public byte[] format(PacketReqDM input) throws TransformException {
		if ("OPTL".equalsIgnoreCase(name)) {
			// OPTL处理
			return formatOptl(input);
		} else {
			// HEAD处理
			return formatHead(input);
		}
	}

	private byte[] formatHead(PacketReqDM input) throws TransformException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (UnitMetadata<Object, byte[]> unitMetadata : requestConfigList) {
			VariableFieldMetadata field = (VariableFieldMetadata)unitMetadata;		// 均为FixedFieldMetadata
			try {
				byte[] byteArray = field.format(input);
				buffer.write(byteArray);
			} catch (Exception e) {
				throw new TransformException("报文请求固定头〖"+field.getName()+"〗Format错误", e);
			}
		}
		logger.debug("HEAD处理结果：\n"+Hex.toDisplayString(buffer.toByteArray()));
		return buffer.toByteArray();
	}

	private byte[] formatOptl(PacketReqDM input) throws TransformException {
		int[] optlBitMap = new int[1];	// 一整型占4个字节
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (int i=0; i<requestConfigList.size(); i++) {	// 均为FixedFieldMetadata或由FixedFieldMetadata组成的ObjectMetadata
			UnitMetadata<Object, byte[]> unitMetadata = requestConfigList.get(i);
			try {
				byte[] byteArray = unitMetadata.format(input);
				if (isInvalidField(unitMetadata, byteArray))
					continue;
				// 设置BITMAP
				optlBitMap[i>>5] = optlBitMap[i>>5]|(1<<(31-i%32));
				// 
				buffer.write(byteArray);
			} catch (Exception e) {
				throw new TransformException("报文请求可选头〖"+unitMetadata.getName()+"〗Format错误", e);
			}
		}
		ByteBuffer byteBuffer = ByteBuffer.allocate(4+buffer.size());
		byteBuffer.putInt(optlBitMap[0]).put(buffer.toByteArray());
		logger.debug("OPTL处理结果：\n"+Hex.toDisplayString(byteBuffer.array()));
		return byteBuffer.array();
	}
	
	/**
	 * 是否无效字段，规则：
	 * 1.全0x00
	 * 2.全0x20 ascii 码代表的空格
	 * 3.全0x40 ebcdic码代表的空格
	 * 4.全0x30 ascii 码代表的字符0
	 * 4.全0xF0 ebcdic码代表的字符0
	 * @param byteArray
	 * @return
	 */
	private boolean isInvalidField(UnitMetadata<Object, byte[]> unitMetadata, byte[] byteArray) {
		if (unitMetadata == null || byteArray == null)
			return true;
		// 均为FixedFieldMetadata或由FixedFieldMetadata组成的ObjectMetadata
		if (unitMetadata instanceof VariableFieldMetadata) {
			VariableFieldMetadata field = (VariableFieldMetadata)unitMetadata;
			if (isAllSameByte(byteArray, (byte)0x00) && field.getValueMode() != ValueMode.INNER)
				return true;
			if (isAllSameByte(byteArray, (byte)0x20))
				return true;
			if (isAllSameByte(byteArray, (byte)0x40))
				return true;
			if (isAllSameByte(byteArray, (byte)0x30))
				return true;
			if (isAllSameByte(byteArray, (byte)0xF0))
				return true;
			return false;
		} 
		if (unitMetadata instanceof ObjectMetadata) {
			ObjectMetadata objectMetadata = (ObjectMetadata)unitMetadata;
			List<UnitMetadata<Object, byte[]>> subUnitMetadatas = objectMetadata.getUnitMetadatas();
			boolean isTrue = false;
			int position = 0;
			for (UnitMetadata<Object, byte[]> subUnitMetadata : subUnitMetadatas) {
				if (subUnitMetadata instanceof VariableFieldMetadata) {
					VariableFieldMetadata subField = (VariableFieldMetadata)subUnitMetadata;
					byte[] subByteArray = new byte[subField.getMinLength()];
					System.arraycopy(byteArray, position, subByteArray, 0, subField.getMinLength());
					if (!isInvalidField(subField, subByteArray)) {
						isTrue = true;
						break;
					}
					position += subField.getMinLength();
				}
				if (subUnitMetadata instanceof ObjectMetadata) {
					ObjectMetadata subObjectMetadata = (ObjectMetadata)subUnitMetadata;
					byte[] subByteArray = new byte[subObjectMetadata.getMinLength()];
					System.arraycopy(byteArray, position, subByteArray, 0, subObjectMetadata.getMinLength());
					if (!isInvalidField(subObjectMetadata, subByteArray)) {
						isTrue = true;
						break;
					}
					position += subObjectMetadata.getMinLength();
				}
			}
			if (isTrue) // 只要一个字段是非无效，整个ObjectMetadata就是非无效
				return false;
		}
		return true;
	}
	
	private boolean isAllSameByte(byte[] byteArray, byte _byte_) {
		if (byteArray == null)
			return false;
		for (int i=0 ; i<byteArray.length; i++ ) {
			if (byteArray[i] != _byte_)
				return false;
		}
		return true;
	}
	
	/**
	 * 根据名称通过配置获取偏移
	 * @param unitMetadataName
	 * @param bitMap
	 * @return
	 */
	public int getUnitMetadataPosition(String unitMetadataName, byte... bitMap) {
		Preconditions.checkState(bitMap == null || bitMap.length == 0 || bitMap.length == 4, "BitMap字节大小不为4");
		int[] optlBitMap = new int[1];	// 一整型占4个字节
		if (bitMap != null && bitMap.length == 4) {
			optlBitMap[0] = MetadataTypeConverter.byteArrayH2Integer(new byte[]{bitMap[0],bitMap[1],bitMap[2],bitMap[3]});
		}
		int position = 0;
		for (int i=0; i<requestConfigList.size(); i++) {	// 均为FixedFieldMetadata或由FixedFieldMetadata组成的ObjectMetadata
			UnitMetadata<Object, byte[]> unitMetadata = requestConfigList.get(i);
			if (bitMap == null || bitMap.length == 0 || (optlBitMap[i>>5]&(1<<(31-i%32))) != 0) {
				if (unitMetadata.getName().equals(unitMetadataName)) {
					return position;
				}
				if (unitMetadata instanceof ObjectMetadata) {
					position += ((ObjectMetadata)unitMetadata).getMinLength();
				}
				if (unitMetadata instanceof VariableFieldMetadata) {
					position += ((VariableFieldMetadata)unitMetadata).getMinLength();
				}
			}
		}
		return -1;
	}
	
	
}
