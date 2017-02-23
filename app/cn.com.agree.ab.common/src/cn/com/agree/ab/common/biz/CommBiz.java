package cn.com.agree.ab.common.biz;

import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;


/**
 * 通讯业务逻辑
 * @author zhangyajun
 */
public interface CommBiz {
	
	public CommCodeDM  findCommCode(String commCode);
	
	public void addOrUpdateCommCode(CommCodeDM commCodeDM);
	
	public void delCommCode(String commCode);

	public PacketRspDM exchange(CommCodeDM commCodeDM, PacketReqDM packetReqDM);
	
	public String systemCode();
	
}
