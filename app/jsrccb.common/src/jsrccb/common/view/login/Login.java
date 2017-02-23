package jsrccb.common.view.login;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import jsrccb.common.biz.TerminalDeviceBiz;
import jsrccb.common.utils.PinUtil;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.dm.Status;
import cn.com.agree.ab.lib.exception.ViewException;
import cn.com.agree.ab.trade.core.component.*;

public class Login extends AbstractCommonTrade {
	@Inject
	private ConfigManager configManager;
	@Inject
	@Named("abViewOpenBiz")
	private IViewOpenBiz viewOpenBiz;
	@Inject
	@Named("terminalDeviceBiz")
	private TerminalDeviceBiz terminalDeviceBiz;
	@Inject
	@Named("tradeBiz")
	private TradeBiz  tradeBiz;
	
	public TextField	text_teller_password 	= 		new TextField(this, "text_teller_password");
	public TextField 	text_terminal_number 	= 		new TextField(this, "text_terminal_number");
	public Button 		button_login 			= 		new Button(this, "button_login");
	public Button 		button_exit 			= 		new Button(this, "button_exit");
	public TextField    text_branch_number      =       new TextField(this, "text_branch_number");
	public TextField    text_teller_number      =       new TextField(this, "text_teller_number");
	public Label        label_pc_number         =       new Label(this, "label_pc_number");
	public CheckBox     checkbox_org            =       new CheckBox(this, "checkbox_org");
	@Override
	public void onInit() throws Exception {
		// 判断是否开启了终端绑定功能
		if ("1".equals(configManager.getUtilIni().getValue("BANKCFG.IPFLAG"))) {
			if (getTellerInfo().get(ITradeKeys.G_QBR) != null && !getTellerInfo().get(ITradeKeys.G_QBR).equals("")
					&& getTellerInfo().get(ITradeKeys.G_TTYNO) != null && !getTellerInfo().get(ITradeKeys.G_TTYNO).equals("")) {
				text_branch_number  .setText((String)getTellerInfo().get(ITradeKeys.G_QBR));
				text_terminal_number.setText((String)getTellerInfo().get(ITradeKeys.G_TTYNO));
				text_terminal_number.setEnabled(false);
			} else {
				pushInfo("IP[" + getStoreData("G_ABC_IP") + "]配置核心逻辑终端号有误,请删除client.prop后重新进行终端初始化", true);
				command("closeClient", "");
			}
		}
	}

	@Override
	protected void changeViewStyle(String commCode) {
	}

	/**
	 * @ABFEditor#button_退出
	 */
	public void button_exit_OnClick() throws Exception {
	}

	/**
	 * @ABFEditor#button_Login
	 */
	public void button_login_OnClick() throws Exception {
		onCommit();
	}
	
	/**
	 * 覆盖父类onCommit，添加提交里的个性逻辑
	 */
	public void onCommit() throws Exception {
		nestedCommun("CM0003302");
		if (!"1".equals(getTempArea().get("INSTN_TYP")) || !"A".equals(getTempArea().get("OPN_RANG"))) {
			// SPECIALTERMINAL表判断 
			List<String> specialTellers = terminalDeviceBiz.specialTerminalTeller(getStoreData("G_ABC_IP"));
			if (specialTellers != null && specialTellers.size()>0 && !specialTellers.contains(text_teller_number.getText())) {
				throw new ViewException("IP[" + getStoreData("G_ABC_IP") + "]为离行终端,该柜员未维护,请联系系统管理员");
			}
		}
		if (checkbox_org.isChecked()) {
			OpenViewArgDM openViewArg = new OpenViewArgDM();
			openViewArg.setWindow(true);
			viewOpenBiz.syncOpenView(this, "60203", openViewArg);
		}
		super.onCommit();
	}
	
	/**
	 * 覆盖父类onResume，添加回调里的个性逻辑
	 */
	@SuppressWarnings("rawtypes")
	public void onResume(Map suspendResult) throws Exception {
		super.onResume(suspendResult);
		
		String  bTradeId = (String)suspendResult.get("#"+ITradeKeys.T_TRADE_ID);
		TradeCodeDM bTradeCodeDM = tradeBiz.findTradeCode(Integer.valueOf(bTradeId));
		if (bTradeCodeDM != null && "60203".equals(bTradeCodeDM.getCode())) {
			if (Status.SUCCESS.toString().equals(suspendResult.get("#"+ITradeKeys.T_TRADE_STATUS))) {
				checkbox_org.setChecked(false);
				super.onCommit();
			}
		}
		
	}

	/**
	 * @ABFEditor#text_teller_number
	 */
	/** 直接使用注解调用方式
	 * @Msf(type = 2, result = {"text_teller_number:account"})
	 */
	public void text_teller_number_OnFocus() throws Exception {
	}

	/**
	 * @ABFEditor#text_teller_number
	 */
	public void text_teller_number_OnBlur() throws Exception {
		if (text_teller_number.getText().length() != 12) {
			throw new ViewException("柜员号必须是12位!");
		}
		text_teller_number.setText(text_teller_number.getText().toUpperCase());
		// 开启了终端绑定功能后校验
		if ("1".equals(configManager.getUtilIni().getValue("BANKCFG.IPFLAG"))) {
			if (!text_teller_number.getText().substring(0, 9).equals(text_branch_number.getText())) 
				throw new ViewException("终端绑定显示非本机构,请确认!");
		} else {
			text_branch_number  .setText(text_teller_number.getText().substring(0, 9).replace("T", "0"));
			text_terminal_number.setText(text_teller_number.getText().substring(9, 12));
		}
		
	}

	/**
	 * @ABFEditor#text_teller_password
	 */
	public void text_teller_password_OnBlur() throws Exception {
		if (!Pattern.matches("\\d{6}", text_teller_password.getText())) {
			throw new ViewException("柜员密码只能为6位数字!");
		}
		// 1.柜员密码加密
		putStoreData("T_TELLER_PASSWORD", PinUtil.encryptTellerPin(text_teller_number.getText(), text_teller_password.getText()));
		// 2.密码校验
		/*
		nestedCommun("CM0003000");
		if (getTempArea("CM0003000_msg") != null) {
			List<Map> msgs = (List<Map>)getTempArea("CM0003000_msg");
			for (Map msg : msgs) {
				if (msg.get("msgCode").equals("WE007")) {
					OpenViewArgDM openViewArg = new OpenViewArgDM();
					openViewArg.setWindow(true);
					viewOpenBiz.syncOpenView(this, "60305", openViewArg);
					text_teller_password.setFocus();
					break;
				}
			}
		}
		*/
		// 3.判断是否调用指纹仪，先判断柜员，再判断机构的，已在公共流程中处理
	}


	/**
	 * @ABFEditor#text_terminal_number
	 */
	public void text_terminal_number_OnBlur() throws Exception {
		if (!Pattern.matches("\\d{3}", text_terminal_number.getText())) {
			throw new ViewException("终端号只能为3位数字!");
		}
	}


}
