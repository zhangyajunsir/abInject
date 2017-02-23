package cn.com.agree.ab.common.biz;

import java.util.List;
import cn.com.agree.ab.common.dm.OrgDM;

public interface BranchBiz {
	
	public OrgDM getBranchInfo(String orgCode);
	
	public List<String> getSuperBranch(String orgCode);
	
	public void addOrUpdateOrgInfo(OrgDM orgDM);
	
}
