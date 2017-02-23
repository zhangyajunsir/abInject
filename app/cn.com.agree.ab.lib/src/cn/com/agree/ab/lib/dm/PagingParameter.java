package cn.com.agree.ab.lib.dm;

/**
 * 说明：
 * 封装分页参数
 * 创建日期：2012-7-11
 * @author niezg
 */
public class PagingParameter extends BasicDM {
	private static final long serialVersionUID = -5871263750693828476L;

	/** 分页起始行，默认为-1，表示不分页，查询全部记录 */
	private int start = -1;
	/** 每页显示行数，默认为0，表示不分页，查询全部记录 */
	private int limit = 0;
	
	/**
	 * 构造方法，不指定分页起始行和每页显示行数，默认不分页，查询全部记录
	 * 创建日期：2012-7-11
	 * 修改说明：
	 * @author niezg
	 */
	public PagingParameter(){
	}
	
	/**
	 * 构造方法
	 * @param start
	 * @param limit
	 * 创建日期：2012-7-11
	 * 修改说明：
	 * @author niezg
	 */
	public PagingParameter(int start,int limit){
		this.start = start;
		this.limit = limit;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * 判断分页参数是否无效，如果返回true(表示分页参数无效)则不分页，查询全部的记录
	 *
	 * @return
	 * 创建日期：2013-1-17
	 * 修改说明：
	 * @author wangk
	 */
	public boolean isInvalid() {
		return start < 0 || limit <= 0;
	}

}
