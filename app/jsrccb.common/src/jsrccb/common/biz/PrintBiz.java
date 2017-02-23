package jsrccb.common.biz;

import java.util.List;

import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.trade.core.Trade;

public interface PrintBiz {
	/**
	 * 1.单条内容打印
	 * 格式化后打印单条文本内容
	 * @param trade       交易对象
	 * @param template    模板路径(如用F表名称：namef.fmt)
	 * @param tradeDataDM 交易数据
	 * @param data        可为空，为空时F表里取值都从tradeDataDM获取
	 * @param preview     是否预览
	 * @param reprint     是否重打
	 * */
	public void print      (Trade trade, String template, TradeDataDM tradeDataDM, String[] data, boolean preview, boolean reprint);

	/**
	 * 2.网格内容打印
	 * 多条或网格数据中每条数据进行F表格式化后，再将所有格式后的数据进行打印
	 * 多页纸张打印
	 * @param trade       交易对象
	 * @param template    模板路径(如用作F表名称：namef.fmt)
	 * @param tradeDataDM 交易数据
	 * @param data        网格数据
	 * @param pageSize    页数大小
	 * @param preview     是否预览
	 */
	public void printGrid  (Trade trade, String template, TradeDataDM tradeDataDM, List<String[]> data, int pageSize, boolean preview);
	
	/**
	 * 3.报表内容打印（报表头+报表体+报表尾）
	 * 多页纸张打印
	 * 报表头和报表尾参照print，报表体参照printGrid
	 * @param trade        交易对象
	 * @param headTemplate 定义名称(如用作F表名称：headf.fmt)
	 * @param bodyTemplate 定义名称(如用作F表名称：bodyf.fmt)
	 * @param footTemplate 定义名称(如用作F表名称：fontf.fmt)
	 * @param tradeDataDM  交易数据
	 * @param data         网格数据
	 * @param pageSize     页数大小
	 * @param preview      是否预览
	 */
	public void printReport(Trade trade, String headTemplate, String bodyTemplate, String footTemplate, TradeDataDM tradeDataDM, List<String[]> data, int pageSize, boolean preview);
	
	/**
	 * 打印处理类型
	 * @return
	 */
	public String type();
}
