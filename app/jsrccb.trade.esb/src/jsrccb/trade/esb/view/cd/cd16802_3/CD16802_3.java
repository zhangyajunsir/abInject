package jsrccb.trade.esb.view.cd.cd16802_3;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;
import cn.com.agree.ab.trade.core.tools.StringUtil;

public class CD16802_3 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label label_注册号码1 = new Label(this, "label_注册号码1");
	public TextField text_注册号码1 = new TextField(this, "text_注册号码1");
	public Label label_客户号 = new Label(this, "label_客户号");
	public TextField text_客户号 = new TextField(this, "text_客户号");
	public Label label_注册号码 = new Label(this, "label_注册号码");
	public TextField text_注册号码 = new TextField(this, "text_注册号码");
	public Label label_单位名 = new Label(this, "label_单位名");
	public TextField text_单位名 = new TextField(this, "text_单位名");
	public Label label_国税税号 = new Label(this, "label_国税税号");
	public TextField text_国税税号 = new TextField(this, "text_国税税号");
	public Label label_地税税号 = new Label(this, "label_地税税号");
	public TextField text_地税税号 = new TextField(this, "text_地税税号");
	public Label label_基本存款账号 = new Label(this, "label_基本存款账号");
	public TextField text_基本存款账号 = new TextField(this, "text_基本存款账号");
	public Label label_开户行号 = new Label(this, "label_开户行号");
	public TextField text_开户行号 = new TextField(this, "text_开户行号");
	public Label label_法人代表 = new Label(this, "label_法人代表");
	public TextField text_法人代表 = new TextField(this, "text_法人代表");
	public TextField text_财务主管 = new TextField(this, "text_财务主管");
	public Label label_法人证件种类 = new Label(this, "label_法人证件种类");
	public ComboBox combo_法人证件种类 = new ComboBox(this, "combo_法人证件种类");
	public Label label_法人身份证号 = new Label(this, "label_法人身份证号");
	public TextField text_法人身份证号 = new TextField(this, "text_法人身份证号");
	public Label label_主管证件种类 = new Label(this, "label_主管证件种类");
	public ComboBox combo_主管证件种类 = new ComboBox(this, "combo_主管证件种类");
	public Label label_主管证件号码 = new Label(this, "label_主管证件号码");
	public TextField text_主管证件号码 = new TextField(this, "text_主管证件号码");
	public Label label_单位地址 = new Label(this, "label_单位地址");
	public TextField text_单位地址 = new TextField(this, "text_单位地址");
	public Label label_单位邮编 = new Label(this, "label_单位邮编");
	public TextField text_单位邮编 = new TextField(this, "text_单位邮编");
	public Label label_办公电话 = new Label(this, "label_办公电话");
	public TextField text_办公电话 = new TextField(this, "text_办公电话");
	public Label label_财务电话 = new Label(this, "label_财务电话");
	public TextField text_财务电话 = new TextField(this, "text_财务电话");
	public Label label_传真 = new Label(this, "label_传真");
	public TextField text_传真 = new TextField(this, "text_传真");
	public Label label_Email = new Label(this, "label_Email");
	public TextField text_Email = new TextField(this, "text_Email");
	public Label label_性质 = new Label(this, "label_性质");
	public ComboBox combo_性质 = new ComboBox(this, "combo_性质");
	public Label label_状态 = new Label(this, "label_状态");
	public ComboBox combo_状态 = new ComboBox(this, "combo_状态");
	public Label label_备注 = new Label(this, "label_备注");
	public TextField text_备注 = new TextField(this, "text_备注");
	public Label label_财务主管 = new Label(this, "label_财务主管");
	public Button button_查询 = new Button(this, "button_查询");

	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
		label_注册号码.setVisible(false);
		text_注册号码.setVisible(false);
		label_客户号.setVisible(false);
		text_客户号.setVisible(false);
		label_单位名.setVisible(false);
		text_单位名.setVisible(false);
		label_国税税号.setVisible(false);
		text_国税税号.setVisible(false);
		label_地税税号.setVisible(false);
		text_地税税号.setVisible(false);
		label_基本存款账号.setVisible(false);
		text_基本存款账号.setVisible(false);
		label_开户行号.setVisible(false);
		text_开户行号.setVisible(false);
		label_法人代表.setVisible(false);
		text_法人代表.setVisible(false);
		label_财务主管.setVisible(false);
		text_财务主管.setVisible(false);
		label_法人证件种类.setVisible(false);
		combo_法人证件种类.setVisible(false);
		label_主管证件种类.setVisible(false);
		combo_主管证件种类.setVisible(false);
		text_法人身份证号.setVisible(false);
		label_法人身份证号.setVisible(false);
		text_主管证件号码.setVisible(false);
		label_主管证件号码.setVisible(false);
		label_单位地址.setVisible(false);
		text_单位地址.setVisible(false);
		label_财务电话.setVisible(false);
		text_财务电话.setVisible(false);
		label_Email.setVisible(false);
		text_Email.setVisible(false);
		label_状态.setVisible(false);
		combo_状态.setVisible(false);
		label_备注.setVisible(false);
		text_备注.setVisible(false);
		label_单位邮编.setVisible(false);
		text_单位邮编.setVisible(false);
		label_办公电话.setVisible(false);
		text_办公电话.setVisible(false);
		label_性质.setVisible(false);
		combo_性质.setVisible(false);
		text_传真.setVisible(false);
		label_传真.setVisible(false);
		button_submit.setVisible(false);
		button_查询.setVisible(true);

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

	// 提交成功后和交易退出前的交易特殊处理
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
	 * @ABFEditor#text_国税税号
	 */
	public void text_国税税号_OnBlur() throws Exception {
		if ("".equals(text_国税税号.getText().trim())) {
			text_国税税号.setText("    ");
		}
	}

	/**
	 * @ABFEditor#text_地税税号
	 */
	public void text_地税税号_OnBlur() throws Exception {
		if ("".equals(text_地税税号.getText().trim())) {
			text_地税税号.setText("    ");
		}
	}

	/**
	 * @ABFEditor#text_基本存款账号
	 */
	public void text_基本存款账号_OnBlur() throws Exception {
		nestedCommun("SA0100000");
	}

	/**
	 * @ABFEditor#text_开户行号
	 */
	public void text_开户行号_OnBlur() throws Exception {
		if (9 != text_开户行号.getText().trim().length()) {
			pushInfo("开户行号必须为9位,请重新输入!", true);
			text_开户行号.setFocus();
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
				pushInfo("请输入正确的电子邮箱格式!", true);
				text_Email.setFocus();
				return;
			}

		}
	}

	/**
	 * @ABFEditor#button_查询
	 */
	public void button_查询_OnClick() throws Exception {
		nestedCommun("00140000008202");
		button_submit.setVisible(true);
		button_查询.setVisible(false);
		label_注册号码.setVisible(true);
		text_注册号码.setVisible(true);
		label_客户号.setVisible(true);
		text_客户号.setVisible(true);
		label_单位名.setVisible(true);
		text_单位名.setVisible(true);
		label_国税税号.setVisible(true);
		text_国税税号.setVisible(true);
		label_地税税号.setVisible(true);
		text_地税税号.setVisible(true);
		label_基本存款账号.setVisible(true);
		text_基本存款账号.setVisible(true);
		label_开户行号.setVisible(true);
		text_开户行号.setVisible(true);
		label_法人代表.setVisible(true);
		text_法人代表.setVisible(true);
		label_财务主管.setVisible(true);
		text_财务主管.setVisible(true);
		label_法人证件种类.setVisible(true);
		combo_法人证件种类.setVisible(true);
		label_主管证件种类.setVisible(true);
		combo_主管证件种类.setVisible(true);
		text_法人身份证号.setVisible(true);
		label_法人身份证号.setVisible(true);
		text_主管证件号码.setVisible(true);
		label_主管证件号码.setVisible(true);
		label_单位地址.setVisible(true);
		text_单位地址.setVisible(true);
		label_财务电话.setVisible(true);
		text_财务电话.setVisible(true);
		label_Email.setVisible(true);
		text_Email.setVisible(true);
		label_状态.setVisible(true);
		combo_状态.setVisible(true);
		label_备注.setVisible(true);
		text_备注.setVisible(true);
		label_单位邮编.setVisible(true);
		text_单位邮编.setVisible(true);
		text_办公电话.setVisible(true);
		label_性质.setVisible(true);
		combo_性质.setVisible(true);
		text_传真.setVisible(true);
		label_传真.setVisible(true);
		label_注册号码1.setVisible(false);
		text_注册号码1.setVisible(false);
		text_客户号.setFocus();
	}

}
