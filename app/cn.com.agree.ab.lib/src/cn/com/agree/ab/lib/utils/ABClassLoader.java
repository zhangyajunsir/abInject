package cn.com.agree.ab.lib.utils;

import cn.com.agree.ab.resource.ResourcePlugin;

public class ABClassLoader extends ClassLoader {
	
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return ResourcePlugin.getDefault().loadClass(name);
    }
	
}
