package jsrccb.trade.esb.view.dq.dq80860;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.ComboBox;
import cn.com.agree.ab.trade.core.component.Label;
import cn.com.agree.ab.trade.core.component.TextField;

public class DQ80860 extends AbstractCommonTrade {


	public Label label_账户类型 = new Label(this, "label_账户类型");
	public ComboBox combo_账户类型 = new ComboBox(this, "combo_账户类型");
	public Label label_账号 = new Label(this, "label_账号");
	public TextField text_账号 = new TextField(this, "text_账号");
	public Label label_证件类型 = new Label(this, "label_证件类型");
	public ComboBox combo_证件类型 = new ComboBox(this, "combo_证件类型");
	public Label label_证件号码 = new Label(this, "label_证件号码");
	public TextField text_证件号码 = new TextField(this, "text_证件号码");
	public Button button_submit = new Button(this, "button_submit");
	public Button button_exit = new Button(this, "button_exit");

	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void changeViewStyle(String commCode) {
		// TODO Auto-generated method stub
		
	}

	/**
	* @ABFEditor#button_submit
	*/  
	public void button_submit_OnClick()  throws Exception{
		onCommit();
	}

	/**
	* @ABFEditor#button_exit
	*/  
	public void button_exit_OnClick()  throws Exception{
		exit(0);
	}

}
