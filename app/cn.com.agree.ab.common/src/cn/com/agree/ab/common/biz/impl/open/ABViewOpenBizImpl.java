package cn.com.agree.ab.common.biz.impl.open;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.MDC;

import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.ExpressionBizProvider;
import cn.com.agree.ab.common.biz.SerialNumBiz;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.dm.TradeRelationsDM;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.key.IComponentKeys;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.commons.csv.CsvUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

import com.google.common.collect.ImmutableMap;

@AutoBindMapper(baseClass = IViewOpenBiz.class, multiple = true)
@Singleton
@Biz("abViewOpenBiz")
public class ABViewOpenBizImpl implements IViewOpenBiz {
	@Inject
	@Named("tradeBiz")
	private TradeBiz tradeBiz;
	@Inject
	@Named("serialNumBiz")
	private SerialNumBiz serialNumBiz;
	@Inject
	private ExpressionBizProvider expressionBizProvider;

	
	/**
	 * 打开视图
	 * 采用异步打开或交易容器打开
	 * 平台异步打开均是窗口打开
	 */
	public void asycOpenView(Trade trade, String tradeCode, OpenViewArgDM openViewArg, BeforeViewOpen... beforeViewOpens) {
		openTrade(trade, openViewArg != null && openViewArg.isWindow() ? ASYC_OPEN_TYPE : COMP_OPEN_TYPE, tradeCode, openViewArg, beforeViewOpens);
	}
	
	/**
	 * 退出打开视图
	 */
	public void exitOpenView(Trade trade, String tradeCode, OpenViewArgDM openViewArg, BeforeViewOpen... beforeViewOpens) {
		openTrade(trade, EXIT_OPEN_TYPE, tradeCode, openViewArg, beforeViewOpens);
	}
	
	/**
	 * 同步打开视图
	 * 采用挂起打开方式，若使用同步打开方式，会造成事件方法调用处等待过长
	 */
	public void syncOpenView(Trade trade, String tradeCode, OpenViewArgDM openViewArg, BeforeViewOpen... beforeViewOpens) {
		openTrade(trade, SUSP_OPEN_TYPE, tradeCode, openViewArg, beforeViewOpens);
	}
	
	
	@SuppressWarnings("unchecked")
	private void openTrade(Trade trade, int openType, String tradeCode, OpenViewArgDM openViewArg, BeforeViewOpen... beforeViewOpens) {
		// 0.由Trade类获取终端登录的柜员相关信息
		Map<String, Object> tellerInfo = trade.getTellerInfo();
		String teller  = (String)tellerInfo.get(ITradeKeys.G_TELLER);
		if (openViewArg == null)
			openViewArg = new OpenViewArgDM();
		// 1.交易属性,获取需要的交易属性，联表查询得到tradeCodeEntity,tradeCodeDM由Entity拷贝而来
		TradeCodeDM tradeCodeDM = filtrateTradeCodeDM(trade, tradeCode);
		if (tradeCodeDM == null)
			throw new BizException("未配置或无可用的交易码【"+tradeCode+"】的交易");
		
		// 2.权限校验
		boolean isPermission = tradeBiz.checkTradePermission(tradeCodeDM.getExpressionid(), tradeCode, null, teller);
		if (!isPermission)
			throw new BizException("柜员【"+teller+"】无权执行交易码【"+tradeCode+"】的交易");
		
		// 3.交易执行流水
		String seqNum = serialNumBiz.generateFrontSerialNum(teller, tradeCodeDM.getId());
		// 3.1设置当前线程日志流水为打开交易的流水，用于第4和5步处理过程
		MDC.put("ab.seqno", seqNum);
		try {
			// 4.交易打开前处理
			if (openViewArg.getTitle() == null || openViewArg.getTitle().length() == 0)
				openViewArg.setTitle(tradeCodeDM.getName());
			//提取待打开交易的交易路径
			String tradeClass = autoAssembleTradeClass(tradeCode, tradeCodeDM.getType(), tradeCodeDM.getSystem(), tradeCodeDM.getPath().trim());
			if (tradeClass == null)
				throw new BizException("无获取交易码【"+tradeCode+"】的类路径");
			//构建当前交易与待打开交易的变量与组件值关系
			autoAssembleRelationVar (trade, tradeClass, openViewArg, tradeCodeDM);
			for (BeforeViewOpen beforeViewOpen : beforeViewOpens) {
				beforeViewOpen.prepare(openViewArg);
			}
			openViewArg.getImportVar().put("#"+ITradeKeys.T_SEQNO,               seqNum);
			openViewArg.getImportVar().put("#"+ITradeKeys.T_TRADE_CODE,          tradeCode);
			openViewArg.getImportVar().put("#"+ITradeKeys.T_TRADE_NAME,          openViewArg.getTitle());
			openViewArg.getImportVar().put("#"+ITradeKeys.T_TRADE_EXPRESSION_ID, tradeCodeDM.getExpressionid().toString());
			// 5.交易打开
			openTrade(trade, openType, tradeClass,  openViewArg.getTitle(), openViewArg.getImportVar(), openViewArg.getExportNames(), autoAssembleTradeStyles(trade, openViewArg, tradeCodeDM.getWindowXsize(), tradeCodeDM.getWindowYsize()));
		} finally {
			// 恢复当前交易流水到日志里
			MDC.put("ab.seqno", trade.getStoreData(ITradeKeys.T_SEQNO));
		}
	}
	
	private TradeCodeDM filtrateTradeCodeDM(Trade trade, String tradeCode) {
		List<TradeCodeDM> tradeCodeDMs = tradeBiz.findTradeCode(tradeCode);
		if (tradeCodeDMs == null || tradeCodeDMs.size() == 0) 
			return null;
		TradeDataDM tradeDataDM = TradeHelper.getTradeData(trade);
		TradeCodeDM tradeCodeDM = null, allTradeCodeDM = null;
		for (TradeCodeDM _tradeCodeDM_ : tradeCodeDMs) {
			if (_tradeCodeDM_.getExpressionid() > 0) {
				Boolean isTure = expressionBizProvider.executeExpression(_tradeCodeDM_.getExpressionid(), tradeDataDM);
				if (isTure != null && isTure) {
					tradeCodeDM = _tradeCodeDM_;
					break;
				}
			} else {
				allTradeCodeDM = _tradeCodeDM_;
			}
		}
		if (tradeCodeDM == null)
			tradeCodeDM = allTradeCodeDM;
		return tradeCodeDM;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void openTrade(Trade trade, int openType, String tradeClass, String title, Map<String, String> importVar, String[] exportNames, String[] openTradeStyles) {
		switch (openType) {
			case ASYC_OPEN_TYPE:
				trade.asyncOpenTrade      (tradeClass, title, importVar, openTradeStyles);
				break;
			case EXIT_OPEN_TYPE:
				trade.exitAndOpenTrade    (tradeClass, title, importVar, openTradeStyles);
				break;
			case SYNC_OPEN_TYPE:
			case SUSP_OPEN_TYPE:
				importVar.put("#"+ITradeKeys.T_SOURCE_PLATFORM_ID, trade.getTradeId());
				trade.suspendAndOpenTrade (tradeClass, title, importVar, exportNames, openTradeStyles);
				break;
			case COMP_OPEN_TYPE:
				Map tellerInfo = trade.getTellerInfo();
				String frameExpressionId = (String)tellerInfo.get("FRAME_EXPRESSION_ID");
				String frameTradeCode    = (String)tellerInfo.get("FRAME_TRADE_CODE");
				if (frameExpressionId.isEmpty() || frameTradeCode.isEmpty()) 
					throw new BizException("未查找到主界面交易");
				TradeCodeDM frameCodeDM = tradeBiz.findTradeCode(Integer.valueOf(frameExpressionId), frameTradeCode);
				if (frameCodeDM == null)
					throw new BizException("未查找到主界面交易");
				Map<String,String> prop = new HashMap<String,String>();
				prop.put("tradeCode", frameCodeDM.getPath());
				Map<String,Object> cont = new HashMap<String,Object>();
				cont.put("tradeCode",   tradeClass);
				cont.put("title",       title);
				cont.put("importVar",   CsvUtil.mapToCsv(importVar));
				cont.put("exportNames", CsvUtil.stringArrayToCsv(exportNames));
				try {
					trade.getTradeMessageSender().oneWay(prop, "CompositeOpenTrade", CsvUtil.mapToCsv(cont));
				} catch (IOException e) {
					throw new BizException("通过主界面多交易容器打开交易失败", e);
				}
				break;
			default:
				throw new BizException("错误的打开方式");
		}
	}
	
	protected String autoAssembleTradeClass (String tradeCode, String tradeType, String tradeSystem, String path) {
		if (tradeSystem == null || "".equals(tradeSystem) || "front".equals(tradeSystem)) 
			return path;
		return null;
	}
	
	/**
	 * A交易联动B交易时根据规则自动带入A交易值到B
	 * @param trade
	 * @param tradePath
	 * @param openViewArg
	 * @param tradeCodeDM
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	protected void autoAssembleRelationVar (Trade trade, String tradePath, OpenViewArgDM openViewArg, TradeCodeDM tradeCodeDM) {
		Map<String, String> importVar = openViewArg.getImportVar();
		if (importVar == null || importVar instanceof ImmutableMap) {
			importVar = new HashMap<String, String>();
			if (openViewArg.getImportVar() != null)
				importVar.putAll(openViewArg.getImportVar());
			openViewArg.setImportVar(importVar);
		}
		List<String> exportName = new ArrayList<String>();
		if (openViewArg.getExportNames() != null) {
			exportName.addAll(Arrays.asList(openViewArg.getExportNames()));
		}
		/** importVar里key中，以#开头的会自动put到打开交易的StoreData里，以$开头且含|的会自动delta到打开交易的组件里
		 * 1.采用先设置每个交易都需要公共信息 ，根据<ITradeKeys>里G开头的全局变量名来查找
		 * 2.根据打开交易的class里的组件成员变量，到本交易的StoreData和组件查找（约束交易中组件的命名规则，需要参照amiba的字典）
		 * 3.再通过数据库配置表来做
		 * */
		/** exportName以#开头的会自动到打开交易的StoreData里获取，以$开头且含|的会自动到打开交易的组件里获取
		 * 1. 根据打开交易的class里的组件成员变量，到本交易的StoreData和组件查找有一样的名称
		 * 2. 采用通过数据库配置表来做
		 * */
		// 1.<ITradeKeys>内G开头的公共信息基本放在TellerInfo里面，联动时候无需再带入。
		// 2.1遍历A交易界面组件
		Map<String, Object> tradeDataA = null;
		try {
			tradeDataA = TradeHelper.getComponentData(trade); // A交易界面栏位值集合
		} catch (Exception e) {
			throw new BizException("获取当前交易组件值失败", e);
		}
		// 2.2遍历A交易StoreData值
		Map<String, Object> storeDataA = (Map<String, Object>)trade.getTradeComponentData().get(IComponentKeys.STOREDATAID);
		// 3.1获取B交易界面组件
		String[] componentIdsOfB = TradeHelper.getComponentId(tradePath);
		// 3.2获取A2B变量映射关系
//		Map<String, String> mapping = null;
		List<TradeRelationsDM> tradeRelationsDMs = null;
		if (trade instanceof AbstractCommonTrade && tradeCodeDM != null) {
			Integer aTradeID = ((TradeCodeDM)((AbstractCommonTrade)trade).getContext(ITradeContextKey.TRADE_CODE_DM)).getId();
			tradeRelationsDMs = tradeBiz.getRelationMapping(aTradeID, tradeCodeDM.getId()); // 由A、B交易的ID查询relations表
		}
		Map<String, String> _mapping_ = new IdentityHashMap<String, String>();	// 可重复key的Map
		// 4.1组件映射
		for(String componentIdOfB : componentIdsOfB) {
			if (storeDataA.keySet().contains(componentIdOfB)) {
				// TradeContainer.tradeInit()，通过key为B交易赋值
				importVar.put("$"+componentIdOfB+"|"+IComponentKeys.TEXT, (String)storeDataA.get(componentIdOfB));
				// TradeContainer.getExportVar(String[] exportVarsNames)，通过key从B交易取值
				exportName.add("$"+componentIdOfB+"|"+IComponentKeys.TEXT);
				
				_mapping_.put("#"+componentIdOfB, "$"+componentIdOfB+"|"+IComponentKeys.TEXT);
			}
			if (tradeDataA.keySet().contains(componentIdOfB+"|"+IComponentKeys.TEXT)) {
				// TradeContainer.tradeInit()，通过key为B交易赋值
				importVar.put("$"+componentIdOfB+"|"+IComponentKeys.TEXT, (String)tradeDataA.get(componentIdOfB+"|"+IComponentKeys.TEXT));
				// TradeContainer.getExportVar(String[] exportVarsNames)，通过key从B交易取值
				exportName.add("$"+componentIdOfB+"|"+IComponentKeys.TEXT);
				
				_mapping_.put("$"+componentIdOfB+"|"+IComponentKeys.TEXT, "$"+componentIdOfB+"|"+IComponentKeys.TEXT);
			}
			
		}
		/* 4.2手动映射,支持屏蔽，支持数据库通配符(
			1.A tradeID/B tradeID 交易一对一配置
			2.A tradeID/B 0                   进行A交易与所有其他交易的通配
			3.A 0      /B tradeID 进行任意交易打开B交易传递变量
			4.A_TRADE_COMPS为!开头表示需要删除自动映射的KEY)
		*/
		if (tradeRelationsDMs != null) {
			for (TradeRelationsDM tradeRelationsDM : tradeRelationsDMs) {
				String keyOfA =tradeRelationsDM.getATradeComps();
				String keyOfB =tradeRelationsDM.getBTradeComps();
				Object valueOfA = null;
				if (keyOfA.startsWith("#"))
					valueOfA = storeDataA.get(keyOfA.substring(1));
				else if (keyOfA.startsWith("$"))
					valueOfA = tradeDataA.get(keyOfA.substring(1));
				else if (keyOfA.startsWith("!")) {
					importVar .remove(keyOfA.substring(1));
					exportName.remove(keyOfA.substring(1));
					_mapping_ .remove(keyOfA.substring(1));
					continue;
				} else {
					valueOfA = tradeDataA.get(keyOfA);
					if (valueOfA == null)
						continue;
					keyOfA   = "$" + keyOfA;
				}
				if (!keyOfB.startsWith("#") && !keyOfB.startsWith("$"))
					keyOfB = "#" + keyOfB;
				if (keyOfB.startsWith("$") && keyOfB.indexOf('|') < 0)
					keyOfB = keyOfB + "|" + IComponentKeys.TEXT;
				importVar .put(keyOfB, (String)valueOfA);
				if (!"0".equals(tradeRelationsDM.getATradeId()) && !"0".equals(tradeRelationsDM.getBTradeId())) {
					exportName.add(keyOfB);
				}
				_mapping_ .put(keyOfA, keyOfB);
			}
		}
		if (trade instanceof AbstractCommonTrade) {
			@SuppressWarnings("rawtypes")
			Map<String,Map<String, String>> allMapping = (Map)((AbstractCommonTrade)trade).getContext(ITradeContextKey.RELATION_MAPPING);
			if (allMapping == null) {
				allMapping = new HashMap<String,Map<String, String>>();
				((AbstractCommonTrade)trade).putContext(ITradeContextKey.RELATION_MAPPING, allMapping);
			}
			Integer aTradeID = ((TradeCodeDM)((AbstractCommonTrade)trade).getContext(ITradeContextKey.TRADE_CODE_DM)).getId();
			allMapping.put(aTradeID+"_"+tradeCodeDM.getId(), _mapping_);
			importVar .put("#"+ITradeKeys.T_TRADE_ID, tradeCodeDM.getId().toString());
			exportName.add("#"+ITradeKeys.T_TRADE_ID);
			exportName.add("#"+ITradeKeys.T_TRADE_STATUS);
		}
		openViewArg.setExportNames(exportName.toArray(new String[exportName.size()]));
	}
	
	@SuppressWarnings("static-access")
	protected String[] autoAssembleTradeStyles (Trade trade, OpenViewArgDM openViewArg, Integer defaultWidth,  Integer defaultHeight) {
		List<String> styles = new ArrayList<String>();
		if (openViewArg.isWindow()) {
			styles.add(trade.OPEN_TRADE_KEY_STYLE);
			styles.add(trade.OPEN_TRADE_STYLE_WINDOW);
		} else {
			styles.add(trade.OPEN_TRADE_KEY_STYLE);
			styles.add(trade.OPEN_TRADE_STYLE_NORMAL);
		}
		if (openViewArg.getWindowX() > 0 && openViewArg.getWindowY() > 0) {
			styles.add(trade.OPEN_TRADE_KEY_X);
			styles.add(openViewArg.getWindowX()+"");
			styles.add(trade.OPEN_TRADE_KEY_Y);
			styles.add(openViewArg.getWindowY()+"");
		}
		int width  = 0;
		int height = 0;
		if (openViewArg.getWindowWidth() > 0 && openViewArg.getWindowHeight() > 0) {
			width  = openViewArg.getWindowWidth();
			height = openViewArg.getWindowHeight();
		}
		if (width  == 0 || height == 0) {
			width  = defaultWidth == null ? 0 : defaultWidth;
			height = defaultHeight== null ? 0 : defaultHeight;
		}
		if (width  >  0 && height >  0) {
			styles.add(trade.OPEN_TRADE_KEY_WIDTH);
			styles.add(width +"");
			styles.add(trade.OPEN_TRADE_KEY_HEIGHT);
			styles.add(height+"");
		}
		return styles.toArray(new String[styles.size()]);
	}
	
	
}
