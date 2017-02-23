package jsrccb.common.biz;

import java.util.List;

import jsrccb.common.dm.TerminalDeviceDM;

public interface TerminalDeviceBiz {
	
	public List<TerminalDeviceDM> findTerminalDevice(String termLtty);
	
	public TerminalDeviceDM findTerminalDevice(String termLtty, String termType); 
	
	public List<String> specialTerminalTeller(String ip);
}
