package jsrccb.common.dao;

import java.util.List;

import jsrccb.common.dao.entity.TerminalDeviceEntity;
import cn.com.agree.ab.lib.dao.EntityDao;

public interface TerminalDeviceDao extends EntityDao<TerminalDeviceEntity> {
	
	public List<TerminalDeviceEntity> findTerminalDevice(String termLtty);
	
	public TerminalDeviceEntity findTerminalDevice(String termLtty, String termType);
	
	public List<String> specialTerminalTeller(String ip);

}
