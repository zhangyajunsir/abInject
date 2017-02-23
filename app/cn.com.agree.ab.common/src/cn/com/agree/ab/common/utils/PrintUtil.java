package cn.com.agree.ab.common.utils;

import java.util.List;
import java.util.Map;

import cn.com.agree.ab.trade.core.Trade;

/**
 * 打印工具抽象
 * @author zhangyajun
 *
 */
public interface PrintUtil<T> {
	
	/**
	 * 格式化数据并调用打印
	 * @param trade
	 * @param printData  打印数据
	 */
	public void print(Trade trade, T printData);
	
	/**
	 * 格式化数据
	 * @param trade
	 * @param formatFile 模板文件路径
	 * @param context    环境类型数据(TradeDataDM变种)
	 * @param arrayData  数组类型数据
	 */
	public T format(Trade trade, String formatFile,  Map<String, Object> context, String[] arrayData);
	
	/**
	 * 预览格式化后数据
	 * @param trade
	 * @param printData  打印数据
	 */
	public void preview(Trade trade, T printData);
	
	/**
	 * 将多页的数据合并到一起（换页符分隔）
	 * @param trade
	 * @param printData
	 * @return
	 */
	public T joinWithFF(Trade trade, List<T> printDatas);

}
