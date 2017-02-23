package jsrccb.common.biz.impl.dev;

import java.io.IOException;

import javax.inject.Singleton;

import jsrccb.common.biz.dev.FingerBiz;

import cn.com.agree.ab.common.biz.impl.AbstractDeviceBiz;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IFp;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = FingerBiz.class)
@Singleton
@Biz("fingerBiz")
public class FingerBizImpl extends AbstractDeviceBiz implements FingerBiz {

	@Override
	public String getDeviceType() {
		return IFp.TYPE;
	}
	
	@Override
	public boolean checkTellerFinger  (Trade trade, boolean must, String... param) {
		if (!must) {
			if (!trade.isUserConfirmed("是否校验柜员指纹仪?")) {
				return true;
			}
		}
		String fingerData = readFinger(trade, true);
		if (fingerData == null)
			return false;
		StringBuffer checkData = new StringBuffer();
		for (String para : param) {
			checkData.append(para).append("|");
		}
		checkData.append(fingerData);
		String result = null;
		try {
			String msgId = trade.pushInfoWithoutButton("正在校验指纹信息.....");
			String errorMessage = null;
			try {
				// 校验时，串口号随意
				result = runLocalDev("libfingerreader", "tiancheng", 0, 30, 3, checkData.toString());
			} catch (DevException e) {
				errorMessage = e.getMessage();
			} finally {
				trade.closeInfo(msgId);
			}
			if (result == null || errorMessage != null) {
				trade.pushError("指纹信息校验失败:"+errorMessage, true);
				return false;
			}
		} catch (IOException e) {
			throw new BizException(e);
		}
		return true;
	}

	@Override
	public boolean checkCustomerFinger(Trade trade, boolean must, String... param) {
		if (!must) {
			if (!trade.isUserConfirmed("是否校验客户指纹仪?")) {
				return true;
			}
		}
		String fingerData = readFinger(trade, true);
		if (fingerData == null)
			return false;
		StringBuffer checkData = new StringBuffer();
		for (String para : param) {
			checkData.append(para).append("|");
		}
		checkData.append(fingerData);
		String result = null;
		try {
			String msgId = trade.pushInfoWithoutButton("正在校验指纹信息.....");
			boolean ok = true;
			try {
				// 校验时，串口号随意
				result = runLocalDev("libfingerreader", "tiancheng", 0, 30, 5, checkData.toString());
			} catch (DevException e) {
				ok = false;
			} finally {
				trade.closeInfo(msgId);
			}
			if (result == null || !ok) {
				trade.pushError("指纹信息校验失败", true);
				return false;
			}
		} catch (IOException e) {
			throw new BizException(e);
		}
		return true;
	}
	
	
	public String  readFinger(Trade trade, boolean must) {
		if (!must) {
			if (!trade.isUserConfirmed("是否读取指纹信息?")) {
				return null;
			}
		}
		int time = 0;
		String result = null;
		while (true) {
			try {
				String msgId = trade.pushInfoWithoutButton("请采集指纹信息.....");
				boolean ok = true;
				time++;
				try {
					result = runClientDev(trade.getDeviceManager(), "libfingerreader", 30, 1);
				} catch (DevException e) {
					ok = false;
				} finally {
					trade.closeInfo(msgId);
				}
				if (result == null || !ok) {
					if (time >= 3) {
						trade.showError("采集指纹信息已3次，请退出！");
						trade.exit(0);
					}
					if (!trade.isUserConfirmed("采集指纹信息失败，是否重采集?")) {
						if (must) {
							trade.exit(0);
						} else {
							return null;
						}
					} else
						continue;
				}
			} catch (IOException e) {
				throw new BizException(e);
			}
			break;
		}
		return result;
	}
	
}
