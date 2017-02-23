package cn.com.agree.ab.lib.dao;

import cn.com.agree.ab.trade.ext.persistence.Record;


/**
 * Record行数据映射对象回调接口
 */
public interface RecordMapper<R> {
	
	/**
	 * Record行数据映射对象回调方法
	 *
	 * @param map    行数据的Record对象
	 * @param index  记录序号
	 * @return R R指定类型的行数据
	 */
	public R recordRow(Record record, int index);

}
