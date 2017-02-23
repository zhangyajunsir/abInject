package jsrccb.common.biz.impl.dev;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import jsrccb.common.biz.dev.MsfBiz;
import jsrccb.common.dm.dev.AccountDM;

import cn.com.agree.ab.common.biz.impl.AbstractDeviceBiz;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IMsf;
import cn.com.agree.ab.trade.core.tools.StringUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = MsfBiz.class)
@Singleton
@Biz("msfBiz")
public class MsfBizImpl  extends AbstractDeviceBiz implements MsfBiz {
	@Inject
	private ConfigManager configManager;
	
	@Override
	public String getDeviceType() {
		return IMsf.TYPE;
	}

	@Override
	public AccountDM readMSF3 (Trade trade, boolean must) {
		if (must && !"1".equals(configManager.getUtilIni().getValue("MSF.CRDMSF"))) {
			must = false;
		}
		if (!must) {
			if (!trade.isUserConfirmed("是否刷折输入?")) {
				return null;
			}
		}
		
		IMsf msf = trade.getDeviceManager().getMsf();
		int time = 0;
		String track3 = null;
		while (true) {
			try {
				String msgId = trade.pushInfoWithoutButton("请刷折.....");
				boolean ok = true;
				time++;
				try {
					track3 = msf.read(IMsf.MODE_TRACK3);
				} catch (IOException e) {
					ok = false;
				} finally {
					trade.closeInfo(msgId);
				}
				if (track3 == null || !ok) {
					if (time >= 3) {
						trade.showError("刷折已3次，请退出！");
						trade.exit(0);
					}
					if (!trade.isUserConfirmed("读存折磁条失败，是否重读")) {
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
		
		String[] track3Array = track3.split("=");
		if (track3Array.length < 4) {
			track3Array = track3.split(">");
			if (track3Array.length < 4) {
				track3Array = track3.split("'");
				if (track3Array.length < 4) {
					throw new BizException("磁条信息有误");
				}
			}
		}
		AccountDM accountDM = new AccountDM();
		accountDM.setAccount(track3Array[0]);
		accountDM.setBvCode (track3Array[1]);
		accountDM.setDvv    (track3Array[2]);
		accountDM.setBookNo (track3Array[3]);
		String accPayType = "";
		if (track3Array.length == 5) {
			accPayType = track3Array[4];
			if (accPayType.length() >= 5)
				accPayType = accPayType.substring(0, 4);
		}
		accountDM.setPayType(accPayType);
		return accountDM;
	}

	@Override
	public AccountDM readMSF23(Trade trade, boolean must) {
		if (must && !"1".equals(configManager.getUtilIni().getValue("MSF.CRDMSF"))) {
			must = false;
		}
		if (!must) {
			if (!trade.isUserConfirmed("是否刷磁卡输入?")) {
				return null;
			}
		}
		
		IMsf msf = trade.getDeviceManager().getMsf();
		int time = 0;
		String track23 = null;
		while (true) {
			try {
				String msgId = trade.pushInfoWithoutButton("请刷磁卡.....");
				boolean ok = true;
				time++;
				try {
					track23 = msf.read(IMsf.MODE_TRACK23);
				} catch (IOException e) {
					ok = false;
				} finally {
					trade.closeInfo(msgId);
				}
				if (track23 == null || track23.length() < 17 || track23.indexOf("=") <= 0 || !ok) {
					if (time >= 3) {
						trade.showError("刷磁卡已3次，请退出！");
						trade.exit(0);
					}
					if (!trade.isUserConfirmed("读磁卡失败，是否重读?")) {
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
		
		String cvv2 = "";
		String cvv3 = "";
		int iRet = track23.indexOf('$');
		if (iRet >= 0)
			track23 = track23.substring(0, iRet);
		String[] track23Array = track23.split("<");
		if (track23Array.length >= 1) {
			cvv2 = track23Array[0];
			if (track23Array.length >= 2) {
				cvv3 = track23Array[1];
			}
		}
		iRet = cvv2.indexOf('=');
		if (iRet < 0) {
			iRet = cvv2.indexOf('>');
			if (iRet < 0) {
				throw new BizException("磁道信息不合法");
			}
		}
		AccountDM accountDM = new AccountDM();
		accountDM.setAccount(cvv2.substring(0, iRet));
		accountDM.setIneffectiveDate(cvv2.substring(iRet + 1, iRet + 5));
		accountDM.setCvv ("");
		accountDM.setCvv2(cvv2);
		accountDM.setCvv3(cvv3);
		return accountDM;
	}

	@Override
	public AccountDM readTellerCard(Trade trade, boolean must) {
		if (must && !"1".equals(configManager.getUtilIni().getValue("MSF.TRLMSF"))) {
			must = false;
		}
		if (!must) {
			if (!trade.isUserConfirmed("是否刷柜员卡输入?")) {
				return null;
			}
		}
		
		IMsf msf = trade.getDeviceManager().getMsf();
		int time = 0;
		String track2 = null;
		while (true) {
			try {
				String msgId = trade.pushInfoWithoutButton("请刷柜员卡.....");
				boolean ok = true;
				time++;
				try {
					track2 = msf.read(IMsf.MODE_TRACK2);
				} catch (IOException e) {
					ok = false;
				} finally {
					trade.closeInfo(msgId);
				}
				if (!ok || track2 == null || track2.length() < 23) {
					if (time >= 3) {
						trade.showError("刷柜员卡已3次，请退出！");
						trade.exit(0);
					}
					if (!trade.isUserConfirmed("读柜员卡失败，是否重读")) {
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
		
		AccountDM accountDM = new AccountDM();
		accountDM.setAccount(track2.substring(0 , 12));
		accountDM.setBvCode (track2.substring(12, 20));
		accountDM.setBookNo (track2.substring(20, track2.length() - 3).replaceAll("<<", ""));
		return accountDM;
		
	}

	@Override
	public void writeMSF23(Trade trade, boolean must, AccountDM accountDM) {
		if (!must) {
			if (!trade.isUserConfirmed("是否写存折磁条数据?")) {
				return;
			}
		}
		String track3 = accountDM.getAccount().trim()+"="+accountDM.getBvCode().trim()+"="+accountDM.getDvv().trim()+"="+accountDM.getBookNo().trim()+"="+accountDM.getPayType().trim();
		String track2 = accountDM.getAccount().trim()+"="+accountDM.getBvCode().trim()+"="+accountDM.getBookNo().trim();
		int time = 0;
		IMsf msf = trade.getDeviceManager().getMsf();
		try {
			while (true) {
				String msgId = trade.pushInfoWithoutButton("请刷折.....");
				boolean ok = true;
				time++;
				try {
					msf.write(track2 + "A" + track3, IMsf.MODE_TRACK23);
				} catch (IOException e) {
					ok = false;
				} finally {
					trade.closeInfo(msgId);
				}
				if (!ok) {
					if (time >= 3) {
						trade.showError("写折已3次，请退出！");
						trade.exit(0);
					}
					if (!trade.isUserConfirmed("写存折磁条失败，是否重写?")) {
						return;
					} else {
						continue;
					}
				}
				break;
			}
			trade.pushInfo("写存折磁条数据成功", true);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}

	@Override
	public void writeTellerCard(Trade trade, boolean must, AccountDM accountDM) {
		if (!must) {
			if (!trade.isUserConfirmed("是否写柜员卡数据?")) {
				return;
			}
		}
		String cvv2 = accountDM.getAccount() + accountDM.getBvCode() + accountDM.getBookNo();
		cvv2 = cvv2 + accCheckNo(cvv2);
		int time = 0;
		IMsf msf = trade.getDeviceManager().getMsf();
		try {
			while (true) {
				String msgId = trade.pushInfoWithoutButton("请刷柜员卡.....");
				boolean ok = true;
				time++;
				try {
					msf.write(cvv2, IMsf.MODE_TRACK2);
				} catch (IOException e) {
					ok = false;
				} finally {
					trade.closeInfo(msgId);
				}
				if (!ok) {
					if (time >= 3) {
						trade.showError("写折已3次，请退出！");
						trade.exit(0);
					}
					if (!trade.isUserConfirmed("写柜员卡失败，是否重写?")) {
						return;
					} else {
						continue;
					}
				}
				break;
			}
			trade.pushInfo("写柜员卡数据成功", true);
		} catch (IOException e) {
			throw new BizException(e);
		}
	}
	
	
	private String accCheckNo(String sTrack) {
		char[] cTmp = sTrack.toCharArray();
		int iTotal = 0;

		/*
		 * if (cTmp.length < 33) { return null; }
		 */
		for (int curr = 0; curr < cTmp.length; curr++) {

			iTotal += cTmp[curr];
			if (iTotal > 255)
				iTotal -= 255;
		}
		if (iTotal < 79)
			iTotal = 79 - iTotal;
		else
			iTotal %= 79;

		String checkno = String.valueOf(iTotal);
		if (checkno.length() != 2)
			checkno = checkno + "0";
		return StringUtil.fixFill(checkno, "0", -3);
	}
}
