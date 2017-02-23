package jsrccb.common.biz.dev;

import jsrccb.common.dm.dev.CitizenCardDM;
import cn.com.agree.ab.trade.core.Trade;

public interface CitizenCardBiz {
	// 使用公共抽象类
	/**
	 * 写市民卡（针对全国、南通、姜堰、南通住建使用不同的实现类实现）
	 * @param trade
	 * @param data
	 */
	public void writeCitizenCard(Trade trade, CitizenCardDM data);

	/**
	 * 读市民卡（针对全国、南通、姜堰、南通住建使用不同的实现类实现）
	 * @param trade
	 * @param must
	 * @return
	 */
	public CitizenCardDM readCitizenCardSimpleData(Trade trade, boolean must);
	
	/**
	 * 读市民卡ALL（针对全国、南通、姜堰、南通住建使用不同的实现类实现）
	 * @param trade
	 * @param must
	 * @return
	 */
	public CitizenCardDM readCitizenCardAllData(Trade trade, boolean must, String condition);
}
