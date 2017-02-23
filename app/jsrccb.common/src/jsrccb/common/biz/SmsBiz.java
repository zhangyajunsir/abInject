package jsrccb.common.biz;

import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.trade.core.Trade;

/**
 * 
 * 短信功能biz接口
 *
 */
public interface SmsBiz {
	
	/**
	 * 向手机发送验证码内容
	 * @param trade         
	 * @param phoneNumber   手机号
	 * @param userName      客户名字
	 * @return
	 */
	public String sendValidateMsg(Trade trade, String phoneNumber , String userName);
	
	
	/**
	 * 读验证码
	 * @param trade         
	 * @return
	 */
	public String readValidate(Trade trade);
	
}
