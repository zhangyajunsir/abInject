package jsrccb.common.biz.impl.dev;

import java.io.IOException;

import javax.inject.Singleton;

import jsrccb.common.biz.dev.IDCardBiz;
import jsrccb.common.dm.dev.IDCardDM;

import cn.com.agree.ab.common.biz.impl.AbstractDeviceBiz;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IIdr;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = IDCardBiz.class)
@Singleton
@Biz("idCardBiz")
public class IDCardBizImpl extends AbstractDeviceBiz implements IDCardBiz {

	@Override
	public String getDeviceType() {
		return IIdr.TYPE;
	}

	@Override
	public IDCardDM read(Trade trade, boolean must) {
		if (!must) {
			if (!trade.isUserConfirmed("是否扫描二代身份证?")) {
				return null;
			}
		}
		int time = 0;
		String result = null;
		while (true) {
			try {
				String msgId = trade.pushInfoWithoutButton("请扫描二代身份证.....");
				boolean ok = true;
				time++;
				try {
					result = runClientDev(trade.getDeviceManager(), "libidcread", 30, 1);
				} catch (DevException e) {
					ok = false;
				} finally {
					trade.closeInfo(msgId);
				}
				if (result == null || !ok) {
					if (time >= 3) {
						trade.showError("扫描二代身份证已3次，请退出！");
						trade.exit(0);
					}
					if (!trade.isUserConfirmed("扫描二代身份证失败，是否重读")) {
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
		// #姓名|性别|民族|出生年月日|住址|公民身份号码|签发机关|有效期限|照片字符串
		String[] info = result.split("\\|", 10);
		/** TODO 信息展示，采用DomainDialog方式
		Map<String,String> input = new HashMap<String,String>();
		input.put("#NAME", info[0]);
		input.put("#SEX", info[1]);
		input.put("#NATION", info[2]);
		input.put("#BIRTHDAY", info[3]);
		input.put("#ADD", info[4]);
		input.put("#ID", info[5]);
		input.put("#JIGOU", info[6]);
		input.put("#QIXIAN", info[7]);
		input.put("#PHOTO", info[8]);
		Map RetMap = trade.syncOpenTrade(lib.trade.IdCard.IdCard.class.getName(), "身份证信息", input, new String[0], Trade.OPEN_TRADE_STYLE_WINDOW);
		if (RetMap == null) {
			return null;
		}
		*/
		IDCardDM idCardDM = new IDCardDM();
		idCardDM.setName    (info[0]);
		idCardDM.setSex     (info[1]);
		idCardDM.setNation  (info[2]);
		idCardDM.setBirthday(info[3]);
		idCardDM.setAddress (info[4]);
		idCardDM.setIdNo    (info[5]);
		idCardDM.setOrg     (info[6]);
		idCardDM.setIneffectiveDate(info[7]);
		idCardDM.setPhoto   (info[8]);
		return idCardDM;
	}
	
	

}
