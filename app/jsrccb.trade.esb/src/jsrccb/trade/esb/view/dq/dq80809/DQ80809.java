package jsrccb.trade.esb.view.dq.dq80809;

import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.lib.exception.ViewException;
import cn.com.agree.ab.trade.core.component.BookComposite;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.ComboBox;
import cn.com.agree.ab.trade.core.component.Group;
import cn.com.agree.ab.trade.core.component.Label;
import cn.com.agree.ab.trade.core.component.Table;
import cn.com.agree.ab.trade.core.component.TextField;

public class DQ80809 extends AbstractCommonTrade {
	
	public Label label_核心客户号 = new Label(this, "label_核心客户号");
	public TextField text_核心客户号 = new TextField(this, "text_核心客户号");
	public Button button_查询 = new Button(this, "button_查询");
	public Group group_基本信息 = new Group(this, "group_基本信息");
	public Label label_企业客户号 = new Label(this, "label_企业客户号");
	public TextField text_企业客户号 = new TextField(this, "text_企业客户号");
	public Label label_客户名称 = new Label(this, "label_客户名称");
	public TextField text_客户名称 = new TextField(this, "text_客户名称");
	public Label label_企业经办人名称 = new Label(this, "label_企业经办人名称");
	public TextField text_企业经办人名称 = new TextField(this, "text_企业经办人名称");
	public Label label_经办人证件类型 = new Label(this, "label_经办人证件类型");
	public ComboBox combo_经办人证件类型 = new ComboBox(this, "combo_经办人证件类型");
	public Label label_经办人证件号码 = new Label(this, "label_经办人证件号码");
	public TextField text_经办人证件号码 = new TextField(this, "text_经办人证件号码");
	public Label label_签约手机号一 = new Label(this, "label_签约手机号一");
	public TextField text_签约手机号一 = new TextField(this, "text_签约手机号一");
	public Label label_签约手机号二 = new Label(this, "label_签约手机号二");
	public TextField text_签约手机号二 = new TextField(this, "text_签约手机号二");
	public Label label_纸质对账单周期 = new Label(this, "label_纸质对账单周期");
	public ComboBox combo_纸质对账单周期 = new ComboBox(this, "combo_纸质对账单周期");
	public Group group_对账周期 = new Group(this, "group_对账周期");
	public Label label_对公活期 = new Label(this, "label_对公活期");
	public ComboBox combo_对公活期 = new ComboBox(this, "combo_对公活期");
	public Label label_定期 = new Label(this, "label_定期");
	public ComboBox combo_定期 = new ComboBox(this, "combo_定期");
	public Label label_通知存款 = new Label(this, "label_通知存款");
	public ComboBox combo_通知存款 = new ComboBox(this, "combo_通知存款");
	public Label label_保证金 = new Label(this, "label_保证金");
	public ComboBox combo_保证金 = new ComboBox(this, "combo_保证金");
	public Label label_贷款 = new Label(this, "label_贷款");
	public ComboBox combo_贷款 = new ComboBox(this, "combo_贷款");
	public Label label_保函 = new Label(this, "label_保函");
	public ComboBox combo_保函 = new ComboBox(this, "combo_保函");
	public Label label_同业 = new Label(this, "label_同业");
	public ComboBox combo_同业 = new ComboBox(this, "combo_同业");
	public Label label_对账操作员个数 = new Label(this, "label_对账操作员个数");
	public ComboBox combo_对账操作员个数 = new ComboBox(this, "combo_对账操作员个数");
	public BookComposite bookcomposite_操作员列表 = new BookComposite(this, "bookcomposite_操作员列表");
	public Group group_操作员对账角色设置 = new Group(this, "group_操作员对账角色设置");
	public Label label_操作员ID = new Label(this, "label_操作员ID");
	public Label label_操作员名称 = new Label(this, "label_操作员名称");
	public TextField text_操作员ID = new TextField(this, "text_操作员ID");
	public TextField text_操作员名称 = new TextField(this, "text_操作员名称");
	public Label label_用户级别 = new Label(this, "label_用户级别");
	public Label label_操作员状态 = new Label(this, "label_操作员状态");
	public ComboBox combo_操作员状态 = new ComboBox(this, "combo_操作员状态");
	public Label label_证件类型 = new Label(this, "label_证件类型");
	public ComboBox combo_证件类型 = new ComboBox(this, "combo_证件类型");
	public Label label_证件号码 = new Label(this, "label_证件号码");
	public TextField text_证件号码 = new TextField(this, "text_证件号码");
	public Label label_操作员对账角色 = new Label(this, "label_操作员对账角色");
	public ComboBox combo_操作员对账角色 = new ComboBox(this, "combo_操作员对账角色");
	public Button button_提交 = new Button(this, "button_提交");
	public Button button_取消 = new Button(this, "button_取消");
	public Button button_submit = new Button(this, "button_submit");
	public Button button_exit = new Button(this, "button_exit");
	public TextField text_用户级别 = new TextField(this, "text_用户级别");
	public Label label_企业账号 = new Label(this, "label_企业账号");
	public TextField text_企业账号 = new TextField(this, "text_企业账号");
	public Label label_对账类型 = new Label(this, "label_对账类型");
	public ComboBox combo_对账类型 = new ComboBox(this, "combo_对账类型");
	public Table table_操作员表1 = new Table(this, "table_操作员表1");
	public Table table_操作员表2 = new Table(this, "table_操作员表2");
	@Override
	public void onInit() throws Exception {
		// TODO Auto-generated method stub
		group_基本信息.setVisible(false);
		bookcomposite_操作员列表.setVisible(false);
		button_submit.setEnabled(false);
		group_操作员对账角色设置.setVisible(false);
		combo_对公活期.setPrefix("1");
		combo_定期.setPrefix("1");
		combo_通知存款.setPrefix("1");
		combo_保证金.setPrefix("1");
		combo_贷款.setPrefix("1");
		combo_保函.setPrefix("1");
		combo_同业.setPrefix("1");
		combo_对账操作员个数.setPrefix("1");
	}

	@Override
	protected void changeViewStyle(String commCode) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	* @ABFEditor#button_exit
	*/  
	public void button_exit_OnClick()  throws Exception{
		exit(0);
	}

	/**
	* @ABFEditor#button_查询
	*/  
	public void button_查询_OnClick()  throws Exception{
		if ("".equals(text_核心客户号.getText().trim())
				&& "".equals(text_企业账号.getText().trim())) {
//			pushInfo("请输入查询条件", true);
			text_核心客户号.setFocus();
			throw new ViewException("请输入查询条件!");
		}
		text_企业客户号.setText("");
		text_客户名称.setText("");
		combo_对账操作员个数.setPrefix("");
		nestedCommun("05140000000058");
		
		combo_对账操作员个数_OnBlur();
		group_基本信息.setVisible(true);
		bookcomposite_操作员列表.setVisible(true);
		button_submit.setEnabled(true);
		
	}

	/**
	* @ABFEditor#text_经办人证件号码
	*/ 
	public void text_经办人证件号码_OnFocus()  throws Exception{
//		if ("00".equals(combo_经办人证件类型.getPrefix())) {
//			String[] cardInfo = CBOD_Msf.scanIDCard(this, 1, 3);
//			if (cardInfo == null) {
//				pushInfo("扫描二代证失败", true);
//				text_经办人证件号码.setFocus();
//				return;
//			}
//			if (cardInfo[0] == "0") {
//				text_经办人证件号码.setFocus();
//				return;
//			}
//			text_经办人证件号码.setEnabled(false);
//			text_经办人证件号码.setText(cardInfo[1]);
//		}
	}

	/**
	* @ABFEditor#text_经办人证件号码
	*/  
	public void text_经办人证件号码_OnBlur()  throws Exception{
//		if ("00".equals(combo_经办人证件类型.getPrefix())) {
//			if (!CBOD_Check.idCard_Check(this, "A", text_经办人证件号码.getText(),
//					"1")) {
//				text_经办人证件号码.setFocus();
//				return;
//			}
//		}
	}

	/**
	* @ABFEditor#text_签约手机号一
	*/  
	public void text_签约手机号一_OnBlur()  throws Exception{
//		if (!"".equals(text_签约手机号一.getText().trim())) {
//			if (!CBOD_Util.mobileValidat(text_签约手机号一.getText().trim())) {
//				pushInfo("手机号输入有误！", true);
//				text_签约手机号一.setFocus();
//				return;
//			}
//		}
	}

	/**
	* @ABFEditor#text_签约手机号二
	*/  
	public void text_签约手机号二_OnBlur()  throws Exception{
//		if (!"".equals(text_签约手机号二.getText().trim())) {
//			if (!CBOD_Util.mobileValidat(text_签约手机号二.getText().trim())) {
//				pushInfo("手机号输入有误！", true);
//				text_签约手机号二.setFocus();
//				return;
//			}
//		}
	}

	/**
	* @ABFEditor#combo_对账周期
	*/  
	public void combo_对账类型_OnBlur()  throws Exception{
		if ("0".equals(combo_对账类型.getPrefix())) {
			label_纸质对账单周期.setVisible(true);
			combo_纸质对账单周期.setVisible(true);
			combo_纸质对账单周期.setRequisite(true);
		} else {
			label_纸质对账单周期.setVisible(false);
			combo_纸质对账单周期.setVisible(false);
			combo_纸质对账单周期.setRequisite(false);
		}
	}

	/**
	* @ABFEditor#combo_对账操作员个数
	*/  
	public void combo_对账操作员个数_OnBlur()  throws Exception{
		if ("1".equals(combo_对账操作员个数.getPrefix().trim())) {
			bookcomposite_操作员列表.setSelectionIndex(0);
			String[] item = new String[] { "NoUser 无", "OperateUser 对账操作员" };
			combo_操作员对账角色.setItems(item);
		} else {
			bookcomposite_操作员列表.setSelectionIndex(1);
			String[] item = new String[] { "NoUser 无", "Submitter 对账录入员",
					"Examiner 对账审核员" };
			combo_操作员对账角色.setItems(item);
		}
	}

	/**
	* @ABFEditor#combo_对账操作员个数
	*/  
	public void combo_对账操作员个数_OnSelectionChanged()  throws Exception{
		if ("1".equals(combo_对账操作员个数.getPrefix())) {
			combo_对账操作员个数.setToolTipText("设置唯一的对账操作员，无需进行对账操作审核（此设置对全部法人行社有效）");
		} else if ("2".equals(combo_对账操作员个数.getPrefix())) {
			combo_对账操作员个数
					.setToolTipText("设置一个对账录入员与一个对账审核员，对账操作由录入员录入，最终由审核员审核该对账操作（此设置对全部法人行社有效）");
		}
	}

	/**
	* @ABFEditor#table_操作员表1
	*/  
	public void table_操作员表1_OnRowDoubleClick()  throws Exception{
		if (table_操作员表1.getSelectedRowIndex() < 0) {
			pushInfo("请选中一行", true);
			table_操作员表1.setFocus();
			return;
		}
		String[] rowData = table_操作员表1
				.getRow(table_操作员表1.getSelectedRowIndex());
		text_操作员ID.setText(rowData[0]);
		text_操作员名称.setText(rowData[1]);
		text_用户级别.setText(rowData[2]);
		combo_操作员状态.setPrefix(rowData[5]);
		combo_证件类型.setPrefix(rowData[3]);
		text_证件号码.setText(rowData[4]);
		combo_操作员对账角色.setPrefix(rowData[6]);
		group_操作员对账角色设置.setVisible(true);
		button_submit.setEnabled(false);
	}

	/**
	* @ABFEditor#table_操作员表2
	*/  
	public void table_操作员表2_OnRowDoubleClick()  throws Exception{
		if (table_操作员表2.getSelectedRowIndex() < 0) {
			pushInfo("请选中一行", true);
			table_操作员表2.setFocus();
			return;
		}
		String[] rowData = table_操作员表2
				.getRow(table_操作员表2.getSelectedRowIndex());
		text_操作员ID.setText(rowData[0]);
		text_操作员名称.setText(rowData[1]);
		text_用户级别.setText(rowData[2]);
		combo_操作员状态.setPrefix(rowData[5]);
		combo_证件类型.setPrefix(rowData[3]);
		text_证件号码.setText(rowData[4]);
		combo_操作员对账角色.setPrefix(rowData[6]);
		group_操作员对账角色设置.setVisible(true);
		button_submit.setEnabled(false);
	}

	/**
	* @ABFEditor#button_提交
	*/  
	public void button_提交_OnClick()  throws Exception{
		if ("1".equals(combo_对账操作员个数.getPrefix().trim())) {
			// 表格1
			String[] rowData = new String[] { text_操作员ID.getText().trim(),
					text_操作员名称.getText().trim(), text_用户级别.getText().trim(),
					combo_证件类型.getPrefix().trim(), text_证件号码.getText().trim(),
					combo_操作员状态.getPrefix().trim(),
					combo_操作员对账角色.getPrefix().trim() };
			boolean flag = table_操作员表1.setRowContent(
					table_操作员表1.getSelectedRowIndex(), rowData);
			if (flag) {
				group_操作员对账角色设置.setVisible(false);
				button_submit.setEnabled(true);
				table_操作员表1.setFocus();
			}
		}
		if ("2".equals(combo_对账操作员个数.getPrefix().trim())) {
			// 表格1
			String[] rowData = new String[] { text_操作员ID.getText().trim(),
					text_操作员名称.getText().trim(), text_用户级别.getText().trim(),
					combo_证件类型.getPrefix().trim(), text_证件号码.getText().trim(),
					combo_操作员状态.getPrefix().trim(),
					combo_操作员对账角色.getPrefix().trim() };
			boolean flag = table_操作员表2.setRowContent(
					table_操作员表2.getSelectedRowIndex(), rowData);
			if (flag) {
				group_操作员对账角色设置.setVisible(false);
				button_submit.setEnabled(true);
				table_操作员表2.setFocus();
			}
		}
	}

	/**
	* @ABFEditor#button_取消
	*/  
	public void button_取消_OnClick()  throws Exception{
		group_操作员对账角色设置.setVisible(false);
		if ("1".equals(combo_对账操作员个数.getPrefix().trim())) {
			button_submit.setEnabled(true);
			table_操作员表1.setFocus();
		} else {
			button_submit.setEnabled(true);
			table_操作员表2.setFocus();
		}
	}

	/**
	* @ABFEditor#button_submit
	*/  
	public void button_submit_OnClick()  throws Exception{
		
	}
}
