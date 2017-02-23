package jsrccb.trade.esb.view.cd.cd16802_7;

import javax.inject.Inject;
import javax.inject.Named;

import jsrccb.common.biz.dev.PinBiz;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;

public class CD16802_7 extends AbstractCommonTrade {

	public Button button_exit = new Button(this, "button_exit");
	public Button button_submit = new Button(this, "button_submit");
	public Label label_注册号码 = new Label(this, "label_注册号码");
	public Label label_开户行 = new Label(this, "label_开户行");
	public TextField text_开户行 = new TextField(this, "text_开户行");
	public Label label_账户种类 = new Label(this, "label_账户种类");
	public ComboBox combo_账户种类 = new ComboBox(this, "combo_账户种类");
	public Label label_账号 = new Label(this, "label_账号");
	public TextField text_账号 = new TextField(this, "text_账号");
	public Label label_密码 = new Label(this, "label_密码");
	public TextField text_密码 = new TextField(this, "text_密码");
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
	public TextField text_注册号码 = new TextField(this, "text_注册号码");
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
	
	//提交成功后和交易退出前的交易特殊处理
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
	* @ABFEditor#text_密码
	*/  
	//@Pin(account="text_账号",must=true,count=1,check=false)
	public void text_密码_OnFocus()  throws Exception{
		if ("12".equals(combo_账户种类.getPrefix())){
			String sPwd = pinBiz.readPasswd(this, text_账号.getText(), true, 1, false);
//			String sPwd = CBOD_Pwd.getPasswd(this, text_账号.getText(), 2, 1);
			if (sPwd == null){
				text_密码.setFocus();
				return;
			}

			putStoreData("sPwd", sPwd);
			text_密码.setText("******");
		}else{
			combo_币种.setFocus();
			return;
		}
	}

	/**
	* @ABFEditor#text_开户行
	*/  
	public void text_开户行_OnBlur()  throws Exception{
		if (9 != text_开户行.getText().trim().length()){
			pushInfo("开户行必须为9位,请重新输入!", true);
			text_开户行.setFocus();
			return;
		}
	}

	/**
	* @ABFEditor#text_地区号
	*/  
	public void text_地区号_OnBlur()  throws Exception{
		if ("".equals(text_地区号.getText().trim())){
			text_地区号.setText("    ");
		}
	}

	/**
	* @ABFEditor#text_账号
	*/  
	public void text_账号_OnBlur()  throws Exception{
		nestedCommun("SA0100000");
		if ("12".equals(combo_账户种类.getPrefix())){
			if (!"CR".equals(getStoreData("sGroupType"))){ //  # 组别
				pushInfo("该账户不是银行卡,请重新输入!", true);
				text_账号.setFocus();
				return;
			}
			if ("P".equals(getStoreData("duigduis"))){// 私P 公U
				pushInfo("卡号必须为单位卡,请重输!", true);
				text_账号.setFocus();
				return;
			}
		}
	}

}
