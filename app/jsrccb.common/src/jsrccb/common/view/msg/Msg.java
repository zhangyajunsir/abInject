package jsrccb.common.view.msg;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.Image;
import cn.com.agree.ab.trade.core.component.Table;

public class Msg extends AbstractCommonTrade {
	
	private static final String ERRR_IMG  = "/${project_name}/resources/image/error.png";
	private static final String WARN_IMG  = "/${project_name}/resources/image/warn.png";
	private static final String INFO_IMG  = "/${project_name}/resources/image/info.png";
	
	public Table table_msg = new Table(this, "table_msg");
	public Image image_ico = new Image(this, "image_ico");
	public Button button_exit = new Button(this, "button_exit");
	
	@Override
	public void onInit() throws Exception {
		Map<String, Object>      level = JsonUtil.parseMap (getStoreData("msgLevel"));
		List<Map<String,Object>> msgs  = JsonUtil.json2List(getStoreData("msgInfo"));
		for (Map<String,Object> msg : msgs) {
			if (msg != null) {
				for (Iterator<Map.Entry<String, Object>> it = msg.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, Object> entry = it.next();
					table_msg.addRow(-1, new String[]{entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString()});
				}
			}
		}
		table_msg.setColumnTitle(1, level.get("desc") == null ? "错误" : level.get("desc").toString());
		if (ExceptionLevel.DEBUG.getCode().equals(level.get("code")) || ExceptionLevel.INFO.getCode().equals(level.get("code"))) {
			image_ico.setImage(INFO_IMG);
		} else if (ExceptionLevel.WARN.getCode().equals(level.get("code"))) {
			image_ico.setImage(WARN_IMG);
		} else {
			image_ico.setImage(ERRR_IMG);
		}
		button_exit.setFocus();
	}

	@Override
	protected void changeViewStyle(String commCode) {
		
	}

	/**
	* @ABFEditor#button_exit
	*/  
	public void button_exit_OnClick()  throws Exception{
		exit(0);
	}

	

}
