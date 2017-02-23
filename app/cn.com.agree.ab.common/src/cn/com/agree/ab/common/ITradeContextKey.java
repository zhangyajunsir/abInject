package cn.com.agree.ab.common;

public interface ITradeContextKey {
	/**交易码 */
	public static final String TRADE_CODE_DM            = "tradeCodeDM";
	/**交易属性 */
	public static final String TRADE_PROP_DM            = "tradePropDM";
	/**授权属性 */
	public static final String AUTH_DM                  = "authDM";
	/**分页属性 */
	public static final String PAGING_DM                = "pagingDM";
	/**复核属性 */
	public static final String REVIEW_DM                = "reviewDM";
    /**通讯日志KEY*/
	public static final String COMM_LOG_DM              = "commLogDM";
	/**交易数据KEY*/
	public static final String TRADE_DATA_DM            = "tradeDataDM";
	/**交易间联动映射关系*/
	public static final String RELATION_MAPPING         = "relationMapping";
}
