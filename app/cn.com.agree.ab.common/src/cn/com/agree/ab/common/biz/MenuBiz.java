package cn.com.agree.ab.common.biz;

import java.util.List;

import cn.com.agree.ab.common.dm.MenuDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.lib.dm.TreeNode;

public interface MenuBiz {
	
	public List<TreeNode<MenuDM>> getAllMenu();

	public List<TreeNode<MenuDM>> filterMenu(TradeDataDM tradeDataDM, String teller);
}
