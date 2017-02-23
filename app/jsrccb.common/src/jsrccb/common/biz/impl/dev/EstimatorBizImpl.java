package jsrccb.common.biz.impl.dev;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import jsrccb.common.biz.dev.EstimatorBiz;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.impl.AbstractDeviceBiz;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IEstimate;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = EstimatorBiz.class)
@Singleton
@Biz("estimatorBiz")
public class EstimatorBizImpl extends AbstractDeviceBiz implements EstimatorBiz{
	@Inject
	private ConfigManager configManager;

	@Override
	public String getDeviceType() {
		return IEstimate.TYPE;
	}

	@SuppressWarnings("unused")
	@Override
	public void assess(Trade trade, boolean must) {
		String estimateCode = getEstimateCode(trade.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID).toString(), trade.getStoreData(ITradeKeys.T_TRADE_CODE));
		if (estimateCode == null) {
			return;
		}
		if (!must) {
			if (!trade.isUserConfirmed("是否进行评价?")) {
				return;
			}
		}
		String estimateHost = configManager.getUtilIni().getValue("ESTIMATOR.IP");
		String estimatePort = configManager.getUtilIni().getValue("ESTIMATOR.PORT");
		String estimateInfo = "1200";
		String[] nameAndPort = getDeviceNameAndPort(trade.getDeviceManager());
		if (nameAndPort[0].indexOf("9600") > 0) {
			estimateInfo = "9600";
		}
		estimateInfo = estimateInfo+"|"+estimateHost+"|"+estimatePort+"|"+trade.getTellerInfo().get(ITradeKeys.G_TELLER)+"|"+estimateCode+"|"+trade.getTellerInfo().get(ITradeKeys.G_QBR);
		String result = null;
		try {
			String msgId = trade.pushInfoWithoutButton("调用评价器中.....");
			boolean ok = true;
			try {
				result = runClientDev(trade.getDeviceManager(), "libestimate", "hejia", Integer.valueOf(nameAndPort[1].substring(3, 4)), 30, 1, estimateInfo);
			} catch (DevException e) {
				ok = false;
			} finally {
				trade.closeInfo(msgId);
			}
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

	private String getEstimateCode(String LVL_BRH_ID, String tradeCode) {
		// TODO 读取配置，注意缓存
		
		return null;
	}

}
