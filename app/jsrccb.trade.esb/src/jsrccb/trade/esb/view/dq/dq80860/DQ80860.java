package jsrccb.trade.esb.view.dq.dq80860;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.ComboBox;
import cn.com.agree.ab.trade.core.component.Label;
import cn.com.agree.ab.trade.core.component.TextField;

public class DQ80860 extends AbstractCommonTrade {


	public Label label_�˻����� = new Label(this, "label_�˻�����");
	public ComboBox combo_�˻����� = new ComboBox(this, "combo_�˻�����");
	public Label label_�˺� = new Label(this, "label_�˺�");
	public TextField text_�˺� = new TextField(this, "text_�˺�");
	public Label label_֤������ = new Label(this, "label_֤������");
	public ComboBox combo_֤������ = new ComboBox(this, "combo_֤������");
	public Label label_֤������ = new Label(this, "label_֤������");
	public TextField text_֤������ = new TextField(this, "text_֤������");
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
