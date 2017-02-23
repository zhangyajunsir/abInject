package jsrccb.trade.esb.view.cd.cd16802;



import javax.inject.Inject;
import javax.inject.Named;

import jsrccb.common.annotation.CustomerName;

//import lib.base.CBOD_Query;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.trade.core.component.*;

public class CD16802 extends AbstractCommonTrade {

	public Button    button_ȡ��    = new Button(this, "button_ȡ��");
	public Label     label_֤������ = new Label(this, "label_֤������");
	public ComboBox  combo_֤������ = new ComboBox(this, "combo_֤������");
	public Label     label_֤������ = new Label(this, "label_֤������");
	public TextField text_֤������  = new TextField(this, "text_֤������");
	public Label     label_����     = new Label(this, "label_����");
	public TextField text_����      = new TextField(this, "text_����");
	public Label     label_�������� = new Label(this, "label_��������");
	public ComboBox  combo_�������� = new ComboBox(this, "combo_��������");
	public ComboBox  combo_����     = new ComboBox(this, "combo_����");
	public Button    button_ȷ��    = new Button(this, "button_ȷ��");
	public Button    button_�˳�    = new Button(this, "button_�˳�");
	public Label     label_����     = new Label(this, "label_����");
	@Inject
	@Named("abViewOpenBiz")
	private IViewOpenBiz viewOpenBiz;
	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * @ABFEditor#combo_��������
	 */
	public void combo_��������_OnBlur() throws Exception {
		if ("1".equals(combo_��������.getPrefix())) {
			combo_����.setItems(new String[]{"1 �Ǽ�","2 ��ѯ","3 �޸�","4 ע��"});
			//combo_����.setListName("����(8103)");
		}
		if ("2".equals(combo_��������.getPrefix())) {
			combo_����.setItems(new String[]{"1 ��ѯ","2 ���","3 ɾ��"});
			//combo_����.setListName("����(8104)");
		}
		if ("3".equals(combo_��������.getPrefix())) {
			combo_����.setItems(new String[]{"1 �����޸�","2 ��������"});
			//combo_����.setListName("��������(510524)");
		}
		
		pushInfo("wj test", true);
		OpenViewArgDM openViewArg = new OpenViewArgDM();
		openViewArg.setWindow(true);
		viewOpenBiz.asycOpenView(this, "help", openViewArg);
	}

	/**
	 * @ABFEditor#text_֤������
	 */
	@CustomerName(idType="combo_֤������",idNo="text_֤������",name="text_����",sysNo="#�ͻ����")
	public void text_֤������_OnBlur() throws Exception {
		pushInfo("ok", true);
	}

	/**
	 * @ABFEditor#button_ȷ��
	 */
	public void button_ȷ��_OnClick() throws Exception {
//		CustomerDM cust = new CustomerDM();
//		cust.setCustomerName(text_����.getText().trim());
//		cust.setCustomerNO(getStoreData("�ͻ����"));
//		cust.setIDType(combo_֤������.getPrefix());
//		cust.setIDNumber(text_֤������.getText());
		
//		this.putContext("customerDM", cust);
		putStoreData("֤������", combo_֤������.getPrefix());
		if ("1".equals(combo_��������.getPrefix())) {
			if ("1".equals(combo_����.getPrefix())) {
				combo_����.setFocus();
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_1", openViewArg);
			}

			if ("2".equals(combo_����.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_2", openViewArg);
			}
			if ("3".equals(combo_����.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_3", openViewArg);
			}
			if ("4".equals(combo_����.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_4", openViewArg);
			}
		}
		if ("2".equals(combo_��������.getPrefix())) {

			if ("1".equals(combo_����.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_8", openViewArg);
			}
			if ("2".equals(combo_����.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_7", openViewArg);
			}
			if ("3".equals(combo_����.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_9", openViewArg);
			}
		}
		if ("3".equals(combo_��������.getPrefix())) {
			if ("1".equals(combo_����.getPrefix())) {
				combo_����.setFocus();

				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_5", openViewArg);
			}
			if ("2".equals(combo_����.getPrefix())) {
				combo_����.setFocus();

				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_6", openViewArg);
			}
		}
	}

	/**
	 * @ABFEditor#button_�˳�
	 */
	public void button_�˳�_OnClick() throws Exception {
		this.exit(0);
	}

	@Override
	protected void changeViewStyle(String commCode) {
		// TODO Auto-generated method stub

	}

}
