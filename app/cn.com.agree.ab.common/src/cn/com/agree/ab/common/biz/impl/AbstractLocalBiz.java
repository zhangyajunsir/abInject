package cn.com.agree.ab.common.biz.impl;

import cn.com.agree.ab.common.dm.CommCodeDM;


public abstract class AbstractLocalBiz extends AbstractCommBiz {
	
	public CommCodeDM  findCommCode() {
		return super.findCommCode(commCode());
	}
	
	public void delCommCode() {
		super.delCommCode(commCode());
	}

	@Override
	public final String systemCode() {
		return "local";
	}
	
	public abstract String commCode();
	
}
