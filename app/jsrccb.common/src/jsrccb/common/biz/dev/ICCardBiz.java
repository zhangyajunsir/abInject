package jsrccb.common.biz.dev;

import java.util.List;

import jsrccb.common.dm.dev.AccountDM;
import jsrccb.common.dm.dev.ICCardDM;

import cn.com.agree.ab.trade.core.Trade;

public interface ICCardBiz {
	/**
	 * 写IC卡
	 * @param trade
	 * @param must
	 * @param accountDM 使用area55和allData属性
	 */
	public void writeICCard(Trade trade, boolean must, AccountDM accountDM);
	
	/**
	 * 读IC卡全部数据
	 * @param trade
	 * @param must
	 * @return
	 */
	public ICCardDM readICCard(Trade trade, boolean must);
	/**
	 * 读IC卡内部前10笔记录
	 * @param trade
	 * @param must
	 * @return 返回表格可以直接显示的数据
	 */
	public List<String[]>  readICCard10Data(Trade trade, boolean must);
	/**
	 * 读IC卡55区域数据
	 * @param trade
	 * @param must
	 * @param condition 可以考虑该字段不由交易拼接
	 * @return
	 */
	public String readICCard55Area(Trade trade, boolean must, String condition);
}
