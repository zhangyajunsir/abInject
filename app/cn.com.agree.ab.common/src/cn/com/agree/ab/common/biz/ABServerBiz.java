package cn.com.agree.ab.common.biz;

import java.util.List;

import cn.com.agree.ab.common.dm.ABServerDM;

public interface ABServerBiz {

	public ABServerDM findABServer(String hostName);
	
	public List<ABServerDM> getAllABServer();
}
