package jsrccb.trade.esb.view.cd.cd16802_7;

import javax.inject.Inject;
import javax.inject.Named;

import jsrccb.common.biz.dev.PinBiz;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;

public class CD16802_7 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label label_ע����� = new Label(this, "label_ע�����");
	public Label label_������ = new Label(this, "label_������");
	public TextField text_������ = new TextField(this, "text_������");
	public Label label_�˻����� = new Label(this, "label_�˻�����");
	public ComboBox combo_�˻����� = new ComboBox(this, "combo_�˻�����");
	public Label label_�˺� = new Label(this, "label_�˺�");
	public TextField text_�˺� = new TextField(this, "text_�˺�");
	public Label label_���� = new Label(this, "label_����");
	public TextField text_���� = new TextField(this, "text_����");
	public Label label_�˻����� = new Label(this, "label_�˻�����");
	public TextField text_�˻����� = new TextField(this, "text_�˻�����");
	public Label label_���� = new Label(this, "label_����");
	public ComboBox combo_���� = new ComboBox(this, "combo_����");
	public Label label_ע���Ա = new Label(this, "label_ע���Ա");
	public TextField text_ע���Ա = new TextField(this, "text_ע���Ա");
	public Label label_ע������ = new Label(this, "label_ע������");
	public TextField text_ע������ = new TextField(this, "text_ע������");
	public Label label_ע������ = new Label(this, "label_ע������");
	public TextField text_ע������ = new TextField(this, "text_ע������");
	public Label label_������ = new Label(this, "label_������");
	public TextField text_������ = new TextField(this, "text_������");
	public Label label_��Ȩ��־ = new Label(this, "label_��Ȩ��־");
	public ComboBox combo_��Ȩ��־ = new ComboBox(this, "combo_��Ȩ��־");
	public TextField text_ע����� = new TextField(this, "text_ע�����");
	@Inject
	@Named("pinBiz")
	private PinBiz pinBiz;
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
	* @ABFEditor#text_����
	*/  
	//@Pin(account="text_�˺�",must=true,count=1,check=false)
	public void text_����_OnFocus()  throws Exception{
		if ("12".equals(combo_�˻�����.getPrefix())){
			String sPwd = pinBiz.readPasswd(this, text_�˺�.getText(), true, 1, false);
//			String sPwd = CBOD_Pwd.getPasswd(this, text_�˺�.getText(), 2, 1);
			if (sPwd == null){
				text_����.setFocus();
				return;
			}

			putStoreData("sPwd", sPwd);
			text_����.setText("******");
		}else{
			combo_����.setFocus();
			return;
		}
	}

	/**
	* @ABFEditor#text_������
	*/  
	public void text_������_OnBlur()  throws Exception{
		if (9 != text_������.getText().trim().length()){
			pushInfo("�����б���Ϊ9λ,����������!", true);
			text_������.setFocus();
			return;
		}
	}

	/**
	* @ABFEditor#text_������
	*/  
	public void text_������_OnBlur()  throws Exception{
		if ("".equals(text_������.getText().trim())){
			text_������.setText("    ");
		}
	}

	/**
	* @ABFEditor#text_�˺�
	*/  
	public void text_�˺�_OnBlur()  throws Exception{
		nestedCommun("SA0100000");
		if ("12".equals(combo_�˻�����.getPrefix())){
			if (!"CR".equals(getStoreData("sGroupType"))){ //  # ���
				pushInfo("���˻��������п�,����������!", true);
				text_�˺�.setFocus();
				return;
			}
			if ("P".equals(getStoreData("duigduis"))){// ˽P ��U
				pushInfo("���ű���Ϊ��λ��,������!", true);
				text_�˺�.setFocus();
				return;
			}
		}
	}

}
