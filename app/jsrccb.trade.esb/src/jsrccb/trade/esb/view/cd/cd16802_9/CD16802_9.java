package jsrccb.trade.esb.view.cd.cd16802_9;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.*;

public class CD16802_9 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label label_注册号码 = new Label(this, "label_注册号码");
	public TextField text_注册号码 = new TextField(this, "text_注册号码");
	public Label label_开户行 = new Label(this, "label_开户行");
	public TextField text_开户行 = new TextField(this, "text_开户行");
	public Label label_账户种类 = new Label(this, "label_账户种类");
	public ComboBox combo_账户种类 = new ComboBox(this, "combo_账户种类");
	public Label label_账号 = new Label(this, "label_账号");
	public TextField text_账号 = new TextField(this, "text_账号");
	public Label label_账户姓名 = new Label(this, "label_账户姓名");
	public TextField text_账户姓名 = new TextField(this, "text_账户姓名");
	public Label label_币种 = new Label(this, "label_币种");
	public ComboBox combo_币种 = new ComboBox(this, "combo_币种");
	public Label label_注册柜员 = new Label(this, "label_注册柜员");
	public TextField text_注册柜员 = new TextField(this, "text_注册柜员");
	public Label label_注册网点 = new Label(this, "label_注册网点");
	public TextField text_注册网点 = new TextField(this, "text_注册网点");
	public Label label_注册日期 = new Label(this, "label_注册日期");
	public TextField text_注册日期 = new TextField(this, "text_注册日期");
	public Label label_地区号 = new Label(this, "label_地区号");
	public TextField text_地区号 = new TextField(this, "text_地区号");
	public Label label_授权标志 = new Label(this, "label_授权标志");
	public ComboBox combo_授权标志 = new ComboBox(this, "combo_授权标志");
	public Button button_查询 = new Button(this, "button_查询");
	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
		text_开户行.setVisible(false);
		combo_账户种类.setVisible(false);
		text_账号.setVisible(false);
		text_账户姓名.setVisible(false);
		combo_币种.setVisible(false);
		text_注册柜员.setVisible(false);
		text_注册网点.setVisible(false);
		text_注册日期.setVisible(false);
		text_地区号.setVisible(false);
		combo_授权标志.setVisible(false);
		button_submit.setVisible(false);
		label_开户行.setVisible(false);
		label_账户种类.setVisible(false);
		label_账号.setVisible(false);
		label_账户姓名.setVisible(false);
		label_币种.setVisible(false);
		label_注册柜员.setVisible(false);
		label_注册网点.setVisible(false);
		label_注册日期.setVisible(false);
		label_地区号.setVisible(false);
		label_授权标志.setVisible(false);
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
	* @ABFEditor#button_查询
	*/  
	public void button_查询_OnClick()  throws Exception{
		nestedCommun("00140000008212");
		text_开户行.setVisible(true);
		combo_账户种类.setVisible(true);
		text_账号.setVisible(true);
		text_账户姓名.setVisible(true);
		combo_币种.setVisible(true);
		text_注册柜员.setVisible(true);
		text_注册网点.setVisible(true);
		text_注册日期.setVisible(true);
		text_地区号.setVisible(true);
		combo_授权标志.setVisible(true);
		button_submit.setVisible(true);
		button_查询.setVisible(true);
		//label_账号编号.setVisible(true);
		label_开户行.setVisible(true);
		label_账户种类.setVisible(true);
		label_账号.setVisible(true);
		label_账户姓名.setVisible(true);
		label_币种.setVisible(true);
		label_注册柜员.setVisible(true);
		label_注册网点.setVisible(true);
		label_注册日期.setVisible(true);
		label_地区号.setVisible(true);
		label_授权标志.setVisible(true);
	}

}
