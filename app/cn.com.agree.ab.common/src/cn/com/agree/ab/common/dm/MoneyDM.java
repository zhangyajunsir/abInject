package cn.com.agree.ab.common.dm;

import java.math.BigDecimal;
import java.math.BigInteger;

import cn.com.agree.ab.lib.dm.BasicDM;

public class MoneyDM extends BasicDM {

	private static final long serialVersionUID = 7048060047727279814L;
	
	private BigDecimal money;

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	public MoneyDM from(String value, int iDecimal) {
		// 1. remove space at left and right side
		value = value.trim();
		// 1.5 if "" return "0"
		if (value.length() == 0) {
			money = null;
			return this;
		}
		boolean positive = true;
		// 2. trim 0 and translate
		char flag = value.charAt(value.length() - 1);
		char endChar = '0';
		if (flag == '}' || ('J' <= flag && 'R' >= flag)) {
			positive = false;
			if ('J' <= flag && 'R' >= flag) {
				endChar = (char) ('1' + flag - 'J');
			}
		} else if (flag == '{' || ('A' <= flag && 'I' >= flag)) {
			if ('A' <= flag && 'I' >= flag) {
				endChar = (char) ('1' + flag - 'A');
			}
		} else if ('p' <= flag && 'y' >= flag) {
			positive = false;
			endChar = (char) (flag - 0x40);
		} else if ('0' <= flag && '9' >= flag) {
			endChar = flag;
		}
		String valueWithoutEnd = value.substring(0, value.length() - 1);
		// 将所有非数字字符转换为0
		value.replaceAll("\\D", "0");
		// 获得最后一个字符前面的数字
		try {
			valueWithoutEnd = String.valueOf(Long.parseLong(valueWithoutEnd));
		} catch (NumberFormatException e) {
			valueWithoutEnd = "";
		}
		if("0".equals(valueWithoutEnd)){
			valueWithoutEnd = "";
		}
		String _value_ = valueWithoutEnd + endChar;
		// 拼接字符串
		if (!positive) {
			_value_ = "-" + _value_;
		}
		if (iDecimal > 0) {
			BigInteger bi = new BigInteger(_value_);
			money = new BigDecimal(bi, iDecimal);
			return this;
		}
		money = new BigDecimal(_value_);
		return this;
	}
	
	public String toNotDot() {
		return money == null ? "" : money.toString().replaceAll("[.]", "");
	}
	
	public String to() {
		return money == null ? "" : money.toString();
	}
	
	public String toString() {
		return to();
	}

}
