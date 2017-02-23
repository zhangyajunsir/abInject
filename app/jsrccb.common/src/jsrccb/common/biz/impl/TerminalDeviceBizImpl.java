package jsrccb.common.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.TerminalDeviceBiz;
import jsrccb.common.dao.TerminalDeviceDao;
import jsrccb.common.dao.entity.TerminalDeviceEntity;
import jsrccb.common.dm.TerminalDeviceDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;


@AutoBindMapper(baseClass = TerminalDeviceBiz.class)
@Singleton
@Biz("terminalDeviceBiz")
public class TerminalDeviceBizImpl implements TerminalDeviceBiz {
	
	@Inject
	@Named("terminalDeviceDao")
	private TerminalDeviceDao terminalDeviceDao;

	@Override
	public List<TerminalDeviceDM> findTerminalDevice(String termLtty) {
		List<TerminalDeviceEntity> terminalDeviceEntitys = terminalDeviceDao.findTerminalDevice(termLtty);
		if (terminalDeviceEntitys == null || terminalDeviceEntitys.size() == 0) {
			return null;
		}
		List<TerminalDeviceDM> terminalDeviceDMs = new ArrayList<TerminalDeviceDM>();
		for (TerminalDeviceEntity terminalDeviceEntity : terminalDeviceEntitys) {
			TerminalDeviceDM terminalDeviceDM = new TerminalDeviceDM();
			terminalDeviceDM.setTermLtty(terminalDeviceEntity.getTermLtty()   == null ? null : terminalDeviceEntity.getTermLtty().trim());
			terminalDeviceDM.setTermType(terminalDeviceEntity.getTermType()   == null ? null : terminalDeviceEntity.getTermType().trim());
			terminalDeviceDM.setTermValue(terminalDeviceEntity.getTermValue() == null ? null : terminalDeviceEntity.getTermValue().trim());
			terminalDeviceDMs.add(terminalDeviceDM);
		}
		
		return terminalDeviceDMs;
	}

	
	/**
	 * 暂不做缓存；后期添加变更方法清除缓存，同时添加该方法进行缓存。
	 */
	@Override
	public TerminalDeviceDM findTerminalDevice(String termLtty, String termType) {
		TerminalDeviceEntity terminalDeviceEntity = terminalDeviceDao.findTerminalDevice(termLtty, termType);
		if (terminalDeviceEntity == null) 
			return null;
		TerminalDeviceDM terminalDeviceDM = new TerminalDeviceDM();
		terminalDeviceDM.setTermLtty(terminalDeviceEntity.getTermLtty()   == null ? null : terminalDeviceEntity.getTermLtty().trim());
		terminalDeviceDM.setTermType(terminalDeviceEntity.getTermType()   == null ? null : terminalDeviceEntity.getTermType().trim());
		terminalDeviceDM.setTermValue(terminalDeviceEntity.getTermValue() == null ? null : terminalDeviceEntity.getTermValue().trim());
		return terminalDeviceDM;
	}

	@Override
	public List<String> specialTerminalTeller(String ip) {
		return terminalDeviceDao.specialTerminalTeller(ip);
	}

}
