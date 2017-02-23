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

	public Button    button_取消    = new Button(this, "button_取消");
	public Label     label_证件类型 = new Label(this, "label_证件类型");
	public ComboBox  combo_证件类型 = new ComboBox(this, "combo_证件类型");
	public Label     label_证件号码 = new Label(this, "label_证件号码");
	public TextField text_证件号码  = new TextField(this, "text_证件号码");
	public Label     label_姓名     = new Label(this, "label_姓名");
	public TextField text_姓名      = new TextField(this, "text_姓名");
	public Label     label_交易名称 = new Label(this, "label_交易名称");
	public ComboBox  combo_交易名称 = new ComboBox(this, "combo_交易名称");
	public ComboBox  combo_功能     = new ComboBox(this, "combo_功能");
	public Button    button_确定    = new Button(this, "button_确定");
	public Button    button_退出    = new Button(this, "button_退出");
	public Label     label_功能     = new Label(this, "label_功能");
	@Inject
	@Named("abViewOpenBiz")
	private IViewOpenBiz viewOpenBiz;
	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * @ABFEditor#combo_交易名称
	 */
	public void combo_交易名称_OnBlur() throws Exception {
		if ("1".equals(combo_交易名称.getPrefix())) {
			combo_功能.setItems(new String[]{"1 登记","2 查询","3 修改","4 注销"});
			//combo_功能.setListName("功能(8103)");
		}
		if ("2".equals(combo_交易名称.getPrefix())) {
			combo_功能.setItems(new String[]{"1 查询","2 添加","3 删除"});
			//combo_功能.setListName("功能(8104)");
		}
		if ("3".equals(combo_交易名称.getPrefix())) {
			combo_功能.setItems(new String[]{"1 密码修改","2 密码重置"});
			//combo_功能.setListName("操作类型(510524)");
		}
		
		pushInfo("wj test", true);
		OpenViewArgDM openViewArg = new OpenViewArgDM();
		openViewArg.setWindow(true);
		viewOpenBiz.asycOpenView(this, "help", openViewArg);
	}

	/**
	 * @ABFEditor#text_证件号码
	 */
	@CustomerName(idType="combo_证件类型",idNo="text_证件号码",name="text_姓名",sysNo="#客户编号")
	public void text_证件号码_OnBlur() throws Exception {
		pushInfo("ok", true);
	}

	/**
	 * @ABFEditor#button_确定
	 */
	public void button_确定_OnClick() throws Exception {
//		CustomerDM cust = new CustomerDM();
//		cust.setCustomerName(text_姓名.getText().trim());
//		cust.setCustomerNO(getStoreData("客户编号"));
//		cust.setIDType(combo_证件类型.getPrefix());
//		cust.setIDNumber(text_证件号码.getText());
		
//		this.putContext("customerDM", cust);
		putStoreData("证件种类", combo_证件类型.getPrefix());
		if ("1".equals(combo_交易名称.getPrefix())) {
			if ("1".equals(combo_功能.getPrefix())) {
				combo_功能.setFocus();
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_1", openViewArg);
			}

			if ("2".equals(combo_功能.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_2", openViewArg);
			}
			if ("3".equals(combo_功能.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_3", openViewArg);
			}
			if ("4".equals(combo_功能.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_4", openViewArg);
			}
		}
		if ("2".equals(combo_交易名称.getPrefix())) {

			if ("1".equals(combo_功能.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_8", openViewArg);
			}
			if ("2".equals(combo_功能.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_7", openViewArg);
			}
			if ("3".equals(combo_功能.getPrefix())) {
				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_9", openViewArg);
			}
		}
		if ("3".equals(combo_交易名称.getPrefix())) {
			if ("1".equals(combo_功能.getPrefix())) {
				combo_功能.setFocus();

				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_5", openViewArg);
			}
			if ("2".equals(combo_功能.getPrefix())) {
				combo_功能.setFocus();

				OpenViewArgDM openViewArg = new OpenViewArgDM();
				openViewArg.setWindow(true);
				viewOpenBiz.syncOpenView(this, "16802_6", openViewArg);
			}
		}
	}

	/**
	 * @ABFEditor#button_退出
	 */
	public void button_退出_OnClick() throws Exception {
		this.exit(0);
	}

	@Override
	protected void changeViewStyle(String commCode) {
		// TODO Auto-generated method stub

	}

}
