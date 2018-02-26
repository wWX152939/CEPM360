package com.pm360.cepm360.app.common.view.parent.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FieldRemap;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon.LocationInterface;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu.ListWithOptionMenuInterface;
import com.pm360.cepm360.app.common.view.parent.list.SimpleTreeForDir.SimpleTreeForDirInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Expandable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class TreeSimpleListOptionMenu<T extends Expandable & Serializable,
										C extends Serializable> {
	
	// 树、列表根布局
	private View mRootView;
	
	// 简单的树结构
	protected SimpleTreeForDir<T> mSimpleTreeForDir;
	
	// 带ViewPager的列表
	protected ListWithOptionMenu<C> mListWithOptionMenu;
	
	/**---------------------------------------------------*/
	private boolean mMixMode;
	
	/**
	 * 构造函数，初始化上下文和树列表标题
	 * @param context
	 * @param title
	 */
	public TreeSimpleListOptionMenu(Context context, 
									String title) {
		this(context, title, null);
	}
	
	/**
	 * 高度定制右侧列表实现
	 * @param context
	 * @param title
	 * @param listWithOptionMenu
	 */
	public TreeSimpleListOptionMenu(Context context, 
						String title,
						ListWithOptionMenu<C> listWithOptionMenu) {
		mSimpleTreeForDir = new SimpleTreeForDir<T>(context, title, mSimpleTreeForDirImpl);
		
		/*
		 *  由于后面列表情况复杂，不能固化实现方式，但是这里如果对于一般显示
		 *  和映射显示，使用默认实现已经足够
		 */
		if (listWithOptionMenu == null) {
			mListWithOptionMenu = new ListWithOptionMenu<C>(context) {
	
				@SuppressWarnings("unchecked")
				@Override
				public void handleDataOnResult(ResultStatus status, List<?> list) {
					super.handleDataOnResult(status, list);
					
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_QUERY:
							mListAdapter.setShowDataList((List<C>) list);
							break;
						default:
							break;
					}
				}

				@Override
				protected void longClickHandler(View view, int position) {
					if (mSimpleTreeForDir.getListAdapter().getItem(position).isHas_child()) {
						
						// 正常模式并且非子树节点时，使能多选模式
						mListWithOptionMenu.enableNormalMultSelect(false);
					} else {
						
						// 正常模式并且子树节点时，使能多选模式
						mListWithOptionMenu.enableNormalMultSelect(true);
					}
					
					super.longClickHandler(view, position);
				}
			};
		} else {
			mListWithOptionMenu = listWithOptionMenu;
		}
		
		// 这里对添加和删除右侧列表项对树节点计数更新
		mListWithOptionMenu.setListWithOptionMenuInterface(new ListWithOptionMenuInterface() {

			@Override
			public void updateTreeCount(int count) {
				mSimpleTreeForDir.getListAdapter().updateTreeNodesCount(count);
			}
		});
		
		//初始化视图
		mRootView = LayoutInflater.from(context)
						.inflate(R.layout.base_tree_list_layout, null);
		
		mSimpleTreeForDir.setLocationInterface(new LocationInterface() {

			@Override
			public View getRootView() {
				return mRootView;
			}

			@Override
			public int getLocationId() {
				return R.id.tree_layout;
			}
		});
		
		mListWithOptionMenu.setLocationInterface(new LocationInterface() {

			@Override
			public View getRootView() {
				return mRootView;
			}

			@Override
			public int getLocationId() {
				return R.id.list_layout;
			}
		});
	}

	/**
	 * 初始化树对象
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 */
	public void initTree(Class<T> listItemClass,
						SimpleListInterface<T> listInterface,
						SimpleServiceInterface<T> serviceInterface) {
		mSimpleTreeForDir.init(listItemClass, listInterface, serviceInterface);
	}
	
	/**
	 * 带选项菜单的初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param simpleDialogInterface
	 * @param optionMenuInterface
	 */
	public void initList(Class<C> listItemClass,
						CommonListInterface<C> listInterface,
						ServiceInterface<C> serviceInterface,
						SimpleDialogInterface simpleDialogInterface,
						OptionMenuInterface optionMenuInterface) {
		mListWithOptionMenu.init(listItemClass, listInterface, serviceInterface,
									simpleDialogInterface, optionMenuInterface);
	}
	
	/**
	 * 带浮动菜单的初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param simpleDialogInterface
	 * @param floatingMenuInterface
	 */
	public void initList(Class<C> listItemClass,
						CommonListInterface<C> listInterface,
						ServiceInterface<C> serviceInterface,
						SimpleDialogInterface simpleDialogInterface,
						FloatingMenuInterface floatingMenuInterface) {
		// 是否使能选择按钮
		if (mListWithOptionMenu.mCurrentMode != OperationMode.NORMAL) {
			mListWithOptionMenu.enableInnerButton(true);
		}
		
		mListWithOptionMenu.init(listItemClass, listInterface, 
				serviceInterface, simpleDialogInterface, floatingMenuInterface);
		
		// 树和列表组合界面，默认不显示浮动菜单
		if (mListWithOptionMenu.mCurrentMode == OperationMode.NORMAL) {
			FloatingMenuView floatingMenu = mListWithOptionMenu.getFloatingMenu();
			if (floatingMenu != null) {
				floatingMenu.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	/**
	 * 带选项菜单和浮动菜单的初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param simpleDialogInterface
	 * @param optionMenuInterface
	 * @param floatingMenuInterface
	 */
	public void initList(Class<C> listItemClass,
						CommonListInterface<C> listInterface,
						ServiceInterface<C> serviceInterface,
						SimpleDialogInterface simpleDialogInterface,
						OptionMenuInterface optionMenuInterface,
						FloatingMenuInterface floatingMenuInterface) {
		
		// 选择模式使能选择按钮
		if (mListWithOptionMenu.mCurrentMode != OperationMode.NORMAL) {
			mListWithOptionMenu.enableInnerButton(true);
		}
		
		mListWithOptionMenu.init(listItemClass, listInterface, serviceInterface,
				simpleDialogInterface, optionMenuInterface, floatingMenuInterface);
		
		// 树和列表组合界面，默认不显示浮动菜单
		if (mListWithOptionMenu.mCurrentMode == OperationMode.NORMAL) {
			FloatingMenuView floatingMenu = mListWithOptionMenu.getFloatingMenu();
			if (floatingMenu != null) {
				floatingMenu.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	/**
	 * 返回根视图
	 * @return
	 */
	public View getRootView() {
		return mRootView;
	}
	
	/**
	 * 返回当前树节点
	 * @return
	 */
	public T getCurrentTreeNode() {
		return mSimpleTreeForDir.getCurrentItem();
	}
	
	/**
	 * 设置当前树节点
	 * @param t
	 */
	public void setCurrentTreeNode(T t) {
		mSimpleTreeForDir.setCurrentItem(t);
	}
	
	/**
	 * 返回当前内容节点
	 * @return
	 */
	public C getCurrentListItem() {
		return mListWithOptionMenu.getCurrentItem();
	}
	
	/**
	 * 设置列表对象的显示列表
	 * @param list
	 */
	public void setListShowList(List<C> list) {
		mListWithOptionMenu.setShowList(list);
	}
	
	/**
	 * 获取显示列表
	 * @return
	 */
	public List<C> getListShowList() {
		return mListWithOptionMenu.getShowList();
	}
	
	/**
	 * 启动混合模式
	 */
	public void enableMixMode() {
		mMixMode = true;
	}
	
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	public DataManagerInterface getTreeServiceManager() {
		return mSimpleTreeForDir.getServiceManager();
	}
	
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	public DataManagerInterface getListServiceManager() {
		return mListWithOptionMenu.getServiceManager();
	}
	
	/**
	 * 获取树适配器
	 * @return
	 */
	public DataTreeListAdapter<T> getTreeAdapter() {
		return mSimpleTreeForDir.getListAdapter();
	}
	
	/**
	 * 获取列表适配器
	 * @return
	 */
	public DataListAdapter<C> getListAdapter() {
		return mListWithOptionMenu.getListAdapter();
	}
	
	/**
	 * 获取树封装对象
	 * @return
	 */
	public SimpleTreeForDir<T> getTree() {
		return mSimpleTreeForDir;
	}
	
	/**
	 * 获取列表封装对象
	 * @return
	 */
	public ListWithOptionMenu<C> getList() {
		return mListWithOptionMenu;
	}
	
	/**
	 * 设置列表域重映射接口
	 * @param fieldRemap
	 */
	public void setFieldRemap(FieldRemap<C> fieldRemap) {
		mListWithOptionMenu.setFieldRemap(fieldRemap);
	}
	
	/**
	 * 设置过滤数据
	 * @param filterList
	 */
	public void setFilterDataList(List<C> filterList) {
		mListWithOptionMenu.setFilterData(filterList);
	}
	
	/**
	 * 设置操作模式
	 * @param mode
	 */
	public void setOperationMode(OperationMode mode) {
		mListWithOptionMenu.setOperationMode(mode);
		mSimpleTreeForDir.setOperationMode(mode);
	}
	
	/**
	 * 设置权限和类型标志
	 * @param viewPermission
	 * @param editPermission
	 * @param type
	 */
	public void setPermission(String viewPermission,String editPermission, int type) {
		mListWithOptionMenu.setPermission(viewPermission, editPermission, type);
		mSimpleTreeForDir.setPermission(viewPermission, editPermission, type);
	}
	
	/**
	 * 获取叶子节点的内容列表
	 * @param treeNode
	 * @return
	 */
	protected List<C> getContentFromTreeNode(T treeNode) {	
		// 已经展开，直接获取显示列表
		if (treeNode.isExpanded()) {	
			return mListWithOptionMenu.getListAdapter().getDataShowList();
		}
		
		List<C> itemList = new ArrayList<C>();
		// 该类型有资源，但未展开，遍历内容列表
		if (treeNode.getCount() > 0) {	
			List<C> dataList = mListWithOptionMenu.getListAdapter().getDataList();
			for (C item : dataList) {
				if (getTreeListRelevanceId(item) == treeNode.getId()) {
					// 直接属于该类型
					itemList.add(item);
				}
			}
		}
        
		return itemList;
	}
	
	/**
	 * 递归获取树节点下的内容
	 * @param treeNode
	 * @return
	 */
	public List<C> getContentsFromTree(T treeNode) {
		// 如果是底层节点直接返回数据
		if (!treeNode.isHas_child()) {
			return getContentFromTreeNode(treeNode);
		}
		
		// 分配内存
		List<C> itemList = new ArrayList<C>();
		
		// 为查找内容添加叶子节点
		List<T> noChildList = new ArrayList<T>();
		List<List<T>> levelList = mSimpleTreeForDir.getListAdapter().getLevelList();
		List<T> levelData = levelList.get(treeNode.getLevel() + 1);
		for (T t : levelData) {
			if (t.getParents_id() == treeNode.getId()) {
				if (!t.isHas_child()) {
					noChildList.add(t);
				} else {
					noChildList.addAll(recursionGetTreeNodes(t, levelList));
				}
			}
		}
		
		// 查找目录下的内容
		for (T t : noChildList) {
			itemList.addAll(getContentFromTreeNode(t));
		}
        
		return itemList;
	}
	
	/**
	 * 获取混合内容列表
	 * @param treeNode
	 * @return
	 */
	public List<C> getContentsList(T treeNode) {
		
		// 分配内存
		List<C> itemList = new ArrayList<C>();
		itemList.addAll(getContents(treeNode));
		
		List<T> treeDataList = mSimpleTreeForDir.getListAdapter().getDataList();
		for (T t : treeDataList) {
			if (t.getParents_id() == treeNode.getId()) {
				itemList.addAll(getContentsList(t));
			}
		}
		
		return itemList;
	}
	
	/**
	 * 获取节点下的内容列表
	 * @param treeNode
	 * @return
	 */
	private List<C> getContents(T treeNode) {
		
		// 分配内存
		List<C> itemList = new ArrayList<C>();
		List<C> dataList = mListWithOptionMenu.getListAdapter().getDataList();
		for (C c : dataList) {
			if (getTreeListRelevanceId(c) == treeNode.getId()) {
				itemList.add(c);
			}
		}
		
		return itemList;
	}
	
	/**
	 * 递归获取树节点
	 * @param treeNode
	 * @return
	 */
	private List<T> recursionGetTreeNodes(T treeNode, List<List<T>> levelList) {
		List<T> list = new ArrayList<T>();
		if (!treeNode.isHas_child()) {
			list.add(treeNode);
		} else {
			List<T> tList = levelList.get(treeNode.getLevel() + 1);
			for (T t : tList) {
				if (t.getParents_id() == treeNode.getId()) {
					list.addAll(recursionGetTreeNodes(t, levelList));
				}
			}
		}
		return list;
	}
	
	/**
	 * 树视图的接口实现
	 */
	private SimpleTreeForDirInterface mSimpleTreeForDirImpl
									= new SimpleTreeForDirInterface() {

		@Override
		public void handleClick(int position, View view) {
			
			// 通用操作
			mSimpleTreeForDir.handleClick(position, view);
			
			// 当没有子节点时，显示浮动菜单，否则隐藏
			setFloatingMenuVisable();
			
			/*
			 *  父节点下操作右侧列表的选项菜单可能
			 *  和子节点下的选项菜单不同
			 */
			switchClickShow();
		}

		@Override
		public void calcListItemCount() {
			calcItemCount();
		}

		@Override
		public <E extends Expandable & Serializable> boolean isBoldText(E t) {
			return getIsBoldText(t);
		}
		
		
	};
	
	/**
	 * 设置浮动菜单的显示和隐藏
	 */
	protected void setFloatingMenuVisable() {
		
		// 如果有子节点，不显示列表的浮动菜单
		if (mListWithOptionMenu.getFloatingMenu() != null) {
			if (mSimpleTreeForDir.getCurrentItem().isHas_child()) {
				mListWithOptionMenu.getFloatingMenu().setVisibility(View.INVISIBLE);
			} else {
				mListWithOptionMenu.getFloatingMenu().setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * 父节点下操作右侧列表的选项菜单可能
	 * 和子节点下的选项菜单不同，如果不同
	 * 可在此函数中切换选项菜单
	 */
	protected void switchClickShow() {
		if (mSimpleTreeForDir.getCurrentItem().isHas_child()) {
			if (mMixMode) {
				mListWithOptionMenu.getListAdapter().setShowDataList(
						getContentsList(mSimpleTreeForDir.getCurrentItem()));
			} else {
				mListWithOptionMenu.getListAdapter().setShowDataList(
						getContentsFromTree(mSimpleTreeForDir.getCurrentItem()));
			}
		} else {
			mListWithOptionMenu.getListAdapter().setShowDataList(
					getContentFromTreeNode(mSimpleTreeForDir.getCurrentItem()));
		}
	}
	
	/**
	 * 计算树节点下的列表项数目
	 */
	protected void calcItemCount() {
		List<T> treeDataList = mSimpleTreeForDir.getListAdapter().getDataList();
		List<C> contentDataList = mListWithOptionMenu.getListAdapter().getDataList();
		
        if (contentDataList != null && contentDataList.size() > 0) {
            // 遍历文件
            for (int i = 0; i < contentDataList.size(); i++) {
                // 遍历目录
                for (int j = 0; j < treeDataList.size(); j++) {
                	
                    // 如果文件的目录就是该目录
                    if (getTreeListRelevanceId(contentDataList.get(i))
                    					== treeDataList.get(j).getId()) {
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
	 * 列表是否需要加粗显示
	 * @param t
	 * @return
	 */
	protected <E extends Expandable & Serializable> boolean getIsBoldText(E t) {
		return false;
	}
	
	/**
	 * 获取树、列表关联Id
	 * @return
	 */
	abstract public int getTreeListRelevanceId(C c);
}
