package jsrccb.trade.esb.view.cd.cd16802_3;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;
import cn.com.agree.ab.trade.core.tools.StringUtil;

public class CD16802_3 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label label_ע�����1 = new Label(this, "label_ע�����1");
	public TextField text_ע�����1 = new TextField(this, "text_ע�����1");
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
	public TextField text_�����к� = new TextField(this, "text_�����к�");
	public Label label_���˴��� = new Label(this, "label_���˴���");
	public TextField text_���˴��� = new TextField(this, "text_���˴���");
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
	public Label label_��ע = new Label(this, "label_��ע");
	public TextField text_��ע = new TextField(this, "text_��ע");
	public Label label_�������� = new Label(this, "label_��������");
	public Button button_��ѯ = new Button(this, "button_��ѯ");

	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
		label_ע�����.setVisible(false);
		text_ע�����.setVisible(false);
		label_�ͻ���.setVisible(false);
		text_�ͻ���.setVisible(false);
		label_��λ��.setVisible(false);
		text_��λ��.setVisible(false);
		label_��˰˰��.setVisible(false);
		text_��˰˰��.setVisible(false);
		label_��˰˰��.setVisible(false);
		text_��˰˰��.setVisible(false);
		label_��������˺�.setVisible(false);
		text_��������˺�.setVisible(false);
		label_�����к�.setVisible(false);
		text_�����к�.setVisible(false);
		label_���˴���.setVisible(false);
		text_���˴���.setVisible(false);
		label_��������.setVisible(false);
		text_��������.setVisible(false);
		label_����֤������.setVisible(false);
		combo_����֤������.setVisible(false);
		label_����֤������.setVisible(false);
		combo_����֤������.setVisible(false);
		text_�������֤��.setVisible(false);
		label_�������֤��.setVisible(false);
		text_����֤������.setVisible(false);
		label_����֤������.setVisible(false);
		label_��λ��ַ.setVisible(false);
		text_��λ��ַ.setVisible(false);
		label_����绰.setVisible(false);
		text_����绰.setVisible(false);
		label_Email.setVisible(false);
		text_Email.setVisible(false);
		label_״̬.setVisible(false);
		combo_״̬.setVisible(false);
		label_��ע.setVisible(false);
		text_��ע.setVisible(false);
		label_��λ�ʱ�.setVisible(false);
		text_��λ�ʱ�.setVisible(false);
		label_�칫�绰.setVisible(false);
		text_�칫�绰.setVisible(false);
		label_����.setVisible(false);
		combo_����.setVisible(false);
		text_����.setVisible(false);
		label_����.setVisible(false);
		button_submit.setVisible(false);
		button_��ѯ.setVisible(true);

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
	public void text_Email_OnBlur() throws Exception {
		if (!"".equals(text_Email)) {
			boolean email = StringUtil.isValidEmailAddr(text_Email.getText().trim());
			if (email == false) {
				pushInfo("��������ȷ�ĵ��������ʽ!", true);
				text_Email.setFocus();
				return;
			}

		}
	}

	/**
	 * @ABFEditor#button_��ѯ
	 */
	public void button_��ѯ_OnClick() throws Exception {
		nestedCommun("00140000008202");
		button_submit.setVisible(true);
		button_��ѯ.setVisible(false);
		label_ע�����.setVisible(true);
		text_ע�����.setVisible(true);
		label_�ͻ���.setVisible(true);
		text_�ͻ���.setVisible(true);
		label_��λ��.setVisible(true);
		text_��λ��.setVisible(true);
		label_��˰˰��.setVisible(true);
		text_��˰˰��.setVisible(true);
		label_��˰˰��.setVisible(true);
		text_��˰˰��.setVisible(true);
		label_��������˺�.setVisible(true);
		text_��������˺�.setVisible(true);
		label_�����к�.setVisible(true);
		text_�����к�.setVisible(true);
		label_���˴���.setVisible(true);
		text_���˴���.setVisible(true);
		label_��������.setVisible(true);
		text_��������.setVisible(true);
		label_����֤������.setVisible(true);
		combo_����֤������.setVisible(true);
		label_����֤������.setVisible(true);
		combo_����֤������.setVisible(true);
		text_�������֤��.setVisible(true);
		label_�������֤��.setVisible(true);
		text_����֤������.setVisible(true);
		label_����֤������.setVisible(true);
		label_��λ��ַ.setVisible(true);
		text_��λ��ַ.setVisible(true);
		label_����绰.setVisible(true);
		text_����绰.setVisible(true);
		label_Email.setVisible(true);
		text_Email.setVisible(true);
		label_״̬.setVisible(true);
		combo_״̬.setVisible(true);
		label_��ע.setVisible(true);
		text_��ע.setVisible(true);
		label_��λ�ʱ�.setVisible(true);
		text_��λ�ʱ�.setVisible(true);
		text_�칫�绰.setVisible(true);
		label_����.setVisible(true);
		combo_����.setVisible(true);
		text_����.setVisible(true);
		label_����.setVisible(true);
		label_ע�����1.setVisible(false);
		text_ע�����1.setVisible(false);
		text_�ͻ���.setFocus();
	}

}
