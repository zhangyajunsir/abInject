package cn.com.agree.ab.common.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import cn.com.agree.ab.common.biz.ABServerBiz;
import cn.com.agree.ab.common.dao.ABServerDao;
import cn.com.agree.ab.common.dao.entity.ABServerEntity;
import cn.com.agree.ab.common.dm.ABServerDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ABServerBiz.class)
@Singleton
@Biz("abserverBiz")
public class ABServerBizImpl implements ABServerBiz {
	@Inject
	@Named("abserverDao")
	private ABServerDao abserverDao;

	@Override
	@Cacheable
	public ABServerDM findABServer(String hostName) {
		ABServerEntity abserverEntity = abserverDao.findABServer(hostName);
		if (abserverEntity == null)
			return null;
		ABServerDM abserverDM = new ABServerDM();
		abserverDM.cloneValueFrom(abserverEntity);
		return abserverDM;
	}

	@Override
	public List<ABServerDM> getAllABServer() {
		List<ABServerEntity> abserverEntitys = abserverDao.queryAllABServer();
		if (abserverEntitys == null)
			return null;
		List<ABServerDM> abserverDMs = new ArrayList<ABServerDM>();
		for (ABServerEntity abserverEntity : abserverEntitys) {
			ABServerDM abserverDM = new ABServerDM();
			abserverDM.cloneValueFrom(abserverEntity);
			abserverDMs.add(abserverDM);
		}
		return abserverDMs;
	}

}
