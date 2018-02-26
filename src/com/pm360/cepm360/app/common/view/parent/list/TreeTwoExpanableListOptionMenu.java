package com.pm360.cepm360.app.common.view.parent.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.TreeTwoBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class TreeTwoExpanableListOptionMenu<P extends Serializable,
					C extends Serializable, L extends Serializable> 
									extends TreeSimpleExpanableListOptionMenu<TreeTwoBean, L> {
	
	// 保存父子层的列表，作为转换源
	private List<P> mParentList;
	private List<C> mChildList;
	
	/**
	 * 自己实现右侧列表实现方式
	 * @param context
	 * @param title
	 * @param listWithOptionMenu
	 */
	public TreeTwoExpanableListOptionMenu(Context context, 
								String title,
								ExpanableListWithOptionMenu<L> listWithOptionMenu) {
		super(context, title, listWithOptionMenu);
	}
	
	/**
	 * 获取父层数据管理
	 * @return
	 */
	public DataManagerInterface getParentManager() {
		return mParentManager;
	}
	
	/**
	 * 获取子层数据管理
	 * @return
	 */
	public DataManagerInterface getChildManager() {
		return mChildManager;
	}
	
	/*
	 * Project数据请求回调接口实现
	 */
	protected DataManagerInterface mChildManager = new DataManagerInterface() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_QUERY:
					if (mParentList != null) {
						handleData(mParentList, (List<C>) list);
					} else {
						mChildList = (List<C>) list;
					}
					break;
				default:
					break;
			}
		}
	};
	
	/*
	 * 父层数据请求回调接口实现，这里不会使用mSimpleTreeForDir中的mListManager
	 */
	protected DataManagerInterface mParentManager = new DataManagerInterface() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_QUERY:
					if (mChildList != null) {
						handleData((List<P>) list, mChildList);
					} else {
						mParentList = (List<P>) list;
					}
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 项目树数据加载完成，处理数据
	 * @param list
	 */
	private void handleData(List<P> parentList, List<C> childList) {
		
		// 结束加载进度对话框
		getTree().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
		
		// 格式化数据，将两个列表转换为一个列表
		List<TreeTwoBean> trees = genTreeTwoBeanList(parentList, childList);
		getTreeAdapter().setDataList((List<TreeTwoBean>) trees);
		
		// 定位列表对象所属目录对象
		TreeTwoBean specifiedItem = null;
		L l = getList().getSpecifiedItem();
		if (l != null) {
			for (int i = 0; i < trees.size(); i++) {
				TreeTwoBean treeTwoBean = trees.get(i);
				if (!treeTwoBean.isHas_child() 
						&& getTreeListRelevanceId(l) == treeTwoBean.getRealId()) {
					specifiedItem = treeTwoBean;
					getTree().setSpecifiedItem(specifiedItem);
					break;
				}
			}
		}
		
		// 选中一个树节点
		if (!getTreeAdapter().getShowList().isEmpty()) {
			
			// 默认选中第一个父节点ID为0的节点
			if (specifiedItem == null) {
				setCurrentTreeNode(getTreeAdapter().getItem(0));
				getTreeAdapter().setSelected(0, true);
			} else {
				setCurrentTreeNode(getTreeAdapter()
						.locateAllDirectoryById(specifiedItem.getId()));
			}
			
			if (getCurrentTreeNode() != null) {
				getListAdapter().setShowList(mListWithOptionMenu
						.formateData(getContentsFromTree(getCurrentTreeNode())));
				mListWithOptionMenu.locationSpecifiedItem();
			}
		}
	}
	
	@Override
	protected void calcItemCount() {
		List<TreeTwoBean> treeDataList = mSimpleTreeForDir.getListAdapter().getDataList();
		List<L> contentDataList = mListWithOptionMenu.getListAdapter().getDataList();
		
        if (contentDataList != null && contentDataList.size() > 0) {
            // 遍历文件
            for (int i = 0; i < contentDataList.size(); i++) {
                // 遍历目录
                for (int j = 0; j < treeDataList.size(); j++) {
                	
                    // 如果文件的目录就是该目录
                    if (!treeDataList.get(j).isParent()
                    		&& getTreeListRelevanceId(contentDataList.get(i))
                    					== treeDataList.get(j).getRealId()) {
                    	treeDataList.get(j).setCount(treeDataList.get(j).getCount() + 1);
                        
                    	// 更新父目录的计数
                        if (treeDataList.get(j).getParents_id() > 0) {
                            mSimpleTreeForDir.getListAdapter()
                            				.setParentTreeNodeCount(treeDataList, 
                            								treeDataList.get(j), 1);
                        }
                        
                        // 找到就跳出循环
                        break;
                    }
                }
            }
        }
	}
	
	/**
	 * 获取叶子节点的内容列表
	 * @param treeNode
	 * @return
	 */
	@Override
	protected List<L> getContentFromTreeNode(TreeTwoBean treeNode) {	
		
		// 已经展开，直接获取显示列表
		if (treeNode.isExpanded()) {	
			return mListWithOptionMenu.getListAdapter().getDataList();
		}
		
		List<L> itemList = new ArrayList<L>();
		
		// 该类型有资源，但未展开，遍历内容列表
		if (treeNode.getCount() > 0) {	
			List<L> dataList = mListWithOptionMenu.getListAdapter().getDataList();
			for (L item : dataList) {
				if (!treeNode.isParent() 
						&& getTreeListRelevanceId(item) == treeNode.getRealId()) {
					
					// 直接属于该类型
					itemList.add(item);
				}
			}
		}
        
		return itemList;
	}
	
	@Override
	protected void setFloatingMenuVisable() {
		
		// 如果有子节点或者是父节点，不显示列表的浮动菜单
		if (mListWithOptionMenu.getFloatingMenu() != null) {
			if (mSimpleTreeForDir.getCurrentItem().isHas_child() 
					|| mSimpleTreeForDir.getCurrentItem().isParent()) {
				mListWithOptionMenu.getFloatingMenu().setVisibility(View.INVISIBLE);
			} else {
				mListWithOptionMenu.getFloatingMenu().setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 生成TreeTwoBean列表，生成该对象是必须实现该接口，该接口主要是将父层和子层
	 * 的对象列表转换为一个对象列表
	 * @param parentList
	 * @param childlist
	 * @return
	 */
	@SuppressLint("UseSparseArrays") 
	public abstract List<TreeTwoBean> genTreeTwoBeanList(List<P> parentList, 
										List<C> childList);

}
