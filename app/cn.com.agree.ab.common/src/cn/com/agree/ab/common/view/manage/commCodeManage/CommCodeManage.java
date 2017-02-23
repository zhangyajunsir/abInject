package cn.com.agree.ab.common.view.manage.commCodeManage;



import javax.inject.Inject;
import javax.inject.Named;
import cn.com.agree.ab.common.biz.CommBiz;
import cn.com.agree.ab.common.dm.CommCodeDM;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;



public class CommCodeManage extends AbstractCommonTrade {
	@Inject
	@Named("cbodCommBiz")
	private  CommBiz commBiz;
	public TextField text_i表 = new TextField(this, "text_i表");
	public Label label_o表 = new Label(this, "label_o表");
	public TextField text_o表 = new TextField(this, "text_o表");
	public Label label_通讯标志 = new Label(this, "label_通讯标志");
	public ComboBox combo_通讯标志 = new ComboBox(this, "combo_通讯标志");
	public Label label_超时时间 = new Label(this, "label_超时时间");
	public TextField text_超时时间 = new TextField(this, "text_超时时间");
	public Label label_备注 = new Label(this, "label_备注");
	public TextField text_备注 = new TextField(this, "text_备注");
	public Button button_退出 = new Button(this, "button_退出");
	public Button button_确定 = new Button(this, "button_确定");
	public Label label_i表 = new Label(this, "label_i表");
	public ComboBox combo_渠道代码 = new ComboBox(this, "combo_渠道代码");
	public Label label_渠道代码 = new Label(this, "label_渠道代码");
	public TextField text_交互码 = new TextField(this, "text_交互码");
	public Label label_交互码 = new Label(this, "label_交互码");
	public ComboBox combo_系统代码 = new ComboBox(this, "combo_系统代码");
	public Label label_系统代码 = new Label(this, "label_系统代码");
	public TextField text_通讯码 = new TextField(this, "text_通讯码");
	public Label label_通讯码 = new Label(this, "label_通讯码");
	public ComboBox combo_功能号 = new ComboBox(this, "combo_功能号");
	public Label label_功能号 = new Label(this, "label_功能号");
	@Override
	public void onInit() throws Exception {
		combo_功能号.setFocus();
		combo_功能号.setEnabled(true);
		combo_系统代码.setEnabled(true);
		text_交互码.setEnabled(true);
		combo_渠道代码.setEnabled(true);
		text_i表.setEnabled(true);
		text_o表.setEnabled(true);
		combo_通讯标志.setEnabled(true);
		text_超时时间.setEnabled(true);
		text_备注.setEnabled(true);		
	}
	/**
	* @ABFEditor#button_确定
	*/  
	@SuppressWarnings("unused")
	public void button_确定_OnClick()  throws Exception{
		if(combo_功能号.getPrefix().equals("1")){
			button_确定.setEnabled(false);
			button_退出.setFocus();
			CommCodeDM commCodeDM = new CommCodeDM();
			commCodeDM.setCommCode(text_通讯码.getText());
			commCodeDM.setSystemCode(combo_系统代码.getPrefix());
			commCodeDM.setTransCode(text_交互码.getText());
			commCodeDM.setChannelCode(combo_渠道代码.getPrefix());
			commCodeDM.setItable(text_i表.getText());
			commCodeDM.setOtable(text_o表.getText());
			commCodeDM.setFlag(Integer.valueOf(combo_通讯标志.getPrefix()));
			commCodeDM.setTimeOut(Integer.valueOf(text_超时时间.getText()));
			commCodeDM.setRemark(text_备注.getText());
			commBiz.addOrUpdateCommCode(commCodeDM);
			pushInfo("通讯码表添加数据成功");
		}if (combo_功能号.getPrefix().equals("2")) {
			button_确定.setEnabled(false);
			button_退出.setFocus();
		    CommCodeDM commCodeDM=commBiz.findCommCode(text_通讯码.getText());
		    putContext("id", commCodeDM.getId());
		    if(commCodeDM == null){
		    	pushError("无此通讯码");
		    	return;
		    }
			combo_系统代码.setPrefix(commCodeDM.getSystemCode());
			text_交互码.setText(commCodeDM.getTransCode());
			combo_渠道代码.setPrefix(commCodeDM.getChannelCode());
			text_i表.setText(commCodeDM.getItable());
			text_o表.setText(commCodeDM.getOtable());
			combo_通讯标志.setPrefix(commCodeDM.getFlag()+"");
			text_超时时间.setText(commCodeDM.getTimeOut()+"");
			text_备注.setText(commCodeDM.getRemark());	
			pushInfo("通讯码表查找数据成功");	
		}if (combo_功能号.getPrefix().equals("3")) {
			button_退出.setFocus();
			button_确定.setEnabled(false);		
			text_通讯码.setEnabled(false);
			combo_系统代码.setEnabled(false);
			text_交互码.setEnabled(false);
			combo_渠道代码.setEnabled(false);
			text_i表.setEnabled(false);
			text_o表.setEnabled(false);
			combo_通讯标志.setEnabled(false);
			text_超时时间.setEnabled(false);
			text_备注.setEnabled(false);
			CommCodeDM commCodeDM = new CommCodeDM();
			commCodeDM.setId((Integer) getContext("id1"));		
			commCodeDM.setCommCode(text_通讯码.getText() );
			commCodeDM.setSystemCode(combo_系统代码.getPrefix());
			commCodeDM.setTransCode(text_交互码.getText());
			commCodeDM.setChannelCode(combo_渠道代码.getPrefix());
			commCodeDM.setItable(text_i表.getText());
			commCodeDM.setOtable(text_o表.getText());
			commCodeDM.setFlag(Integer.valueOf(combo_通讯标志.getPrefix()));
			commCodeDM.setTimeOut(Integer.valueOf(text_超时时间.getText()));
			commCodeDM.setRemark(text_备注.getText());
			commBiz.addOrUpdateCommCode(commCodeDM);
			pushInfo("通讯码表更新数据成功");	
		}if(combo_功能号.getPrefix().equals("4")){
			button_退出.setFocus();
			button_确定.setEnabled(false);
			commBiz.delCommCode(text_通讯码.getText());
			pushInfo("通讯码表删除数据成功");
		}	
	}
	/**
	* @ABFEditor#combo_功能号
	*/  
	public void combo_功能号_OnBlur()  throws Exception{
		if(combo_功能号.getPrefix().equals("1")){	
			combo_功能号.setEnabled(true);
			combo_系统代码.setEnabled(true);
			text_交互码.setEnabled(true);
			combo_渠道代码.setEnabled(false);
			text_i表.setEnabled(true);
			text_o表.setEnabled(true);
			combo_通讯标志.setEnabled(true);
			text_超时时间.setEnabled(true);
			text_备注.setEnabled(true);
			text_通讯码.setText("");
			combo_系统代码.setPrefix("host");
			text_交互码.setText("");
			combo_渠道代码.setPrefix("");
			text_i表.setText("");
			text_o表.setText("");
			combo_通讯标志.setPrefix("0");
			text_超时时间.setText("60");
			text_备注.setText("");	
		}if(combo_功能号.getPrefix().equals("2")){
			text_通讯码.setFocus();
			text_通讯码.setEnabled(true);
			combo_系统代码.setEnabled(false);
			text_交互码.setEnabled(false);
			combo_渠道代码.setEnabled(false);
			text_i表.setEnabled(false);
			text_o表.setEnabled(false);
			combo_通讯标志.setEnabled(false);
			text_超时时间.setEnabled(false);
			text_备注.setEnabled(false);
			text_通讯码.setText("");
			combo_系统代码.setPrefix("");
			text_交互码.setText("");
			combo_渠道代码.setPrefix("");
			text_i表.setText("");
			text_o表.setText("");
			combo_通讯标志.setPrefix("");
			text_超时时间.setText("");
			text_备注.setText("");
		}if(combo_功能号.getPrefix().equals("3")){
			text_通讯码.setFocus();
			text_通讯码.setEnabled(true);
			combo_系统代码.setEnabled(true);
			text_交互码.setEnabled(true);
			combo_渠道代码.setEnabled(true);
			text_i表.setEnabled(true);
			text_o表.setEnabled(true);
			combo_通讯标志.setEnabled(true);
			text_超时时间.setEnabled(true);
			text_备注.setEnabled(true);
			text_通讯码.setText("");
			combo_系统代码.setPrefix("");
			text_交互码.setText("");
			combo_渠道代码.setPrefix("");
			text_i表.setText("");
			text_o表.setText("");
			combo_通讯标志.setPrefix("");
			text_超时时间.setText("");
			text_备注.setText("");
		}if(combo_功能号.getPrefix().equals("4")){
			text_通讯码.setFocus();
			text_通讯码.setEnabled(true);
			combo_系统代码.setEnabled(false);
			text_交互码.setEnabled(false);
			combo_渠道代码.setEnabled(false);
			text_i表.setEnabled(false);
			text_o表.setEnabled(false);
			combo_通讯标志.setEnabled(false);
			text_超时时间.setEnabled(false);
			text_备注.setEnabled(false);
			text_通讯码.setText("");
			combo_系统代码.setPrefix("");
			text_交互码.setText("");
			combo_渠道代码.setPrefix("");
			text_i表.setText("");
			text_o表.setText("");
			combo_通讯标志.setPrefix("");
			text_超时时间.setText("");
			text_备注.setText("");
		}
	}
	/**
	* @ABFEditor#text_通讯码
	*/  
	@SuppressWarnings("unused")
	public void text_通讯码_OnBlur()  throws Exception{
		if(combo_功能号.getPrefix().equals("3")||combo_功能号.getPrefix().equals("4")){
			CommCodeDM commCodeDM=commBiz.findCommCode(text_通讯码.getText());
			putContext("id1", commCodeDM.getId());
			 if(commCodeDM == null){
			    	pushError("无此通讯码");
			    	return;
			    }
			combo_系统代码.setPrefix(commCodeDM.getSystemCode());
			text_交互码.setText(commCodeDM.getTransCode());
			combo_渠道代码.setPrefix(commCodeDM.getChannelCode());
			text_i表.setText(commCodeDM.getItable());
			text_o表.setText(commCodeDM.getOtable());
			combo_通讯标志.setPrefix(commCodeDM.getFlag()+"");
			text_超时时间.setText(commCodeDM.getTimeOut()+"");
			text_备注.setText(commCodeDM.getRemark());	
		}	
	}
	@Override
	protected void changeViewStyle(String commCode) {
		
	}
	/**
	* @ABFEditor#button_退出
	*/  
	public void button_退出_OnClick()  throws Exception{
		exit(0);
	}
}
