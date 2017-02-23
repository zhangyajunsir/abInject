package jsrccb.common.view.init;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jsrccb.common.biz.TerminalDeviceBiz;
import jsrccb.common.dm.TerminalDeviceDM;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.exception.ViewException;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.ComboBox;
import cn.com.agree.ab.trade.core.component.Label;
import cn.com.agree.ab.trade.core.component.TextField;
import cn.com.agree.ab.trade.core.tools.FileUtil;

public class Init extends AbstractCommonTrade {
	private static final Logger	logger	= LoggerFactory.getLogger(Init.class);
	private static final String clientPropPath = ".\\configuration\\client.prop";
	@Inject
	@Named("terminalDeviceBiz")
	private TerminalDeviceBiz terminalDeviceBiz;
	@Inject
	private ConfigManager configManager;

	public Label     label_terminal_type   = new Label    (this, "label_terminal_type");
	public ComboBox  combo_terminal_type   = new ComboBox (this, "combo_terminal_type");
	public Label     label_terminal_number = new Label    (this, "label_terminal_number");
	public TextField text_branch_number    = new TextField(this, "text_branch_number");
	public TextField text_pc_number        = new TextField(this, "text_pc_number");
	public TextField text_terminal_number  = new TextField(this, "text_terminal_number");
	public Button    button_enter          = new Button   (this, "button_enter");
	public Button    button_cancel         = new Button   (this, "button_cancel");
	public Label     label1                = new Label    (this, "label1");
	public Label     label2                = new Label    (this, "label2");
	public Label     label_title           = new Label    (this, "label_title");
	
	@Override
	public void onInit() throws Exception {
		TerminalDeviceDM terminalDeviceDM = null;
		try {
			terminalDeviceDM = terminalDeviceBiz.findTerminalDevice(getStoreData("G_ABC_IP"), "G_TTYNO");
		} catch (Exception e) {
			// 直接抛异常会反复进入该交易
			logger.error(e.getMessage(), e);
			pushError(e.getMessage(), true);
			command("closeClient", "force");
		}
		if ("1".equals(configManager.getUtilIni().getValue("BANKCFG.IPFLAG"))) {
			if (terminalDeviceDM == null || terminalDeviceDM.getTermValue() == null || terminalDeviceDM.getTermValue().equals("")) {
				pushInfo("IP[" + getStoreData("G_ABC_IP") + "]核心逻辑终端号未配置或有误,请联系系统管理员", true);
				command("closeClient", "force");
			}
		}
		if (terminalDeviceDM != null) {
			text_branch_number  .setText(terminalDeviceDM.getTermValue().substring(0, 9));
			text_branch_number  .setEnabled(false);
			text_terminal_number.setText(terminalDeviceDM.getTermValue().substring(12));
			text_terminal_number.setEnabled(false);
		}
	}

	@Override
	protected void changeViewStyle(String commCode) {
		
	}

	/**
	* @ABFEditor#combo_terminal_type
	*/  
	public void combo_terminal_type_OnBlur()  throws Exception{
		if ("3".equals(combo_terminal_type.getPrefix())) {
			throw new ViewException("暂不支持移动终端");
		}
	}

	/**
	* @ABFEditor#button_enter
	*/  
	public void button_enter_OnClick()  throws Exception{
		Properties prop  = new Properties();
		prop.put(ITradeKeys.G_TERMINAL_TYPE, combo_terminal_type.getPrefix());
		prop.put(ITradeKeys.G_QBR,           text_branch_number.getText());
		prop.put(ITradeKeys.G_TTYNO,         text_terminal_number.getText());
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		prop.store(bos, null);
		FileUtil.writeClientFile(this, clientPropPath, bos.toByteArray());
		
		
		exit(0);
	}

	/**
	* @ABFEditor#button_cancel
	*/  
	public void button_cancel_OnClick()  throws Exception{
		command("closeClient", "");
	}

	/**
	* @ABFEditor#text_branch_number
	*/  
	public void text_branch_number_OnBlur()  throws Exception{
		if (!Pattern.matches("\\d{9}", text_branch_number.getText())) {
			throw new ViewException("只能为9位数字!");
		}
	}

	/**
	* @ABFEditor#text_terminal_number
	*/  
	public void text_terminal_number_OnBlur()  throws Exception{
		if (!Pattern.matches("\\d{3}", text_terminal_number.getText())) {
			throw new ViewException("只能为3位数字!");
		}
	}

	

}
