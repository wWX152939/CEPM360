package com.pm360.cepm360.app.common.view.parent.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FieldRemap;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon.LocationInterface;
import com.pm360.cepm360.app.common.view.parent.list.SimpleDirList.SimpleDirInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleDirListOptionMenu<T extends Serializable,
										C extends Serializable> {
	
	// 树、列表根布局
	private View mRootView;
	
	// 简单的目录结构，但并不是树结构
	protected SimpleDirList<T> mSimpleDirList;
	
	// 带ViewPager的列表
	protected ListWithOptionMenu<C> mListWithOptionMenu;
	
	// 用于保存目录节点到右侧列表的缓存，如果没有从服务器提取保存
	protected Map<T, List<C>> mDirListMap = new HashMap<T, List<C>>();
	
	/**---------------------------------------------------*/
	
	/**
	 * 构造函数，初始化上下文和树列表标题
	 * @param context
	 * @param title
	 */
	public SimpleDirListOptionMenu(Context context, 
									String title) {
		this(context, title, null);
	}
	
	/**
	 * 高度定制右侧列表实现
	 * @param context
	 * @param title
	 * @param listWithOptionMenu
	 */
	public SimpleDirListOptionMenu(Context context, 
						String title,
						ListWithOptionMenu<C> listWithOptionMenu) {
		mSimpleDirList = new SimpleDirList<T>(context, title, mSimpleDirListImpl);
		
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
						
					// 正常模式时，使能多选模式
					mListWithOptionMenu.enableNormalMultSelect(true);
					super.longClickHandler(view, position);
				}
			};
		} else {
			mListWithOptionMenu = listWithOptionMenu;
		}
		
		//初始化视图
		mRootView = LayoutInflater.from(context)
						.inflate(R.layout.base_tree_list_layout, null);
		
		mSimpleDirList.setLocationInterface(new LocationInterface() {

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
		mSimpleDirList.init(listItemClass, listInterface, serviceInterface);
	}
	
	/**
	 * 不带浮动菜单和选项菜单的简单列表
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 */
	public void initList(Class<C> listItemClass,
			CommonListInterface<C> listInterface,
			ServiceInterface<C> serviceInterface) {
		mListWithOptionMenu.init(listItemClass, listInterface, serviceInterface);
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
			mListWithOptionMenu.enableInnerButton(false);
		}
		
		mListWithOptionMenu.init(listItemClass, listInterface, 
				serviceInterface, simpleDialogInterface, floatingMenuInterface);
		
		// 树和列表组合界面，默认不显示浮动菜单
		if (mListWithOptionMenu.mCurrentMode == OperationMode.NORMAL) {
			mListWithOptionMenu.getFloatingMenu().setVisibility(View.INVISIBLE);
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
			mListWithOptionMenu.enableInnerButton(false);
		}
		
		mListWithOptionMenu.init(listItemClass, listInterface, serviceInterface,
				simpleDialogInterface, optionMenuInterface, floatingMenuInterface);
		
		// 树和列表组合界面，默认不显示浮动菜单
		if (mListWithOptionMenu.mCurrentMode == OperationMode.NORMAL) {
			mListWithOptionMenu.getFloatingMenu().setVisibility(View.INVISIBLE);
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
		return mSimpleDirList.getCurrentItem();
	}
	
	/**
	 * 设置当前树节点
	 * @param t
	 */
	public void setCurrentTreeNode(T t) {
		mSimpleDirList.setCurrentItem(t);
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
	 * 返回服务请求处理接口实现
	 * @return
	 */
	public DataManagerInterface getTreeServiceManager() {
		return mSimpleDirList.getServiceManager();
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
	public DataListAdapter<T> getTreeAdapter() {
		return mSimpleDirList.getListAdapter();
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
	public SimpleDirList<T> getTree() {
		return mSimpleDirList;
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
		mSimpleDirList.setOperationMode(mode);
	}
	
	/**
	 * 将服务器提取的数据缓存到映射表
	 * @param list
	 */
	public Map<T, List<C>> getDirListMap(Map<T, List<C>> listMap) {
		return null;
	}
	
	/**
	 * 设置权限和类型标志
	 * @param viewPermission
	 * @param editPermission
	 * @param type
	 */
	public void setPermission(String viewPermission,String editPermission, int type) {
		mListWithOptionMenu.setPermission(viewPermission, editPermission, type);
		mSimpleDirList.setPermission(viewPermission, editPermission, type);
	}
	
	/**
	 * 简单目录结构视图的接口实现
	 */
	private SimpleDirInterface<T> mSimpleDirListImpl
									= new SimpleDirInterface<T>() {

		@Override
		public void handleClick(int position, View view) {
			mSimpleDirList.handleClick(position);
			if (null != getDirListMap(mDirListMap)) {
				mDirListMap = getDirListMap(mDirListMap);
			}
			// 如果缓存里面有，直接返回
			if (mDirListMap.containsKey(mSimpleDirList.getCurrentItem())) {
				List<C> list = mDirListMap.get(mSimpleDirList.getCurrentItem());
				mListWithOptionMenu.getListAdapter().setShowDataList(list);
			} else {
				mListWithOptionMenu.loadData();
			}
		}

		@Override
		public int getListItemId(T t) {
			return getDirNodeItemId(t);
		}
	};
	
	/**
	 * 获取树、列表关联Id
	 * @return
	 */
	abstract public int getTreeListRelevanceId(C c);
	
	/**
	 * 获取目录节点的ID
	 * @param t
	 * @return
	 */
	abstract public int getDirNodeItemId(T t);
}
