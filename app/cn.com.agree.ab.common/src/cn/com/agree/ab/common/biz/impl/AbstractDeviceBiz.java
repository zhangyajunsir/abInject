package cn.com.agree.ab.common.biz.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.lib.biz.IDeviceBiz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.core.IDeviceManager;
import cn.com.agree.ab.trade.core.device.IRun;
import cn.com.agree.ab.trade.core.tools.RuntimeUtil;
import cn.com.agree.ab.trade.local.device.ITradeDevice;
import cn.com.agree.commons.csv.CsvUtil;

public abstract class AbstractDeviceBiz implements IDeviceBiz {
	
	private static final Logger	logger	= LoggerFactory.getLogger(AbstractDeviceBiz.class);
	@Override
	public String[] getDeviceNameAndPort(IDeviceManager deviceManager) {
		try {
			ITradeDevice tradeDevice = (ITradeDevice)deviceManager.getDevice(getDeviceType());
			return tradeDevice.getConfigs(false);
		} catch (IOException e) {
			throw new BizException(e.getMessage(), e);
		}
		
	}

	@Override
	public Map<String,String[]> getDeviceAllNameAndPort(IDeviceManager deviceManager) {
		try {
			ITradeDevice tradeDevice = (ITradeDevice)deviceManager.getDevice(getDeviceType());
			String[] nameAndPort = tradeDevice.getConfigs(true);
			Map<String,String[]> map = new HashMap<String,String[]>();
			map.put("name", CsvUtil.csvToStringArray(nameAndPort[0]));
			map.put("port", CsvUtil.csvToStringArray(nameAndPort[1]));
			return map;
		} catch (IOException e) {
			throw new BizException(e.getMessage(), e);
		}
	}
	
	public String runLocalDev(String device, String company, int termtype, int timeout, int funcId, String... paras)  {
		List<String> commands  = new ArrayList<String>();
		commands.add("./bin/ahadev");
		commands.add("-d="+device);
		commands.add("-c="+company);
		commands.add("-t="+termtype);
		commands.add("-a=0");
		commands.add("-T="+timeout);
		commands.add("-f="+funcId);
		if (paras == null || paras.length == 0) {
			commands.add("-p=''");
		} else {
			StringBuffer paraSB = new StringBuffer("-p=");
			for (String para : paras) {
				paraSB.append(para).append(" ");
			}
			paraSB.deleteCharAt(paraSB.length()-1);
			commands.add(paraSB.toString());
		}

		StringBuffer commandSB = new StringBuffer("./bin/ahadev ");
		commandSB.append(" -d=").append(device) .append(" ");
		commandSB.append(" -c=").append(company).append(" ");
		commandSB.append(" -t=").append(termtype).append(" ");
		commandSB.append(" -a=0 ");
		commandSB.append(" -T=").append(timeout).append(" ");
		commandSB.append(" -f=").append(funcId) .append(" ");
		commandSB.append(" -p='");
		for (String para : paras) {
			commandSB.append(para).append(" ");
		}
		if (paras.length > 0)
			commandSB.deleteCharAt(commandSB.length()-1);
		commandSB.append("'");
		logger.debug("执行外部命令：{}", commandSB);
		byte[] result = null;
		try {
			// 将整条命令拆成多个，解决JAVA中Runtime.getRuntime().exec执行整条命令时，参数不能带空格问题
			result = RuntimeUtil.run(commands.toArray(new String[commands.size()]));
		} catch (IOException e) {
			throw new DevException(e.getMessage(), e);
		}
		if (result == null || result.length == 0) {
			throw new DevException("["+commandSB+"]失败!");
		}
		String[] results = null;
		
		try {
			String out = new String(result,"GBK");
			results = StringUtils.split(out , new String(new byte[]{(byte)0xff}));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if (results == null) {
			throw new DevException("["+commandSB+"]失败!");
		}
		
		if (results[0].indexOf("0000") < 0) {
			throw new DevException(results[1]);
		}
		return results[1];
	}

	/**
	 * 执行客户端AHADEV
	 * 
	 * @return
	 */
	public String runClientDev(IDeviceManager deviceManager, String device, int timeout, int funcId, String... paras) {
		
		String[] nameAndPort = getDeviceNameAndPort(deviceManager);
		if (nameAndPort == null || nameAndPort.length < 2 || nameAndPort[1].length() < 4) {
			throw new BizException("端口配置出错");
		}
		return runClientDev(deviceManager, device, nameAndPort[0], Integer.valueOf(nameAndPort[1].substring(3, 4)), timeout, funcId, paras);

	}
	
	public String runClientDev(IDeviceManager deviceManager, String device, String company, int comNo, int timeout, int funcId, String... paras) {
		List<String> commands  = new ArrayList<String>();
		commands.add(".\\driver\\ahadev\\ahadev.exe");
		commands.add("-d="+device);
		commands.add("-c="+company);
		commands.add("-t=0");
		commands.add("-a="+comNo);
		commands.add("-T="+timeout);
		commands.add("-f="+funcId);
		if (paras == null || paras.length == 0) {
			commands.add("-p=''");
		} else {
			StringBuffer paraSB = new StringBuffer("-p=");
			for (String para : paras) {
				paraSB.append(para).append(" ");
			}
			paraSB.deleteCharAt(paraSB.length()-1);
			commands.add(paraSB.toString());
		}
		
		StringBuffer commandSB = new StringBuffer(".\\driver\\ahadev\\ahadev.exe ");
		commandSB.append(" -d=").append(device) .append(" ");
		commandSB.append(" -c=").append(company).append(" ");
		commandSB.append(" -t=0 ");
		commandSB.append(" -a=").append(comNo)  .append(" ");
		commandSB.append(" -T=").append(timeout).append(" ");
		commandSB.append(" -f=").append(funcId) .append(" ");
		commandSB.append(" -p='");
		for (String para : paras) {
			commandSB.append(para).append(" ");
		}
		if (paras.length > 0)
			commandSB.deleteCharAt(commandSB.length()-1);
		commandSB.append("'");
		
        byte[] result = null;
		try {
			IRun run = (IRun) deviceManager.getDevice("run");
	        if (run == null) {
	            throw new IOException("0026 设备管理器未注册'run'类型设备");
	        }
	        // 将整条命令拆成多个，解决JAVA中Runtime.getRuntime().exec执行整条命令时，参数不能带空格问题
			result = run.run(commands.toArray(new String[commands.size()]), new String[0]);

		} catch (IOException e) {
			throw new DevException(e.getMessage(), e);
		}
		if (result == null || result.length == 0) {
			throw new DevException("["+commandSB+"]失败!");
		}
		String[] results = null;
		try {
			String out = new String(result,"GBK");
			results = StringUtils.split(out , new String(new byte[]{(byte)0xff}));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (results[0].indexOf("0000") < 0) {
			throw new DevException(results[1]);
		}
		//只有成功内容,不包含成功0000
		return results[1];
	}
}
