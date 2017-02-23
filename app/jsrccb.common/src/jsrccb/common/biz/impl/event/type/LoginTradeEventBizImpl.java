package jsrccb.common.biz.impl.event.type;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jsrccb.common.biz.OldBiz;
import jsrccb.common.biz.TerminalDeviceBiz;
import jsrccb.common.biz.dev.FingerBiz;
import jsrccb.common.biz.dev.MsfBiz;
import jsrccb.common.biz.dev.PinBiz;
import jsrccb.common.dm.PinKeyDM;
import jsrccb.common.dm.TerminalDeviceDM;
import jsrccb.common.dm.dev.AccountDM;
import jsrccb.common.exception.PwdException;

import org.slf4j.MDC;

import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.TellerBiz;
import cn.com.agree.ab.common.biz.impl.event.AbstractTradeTypeEventBiz;
import cn.com.agree.ab.common.dm.TellerDM;
import cn.com.agree.ab.common.utils.TradeHelper;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.delta.BooleanDelta;
import cn.com.agree.ab.delta.StringDelta;
import cn.com.agree.ab.key.IComponentKeys;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.biz.IViewOpenBiz;
import cn.com.agree.ab.lib.config.ConfigManager;
import cn.com.agree.ab.lib.dm.OpenViewArgDM;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.utils.ContextHelper;
import cn.com.agree.ab.task.TaskPlugin;
import cn.com.agree.ab.trade.local.TradeBreakException;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = AbstractTradeTypeEventBiz.class, multiple = true)
@Singleton
@Biz("loginTradeEventBizImpl")
public class LoginTradeEventBizImpl extends CommonTradeEventBizImpl {
	@Inject
	@Named("terminalDeviceBiz")
	private TerminalDeviceBiz terminalDeviceBiz;
	@Inject
	@Named("abViewOpenBiz")
	private IViewOpenBiz viewOpenBiz;
	@Inject
	@Named("msfBiz")
	private MsfBiz msfBiz;
	@Inject
	@Named("fingerBiz")
	private FingerBiz fingerBiz;
	@Inject
	@Named("oldBiz")
	private OldBiz oldBiz;
	@Inject
	@Named("tellerBiz")
	private TellerBiz tellerBiz;
	@Inject
	private ConfigManager configManager;
	@Inject
	@Named("pinBiz")
	protected PinBiz pinBiz;
	@Override
	public void onInit(AbstractCommonTrade trade) {
		super.onInit(trade);
		try {
			
			
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用登录交易初始化方法发生错误：" + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onCommit(AbstractCommonTrade trade, String message) {
		try {
			Map<String, Object> tellerInfo = trade.getTellerInfo();
			//查数据库配置判断是否校验指纹，获取指纹并校验
			boolean fingerFlag = false;
			TerminalDeviceDM terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)trade.getProperty("text_teller_number", IComponentKeys.TEXT), "G_FIN_FLAG");
			if (terminalDeviceDM == null || terminalDeviceDM.getTermValue() == null) {
				terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)trade.getProperty("text_branch_number", IComponentKeys.TEXT), "G_FIN_FLAG");
			}
			if (terminalDeviceDM != null && !"0".equals(terminalDeviceDM.getTermValue().trim())) {  //从数据库拿值后要trim
				fingerFlag = fingerBiz.checkTellerFinger(trade, true, (String)trade.getProperty("text_branch_number", IComponentKeys.TEXT), (String)trade.getProperty("text_teller_number", IComponentKeys.TEXT));
				if (!fingerFlag) {
					throw new BizException("指纹校验未通过");
				}
			}
			trade.getContext().put("fingerFlag", fingerFlag);
			// 0.软加密方式时，获得主密钥、工作密钥；硬加密方式时，获得设备ID、工作密钥
			PinKeyDM pinKeyDM = pinBiz.queryPinKey(trade);
			if (null != pinKeyDM) {
				tellerInfo.put(ITradeKeys.G_DEV_ID_GM, pinKeyDM.getGmDevId());
				tellerInfo.put(ITradeKeys.G_DEV_ID,    pinKeyDM.getDevId());
			}else{
				pinKeyDM = pinBiz.queryPinKey((String)tellerInfo.get(ITradeKeys.G_DEV_ID));
			}
			if (pinKeyDM == null || pinKeyDM.getMainKey() == null || pinKeyDM.getWorkKey() == null) {
				throw new BizException("获得主密钥和工作密钥失败");
			}
			tellerInfo.put(ITradeKeys.G_MAIN_PWDKEY, pinKeyDM.getMainKey());
			tellerInfo.put(ITradeKeys.G_WORK_PWDKEY, pinKeyDM.getWorkKey());
			trade.updateTellerInfo(tellerInfo);
			// 1.提交
			super.onCommit(trade, message);
			// 2.更新柜员信息（包括权限）到本地库
			final TellerDM tellerDM = assemblyTellerDM(trade);
			final Map mdcMap = MDC.getCopyOfContextMap();
			TaskPlugin.getDefault().execute(new Runnable() {
				@Override
				public void run() {
					// 放在业务代码之前清理（选择性清理），是因为线程池初创新进程会复制父进程里的变量
					ContextHelper.destory();
					MDC.setContextMap(mdcMap);
					// end
					tellerBiz.addOrUpdateTeller(tellerDM);
				}
			});
			
			// 3.更新机构信息在机构签到交易调用
			// 4.插入柜员登陆日志
			oldBiz.updateTellerLogin((String)tellerInfo.get(ITradeKeys.G_TELLER),   tellerInfo.get(ITradeKeys.G_QBR)+"A01"+tellerInfo.get(ITradeKeys.G_TTYNO));
			// 5.登录清理
			oldBiz.clearSummon((String)tellerInfo.get(ITradeKeys.G_TELLER), (String)tellerInfo.get(ITradeKeys.G_DATE));
			oldBiz.clearTLmsg ((String)tellerInfo.get(ITradeKeys.G_TELLER));
			// 6.打开主交易
			viewOpenBiz.exitOpenView(trade, "frame", null);
		} catch (PwdException e) {
			// 1.密码到期强制修改密码
			try {
				trade.pushWarning(e.getMessage(), true);
			} catch (IOException e1) {
			}
			OpenViewArgDM openViewArg = new OpenViewArgDM();
			openViewArg.setWindow(true);
			viewOpenBiz.syncOpenView(trade, "60305", openViewArg);
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用登录交易提交方法发生错误：" + e.getMessage(), e);
		}
	}

	@Override
	public void onMessage(AbstractCommonTrade trade, String app, String message) {
		super.onMessage(trade, app, message);
		// -
		
	}

	@Override
	public void onResume(AbstractCommonTrade trade, Map suspendResult) {
		super.onResume(trade, suspendResult);
		// -
		
	}

	@Override
	public void preEvent(AbstractCommonTrade trade, Method eventMethod) {
		super.preEvent(trade, eventMethod);
		try {
			if (eventMethod.getName().equals("text_teller_number_OnFocus")) {
				TerminalDeviceDM terminalDeviceDM = terminalDeviceBiz.findTerminalDevice((String)trade.getTellerInfo().get(ITradeKeys.G_QBR), "G_CARD_FLAG");
				if (terminalDeviceDM != null && "1".equals(terminalDeviceDM.getTermValue())) {
					AccountDM accountDM = msfBiz.readTellerCard(trade, true);
					if ("1".equals(configManager.getUtilIni().getValue("MSF.TRLMSF"))) {
						trade.delta(new BooleanDelta("text_teller_number", IComponentKeys.EDITABLE, false));
					}
					if (accountDM!=null) {
						trade.delta(new StringDelta("text_teller_number", IComponentKeys.TEXT, accountDM.getAccount() == null ? "" : accountDM.getAccount().toUpperCase()));
						trade.putStoreData("tellerCardFlag", "1");
						trade.putStoreData("tellerCardNo", accountDM.getBvCode());
					}
				}
				return;
			}
			
			
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用" + eventMethod.getName() + "方法发生错误：" + e.getMessage(), e);
		}
	}

	@Override
	public void posEvent(AbstractCommonTrade trade, Method eventMethod) {
		try {
			if (eventMethod.getName().equals("button_exit_OnClick")) {
				trade.command("closeClient", "");
				return;
			}
			if (eventMethod.getName().equals("text_teller_number_OnBlur")) {
				@SuppressWarnings("unchecked")
				Map<String, Object> tellerInfo = trade.getTellerInfo();
				tellerInfo.put(ITradeKeys.G_PIN_KEY, "0123456789123456");
				tellerInfo.put(ITradeKeys.G_MAC_KEY, "0123456789123456");
				tellerInfo.put(ITradeKeys.G_DATE,    "");
				tellerInfo.put(ITradeKeys.G_TTYNO,   trade.getProperty("text_terminal_number", IComponentKeys.TEXT));
				tellerInfo.put(ITradeKeys.G_QBR,     trade.getProperty("text_branch_number", IComponentKeys.TEXT));
				tellerInfo.put(ITradeKeys.G_TELLER,  trade.getProperty("text_teller_number", IComponentKeys.TEXT));
				tellerInfo.put("G_POSTEL",           trade.getProperty("text_teller_number", IComponentKeys.TEXT).toString().substring(9, 12));
				tellerInfo.put(ITradeKeys.G_DEV_ID,  "gt."+trade.getProperty("text_branch_number", IComponentKeys.TEXT)+".zpk");
				trade.updateTellerInfo(tellerInfo);
				// 初始化通讯，同时给全局别名添加相关信息
				trade.nestedCommun("CM0999900");
				return;
			}
		} catch (BasicRuntimeException e) {
			throw e;
		} catch (TradeBreakException e) {
			throw e;
		} catch (Exception e) {
			throw new BizException("调用" + eventMethod.getName() + "方法发生错误：" + e.getMessage(), e);
		}
		super.posEvent(trade, eventMethod);
	}

	@Override
	public void preExit(AbstractCommonTrade trade) {
		super.preExit(trade);
		// -
		
	}

	@Override
	public void posExit() {
		
		// -
		super.posExit();
	}

	@Override
	public String getType() {
		return "login"; // 登陆类型
	}

	@Override
	public String getBranch() {
		return ""; // 全法人
	}
	
	protected void posCommit(AbstractCommonTrade trade, String message) {
		// 覆盖掉父类的画面控制
	}
	
	@SuppressWarnings("unchecked")
	private TellerDM assemblyTellerDM(AbstractCommonTrade trade) {
		Map<String, Object> tellerInfo = trade.getTellerInfo();
		String[] SCM04350 = (String[])trade.getTempArea("SCM04350");
		TellerDM tellerDM = new TellerDM();
		tellerDM.setCode((String)tellerInfo.get(ITradeKeys.G_TELLER));
		tellerDM.setPassword(trade.getStoreData("T_TELLER_PASSWORD"));
		// 员工号
		tellerDM.setEmployeeCode(SCM04350[0]);
//		tellerDM.setEmployeePassword(employeePassword);
		// 所属机构
		tellerDM.setOrgCode((String)tellerInfo.get(ITradeKeys.G_QBR));
/*		tellerDM.setDepartmentNo(departmentNo);
		tellerDM.setDepartment(department);*/
		// 基本信息
		tellerDM.setName((String)tellerInfo.get(ITradeKeys.G_TELLER_NAME));
/*		tellerDM.setEnglishName(englishName);
		tellerDM.setSex(sex);
		tellerDM.setTelphone(telphone);
		tellerDM.setMobile(mobile);*/
		tellerDM.setIdCard(SCM04350[3]);
		// 密码相关
/*		tellerDM.setPasswordChangeDate(passwordChangeDate);
		tellerDM.setPasswordFailureDate(passwordFailureDate);
		tellerDM.setPasswordLockNum(passwordLockNum);*/
		// 行内信息
//		tellerDM.setPost(post);
		tellerDM.setType (SCM04350[6] == null || SCM04350[6].equals("") ? 0 : Integer.valueOf(SCM04350[6]));
//		tellerDM.setLevel(SCM04350[5] == null || SCM04350[5].equals("") ? 0 : Integer.valueOf(SCM04350[5]));	// 返回可能是A\B
/*		tellerDM.setRoleId(roleId);
		tellerDM.setBoxNo(boxNo);
		tellerDM.setTellerAttribute(tellerAttribute);
		tellerDM.setAuthenCode(authenCode);
		tellerDM.setCustomerManagerNo(customerManagerNo);
		tellerDM.setSalaryAccNo(salaryAccNo);*/
		// 临时信息
/*		tellerDM.setTempType(tempType);
		tellerDM.setTempLevel(tempLevel);
		tellerDM.setTempFailureDate(tempFailureDate);*/
		// 柜员卡
		tellerDM.setTellerCardFlag("1".equals(trade.getStoreData("tellerCardFlag")) ? 1 : 0);
		tellerDM.setTellerCardNo(trade.getStoreData("tellerCardNo"));
//		tellerDM.setTellerCardStatus(tellerCardStatus);
		// OID
		tellerDM.setLoginOid(trade.getStoreData("G_ABC_OID"));
		tellerDM.setLoginIp(trade.getStoreData("G_ABC_IP"));
		// 同步
/*		tellerDM.setStatus(status);
		tellerDM.setStartDate(startDate);
		tellerDM.setEndDate(endDate);
		tellerDM.setCoreSynchFlag(coreSynchFlag);
		tellerDM.setCoreSynchStatus(coreSynchStatus);*/
		// 柜员权限
		List<Integer> tellerPostIds = new ArrayList<Integer>();
		char[] tellerPosts = ((String)tellerInfo.get("G_POST_MASK")).toCharArray();
		for (int i=0; i<tellerPosts.length; i++) {
			if (tellerPosts[i] == '1')
				tellerPostIds.add(i+1);	// 数据库中岗位id与此处的index是一样
		}
		tellerDM.setTellerPosts(tellerPostIds);
		return tellerDM;
	}
	
}
