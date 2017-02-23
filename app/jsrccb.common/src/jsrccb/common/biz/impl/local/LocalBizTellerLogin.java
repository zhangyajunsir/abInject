package jsrccb.common.biz.impl.local;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.OldBiz;
import jsrccb.common.biz.dev.OutClearBiz;
import jsrccb.common.biz.dev.OutClearBiz.OutClearConfig;
import jsrccb.common.biz.dev.PinBiz;
import jsrccb.common.exception.PwdException;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.CommBizProvider;
import cn.com.agree.ab.common.biz.impl.AbstractLocalBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.dm.PacketReqDM;
import cn.com.agree.ab.common.dm.PacketRspDM;
import cn.com.agree.ab.common.utils.ObjectMergeUtil;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractLocalBiz.class, multiple = true)
@Singleton
@Biz("tellerLoginLocalBiz") 
public class LocalBizTellerLogin extends AbstractLocalBiz {
	@Inject
	protected CommBizProvider commBizProvider;
	@Inject
	@Named("pinBiz")
	protected PinBiz pinBiz;
	@Inject
	@Named("outClearBiz")
	protected OutClearBiz outClearBiz;
	@Inject
	@Named("oldBiz")
	protected OldBiz oldBiz;

	@SuppressWarnings("rawtypes")
	@Override
	public PacketRspDM exchange(CommCodeDM commCodeDM, PacketReqDM packetReqDM) {
		PacketRspDM packetRspDM    = PacketRspDM.newInstance();

		// 1.CM0043501
		PacketRspDM CM0043501RspDM = commBizProvider.commCommit("CM0043501", packetReqDM);
		// 2.CM0003000
		PacketRspDM CM0003000RspDM = commBizProvider.commCommit("CM0003000", packetReqDM);
		if (CM0003000RspDM.getMsgList() != null) {
			for (Object msg : CM0003000RspDM.getMsgList()) {
				Map msgMap = (Map)msg;
				if (msgMap.get("msgCode").equals("WE007")) {
					PwdException rpcException = new PwdException(ExceptionLevel.WARN, "密码过期");
					throw rpcException;
				}
			}
		}
		CM0003000RspDM.getTellerInfo().put(ITradeKeys.G_SCREEN_PWD, packetReqDM.getStoreData().get("T_TELLER_PASSWORD"));
		CM0003000RspDM.getTellerInfo().put(ITradeKeys.G_SCREEN_SOURCE_PWD, packetReqDM.getUiData().get("text_teller_password|text"));
		// 3.CM0003302 忽略
		// 4.CM0000802
		PacketRspDM CM0000802RspDM = commBizProvider.commCommit("CM0000802", packetReqDM);
		packetReqDM.getTellerInfo().put(ITradeKeys.G_LVL_BRH_ID, CM0000802RspDM.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID));
//		// 5.加密方式的获取放LoginTradeEventBizImpl里实现
		// 6.村镇银行控制标志
		PacketRspDM CM0011802RspDM = commBizProvider.commCommit("CM0011802", packetReqDM);
		String lgrpType = CM0011802RspDM.getTempArea().get("LGPR_TYPE") == null||"".equals(CM0011802RspDM.getTempArea().get("LGPR_TYPE")) ? "" : CM0011802RspDM.getTempArea().get("LGPR_TYPE").toString().substring(0, 1);
		if ("2".equals(lgrpType)) {
			CM0011802RspDM.getTellerInfo().put("G_TOWN_BANK", "2");
		} else {
			CM0011802RspDM.getTellerInfo().put("G_TOWN_BANK", "0");
		}
		// 7.获取安理通开关，已无用
		// 8.获取柜外清开关
		OutClearConfig outClearConfig = outClearBiz.getOutClearConfig((String)packetReqDM.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID), (String)packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR));
		packetRspDM.getTellerInfo().put("G_SMARTPIN_FLAG",   outClearConfig.isConfigSwitch() ? "1" : "0");
		packetRspDM.getTellerInfo().put("G_SMARTPIN_AMOUNT", outClearConfig.getAmount() == null || "".equals(outClearConfig.getAmount()) ? "0" : outClearConfig.getAmount());
		// 9.获取冠字号开关
		boolean crownWordNumberSwitch = oldBiz.crownWordNumberSwitch((String)packetReqDM.getTellerInfo().get(ITradeKeys.G_LVL_BRH_ID), (String)packetReqDM.getTellerInfo().get(ITradeKeys.G_QBR));
		packetRspDM.getTellerInfo().put("G_GZH_FLAG",        crownWordNumberSwitch ? "1" : "0");
		// 合并结果
		ObjectMergeUtil.merge(packetRspDM, CM0003000RspDM, CM0043501RspDM, CM0000802RspDM);
		// 指纹标识
		Boolean fingerFlag = (Boolean)packetReqDM.getContext().get("fingerFlag");
		if (fingerFlag)
			packetRspDM.getTellerInfo().put("G_LOGIN_TYPE", "0");
		else
			packetRspDM.getTellerInfo().put("G_LOGIN_TYPE", "1");
		return packetRspDM;
	}

	@Override
	public String commCode() {
		return "teller_login";
	}

}
