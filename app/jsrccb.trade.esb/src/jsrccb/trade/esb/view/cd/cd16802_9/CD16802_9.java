package jsrccb.trade.esb.view.cd.cd16802_9;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.*;

public class CD16802_9 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label label_ע����� = new Label(this, "label_ע�����");
	public TextField text_ע����� = new TextField(this, "text_ע�����");
	public Label label_������ = new Label(this, "label_������");
	public TextField text_������ = new TextField(this, "text_������");
	public Label label_�˻����� = new Label(this, "label_�˻�����");
	public ComboBox combo_�˻����� = new ComboBox(this, "combo_�˻�����");
	public Label label_�˺� = new Label(this, "label_�˺�");
	public TextField text_�˺� = new TextField(this, "text_�˺�");
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
	public Button button_��ѯ = new Button(this, "button_��ѯ");
	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
		text_������.setVisible(false);
		combo_�˻�����.setVisible(false);
		text_�˺�.setVisible(false);
		text_�˻�����.setVisible(false);
		combo_����.setVisible(false);
		text_ע���Ա.setVisible(false);
		text_ע������.setVisible(false);
		text_ע������.setVisible(false);
		text_������.setVisible(false);
		combo_��Ȩ��־.setVisible(false);
		button_submit.setVisible(false);
		label_������.setVisible(false);
		label_�˻�����.setVisible(false);
		label_�˺�.setVisible(false);
		label_�˻�����.setVisible(false);
		label_����.setVisible(false);
		label_ע���Ա.setVisible(false);
		label_ע������.setVisible(false);
		label_ע������.setVisible(false);
		label_������.setVisible(false);
		label_��Ȩ��־.setVisible(false);
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

	// �ύ�ɹ���ͽ����˳�ǰ�Ľ������⴦��
	@Override
	public void posCommit(String message) {

	}

	/**
	 * @ABFEditor#button_exit
	 */
	public void button_exit_OnClick() throws Exception {
		exit(0);

	}

	/**
	* @ABFEditor#button_��ѯ
	*/  
	public void button_��ѯ_OnClick()  throws Exception{
		nestedCommun("00140000008212");
		text_������.setVisible(true);
		combo_�˻�����.setVisible(true);
		text_�˺�.setVisible(true);
		text_�˻�����.setVisible(true);
		combo_����.setVisible(true);
		text_ע���Ա.setVisible(true);
		text_ע������.setVisible(true);
		text_ע������.setVisible(true);
		text_������.setVisible(true);
		combo_��Ȩ��־.setVisible(true);
		button_submit.setVisible(true);
		button_��ѯ.setVisible(true);
		//label_�˺ű��.setVisible(true);
		label_������.setVisible(true);
		label_�˻�����.setVisible(true);
		label_�˺�.setVisible(true);
		label_�˻�����.setVisible(true);
		label_����.setVisible(true);
		label_ע���Ա.setVisible(true);
		label_ע������.setVisible(true);
		label_ע������.setVisible(true);
		label_������.setVisible(true);
		label_��Ȩ��־.setVisible(true);
	}

}
