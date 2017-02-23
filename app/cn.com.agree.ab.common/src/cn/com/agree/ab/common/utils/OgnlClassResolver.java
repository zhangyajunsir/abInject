package cn.com.agree.ab.common.utils;

import java.util.Map;

import cn.com.agree.ab.resource.ResourcePlugin;

import ognl.ClassResolver;

public class OgnlClassResolver implements ClassResolver {

	@SuppressWarnings("rawtypes")
	@Override
	public Class classForName(String className, Map context) throws ClassNotFoundException {
		return ResourcePlugin.getDefault().loadClass(className);
	}

}
