package jsrccb.common.biz.dev;

import cn.com.agree.ab.trade.core.Trade;
import jsrccb.common.dm.PinDM;
import jsrccb.common.dm.PinKeyDM;


public interface PinBiz {
	
	/**
	 * 加密密码
	 * @param pinMainKey
	 * @param pinWorkKey
	 * @param account
	 * @param passwd
	 * @return
	 */
	public String encryptPin(String pinMainKey, String pinWorkKey, String account, String passwd);
	
	/**
	 * 同步密钥
	 * @param devID
	 * @return
	 */
	public PinKeyDM syncPinKey(String devID);

	/**
	 * 查询密钥
	 * @param devID
	 * @return 主密钥，工作密钥
	 */
	public PinKeyDM queryPinKey(String devID);
	
	/**
	 * 读取密码
	 * @param trade
	 * @param account
	 * @param must
	 * @param count
	 * @param check
	 * @return
	 */
	public PinDM readPasswd(Trade trade, String account, boolean must, int count, boolean check);

	/**
	 * 国密方式进行主密钥和工作密钥的获取和写入
	 * @param devId 设备号
	 * @param flag  有无主密钥标志
	 * @return PinKeyDM
	 */
	public PinKeyDM queryPinKey(Trade trade);
}
