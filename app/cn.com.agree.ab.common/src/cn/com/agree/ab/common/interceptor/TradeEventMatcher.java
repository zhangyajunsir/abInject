package cn.com.agree.ab.common.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.inject.matcher.AbstractMatcher;

public class TradeEventMatcher extends AbstractMatcher<Method> implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	private List<String> regexList = new ArrayList<String>();
	
	public TradeEventMatcher(String... regexs) {
		for (String regex : regexs) {
			regexList.add(regex);
		}
	}

	@Override
	public boolean matches(Method arg0) {
		for (String regex : regexList) {
			if (Pattern.matches(regex, arg0.getName())) {
				return true;
			}
		}
		return false;
	}

}
