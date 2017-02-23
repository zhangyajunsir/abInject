package cn.com.agree.ab.common.biz.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.common.biz.ExpressionBizProvider;
import cn.com.agree.ab.common.biz.MenuBiz;
import cn.com.agree.ab.common.biz.TradeBiz;
import cn.com.agree.ab.common.dao.MenuDao;
import cn.com.agree.ab.common.dao.entity.MenuEntity;
import cn.com.agree.ab.common.dao.entity.MenuTradeEntity;
import cn.com.agree.ab.common.dm.MenuDM;
import cn.com.agree.ab.common.dm.MenuTradeDM;
import cn.com.agree.ab.common.dm.TradeCodeDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.ab.lib.annotation.Cacheable;
import cn.com.agree.ab.lib.dm.TreeNode;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = MenuBiz.class)
@Singleton
@Biz("menuBiz")
public class MenuBizImpl implements MenuBiz {
	private static final Logger	logger	= LoggerFactory.getLogger(MenuBizImpl.class);
	@Inject
	private ExpressionBizProvider expressionBizProvider;
	@Inject
	@Named("tradeBiz")
	private TradeBiz tradeBiz;
	@Inject
	@Named("menuDao")
	private MenuDao menuDao;

	@Override
	@Cacheable
	public List<TreeNode<MenuDM>> getAllMenu() {
		List<MenuEntity> menuEntitys = menuDao.findAllMenu();
		if (menuEntitys == null || menuEntitys.size() == 0)
			return null;
		List<MenuDM> menuDMList = new ArrayList<MenuDM>();
		for (MenuEntity menuEntity : menuEntitys) {
			MenuDM menuDM = new MenuDM();
			menuDM.cloneValueFrom(menuEntity);
			menuDMList.add(menuDM);
		}
		List<TreeNode<MenuDM>> trees            = createTree(menuDMList);
		List<MenuTradeEntity>  menuTradeEntitys = menuDao.findAllMenuTradeMapping();
		for (MenuTradeEntity menuTradeEntity : menuTradeEntitys) {
			MenuTradeDM menuTradeDM = new MenuTradeDM();
			menuTradeDM.cloneValueFrom(menuTradeEntity);
			for (TreeNode<MenuDM> treeRoot : trees) {
				boolean bo = addChild(treeRoot, menuTradeDM);
				if (bo)
					break;
			}
		}
		return trees;
	}
	
	/**
	 * 生成一颗多叉树，根节点为0
	 * 数据库中父节点记录必须在子节点记录之前
	 * @param menuDMList 生成多叉树的节点扁平集合
	 * @return ManyNodeTree
	 */
	private List<TreeNode<MenuDM>> createTree(List<MenuDM> menuDMList)
	{
		if(menuDMList == null || menuDMList.size() < 0)
			return null;
		
		List<TreeNode<MenuDM>> trees = new ArrayList<TreeNode<MenuDM>>();
		//将所有节点添加到多叉树中
		for(MenuDM menuDM : menuDMList)
		{
			if(menuDM.getParentId() == 0)
			{
				//向根添加一个节点
				trees.add(new TreeNode<MenuDM>(menuDM));
			}
			else
			{
				for (TreeNode<MenuDM> treeRoot : trees) {
					boolean bo = addChild(treeRoot, menuDM);
					if (bo)
						break;
				}
			}
		}
		
		return trees;
	}
	
	/**
	 * 向指定多叉树节点添加子菜单
	 * 
	 * @param manyTreeNode 多叉树节点
	 * @param child 节点
	 */
	@SuppressWarnings("unchecked")
	private boolean addChild(TreeNode<MenuDM> node, MenuDM      child)
	{
		if (child.getParentId() != null && child.getParentId().equals(node.getBean().getId())) {
			node.addChildNode(new TreeNode<MenuDM>(child));
			return true;
		}
		for(TreeNode<?> _item_ : node.getChildList())
		{
			TreeNode<MenuDM> item = (TreeNode<MenuDM>)_item_;
			boolean bo = addChild(item, child);
			if (bo)
				return true;
		}
		return false;
	}

	/**
	 * 向指定多叉树节点添加交易映射
	 * 
	 * @param manyTreeNode 多叉树节点
	 * @param child 节点
	 */
	@SuppressWarnings("unchecked")
	private boolean addChild(TreeNode<MenuDM> node, MenuTradeDM child) {
		if (child.getMenuId() != null && child.getMenuId().equals(node.getBean().getId())) {
			node.addChildNode(new TreeNode<MenuTradeDM>(child)); 
			return true;
		}
		for(TreeNode<?> _item_ : node.getChildList())
		{
			if (_item_.getBean() instanceof MenuDM) {
				TreeNode<MenuDM> item = (TreeNode<MenuDM>)_item_;
				boolean bo = addChild(item, child);
				if (bo)
					return true;
			}
		}
		
		return false;
	}
	
	public List<TreeNode<MenuDM>> filterMenu(TradeDataDM tradeDataDM, String teller) {
		long time = System.currentTimeMillis();
		List<TreeNode<MenuDM>> menuDMs = getAllMenu();
		logger.debug("查询所有菜单耗时：{}ms", System.currentTimeMillis()-time);
		if (menuDMs == null || menuDMs.size() == 0)
			return menuDMs;
		
		List<TreeNode<MenuDM>> _menuDMs_ = new ArrayList<TreeNode<MenuDM>>();
		for (TreeNode<MenuDM> menuNode : menuDMs) {
			if (menuNode.isLeaf()) {
				// 当前菜单无子菜单或交易
				continue;
			}
			if (menuNode.getBean().getExpressionId() > 0) {
				Boolean isTure = expressionBizProvider.executeExpression(menuNode.getBean().getExpressionId(), tradeDataDM);
				if (isTure == null || !isTure) {
					// 当前菜单不可用
					continue;
				}
			} 
			List<TreeNode<?>> subNodes = filterSubNode(menuNode, tradeDataDM, teller);
			if (subNodes == null || subNodes.size() == 0) {
				// 当前菜单无有效子菜单或交易
				continue;
			}
			// 使用新TreeNode，不能影响原来TreeNode结构
			TreeNode<MenuDM> _menuNode_ = new TreeNode<MenuDM>(menuNode.getBean());
			_menuNode_.setChildList(subNodes);
			_menuDMs_ .add(_menuNode_);
		}
		logger.debug("筛选可用菜单耗时：{}ms", System.currentTimeMillis()-time);
		return _menuDMs_;
	}
	
	@SuppressWarnings("unchecked")
	private List<TreeNode<?>> filterSubNode(TreeNode<MenuDM> parentNode, TradeDataDM tradeDataDM, String teller) {
		if (parentNode.isLeaf()) {
			return null;
		}
		List<TreeNode<?>> nodes = new ArrayList<TreeNode<?>>();
		for (TreeNode<?> node : parentNode.getChildList()) {
			if (node.getBean() instanceof MenuDM) {
				MenuDM _menuDM_ = (MenuDM)node.getBean();
				if (_menuDM_.getExpressionId() > 0) {
					Boolean isTure = expressionBizProvider.executeExpression(_menuDM_.getExpressionId(), tradeDataDM);
					if (isTure == null || !isTure) {
						// 当前菜单不可用
						continue;
					}
				} 
				List<TreeNode<?>> subNodes = filterSubNode((TreeNode<MenuDM>)node, tradeDataDM, teller);
				if (subNodes == null || subNodes.size() == 0) {
					// 当前菜单无有效子菜单或交易
					continue;
				}
				// 使用新TreeNode，不能影响原来TreeNode结构
				TreeNode<MenuDM> _menuNode_ = new TreeNode<MenuDM>(_menuDM_);
				_menuNode_.setChildList(subNodes);
				nodes     .add(_menuNode_);
			}
			if (node.getBean() instanceof MenuTradeDM) {
				MenuTradeDM menuTradeDM = (MenuTradeDM)node.getBean();
				if (menuTradeDM.getExpressionId() > 0) {
					Boolean isTure = expressionBizProvider.executeExpression(menuTradeDM.getExpressionId(), tradeDataDM);
					if (isTure == null || !isTure) {
						// 当前菜单与交易关联不可用
						continue;
					}
				}
				TradeCodeDM tradeCodeDM = tradeBiz.findTradeCode(menuTradeDM.getTradeId());
				if (tradeCodeDM == null) {
					// 无此交易
					continue;
				}
				if (tradeCodeDM.getExpressionid() > 0) {
					Boolean isTure = expressionBizProvider.executeExpression(tradeCodeDM.getExpressionid(), tradeDataDM);
					if (isTure == null || !isTure) {
						// 当前交易不可用
						continue;
					}
				}
				/* 太耗时 
				if (!tradeBiz.checkTradePermission(tradeCodeDM.getExpressionid(), tradeCodeDM.getCode(), null, teller)) {
					// 当前交易，柜员无权限
					continue;
				}*/
				TreeNode<TradeCodeDM> tradeNode_ = new TreeNode<TradeCodeDM>(tradeCodeDM);
				nodes.add(tradeNode_);
			}
		}
		return nodes;
	}
	
}
