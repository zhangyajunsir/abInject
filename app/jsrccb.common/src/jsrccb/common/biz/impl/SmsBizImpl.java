package jsrccb.common.biz.impl;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.rpc.communication.CommClientProvider;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IPin;
import cn.com.agree.ab.trade.core.tools.FileUtil;
import cn.com.agree.ab.trade.core.tools.MathUtil;
import cn.com.agree.ab.trade.core.tools.StringUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;
import jsrccb.common.biz.SmsBiz;

@AutoBindMapper(baseClass = SmsBiz.class)
@Singleton
@Biz("smsBiz")
public class SmsBizImpl implements SmsBiz {
	private static final Logger logger = LoggerFactory
			.getLogger(SmsBizImpl.class);

	@Inject
	private CommClientProvider commClientProvider;

	@Override
	public String sendValidateMsg(Trade trade, String phoneNumber,
			String userName) {
		// 1. 检查手机号是否正确
		boolean phoneBoo = checkPhone(phoneNumber);
		if (!phoneBoo) {
			// 手机号不正确
			throw new BasicRuntimeException("手机号码不正确，请重新输入!");
		}

		// 2. 随机生成6位校验码
		String validateCode = createRandomVcode();
		
		// 3. 按照短信系统模板传给短信平台 (此接口数据只是测试,已测试通过)
		String smsDefcode ="6666";  //交易码  4位
		String smsQbr = trade.getTellerInfo().get(ITradeKeys.G_QBR).toString()+ " ";   // 补齐10位
		String smsTeller = "11110000";  //柜员号
		String customIp = "66.3.44.46";   /// F5地址
		customIp = StringUtil.fixFill(customIp, " ", -35);
		String smsDate = "        ";   //短信日期
		String smsTime = "        ";   //短信时间 
		String smsMode = "000020200003 "; //模板编号  (短信平台生成)
		String smsWJCode   = "        ";   //问卷码
		String smsName = userName;   //姓名 60位
		smsName = StringUtil.fixFill(smsName, " ", -60);
		String smsCh = "    ";  //称 呼
		String iPhoneN = phoneNumber;   //手机号码
		iPhoneN = StringUtil.fixFill(iPhoneN, " ", -15);
		String WjDate =  MathUtil.addBigDecimal( trade.getTellerInfo().get(ITradeKeys.G_DATE).toString(), "1");  //问卷有效期 当前日期+1
		String contextT = userName + "@|@" + validateCode+"@|@"+DateUtil.getCurZhCNDateTimeMinte();
		contextT = StringUtil.fixFill(contextT, " ", -280);  // 发送内容
		String context = "";
		context = context + smsDefcode + smsQbr + smsTeller + customIp + smsDate + smsTime + smsMode;
		context = context + smsWJCode + smsName + smsCh + iPhoneN + WjDate + contextT;
		// 总报文长度放到传送内容最前面，共4位
		int intLeng = context.length();
		String strLeng = String.valueOf(intLeng);
		context = StringUtil.fixFill(strLeng, "0", 4) + context;
		
//		String context = userName + "先生/女士：您的验证码是" + validateCode + ",您在";
//		context = context + DateUtil.getCurZhCNDateTimeMinte()
//				+ "在我行新建/修改您的客户资料信息。切勿泄露验证码。【江苏农信】";

		// 4. socket通讯
		String sendResult = null;
		try {
			sendResult = exchange(trade, context);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sendResult;
	}

	/**
	 * 与短信平台进行sokcet通讯
	 * 
	 * @param trade
	 * @param context
	 *            发送内容
	 * @return
	 * @throws IOException
	 */
	public String exchange(Trade trade, String context) {
		byte[] input = null;
		String outPutResult = null;
		try {
			input = context.getBytes("GBK");
			byte[] ouput = commClientProvider.transform("03", trade
					.getTellerInfo().get(ITradeKeys.G_QBR).toString(), "60",
					input);
			outPutResult = new String(ouput);
			trade.pushInfo(outPutResult, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outPutResult;
	}

	@Override
	public String readValidate(Trade trade) {
		// 1.调密码器，明文(机构签到)
		IPin pinDev = trade.getDeviceManager().getPin();
		String checkPassword = "";
		try {
			checkPassword = pinDev.readOnce(new String[] { "", "", "", "" });
		} catch (IOException e) {
			throw new BizException(e);
		}
		// 2.检查是不是6位
		// 3.把明文返回交易，让交易进行检查是否一样。
		return checkPassword.trim();
	}

	private boolean checkPhone(String phones) {
		if (phones == null || "".equals(phones)) {
			return false;
		}

		boolean flag = false;
		try {
			Pattern regex = Pattern.compile("^1\\d{10}$");
			Matcher matcher = regex.matcher(phones);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/*
	 * 随机生成6位验证码
	 */
	private String createRandomVcode() {
		// 验证码
		String vcode = "";
		for (int i = 0; i < 6; i++) {
			vcode = vcode + (int) (Math.random() * 9);
		}
		return vcode;
	}

}
