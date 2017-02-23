package jsrccb.common.biz.impl.dev;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.dev.PinBiz;
import jsrccb.common.dao.PinKeyDao;
import jsrccb.common.dao.entity.PinKeyEntity;
import jsrccb.common.dm.PinDM;
import jsrccb.common.dm.PinKeyDM;
import jsrccb.common.utils.VerifyDataUtil;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.impl.AbstractDeviceBiz;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IPin;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = PinBiz.class)
@Singleton
@Biz("pinBiz")
public class PinBizImpl extends AbstractDeviceBiz implements PinBiz {
	@Inject
	@Named("pinKeyDao")
	private PinKeyDao pinKeyDao;

	@Override
	public String getDeviceType() {
		return IPin.TYPE;
	}

	@Override
	public String encryptPin(String pinMainKey, String pinWorkKey,String account, String passwd) {
		String pinBlock = null;
		try {
			pinBlock = runLocalDev("libhsm", "keyou", 1, 30, 2, pinMainKey,pinWorkKey, passwd, account);
		} catch (DevException de) {
			throw new BizException("加密密码失败!", de);
		}
		return pinBlock;
	}

	/**
	 * 设备ID：软加密时，为机构号；硬加密时，为设备ID
	 */
	@Override
	public PinKeyDM syncPinKey(String devID) {
		PinKeyEntity pinKeyEntity = pinKeyDao.getPinKey(devID);
		if (pinKeyEntity == null)
			throw new BizException("库里没有[" + devID + "]记录!");

		String pinKey = null;
		try {
			pinKey = runLocalDev("libhsm", "keyou", 1, 30, 1, devID);
		} catch (DevException de) {
			throw new BizException("同步密钥失败!", de);
		}
		// 更新数据库，新增数据库需要运维完成
		pinKeyEntity.setWorkKey(pinKey.substring(0, 32));
		pinKeyDao.updatePinKey(pinKeyEntity);
		//
		PinKeyDM pinKeyDM = new PinKeyDM();
		pinKeyDM.cloneValueFrom(pinKeyEntity);
		return pinKeyDM;
	}

	/**
	 * 设备ID：软加密时，为机构号；硬加密时，为设备ID
	 */
	@Override
	public PinKeyDM queryPinKey(String devID) {
		PinKeyEntity pinKeyEntity = pinKeyDao.getPinKey(devID);
		if (pinKeyEntity == null)
			return null;
		PinKeyDM pinKeyDM = new PinKeyDM();
		pinKeyDM.cloneValueFrom(pinKeyEntity);
		return pinKeyDM;
	}

	/**
	 * account 账号 must 密码是否必输 count 输入次数 check 是否进行简单密码校验
	 */
	@Override
	public PinDM readPasswd(Trade trade, String account, boolean must,int count, boolean check) {
		if (!must) {
			if (!trade.isUserConfirmed("是否输入密码?")) {
				return null;
			}
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> tellerInfo = trade.getTellerInfo();
		String mainKey = (String)tellerInfo.get(ITradeKeys.G_MAIN_PWDKEY);
		String workKey = (String)tellerInfo.get(ITradeKeys.G_WORK_PWDKEY);
		String[] nameAndPort = getDeviceNameAndPort(trade.getDeviceManager());
		
		PinDM pinDM = new PinDM();
		pinDM.setDevId  ((String)tellerInfo.get(ITradeKeys.G_DEV_ID));
		pinDM.setGmDevId((String)tellerInfo.get(ITradeKeys.G_DEV_ID_GM));
		pinDM.setMainKey(mainKey);
		pinDM.setWorkKey(workKey);
		
		int time = 0;
		times: 
		while (true) {
				time = time + 1;
				if (time > 2 && !trade.isUserConfirmed("您已经输入" + (time - 1) + "次密码了,是否重新输入")) {
					trade.exit(0);
				}
				try {
				if (time == 2) {
					trade.pushInfo("请重新输入密码！", true);
				}
				String lastPin = null;
				for (int i = 0; i < count; i++) {
					String pinBlock = null;
					if (nameAndPort.length > 0 && !"JsrccbPin".equals(nameAndPort[0])) {// 如果配置是国密
						// 6、密码键盘获取 密文密码
						String result6 = runClientDev(trade.getDeviceManager(),"libhardencpin", 40, 4, account);
						pinDM.setGM32MW(result6);
						if (check) {
							// 简单密码校验 国密密文 
							String simplePWD = runLocalDev("libhardencpin", "keyou", 0, 30, 3, (String)tellerInfo.get(ITradeKeys.G_DEV_ID_GM), account, "0000000000000000", result6, "gt.ruomi.zpk");
							if (!VerifyDataUtil.simplePWDCheck_GM(simplePWD)) {
								trade.pushInfo("密码过于简单，请重新输入", true);
								continue times;
							}
						}
						// 7、转PIN
						pinBlock = runLocalDev("libhardencpin", "keyou", 0, 30, 3,  (String)tellerInfo.get(ITradeKeys.G_DEV_ID_GM), account, account, result6, "01.GTTOCBOD.zpk");
					} else {//国际方式获取密码并加密
						IPin pinDev = trade.getDeviceManager().getPin();
						String passwd = pinDev.readOnce(new String[] {}).trim();
						if (passwd.trim().length() != 6) {
							trade.pushInfo("交易密码只能为6位!", true);
							continue times;
						}
						if (check) {
							// 简单密码校验 国际明文 
							if (!VerifyDataUtil.simplePWDCheck(passwd)) {
								trade.pushInfo("密码过于简单，请重新输入", true);
								continue times;
							}
						}
						pinBlock = encryptPin(mainKey, workKey, account, passwd);
					}
					if (lastPin != null && !lastPin.equals(pinBlock)) {
						trade.pushInfo("两次密码输入不一致,请重新输入", true);
						continue times;
					}
					lastPin = pinBlock;
				}
				pinDM.setGG16MW(lastPin);
				return pinDM;
			} catch (IOException e) {
				continue times;
			} catch (Exception e) {
				throw new BizException(e.getMessage());
			}
		}
	}

	/**
	 * 国密方式进行主密钥和工作密钥的获取和写入
	 */
	public PinKeyDM queryPinKey(Trade trade) {
		String mainKey = "";
		String workKey = "";
		PinKeyDM pinKeyDM = null;
		try {
			String[] nameAndPort = getDeviceNameAndPort(trade.getDeviceManager());
			if (nameAndPort.length > 0 && !"JsrccbPin".equals(nameAndPort[0])) {// 如果配置是国密
				//1. 获取设备id
				String result1 = runClientDev(trade.getDeviceManager(),"libhardencpin", 30, 1);
				String[]  info = result1.split("\\|", 2);
				String   devId = "JSNX." + info[0] + ".zpk";
				if ("0".equals(info[1])) {
					// 2.从加密平台获取主密钥
					String resultM = runLocalDev("libhardencpin", "keyou", 0, 30, 1, "JSNX."+info[0]+".zmk", "gt.JSNX.zmk");
					if (resultM != null && resultM.length() == 48) {
						// 3.将主密钥写入密码键盘
						mainKey = resultM.substring(0, 32);
						resultM = runClientDev(trade.getDeviceManager(),"libhardencpin", 30, 2, resultM);
					}
				}
				// 4.从加密平台获取工作密钥
				String resultW = runLocalDev("libhardencpin", "keyou", 0, 30, 2, devId, "JSNX."+info[0]+".zmk");
				if (resultW != null && resultW.length() == 48) {
					// 5、将工作密钥写入密码键盘
					workKey = resultW.substring(0, 32);
					resultW = runClientDev(trade.getDeviceManager(),"libhardencpin", 30, 3, resultW);
				}
				pinKeyDM = new PinKeyDM();
				pinKeyDM.setDevId("01.GTTOCBOD.zpk");
				pinKeyDM.setGmDevId(devId);
				pinKeyDM.setMainKey(mainKey);
				pinKeyDM.setWorkKey(workKey);
			}
		} catch (Exception e) {
			throw new BizException(e.getMessage());
		}
		return pinKeyDM;

	}

}
