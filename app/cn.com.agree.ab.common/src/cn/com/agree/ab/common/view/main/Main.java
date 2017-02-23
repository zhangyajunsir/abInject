package cn.com.agree.ab.common.view.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.props.Props;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.TextField;
import cn.com.agree.ab.trade.core.tools.FileUtil;

public class Main extends Trade {
	private static final Logger	logger	= LoggerFactory.getLogger(Main.class);
	@Inject
	@Named("abViewOpenBiz")
	private IViewOpenBiz viewOpenBiz;
	@Inject
	private ConfigManager configManager;
	
	private static final String clientPropPath = ".\\configuration\\client.prop";

	public TextField text_path    = new TextField(this, "text_path");
	public Button    button_path  = new Button(this,    "button_path");
	public TextField text_code    = new TextField(this, "text_code");
	public Button    button_code  = new Button(this,    "button_code");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int init() throws Exception {
		// 查询柜员绑定或其它属相绑定
		boolean isExist = FileUtil.isClientFileExists(this, clientPropPath);
		if (!isExist) {
			// 初始化交易
			viewOpenBiz.exitOpenView(this, "init", null);
		} 
		byte[] propBytes = FileUtil.readClientFile(this, clientPropPath);
		Properties prop  = new Properties();
		prop.load(new ByteArrayInputStream(propBytes));
		// 清空tellerInfo并加载client.prop内容
		updateTellerInfo(new HashMap<String, String>((Map) prop), true);
		// 更新LastOid到client.prop
		prop.put(ITradeKeys.G_LAST_OID, getStoreData(G_ABC_OID));
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		prop.store(bos, null);
		FileUtil.writeClientFile(this, clientPropPath, bos.toByteArray());
		// END
		
		Props iniCfg = configManager.getUtilIni();
		String debug = iniCfg.getValue("MAIN.DEBUG");
		if (!"1".equals(debug)) {
			viewOpenBiz.exitOpenView(this, "login", null);
		}
		return 0;
	}

	/**
	* @ABFEditor#button_code
	*/  
	public void button_code_OnClick()  throws Exception{
		viewOpenBiz.exitOpenView(this, text_code.getText(), null);
	}

	/**
	* @ABFEditor#button_path
	*/  
	public void button_path_OnClick()  throws Exception{
		this.exitAndOpenTrade(text_path.getText(), "", null);
	}
}
