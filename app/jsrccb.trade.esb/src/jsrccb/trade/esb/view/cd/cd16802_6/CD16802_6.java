package jsrccb.trade.esb.view.cd.cd16802_6;

import jsrccb.common.annotation.CustomerName;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;
import cn.com.agree.ab.trade.core.device.IPin;

public class CD16802_6 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label labe_证件种类 = new Label(this, "labe_证件种类");
	public Label label_客户名 = new Label(this, "label_客户名");
	public TextField text_客户名 = new TextField(this, "text_客户名");
	public ComboBox combo_证件种类 = new ComboBox(this, "combo_证件种类");
	public Label label_证件号码 = new Label(this, "label_证件号码");
	public TextField text_证件号码 = new TextField(this, "text_证件号码");
	public TextField text_新密码 = new TextField(this, "text_新密码");
	public Label label_新密码 = new Label(this, "label_新密码");
	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected void changeViewStyle(String commCode) {
		// TODO Auto-generated method stub

	}

	/**
	 * @ABFEditor#button_enter
	 */
	public void button_submit_OnClick() throws Exception {
		onCommit();
	}
	
	//提交成功后和交易退出前的交易特殊处理
	@Override
	public void posCommit(String message){
		
	}
	/**
	 * @ABFEditor#button_exit
	 */
	public void button_exit_OnClick() throws Exception {
		exit(0);
		
		
	}

	/**
	* @ABFEditor#text_证件号码
	*/  
	@CustomerName(idType="combo_证件种类",idNo="text_证件号码",name="text_客户名",sysNo="#客户编号")
	public void text_证件号码_OnBlur()  throws Exception{
	}

	/**
	* @ABFEditor#text_新密码
	*/  
	public void text_新密码_OnFocus()  throws Exception{
		IPin pin = this.getDeviceManager().getPin();
		String Password = "";
		Password = pin.readOnce(new String[] { "", "", "", "" });
		putStoreData("sPwd", Password);
		
		text_新密码.setText("******");	
		
		int length = text_新密码.getText().length();  //密码长度
		if (length != 6){
			pushInfo("密码输入位数有误!", true);
			text_新密码.setFocus();
			return;
		}
	}

}
