package jsrccb.common.biz.impl.dev.print;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jodd.props.Props;
import jsrccb.common.biz.PrintBiz;

import org.apache.commons.lang3.StringUtils;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.exception.ConfigException;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.inject.annotations.AutoBindMapper;



/**
 * 远程流水打印
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintBiz.class, multiple = true)
@Singleton
@Biz("printText2LPBiz")
public class RemotePrintBizImpl extends PrintBizImpl {
	private final String hexString = "0123456789ABCDEF";
	@Inject
	@Named("jsrccbPrintText2LP")
	private PrintUtil<String> printUtil;
	@Inject
	private ConfigManager configManager;
	
	private Map<String, Map<String, String>> prtMapping = null;
	private String command = "";

	RemotePrintBizImpl(){
		 try {
			command = decode("1B010D1B022C");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	protected PrintUtil<String> printUtil() {
		return printUtil;
	}
	
	protected String format(Trade trade, String template, TradeDataDM tradeDataDM, String[] data) {
		String printData = super.format(trade, template, tradeDataDM, data).replaceFirst(command, "");
		// 将F表里配置的伪指令转换成真实流水打印机指令
		String cfgPath = configManager.getAbsConfigPath("/storage/" + trade.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID) + "/printer.ini");
		if (cfgPath == null)
			throw new ConfigException("无配置文件【/storage/" + trade.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID) + "/printer.ini】。");
		Props printCfg = configManager.getGBKIni(cfgPath);
		String printType = printCfg.getValue(trade.getStoreData(ITradeKeys.G_QBR)+".PRINTER_TYPE");
		if (printType == null || printType.isEmpty())
			throw new ConfigException("当前柜员的打印配置信息错误，请联系管理员");
		Map<String, String> mapping = null;
		try {
			mapping = getMapping(printType);
		} catch (Exception e) {
			throw new BizException("流水打印伪指令配置载入异常", e);
		}
		if (mapping == null)
			throw new BizException("没有["+printType+"]流水打印伪指令配置");
		Iterator<Map.Entry<String,String>> iter = mapping.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry<String,String> entry = iter.next();
			String key   = (String)entry.getKey();
			String value = (String)entry.getValue();
			if (printData.indexOf(key) != -1){
				printData = printData.replaceAll(key, value);
			}
		}
		return printData;
	}
	
	@Override
	public String type() {
		return "text2LP";
	}
	
	private Map<String, String> getMapping(String printType) throws Exception {
		if (prtMapping != null)
			return prtMapping.get(printType);
		
		synchronized (this) {
			if (prtMapping != null)
				return prtMapping.get(printType);
			Map<String, Map<String, String>> _prtMapping_ = new HashMap<String, Map<String, String>>();
			BufferedReader br = new BufferedReader(new FileReader(new File("./bin/report/prtmap.ini")));
			String  type = null;
			String  line = null;
			while ((line = br.readLine()) != null){
				line = line.trim();
				if (line.startsWith("#") || line.isEmpty())
					continue;
				if (line.startsWith("[")){
					type = StringUtils.substringBetween(line, "[", "]");
					_prtMapping_.put(type, new HashMap<String,String>());
					continue;
				}
				if (line.indexOf("=") == -1){
					continue;
				}
				String key   = StringUtils.substringBefore(line, "=").trim();
				String value = "";
				if(line.indexOf("#") != -1)
					value = StringUtils.substringBetween(line, "=", "#").trim();
				else
					value = StringUtils.substringAfter(line, "=").trim();
				if (key.equals("@currDate")){
					value = DateUtil.getTodayTimeString();
				} else {
					key   = decode(key);
					value = decode(value);
				}
				
				_prtMapping_.get(type).put(key, value);	
			}
			br.close();
			if(prtMapping == null ){
				prtMapping =  new HashMap<String, Map<String, String>>();
			} else {
				prtMapping.clear();
			}
			prtMapping.putAll(_prtMapping_);
		}
		return prtMapping.get(printType);
	}
	
	/**
	 * GBK编码的十六进制字符串转成正常字符串
	 * @param s
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private String decode(String s) throws UnsupportedEncodingException
	{
	    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(s.length() / 2);
	    for(int i = 0; i < s.length(); i += 2)
	        bytearrayoutputstream.write(hexString.indexOf(s.charAt(i)) << 4 | hexString.indexOf(s.charAt(i + 1)));
	
	    return new String(bytearrayoutputstream.toByteArray(),"GBK");
	}

}
