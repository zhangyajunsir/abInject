package jsrccb.common.biz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import jsrccb.common.biz.PrintBizProvider.PrintConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.utils.XmlConfigUtil;
import cn.com.agree.ab.common.utils.gtable.GTable;
import cn.com.agree.ab.lib.config.ConfigLoad;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.resource.ResourcePlugin;
import cn.com.agree.ab.trade.core.Trade;

/**
 * 此类功能类似工厂模式
 * 不需要预先绑定，但需要添加@Singleton代表单例
 * @author zhangyajun
 */
@Singleton
public class PrintBizProvider implements ConfigLoad<PrintConfig>{
	private static final Logger	logger	= LoggerFactory.getLogger(PrintBizProvider.class);
	private String xmlPath;
	@Inject
	private ConfigManager configManager;
	@Inject
	private Set<PrintBiz> printBizs;
	@Inject
	private GTable gTable;
	@Inject
	@Named("reversePrintTextBiz")
	private PrintBiz reversePrintTextBiz;
	@Inject
	@Named("stagingPrintBiz")
	private PrintBiz stagingPrintBiz;
	
	public class ProofConfig {
		String  name;
		String  type;
		boolean isPreview;
		boolean isMust;
		boolean reprint;
		String  template;
		String  desc;
		
		private ProofConfig(){}
	}
	
	public class CommitSpecial {
		String   commitCode;
		List<String> skip;
		List<String> add;
		List<String> grid;
		
		private CommitSpecial(){}
	}
	
	public class ReportConfig {
		String  name;
		String  type;
		boolean isPreview;
		int     pageSize;
		String  headTemplate;
		String  bodyTemplate;
		String  footTemplate;
		String  desc;
		
		private ReportConfig(){}
	}
	
	public class PrintConfig {
		Map<String, ProofConfig>   proofs;
		Map<String, CommitSpecial> commitSpecials;
		Map<String, ReportConfig>  reports;
		
		private PrintConfig(){}
	}

	@SuppressWarnings("unchecked")
	public void autoPrint  (Trade trade, TradeDataDM tradeDataDM, String commCode, Map<String, Object> rspMap) {
		Map<String, ProofConfig> proofs = getPrintConfig().proofs;
		CommitSpecial commitSpecial = getPrintConfig().commitSpecials.get(commCode);
		for (String addPrint : commitSpecial.add) {
			rspMap.put(addPrint, null);
		}
		for (Iterator<String> iterator = rspMap.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (commitSpecial != null && commitSpecial.skip.contains(key))
				continue;
			if (proofs.get(key) == null)
				continue;
			
			ProofConfig proof = proofs.get(key);
			PrintBiz printBiz = getPrintBiz(proof.type);
			String reverseSeq = (String)tradeDataDM.getStoreData().get(ITradeKeys.G_REVERSE_SEQNO);
			if (reverseSeq != null && !reverseSeq.isEmpty()) {
				printBiz = reversePrintTextBiz;
			}
			boolean isUnifyPrint = false;	// 后期添加相关判断方法
			if (isUnifyPrint) {
				printBiz = stagingPrintBiz;
			}
			if (printBiz == null)
				throw new BizException(proof.name+"的类型"+proof.type+"配置错误");
			
			if (!isUnifyPrint) {
				if (!proof.isMust)
					try {
						trade.pushInfo("请准备好\"" + proof.desc + "\"，按确定打印！", true);
					} catch (IOException e) {
					}
				else
					if (!trade.isUserConfirmed("是否打印\"" + proof.desc + "\"？"))
						continue;
			}
			
			Object value = rspMap.get(key);
			if (value instanceof List) {
				List<String[]> list = (List<String[]>)value;
				if (commitSpecial != null && commitSpecial.grid.contains(key))	// 凭证连续打印
					printBiz.printGrid(trade, proof.template, tradeDataDM, list, 0, proof.isPreview);
				else {
					for (int i=0;;) {											// 凭证批量打印(CBOD_Output.PrintTablebuffer)
						printBiz.print(trade, proof.template, tradeDataDM, list.get(i), proof.isPreview, proof.reprint);
						i++;
						if (i == list.size()) {
							break;
						}
						if (!isUnifyPrint) {
							if (!proof.isMust)
								try {
									trade.pushInfo("请准备好\"" + proof.desc + "\"，按确定打印！", true);
								} catch (IOException e) {
								}
							else
								if (!trade.isUserConfirmed("是否打印\"" + proof.desc + "\"？"))
									break;
						}
					}
				}
			} else if (value instanceof String) {
				// G表F表不能热加载
				String[] array = null;
				try {
					array = gTable.unpackAll("/dsr/g/"+key+"g.dsr", (String)value);
				} catch (Exception e) {
					logger.warn("通过/dsr/g/"+key+"g.dsr拆解["+value+"]失败");
				}
				printBiz.print(trade, proof.template, tradeDataDM,           array, proof.isPreview, proof.reprint);
			} else if (value instanceof String[]) {
				printBiz.print(trade, proof.template, tradeDataDM, (String[])value, proof.isPreview, proof.reprint);
			} else {
				// 其它情况
				printBiz.print(trade, proof.template, tradeDataDM,            null, proof.isPreview, proof.reprint);
			}
		}
	}
	
	/**
	 * 
	 * @param trade
	 * @param name
	 * @param tradeDataDM
	 * @param data
	 */
	public void printReport(Trade trade, String name, TradeDataDM tradeDataDM, List<String[]> data) {
		// CBOD_Output.printTableContent
		// CBOD_Output.priTabLP
		Map<String, ReportConfig>  reports = getPrintConfig().reports;
		ReportConfig reportConfig  = reports.get(name);
		if (reportConfig == null)
			return;
		PrintBiz printBiz = getPrintBiz(reportConfig.type);
		if (printBiz == null)
			throw new BizException(name+"的类型"+reportConfig.type+"配置错误");
		printBiz.printReport(trade, reportConfig.headTemplate, reportConfig.bodyTemplate, reportConfig.footTemplate, tradeDataDM, data, reportConfig.pageSize, reportConfig.isPreview);
	}
	
	private PrintBiz getPrintBiz(String type) {
		PrintBiz printBiz = null;
		for (PrintBiz _printBiz_ : printBizs) {
			if (type.equalsIgnoreCase(_printBiz_.type())) {
				printBiz = _printBiz_;
				break;
			}
		}
		return printBiz;
	}
	
	private PrintConfig getPrintConfig() {
		if (xmlPath == null) {
			synchronized (this) {
				if (xmlPath == null)
					xmlPath = configManager.getAbsConfigPath("/resources/config/print.xml");
			}
		}
		return configManager.getConfigObject(xmlPath, this);
	}

	@Override
	public PrintConfig load(URL url, InputStream is) throws Exception {
		URL xsdURL = null;
		try {
			xsdURL = ResourcePlugin.getDefault().getResourceURL(configManager.getAbsConfigPath("/resources/xsd/print.xsd"));
		} catch (FileNotFoundException e) {
			throw new BasicRuntimeException("Schema文件〖print.xsd〗不存在", e);
		}
		XmlConfigUtil.vildateSchema(url, xsdURL);
		
		Document document = XmlConfigUtil.initDoc(is);
		PrintConfig printConfig = new PrintConfig();
		XPath xpath = XmlConfigUtil.staticXpath();
		
		XPathExpression pathExpr = null;
		Node node = null;
		try {
			pathExpr = xpath.compile("print-cfg/proofs");
			node = (Node)pathExpr.evaluate(document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new BasicRuntimeException(e.getMessage(), e);
		}
		printConfig.proofs = new HashMap<String, ProofConfig>();
		NodeList nodeSet = node.getChildNodes();
		for (int i = 0; i < nodeSet.getLength(); i++) {
			Node _node_ = nodeSet.item(i);
			if("proof".equals(_node_.getNodeName())){
				NamedNodeMap nodeMap = _node_.getAttributes();
				ProofConfig proofConfig = new ProofConfig();
				proofConfig.name = nodeMap.getNamedItem("name").getNodeValue();
				proofConfig.type = nodeMap.getNamedItem("type").getNodeValue();
				proofConfig.isPreview = nodeMap.getNamedItem("preview") != null && "true".equals(nodeMap.getNamedItem("preview").getNodeValue()) ? true : false;
				proofConfig.isMust    = nodeMap.getNamedItem("must")    != null && "true".equals(nodeMap.getNamedItem("must")   .getNodeValue()) ? true : false;
				proofConfig.reprint   = nodeMap.getNamedItem("reprint") != null && "true".equals(nodeMap.getNamedItem("reprint").getNodeValue()) ? true : false;
				proofConfig.template  = nodeMap.getNamedItem("template") == null ? "" : nodeMap.getNamedItem("template").getNodeValue();
				proofConfig.desc = _node_.getTextContent();
				printConfig.proofs.put(nodeMap.getNamedItem("name").getNodeValue(), proofConfig);
			}
		}
		
		try {
			pathExpr = xpath.compile("print-cfg/commit-special");
			node = (Node)pathExpr.evaluate(document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new BasicRuntimeException(e.getMessage(), e);
		}
		printConfig.commitSpecials = new HashMap<String, CommitSpecial>();
		nodeSet = node.getChildNodes();
		for (int i = 0; i < nodeSet.getLength(); i++) {
			Node _node_ = nodeSet.item(i);
			if("commit-code".equals(_node_.getNodeName())){
				NamedNodeMap nodeMap = _node_.getAttributes();
				CommitSpecial commitSpecial = new CommitSpecial();
				commitSpecial.commitCode = nodeMap.getNamedItem("name").getNodeValue();
				commitSpecial.skip = nodeMap.getNamedItem("skip") == null ? new ArrayList<String>() : Arrays.asList(nodeMap.getNamedItem("skip").getNodeValue().split(","));
				commitSpecial.add  = nodeMap.getNamedItem("add")  == null ? new ArrayList<String>() : Arrays.asList(nodeMap.getNamedItem("add") .getNodeValue().split(","));
				commitSpecial.grid = nodeMap.getNamedItem("grid") == null ? new ArrayList<String>() : Arrays.asList(nodeMap.getNamedItem("grid").getNodeValue().split(","));
				printConfig.commitSpecials.put(nodeMap.getNamedItem("name").getNodeValue(), commitSpecial);
			}
		}
		
		try {
			pathExpr = xpath.compile("print-cfg/reports");
			node = (Node)pathExpr.evaluate(document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new BasicRuntimeException(e.getMessage(), e);
		}
		printConfig.reports = new HashMap<String, ReportConfig>();
		nodeSet = node.getChildNodes();
		for (int i = 0; i < nodeSet.getLength(); i++) {
			Node _node_ = nodeSet.item(i);
			if("report".equals(_node_.getNodeName())){
				NamedNodeMap nodeMap = _node_.getAttributes();
				ReportConfig reportConfig = new ReportConfig();
				reportConfig.name = nodeMap.getNamedItem("name").getNodeValue();
				reportConfig.type = nodeMap.getNamedItem("type").getNodeValue();
				reportConfig.isPreview = nodeMap.getNamedItem("preview")   != null && "true".equals(nodeMap.getNamedItem("preview").getNodeValue()) ? true : false;
				reportConfig.pageSize  = nodeMap.getNamedItem("page-size") == null ? 0 : Integer.valueOf(nodeMap.getNamedItem("page-size").getNodeValue());
				reportConfig.headTemplate  = nodeMap.getNamedItem("head-template") == null ? "" : nodeMap.getNamedItem("head-template").getNodeValue();
				reportConfig.bodyTemplate  = nodeMap.getNamedItem("body-template") == null ? "" : nodeMap.getNamedItem("body-template").getNodeValue();
				reportConfig.footTemplate  = nodeMap.getNamedItem("foot-template") == null ? "" : nodeMap.getNamedItem("foot-template").getNodeValue();
				reportConfig.desc = _node_.getNodeValue();
				printConfig.reports.put(nodeMap.getNamedItem("name").getNodeValue(), reportConfig);
			}
		}
		return printConfig;
	}
	
}
