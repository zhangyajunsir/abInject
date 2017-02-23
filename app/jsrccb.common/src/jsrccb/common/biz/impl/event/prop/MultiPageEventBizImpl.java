package jsrccb.common.biz.impl.event.prop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.PrintBizProvider;
import jsrccb.common.dm.PageDM;
import jsrccb.common.dm.PageDM.PageAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.CommBizProvider;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradePropEventBiz;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.dm.TradePropDM;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.core.component.Table;
import cn.com.agree.ab.trade.core.component.common.Component;
import cn.com.agree.ab.trade.local.TradeBreakException;
import cn.com.agree.commons.csv.CsvUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.common.collect.ImmutableMap;

@AutoBindMapper(baseClass = AbstractTradePropEventBiz.class, multiple = true)
@Singleton
@Biz("multiPageEventBiz")
public class MultiPageEventBizImpl extends AbstractTradePropEventBiz {
	private static final Logger	logger	= LoggerFactory.getLogger(MultiPageEventBizImpl.class);
	@Inject
	private PrintBizProvider printBizProvider;
	@Inject
	private CommBizProvider  commBizProvider;
	@Inject
	@Named("abViewOpenBiz")
	private IViewOpenBiz     viewOpenBiz;
	
	public void onInit   (AbstractCommonTrade trade) {
		TradeCodeDM tradeCodeDM = (TradeCodeDM)trade.getContext(ITradeContextKey.TRADE_CODE_DM);
		boolean hasMultiPageProp = false;
		for (TradePropDM _tradePropDM_ : tradeCodeDM.getTradePropList()) {
			if (_tradePropDM_.getMultipleQueryFlag() == 1) {
				hasMultiPageProp = true;
				break;
			}
		}
		if (!hasMultiPageProp)
			return;
		try {
			// 给交易所有表格注册翻页热键
			Map<String, Component> components = TradeHelper.getComponent(trade);
			for (Iterator<Map.Entry<String, Component>> iterator = components.entrySet().iterator(); iterator.hasNext(); ) {
				Map.Entry<String, Component> entry = iterator.next();
				if (entry.getKey().startsWith("table") && entry.getValue() instanceof cn.com.agree.ab.trade.core.component.Table) {
					// 首页
					trade.unregisterHotKey(entry.getKey(), "HOME");
					trade.registerHotKey  (entry.getKey(), "HOME",
							CsvUtil.stringArrayToCsv(new String[]{"onCommit", 
										JsonUtil.obj2json(ImmutableMap.of("tradeProp", "multiPage", "tableId", entry.getKey(), "action", PageAction.HOME.getCode()))
									}));
					// 上一页
					trade.unregisterHotKey(entry.getKey(), "PAGE_UP");
					trade.registerHotKey  (entry.getKey(), "PAGE_UP",
							CsvUtil.stringArrayToCsv(new String[]{"onCommit", 
										JsonUtil.obj2json(ImmutableMap.of("tradeProp", "multiPage", "tableId", entry.getKey(), "action", PageAction.PREVIOUS.getCode()))
									}));
					// 下一页
					trade.unregisterHotKey(entry.getKey(), "PAGE_DOWN");
					trade.registerHotKey  (entry.getKey(), "PAGE_DOWN",
							CsvUtil.stringArrayToCsv(new String[]{"onCommit", 
										JsonUtil.obj2json(ImmutableMap.of("tradeProp", "multiPage", "tableId", entry.getKey(), "action", PageAction.NEXT.getCode()))
									}));
					// 尾页
					trade.unregisterHotKey(entry.getKey(), "END");
					trade.registerHotKey  (entry.getKey(), "END",
							CsvUtil.stringArrayToCsv(new String[]{"onCommit", 
										JsonUtil.obj2json(ImmutableMap.of("tradeProp", "multiPage", "tableId", entry.getKey(), "action", PageAction.END.getCode()))
									}));
					// 终端打印
					trade.unregisterHotKey(entry.getKey(), "/");
					trade.registerHotKey  (entry.getKey(), "/",
							CsvUtil.stringArrayToCsv(new String[]{"onMessage", "multiPage",
										JsonUtil.obj2json(ImmutableMap.of("tableId", entry.getKey(), "action", PageAction.PRITABLE.getCode()))
									}));
					// 流水打印
					trade.unregisterHotKey(entry.getKey(), "*");
					trade.registerHotKey  (entry.getKey(), "*",
							CsvUtil.stringArrayToCsv(new String[]{"onMessage", "multiPage",
										JsonUtil.obj2json(ImmutableMap.of("tableId", entry.getKey(), "action", PageAction.PRITABLP.getCode()))
									}));
				}
			}
		} catch (Exception e) {
			throw new BizException(e.getMessage(), e);
		}
		trade.putContext(ITradeContextKey.PAGING_DM, new PageDM<String[]>());
	}
	
	@SuppressWarnings("unchecked")
	public void onMessage(AbstractCommonTrade trade, String app, String message) {
		if (!"multiPage".equals(app))
			return;
		if (message == null || !JsonUtil.isJsonObjectString(message))
			throw new BizException("多页属性onMessage方法参数非JSON字串");
		
		TradeCodeDM tradeCodeDM = (TradeCodeDM)trade.getContext(ITradeContextKey.TRADE_CODE_DM);
		TradePropDM tradePropDM = null;
		for (TradePropDM _tradePropDM_ : tradeCodeDM.getTradePropList()) {
			if (_tradePropDM_ != null && _tradePropDM_.getCommCode() != null && trade.getStoreData(ITradeKeys.T_COMM_CODE).equals(_tradePropDM_.getCommCode().getCommCode())) {
				tradePropDM = _tradePropDM_;
				break;
			}
		}
		if (tradePropDM == null || tradePropDM.getMultipleQueryFlag() != 1)
			throw new BizException("通讯码〖"+trade.getStoreData(ITradeKeys.T_COMM_CODE)+"〗非多页属性");
		ContextHelper.setContext(ITradeContextKey.TRADE_PROP_DM, tradePropDM);
		TradeDataDM tradeDataDM = TradeHelper.getTradeData(trade);
		ContextHelper.setContext(ITradeContextKey.TRADE_DATA_DM, tradeDataDM);
		
		Map<String, Object> msgMap = JsonUtil.parseMap(message);
		PageDM<String[]> pageDM = (PageDM<String[]>)trade.getContext().get(ITradeContextKey.PAGING_DM);
		PageDM<String[]>.PageDataDM pageDataDM = pageDM.getPageDataDM((String)msgMap.get("tableId"));
		String tableId = msgMap.get("tableId").toString();
		switch(Integer.valueOf(msgMap.get("action").toString())) {
			case 6 : // 流水打印
				if (!pageDM.isFinished() && pageDataDM.getPageSize() < 50) {	// 后台数据未查询完，且页数小于50，发起循环查询
					if (pageDataDM.getCurrentPageNum() < pageDataDM.getPageSize()) {
						try {
							preCommit(trade, JsonUtil.obj2json(ImmutableMap.of("tradeProp", "multiPage", "tableId", tableId, "action", PageAction.END.getCode())));
						} catch (TradeBreakException e) {
						}
						tradeDataDM.getUiData().put(tableId+"|rows", trade.getProperty(tableId, "table_data")); // 将tradeDataDM里tableId的数据更新成尾页数据
					}
					String infoId = null;
					try {
						infoId = trade.pushInfoWithoutButton("正在循环查询后台表格数据!");
						// 循环查询，不进行界面表格的变动
						while (!pageDM.isFinished() && pageDataDM.getPageSize() < 50) {
							if ((pageDM.getPageContext().get("staKey") == null || pageDM.getPageContext().get("staKey").toString().trim().equals("")) && 
									(pageDM.getPageContext().get("endKey") == null || pageDM.getPageContext().get("endKey").toString().trim().equals("")))
								pageDM.getPageContext().put("action", 0);
							else
								pageDM.getPageContext().put("action", 2);
							Map<String, Object> uiData = tradeDataDM.getUiData();
							try {
								commBizProvider.commCommit(trade.getStoreData(ITradeKeys.T_COMM_CODE), tradeDataDM); //此方法会清空uiData
							} catch (RpcException e) {
								logger.error("后台通讯报错级别：{} 原因：{} 后台原因：{}", e.getLevel().getDesc(), e.getMessage(), e.getMsg());
								if (e.getLevel() != ExceptionLevel.DEBUG) {
									OpenViewArgDM openViewArgDM = new OpenViewArgDM();
									openViewArgDM.setWindow(true);
									openViewArgDM.setImportVar(ImmutableMap.of("#msgLevel", e.getLevel().toJsonString(), "#msgInfo", JsonUtil.obj2json(e.getMsg())));
									viewOpenBiz.asycOpenView(trade, "msg", openViewArgDM);
								}
								if (e.getLevel() == ExceptionLevel.ERROR || e.getLevel() == ExceptionLevel.FATAL) 
									throw new TradeBreakException(e.getLevel().getLevel(), null);
							}
							ObjectMergeUtil.merge(uiData, tradeDataDM.getUiData(), true, true); // 新UiData合并到老UiData里
							tradeDataDM.setUiData(uiData);
							posCommit(trade, JsonUtil.obj2json(ImmutableMap.of("tradeProp", "multiPage", "tableId", tableId, "action", PageAction.PRITABLP.getCode())));
						}
					} catch (IOException e) {
					} finally {
						try {trade.closeInfo(infoId);} catch (IOException e) {}
					}
				}
				tableId = tableId + "_lp";
			case 5 : // 终端打印	
				printBizProvider.printReport(trade, tableId, tradeDataDM, pageDataDM.getAllData());
				break;
			default:
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void preCommit(AbstractCommonTrade trade, String message) {
		if (message == null || !JsonUtil.isJsonObjectString(message))
			return;
		Map<String, Object> msgMap = JsonUtil.parseMap(message);
		if (!"multiPage".equals(msgMap.get("tradeProp")))
			return;
		TradePropDM tradePropDM = (TradePropDM)ContextHelper.getContext(ITradeContextKey.TRADE_PROP_DM);
		if (tradePropDM == null || tradePropDM.getMultipleQueryFlag() != 1)
			throw new BizException("通讯码〖"+trade.getStoreData(ITradeKeys.T_COMM_CODE)+"〗非多页属性");
		PageDM<String[]> pageDM = (PageDM<String[]>)trade.getContext().get(ITradeContextKey.PAGING_DM);
		PageDM<String[]>.PageDataDM pageDataDM = pageDM.getPageDataDM((String)msgMap.get("tableId"));
		List<?> pageData = null;
		int currentPageNum = pageDataDM.getCurrentPageNum();	// 页码大于0的整数
		switch(Integer.valueOf(msgMap.get("action").toString())) {
			case 0 : // 首页
				if (currentPageNum == 1)
					throw new BizException(ExceptionLevel.INFO, "已首页");
				if (pageDataDM.getPageSize() > 0) {
					pageData = pageDataDM.getPageData(1);
					currentPageNum = 1;
				}
				break;
			case 1 : // 上一页
				if (currentPageNum == 1)
					throw new BizException(ExceptionLevel.INFO, "已首页");
				if (pageDataDM.getPageSize() > 0) {
					currentPageNum = currentPageNum - 1;
					if (currentPageNum < 1)
						currentPageNum = 1;
					if (currentPageNum > pageDataDM.getPageSize())
						currentPageNum = pageDataDM.getPageSize();
					pageData = pageDataDM.getPageData(currentPageNum);
				}
				break;
			case 2 : // 下一页
				if (currentPageNum == pageDataDM.getPageSize() && pageDM.isFinished())
					throw new BizException(ExceptionLevel.INFO, "已尾页");
				if (pageDataDM.getPageSize() > 0) {
					currentPageNum = currentPageNum + 1;
					if (currentPageNum <= pageDataDM.getPageSize()) {
						pageData = pageDataDM.getPageData(currentPageNum);
					}
				}
				break;
			case 3 : // 尾页（仅限已查询的最后一页）
				if (currentPageNum == pageDataDM.getPageSize() && !pageDM.isFinished())
					throw new TradeBreakException();
				if (currentPageNum == pageDataDM.getPageSize() &&  pageDM.isFinished())
					throw new BizException(ExceptionLevel.INFO, "已尾页");
				if (pageDataDM.getPageSize() > 0) {
					pageData = pageDataDM.getPageData(pageDataDM.getPageSize());
					currentPageNum = pageDataDM.getPageSize();
				}
				break;
			case 4 : // 指定页码
				break;
			default:
				break;
		}
		if (pageData != null) {
			Map<String, Component> components;
			try {
				components = TradeHelper.getComponent(trade);
			} catch (Exception e) {
				throw new BizException(e.getMessage(), e);
			}
			Table table = (Table)components.get(msgMap.get("tableId"));
			if(table != null){
				table.clear();
				for (int i = 0; i < pageData.size(); i++) {
					if (pageData.get(i) instanceof String[]) {
						table.addRow(-1, (String[])pageData.get(i));
	        		}
	        	}
				pageDataDM.setCurrentPageNum(currentPageNum);
				logger.info("{}已从本地多页数据里获取了第{}页数据", msgMap.get("tableId"), currentPageNum);
				throw new TradeBreakException();
			}
		}
		if (!msgMap.get("action").equals(""+PageAction.NEXT.getCode()))
			throw new TradeBreakException();
		if ((pageDM.getPageContext().get("staKey") == null || pageDM.getPageContext().get("staKey").toString().trim().equals("")) && 
				(pageDM.getPageContext().get("endKey") == null || pageDM.getPageContext().get("endKey").toString().trim().equals("")))
			pageDM.getPageContext().put("action", 0);
		else
			pageDM.getPageContext().put("action", 2);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void posCommit(AbstractCommonTrade trade, String message) {
		if (message != null && !JsonUtil.isJsonObjectString(message))
			return;
		Map<String, Object> msgMap = message == null ? null : JsonUtil.parseMap(message);
		if (msgMap != null && !"multiPage".equals(msgMap.get("tradeProp")))
			return;
		TradePropDM tradePropDM = (TradePropDM)ContextHelper.getContext(ITradeContextKey.TRADE_PROP_DM);
		if (tradePropDM == null || tradePropDM.getMultipleQueryFlag() != 1) {
			return;
		}
		TradeDataDM tradeDataDM = (TradeDataDM)ContextHelper.getContext(ITradeContextKey.TRADE_DATA_DM);
		if (tradeDataDM == null)
			return;
		PageDM<String[]> pageDM = (PageDM<String[]>)tradeDataDM.getContext().get(ITradeContextKey.PAGING_DM);
		pageDM.getPageContext().put("staKey", ((Map)tradeDataDM.getTempArea().get("pageContext")).get("staKey"));
		pageDM.getPageContext().put("endKey", ((Map)tradeDataDM.getTempArea().get("pageContext")).get("endKey"));
		if ("1".equals(((Map)tradeDataDM.getTempArea().get("pageContext")).get("noData"))) {
			pageDM.setFinished(true);
		}
		Map<String, Object> uiData = tradeDataDM.getUiData();
		for (Iterator<Map.Entry<String, Object>> iterator = uiData.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getKey().startsWith("table") && entry.getValue() instanceof List) {
				String tableId = entry.getKey().substring(0, entry.getKey().indexOf('|'));
				PageDM<String[]>.PageDataDM pageDataDM = pageDM.getPageDataDM(tableId);
				int currentPageNum = pageDataDM.getCurrentPageNum();
				List<String[]> uiTableData = new ArrayList((List<String[]>)entry.getValue()); // 包含了新老数据，需要剔除老数据
				for (int i = currentPageNum == 0 ? 1 : currentPageNum; i <= pageDataDM.getPageSize(); i++) {
					int pageLen = pageDataDM.getPageData(i).size();
					for (int j = 0; j < pageLen; j++) {
						uiTableData.remove(0);
					}
				}
				if (uiTableData.size() == 0)
					continue;
				pageDataDM.addPageData(uiTableData);
				if (msgMap == null || Integer.valueOf(msgMap.get("action").toString()) == PageAction.NEXT.getCode())
					pageDataDM.setCurrentPageNum(currentPageNum + 1);
			}
		}
	
	}
}
