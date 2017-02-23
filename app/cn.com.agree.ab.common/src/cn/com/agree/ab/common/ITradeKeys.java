package cn.com.agree.ab.common;

/**
 * T开头的代表交易内范围，主要作为StoreData的key
 * G开头的代表柜员内范围，主要作为TellerInfo的key
 * http://blog.csdn.net/feiyu8607/article/details/7064751
 * @author zhangyajun
 *
 */
public interface ITradeKeys {
	/**交易主控流水 */
	public static final String T_SEQNO               = String.valueOf("G_SEQNO");	// 平台使用G_SEQNO作为交易执行流水
	/**默认主通讯码*/                                      
    public static final String T_COMM_CODE           = String.valueOf("T_COMM_CODE");
    /**进入交易的表达式ID*/
    public static final String T_TRADE_EXPRESSION_ID = String.valueOf("T_TRADE_EXPRESSION_ID");
	/**交易码*/
    public static final String T_TRADE_CODE          = String.valueOf("T_TRADE_CODE");
    /**交易名称*/
    public static final String T_TRADE_NAME          = String.valueOf("T_TRADE_NAME");
    /**交易码表ID*/
    public static final String T_TRADE_ID            = String.valueOf("T_TRADE_ID");
    /**同步打开后继交易时传入的当前交易tradeId*/
    public static final String T_SOURCE_PLATFORM_ID  = String.valueOf("T_SOURCE_PLATFORM_ID");
    /**交易执行状态*/
    public static final String T_TRADE_STATUS        = String.valueOf("T_TRADE_STATUS");
	/**会计日期(格式yyyymmdd) */                          
	public static final String G_DATE                = String.valueOf("G_DATE");
	/**会计日期（年）*/                                     
    public static final String G_YEAR                = String.valueOf("G_YEAR");
    /**会计日期（月）*/                                     
    public static final String G_MONTH               = String.valueOf("G_MONTH");
    /**会计日期（日）*/                                     
    public static final String G_DAY                 = String.valueOf("G_DAY");
    /**机构号*/                                         
    public static final String G_QBR                 = String.valueOf("G_QBR");
    /**机构名称*/                                        
    public static final String G_QBR_NAME            = String.valueOf("G_QBR_NAME");
    /**柜员姓名*/                                        
    public static final String G_TELLER_NAME         = String.valueOf("G_TELLER_NAME");
    /**柜员级别*/                                        
    public static final String G_TELLER_LEVEL        = String.valueOf("G_TELLER_LEVEL");
    /**柜员类型*/                                        
    public static final String G_TELLER_TYPE         = String.valueOf("G_TELLER_TYPE");
    /**柜员号*/                                         
    public static final String G_TELLER              = String.valueOf("G_TELLER");
    /**终端号*/                                         
    public static final String G_TTYNO               = String.valueOf("G_TTYNO");
    /**密码密钥*/                                        
    public static final String G_PIN_KEY             = String.valueOf("G_PIN_KEY");
    /**MAC密钥*/                                       
    public static final String G_MAC_KEY             = String.valueOf("MACKEY");
    /**设备号*/                                         
    public static final String G_DEV_ID              = String.valueOf("G_INM_ENC_DEV_ID");
    /**法人行*/                                         
    public static final String G_LVL_BRH_ID          = String.valueOf("G_LVL_BRH_ID");
    /**锁屏密码(密文)*/ 
    public static final String G_SCREEN_PWD          = String.valueOf("G_SCREEN_PWD");
    /**锁屏密码(明文)*/ 
    public static final String G_SCREEN_SOURCE_PWD   = String.valueOf("G_SCREEN_PWD_AHA");
    /**终端类型*/ 
    public static final String G_TERMINAL_TYPE       = String.valueOf("G_TERMINAL_TYPE");
    /**上次OID*/ 
    public static final String G_LAST_OID            = String.valueOf("G_LAST_OID");
    /**密码主密钥*/                                        
    public static final String G_MAIN_PWDKEY         = String.valueOf("G_MAIN_PWDKEY");
    /**密码工作密钥*/                                       
    public static final String G_WORK_PWDKEY         = String.valueOf("G_WORK_PWDKEY");
    /**冲正流水号*/                                       
    public static final String G_REVERSE_SEQNO       = String.valueOf("G_OLD_SERIALNO");
    /**国密设备号*/
    public static final String G_DEV_ID_GM              = String.valueOf("G_DEV_ID_GM");
    /**使用国密标志*/
    public static final String G_GM_FLAG              = String.valueOf("G_GM_FLAG");
}
