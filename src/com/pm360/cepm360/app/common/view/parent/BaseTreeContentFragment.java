package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.TreeListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SelectInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.TreeContentCombInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Expandable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BaseTreeContentFragment<T extends Expandable, 
								 C extends Serializable> extends CommonFragment
								 implements SelectInterface {
	/** ------------------------- 常量定义 ---------------------*/
	// 列表序号
	protected static final String SERIAL_NUMBER = "serial";
	protected static final String RESULT_KEY = "result_key";
	
	// 初始位置
	protected static final int BASE_POSITION = 0;
	protected static final int SHOW_TOAST = BASE_POSITION;
	protected static final int SHOW_PROGRESS_DIALOG = BASE_POSITION + 1;
	protected static final int DISMISS_PROGRESS_DIALOG = BASE_POSITION + 2;
	protected static final int DELAYTIME_FOR_SHOW = 500;

	
	/** ------------------------- 视图变量 ---------------------*/
	// 布局和视图定义
	private View mRootLayout;
	
	private View mTreeRootLayout;
	private View mContentListHeader;
	
	protected LinearLayout mContentLayout;
	
	private ListView mTreeListView;
	private ListView mContentListView;
	
	private OptionsMenuView mTreeOptionsMenu;
	private OptionsMenuView mContentOptionsMenu;
	
	private FloatingMenuView mTreeFloatingMenu;
	private FloatingMenuView mContentFloatingMenu;
	
	// 适配器adapter定义
	protected DataTreeListAdapter<T> mTreeAdapter;
	protected DataListAdapter<C> mContentAdapter;
	
	// 对话框
	private BaseDialog mTreeDialog;
	private BaseDialog mContentDialog;
	private ProgressDialog mProgressDialog;
	
	protected TextView mTreeListTitle;
	private ImageView mTreeExpand;
	private CheckBox mHeaderCheckBox;
	
	private View mButtonBar;
	
	/**--------------------- 暂存数据 ---------------------**/
	// 应用对象，用于获取共享数据
	protected CepmApplication mApplication;
		
	// 当前数据
	protected T mCurrentTree;
	protected C mCurrentContent;
	
	// 更新数据
	private T mCurrentUpdateTree;
	private C mCurrentUpdateContent;
	
	/** ------------------------- 标识变量 ---------------------*/
	
	// 数据是否加载完成
	protected boolean mDataLoaded;
	
	// 添加还是修改
	private boolean mIsAddOperation;
	// 是否是树节点操作
	private boolean mIsTreeOperation;
	protected boolean mIsFloatMenuAdd;
	
	// 使能选项菜单
	private boolean mEnableTreeOptionMenu;
	private boolean mEnableContentOptionMenu;
	
	// 使能浮动菜单
	private boolean mEnableTreeFloatingMenu;
	private boolean mEnableContentFloatingMenu;
	
	protected boolean mEnableInnerButton = true;
	
	/** ------------------------- 模式相关 ---------------------*/
	// 模式定义，普通模式和批量操作模式
	private ActionMode mActionMode;
	private boolean mMixMode;
	
	// 操作模式
	protected OperationMode mCurrentMode;
	
	public enum OperationMode {
		NORMAL,
		SINGLE_SELECT,
		MULTI_SELECT
	}
	
	
	
	/** ------------------------- 资源保存变量 ---------------------*/
	// 对话框显示Lables
	private String[] mTreeDialogNames;
	private String[] mContentDialogNames;
	
	// 保存列表显示项的IDS
	private int[] mTreeItemIds;
	private int[] mContentItemIds;
	private int[] mContentHeaderItemIds;
	
	// 通过xml文件提供内容表头字符串数组
	private String[] mContentItemNames;
	// 需要实现类提供内容显示的域
	protected String[] mTreeDisplayFields;
	protected String[] mContentDisplayFields;
	
	// 对象中可更新的字段定义
	protected String[] mTreeUpdateFields;
	protected String[] mContentUpdateFields;
	
	// 选项菜单
	protected String[] mTreeOptionSubMenuNames;
	protected String[] mContentOptionSubMenuNames;
	
	// 浮动菜单
	private int[] mTreeFloatingMenuImages;
	private String[] mTreeFloatingMenuTips;
	
	private int[] mContentFloatingMenuImages;
	private String[] mContentFloatingMenuTips;
	
	
	/** ------------------------- 映射变量 ---------------------*/
	// 更新字段映射表
	private Map<String, String> mTreeUpdateFieldMap;
	private Map<String, String> mContentUpdateFieldMap;
	
	// 界面数据保存
	protected Map<String, String> mSaveData;
	
	// 字段转换映射表
	private Map<String, Map<String, String>> mContentDisplaySwitchMap;
	private Map<String, Map<String, String>> mContentUpdateSwitchMap;
	
	/** ------------------------- 其他变量 ---------------------*/
	// 删除操作数据
	protected List<C> mRemoveList = new ArrayList<C>();
	protected int mRemoveCount;
	protected int mRemoveFailedCount;
	
	// 多选模式选中的项
	private List<C> mSelectedDataList = new ArrayList<C>();
	// 要过滤的数据列表
	protected List<C> mFilterDataList;
	// 默认显示不能点击的数据列表
	protected List<C> mDefaultDataList;
	
	/** ------------------------- 接口实现变量 ---------------------*/
	// 列表接口实现
	protected SimpleListInterface<T> mTreeListImplement;
	protected CommonListInterface<C> mContentListImplement;
	
	// 服务接口实现
	protected ServiceInterface<T> mTreeServiceImplement;
	protected ServiceInterface<C> mContentServiceImplement;
	
	// 选项菜单接口
	protected OptionMenuInterface mTreeOptionMenuImplement;
	protected OptionMenuInterface mContentOptionMenuImplement;
	
	// 浮动菜单接口
	protected FloatingMenuInterface mTreeFloatingMenuImplement;
	protected FloatingMenuInterface mContentFloatingMenuImplement;
	
	// 对话框接口
	protected SimpleDialogInterface mTreeDialogImplement;
	protected SimpleDialogInterface mContentDialogImplement;
	
	// 内容和树关联ID
	protected TreeContentCombInterface<C> mTreeContentCombImplement;
	
	protected FieldRemapInterface<C> mFieldRemapInterface;
	
	// 保存树和内容类型对象
	protected Class<T> mTreeClass;
	protected Class<C> mContentClass;
	
	/** 方法定义 */
	/**
	 * 初始化树列表数据
	 * @param treeClass
	 * @param commonListInterface
	 * @param serviceInterface
	 */
	protected void initTreeList(Class<T> clazz,
								SimpleListInterface<T> simpleListInterface,
								ServiceInterface<T> serviceInterface) {
		mTreeClass = clazz;
		mTreeListImplement = simpleListInterface;
		mTreeServiceImplement = serviceInterface;
	}
	
	/** 方法定义 */
	/**
	 * 初始化树列表数据
	 * @param treeClass
	 * @param commonListInterface
	 * @param serviceInterface
	 * @param floatingMenuInterface
	 * @param dialogAdapterInterface
	 */
	protected void initTreeList(Class<T> clazz,
								SimpleListInterface<T> simpleListInterface,
								ServiceInterface<T> serviceInterface,
								FloatingMenuInterface floatingMenuInterface,
								SimpleDialogInterface simpleDialogInterface) {
		initTreeList(clazz, simpleListInterface, serviceInterface);
		mTreeFloatingMenuImplement = floatingMenuInterface;
		mTreeDialogImplement = simpleDialogInterface;
		// 启用浮动菜单
		mEnableTreeFloatingMenu = true;
	}
	
	/** 方法定义 */
	/**
	 * 初始化树列表数据
	 * @param treeClass
	 * @param commonListInterface
	 * @param serviceInterface
	 * @param optionMenuInterface
	 * @param dialogAdapterInterface
	 */
	protected void initTreeList(Class<T> clazz,
								SimpleListInterface<T> simpleListInterface,
								ServiceInterface<T> serviceInterface,
								OptionMenuInterface optionMenuInterface,
								SimpleDialogInterface simpleDialogInterface) {
		initTreeList(clazz, simpleListInterface, serviceInterface);
		mTreeOptionMenuImplement = optionMenuInterface;
		mTreeDialogImplement = simpleDialogInterface;
		// 启用选项菜单
		mEnableTreeOptionMenu = true;
	}
	
	/** 方法定义 */
	/**
	 * 初始化树列表数据
	 * @param treeClass
	 * @param commonListInterface
	 * @param serviceInterface
	 * @param optionMenuInterface
	 * @param floatingMenuInterface
	 * @param dialogAdapterInterface
	 */
	protected void initTreeList(Class<T> clazz,
								SimpleListInterface<T> simpleListInterface,
								ServiceInterface<T> serviceInterface,
								OptionMenuInterface optionMenuInterface,
								FloatingMenuInterface floatingMenuInterface,
								SimpleDialogInterface simpleDialogInterface) {
		initTreeList(clazz, simpleListInterface, serviceInterface);
		mTreeOptionMenuImplement = optionMenuInterface;
		mTreeFloatingMenuImplement = floatingMenuInterface;
		mTreeDialogImplement = simpleDialogInterface;
		// 启用选项菜单和浮动菜单
		mEnableTreeOptionMenu = true;
		mEnableTreeFloatingMenu = true;
	}
	
	/**
	 * 初始化内容·列表数据
	 * @param clazz
	 * @param commonListInterface
	 * @param treeContentCombInterface
	 * @param serviceInterface
	 */
	protected void initContentList(Class<C> clazz,
								CommonListInterface<C> commonListInterface,
								TreeContentCombInterface<C> treeContentCombInterface,
								ServiceInterface<C> serviceInterface) {
		mContentClass = clazz;
		mContentListImplement = commonListInterface;
		mTreeContentCombImplement = treeContentCombInterface;
		mContentServiceImplement = serviceInterface;
	}
	
	/**
	 * 初始化内容·列表数据
	 * @param clazz
	 * @param commonListInterface
	 * @param treeContentCombInterface
	 * @param serviceInterface
	 * @param floatingMenuInterface
	 * @param dialogAdapterInterface
	 */
	protected void initContentList(Class<C> clazz,
								CommonListInterface<C> commonListInterface,
								TreeContentCombInterface<C> treeContentCombInterface,
								ServiceInterface<C> serviceInterface,
								FloatingMenuInterface floatingMenuInterface,
								SimpleDialogInterface dialogAdapterInterface) {
		initContentList(clazz, 
						commonListInterface, 
						treeContentCombInterface, 
						serviceInterface);
		mContentFloatingMenuImplement = floatingMenuInterface;
		mContentDialogImplement = dialogAdapterInterface;
		// 启用浮动菜单
		mEnableContentFloatingMenu = true;
	}
	
	/**
	 * 初始化内容·列表数据
	 * @param clazz
	 * @param commonListInterface
	 * @param treeContentCombInterface
	 * @param serviceInterface
	 * @param optionMenuInterface
	 * @param dialogAdapterInterface
	 */
	protected void initContentList(Class<C> clazz,
								CommonListInterface<C> commonListInterface,
								TreeContentCombInterface<C> treeContentCombInterface,
								ServiceInterface<C> serviceInterface,
								OptionMenuInterface optionMenuInterface,
								SimpleDialogInterface dialogAdapterInterface) {
		initContentList(clazz, 
						commonListInterface, 
						treeContentCombInterface, 
						serviceInterface);
		mContentOptionMenuImplement = optionMenuInterface;
		mContentDialogImplement = dialogAdapterInterface;
		// 启用选项菜单
		mEnableContentOptionMenu = true;
	}
	
	/**
	 * 初始化内容·列表数据
	 * @param clazz
	 * @param commonListInterface
	 * @param treeContentCombInterface
	 * @param serviceInterface
	 * @param optionMenuInterface
	 * @param floatingMenuInterface
	 * @param dialogAdapterInterface
	 */
	protected void initContentList(Class<C> clazz,
								CommonListInterface<C> commonListInterface,
								TreeContentCombInterface<C> treeContentCombInterface,
								ServiceInterface<C> serviceInterface,
								OptionMenuInterface optionMenuInterface,
								FloatingMenuInterface floatingMenuInterface,
								SimpleDialogInterface dialogAdapterInterface) {
		initContentList(clazz, 
						commonListInterface, 
						treeContentCombInterface, 
						serviceInterface);
		mContentOptionMenuImplement = optionMenuInterface;
		mContentFloatingMenuImplement = floatingMenuInterface;
		mContentDialogImplement = dialogAdapterInterface;
		// 启用选项菜单和浮动菜单
		mEnableContentOptionMenu = true;
		mEnableContentFloatingMenu = true;
	}
	
	/**
	 * 返回当前树节点
	 * @return
	 */
	public T getCurrentTreeNode() {
		return mCurrentTree;
	}
	
	/**
	 * 返回当前内容节点
	 * @return
	 */
	public C getCurrentContentItem() {
		return mCurrentContent;
	}
	
	/**
	 * 设置显示列表
	 * @param list
	 */
	public void setDataShowList(List<C> list) {
		mContentAdapter.setShowDataList(list);
	}
	
	/**
	 * 获取显示列表
	 * @return
	 */
	public List<C> getDataShowList() {
		return mContentAdapter.getDataShowList();
	}
	
	/**
	 * 启动混合模式
	 */
	public void enableMixMode() {
		mMixMode = true;
	}
	
	/**
	 * 设置域重映射接口
	 * @param fieldRemapInterface
	 */
	protected void setFieldRemapInterface(FieldRemapInterface<C> fieldRemapInterface) {
		mFieldRemapInterface = fieldRemapInterface;
	}
	
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	protected DataManagerInterface getTreeServiceHandler() {
		return mTreeManager;
	}
	
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	protected DataManagerInterface getContentServiceHandler() {
		return mContentManager;
	}
	
	/**
	 * 建立ID和Name映射
	 * @param globalStrings
	 * @return
	 */
	protected Map<String, String> globalIdNameMap(String[][] globalStrings) {
		Map<String, String> globalMap = new HashMap<String, String>();
		for (String[] itemStrings : globalStrings) {
			globalMap.put(itemStrings[0], itemStrings[1]);
			globalMap.put(itemStrings[1], itemStrings[0]);
		}
		return globalMap;
	}
	
	/**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SHOW_TOAST:
	                Toast.makeText( getActivity(), 
	                				(CharSequence) msg.obj,
	                				Toast.LENGTH_SHORT).show();
					break;	
					
				case SHOW_PROGRESS_DIALOG:
					if (!mDataLoaded && !mProgressDialog.isShowing()) {
						mProgressDialog.show();
						mProgressDialog.setContentView(R.layout.layout_progress);
					}
					break;
					
				case DISMISS_PROGRESS_DIALOG:
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
						mDataLoaded = false;
					}
					break;
					
				default:
					break;
			}
		}
	};
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 * @param object
	 */
	private void sendMessage(int what, Object object) {
		if (object != null) {
			// 创建初始化Message
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = object;
			
			// 发送消息到mHandler
			mHandler.sendMessage(msg);
		} else {
			mHandler.sendEmptyMessage(what);
		}
	}
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 */
	protected void sendMessage(int what) {
		sendMessage(what, null);
	}
	
	/**
	 * 材料类型操作管理
	 */
	protected DataManagerInterface mTreeManager = new DataManagerInterface() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_QUERY:
					mTreeAdapter.addDataList((List<T>) list);
					if (list.size() == 0) {
						mTreeServiceImplement.addItem(null);
					} else { 	
						// 如果没有父节点为0的节点，默认创建一个顶层节点
						if (mTreeAdapter.getShowList().size() == 0) {
							// 创建一个顶层节点
							mTreeServiceImplement.addItem(null);
						}
					}
					
					// 默认选中第一个父节点ID为0的节点
					if (mTreeAdapter.getShowList().size() != 0) {
						if (mCurrentTree == null) {
							handleClick(BASE_POSITION, null);
						} else {
							if (mMixMode) {
								mContentAdapter.setShowDataList(getContentsList(mCurrentTree));
							} else {
								mContentAdapter.setShowDataList(getContentsFromTree(mCurrentTree));
							}
						}
					}
					
					// 结束加载进度对话框
					mDataLoaded = true;
					sendMessage(DISMISS_PROGRESS_DIALOG);
					break;
					
				case AnalysisManager.SUCCESS_DB_ADD:
					mTreeAdapter.addTreeNode((T) list.get(0));
					
					// 默认选中第一个父节点ID为0的节点
					if (mCurrentTree == null 
							&& mTreeAdapter.getShowList().size() != 0) {
						handleClick(BASE_POSITION, null);
					}
					break;
					
				case AnalysisManager.SUCCESS_DB_DEL:
					mTreeAdapter.deleteTreeNode();
					break;
					
				case AnalysisManager.SUCCESS_DB_UPDATE:
					MiscUtils.clone(mCurrentTree, mCurrentUpdateTree);
					mTreeAdapter.notifyDataSetChanged();
					break;
					
				default:
					break;
			}
			
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
		}
	};
	
	/**
	 * 材料操作管理
	 */
	protected DataManagerInterface mContentManager = new DataManagerInterface() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			// 操作处理
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_QUERY:
					if (OperationMode.MULTI_SELECT == mCurrentMode 
							&& mFilterDataList != null 
							&& mFilterDataList.size() > 0) {
						filterSelectedDatas((List<C>) list);
					}
					
					mContentAdapter.addDataList((List<C>) list);
					
					if (mHeaderCheckBox != null && mHeaderCheckBox.isChecked()) {
						mHeaderCheckBox.setChecked(false);
					}
					break;
					
				case AnalysisManager.SUCCESS_DB_ADD:
					mContentAdapter.addDataToList((C) list.get(0));
					mTreeAdapter.updateTreeNodesCount(1);
					break;
					
				case AnalysisManager.SUCCESS_DB_DEL:
					mRemoveCount++;
					if (mRemoveCount == mRemoveList.size()) {
						if (mRemoveFailedCount == 0) {	// 删除都成功
							allDeleteSuccessful();
						} else {	// 删除部分失败
							partDeleteSucessful(status);
						}
						deleteDataClear();
					}
					break;
					
				case AnalysisManager.EXCEPTION_DB_DELETE:
					mRemoveCount++;
					mRemoveFailedCount++;
					if (mRemoveCount == mRemoveList.size()) {
						if (mRemoveCount == mRemoveFailedCount) {	// 全部失败
							status.setMessage(getResources()
										.getString(R.string.delete_all_failed));
						} else {	// 部分失败
							partDeleteSucessful(status);
						}
						deleteDataClear();
					}
					break;
					
				case AnalysisManager.SUCCESS_DB_UPDATE:
					MiscUtils.clone(mCurrentContent, mCurrentUpdateContent);
					mContentAdapter.notifyDataSetChanged();
					break;
					
				default:
					break;
			}
			
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
					&& mRemoveCount == 0) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
		}
	};
	
	/**
	 * 全部删除成功
	 */
	private void allDeleteSuccessful() {
		mContentAdapter.clearSelection();
		mContentAdapter.deleteData(mRemoveList);
		// 更新树节点计数
		mTreeAdapter.updateTreeNodesCount(-mRemoveCount);
		// 将当前项置为空
		mCurrentContent = null;
		
		// 如果批量删除是由删除类型节点引起时，全部删除成功后删除树节点
		if (mIsTreeOperation) {
			// 删除当前树节点
			mTreeServiceImplement.deleteItem(mCurrentTree);
			// 不需要清除标志，因为每次进入删除操作都会正确设置改标志，退出时不必清除
			//mIsTreeOperation = false;
		}
	}
	
	/**
	 * 部分删除成功
	 * @param status
	 */
	private void partDeleteSucessful(ResultStatus status) {
		status.setMessage(getResources()
				.getString(R.string.delete_part_failed));
		mContentAdapter.clearAll();
		mContentServiceImplement.getListData();
	}
	
	/**
	 * 清理删除操作使用的数据
	 */
	private void deleteDataClear() {
		mRemoveList.clear();
		mRemoveCount = 0;
		mRemoveFailedCount = 0;
	}
	
	/**
	 * 多选模式中，过滤掉已选择的内容
	 * @param list
	 */
	private void filterSelectedDatas(List<C> list) {
		Iterator<C> iterator = list.iterator();
		while (iterator.hasNext()) {
			C c = iterator.next();
			for (C filter : mFilterDataList) {
				if (mContentListImplement.getListItemId(filter) 
								== mContentListImplement.getListItemId(c)) {
					iterator.remove();
					break;
				}
			}
		}
	}

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// 初始化权限
		initPermission();
				
		// 权限判断是否初始化视图
		if (mHasViewPermission || mHasEditPermission) {
			// 初始化部分成员数据
			initBasicLocalData();
			
			// 初始化布局管理器
			initBasicLayout(inflater, container);
			
			// 初始化树视图
			initTreeView();
			
			// 初始化内容视图
			initContentView();
			
			// 初始化进度对话框
			createProgressDialog();
			
			// 加载数据
			loadData();
		} else {
			// 没有任何权限，显示无权限信息
			showNoViewPermission(inflater, container);
		}
		
		return mRootLayout;
	}
	
	/**
	 * 显示无查看权限信息
	 * @param inflater
	 * @param container
	 */
	private void showNoViewPermission(  LayoutInflater inflater, 
										ViewGroup container) {
		// 没有任何权限，显示无权限信息
		mRootLayout = inflater.inflate( R.layout.no_permissions_content_layout,
										container,
										false);
	}
	
	/**
	 * 初始化部分成员数据
	 */
	private void initBasicLocalData() {	
		// 设置选择模式
		argumentHandler();
		
		// 初始化显示，更新域并建立域映射表
		initFeildsAndMap();
	}
	
	/**
	 * 设置选择模式
	 */
	@SuppressWarnings("unchecked")
	private void argumentHandler() {
		// 默认是正常模式
		mCurrentMode = OperationMode.NORMAL;
				
		// 设置选择模式
		Bundle bundle = getArguments();
		if (bundle != null) {
			
			// 获取选择模式
			if (bundle.containsKey(ListSelectActivity.SELECT_MODE_KEY)) {
				if (bundle.getBoolean(ListSelectActivity.SELECT_MODE_KEY)) {
					mCurrentMode = OperationMode.MULTI_SELECT;
					
					// 获取过滤数据
					if (bundle.containsKey(ListSelectActivity.FILTER_DATA_KEY)) {
						mFilterDataList = (List<C>) 
								bundle.getSerializable(ListSelectActivity.FILTER_DATA_KEY);
					}
					if (bundle.containsKey(ListSelectActivity.DEFAULT_DATA_KEY)) {
						mDefaultDataList = (List<C>) 
								bundle.getSerializable(ListSelectActivity.DEFAULT_DATA_KEY);
					}
				} else {
					mCurrentMode = OperationMode.SINGLE_SELECT;
				}
			}
		}
	}
	
	/**
	 * 初始化显示，更新域并建立域映射表
	 */
	private void initFeildsAndMap() {
		// 初始化树更新域
		if (mTreeDialogImplement != null) {
			mTreeUpdateFields = mTreeDialogImplement.getUpdateFeilds();
		}
		mTreeDisplayFields = mTreeListImplement.getDisplayFeilds();
		
		// 初始化内容更新域
		mContentDisplayFields = mContentListImplement.getDisplayFeilds();
		if (mContentDialogImplement != null) {
			mContentUpdateFields = mContentDialogImplement.getUpdateFeilds();
			if (DialogAdapterInterface.class.isInstance(mContentDialogImplement)) {
				mContentUpdateSwitchMap = ((DialogAdapterInterface) mContentDialogImplement).getUpdateFieldsSwitchMap();
			}
		}
		
		// 字段转换映射表
		mContentDisplaySwitchMap = mContentListImplement.getDisplayFieldsSwitchMap();
				
		// 从资源库初始化视图中对话框显示的字段名字符串数组
		if (OperationMode.NORMAL == mCurrentMode) { // 正常模式
			if (mTreeDialogImplement != null
					&& mTreeDialogImplement.getDialogLableNames() != 0 ) {
				mTreeDialogNames = getActivity().getResources()
							.getStringArray(mTreeDialogImplement.getDialogLableNames());
				
				// 初始化更新字段映射表
				if (mTreeUpdateFields != null) {
					mTreeUpdateFieldMap = new HashMap<String, String>();
					for (int i = 0; i < mTreeDialogNames.length; i++) {
						mTreeUpdateFieldMap.put(mTreeUpdateFields[i], mTreeDialogNames[i]);
					}
				}
			}
			if (mContentDialogImplement != null 
					&& mContentDialogImplement.getDialogLableNames() != 0) {
				mContentDialogNames = getActivity().getResources()
						.getStringArray(mContentDialogImplement.getDialogLableNames());
				
				// 初始化更新字段映射表
				if (mContentUpdateFields != null) {
					mContentUpdateFieldMap = new HashMap<String, String>();
					for (int i = 0; i < mContentDialogNames.length; i++) {
						mContentUpdateFieldMap.put(mContentUpdateFields[i], mContentDialogNames[i]);
					}
				}
			}
		}
		
		TypedArray typedArray = getResources()
				.obtainTypedArray(mContentListImplement.getListItemIds());
		mContentItemIds = new int[typedArray.length()];
		for (int i = 0; i < mContentItemIds.length; i++) {
			mContentItemIds[i] = typedArray.getResourceId(i, 0);
		}
		typedArray.recycle();
	}
	
	/**
	 * 初始化进度对话框
	 */
	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(getActivity());
		
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
	}
	
	/**
	 * 初始化视图布局
	 * @param inflater
	 * @param container
	 */
	private void initBasicLayout(LayoutInflater inflater, ViewGroup container) {
		// 获取fragment的根布局
		// LayoutInflater.from(getActivity());
		mRootLayout = inflater.inflate(R.layout.tree_and_list_layout, container, false);
		
		// 获取树区域的根布局
		mTreeRootLayout = mRootLayout.findViewById(R.id.tree_layout);
		
		// 设置内容列表背景色
     	mContentLayout = (LinearLayout) mRootLayout
     							.findViewById(R.id.content_list_layout);
	}
	
	/**
	 * 初始化树视图
	 */
	private void initTreeView() {
		// 基本配置
		initTreeBaseData();
		
		// 获取树视图相关资源，并初始化树列表视图
		initTreeListView();
		
		if (OperationMode.NORMAL == mCurrentMode
				&& mHasEditPermission) {
			// 初始化弹出菜单
			if (mEnableTreeOptionMenu) {
				initTreeOptionsMenu();
			}
			
			if (mEnableTreeFloatingMenu) {
				initTreeFloatingMenu();
			}
			
			// 初始化弹出对话框
			if (mEnableTreeFloatingMenu
					|| mEnableTreeOptionMenu) {
				initTreeDialog();
			}
		}
	}
	
	/**
	 * 配置树视图基本信息
	 */
	private void initTreeBaseData() {
		// 获取树列表分类标题控件，标题的名称在实现类中设置
		mTreeListTitle = (TextView) mRootLayout.findViewById(R.id.wz_category);
		
		// 获取扩展树列表控件
		mTreeExpand = (ImageView) mRootLayout.findViewById(R.id.expand_icon);
		mTreeExpand.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				LayoutParams params = (LayoutParams) mTreeRootLayout.getLayoutParams();
				if (params.weight == BASE_POSITION + 3) { // 未展开
					params.weight = 2;
					mTreeExpand.setImageResource(R.drawable.arrow_double_left_white);
				} else { // 已展开
					params.weight = 3;
					mTreeExpand.setImageResource(R.drawable.arrow_double_right_white);
				}
				mTreeRootLayout.setLayoutParams(params);
			}
		});
	}
	
	/**
	 * 获取树视图相关资源，并初始化树列表视图
	 */
	private void initTreeListView() {
		// 获取树列表项的控件ID并初始化
		TypedArray typedArray = getResources()
						.obtainTypedArray(R.array.tree_item_ids);
		mTreeItemIds = new int[typedArray.length()];
		for (int i = 0; i < mTreeItemIds.length; i++) {
			mTreeItemIds[i] = typedArray.getResourceId(i, 0);
		}
		
		typedArray.recycle();
		
		// 获取树视图对象
		mTreeListView = (ListView) mRootLayout.findViewById(R.id.tree_listview);
		
		// 创建树视图适配器，并将其绑定到树视图列表上
		mTreeAdapter = new DataTreeListAdapter<T>(getActivity(), mTreeInterface, DataTreeListAdapter.BACKGROUND_TYPE_TREE_CONTENT);
		mTreeListView.setAdapter(mTreeAdapter);
	}
	
	/**
	 * 初始化浮动菜单，可定制重写该方法
	 */
	private void initTreeFloatingMenu() {
		// 提取资源信息
		mTreeFloatingMenuImages = new int[] { R.drawable.icn_add };
		mTreeFloatingMenuTips = new String[] { getResources().getString(R.string.add) };
		
		if (mTreeFloatingMenuImplement != null) {
			setTreeFloatingMenuImages(mTreeFloatingMenuImplement.getFloatingMenuImages());
			setTreeFloatingMenuTips(mTreeFloatingMenuImplement.getFloatingMenuTips());
		}
        mTreeFloatingMenu = (FloatingMenuView) mRootLayout.findViewById(R.id.floating_menu);
        
        // 初始化浮动菜单项
        for (int i = 0; i < mTreeFloatingMenuTips.length; i++) {
            mTreeFloatingMenu.addPopItem(mTreeFloatingMenuTips[i], mTreeFloatingMenuImages[i]);
        }
        
        // 为菜单项添加监听
        if (mTreeFloatingMenuImplement != null) {
        	setTreeItemClickListener(mTreeFloatingMenuImplement.getFloatingMenuListener());
        } else {
        	setTreeItemClickListener(mTreeItemClickListener);
        }
        mTreeFloatingMenu.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 设置浮动菜单图片
	 * @param menuImages
	 */
	private void setTreeFloatingMenuImages(int[] menuImages) {
		if (menuImages != null) {
			mTreeFloatingMenuImages = menuImages;
		}
	}
	
	/**
	 * 设置浮动菜单提示
	 * @param menuTips
	 */
	private void setTreeFloatingMenuTips(String[] menuTips) {
		if (menuTips != null) {
			mTreeFloatingMenuTips = menuTips;
		}
	}
	
	/**
	 * 浮动菜单监听器
	 */
	private OnItemClickListener mTreeItemClickListener = new OnItemClickListener() {
		
		 @Override
         public void onItemClick(AdapterView<?> parent,
         						View view,
         						int position,
         						long id) {
			 switch (position) {
	             case 0:
	             	 mIsAddOperation = true;
	             	 mIsFloatMenuAdd = false;
	                 mTreeDialog.show(null);
	                 break;
	             default:
	                 break;
			 }
         }
	};
	
	/**
	 * 设置浮动菜单的监听器
	 * @param listener
	 */
	private void setTreeItemClickListener(OnItemClickListener listener) {
		if (listener != null) {
			mTreeItemClickListener = listener;
		}
		
		mTreeFloatingMenu.setPopOnItemClickListener(mContentItemClickListener);
	}
	
	/**
	 * 初始化弹出菜单，可定制重写该方法
	 */
	protected void initTreeOptionsMenu() {
		// 增加，修改，删除
		mTreeOptionSubMenuNames = getResources()
				.getStringArray(R.array.directory_list_options_menu);
		
		if (mTreeOptionMenuImplement != null 
				&& mTreeOptionMenuImplement.getOptionMenuNames() != 0) {
			mTreeOptionSubMenuNames = getResources()
					.getStringArray(mTreeOptionMenuImplement.getOptionMenuNames());
		}
        mTreeOptionsMenu = new OptionsMenuView(getActivity(), mTreeOptionSubMenuNames);
        
        // 设置监听
        if (mTreeOptionMenuImplement != null) {
        	setTreeOptionSubMenutListener(mTreeOptionMenuImplement.getOptionMenuClickListener());
        } else {
        	setTreeOptionSubMenutListener(mTreeSubMenuListener);
        }
	}
	
	/**
	 * 设置选项菜单监听器
	 * @param listener
	 */
	protected void setTreeOptionSubMenutListener(SubMenuListener listener) {
		if (listener != null) {
			mTreeSubMenuListener = listener;
		}
		
		mTreeOptionsMenu.setSubMenuListener(mTreeSubMenuListener);
	}
	
	
	/**
	 * 选项菜单监听器
	 */
	private SubMenuListener mTreeSubMenuListener = new SubMenuListener() {

		@Override
		public void onSubMenuClick(View view) {
			mTreeOptionsMenu.dismiss();
			switch ((Integer) view.getTag()) {
				case 0:		// 增加
					if (!mCurrentTree.isHas_child() 
								&& mCurrentTree.getCount() > 0) {
						sendMessage(SHOW_TOAST, getResources()
											.getString(R.string.tree_node_cannot_create));
					} else {
						mIsAddOperation = true;
						mIsFloatMenuAdd = true;
						mTreeDialog.show(null);
					}
					break;
				case 1:		// 修改
					mIsAddOperation = false;
					showUpdateTreeDialog();
					break;
				case 2:		// 删除
					if (mCurrentTree.getCount() == 0) {
						mIsTreeOperation = true;
						commonConfirmDelete();
					} else {
						sendMessage(SHOW_TOAST, getResources()
								.getString(R.string.tree_node_cannot_delete));
					}
					break;
			}
		}
		
	};
	
	
	/**
	 * 显示并填充更新对话框
	 */
	private void showUpdateTreeDialog() {
		Map<String, String> treeNodeMap = MiscUtils.beanToMap(mCurrentTree);
		
		// 为默认值字符串分配内存
		String[] defaultValues = new String[mTreeUpdateFields.length];
		for (int i = 0; i < mTreeUpdateFields.length; i++ ) {
			defaultValues[i] = treeNodeMap.get(mTreeUpdateFields[i]);
		}
		
		// 显示对话框
		mTreeDialog.show(defaultValues);
	}
	
	/**
	 * 初始化弹出对话框
	 */
	private void initTreeDialog() {
		// 对话框标题
		String title = getResources()
				.getString(mTreeDialogImplement.getDialogTitleId());
		mTreeDialog = new BaseDialog(getActivity(), title);
		
		// 对话框显示内容初始化
		if (DialogAdapterInterface.class.isInstance(mTreeDialogImplement)) {
			mTreeDialog.init(mTreeDialogImplement.getDialogLableNames(),
							 ((DialogAdapterInterface) mTreeDialogImplement).getDialogStyles(), 
							 ((DialogAdapterInterface) mTreeDialogImplement).getSupplyData());
			((DialogAdapterInterface) mTreeDialogImplement).additionalInit(mTreeDialog);
		} else {
			mTreeDialog.init(mTreeDialogImplement.getDialogLableNames(), null, null);
		}
		
        Button saveImageView = (Button) mTreeDialog.getPopupView()
                							.findViewById(R.id.save_Button);
        saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// 保存界面输入的数据
				mSaveData = mTreeDialog.SaveData();
				
				Integer[] columns = mTreeDialogImplement.getImportantColumns(mSaveData);
				
				if (columns != null) {
					for (Integer column : columns) {
						if (mSaveData.get(mTreeDialogNames[column]).equals("")) {
							sendMessage(SHOW_TOAST, 
									getResources().getString(R.string.pls_input_all_info));
								return;
						}
					}
				}
				
				if (mIsAddOperation) {	// 添加
					// 获取界面保存的数据
					mTreeServiceImplement.addItem(treeAddFieldCopy());
				} else {	// 修改
					copyUpdateFieldToItem(true);
					mTreeServiceImplement.updateItem(mCurrentUpdateTree);
				}
				mTreeDialog.dismiss();
			}        	
        });
	}
	
	/**
	 * 树添加拷贝
	 * @return
	 */
	@SuppressLint("NewApi") 
	private T treeAddFieldCopy() {
		// 树列表项添加域拷贝
		T treeNode = null;
		try {
			treeNode = mTreeClass.newInstance();
		} catch (java.lang.InstantiationException 
						| IllegalAccessException e) {
			e.printStackTrace();
		}
		Field[] fs = mTreeClass.getDeclaredFields();
		fieldCopy(fs, treeNode, mTreeUpdateFieldMap);
		return treeNode;
	}
	
	/**
	 * 内容添加拷贝
	 * @return
	 */
	@SuppressLint("NewApi") 
	private C contentAddFieldCopy() {
		// 内容列表项添加域拷贝
		C contentItem = null;
		try {
			contentItem = mContentClass.newInstance();
		} catch (java.lang.InstantiationException 
						| IllegalAccessException e) {
			e.printStackTrace();
		}
		Field[] fs = mContentClass.getDeclaredFields();
		fieldCopy(fs, contentItem, mContentUpdateFieldMap);
		return contentItem;
	}
	
	/**
	 * 更新域拷贝
	 * @param isTreeItem
	 * @param saveData
	 */
	private void copyUpdateFieldToItem(boolean isTreeItem) {
		// 树列表项更新域拷贝
		if (isTreeItem) {
			mCurrentUpdateTree = MiscUtils.clone(mCurrentTree);
			Field[] fs = mTreeClass.getDeclaredFields();
			fieldCopy(fs, mCurrentUpdateTree, mTreeUpdateFieldMap);
		// 内容列表项更新域拷贝
		} else {
			mCurrentUpdateContent = MiscUtils.clone(mCurrentContent);
			Field[] fs = mContentClass.getDeclaredFields();
			fieldCopy(fs, mCurrentUpdateContent, mContentUpdateFieldMap);
		}
	}
	
	/**
	 * 将UI添加或修改域拷贝到对应的对象中
	 * @param fs
	 * @param target
	 * @param map
	 * @param saveData
	 */
	private <K> boolean fieldCopy(  Field[] fs, 
									K target, 
									Map<String, String> map) {
		if (mSaveData != null) {
			// 遍历类域
			for (int i = 0; i < fs.length; i++) {
				Field field = fs[i];
				field.setAccessible(true);
				
				// 如果映射表中包含了该域的名称
				if (map.containsKey(field.getName())) {
					Class<?> type = field.getType();
					String fieldName = field.getName();
					String value = mSaveData.get(map.get(fieldName));
					if (!mIsTreeOperation
							&& mContentUpdateSwitchMap != null 
							&& mContentUpdateSwitchMap.containsKey(fieldName)) {
						value = mContentUpdateSwitchMap.get(fieldName).get(value);
					}
					
					try {
						if (type.equals(String.class)) {
							field.set(target, value);
						} else if (type.equals(Date.class)) {
							if (!value.equals("")) {
								field.set(target, DateUtils.stringToDate(DateUtils.FORMAT_LONG, value));
							} else {
								field.set(target, null);
							}
						} else {
							// 清除
							if (value.equals("")) {
								value = "0";
							}
							
							if (type.equals(int.class)) {
								field.setInt(target, Integer.parseInt(value));
							} else if (type.equals(double.class)) {
								field.setDouble(target, Double.parseDouble(value));
							} else if (type.equals((long.class))) {
								field.setLong(target, Long.parseLong(value));
							}
						}
					} catch (IllegalAccessException 
								| IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
			// 重置保存数据
			mSaveData = null;
			return true;
		}
		
		return false;		
	}
	
	/**
	 * 删除确认提示
	 */
	private void commonConfirmDelete() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(getActivity())
			// 设置对话框主体内容
	        .setMessage(getResources().getString(R.string.confirm_delete))
	        // 设置对话框标题
	        .setTitle(getResources().getString(R.string.remind))
	        // 为对话框按钮注册监听
	        .setPositiveButton(getResources().getString(R.string.confirm),
	        	new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	// 首先隐藏对话框
		                dialog.dismiss();
		                
		                if (!mIsTreeOperation) { 	// 删除材料
		                    mRemoveList.addAll(mContentAdapter.getSelectedDatas());
		                } else {	// 删除一个材料类型
		                	if (mCurrentTree.getCount() == 0) {
		                		mTreeServiceImplement.deleteItem(mCurrentTree);
		                	} else {
		                		mRemoveList.addAll(getContentFromTreeNode(mCurrentTree));
		                	}
		                }
		                
		                // 开始循环删除材料，普通模式和批量删除模式一样处理
		                for (int i = 0; i < mRemoveList.size(); i++) {
	                    	C contentItem = mRemoveList.get(i);
	                    	mContentServiceImplement.deleteItem(contentItem);
	                    }
	                    
	                    // 如果mActionMode不为空，说明是批量模式，要结束批量模式
	                    if (mActionMode != null) {
	                        mActionMode.finish();
	                        mActionMode = null;
	                    }
		            }
	        })
	        .setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		        }
	        }).show();
	}
	
	/**
	 * 获取叶子节点的内容列表
	 * @param treeNode
	 * @return
	 */
	protected List<C> getContentFromTreeNode(T treeNode) {	
		// 已经展开，直接获取显示列表
		if (treeNode.isExpanded()) {	
			return mContentAdapter.getDataShowList();
		}
		
		List<C> itemList = new ArrayList<C>();
		// 该类型有资源，但未展开，遍历内容列表
		if (treeNode.getCount() > 0) {	
			List<C> dataList = mContentAdapter.getDataList();
			for (C item : dataList) {
				if (mTreeContentCombImplement.getTreeContentCombId(item) 
										== treeNode.getId()) {
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
		List<List<T>> levelList = mTreeAdapter.getLevelList();
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
		
		List<T> treeDataList = mTreeAdapter.getDataList();
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
		List<C> dataList = mContentAdapter.getDataList();
		for (C c : dataList) {
			if (mTreeContentCombImplement.getTreeContentCombId(c) 
					== treeNode.getId()) {
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
	 * 用于返回内容适配器中已经选择的列表项
	 * @return
	 */
	public List<C> getSelectedDataList() {
		return mContentAdapter.getSelectedDatas();
	}
	
	/**
	 * 初始化内容视图
	 */
	private void initContentView() {
			
		// 初始化内容列表头
		initContentListHeader();
		
		// 为内容列表设置适配器
		contentListSetAdapter();
		
		if (OperationMode.NORMAL == mCurrentMode) {
			if (mHasEditPermission) {
				// 初始化浮动菜单
				if (mEnableContentFloatingMenu) {
					initContentFloatingMenu();
				}
				
				// 初始化内容弹出菜单
				if (mEnableContentOptionMenu) {
					initContentOptionsMenu();
				}
				
				// 初始化内容弹出对话框
				if (mEnableContentFloatingMenu
						|| mEnableContentOptionMenu) {
					initContentDialog();
				}
			}
		} else if (mEnableInnerButton) {
		    initActionButton();
		}
	}
	
	/**
	 * 初始化内容列表头
	 */
	private void initContentListHeader() {
		// 库存列表头布局
	    ViewGroup parent = (ViewGroup) mRootLayout.findViewById(R.id.content_header_layout);
		mContentListHeader = LayoutInflater.from(getActivity())
                .inflate(mContentListImplement.getListHeaderLayoutId(), parent, false);
		
		if (OperationMode.MULTI_SELECT == mCurrentMode) {
		    mHeaderCheckBox = (CheckBox) mContentListHeader.findViewById(R.id.mult_select);
		    mHeaderCheckBox.setVisibility(View.VISIBLE);
		    mHeaderCheckBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (((CheckBox) view).isChecked()) {
						mSelectedDataList.removeAll(mContentAdapter.getSelectedDatas());
						mContentAdapter.setSelectedAll();
						mSelectedDataList.addAll(mContentAdapter.getSelectedDatas());
					} else {
						mSelectedDataList.removeAll(mContentAdapter.getSelectedDatas());
						mContentAdapter.clearSelection();
						mContentAdapter.notifyDataSetChanged();
					}
				}
			});
			//mContentListHeader.findViewById(R.id.select_right_view).setVisibility(View.VISIBLE);
		}
		
		// 获取列表使用的相关资源
		mContentItemNames = getResources()
						.getStringArray(mContentListImplement.getListHeaderNames());
		TypedArray typedArray = getResources()
						.obtainTypedArray(mContentListImplement.getListHeaderIds());

		if (mContentItemNames != null) {
			mContentHeaderItemIds = new int[typedArray.length()];
			for (int i = 0; i < mContentHeaderItemIds.length; i++) {
				mContentHeaderItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mContentItemNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) 
							mContentListHeader.findViewById(mContentHeaderItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.table_title_textsize));
					tv.setText(mContentItemNames[i]);
				}
			}
		}
		
		typedArray.recycle();
		parent.addView(mContentListHeader);
	}
	
	/**
	 * 为内容列表设置适配器   
	 */
	private void contentListSetAdapter() {
		
		// 获取内容列表视图控件
		mContentListView = (ListView) mRootLayout.findViewById(R.id.content_listview);
		
		// 初始化适配器并与内容列表视图控件绑定
		mContentAdapter = new DataListAdapter<C>(getActivity(), mListInterface, mContentListImplement.getListHeaderIds());
		mContentAdapter.setOngetBDLASlidePaneListener(this);
		mContentListView.setAdapter(mContentAdapter);
	}
	
	/**
	 * 初始化浮动菜单，可定制重写该方法
	 */
	protected void initContentFloatingMenu() {
		// 提取资源信息
		mContentFloatingMenuImages = new int[] { R.drawable.icn_add };
		mContentFloatingMenuTips = new String[] { getResources().getString(R.string.add) };
		
		if (mContentFloatingMenuImplement != null) {
			setContentFloatingMenuImages(mContentFloatingMenuImplement.getFloatingMenuImages());
			setContentFloatingMenuTips(mContentFloatingMenuImplement.getFloatingMenuTips());
		}
        mContentFloatingMenu = (FloatingMenuView) mRootLayout.findViewById(R.id.floating_menu);
        
        // 初始化浮动菜单项
        for (int i = 0; i < mContentFloatingMenuTips.length; i++) {
            mContentFloatingMenu.addPopItem(mContentFloatingMenuTips[i], mContentFloatingMenuImages[i]);
        }
        
        // 为菜单项添加监听
        if (mContentFloatingMenuImplement != null) {
        	setContentItemClickListener(mContentFloatingMenuImplement.getFloatingMenuListener());
        } else {
        	setContentItemClickListener(mContentItemClickListener);
        }
        mContentFloatingMenu.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 设置浮动菜单图片
	 * @param menuImages
	 */
	private void setContentFloatingMenuImages(int[] menuImages) {
		if (menuImages != null) {
			mContentFloatingMenuImages = menuImages;
		}
	}
	
	/**
	 * 设置浮动菜单提示
	 * @param menuTips
	 */
	private void setContentFloatingMenuTips(String[] menuTips) {
		if (menuTips != null) {
			mContentFloatingMenuTips = menuTips;
		}
	}
	
	/**
	 * 浮动菜单监听器
	 */
	private OnItemClickListener mContentItemClickListener = new OnItemClickListener() {
		
		 @Override
         public void onItemClick(AdapterView<?> parent,
         						View view,
         						int position,
         						long id) {
			 switch (position) {
             case 0:
             	 mIsAddOperation = true;
                 mContentDialog.show(null);
                 break;
             default:
                 break;
			 }
         }
	};
	
	/**
	 * 设置浮动菜单的监听器
	 * @param listener
	 */
	private void setContentItemClickListener(OnItemClickListener listener) {
		if (listener != null) {
			mContentItemClickListener = listener;
		}
		
		mContentFloatingMenu.setPopOnItemClickListener(mContentItemClickListener);
	}
	
	/**
	 * 初始化内容弹出菜单，可定制重写该方法
	 */
	protected void initContentOptionsMenu() {
		// 修改，删除
		mContentOptionSubMenuNames = getResources()
				.getStringArray(R.array.list_options_menu);
		if (mContentOptionMenuImplement != null
				&& mContentOptionMenuImplement.getOptionMenuNames() != 0) {
			mContentOptionSubMenuNames = getResources()
					.getStringArray(mContentOptionMenuImplement.getOptionMenuNames());
		}
        mContentOptionsMenu = new OptionsMenuView(getActivity(), mContentOptionSubMenuNames);
        
        if (mContentOptionMenuImplement != null) {
        	setContentOptionSubMenutListener(mContentOptionMenuImplement.getOptionMenuClickListener());
        } else {
        	setContentOptionSubMenutListener(mContentSubMenuListener);
        }
	}
	
	/**
	 * 设置选项菜单监听器
	 * @param listener
	 */
	protected void setContentOptionSubMenutListener(SubMenuListener listener) {
		if (listener != null) {
			mContentSubMenuListener = listener;
		}
		
		mContentOptionsMenu.setSubMenuListener(mContentSubMenuListener);
	}
	
	
	/**
	 * 选项菜单监听器
	 */
	private SubMenuListener mContentSubMenuListener = new SubMenuListener() {

		@Override
		public void onSubMenuClick(View view) {
			mContentOptionsMenu.dismiss();
			switch ((Integer) view.getTag()) {
				case 0:		// 修改
					mIsAddOperation = false;
					showUpdateContentDialog(true);
					break;
				case 1:		// 删除
					mIsTreeOperation = false;
					commonConfirmDelete();
					break;
			}
		}
		
	};
	
	/**
	 * 修改一条资源
	 */
	private void showUpdateContentDialog(boolean isEdit) {
		// 设置是否可以修改
		mContentDialog.switchModifyDialog(isEdit);
				
		Map<String, String> contentItemMap 
							= MiscUtils.beanToMap(mCurrentContent);
		
		// 构建默认值数组
		String[] defaultValues = new String[mContentUpdateFields.length];
		for (int i = 0; i < mContentUpdateFields.length; i++) {
			if (mContentUpdateSwitchMap != null &&
					mContentUpdateSwitchMap.containsKey(mContentUpdateFields[i])) {
				defaultValues[i] = mContentUpdateSwitchMap.get(mContentUpdateFields[i])
										.get(contentItemMap.get(mContentUpdateFields[i]));
			} else {
				defaultValues[i] = contentItemMap.get(mContentUpdateFields[i]);
			}
		}
		
		// 显示内容对话框
		mContentDialog.show(defaultValues);
	}
	
	/** 
	 * 初始化内容弹出对话框
	 */
	private void initContentDialog() {
		// 对话框标题
		String title = getResources()
				.getString(mContentDialogImplement.getDialogTitleId());
		mContentDialog = new BaseDialog(getActivity(), title);

		// 对话框显示内容初始化
		if (DialogAdapterInterface.class.isInstance(mContentDialogImplement)) {
			mContentDialog.init(mContentDialogImplement.getDialogLableNames(), 
					((DialogAdapterInterface) mContentDialogImplement).getDialogStyles(), 
					((DialogAdapterInterface) mContentDialogImplement).getSupplyData());
			((DialogAdapterInterface) mContentDialogImplement).additionalInit(mContentDialog);
		} else {
			mContentDialog.init(mContentDialogImplement.getDialogLableNames(), null, null);
		}
		
        Button saveImageView = (Button) mContentDialog.getPopupView()
                							.findViewById(R.id.save_Button);
        saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// 保存界面输入的数据
				mSaveData = mContentDialog.SaveData();
				Integer[] columns = mContentDialogImplement.getImportantColumns(mSaveData);
				
				if (columns != null) {
					for (Integer column : columns) {
						if (mSaveData.get(mContentDialogNames[column]).equals("")) {
							sendMessage(SHOW_TOAST, 
									getResources().getString(R.string.pls_input_all_info));
								return;
						}
					}
				}
				
				if (mIsAddOperation) {	// 添加
					// 获取界面保存的数据
					mContentServiceImplement.addItem(contentAddFieldCopy());
				} else {	// 修改
					copyUpdateFieldToItem(false);
					mContentServiceImplement.updateItem(mCurrentUpdateContent);
				}
				mContentDialog.dismiss();
			}        	
        });
	}
	
	/**
	 * 确认/取消 按钮
	 */
	private void initActionButton() {
	    mButtonBar = (View) mRootLayout.findViewById(R.id.button_bar);
	    mButtonBar.setVisibility(View.VISIBLE);
	    
        Button ok = (Button) mRootLayout.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View view) {
                Serializable result = null;
                List<C> data = null;
                if (mCurrentMode == OperationMode.SINGLE_SELECT) {
                	data = getSelectedDataList();
                    if (data.size() == 1) {
                        result = data.get(BASE_POSITION);
                    }
                } else {
                	data = mSelectedDataList;
                    if (data.size() > 0) {
                        result = (Serializable) data;
                    }
                }
                if (result == null) {
                	Toast.makeText(getActivity(), R.string.pls_select, Toast.LENGTH_SHORT).show();
                	return;
                }
                setSeletctedData(result);
            }
        });

        Button cancel = (Button) mRootLayout.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                setSeletctedData(null);
            }
        });
    }
	
	/**
	 * 返回选中的数据
	 * @param result
	 */
    private void setSeletctedData(Serializable result) {
        Intent intent = new Intent();
        
        if (result != null) {
            // 传回选中的列表数据
            Bundle resultBundle = new Bundle();
            resultBundle.putSerializable(RESULT_KEY, result);
            intent.putExtras(resultBundle);
            getActivity().setResult(Activity.RESULT_OK, intent);
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED, intent);
        }
        
        // 结束当前Activity
        getActivity().finish();
    }
		
	/**
	 * 加载数据
	 */
	private void loadData() {
		// 延时显示进度对话框
		mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS_DIALOG, 
										 DELAYTIME_FOR_SHOW);
		
		// 加载资源类型数据
		mContentServiceImplement.getListData();
		
		// 加载资源数据
		mTreeServiceImplement.getListData();			
	}
	
    /**
     * 用于列表的多选 或批量操作
     */
    private ActionMode.Callback mCallback = new ActionMode.Callback() {
    	// 进入批量操作前的准备工作
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        // 退出批量操作前的清理工作
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mContentAdapter.setSelected(-1, false);
            mActionMode = null;
            mode = null;
        }

        // 进入批量操作初始化工作
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.operation_menu, menu);
            return true;
        }

        // 批量操作处理
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
	            case R.id.action_delete:	// 删除操作
	            	mIsTreeOperation = false;
	                commonConfirmDelete();
	                break;
	                
	            case R.id.action_select_all:	// 权限操作
	                mContentAdapter.setSelectedAll();
	                updateActionModeTitle(  mActionMode, 
    						getActivity(), 
    						mContentAdapter.getSelectedList().size());
	                break;
            }
            return false;
        }
    };

    /**
     * 当增加或减少选择项时，更新ActionMode标题
     * @param mode
     * @param context
     * @param selectedNum
     */
    private void updateActionModeTitle(  ActionMode mode, 
    									Context context,
    									int selectedNum) {
        if (mode != null) {
            mode.setTitle(context.getString(R.string.multi_select_title, selectedNum));
        }
    }
    
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
			if (mContentAdapter != null) {
				mContentAdapter.setSPListener();
			}
		}
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

	/**
	 * 单击处理
	 * @param position
	 */
	public void handleClick(int position, View view) {
		mCurrentTree = mTreeAdapter.getItem(position);
		mTreeAdapter.setSelected(position, true);
		if (view != null) {
			mTreeAdapter.updateListView(position);
		}
		
		if (mCurrentTree.isHas_child()) {
			// 选择模式和无编辑权限下，mContentFloatingMenu不被初始化，为null
			if (mContentFloatingMenu != null) {
				mContentFloatingMenu.setVisibility(View.INVISIBLE);
			}
			
			// 复选模式下，如果当前树节点有孩子节点，退出复选模式
			if (mActionMode != null) {
				mActionMode.finish();
				mActionMode = null;
			}
			
			if (mMixMode) {
				mContentAdapter.setShowDataList(getContentsList(mCurrentTree));
			} else {
				mContentAdapter.setShowDataList(getContentsFromTree(mCurrentTree));
			}
		} else {
			if (mContentFloatingMenu != null) {
				mContentFloatingMenu.setVisibility(View.VISIBLE);
			}
			mContentAdapter.setShowDataList(getContentFromTreeNode(mCurrentTree));
		}
		
		// 过滤
		if (mFilterDataList != null && !mFilterDataList.isEmpty()) {
			filterSelectedDatas(mContentAdapter.getDataShowList());
		}
		
		if (mCurrentMode == OperationMode.MULTI_SELECT) {
			if (mSelectedDataList.size() > 0 
					&& mSelectedDataList.containsAll(mContentAdapter.getDataShowList())) {
				mHeaderCheckBox.setChecked(true);
			} else {
				mHeaderCheckBox.setChecked(false);
			}
		}
	}
    
    /**
     * 树视图适配器接口实现
     */
    private TreeListAdapterInterface mTreeInterface = new TreeListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return R.layout.treeview_list_item;
		}

		@Override
		public void initListViewItem(ViewHolder viewHolder, int position) {
			// 获取position的数据项，并转化为map
			T treeNode = mTreeAdapter.getItem(position);
			// 建立域名称和域值的映射
			Map<String, String> treeNodeMap = MiscUtils.beanToMap(treeNode);
			
			// 如果有子类型，显示为文件夹图标，否则显示文件图标
			if (treeNode.isHas_child()) {
				viewHolder.ivs[1].setImageResource(R.drawable.folder2);
			} else {
				viewHolder.ivs[1].setImageResource(R.drawable.file_icon_default);
			}
			
			
			
			String str = treeNodeMap.get(mTreeDisplayFields[0])
					+ "(" + treeNode.getCount() + ")" ;
			
			int fstart=str.indexOf("(" + treeNode.getCount() + ")");
	        int fend=fstart+String.valueOf(treeNode.getCount()).length()+2;
	        SpannableStringBuilder style=new SpannableStringBuilder(str);
	        if (treeNode.getCount() > 0) {
	        	style.setSpan(new ForegroundColorSpan(Color.BLUE),fstart,fend,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
	        	viewHolder.tvs[0].setText(style);
	        } else {
	        	viewHolder.tvs[0].setText(treeNodeMap.get(mTreeDisplayFields[0]));	
	        }
			
			
			viewHolder.tvs[0].setEllipsize(TruncateAt.MARQUEE);
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			for (int i = 0; i < viewHolder.ivs.length; i++) {
				viewHolder.ivs[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						handleClick(position, view);
					}
				});
				
				viewHolder.ivs[i].setOnLongClickListener(new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View view) {
						return handleLongClick(view, position);
					}
				});
			}
			
			viewHolder.tvs[0].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					handleClick(position, view);				
				}
			});
			
			viewHolder.tvs[0].setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View view) {
					return handleLongClick(view, position);
				}
			});
		}
		
		/**
		 * 长按处理
		 * @param view
		 * @param position
		 */
		private boolean handleLongClick(View view, int position) {
			mCurrentTree = mTreeAdapter.getItem(position);
			mTreeAdapter.setSelected(position, true);
			
			// 选择模式和无编辑权限下，mTreeOptionsMenu不被初始化，为null
			if (mTreeOptionsMenu != null) {
				mTreeOptionsMenu.showAsDropDown(view, 0,
	                    (-view.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
			}
			
			return true;
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.ivs = new ImageView[2];
			holder.tvs = new TextView[1];
			
			int index = 0;
			// 初始化ImageView
			for (int i = 0; i < holder.ivs.length; i++) {
				holder.ivs[i] = (ImageView) convertView.findViewById(mTreeItemIds[index++]);
			}
			// 初始化TextView
			for (int i = 0; i < holder.tvs.length; i++) {
				holder.tvs[i] = (TextView) convertView.findViewById(mTreeItemIds[index++]);
			}			
		}

		@Override
		public void calculateContentItemCount() {
			List<T> treeDataList = mTreeAdapter.getDataList();
			List<C> contentDataList = mContentAdapter.getDataList();
			
	        if (contentDataList != null && contentDataList.size() > 0) {
	            // 遍历文件
	            for (int i = 0; i < contentDataList.size(); i++) {
	                // 遍历目录
	                for (int j = 0; j < treeDataList.size(); j++) {
	                    // 如果文件的目录就是该目录
	                    if (mTreeContentCombImplement.getTreeContentCombId(contentDataList.get(i)) 
	                    				== treeDataList.get(j).getId()) {
	                    	treeDataList.get(j).setCount(treeDataList.get(j).getCount() + 1);
	                        // 更新父目录的计数
	                        if (treeDataList.get(j).getParents_id() > 0) {
	                            mTreeAdapter.setParentTreeNodeCount(treeDataList, treeDataList.get(j), 1);
	                        }
	                        // 找到就跳出循环
	                        break;
	                    }
	                }
	            }
	        }
	    }
	};
	
	/**
	 * 该类为定制类，外部类已经给出了一般实现
	 * @author yuanlu
	 *
	 */
	public abstract class ListAdapterImplement implements ListAdapterInterface<C> {

		@Override
		public int getLayoutId() {
			return mContentListImplement.getListLayoutId();
		}

		@Override
		public View getHeaderView() {
			return mContentListHeader;
		}

		@Override
		public List<C> findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(C c1, C c2) {
			return mContentListImplement.getListItemId(c1) 
						== mContentListImplement.getListItemId(c2);
		}
	}
	
	/**
	 * 内容视图适配器接口一般实现，
	 * 如需定制请重写实现mListInterface的三个接口
	 */
	private ListAdapterInterface<C> mListInterface = new ListAdapterImplement() {
		// 重写三个接口
		@Override
		public void regesterListeners(final DataListAdapter.ViewHolder viewHolder,
										final int position) {
			for (int i = 0; i < mContentItemIds.length; i++ ) {
				viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						// ActionMode批量操作模式
						if (mActionMode != null 
								&& !mCurrentTree.isHas_child()) {
							mContentAdapter.setPickSelected(position);
							updateActionModeTitle(mActionMode, getActivity(),
                                    			  mContentAdapter.getSelectedList().size());
						// 非多选模式，弹出选项菜单，但如果是单选模式也不会弹出
						} else if (OperationMode.MULTI_SELECT != mCurrentMode) {
							mCurrentContent = mContentAdapter.getItem(position);
							mContentAdapter.setSelected(position, true);
							
							// 如果是选择模式和无编辑权限下，mContentOptionsMenu不被初始化，为空
							if (mContentOptionsMenu != null 
									&& !mCurrentTree.isHas_child()) {
								mContentOptionsMenu.showAsDropDown(view, 0, (-view
										.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
							}
						} else { // 多选择模式
							if (viewHolder.cbs[0].isChecked()) {
								viewHolder.cbs[0].setChecked(false);
								if (mHeaderCheckBox.isChecked()) {
									mHeaderCheckBox.setChecked(false);
								}
								
								C c = mContentAdapter.getItem(position);
								mSelectedDataList.remove(c);
							} else {
								viewHolder.cbs[0].setChecked(true);
								mCurrentContent = mContentAdapter.getItem(position);
								
								mSelectedDataList.add(mCurrentContent);
							}
							mContentAdapter.setPickSelected(position);
						}
					}
				});
				
				// 多选模式，启动多选功能（注册复选框的单击监听器）
				if (OperationMode.MULTI_SELECT == mCurrentMode) {
					if (mContentAdapter.isContainPosition(position)) {
						viewHolder.cbs[0].setChecked(true);
					} else {
						viewHolder.cbs[0].setChecked(false);
					}
					
					viewHolder.cbs[0].setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View view) {
							if (((CheckBox) view).isChecked()) {
								mCurrentContent = mContentAdapter.getItem(position);
								
								mSelectedDataList.add(mCurrentContent);
							} else {
								if (mHeaderCheckBox.isChecked()) {
									mHeaderCheckBox.setChecked(false);
								}
								
								C c = mContentAdapter.getItem(position);
								mSelectedDataList.remove(c);
							}
							mContentAdapter.setPickSelected(position);
						}
					});
				}
				
				// 正常模式下，注册长按监听器
				if (OperationMode.NORMAL == mCurrentMode) {
					// 注册长按监听
	                viewHolder.tvs[i].setOnLongClickListener(new View.OnLongClickListener() {
	                    @Override
	                    public boolean onLongClick(View view) {
	                    	if (!mCurrentTree.isHas_child() 
	                    			&& mHasEditPermission) {
		                    	// 长按进入ActionMode，此时ActionMode应该是NULL
		                        mActionMode = getActivity().startActionMode(mCallback);
		                        mContentAdapter.setPickSelected(position);
		                        updateActionModeTitle(mActionMode, getActivity(),
		                                				mContentAdapter.getSelectedList().size());
	                    	} else {
	                    		mCurrentContent = mContentAdapter.getItem(position);
	                    		mContentAdapter.setSelected(position, true);
	                    	}
	                        return true;
	                    }
	                });
				}	
			}
		}

		@SuppressLint("ResourceAsColor") 
		@Override
		public void initListViewItem(View convertView,
										DataListAdapter.ViewHolder holder,
										DataListAdapter<C> adapter, 
										int position) {
			// 获取当前位置对象，然后转换为映射
			C contentItem = adapter.getItem(position);
			
			if (OperationMode.MULTI_SELECT == mCurrentMode
					&& mDefaultDataList != null) {
				for (C defaultData : mDefaultDataList) {
					if (isSameObject(contentItem, defaultData)) {
						holder.cbs[0].setChecked(true);
						holder.cbs[0].setClickable(false);
					}
				}
				
			}
			
			if (OperationMode.MULTI_SELECT == mCurrentMode
					&& mSelectedDataList.contains(contentItem)) {
				if (!mContentAdapter.isContainPosition(position)) {
					holder.cbs[0].setChecked(true);
					mContentAdapter.setPickSelected(position);
				}
			}
			
			Map<String, String> contentMap = MiscUtils.beanToMap(contentItem);
			
			displayFieldRemap(contentMap, contentItem, position);
			
			for (int i = 0; i < mContentDisplayFields.length; i++) {
				holder.tvs[i].setText(contentMap.get(mContentDisplayFields[i]));
				if (OperationMode.NORMAL != mCurrentMode) {
					holder.tvs[i].setTextColor(Color.BLACK);
				}
				holder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
			}
			
		}

		@Override
		public void initLayout(View convertView, DataListAdapter.ViewHolder holder) {
			// 分配引用数组内存
			holder.tvs = new TextView[mContentItemIds.length];
			holder.cbs = new CheckBox[1];
			
			// 为引用数组成员分配视图对象内存
			for (int i = 0; i < mContentItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
									.findViewById(mContentItemIds[i]);
			}
			
			if (OperationMode.MULTI_SELECT == mCurrentMode) {
				// 显示复选框和分界线
				holder.cbs[0] = (CheckBox) convertView.findViewById(R.id.mult_select);
				holder.cbs[0].setVisibility(View.VISIBLE);
				//convertView.findViewById(R.id.select_right_view).setVisibility(View.VISIBLE);
			}
		}
	};
	
	/**
	 * 显示域重映射
	 * @param displayFieldMap
	 * @param c
	 * @param position
	 */
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
			C c, int position) {
		if (mFieldRemapInterface != null) {
			mFieldRemapInterface.displayFieldRemap(displayFieldMap, c, position);
		} else {
			// 重映射序号
			displayFieldMap.put(SERIAL_NUMBER, position + 1 + "");
			
			// 重映射状态
			for (int i = 1; i < mContentDisplayFields.length; i++) {
				if (mContentDisplaySwitchMap != null
						&& mContentDisplaySwitchMap.containsKey(mContentDisplayFields[i])) {
					String value = displayFieldMap.get(mContentDisplayFields[i]);
					String[] vs = null;
					StringBuffer result = new StringBuffer();
					if (value.contains(",")) {
						StringBuffer sb = new StringBuffer();
						vs = value.split(",");
						for (String v : vs) {
							sb.append(mContentDisplaySwitchMap.get(mContentDisplayFields[i]).get(v));
							sb.append(",");
						}
						result.append(sb.subSequence(0, sb.length() - 1));
					} else {
						result.append(mContentDisplaySwitchMap.get(mContentDisplayFields[i]).get(value));
					}
					displayFieldMap.put(mContentDisplayFields[i], result.toString());
				}
			}
		}
	}
	
	/**
	 * 域重映射接口
	 * @author yuanlu
	 *
	 * @param <C>
	 */
	public interface FieldRemapInterface<C> {
		void displayFieldRemap(Map<String, String> displayFieldMap, C c, int position);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFilterData(List<?> filters) {
		mFilterDataList = (List<C>) filters;
		
		if (mCurrentTree != null) {
			mContentAdapter.setShowDataList(getContentsList(mCurrentTree));
		} else {
			mContentAdapter.getDataShowList().clear();
		}
		
		if (mFilterDataList != null && !mFilterDataList.isEmpty()) {
			filterSelectedDatas(mContentAdapter.getDataShowList());
		}
		
		// 清除选中的数据
		mHeaderCheckBox.setChecked(false);
		mSelectedDataList.clear();
		mContentAdapter.clearSelection();
		mContentAdapter.notifyDataSetChanged();
	}

	@Override
	public void enableInnerButton(boolean enable) {
		mEnableInnerButton = enable;
		
		// 如果已经初始化，将其设置为不可见
		if (!enable) {
			if (mButtonBar != null) {
				mButtonBar.setVisibility(View.GONE);
			}
		} else {
			
			/*
			 *  如果要使能按钮，但当前按钮为NULL并且已经完成初始化，
			 *  则初始化按钮控件
			 */
			if (mButtonBar == null && mRootLayout != null) {
				initActionButton();
			}
		}
	}
}