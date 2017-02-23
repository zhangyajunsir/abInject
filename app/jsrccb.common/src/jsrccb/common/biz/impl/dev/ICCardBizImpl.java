package jsrccb.common.biz.impl.dev;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;

import jsrccb.common.biz.dev.ICCardBiz;
import jsrccb.common.dm.dev.AccountDM;
import jsrccb.common.dm.dev.ICCardDM;
import cn.com.agree.ab.common.ITradeKeys;
import cn.com.agree.ab.common.biz.impl.AbstractDeviceBiz;
import cn.com.agree.ab.common.exception.DevException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.exception.BizException;
import cn.com.agree.ab.lib.utils.DateUtil;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.device.IIcCard;
import cn.com.agree.ab.trade.core.tools.FileUtil;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ICCardBiz.class)
@Singleton
@Biz("icCardBiz")
public class ICCardBizImpl extends AbstractDeviceBiz implements ICCardBiz {
	
	//目前交易都是强制读卡
	@Override
	public void writeICCard(Trade trade, boolean must, AccountDM accountDM) {
//		String result = "";
//		int time = 0;
//		String sMsgWndName;
//		try {
//			sMsgWndName = trade.pushInfoWithoutButton("写IC卡中.....");
//			time++;
//			//写入数据  具体  OPM-TX-LOG-NO 对应AccountDM 那个属性？？？
//			/* psData 授权金额，其他金额，交易货币代码，交易日期，交易类型，交易时间，商户名称 # 
//			 * "P012000000000613" "Q012000000000613" "R003156"  "S00820110420" "T00260" "U006112233" "W012321321321321"
//			 */
//			String data55 = Sundry.FixFill(ps55Data.length() + "", '0', '+', 3) + ps55Data + trade.getStoreData("cardBuffer");
//			result = runClientDev(trade.getDeviceManager(), "libiccard", 30, 3, data55);
//			trade.closeInfo(sMsgWndName);
//			if (result == null) {
//				trade.showError("写卡失败！");
//				return false;
//				/*return new String[] {"0",Sundry.substr(info[1], 21, info[1].getBytes().length - 20) }; // 写卡失败，
//				// 返回信息，
//				// 用于圈存冲正*/
//			}else{
//				String[] info = result.split("\\|", 10);
//				trade.pushInfo("写卡成功！");
//				trade.putStoreData("succFlag", info[1]);
//				//Sundry.substr(info[1], 21, info[1].getBytes().length - 20)
//				return true;
//				//return new String[] { "1", fenge[1] }; // 写卡成功
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return false;
	}
	/**
     	0- 账号|
		1- IC卡序列号|
		2- 姓名|
		3- 证件类型|
		4- 证件号码|
		5- 二磁道信息|
		6- 余额 |
		7- 余额上限|
		8- 失效日期
		9- 55区的数据
		10- 55域长度（3位）+55域数据+IC卡buffer变量
		11- IC卡buffer数据
		实现老图形功能的readAllIcCard方法
	*/
	@Override
	public ICCardDM readICCard(Trade trade, boolean must) {
		
		String psData = "";
		
		// 返回格式yyyyMMddHHmmssSSS
		String abTime = DateUtil.getDateToString();
		String timeResult = abTime.substring(8, 14);   //时间   取HHmmss
		psData = "P012000000000000";                 // #授权金额
		psData = psData + "Q012000000000000";        // #其他金额
		psData = psData + "R003156";                 // #交易货币代码
		psData = psData + "S008" + trade.getTellerInfo().get(ITradeKeys.G_DATE).toString(); // 交易日期   
		psData = psData + "T00201";                  // #交易类型，管理类型
		psData = psData + "U006" + timeResult;       // #交易时间
		psData = psData + "W000";// //#商户名称
		
		int time = 0;
		String result = null;
		boolean confirmFlag = true;
		while (true) {
			try {
				if (time >= 3) {
					trade.isUserConfirmed("扫描IC卡已3次，请退出！");
					trade.exit(0);
				}
				String msgId = trade.pushInfoWithoutButton("请插入IC卡.....");
				time++;
				try {
					result = runClientDev(trade.getDeviceManager(), "libiccard", 30, 5, psData);
					//trade.pushInfo(result, true);
				} catch (DevException e) {
					confirmFlag = trade.isUserConfirmed("读卡异常，是否重读");
					if (!confirmFlag) {
						trade.exit(0);
					} 
					continue;
				} finally {
					trade.closeInfo(msgId);
				}
				if (result == null){
					if (!trade.isUserConfirmed("扫描IC卡(数据读取)失败，是否重读")) {
						trade.exit(0);
					} else
						continue;									
				}else {
					// 组成规则：
					// 账号| IC卡序列号|姓名|证件类型|证件号码|二磁道信息|余额
					// |余额上限|失效日期|55域长度（3位）+55域数据+IC卡buffer变量
					String[] info = result.split("\\|", 10);
					
					//取55域长度 (前3位)
					int data55Length =Integer.parseInt(info[9].substring(0, 3));     //取第0个位置到3个位置
					//55域数据
					String psData55 = info[9].substring(3, data55Length + 3);        //取第3个位置到data55Length+3 位置
					//IC卡buffer数据
					String icCardDateBuffer = info[9].substring(data55Length + 3);   //从data55Length+3 位置取后面的值
					
					ICCardDM icCardDM = new ICCardDM();
					trade.putStoreData("sIcCardSeqid", info[1]);
					icCardDM.setAccount(info[0]);            //账号
					icCardDM.setIcCardseq(info[1]);          //IC卡序列号
					icCardDM.setName(info[2]);               //姓名
					icCardDM.setIdType(info[3]);             //证件类型
					icCardDM.setIdNo(info[4]);               //证件号码
					icCardDM.setInfo(info[5]);               //二磁道信息
					icCardDM.setBalance(info[6]);            //余额 
					icCardDM.setBalceiling(info[7]);         //余额上限
					icCardDM.setIneffectiveDate(info[8]);    //失效日期 
					icCardDM.setPs55data(psData55);          //55域数据
					icCardDM.setPsAll55date(info[9]);        //55域长度（3位）+55域数据+IC卡buffer变量
					icCardDM.setIcCardBuffer(icCardDateBuffer);   //IC卡buffer数据
					
					return icCardDM;
				}
			} catch (IOException e) {
				throw new BizException(e);
			}
			break;
		}
		return null;
	}

	@Override
	public List<String[]> readICCard10Data(Trade trade, boolean must) {
//		int time = 0;
//		String result = null;
//		boolean confirmFlag = true;
//		while (true) {
//			try {
//				if (time >= 3) {
//					trade.showError("扫描IC卡已3次，请退出！");
//					trade.exit(0);
//				}
//				String msgId = trade.pushInfoWithoutButton("请插入IC卡.....");
//				time++;
//				try {
//					result = runClientDev(trade.getDeviceManager(), "libiccard", 30, 4);
//					trade.pushInfo(result, true);
//				} catch (DevException e) {
//					confirmFlag = trade.isUserConfirmed("读卡异常，是否重读");
//					if (!confirmFlag) {
//						trade.exit(0);
//					} 
//					continue;
//				} finally {
//					trade.closeInfo(msgId);
//				}
//				if (result == null) {
//					if (!trade.isUserConfirmed("扫描IC卡失败，是否重读")) {
//						trade.exit(0);
//					} else
//						continue;
//				}else{
//					String[] info = result.split("\\|", 10);
//					try {
//						if ("".equals(info[1].trim()) || info[1].trim() == null) {
//							trade.pushInfo("IC卡没有数据", true);
//							return null;
//						}
//						
//						// 查看共有几笔数据，且有没有写入数据
//						String sSubTwo = "";
//						int iSubTwo = 0;
//						sSubTwo = Sundry.substr(info[1].trim(), 1, 2);
//						iSubTwo = Integer.parseInt(sSubTwo);
//						// 每笔报文长度
//						String sSubLength = "";
//						int iSubLength = 0;
//						sSubLength = Sundry.substr(info[1].trim(), 3, 3);
//						iSubLength = Integer.parseInt(sSubLength);
//				
//						// 报文长度组合
//						String psAllInfoTmp = "";
//						String psAllInfo = "";
//						int iFristPost = 6; // 首次截取位置
//						for (int i = 0; i < iSubTwo; i++) {
//							psAllInfoTmp = Sundry.substr(info[1].trim(), iFristPost, iSubLength);
//							iFristPost = iFristPost + iSubLength; // 循环截取
//							psAllInfo = psAllInfo + psAllInfoTmp + "|";
//							
//						}
//						// sSubTwo :笔数 ; sSubLength:每笔长度; psAllInfo:所有报文且每一笔报文以"|"进行分隔
//						return new String[] { sSubTwo, sSubLength, psAllInfo };
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			} catch (IOException e) {
//				throw new BizException(e);
//			}
//			break;
//		}
		return null;
	}

	@Override
	public String readICCard55Area(Trade trade, boolean must, String condition) {
//		int time = 0;
//		String result = "";
//		boolean confirmFlag = true;
//		while (true) {
//			try {
//				if (time >= 3) {
//					trade.showError("扫描IC卡已3次，请退出！");
//					trade.exit(0);
//				}
//				String msgId = trade.pushInfoWithoutButton("请插入IC卡.....");
//				time++;
//				try {
//					result = runClientDev(trade.getDeviceManager(), "libiccard", 30, 2, condition);
//					trade.pushInfo(result, true);
//
//				} catch (DevException e) {
//					confirmFlag = trade.isUserConfirmed("读卡异常，是否重读");
//					if (!confirmFlag) {
//						trade.exit(0);
//					} 
//					continue;
//				} finally {
//					trade.closeInfo(msgId);
//				}
//				if (result == null) {
//					if (!trade.isUserConfirmed("扫描IC卡失败(数据返回失败)，是否重读")) {
//						if (must) {
//							trade.exit(0);
//						} else {
//							return null;
//						}
//					} else
//						continue;
//				}else{
//					String[] info = result.split("\\|", 10);
//					// 账号| IC卡序列号|姓名|证件类型|证件号码|二磁道信息|余额
//					// |余额上限|失效日期|55域长度（3位）+55域数据+IC卡buffer变量
//					String temp = info[1];
//					String psData55;
//					try {
//						psData55 = Sundry.substr(temp, 4,Integer.parseInt(Sundry.substr(info[1], 1, 3)));
//						// int m = 4 + psData55.length();
//						// String cardBuffer = Sundry.substr(result, m, result.length() - m); //
//						// IC卡buffer数据
//						String cardBuffer = Sundry.substr(trade, temp, psData55.length() + 3);
//						
//						// 写卡时会用到
//						trade.putStoreData("cardBuffer", cardBuffer);
//						return psData55;
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			} catch (IOException e) {
//				throw new BizException(e);
//			}
//			break;
//		}
		
		return null;
	}

	@Override
	public String getDeviceType() {
		return IIcCard.TYPE;
	}

}
