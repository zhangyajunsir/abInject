package cn.com.agree.ab.lib.dm;

import java.util.List;
import java.util.ArrayList;

public class TreeNode<T> extends BasicDM {
	private static final long serialVersionUID = -7734552233979304946L;
	
	private String nodeName;
	private T obj;
	private TreeNode<?>       parentNode;
	private List<TreeNode<?>> childList = new ArrayList<TreeNode<?>>();

    public TreeNode(T t) {
    	this(null, null, t);
    }
    
    public TreeNode(String name, T t) {
    	this(null, name, t);
    }

    public TreeNode(TreeNode<?> parentNode, String name, T	t) {
    	this.parentNode = parentNode;
    	this.nodeName   = name;
    	this.obj        = t;
    }

    public boolean isLeaf() {
        if (childList == null) {
            return true;
        } else {
            if (childList.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /* 插入一个child节点到当前节点中 */
    public void addChildNode(TreeNode<?> treeNode) {
        childList.add(treeNode);
    }

    public boolean isValidTree() {
        return true;
    }

    /* 返回当前节点的父辈节点链 */
    public List<TreeNode<?>> getElders() {
        List<TreeNode<?>> elderList = new ArrayList<TreeNode<?>>();
        TreeNode<?> parentNode = this.getParentNode();
        if (parentNode == null) {
            return elderList;
        } else {
            elderList.add(parentNode);
            elderList.addAll(parentNode.getElders());
            return elderList;
        }
    }

    /* 返回当前节点的孩子集合 */
    public List<TreeNode<?>> getChildList() {
        return childList;
    }

    /* 删除当前节点和它下面的晚辈 */
    public void deleteNode() {
        TreeNode<?> parentNode = this.getParentNode();
        if (parentNode != null) {
            parentNode.deleteChildNode(this);
        }
    }

    /* 删除当前节点的某个子节点 */
    public void deleteChildNode(TreeNode<?> childNode) {
        List<TreeNode<?>> childList = this.getChildList();
        for (int i = 0; i < childList.size(); i++) {
            if (childNode == childList.get(i)) {
                childList.remove(i);
                return;
            }
        }
    }

    public void setChildList(List<TreeNode<?>> childList) {
        this.childList = childList;
    }

    public TreeNode<?>   getParentNode() {
        return parentNode;
    }

    public void setParentNode(TreeNode<?> parentNode) {
        this.parentNode = parentNode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public T getBean() {
        return obj;
    }

}