package jsrccb.common.utils.print;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jodd.props.Props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.common.utils.PrintUtil;
import cn.com.agree.ab.common.utils.print.PrintText2LP;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.ConfigException;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.tools.RuntimeUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

/**
 * 打印文本到流水打印机(江苏农信特殊打印)
 * 流水打印都是到远程流水服务器上打印，但打印文件在本地服务器上生成
 * @author zhangyajun
 *
 */
@AutoBindMapper(baseClass = PrintUtil.class)
@Singleton
@Named("jsrccbPrintText2LP")
public class JsrccbPrintText2LP extends PrintText2LP {
	private static final Logger	logger	= LoggerFactory.getLogger(JsrccbPrintText2LP.class);
	@Inject
	private ConfigManager configManager;
	

	@SuppressWarnings("static-access")
	public void print2LP(Trade trade, String filePath) {
		String cfgPath = configManager.getAbsConfigPath("/storage/" + trade.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID) + "/printer.ini");
		if (cfgPath == null)
			throw new ConfigException("无配置文件【/storage/" + trade.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID) + "/printer.ini】。");
		Props printCfg = configManager.getGBKIni(cfgPath);
		// 获取打印文件的文件名和文件父目录的绝对路径
		if (filePath == null || filePath.isEmpty())
			throw new DevException("打印文件路径为空");
		File printFile = new File(filePath);
		if (!printFile.exists())
			throw new DevException("打印文件不存在");
		// 获取打印机名称
		String printName = printCfg.getValue(trade.getStoreData(ITradeKeys.G_TELLER)+".PRINTER_NAME");
		if (printName == null || printName.isEmpty())
			printName = printCfg.getValue(trade.getStoreData(ITradeKeys.G_QBR)+".PRINTER_NAME");
		if (printName == null || printName.isEmpty())
			throw new ConfigException("当前柜员的打印配置信息错误，请联系管理员");
		// 获取FTS的节点名
		String ftsDstNodeId = printCfg.getValue(trade.getStoreData(ITradeKeys.G_QBR)+".FTS");
		if (ftsDstNodeId == null || ftsDstNodeId.isEmpty())
			throw new ConfigException("当前机构的文件传输配置信息错误，请联系管理员");
		String ftsSrcNodeId = configManager.getUtilIni().getValue("FTS.REPORT_LABLE");
		if (ftsSrcNodeId == null || ftsSrcNodeId.isEmpty())
			throw new ConfigException("远程打印文件传输配置信息错误，请联系管理员");
		// 上传文件
		String res = ""; 
		String cmd = "fts_act_send -l " + ftsSrcNodeId + " -s " + new File(printFile.getParent()).getAbsolutePath() + " -d " + ftsDstNodeId + " -r /tmp -f " + printFile.getName() + " | grep rtn";
		String infoId = "";
		try {
			infoId = trade.pushInfoWithoutButton("打印文件正在上送打印服务器,请勿按任何键......");
			res = new String(RuntimeUtil.run(cmd), "GBK");
		} catch (IOException e) {
			throw new DevException("打印文件上送失败", e);
		} finally {
			try {
				trade.closeInfo(infoId);
			} catch (IOException e) {}
		}
		logger.debug("执行[{}]结果[{}]", cmd, res);
		if (!"0".equals(res.charAt(res.indexOf("rtn")+4)+""))
			throw new DevException("打印文件上送失败");
		// 远程调用打印命令
		String ip = printCfg.getValue(trade.getStoreData(ITradeKeys.G_QBR)+".IP");
		if (ip == null || ip.isEmpty())
			throw new ConfigException("当前机构的打印配置信息错误，请联系管理员");
		String user = printCfg.getValue(trade.getStoreData(ITradeKeys.G_QBR)+".USER");
		if (user == null || user.isEmpty())
			throw new ConfigException("当前机构的打印配置信息错误，请联系管理员");
		cmd = "./bin/atelnet/remoteprint.sh " + ip + " " + user + " " + trade.getStoreData(trade.G_ABC_IP) + " " + printName + " " + printFile.getName();
		try {
			RuntimeUtil.run(cmd);
		} catch (IOException e) {
			throw new DevException("远程调用打印失败", e);
		}
	}

}
