package jsrccb.common.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.agree.ab.lib.dm.BasicDM;

/**
 * 柜面交易表格翻页属性
 * @author zhangyajun
 *
 * @param <T>
 */
public class PageDM<T> extends BasicDM {
	private static final long serialVersionUID = -6032514605013715349L;
	
	private Map<String, Object>     pageContext      = new HashMap<String, Object>();
	private boolean                 finished         = false;
	private Map<String, PageDataDM> tablePageDataMap = new HashMap<String, PageDataDM>();
	private PageAction  pageAction;
	
	public Map<String, Object> getPageContext() {
		return pageContext;
	}
	public void setPageContext(Map<String, Object> pageContext) {
		this.pageContext = pageContext;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public PageAction getPageAction() {
		return pageAction;
	}
	public void setPageAction(PageAction pageAction) {
		this.pageAction = pageAction;
	}
	public PageDataDM getPageDataDM(String tableId) {
		PageDataDM pageDataDM = tablePageDataMap.get(tableId);
		if (pageDataDM == null) {
			pageDataDM = new PageDataDM();
			tablePageDataMap.put(tableId, pageDataDM);
		}
		return pageDataDM;
	}
	

	public class PageDataDM extends BasicDM {
		private static final long serialVersionUID = 3230548479915342928L;
		private List<List<T>> data	   = new ArrayList<List<T>>();
		private int           currentPageNum;
		
		private PageDataDM() {
		}
		
		
		public int  getCurrentPageNum() {
			return currentPageNum;
		}
		public void setCurrentPageNum(int currentPageNum) {
			this.currentPageNum = currentPageNum;
		}
		public void addPageData(List<T> onePageData) {
			this.data.add(onePageData);
		}
		/**
		 * 获取指定页码的数据
		 * @param pageNum 页码大于0的整数
		 * @return
		 */
		public List<T> getPageData(int pageNum) {
			if (data.isEmpty())
				return null;
			pageNum = pageNum - 1;
			if (pageNum < 0)
				pageNum = 0;
			if (pageNum >= data.size())
				pageNum =  data.size()-1;
			return data.get(pageNum);
		}
		public List<T> getAllData() {
			List<T> list = new ArrayList<T>();
			for (List<T> _data_ : data) {
				if (_data_ != null)
					list.addAll(_data_);
			}
			return list;
		}
		public int getPageSize() {
			return data.size();
		}
		
	}

	public enum PageAction {
		HOME(0, "首页"), PREVIOUS(1, "上一页 "), NEXT(2, "下一页"), END(3, "尾页"), NUMBER(4, "指定页码"),
		PRITABLE(5,"终端打印"), PRITABLP(6,"流水打印");
		
		private int    code;
		private String desc;
		
		private PageAction(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		public int getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
	}
}
