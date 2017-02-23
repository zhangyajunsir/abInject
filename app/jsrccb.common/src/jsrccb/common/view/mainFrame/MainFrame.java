package jsrccb.common.view.mainFrame;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;

public class MainFrame extends AbstractCommonTrade {

	public TextField text_code = new TextField(this, "text_code");
	public MultiTradeComposite multiTradeComposite = new MultiTradeComposite(this, "multiTradeComposite");
	
	
	@Override
	public void onInit() throws Exception {
		
	}

	@Override
	protected void changeViewStyle(String commCode) {
		
	}

	/**
	* @ABFEditor#text_code
	*/  
	public void text_code_OnBlur()  throws Exception{
		viewOpenBiz.asycOpenView(this, text_code.getText(), null);
	}


}
