package jsrccb.common.utils;

import cn.com.agree.ab.lib.exception.BasicRuntimeException;

public class PinUtil {
	
	private static char[][] tellerPinMapping = new char[][] {
		{'A','Q','Y','E','R','T','P','U','I','O'},
		{'E','A','I','V','Z','E','D','S','O','P'},
		{'G','K','A','C','B','D','H','F','X','Z'},
		{'P','D','L','A','K','C','G','Z','S','U'},
		{'J','Y','N','D','A','V','M','X','P','V'},
		{'L','Z','G','F','L','A','Q','E','T','W'},
		{'O','T','X','G','W','B','A','I','R','Z'},
		{'Q','V','W','R','S','N','P','A','U','C'},
		{'R','B','P','T','U','M','N','R','A','X'},
		{'S','M','C','Z','D','Y','I','T','G','A'}
	};

	public static String encryptTellerPin(String teller, String pin) {
		if (teller == null || teller.length() != 12){
			throw new BasicRuntimeException("非12位柜员号!");
		}
		if (pin == null || pin.length()!=6){
			throw new BasicRuntimeException("非6位密码!");
		}
		char[] a = teller.toCharArray();
		char[] b = pin.toCharArray();
		char[] c = new char[6];
		c[0] = tellerPinMapping[Integer.parseInt(String.valueOf(a[6 ]))][Integer.parseInt(String.valueOf(b[0]))];
		c[1] = tellerPinMapping[Integer.parseInt(String.valueOf(a[7 ]))][Integer.parseInt(String.valueOf(b[1]))];
		c[2] = tellerPinMapping[Integer.parseInt(String.valueOf(a[8 ]))][Integer.parseInt(String.valueOf(b[2]))];
		c[3] = tellerPinMapping[Integer.parseInt(String.valueOf(a[9 ]))][Integer.parseInt(String.valueOf(b[3]))];
		c[4] = tellerPinMapping[Integer.parseInt(String.valueOf(a[10]))][Integer.parseInt(String.valueOf(b[4]))];
		c[5] = tellerPinMapping[Integer.parseInt(String.valueOf(a[11]))][Integer.parseInt(String.valueOf(b[5]))];
		return new String(c);
	}
	
	
}
