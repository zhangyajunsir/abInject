package jsrccb.trade.esb.view.cd.cd16802_1;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;
import cn.com.agree.ab.trade.core.tools.StringUtil;

public class CD16802_1 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label label_����绰 = new Label(this, "label_����绰");
	public TextField text_����绰 = new TextField(this, "text_����绰");
	public Label label_���� = new Label(this, "label_����");
	public TextField text_���� = new TextField(this, "text_����");
	public Label label_Email = new Label(this, "label_Email");
	public TextField text_Email = new TextField(this, "text_Email");
	public Label label_���� = new Label(this, "label_����");
	public ComboBox combo_���� = new ComboBox(this, "combo_����");
	public Label label_״̬ = new Label(this, "label_״̬");
	public ComboBox combo_״̬ = new ComboBox(this, "combo_״̬");
	public Label label_������Ա = new Label(this, "label_������Ա");
	public TextField text_������Ա = new TextField(this, "text_������Ա");
	public Label label_�������� = new Label(this, "label_��������");
	public TextField text_�������� = new TextField(this, "text_��������");
	public Label label_�޸Ĺ�Ա = new Label(this, "label_�޸Ĺ�Ա");
	public TextField text_�޸Ĺ�Ա = new TextField(this, "text_�޸Ĺ�Ա");
	public Label label_�޸����� = new Label(this, "label_�޸�����");
	public TextField text_�޸����� = new TextField(this, "text_�޸�����");
	public Label label_��ע = new Label(this, "label_��ע");
	public TextField text_��ע = new TextField(this, "text_��ע");
	public Label label_�ͻ��� = new Label(this, "label_�ͻ���");
	public TextField text_�ͻ��� = new TextField(this, "text_�ͻ���");
	public Label label_ע����� = new Label(this, "label_ע�����");
	public TextField text_ע����� = new TextField(this, "text_ע�����");
	public Label label_��λ�� = new Label(this, "label_��λ��");
	public TextField text_��λ�� = new TextField(this, "text_��λ��");
	public Label label_��˰˰�� = new Label(this, "label_��˰˰��");
	public TextField text_��˰˰�� = new TextField(this, "text_��˰˰��");
	public Label label_��˰˰�� = new Label(this, "label_��˰˰��");
	public TextField text_��˰˰�� = new TextField(this, "text_��˰˰��");
	public Label label_��������˺� = new Label(this, "label_��������˺�");
	public TextField text_��������˺� = new TextField(this, "text_��������˺�");
	public Label label_�����к� = new Label(this, "label_�����к�");
	public Label label_���˴��� = new Label(this, "label_���˴���");
	public TextField text_���˴��� = new TextField(this, "text_���˴���");
	public Label label_�������� = new Label(this, "label_��������");
	public TextField text_�������� = new TextField(this, "text_��������");
	public Label label_����֤������ = new Label(this, "label_����֤������");
	public ComboBox combo_����֤������ = new ComboBox(this, "combo_����֤������");
	public Label label_�������֤�� = new Label(this, "label_�������֤��");
	public TextField text_�������֤�� = new TextField(this, "text_�������֤��");
	public Label label_����֤������ = new Label(this, "label_����֤������");
	public ComboBox combo_����֤������ = new ComboBox(this, "combo_����֤������");
	public Label label_����֤������ = new Label(this, "label_����֤������");
	public TextField text_����֤������ = new TextField(this, "text_����֤������");
	public Label label_��λ��ַ = new Label(this, "label_��λ��ַ");
	public TextField text_��λ��ַ = new TextField(this, "text_��λ��ַ");
	public Label label_��λ�ʱ� = new Label(this, "label_��λ�ʱ�");
	public TextField text_��λ�ʱ� = new TextField(this, "text_��λ�ʱ�");
	public Label label_�칫�绰 = new Label(this, "label_�칫�绰");
	public TextField text_�칫�绰 = new TextField(this, "text_�칫�绰");
	public TextField text_�����к� = new TextField(this, "text_�����к�");

	@Override
	public void onInit() throws Exception {
//		CustomerDM cust = TradeHelper.getTradeContext(
//				getStoreData(ITradeKeys.T_SOURCE_PLATFORM_ID), "customerDM");
		// TODO Auto-generated method stub
		text_�ͻ���.setText(getStoreData("�ͻ���"));
		text_��λ��.setText(getStoreData("��λ��"));
//		putStoreData("֤������", cust.getIDType());
//		putStoreData("֤������", cust.getIDNumber());
		nestedCommun("EC0397701");
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
	 * @ABFEditor#text_��˰˰��
	 */
	public void text_��˰˰��_OnBlur() throws Exception {
		if ("".equals(text_��˰˰��.getText().trim())) {
			text_��˰˰��.setText("    ");
		}
	}

	/**
	 * @ABFEditor#text_��˰˰��
	 */
	public void text_��˰˰��_OnBlur() throws Exception {
		if ("".equals(text_��˰˰��.getText().trim())) {
			text_��˰˰��.setText("    ");
		}
	}

	/**
	 * @ABFEditor#text_��������˺�
	 */
	public void text_��������˺�_OnBlur() throws Exception {
		nestedCommun("SA0100000");

	}

	/**
	 * @ABFEditor#text_�����к�
	 */
	public void text_�����к�_OnBlur() throws Exception {
		if (9 != text_�����к�.getText().trim().length()) {
			pushInfo("�����кű���Ϊ9λ,����������!", true);
			text_�����к�.setFocus();
			return;
		}
	}

	/**
	* @ABFEditor#text_Email
	*/  
	public void text_Email_OnBlur()  throws Exception{
		if (!"".equals(text_Email)){
			boolean email = StringUtil.isValidEmailAddr(text_Email.getText().trim());
//			boolean email = CheckUtil.Email_Check(this, text_Email.getText().trim());
			if (email == false) {
				pushInfo("��������ȷ�ĵ��������ʽ!", true);
				text_Email.setFocus();
				return;
			}
			
		}
	}

}
