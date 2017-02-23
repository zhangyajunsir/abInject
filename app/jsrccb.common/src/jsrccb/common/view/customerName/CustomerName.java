package jsrccb.common.view.customerName;


import java.util.List;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.utils.JsonUtil;
import cn.com.agree.ab.trade.core.component.Table;

public class CustomerName extends AbstractCommonTrade {

	public Table table_客户 = new Table(this, "table_客户");
	@Override
	public void onInit() throws Exception {
		registerHotKey("table_客户", "CR", "table_客户_OnRowDoubleClick");
//		nestedCommun("EC0390001");
		List<String[]> a = JsonUtil.getList(getStoreData("list"), String[].class);
		for (int i = 0; i < a.size(); i++) {
			if (!"".equals(a.get(i)[0])) {
				table_客户.addRow(-1, a.get(i));
			}
		}
	}

	@Override
	protected void changeViewStyle(String commCode) {

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
	* @ABFEditor#table_客户
	*/  
	public void table_客户_OnRowDoubleClick()  throws Exception{
		if (table_客户.getSelectedRowIndex()>=0) {
			String[] rowData = table_客户.getRow(table_客户.getSelectedRowIndex());
			putStoreData("CUSTOM_NAME",rowData[0]);
			putStoreData("CUSTOM_NO",rowData[1]);
			button_exit_OnClick();
		}
	}

}
