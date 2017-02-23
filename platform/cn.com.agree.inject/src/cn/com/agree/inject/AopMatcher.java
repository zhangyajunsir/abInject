package cn.com.agree.inject;

import java.lang.reflect.Method;

import com.google.inject.matcher.Matcher;

public interface AopMatcher {

	Matcher<? super Class<?>> getClassMatcher();
	
	Matcher<? super Method> getMethodMatcher();
}
