package jsrccb.common.view.auth;

import javax.inject.Inject;
import javax.inject.Named;

import jsrccb.common.biz.TerminalDeviceBiz;
import jsrccb.common.biz.dev.FingerBiz;
import jsrccb.common.biz.dev.MsfBiz;
import jsrccb.common.dm.AuthDM;
import jsrccb.common.dm.AuthDM.AuthLevel;
import jsrccb.common.dm.AuthDM.AuthStatus;
import jsrccb.common.dm.AuthDM.AuthWay;
import jsrccb.common.dm.TerminalDeviceDM;
import jsrccb.common.dm.dev.AccountDM;
import jsrccb.common.utils.PinUtil;
import cn.com.agree.ab.common.ITradeContextKey;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.key.ITradeConstants;
import cn.com.agree.ab.lib.exception.ViewException;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.ComboBox;
import cn.com.agree.ab.trade.core.component.Label;
import cn.com.agree.ab.trade.core.component.List;
import cn.com.agree.ab.trade.core.component.TextField;

public class Auth extends AbstractCommonTrade {
	@Inject
	@Named("terminalDeviceBiz")
	private TerminalDeviceBiz terminalDeviceBiz;
	@Inject
	@Named("msfBiz")
	private MsfBiz msfBiz;
	@Inject
	@Named("fingerBiz")
	private FingerBiz fingerBiz;
	
	
	public Label label_level                = new Label    (this, "label_level");
	public ComboBox combo_auth_way          = new ComboBox (this, "combo_auth_way");
	public TextField text_auth_teller_a     = new TextField(this, "text_auth_teller_a");
	public TextField text_auth_teller_a_pwd = new TextField(this, "text_auth_teller_a_pwd");
	public TextField text_auth_teller_b     = new TextField(this, "text_auth_teller_b");
	public TextField text_auth_teller_b_pwd = new TextField(this, "text_auth_teller_b_pwd");
	public Button button_submit             = new Button   (this, "button_submit");
	public Button button_exit               = new Button   (this, "button_exit");
	public List list_auth_message           = new List     (this, "list_auth_message");

	@Override
	public void onInit() throws Exception {
		command(ITradeConstants.COMMAND_ADD_FOCUS_GREEDY_COMPONENTS, "button_exit");
		AuthDM authDM = TradeHelper.getTradeContext(getStoreData(ITradeKeys.T_SOURCE_PLATFORM_ID), ITradeContextKey.AUTH_DM);
		if (authDM == null || authDM.getAuthStatus() != AuthStatus.NEED_AUTH) {
			pushError("未取到授权信息", true);
			exit(0);
		}
		label_level.setText(authDM.getAuthLevel().getDesc());
		if (authDM.getAuthWay() == AuthWay.LOCAL_AUTH) {
			combo_auth_way.setPrefix("0");
			combo_auth_way.setEnabled(false);
		}
		if (authDM.getAuthWay() == AuthWay.ORG_REMOTE_AUTH) {
			combo_auth_way.setPrefix("1");
			combo_auth_way.setEnabled(false);
		}

		if (authDM.getAuthLevel() == AuthLevel.B) {
			text_auth_teller_a    .setEnabled(false);
			text_auth_teller_a_pwd.setEnabled(false);
		} 
		if (authDM.getAuthLevel() == AuthLevel.A) {
			text_auth_teller_b    .setEnabled(false);
			text_auth_teller_b_pwd.setEnabled(false);
		}
		java.util.List<String> authMSG = authDM.getAuthMSG();
		list_auth_message.setItems(authMSG.toArray(new String[authMSG.size()]));
		combo_auth_way.setFocus();
	}

	@Override
	protected void changeViewStyle(String commCode) {

	}

	/**
	 * @ABFEditor#combo1_授权种类
	 */
	public void combo_auth_way_OnBlur() throws Exception {
		if ("1".equals(combo_auth_way.getPrefix())){	//同机构远程授权
			/*
			pushInfo("暂不支持的授权种类", true);
			combo_auth_way.setFocus();
			return;
			*/
			throw new ViewException("不支持同机构跨终端授权");
		}
	}

	public void text_auth_teller_a_OnFocus() throws Exception {
		TerminalDeviceDM terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)getTellerInfo().get(ITradeKeys.G_QBR), "G_CARD_FLAG");
		if (terminalDeviceDM != null && "1".equals(terminalDeviceDM.getTermValue())) {
			AccountDM accountDM = msfBiz.readTellerCard(this, true);
			text_auth_teller_a.setText(accountDM.getAccount() == null ? "" : accountDM.getAccount().toUpperCase());
		}
		
	}

	public void text_auth_teller_a_OnBlur() throws Exception {
		if (text_auth_teller_a.getText().trim().equals((String) getTellerInfo().get(ITradeKeys.G_TELLER))) {
			text_auth_teller_a.setText("");
			throw new ViewException("对不起，A级主管不能自授权！");
		}
	}

	public void text_auth_teller_a_pwd_OnBlur() throws Exception {
		if (6 != text_auth_teller_a_pwd.getText().length()) {
			throw new ViewException("请输入六位密码");
		}
		putTempArea("T_TELLER", text_auth_teller_a.getText());
		putTempArea("T_TELLER_PASSWORD", PinUtil.encryptTellerPin(text_auth_teller_a.getText(), text_auth_teller_a_pwd.getText()));
		nestedCommun("CM0043502");
		if (!"A".equals(getTempArea("T_TELLER_LEVEL"))){
			throw new ViewException("该柜员非A级柜员!");
		}
		//指纹仪	
		TerminalDeviceDM terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)text_auth_teller_a.getText(), "G_FIN_FLAG");
		if (terminalDeviceDM == null || terminalDeviceDM.getTermValue() == null) {
			terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)getTellerInfo().get(ITradeKeys.G_QBR), "G_FIN_FLAG");
		}
		if (terminalDeviceDM != null && !"0".equals(terminalDeviceDM.getTermValue().trim())) {
			boolean fingerFlag = fingerBiz.checkTellerFinger(this, true, (String)getTellerInfo().get(ITradeKeys.G_QBR), (String)text_auth_teller_a.getText());
			if (!fingerFlag)
				text_auth_teller_a_pwd.setFocus();
		}
		putStoreData("T_TELLER_A",     (String)getTempArea("T_TELLER"));
		putStoreData("T_TELLER_A_PWD", (String)getTempArea("T_TELLER_PASSWORD"));
	}

	public void text_auth_teller_b_OnFocus() throws Exception {
		TerminalDeviceDM terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)getTellerInfo().get(ITradeKeys.G_QBR), "G_CARD_FLAG");
		if (terminalDeviceDM != null && "1".equals(terminalDeviceDM.getTermValue())) {
			AccountDM accountDM = msfBiz.readTellerCard(this, true);
			text_auth_teller_b.setText(accountDM.getAccount() == null ? "" : accountDM.getAccount().toUpperCase());
		}
	}

	public void text_auth_teller_b_OnBlur() throws Exception {
		if (text_auth_teller_b.getText().trim().equals((String)getTellerInfo().get(ITradeKeys.G_TELLER))) {
			text_auth_teller_b.setText("");
			throw new ViewException("对不起，B级主管不能自授权！");
		}
	}

	public void text_auth_teller_b_pwd_OnBlur() throws Exception {
		if (6 != text_auth_teller_b_pwd.getText().length()) {
			throw new ViewException("请输入六位密码");
		}
		putTempArea("T_TELLER", text_auth_teller_b.getText());
		putTempArea("T_TELLER_PASSWORD", PinUtil.encryptTellerPin(text_auth_teller_b.getText(), text_auth_teller_b_pwd.getText()));
		nestedCommun("CM0043502");
		if (!"B".equals(getTempArea("T_TELLER_LEVEL")) && !"A".equals(getTempArea("T_TELLER_LEVEL"))){
			throw new ViewException("该柜员非A或B级柜员!");
		}
		//指纹仪	
		TerminalDeviceDM terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)text_auth_teller_b.getText(), "G_FIN_FLAG");
		if (terminalDeviceDM == null || terminalDeviceDM.getTermValue() == null) {
			terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)getTellerInfo().get(ITradeKeys.G_QBR), "G_FIN_FLAG");
		}
		if (terminalDeviceDM != null && !"0".equals(terminalDeviceDM.getTermValue().trim())) {
			boolean fingerFlag = fingerBiz.checkTellerFinger(this, true, (String)getTellerInfo().get(ITradeKeys.G_QBR), (String)text_auth_teller_b.getText());
			if (!fingerFlag)
				text_auth_teller_b_pwd.setFocus();
		}
		putStoreData("T_TELLER_B",     (String)getTempArea("T_TELLER"));
		putStoreData("T_TELLER_B_PWD", (String)getTempArea("T_TELLER_PASSWORD"));
	}

	/**
	 * @ABFEditor#button_确定
	 */
	public void button_submit_OnClick() throws Exception {
		AuthDM authDM = TradeHelper.getTradeContext(getStoreData(ITradeKeys.T_SOURCE_PLATFORM_ID), ITradeContextKey.AUTH_DM);
		authDM.setAuthTellerA   (getStoreData("T_TELLER_A"));
		authDM.setAuthTellerAPWD(getStoreData("T_TELLER_A_PWD"));
		authDM.setAuthTellerB   (getStoreData("T_TELLER_B"));
		authDM.setAuthTellerBPWD(getStoreData("T_TELLER_B_PWD"));
		authDM.setAuthStatus    (AuthStatus.PASS_AUTH);
		exit(0);
	}
	
	/**
	 * @ABFEditor#button_返回
	 */
	public void button_exit_OnClick() throws Exception {
		AuthDM authDM = TradeHelper.getTradeContext(getStoreData(ITradeKeys.T_SOURCE_PLATFORM_ID), ITradeContextKey.AUTH_DM);
		authDM.setAuthStatus    (AuthStatus.CANCEL_AUTH);
		exit(0);
	}
}
