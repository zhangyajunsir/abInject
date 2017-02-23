package jsrccb.common.biz.impl.dev;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.dev.OutClearBiz;
import jsrccb.common.dao.OldDao;

import cn.com.agree.ab.common.biz.impl.AbstractDeviceBiz;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IOutClear;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = OutClearBiz.class)
@Singleton
@Biz("outClearBiz")
public class OutClearBizImpl extends AbstractDeviceBiz implements OutClearBiz {
	@Inject
	@Named("oldDao")
	private OldDao oldDao;

	@Override
	public String getDeviceType() {
		return IOutClear.TYPE;
	}

	@Override
	public boolean displayWithButton(Trade trade, boolean must,	String... displayInfos) {
		boolean confirmFlag = false;
		
		int time = 0;
		String result = null;
		String sMsgWndName = "";
		while (true) {
			try {
				if (time >= 3) {
					trade.isUserConfirmed("调用柜外清失败次数超过3次，退出当前交易!");
					trade.exit(0);
				}
				sMsgWndName = trade.pushInfoWithoutButton("调用柜外清中.....");
				time++;
				result = runClientDev(trade.getDeviceManager(), "libanlitong", 30, 1, displayInfos);
				trade.pushInfo("result : "+result, true);
				trade.closeInfo(sMsgWndName);
				if (result == null) {
					confirmFlag = trade.isUserConfirmed("调用柜外清(数据返回)失败，是否重新调用");
					if (!confirmFlag) {
						trade.exit(0);
					} else
						continue;
				} else {
					String[] info = result.split("\\|", 10);

					if ("0".equals(info[1].trim())) {
						return true; // 确认
					} else if ("1".equals(info[1].trim())) {
						return false; // 取消
					} else if ("2".equals(info[1].trim())) {
						return false; // 其他情况
					} else if ("3".equals(info[1].trim())) {
						return false; // 未互动
					} else {
						return false; // 未互动
					}
				}
			} catch (IOException e) {
					try {
						trade.closeInfo(sMsgWndName);
						confirmFlag = trade.isUserConfirmed("调用柜外清失败，是否重新调用");
						if (!confirmFlag) {
							trade.exit(0);
						} else
						continue;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
			break;
		}
		return false;
	}

	@Override
	public void displayNoButton(Trade trade, boolean must,
			String... displayInfos) {
	
		boolean confirmFlag = true;
		int time = 0;
		String result = null;
		String sMsgWndName = "";
		while (true) {
			try {
				if (time >= 3) {
					trade.isUserConfirmed("调用柜外清失败次数超过3次，退出当前交易!");
					trade.exit(0);
				}
				sMsgWndName = trade.pushInfoWithoutButton("调用柜外清中.....");
				time++;
				try {
					result = runClientDev(trade.getDeviceManager(), "libanlitong", 30, 2, displayInfos);
				} catch (DevException e) {
					confirmFlag = false;
				} finally {
					trade.closeInfo(sMsgWndName);
				}
				if (result == null || !confirmFlag) {
					trade.pushError("result is empty!!!",true);
					confirmFlag = trade.isUserConfirmed("调用柜外清失败，是否重新调用");
					if (!confirmFlag) {
						trade.exit(0);
					}
					continue;
				} 
			} catch (IOException e) {
				confirmFlag = trade.isUserConfirmed("调用柜外清失败，是否重新调用");
				if (!confirmFlag) {
						trade.exit(0);
				}
				continue;
			}
			break;
		}
		return;
	}

	@Override
	public OutClearConfig getOutClearConfig(String LVL_BRH_ID, String BRANCH) {
		OutClearConfig outClearConfig = new OutClearConfig();
		outClearConfig.setConfigSwitch(oldDao.outClearSwitch(LVL_BRH_ID, BRANCH));
		if (outClearConfig.isConfigSwitch()){
			String amount = oldDao.outClearAmount(LVL_BRH_ID);
			if (amount == null || "".equals(amount))
				// 省配置 
				amount = oldDao.outClearAmount("000");
			outClearConfig.setAmount(amount);
		}
		return outClearConfig;
	}

}
