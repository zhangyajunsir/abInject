package jsrccb.common.view.login3G;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.exception.ViewException;

public class Login3G extends AbstractCommonTrade {

	@Override
	public void onInit() throws Exception {
		throw new ViewException("暂不支持3G终端");
	}

	@Override
	protected void changeViewStyle(String commCode) {
		// TODO Auto-generated method stub
		
	}

}
