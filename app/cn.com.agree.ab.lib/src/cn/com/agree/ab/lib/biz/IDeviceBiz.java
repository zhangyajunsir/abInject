package cn.com.agree.ab.lib.biz;

import java.util.Map;

import cn.com.agree.ab.trade.core.IDeviceManager;

/**
 * 外设业务逻辑
 * @author zhangyajun
 *
 */
public interface IDeviceBiz {
	
	public String[] getDeviceNameAndPort(IDeviceManager deviceManager);
	
	public Map<String,String[]> getDeviceAllNameAndPort(IDeviceManager deviceManager);
	
	public String   getDeviceType();

	/**
	usage: ahadev
	  AHA DEVICE for JSRCCB which wrote by wujian.
	  Copyright 2011 Agree Tech., All rights reserved.
	  version: 0.1.3, Mar 21 2016 14:38:53
	  For report bugs, Please mail 'wujian@agree.com.cn'.
	options:
	  -v,--version           show version.
	  -V,--libversion        show library version.
	  -h,--help              show this usage message.
	  -d,--device=<val>      device name, must set.
	  -c,--company=<val>     company name, must set.
	  -t,--termtype=<val>    terminal type, must set.	图形服务端默认1，客户端默认0
	  -n,--ttyname=<val>     tty name, maybe set.		图形无用，不带该参数
	  -a,--auxport=<val>     aux port, must set.		图形服务端默认0，客户端为串口号
	  -T,--timeout=<val>     timeout, maybe set, default 30(s).
	  -f,--funcid=<val>      function id, maybe set, default 1.
	  -p,--param=<val>       function parameter, maybe set.
	  -~,--debug             debug mode
	  -&,--debugbin          debug mode: biniary data
	**/
	/**
	 * 执行服务端AHADEV
	 * 
	 * @return
	 */
	public String runLocalDev (String device, String company, int termtype, int timeout, int funcId, String... paras);
	/**
	 * 执行客户端AHADEV
	 * 
	 * @return
	 */
	public String runClientDev(IDeviceManager deviceManager, String device, int timeout, int funcId, String... paras);
	/**
	 * 执行客户端AHADEV
	 * 
	 * @return
	 */
	public String runClientDev(IDeviceManager deviceManager, String device, String company, int comNo, int timeout, int funcId, String... paras);
}
