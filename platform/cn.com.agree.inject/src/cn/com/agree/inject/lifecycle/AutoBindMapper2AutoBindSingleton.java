package cn.com.agree.inject.lifecycle;

import java.lang.annotation.Annotation;

import cn.com.agree.inject.annotations.AutoBindMapper;

import com.netflix.governator.annotations.AutoBindSingleton;

public class AutoBindMapper2AutoBindSingleton implements AutoBindSingleton {
	
	private Class<?> value = AutoBindSingleton.class;
	
	private Class<?> baseClass = AutoBindSingleton.class;
	
	private boolean multiple = false;
	
	private boolean eager = false;
	
	public AutoBindMapper2AutoBindSingleton (Class<?> value, Class<?> baseClass, boolean multiple, boolean eager) {
		if (value != AutoBindMapper.class)
			this.value = value;
		if (baseClass != AutoBindMapper.class)
			this.baseClass = baseClass;
		this.multiple = multiple;
		this.eager = eager;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return AutoBindSingleton.class;
	}

	@Override
	public Class<?> value() {
		return value;
	}

	@Override
	public Class<?> baseClass() {
		return baseClass;
	}

	@Override
	public boolean multiple() {
		return multiple;
	}

	@Override
	public boolean eager() {
		return eager;
	}

}
