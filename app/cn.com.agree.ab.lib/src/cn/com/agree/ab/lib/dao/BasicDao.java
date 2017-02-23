package cn.com.agree.ab.lib.dao;

import java.util.List;
import java.util.Map;

import cn.com.agree.ab.lib.dm.DataStore;
import cn.com.agree.ab.lib.dm.PagingParameter;
import cn.com.agree.ab.trade.ext.persistence.Where;

/**
 * 数据查询DAO接口
 *
 * 创建日期：2012-9-26
 * @author wangk
 */
public interface BasicDao {
	
	/**
	 * 查询SQL语句
	 *
	 * @param sql                        SQL语句
	 * @param params                     SQL参数
	 * @return List<Map<String, Object>> 查询结果
	 * 创建日期：2012-9-26
	 * 修改说明：
	 * @author wangk
	 */
	public List<Map<String, Object>> search(String sql, Object... params);
	public List<Map<String, Object>> search(String sql, List<Object> params);
	public List<Map<String, Object>> search(String sql, Map<String, Object> params);
	
	/**
	 * 查询SQL语句
	 *
	 * @param <R>           行记录类型
	 * @param sql           SQL语句
	 * @param mapRowMapper  Map行数据映射对象
	 * @param params        SQL参数 
	 * @return List<R>      查询结果
	 * 创建日期：2012-9-26
	 * 修改说明：
	 * @author wangk
	 */
	public <R> List<R> search(String sql, RecordMapper<R> mapRowMapper, Object... params);
	public <R> List<R> search(String sql, RecordMapper<R> mapRowMapper, List<Object> params);
	public <R> List<R> search(String sql, RecordMapper<R> mapRowMapper, Map<String, Object> params);
	
	/**
	 * 分页查询数据
	 *
	 * @param sql                             SQL语句
	 * @param paging                          分页参数
	 * @param params                          SQL参数
	 * @return DataStore<Map<String, Object>> 分页数据
	 * 创建日期：2012-9-26
	 * 修改说明：
	 * @author wangk
	 */
	public DataStore<Map<String, Object>> search(String sql, PagingParameter paging, Object... params);
	public DataStore<Map<String, Object>> search(String sql, PagingParameter paging, List<Object> params);
	public DataStore<Map<String, Object>> search(String sql, PagingParameter paging, Map<String, Object> params);
	
	/**
	 * 分页查询数据
	 *
	 * @param <R>           行记录类型
	 * @param sql           SQL语句
	 * @param mapRowMapper  Map行数据映射对象
	 * @param paging        分页参数
	 * @param params        SQL参数
	 * @return DataStore<R> 分页数据
	 * 创建日期：2012-9-26
	 * 修改说明：
	 * @author wangk
	 */
	public <R> DataStore<R> search(String sql, RecordMapper<R> mapRowMapper, PagingParameter paging, Object... params);
	public <R> DataStore<R> search(String sql, RecordMapper<R> mapRowMapper, PagingParameter paging, List<Object> params);
	public <R> DataStore<R> search(String sql, RecordMapper<R> mapRowMapper, PagingParameter paging, Map<String, Object> params);

	/**
	 * 连接查询
	 *
	 * @param condition                  查询条件，指定列格式：表名.列名
	 * @param classLink                  连接实体类类型链
	 * @return List<Map<String, Object>> 查询结果，Map.key: 对象名(Class指定的类名的首字母小写形式).属性名
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public List<Map<String, Object>> join(Where condition, Class<?>... classLink);
	
	/**
	 * 连接查询
	 *
	 * @param condition 查询条件
	 * @param orders    排序对象
	 * @param classLink 连接实体类类型链
	 * @return          查询结果
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public List<Map<String, Object>> join(Where condition, String orders, Class<?>... classLink);
	
	/**
	 * 连接查询（内连接）
	 *
	 * @param <R>          映射类型参数
	 * @param condition    查询条件
	 * @param mapRowMapper 行匹配对象
	 * @param classLink    连接实体类型链（从子表到父表的顺序）
	 * @return
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public <R> List<R> join(Where condition, RecordMapper<R> mapRowMapper, Class<?>... classLink);	
	
	/**
	 * 连接查询
	 *
	 * @param <R>          映射类型参数
	 * @param condition    查询条件
	 * @param orders       排序对象
	 * @param mapRowMapper 行匹配对象
	 * @param classLink    连接实体类型链
	 * @return
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public <R> List<R> join(Where condition, String orders, RecordMapper<R> mapRowMapper, Class<?>... classLink);	
	
	/**
	 * 连接查询
	 *
	 * @param condition   查询条件
	 * @param paging      分页参数
	 * @param classLink   连接实体类型链
	 * @return
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public DataStore<Map<String, Object>> join(Where condition, PagingParameter paging, Class<?>... classLink);
	
	/**
	 * 连接查询
	 *
	 * @param condition  查询条件
	 * @param orders     排序对象
	 * @param paging     分页参数
	 * @param classLink  连接实体类型链
	 * @return
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public DataStore<Map<String, Object>> join(Where condition, String orders, PagingParameter paging, Class<?>... classLink);
	
	/**
	 * 连接查询
	 *
	 * @param <R>          映射类型参数
	 * @param condition    查询条件
	 * @param mapRowMapper 行匹配对象
	 * @param paging       分页参数
	 * @param classLink    连接实体类型链
	 * @return
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public <R> DataStore<R> join(Where condition, RecordMapper<R> mapRowMapper, PagingParameter paging, Class<?>... classLink);
	
	/**
	 * 连接查询
	 *
	 * @param <R>          映射类型参数
	 * @param condition    查询条件
	 * @param orders       排序对象
	 * @param mapRowMapper 行匹配对象
	 * @param paging       分页参数
	 * @param classLink    连接实体类型链
	 * @return
	 * 创建日期：2012-10-19
	 * 修改说明：
	 * @author wangk
	 */
	public <R> DataStore<R> join(Where condition, String orders, RecordMapper<R> mapRowMapper, PagingParameter paging, Class<?>... classLink);

}
