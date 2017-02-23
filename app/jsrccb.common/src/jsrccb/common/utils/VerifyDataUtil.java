package jsrccb.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.lib.config.ConfigLoad;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.inject.InjectPlugin;

@Singleton
public class VerifyDataUtil {
	private static final Logger	logger	= LoggerFactory.getLogger(VerifyDataUtil.class);
	
	/**
	 * 简单密码校验
	 * 
	 * @param trade
	 * @param passWord
	 *            密码明文
	 * @return
	 * @throws Exception
	 */
	public static boolean simplePWDCheck(String passWord) throws Exception {

		String passWordTmp = passWord.trim();
		// 转字符类型
		char[] strCahr = passWordTmp.toCharArray();
		// 临时数组
		String[] iTmp = new String[5];
		for (int i = 0; i < 5; i++) {
			// 转ascii
			iTmp[i] = Integer.valueOf(strCahr[i])
					- Integer.valueOf(strCahr[i + 1]) + "";
		}
		if (iTmp[0].equals(iTmp[1]) && iTmp[1].equals(iTmp[2])
				&& iTmp[2].equals(iTmp[3]) && iTmp[3].equals(iTmp[4])) {
			return false;
		}
		return true;
	}

	/**
	 * 简单密码校验
	 * 
	 * @param trade
	 * @param passWord
	 *            国密 密文
	 * @return
	 * @throws Exception
	 */
	public static boolean simplePWDCheck_GM(String passWord) throws Exception{
		ConfigManager configManager = InjectPlugin.getDefault().getInstance(ConfigManager.class);
		ConfigLoad<List<String>> cl = new ConfigLoad<List<String>>() {
			@Override
			public List<String> load(URL url, InputStream is) throws Exception {
				List<String>   list  = new ArrayList<String>();
				BufferedReader input = null;
				try {
					input = new BufferedReader(new InputStreamReader(is));
					String line = null;  
					while((line = input.readLine()) != null) {
						if (!"".equals(line.trim())){
							list.add(line.trim());
						}
					}
				} finally {
					if (input != null) input.close();
				}
				return list;
			}
		};
		
		List<String> list =  configManager.getConfigObject(configManager.getAbsConfigPath("/storage/simpencypnationpwd.ini"), cl);
		if (list.contains(passWord)) {
			return false;
		}
		return true;
	}
		
	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern regex = Pattern
					.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static String[] split(byte[] buff, int splitint) {
		int count = 0;
		for (int i = 0; i < buff.length; i++) {
			if (buff[i] == (byte) splitint) {
				count++;
			}
		}
		if (count == 0)
			return null;
		String[] splitbuff = new String[count];
		for (int i = 0, y = 0, z = 0; i < buff.length; i++) {
			if (buff[i] == (byte) splitint) {
				splitbuff[y] = new String(buff, z, i - z);
				z = i + 1;
				y++;
			}
		}
		return splitbuff;
	}


	/**
	 * 功能：身份证的有效验证
	 * 
	 * @param IDStr
	 *            身份证号
	 * @return 有效与否
	 */
	@SuppressWarnings({ "rawtypes" })
	public static boolean IdCardCheck(String IDStr) {
		String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
				"3", "2" };
		String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
				"9", "10", "5", "8", "4", "2" };
		String Ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (IDStr.length() != 15 && IDStr.length() != 18) {
			throw new BizException("身份证号码长度应该为15位或18位。");
		}
		// =======================(end)========================

		// ================ 数字 除最后以为都为数字 ================
		if (IDStr.length() == 18) {
			Ai = IDStr.substring(0, 17);
		} else if (IDStr.length() == 15) {
			Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		}
		if (isNumeric(Ai) == false) {
			throw new BizException("身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。");
		}
		// =======================(end)========================

		// ================ 出生年月是否有效 ================
		String strYear = Ai.substring(6, 10);// 年份
		String strMonth = Ai.substring(10, 12);// 月份
		String strDay = Ai.substring(12, 14);// 月份
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			throw new BizException("身份证生日无效。");
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
					|| (gc.getTime().getTime() - s.parse(
							strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				throw new BizException("身份证生日不在有效范围。");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			throw new BizException("身份证月份无效");
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			throw new BizException("身份证日期无效");
		}
		// =====================(end)=====================

		// ================ 地区码时候有效 ================
		Hashtable h = GetAreaCode();
		if (h.get(Ai.substring(0, 2)) == null) {
			throw new BizException("身份证地区编码错误。");
		}
		// ==============================================

		// ================ 判断最后一位的值 ================
		int TotalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			TotalmulAiWi = TotalmulAiWi
					+ Integer.parseInt(String.valueOf(Ai.charAt(i)))
					* Integer.parseInt(Wi[i]);
		}
		int modValue = TotalmulAiWi % 11;
		String strVerifyCode = ValCodeArr[modValue];
		Ai = Ai + strVerifyCode;

		if (IDStr.length() == 18) {
			if (Ai.equals(IDStr) == false) {
				throw new BizException("身份证无效，不是合法的身份证号码,检验码错误,应为"+strVerifyCode);
			}
		} else {
			return true;
		}
		// =====================(end)=====================
		return true;
	}

	/**
	 * 功能：设置地区编码
	 * 
	 * @return Hashtable 对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Hashtable GetAreaCode() {
		Hashtable hashtable = new Hashtable();
		hashtable.put("11", "北京");
		hashtable.put("12", "天津");
		hashtable.put("13", "河北");
		hashtable.put("14", "山西");
		hashtable.put("15", "内蒙古");
		hashtable.put("21", "辽宁");
		hashtable.put("22", "吉林");
		hashtable.put("23", "黑龙江");
		hashtable.put("31", "上海");
		hashtable.put("32", "江苏");
		hashtable.put("33", "浙江");
		hashtable.put("34", "安徽");
		hashtable.put("35", "福建");
		hashtable.put("36", "江西");
		hashtable.put("37", "山东");
		hashtable.put("41", "河南");
		hashtable.put("42", "湖北");
		hashtable.put("43", "湖南");
		hashtable.put("44", "广东");
		hashtable.put("45", "广西");
		hashtable.put("46", "海南");
		hashtable.put("50", "重庆");
		hashtable.put("51", "四川");
		hashtable.put("52", "贵州");
		hashtable.put("53", "云南");
		hashtable.put("54", "西藏");
		hashtable.put("61", "陕西");
		hashtable.put("62", "甘肃");
		hashtable.put("63", "青海");
		hashtable.put("64", "宁夏");
		hashtable.put("65", "新疆");
		hashtable.put("71", "台湾");
		hashtable.put("81", "香港");
		hashtable.put("82", "澳门");
		hashtable.put("91", "国外");
		return hashtable;
	}

	/**
	 * 功能：判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 功能：判断字符串是否为日期格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String strDate) {
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

}
