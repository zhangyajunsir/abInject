package cn.com.agree.ab.lib.rpc.packet.metadata;

import cn.com.agree.ab.lib.rpc.packet.Formatter;
import cn.com.agree.ab.lib.rpc.packet.Parser;

/**
 * 报文不可分单元
 * @author zhangyajun
 *
 * @param <T>
 */
public interface UnitMetadata<T, K> extends Metadata<T>, Formatter<T, K>, Parser<K, T> {
	
	/**
	 * 元信息别名
	 * @return
	 */
	public String getAlias();
	/**
	 * 元信息描述
	 * @return
	 */
	public String getDesc();
	/**
	 * 获取编码集
	 * @return
	 */
	public String getEncoding();
	/**
	 * 设置元信息别名
	 * @param alias
	 */
	public void setAlias(String alias);
	/**
	 * 设置圆心描述
	 * @param desc
	 */
	public void setDesc(String desc);
	/**
	 * 设置编码集
	 * @param encoding
	 */
	public void setEncoding(String encoding);
	
}
