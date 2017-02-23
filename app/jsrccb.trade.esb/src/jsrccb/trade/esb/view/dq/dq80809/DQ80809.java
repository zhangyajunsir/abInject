package jsrccb.trade.esb.view.dq.dq80809;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.exception.ViewException;
import cn.com.agree.ab.trade.core.component.BookComposite;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.ComboBox;
import cn.com.agree.ab.trade.core.component.Group;
import cn.com.agree.ab.trade.core.component.Label;
import cn.com.agree.ab.trade.core.component.Table;
import cn.com.agree.ab.trade.core.component.TextField;

public class DQ80809 extends AbstractCommonTrade {
	
	public Label label_���Ŀͻ��� = new Label(this, "label_���Ŀͻ���");
	public TextField text_���Ŀͻ��� = new TextField(this, "text_���Ŀͻ���");
	public Button button_��ѯ = new Button(this, "button_��ѯ");
	public Group group_������Ϣ = new Group(this, "group_������Ϣ");
	public Label label_��ҵ�ͻ��� = new Label(this, "label_��ҵ�ͻ���");
	public TextField text_��ҵ�ͻ��� = new TextField(this, "text_��ҵ�ͻ���");
	public Label label_�ͻ����� = new Label(this, "label_�ͻ�����");
	public TextField text_�ͻ����� = new TextField(this, "text_�ͻ�����");
	public Label label_��ҵ���������� = new Label(this, "label_��ҵ����������");
	public TextField text_��ҵ���������� = new TextField(this, "text_��ҵ����������");
	public Label label_������֤������ = new Label(this, "label_������֤������");
	public ComboBox combo_������֤������ = new ComboBox(this, "combo_������֤������");
	public Label label_������֤������ = new Label(this, "label_������֤������");
	public TextField text_������֤������ = new TextField(this, "text_������֤������");
	public Label label_ǩԼ�ֻ���һ = new Label(this, "label_ǩԼ�ֻ���һ");
	public TextField text_ǩԼ�ֻ���һ = new TextField(this, "text_ǩԼ�ֻ���һ");
	public Label label_ǩԼ�ֻ��Ŷ� = new Label(this, "label_ǩԼ�ֻ��Ŷ�");
	public TextField text_ǩԼ�ֻ��Ŷ� = new TextField(this, "text_ǩԼ�ֻ��Ŷ�");
	public Label label_ֽ�ʶ��˵����� = new Label(this, "label_ֽ�ʶ��˵�����");
	public ComboBox combo_ֽ�ʶ��˵����� = new ComboBox(this, "combo_ֽ�ʶ��˵�����");
	public Group group_�������� = new Group(this, "group_��������");
	public Label label_�Թ����� = new Label(this, "label_�Թ�����");
	public ComboBox combo_�Թ����� = new ComboBox(this, "combo_�Թ�����");
	public Label label_���� = new Label(this, "label_����");
	public ComboBox combo_���� = new ComboBox(this, "combo_����");
	public Label label_֪ͨ��� = new Label(this, "label_֪ͨ���");
	public ComboBox combo_֪ͨ��� = new ComboBox(this, "combo_֪ͨ���");
	public Label label_��֤�� = new Label(this, "label_��֤��");
	public ComboBox combo_��֤�� = new ComboBox(this, "combo_��֤��");
	public Label label_���� = new Label(this, "label_����");
	public ComboBox combo_���� = new ComboBox(this, "combo_����");
	public Label label_���� = new Label(this, "label_����");
	public ComboBox combo_���� = new ComboBox(this, "combo_����");
	public Label label_ͬҵ = new Label(this, "label_ͬҵ");
	public ComboBox combo_ͬҵ = new ComboBox(this, "combo_ͬҵ");
	public Label label_���˲���Ա���� = new Label(this, "label_���˲���Ա����");
	public ComboBox combo_���˲���Ա���� = new ComboBox(this, "combo_���˲���Ա����");
	public BookComposite bookcomposite_����Ա�б� = new BookComposite(this, "bookcomposite_����Ա�б�");
	public Group group_����Ա���˽�ɫ���� = new Group(this, "group_����Ա���˽�ɫ����");
	public Label label_����ԱID = new Label(this, "label_����ԱID");
	public Label label_����Ա���� = new Label(this, "label_����Ա����");
	public TextField text_����ԱID = new TextField(this, "text_����ԱID");
	public TextField text_����Ա���� = new TextField(this, "text_����Ա����");
	public Label label_�û����� = new Label(this, "label_�û�����");
	public Label label_����Ա״̬ = new Label(this, "label_����Ա״̬");
	public ComboBox combo_����Ա״̬ = new ComboBox(this, "combo_����Ա״̬");
	public Label label_֤������ = new Label(this, "label_֤������");
	public ComboBox combo_֤������ = new ComboBox(this, "combo_֤������");
	public Label label_֤������ = new Label(this, "label_֤������");
	public TextField text_֤������ = new TextField(this, "text_֤������");
	public Label label_����Ա���˽�ɫ = new Label(this, "label_����Ա���˽�ɫ");
	public ComboBox combo_����Ա���˽�ɫ = new ComboBox(this, "combo_����Ա���˽�ɫ");
	public Button button_�ύ = new Button(this, "button_�ύ");
	public Button button_ȡ�� = new Button(this, "button_ȡ��");
	public Button button_submit = new Button(this, "button_submit");
	public Button button_exit = new Button(this, "button_exit");
	public TextField text_�û����� = new TextField(this, "text_�û�����");
	public Label label_��ҵ�˺� = new Label(this, "label_��ҵ�˺�");
	public TextField text_��ҵ�˺� = new TextField(this, "text_��ҵ�˺�");
	public Label label_�������� = new Label(this, "label_��������");
	public ComboBox combo_�������� = new ComboBox(this, "combo_��������");
	public Table table_����Ա��1 = new Table(this, "table_����Ա��1");
	public Table table_����Ա��2 = new Table(this, "table_����Ա��2");
	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
		group_������Ϣ.setVisible(false);
		bookcomposite_����Ա�б�.setVisible(false);
		button_submit.setEnabled(false);
		group_����Ա���˽�ɫ����.setVisible(false);
		combo_�Թ�����.setPrefix("1");
		combo_����.setPrefix("1");
		combo_֪ͨ���.setPrefix("1");
		combo_��֤��.setPrefix("1");
		combo_����.setPrefix("1");
		combo_����.setPrefix("1");
		combo_ͬҵ.setPrefix("1");
		combo_���˲���Ա����.setPrefix("1");
	}

	@Override
	protected void changeViewStyle(String commCode) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	* @ABFEditor#button_exit
	*/  
	public void button_exit_OnClick()  throws Exception{
		exit(0);
	}

	/**
	* @ABFEditor#button_��ѯ
	*/  
	public void button_��ѯ_OnClick()  throws Exception{
		if ("".equals(text_���Ŀͻ���.getText().trim())
				&& "".equals(text_��ҵ�˺�.getText().trim())) {
//			pushInfo("�������ѯ����", true);
			text_���Ŀͻ���.setFocus();
			throw new ViewException("�������ѯ����!");
		}
		text_��ҵ�ͻ���.setText("");
		text_�ͻ�����.setText("");
		combo_���˲���Ա����.setPrefix("");
		nestedCommun("05140000000058");
		
		combo_���˲���Ա����_OnBlur();
		group_������Ϣ.setVisible(true);
		bookcomposite_����Ա�б�.setVisible(true);
		button_submit.setEnabled(true);
		
	}

	/**
	* @ABFEditor#text_������֤������
	*/ 
	public void text_������֤������_OnFocus()  throws Exception{
//		if ("00".equals(combo_������֤������.getPrefix())) {
//			String[] cardInfo = CBOD_Msf.scanIDCard(this, 1, 3);
//			if (cardInfo == null) {
//				pushInfo("ɨ�����֤ʧ��", true);
//				text_������֤������.setFocus();
//				return;
//			}
//			if (cardInfo[0] == "0") {
//				text_������֤������.setFocus();
//				return;
//			}
//			text_������֤������.setEnabled(false);
//			text_������֤������.setText(cardInfo[1]);
//		}
	}

	/**
	* @ABFEditor#text_������֤������
	*/  
	public void text_������֤������_OnBlur()  throws Exception{
//		if ("00".equals(combo_������֤������.getPrefix())) {
//			if (!CBOD_Check.idCard_Check(this, "A", text_������֤������.getText(),
//					"1")) {
//				text_������֤������.setFocus();
//				return;
//			}
//		}
	}

	/**
	* @ABFEditor#text_ǩԼ�ֻ���һ
	*/  
	public void text_ǩԼ�ֻ���һ_OnBlur()  throws Exception{
//		if (!"".equals(text_ǩԼ�ֻ���һ.getText().trim())) {
//			if (!CBOD_Util.mobileValidat(text_ǩԼ�ֻ���һ.getText().trim())) {
//				pushInfo("�ֻ�����������", true);
//				text_ǩԼ�ֻ���һ.setFocus();
//				return;
//			}
//		}
	}

	/**
	* @ABFEditor#text_ǩԼ�ֻ��Ŷ�
	*/  
	public void text_ǩԼ�ֻ��Ŷ�_OnBlur()  throws Exception{
//		if (!"".equals(text_ǩԼ�ֻ��Ŷ�.getText().trim())) {
//			if (!CBOD_Util.mobileValidat(text_ǩԼ�ֻ��Ŷ�.getText().trim())) {
//				pushInfo("�ֻ�����������", true);
//				text_ǩԼ�ֻ��Ŷ�.setFocus();
//				return;
//			}
//		}
	}

	/**
	* @ABFEditor#combo_��������
	*/  
	public void combo_��������_OnBlur()  throws Exception{
		if ("0".equals(combo_��������.getPrefix())) {
			label_ֽ�ʶ��˵�����.setVisible(true);
			combo_ֽ�ʶ��˵�����.setVisible(true);
			combo_ֽ�ʶ��˵�����.setRequisite(true);
		} else {
			label_ֽ�ʶ��˵�����.setVisible(false);
			combo_ֽ�ʶ��˵�����.setVisible(false);
			combo_ֽ�ʶ��˵�����.setRequisite(false);
		}
	}

	/**
	* @ABFEditor#combo_���˲���Ա����
	*/  
	public void combo_���˲���Ա����_OnBlur()  throws Exception{
		if ("1".equals(combo_���˲���Ա����.getPrefix().trim())) {
			bookcomposite_����Ա�б�.setSelectionIndex(0);
			String[] item = new String[] { "NoUser ��", "OperateUser ���˲���Ա" };
			combo_����Ա���˽�ɫ.setItems(item);
		} else {
			bookcomposite_����Ա�б�.setSelectionIndex(1);
			String[] item = new String[] { "NoUser ��", "Submitter ����¼��Ա",
					"Examiner �������Ա" };
			combo_����Ա���˽�ɫ.setItems(item);
		}
	}

	/**
	* @ABFEditor#combo_���˲���Ա����
	*/  
	public void combo_���˲���Ա����_OnSelectionChanged()  throws Exception{
		if ("1".equals(combo_���˲���Ա����.getPrefix())) {
			combo_���˲���Ա����.setToolTipText("����Ψһ�Ķ��˲���Ա��������ж��˲�����ˣ������ö�ȫ������������Ч��");
		} else if ("2".equals(combo_���˲���Ա����.getPrefix())) {
			combo_���˲���Ա����
					.setToolTipText("����һ������¼��Ա��һ���������Ա�����˲�����¼��Ա¼�룬���������Ա��˸ö��˲����������ö�ȫ������������Ч��");
		}
	}

	/**
	* @ABFEditor#table_����Ա��1
	*/  
	public void table_����Ա��1_OnRowDoubleClick()  throws Exception{
		if (table_����Ա��1.getSelectedRowIndex() < 0) {
			pushInfo("��ѡ��һ��", true);
			table_����Ա��1.setFocus();
			return;
		}
		String[] rowData = table_����Ա��1
				.getRow(table_����Ա��1.getSelectedRowIndex());
		text_����ԱID.setText(rowData[0]);
		text_����Ա����.setText(rowData[1]);
		text_�û�����.setText(rowData[2]);
		combo_����Ա״̬.setPrefix(rowData[5]);
		combo_֤������.setPrefix(rowData[3]);
		text_֤������.setText(rowData[4]);
		combo_����Ա���˽�ɫ.setPrefix(rowData[6]);
		group_����Ա���˽�ɫ����.setVisible(true);
		button_submit.setEnabled(false);
	}

	/**
	* @ABFEditor#table_����Ա��2
	*/  
	public void table_����Ա��2_OnRowDoubleClick()  throws Exception{
		if (table_����Ա��2.getSelectedRowIndex() < 0) {
			pushInfo("��ѡ��һ��", true);
			table_����Ա��2.setFocus();
			return;
		}
		String[] rowData = table_����Ա��2
				.getRow(table_����Ա��2.getSelectedRowIndex());
		text_����ԱID.setText(rowData[0]);
		text_����Ա����.setText(rowData[1]);
		text_�û�����.setText(rowData[2]);
		combo_����Ա״̬.setPrefix(rowData[5]);
		combo_֤������.setPrefix(rowData[3]);
		text_֤������.setText(rowData[4]);
		combo_����Ա���˽�ɫ.setPrefix(rowData[6]);
		group_����Ա���˽�ɫ����.setVisible(true);
		button_submit.setEnabled(false);
	}

	/**
	* @ABFEditor#button_�ύ
	*/  
	public void button_�ύ_OnClick()  throws Exception{
		if ("1".equals(combo_���˲���Ա����.getPrefix().trim())) {
			// ���1
			String[] rowData = new String[] { text_����ԱID.getText().trim(),
					text_����Ա����.getText().trim(), text_�û�����.getText().trim(),
					combo_֤������.getPrefix().trim(), text_֤������.getText().trim(),
					combo_����Ա״̬.getPrefix().trim(),
					combo_����Ա���˽�ɫ.getPrefix().trim() };
			boolean flag = table_����Ա��1.setRowContent(
					table_����Ա��1.getSelectedRowIndex(), rowData);
			if (flag) {
				group_����Ա���˽�ɫ����.setVisible(false);
				button_submit.setEnabled(true);
				table_����Ա��1.setFocus();
			}
		}
		if ("2".equals(combo_���˲���Ա����.getPrefix().trim())) {
			// ���1
			String[] rowData = new String[] { text_����ԱID.getText().trim(),
					text_����Ա����.getText().trim(), text_�û�����.getText().trim(),
					combo_֤������.getPrefix().trim(), text_֤������.getText().trim(),
					combo_����Ա״̬.getPrefix().trim(),
					combo_����Ա���˽�ɫ.getPrefix().trim() };
			boolean flag = table_����Ա��2.setRowContent(
					table_����Ա��2.getSelectedRowIndex(), rowData);
			if (flag) {
				group_����Ա���˽�ɫ����.setVisible(false);
				button_submit.setEnabled(true);
				table_����Ա��2.setFocus();
			}
		}
	}

	/**
	* @ABFEditor#button_ȡ��
	*/  
	public void button_ȡ��_OnClick()  throws Exception{
		group_����Ա���˽�ɫ����.setVisible(false);
		if ("1".equals(combo_���˲���Ա����.getPrefix().trim())) {
			button_submit.setEnabled(true);
			table_����Ա��1.setFocus();
		} else {
			button_submit.setEnabled(true);
			table_����Ա��2.setFocus();
		}
	}

	/**
	* @ABFEditor#button_submit
	*/  
	public void button_submit_OnClick()  throws Exception{
		
	}
}
