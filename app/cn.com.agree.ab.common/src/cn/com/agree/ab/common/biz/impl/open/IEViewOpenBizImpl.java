package cn.com.agree.ab.common.biz.impl.open;

import javax.inject.Singleton;

import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = IViewOpenBiz.class, multiple = true)
@Singleton
@Biz("ieViewOpenBiz")
public class IEViewOpenBizImpl extends ABViewOpenBizImpl {

}
