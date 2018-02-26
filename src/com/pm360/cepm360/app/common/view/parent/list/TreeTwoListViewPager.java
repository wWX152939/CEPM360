package com.pm360.cepm360.app.common.view.parent.list;

import android.annotation.SuppressLint;
import android.content.Context;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.TreeTwoBean;

import java.io.Serializable;
import java.util.List;

public abstract class TreeTwoListViewPager<P extends Serializable,
					C extends Serializable, L extends Serializable> 
									extends TreeSimpleListViewPager<TreeTwoBean, L> {
	
	// 保存父子层的列表，作为转换源
	private List<P> mParentList;
	private List<C> mChildList;

	public TreeTwoListViewPager(Context context, 
								String title, 
								ListWithViewPager<L> listWithViewPager) {
		super(context, title, listWithViewPager);
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
		getTreeAdapter().addDataList((List<TreeTwoBean>) trees);
		
		// 默认选中第一个父节点ID为0的节点
		if (getTreeAdapter().getShowList().size() != 0) {
			if (getCurrentTreeNode() != null) {
				getListAdapter().setShowDataList(
						getContentsFromTree(getCurrentTreeNode()));
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
