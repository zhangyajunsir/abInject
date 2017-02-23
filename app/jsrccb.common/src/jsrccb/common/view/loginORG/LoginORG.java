package jsrccb.common.view.loginORG;

import javax.inject.Inject;
import javax.inject.Named;
import jsrccb.common.biz.OldBiz;
import jsrccb.common.biz.dev.PinBiz;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.BranchBiz;
import cn.com.agree.ab.common.dm.OrgDM;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.trade.core.component.*;

public class LoginORG extends AbstractCommonTrade {
	
	@Inject
	@Named("oldBiz")
	private OldBiz oldBiz;
	
	@Inject
	@Named("pinBiz")
	private PinBiz pinBiz;
	
	@Inject
	@Named("branchBiz")
	private BranchBiz branchBiz;
	
	
	public DateText datetext_business_date = new DateText(this, "datetext_business_date");
	public Label label_business_date = new Label(this, "label_business_date");
	public Label label_function_number = new Label(this, "label_function_number");
	public ComboBox combo_function_number = new ComboBox(this, "combo_function_number");
	public Composite composite1 = new Composite(this, "composite1");
	public Button button_exit = new Button(this, "button_exit");

	public Button button_submit = new Button(this, "button_submit");
	@Override
	public void onInit() throws Exception {
	
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

	public void posCommit(String message){
		String devID = getStoreData(ITradeKeys.G_DEV_ID);
		String orgcode = getStoreData(ITradeKeys.G_QBR);
		String date = getStoreData(ITradeKeys.G_DATE);
		nestedCommun("CM0000802");
		Object[] result = (Object[]) getTempArea("SCM00802");
		//更新密钥
		pinBiz.syncPinKey(devID) ;
		//清理ORGNUM数据表
		oldBiz.clearOrgNum(orgcode, date) ;
		//设置机构的相关信息
		OrgDM orgDM = setOrgDM(result);
		//更新机构信息
		branchBiz.addOrUpdateOrgInfo(orgDM);
	}
	/**
	* @ABFEditor#button_login
	*/  
	public void button_submit_OnClick()  throws Exception{
		onCommit();	
	}
	
	/**设置机构表字段的数据*/
	private static OrgDM setOrgDM(Object[] result){
		
		OrgDM orgDM = new OrgDM();
		orgDM.setCode(result[0].toString());
		orgDM.setType(Integer.parseInt(result[10].toString()));
		orgDM.setAddress(result[22].toString());
		orgDM.setName(result[1].toString());
		orgDM.setPhone(result[7].toString());
		orgDM.setShortName1(result[2].toString());
		orgDM.setShortName2(result[3].toString());
		
		return orgDM ;
	}

	public void combo_function_number_OnBlur()  throws Exception{
		nestedCommun("CM0060201");
		if("1".equals(combo_function_number.getPrefix())){
			setDefaultCommCode("CM0002001");
			
		}else{
			setDefaultCommCode("CM0002002");
			
		}
	}

}