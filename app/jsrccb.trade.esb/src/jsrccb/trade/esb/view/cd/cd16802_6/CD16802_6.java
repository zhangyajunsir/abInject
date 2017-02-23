package jsrccb.trade.esb.view.cd.cd16802_6;

import jsrccb.common.annotation.CustomerName;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;
import cn.com.agree.ab.trade.core.device.IPin;

public class CD16802_6 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label labe_֤������ = new Label(this, "labe_֤������");
	public Label label_�ͻ��� = new Label(this, "label_�ͻ���");
	public TextField text_�ͻ��� = new TextField(this, "text_�ͻ���");
	public ComboBox combo_֤������ = new ComboBox(this, "combo_֤������");
	public Label label_֤������ = new Label(this, "label_֤������");
	public TextField text_֤������ = new TextField(this, "text_֤������");
	public TextField text_������ = new TextField(this, "text_������");
	public Label label_������ = new Label(this, "label_������");
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
	
	//�ύ�ɹ���ͽ����˳�ǰ�Ľ������⴦��
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
	* @ABFEditor#text_֤������
	*/  
	@CustomerName(idType="combo_֤������",idNo="text_֤������",name="text_�ͻ���",sysNo="#�ͻ����")
	public void text_֤������_OnBlur()  throws Exception{
	}

	/**
	* @ABFEditor#text_������
	*/  
	public void text_������_OnFocus()  throws Exception{
		IPin pin = this.getDeviceManager().getPin();
		String Password = "";
		Password = pin.readOnce(new String[] { "", "", "", "" });
		putStoreData("sPwd", Password);
		
		text_������.setText("******");	
		
		int length = text_������.getText().length();  //���볤��
		if (length != 6){
			pushInfo("��������λ������!", true);
			text_������.setFocus();
			return;
		}
	}

}
