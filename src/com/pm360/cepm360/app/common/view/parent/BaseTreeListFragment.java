package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
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
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.BaseDragListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.TreeListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.ActionBarInterface;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SHCommonListInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleMultiSelectInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.ServerDragFrameLayout;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.core.TreePresenter;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.ExpandableSort;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class BaseTreeListFragment<T extends Expandable & Serializable> 
	extends CommonFragment {
	
	/** ----------------- 常量定义 ----------------- */
	protected static final String SERIAL_NUMBER = "serial";
	protected static final int BASE_POSITION = 0;
	protected static final int SHOW_TOAST = BASE_POSITION;
	protected static final int SHOW_PROGRESS_DIALOG = BASE_POSITION + 1;
	protected static final int DISMISS_PROGRESS_DIALOG = BASE_POSITION + 2;
	protected static final int DELAYTIME_FOR_SHOW = 500;
	
	/**------------------ 视图控件 -----------------*/
	// 根布局
	protected View mRootLayout;
	// 列表头视图
	protected View mListHeader;
	// 列表视图
	private ListView mListView;
	// 上下文菜单
	protected OptionsMenuView mOptionsMenu;
	// 浮动菜单
	protected FloatingMenuView mFloatingMenu;
	// 弹出对话框
	protected BaseDialog mDialog;
	// 进度对话框
	private ProgressDialog mProgressDialog;
	
	// 适配器adapter定义
	protected DataTreeListAdapter<T> mListAdapter;
	
	/**------------------ 保存资源数据 -------------*/
	// 保存对话框标签名数组
	protected String[] mDialogLableNames;
	
	// 通过xml文件提供内容表头控件ID数组
	protected int[] mListHeaderItemIds;
	protected int[] mListItemIds;
	// 通过xml文件提供内容表头字符串数组
	protected String[] mListHeaderNames;
	
	/**------------------ 暂存数据 -----------------*/
	// 当前更新列表项
	protected T mCurrentUpdateItem;
	// 当前数据
	protected T mCurrentItem;
	// 指定列表项
	private T mSpecifiedItem;
	
	// 界面数据保存
	private Map<String, String> mSaveData;
	
	// 保存类型对象
	private Class<T> mListItemClass;
	
	private Map<String, Map<String, String>> mDisplayFieldSwitchMap;
	private Map<String, Map<String, String>> mUpdateFieldSwitchMap;
	
	
	/**------------------ 继承者提供数据 ----------*/
	// 内容显示的域
	protected String[] mDisplayFields;
	protected String[] mUpdateFields;
	
	private int[] mMenuImages;
	private String[] mMenuTips;
	
	private String[] mSubMenuNames;
	
	
	/**------------------ 删除操作数据 ------------*/
	private List<T> mRemoveList = new ArrayList<T>();
	private int mRemoveCount;
	private int mRemoveFailedCount;
	
	/** ----------------- 映射表定义 --------------*/
	// 更新字段映射表
	private Map<String, String> mFieldLableMap;
	
	/** ----------------- 模式操作 --------------- */
		
	// 操作模式定义
	protected enum OperationMode {
		NORMAL,
		SINGLE_SELECT
	}
	
	// 当前操作模式
	protected OperationMode mCurrentMode;
	// 模式定义，普通模式和批量操作模式
	protected ActionMode mActionMode;
	// 正常模式下是否启用多选功能
	private boolean mEnableNormalMultSelect;
	
	/** ------------------- 标识 -------------------- */
	// 数据是否加载成功
	protected boolean mDataLoaded;
	protected boolean mIsBlackBackGroud;
	protected boolean mIsDeleteParentNode;
	
	// 增加修改操作，是否是增加操作
	protected boolean mIsAddOperation;
	protected boolean mIsFloatMenuAdd;
	protected boolean mIsInsertOperation;
	
	// 是否启用浮动菜单
	private boolean mEnableFloatingMenu;
	// 使能选择菜单功能
	private boolean mEnableOptionMenu;
	
	/** ------------------ 接口 ----------------- */
	// 如需要对dialog做额外的界面操作，设置该接口
	protected BaseWidgetInterface mBaseWidgetInterface;
	
	private CommonListInterface<T> mListImplement;
	private ServiceInterface<T> mServiceImplement;
	private SimpleDialogInterface mDialogImplement;
	private OptionMenuInterface mOptionMenuImplement;
	private FloatingMenuInterface mFloatingMenuImplement;
	private SimpleMultiSelectInterface mSimpleMultiSelectInterface;
	private ActionBarInterface mActionBarInterface;
	
	
	/** --------------------------------- 方法定义 ------------------------ */
	
	/**
	 * 初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 */
	private void init(Class<T> listItemClass,
						boolean isDeleteParentNode,
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface) {
		mListItemClass = listItemClass;
		mIsDeleteParentNode = isDeleteParentNode;
		mListImplement = listInterface;
		mServiceImplement = serviceInterface;
	}
	
	/**
	 * 初始化
	 * @param listItemClass
	 * @param miscInterface
	 * @param serviceInterface
	 * @param floatingMenuInterface
	 * @param dialogInterface
	 */
	private void init(Class<T> listItemClass,
						boolean isDeleteParentNode,
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						FloatingMenuInterface floatingMenuInterface,
						SimpleDialogInterface dialogInterface) {
		init(listItemClass, isDeleteParentNode, listInterface, serviceInterface);
		mFloatingMenuImplement = floatingMenuInterface;
		mDialogImplement = dialogInterface;
		enableFloatingMenu(true);
	}
	
	/**
	 * 初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param optionMenuInterface
	 * @param dialogInterface
	 */
	@SuppressWarnings("unused")
	private void init(Class<T> listItemClass,
						boolean isDeleteParentNode,
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						OptionMenuInterface optionMenuInterface,
						SimpleDialogInterface dialogInterface) {
		init(listItemClass, isDeleteParentNode, listInterface, serviceInterface);
		mOptionMenuImplement = optionMenuInterface;
		mDialogImplement = dialogInterface;
		enableOptionMenu(true);
	}
	
	/**
	 * 初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param optionMenuInterface
	 * @param dialogInterface
	 */
	protected void init(Class<T> listItemClass,
						boolean isDeleteParentNode,
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						FloatingMenuInterface floatingMenuInterface,
						OptionMenuInterface optionMenuInterface,
						SimpleDialogInterface dialogInterface) {
		init( listItemClass,
			  isDeleteParentNode,
			  listInterface, 
			  serviceInterface, 
			  floatingMenuInterface, 
			  dialogInterface);
		mOptionMenuImplement = optionMenuInterface;
		enableFloatingMenu(true);
		enableOptionMenu(true);
	}
	
	protected void init(Class<T> listItemClass,
			boolean isDeleteParentNode,
			CommonListInterface<T> listInterface,
			ServiceInterface<T> serviceInterface,
			FloatingMenuInterface floatingMenuInterface,
			OptionMenuInterface optionMenuInterface,
			SimpleDialogInterface dialogInterface,
			ActionBarInterface actionBarInterface) {
		init( listItemClass,
		  isDeleteParentNode,
		  listInterface, 
		  serviceInterface, 
		  floatingMenuInterface, 
		  dialogInterface);
		mOptionMenuImplement = optionMenuInterface;
		mActionBarInterface = actionBarInterface;
		enableFloatingMenu(true);
		enableOptionMenu(true);
	}
	
	public void setSimpleMultiSelectInterface(SimpleMultiSelectInterface simpleMultiSelectInterface) {
		mSimpleMultiSelectInterface = simpleMultiSelectInterface;
	}
	
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	protected DataManagerInterface getServiceManager() {
		return mListManager;
	}
	
	/**
	 * 使能浮动按钮
	 */
	private void enableFloatingMenu(boolean flag) {
		mEnableFloatingMenu = flag;
	}
	
	/**
	 * 启用选择菜单功能
	 */
	protected void enableOptionMenu(boolean flag) {
		mEnableOptionMenu = flag;
	}
	
	/**
	 * 启用正常模式下多选功能
	 */
	protected void enableNormalMultSelect() {
		mEnableNormalMultSelect = true;
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
			}
		}
	};
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 * @param object
	 */
	protected void sendMessage(int what, Object object) {
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
	 * 服务返回结果处理
	 */
	private DataManagerInterface mListManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_QUERY:
					mListAdapter.setDataList((List<T>) list);
					
					// 结束加载进度对话框
					mDataLoaded = true;
					sendMessage(DISMISS_PROGRESS_DIALOG);
					
					if (mSpecifiedItem != null) {
						locationSpecifidItem(mSpecifiedItem);
					}
					break;
					
				case AnalysisManager.SUCCESS_DB_ADD:
					mListAdapter.addTreeNode((T) list.get(0));
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
					MiscUtils.clone(mCurrentItem, mCurrentUpdateItem);
					mListAdapter.notifyDataSetChanged();
					break;
			}
			
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
					&& mRemoveCount == 0) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
			
			doExtraGetServerData(status, list);
		}
		
	};
	

	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		
	}
	
	/**
	 * 全部删除成功
	 */
	private void allDeleteSuccessful() {
		mListAdapter.getSelected().remove(BASE_POSITION);
		// 将当前项置为空
		mCurrentItem = null;
	}
	
	/**
	 * 部分删除成功
	 * @param status
	 */
	private void partDeleteSucessful(ResultStatus status) {
		status.setMessage(getResources()
				.getString(R.string.delete_part_failed));
		mListAdapter.clearAll();
		mServiceImplement.getListData();
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
			
			// 初始化内容视图
			initContentView();
			
			// 初始化进度对话框
			createProgressDialog();

			// 额外的操作
			doExtraEventWithViewPermission();
			
			if (mHasEditPermission) {
				doExtraEventWithEditPermission();
			}
			
			// 加载数据
			loadData();
			
		} else {
			// 没有任何权限，显示无权限信息
			showNoViewPermission(inflater, container);
		}
		
		return mRootLayout;
	}
	
	/**
	 * 重载该方法实现其他操作
	 */
	protected void doExtraEventWithViewPermission() {
		
	}
	
	/**
	 * 重载该方法实现其他操作
	 */
	protected void doExtraEventWithEditPermission() {
		
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
		initOperationMode();
		
		// 初始化显示，更新域并建立域映射表
		initFeildsAndMap();
	}
	
	/**
	 * 设置选择模式
	 */
	private void initOperationMode() {
		// 默认是正常模式
		mCurrentMode = OperationMode.NORMAL;
		mIsBlackBackGroud = true;
				
		// 设置选择模式
		Bundle bundle = getArguments();
		if (bundle != null) {
			// 获取选择模式
			if (bundle.containsKey(ListSelectActivity.SELECT_MODE_KEY)) {
				if (!bundle.getBoolean(ListSelectActivity.SELECT_MODE_KEY)) {
					mCurrentMode = OperationMode.SINGLE_SELECT;
				} else {
					sendMessage(SHOW_TOAST, getResources()
							.getString(R.string.tree_list_cannot_multi_select));
				}
			}
			
			// 获取选择模式
			if (bundle.containsKey(ListSelectActivity.IS_BLACK_BACKGROUD_COLOR)) {
				mIsBlackBackGroud = bundle.getBoolean(ListSelectActivity.IS_BLACK_BACKGROUD_COLOR);
			}
		}
		
		doExtraSetWhiteBackground();
		
	}
	
	/**
	 * 重载改方法，实现需要白色背景显示
	 */
	protected void doExtraSetWhiteBackground() {
		
	}
	
	/**
	 * 初始化显示，更新域并建立域映射表
	 */
	private void initFeildsAndMap() {
		// 初始化显示域
		mDisplayFields = mListImplement.getDisplayFeilds();
		// 初始化更新域
		mUpdateFields = mDialogImplement.getUpdateFeilds();
		// 初始化域切换映射
		mDisplayFieldSwitchMap = mListImplement.getDisplayFieldsSwitchMap();
		if (DialogAdapterInterface.class.isInstance(mDialogImplement)) {
			mUpdateFieldSwitchMap = ((DialogAdapterInterface) mDialogImplement).getUpdateFieldsSwitchMap();
		}
				
		// 设置域名和标签映射，用于反映射提取保存的界面值
		if (OperationMode.NORMAL == mCurrentMode) {
			if (mDialogImplement.getDialogLableNames() != 0) {
				mDialogLableNames = getActivity().getResources()
							.getStringArray(mDialogImplement.getDialogLableNames());
				
				// 初始化更新字段映射表
				if (mUpdateFields != null) {
					mFieldLableMap = new HashMap<String, String>();
					for (int i = 0; i < mDialogLableNames.length; i++) {
						mFieldLableMap.put(mUpdateFields[i], mDialogLableNames[i]);
					}
				}
			}
		}
		
		TypedArray typedArray = getResources()
				.obtainTypedArray(mListImplement.getListItemIds());
		mListItemIds = new int[typedArray.length()];
		for (int i = 0; i < mListItemIds.length; i++) {
			mListItemIds[i] = typedArray.getResourceId(i, 0);
		}
		typedArray.recycle();
	}
	
	/**
	 * 初始化视图布局
	 * @param inflater
	 * @param container
	 */
	private void initBasicLayout(LayoutInflater inflater, ViewGroup container) {
		// 获取fragment的根布局
		mRootLayout = inflater.inflate(R.layout.base_list_fragment, container, false);
	}
	
	/**
	 * 初始化内容视图
	 */
	private void initContentView() {
		// 初始化内容列表头
		initListHeader();
		
		// 为内容列表设置适配器
		setListAdapter();
		
		if (OperationMode.NORMAL == mCurrentMode) {
			if (mHasEditPermission) {
				if (mEnableFloatingMenu) {
					// 初始化浮动菜单
					initFloatingMenu();
				}
				
				if (mEnableOptionMenu) {
					// 初始化内容弹出菜单
					initOptionsMenu();
				}
				
				if (mEnableFloatingMenu 
						|| mEnableOptionMenu) {
					// 初始化内容弹出对话框
					initDialog();
				}
			}
		} else { // 选择模式
		    initActionButton();
		}
	}
	
	/**
	 * 初始化内容列表头
	 */
	private void initListHeader() {
		// 库存列表头布局，这里采用软件绑定布局方法
	    ViewGroup parent = (ViewGroup) 
	    			mRootLayout.findViewById(R.id.content_header_layout);
		mListHeader = LayoutInflater.from(getActivity())
                	.inflate(mListImplement.getListHeaderLayoutId(), parent, false);
		
		// 获取列表使用的相关资源
		mListHeaderNames = getResources()
						.getStringArray(mListImplement.getListHeaderNames());
		TypedArray typedArray = getResources()
						.obtainTypedArray(mListImplement.getListHeaderIds());

		if (mListHeaderNames != null) {
			mListHeaderItemIds = new int[typedArray.length()];
			for (int i = 0; i < mListHeaderItemIds.length; i++) {
				mListHeaderItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListHeaderNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) 
							mListHeader.findViewById(mListHeaderItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.table_title_textsize));
					tv.setText(mListHeaderNames[i]);
					//tv.setGravity(Gravity.CENTER);
					//tv.setPadding(0, 0, 0, 0);
					//setHeaderTextColor(tv);
				}
			}
		}
		
		typedArray.recycle();
		parent.addView(mListHeader);
	}
	
	protected void setHeaderTextColor(TextView tv) {
		
	}
	
	/**
	 * 为内容列表设置适配器   
	 */
	private void setListAdapter() {
		// 设置内容列表背景色
		View contentLayout = mRootLayout.findViewById(R.id.content_list_layout);
		
		if (mCurrentMode != OperationMode.NORMAL) {
			contentLayout.setBackgroundResource(R.drawable.corners_white_bg);
		}
		
		// 获取内容列表视图控件
		mListView = (ListView) mRootLayout.findViewById(R.id.content_listview);
		if (!mIsBlackBackGroud) {
			contentLayout.setBackgroundColor(Color.WHITE);
			LayoutParams params = (LayoutParams) contentLayout.getLayoutParams();
			params.setMargins(0, 0, 0, 0);
			contentLayout.setLayoutParams(params);
			mListView.setDivider(getActivity().getResources().getDrawable(R.color.divider));
			mListView.setDividerHeight(1);
		}
		
		// 初始化适配器并与内容列表视图控件绑定
		mListAdapter = new DataTreeListAdapter<T>(getActivity(), mListAdapterImplement, mIsBlackBackGroud,
				SHCommonListInterface.class.isInstance(mListImplement) ? ((SHCommonListInterface<T>) mListImplement).getListItemDoScrollIds() : mListImplement.getListHeaderIds());
		mListAdapter.setOngetBDLASlidePaneListener(this);
		final ServerDragFrameLayout serverDragLayout = (ServerDragFrameLayout)mRootLayout.findViewById(R.id.server_drag_layout);
		mListAdapter.setOnDragStatusListener(new BaseDragListAdapter.OnDragStatusListener() {

			@Override
			public boolean getDragStatus() {
				return serverDragLayout.getDragStatus();
			}

			@Override
			public void setDragStatus(boolean newStatus) {
				serverDragLayout.setDragStatus(newStatus);
			}});
		serverDragLayout.addAdapter(mListAdapter);		
		mListAdapter.addCHScrollView(null, mListHeader);
		mListView.setAdapter(mListAdapter);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.v("ccc","onHiddenChanged "+hidden);		
		if (!hidden) {
			if (mListAdapter != null) {
				mListAdapter.setSPListener();
			}
		}
		super.onHiddenChanged(hidden);
	}

	/**
	 * 展开到指定列表项
	 * @param t
	 */
	private void locationSpecifidItem(T t) {
		// 获取显示列表
		List<List<T>> levelList = mListAdapter.getLevelList();
		List<T> lineList = new ArrayList<T>();
		
		T temp = t;
		// 在lineList中逆序保存
		lineList.add(temp);
		for (int i = t.getLevel() - 1; i >= 0; i--) {
			List<T> list = levelList.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).getId() == temp.getParents_id()) {
					temp = list.get(j);
					lineList.add(temp);
					break;
				}
			}
		}
		
		// 递归展开第n项
		int position = -1;
		for (int i = 0; i <= t.getLevel(); i++) {
			List<T> showList = mListAdapter.getShowList();
			T guard = lineList.get(lineList.size()-i-1);
			for (int j = position + 1; j < showList.size(); j++) {
				position++;
				if (guard.getId() == showList.get(j).getId()) {
					break;
				}
			}
			
			// 最后一个不展开
			if (i != t.getLevel()) {
				mListAdapter.updateListView(position);
			}
		}
		
		commonListClick(position);
	}
	
	protected void commonListClick(int position) {
		mCurrentItem = mListAdapter.getItem(position);
		mListAdapter.setSelected(position, true);
	}
	
    /**
	 * 单击处理
	 * @param position
	 */
	public void handleClickWithExpand(int position, View view) {
		if (mActionMode == null) {
			commonListClick(position);
		}
		mListAdapter.updateListView(position);
	}
	
	/**
	 * 单击处理
	 * @param position
	 */
	public void handleClickWithTextView(int position, View view) {
		if (mActionMode == null) {
			commonListClick(position);
		}
		mListAdapter.updateListView(position);
	}
	
	/**
	 * 单击处理
	 * @param position
	 */
	public void handleClickWithShowOptionMenu(int position, View view) {
		commonListClick(position);
		if (mOptionsMenu != null) {
			mOptionsMenu.showAsDropDown(view, 0, (-view
					.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
		}
	}
	
	protected void setListItemTextContent(ViewHolder viewHolder, Map<String, String> treeNodeMap, int i, int position) {
		if ((treeNodeMap.get(mDisplayFields[i]) != null) && !(treeNodeMap.get(mDisplayFields[i])).equals("0")) {
			viewHolder.tvs[i].setText(treeNodeMap.get(mDisplayFields[i]));
			viewHolder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
			if (!mIsBlackBackGroud) {
				viewHolder.tvs[i].setTextColor(Color.BLACK);
			}
		} else {
			viewHolder.tvs[i].setText("");
		}
	}
	
	/**
	 * 长按处理
	 * @param view
	 * @param position
	 */
	protected void handleLongClick(ViewHolder viewHolder, final int position) {
		// 注册长按监听
        viewHolder.tvs[position].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
            	if (mHasEditPermission) {
            		if (mActionMode == null && mEnableNormalMultSelect) {
                    	// 长按进入ActionMode，此时ActionMode应该是NULL
            			if (mSimpleMultiSelectInterface != null) {
                			mSimpleMultiSelectInterface.setEnableMultiSelectMode(true);
            			}
                        mActionMode = getActivity().startActionMode(mCallback);
                        mListAdapter.clearSelectionAll();
                		mListAdapter.setEnableMultiSelectMode(true);
                        updateActionModeTitle(mActionMode, getActivity(),
                                				mListAdapter.getSelected().size());
            		}
            	} else {
            		mCurrentItem = mListAdapter.getItem(position);
            		mListAdapter.setSelected(position, true);
            	}
                return true;
            }
        });
	}
	
	/**
	 * 实现列表适配器接口
	 */
	private TreeListAdapterInterface mListAdapterImplement 
										= new TreeListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return mListImplement.getListLayoutId();
		}

		@Override
		public void initListViewItem(ViewHolder viewHolder,
									 int position) {
			// 获取position的数据项，并转化为map
			T treeNode = mListAdapter.getItem(position);
			// 建立域名称和域值的映射
			Map<String, String> treeNodeMap = MiscUtils.beanToMap(treeNode);
			
			displayFieldRemap(treeNodeMap, treeNode, position);
			
			for (int i = 0; i < mDisplayFields.length; i++) {
				setListItemTextContent(viewHolder, treeNodeMap, i, position);
			}
		}

		@Override
		public void regesterListeners( final ViewHolder viewHolder,
									   final int position) {
			for (int i = 0; i < viewHolder.ivs.length; i++) {
				viewHolder.ivs[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						handleClickWithExpand(position, view);
					}
				});
			}
			
			viewHolder.tvs[0].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					handleClickWithTextView(position, view);
				}
			});
			
			for (int i = 1; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						handleClickWithShowOptionMenu(position, view);
					}
				});
				
				// 正常模式下，注册长按监听器
				if (OperationMode.NORMAL == mCurrentMode) {
					handleLongClick(viewHolder, i);
				}
			}
			
			doExtraRegListener(viewHolder, position);
		}
		

		@Override
		public void initLayout( View convertView,
								ViewHolder holder) {
			// 分配引用数组内存
			holder.ivs = new ImageView[1];
			for (int i = 0; i < holder.ivs.length; i++) {
				holder.ivs[i] = (ImageView) 
						convertView.findViewById(mListItemIds[i]);
			}
			
			// 为引用数组成员分配视图对象内存
			holder.tvs = new TextView[mListItemIds.length-holder.ivs.length];
			for (int i = 0; i < holder.tvs.length; i++) {
				holder.tvs[i] = (TextView) 
						convertView.findViewById(mListItemIds[i + holder.ivs.length]);
			}
			doExtraInitLayout(convertView, holder);
		}

		@Override
		public void calculateContentItemCount() {
			
		}
	};
	
	/**
     * 用于列表的多选 或批量操作
     */
    protected ActionMode.Callback mCallback = new ActionMode.Callback() {
    	// 进入批量操作前的准备工作
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        // 退出批量操作前的清理工作
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        	if (mSimpleMultiSelectInterface != null) {
        		mSimpleMultiSelectInterface.setEnableMultiSelectMode(false);
        	}
    		mListAdapter.setEnableMultiSelectMode(false);
    		mListAdapter.clearSelectionAll();
            mListAdapter.setSelected(-1, false);
            mActionMode = null;
            mode = null;
        }

        // 进入批量操作初始化工作
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            if (mActionBarInterface != null) {
            	LogUtil.i("wzw mActionBarInterface.getActionBarMenu()" + mActionBarInterface.getActionBarMenu());
            	inflater.inflate(mActionBarInterface.getActionBarMenu(), menu);
            } else {
            	inflater.inflate(R.menu.operation_menu, menu);
            }
            return true;
        }

        // 批量操作处理
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
	            case R.id.action_delete:	// 删除操作
	            	if (mListAdapter.getSelected().isEmpty()) {
	            		int id = mSimpleMultiSelectInterface == null ? R.string.pls_select_task : mSimpleMultiSelectInterface.getNoSelectDeleteToastStringId();
	            		Toast.makeText(getActivity().getBaseContext(), id, Toast.LENGTH_SHORT).show();
	            		return false;
	            	}
	                commonConfirmDelete();
	                break;
	                
	            case R.id.action_select_all:	// 权限操作
	                mListAdapter.setSelectedAll();
	                updateActionModeTitle(  mActionMode, 
    						getActivity(), 
    						mListAdapter.getDataList().size());
	                break;
            }
            if (mActionBarInterface != null) {
            	mActionBarInterface.onActionItemClicked(item);
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
    protected void updateActionModeTitle(  ActionMode mode, 
    									Context context,
    									int selectedNum) {
        if (mode != null) {
            mode.setTitle(context.getString(R.string.multi_select_title, selectedNum));
        }
    }
	
	
	protected void doExtraInitLayout(View convertView, ViewHolder viewHolder) {
		
	}
	
	protected void doExtraRegListener(ViewHolder viewHolder, int position) {
		
	}

	/**
	 * 显示域重映射
	 * @param displayFieldMap
	 * @param t
	 * @param position
	 */
	public void displayFieldRemap(Map<String, String> displayFieldMap,
			T t, int position) {
		// 重映射状态
		for (int i = 0; i < mDisplayFields.length; i++) {
			if (mDisplayFieldSwitchMap != null 
					&& mDisplayFieldSwitchMap.containsKey(mDisplayFields[i])) {
				String value = displayFieldMap.get(mDisplayFields[i]);
				String result = mDisplayFieldSwitchMap.get(mDisplayFields[i]).get(value);
				displayFieldMap.put(mDisplayFields[i], result);
			}
		}
	}
	
	/**
	 * 初始化浮动菜单，可定制重写该方法
	 */
	protected void initFloatingMenu() {
		mMenuImages = new int[] { R.drawable.icn_add };
		mMenuTips = new String[] { 
	    		getActivity().getResources().getString(R.string.add) 
	    };
		// 提取资源信息
		if (mFloatingMenuImplement != null) {
			setFloatingMenuImages(mFloatingMenuImplement.getFloatingMenuImages());
			setFloatingMenuTips(mFloatingMenuImplement.getFloatingMenuTips());
		}
        mFloatingMenu = (FloatingMenuView) 
        		mRootLayout.findViewById(R.id.floating_menu);
        
        // 初始化浮动菜单项
        for (int i = 0; i < mMenuTips.length; i++) {
            mFloatingMenu.addPopItem(mMenuTips[i], mMenuImages[i]);
        }
        
        // 为菜单项添加监听
        if (mFloatingMenuImplement != null) {
        	setItemClickListener(mFloatingMenuImplement.getFloatingMenuListener());
        } else {
        	setItemClickListener(mItemClickListener);
        }
	}
	
	/**
	 * 设置浮动菜单图片
	 * @param menuImages
	 */
	private void setFloatingMenuImages(int[] menuImages) {
		if (menuImages != null) {
			mMenuImages = menuImages;
		}
	}
	
	/**
	 * 设置浮动菜单提示
	 * @param menuTips
	 */
	private void setFloatingMenuTips(String[] menuTips) {
		if (menuTips != null) {
			mMenuTips = menuTips;
		}
	}
	
	/**
	 * 浮动菜单监听器
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		
		 @Override
         public void onItemClick(AdapterView<?> parent,
         						View view,
         						int position,
         						long id) {
             switch (position) {
                 case 0:
                 	 mIsAddOperation = mIsFloatMenuAdd = true;
                 	 mIsInsertOperation = false;
                     mDialog.show(null);
                     mFloatingMenu.dismiss();
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
	private void setItemClickListener(OnItemClickListener listener) {
		if (listener != null) {
			mItemClickListener = listener;
		}
		
		mFloatingMenu.setPopOnItemClickListener(mItemClickListener);
	}
	
	/**
	 * 初始化内容弹出菜单，可定制重写该方法
	 */
	protected void initOptionsMenu() {
		mSubMenuNames = getResources()
				.getStringArray(R.array.directory_list_options_menu);

		T listItem = null;
		try {
			listItem = mListItemClass.newInstance();
		} catch (java.lang.InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		
		// 查看，修改
		if (mOptionMenuImplement != null
				&& mOptionMenuImplement.getOptionMenuNames() != 0) {
			mSubMenuNames = getResources()
					.getStringArray(mOptionMenuImplement.getOptionMenuNames());
		}
        mOptionsMenu = new OptionsMenuView(getActivity(), mSubMenuNames);
        
        if (mOptionMenuImplement != null) {
        	setOptionSubMenutListener(mOptionMenuImplement.getOptionMenuClickListener());
        } else {
        	setOptionSubMenutListener(mSubMenuListener);
        	if (!ExpandableSort.class.isInstance(listItem)) {
        		mOptionsMenu.setVisibileMenu(1, false);
    		} 
        }
	}
	
	protected void optionMenuAddFunction() {
		mDialog.show(null);
	}
	
	/**
	 * 选项菜单监听器
	 */
	private SubMenuListener mSubMenuListener = new SubMenuListener() {
		
		@Override
		public void onSubMenuClick(View view) {
			mOptionsMenu.dismiss();
			switch ((Integer) view.getTag()) {
				case 0:		// 添加
					mIsAddOperation = true;
					mIsInsertOperation = false;
					mIsFloatMenuAdd = false;
					optionMenuAddFunction();
					break;
				case 1:		// 修改
					mIsAddOperation = false;
					mIsInsertOperation = false;
					showUpdateDialog(true);
					break;
				case 2:
					if (!mIsDeleteParentNode && mCurrentItem.isHas_child()) {
						sendMessage(SHOW_TOAST, 
								getActivity().getResources().getString(R.string.EPSMaintain_no_delete));
					} else {
						commonConfirmDelete();
					}
					break;
			}
		}
	};
	
	/**
	 * 设置选项菜单监听器
	 * @param listener
	 */
	protected void setOptionSubMenutListener(SubMenuListener listener) {
		if (listener != null) {
			mSubMenuListener = listener;
		}
		
		mOptionsMenu.setSubMenuListener(mSubMenuListener);
	}
	
	/**
	 * 删除确认提示
	 */
	protected void commonConfirmDelete() {
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
		                
		                deleteCurrentAndChildItem();
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
	
	protected void deleteCurrentAndChildItem() {
		mRemoveList.addAll(mListAdapter.deleteTreeNode());
        
        // 开始循环删除，普通模式和批量删除一样处理
        for (int i = 0; i < mRemoveList.size(); i++) {
        	T listItem = mRemoveList.get(i);
        	mServiceImplement.deleteItem(listItem);
        }
	}
	
	/**
	 * 修改一条
	 */
	protected void showUpdateDialog(boolean isEdit) {
		// 设置是否可以修改
		mDialog.switchModifyDialog(isEdit);
		
		Map<String, String> listItemMap 
						= MiscUtils.beanToMap(mCurrentItem);
		
		// 构建默认值数组
		String[] defaultValues = new String[mUpdateFields.length];
		for (int i = 0; i < mUpdateFields.length; i++) {
			if (mUpdateFieldSwitchMap != null &&
					mUpdateFieldSwitchMap.containsKey(mUpdateFields[i])) {
				defaultValues[i] = mUpdateFieldSwitchMap.get(mUpdateFields[i])
										.get(listItemMap.get(mUpdateFields[i]));
			} else {
				defaultValues[i] = listItemMap.get(mUpdateFields[i]);
			}


			if (("0").equals(defaultValues[i])) {
				defaultValues[i] = "";
			}
		}
		
		doExtraSetDefaultDataForDialog(defaultValues);
		
		// 显示内容对话框
		mDialog.show(defaultValues);
	}
	
	protected void doExtraSetDefaultDataForDialog(String[] defaultValues) {
		
	}
	
	protected void doExtraInitDialog() {
		
	}
	
	/**
	 * 如需要，设置该接口
	 * @param baseWidgetInterface
	 */
	protected void setBaseWidgetInterface(BaseWidgetInterface baseWidgetInterface) {
		mBaseWidgetInterface = baseWidgetInterface;
	}
	
	/** 
	 * 初始化内容弹出对话框
	 */
	private void initDialog() {
		// 对话框标题
		String title = getResources().getString(mDialogImplement.getDialogTitleId());
		mDialog = new BaseDialog(getActivity(), title, mBaseWidgetInterface == null ? new BaseWidgetInterface() {
			
			@Override
			public Integer[] getImportantColumns() {
				return mDialogImplement.getImportantColumns(new HashMap<String, String>());
			}
			
			@Override
			public TwoNumber<View, android.widget.LinearLayout.LayoutParams> createExtraLayout() {
				return null;
			}
		}: mBaseWidgetInterface);

		// 对话框显示内容初始化
		if (DialogAdapterInterface.class.isInstance(mDialogImplement)) {
			mDialog.init(mDialogImplement.getDialogLableNames(), 
									((DialogAdapterInterface) mDialogImplement).getDialogStyles(), 
									((DialogAdapterInterface) mDialogImplement).getSupplyData());
			((DialogAdapterInterface) mDialogImplement).additionalInit(mDialog);
		} else {
			mDialog.init(mDialogImplement.getDialogLableNames(), null, null);
		}
		
        Button saveImageView = (Button) mDialog.getPopupView()
                							.findViewById(R.id.save_Button);
        saveImageView.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View view) {
				// 保存界面输入的数据
				mSaveData = mDialog.SaveData();
				Integer[] columns = mDialogImplement.getImportantColumns(mSaveData);
				
				if (columns != null) {
					for (Integer column : columns) {
						if (mSaveData.get(mDialogLableNames[column]).equals("")) {
							sendMessage(SHOW_TOAST, 
									getResources().getString(R.string.pls_input_all_info));
								return;
						}
					}
				}
				
				if (mIsAddOperation) {	// 添加
					// 获取界面保存的数据
					T addItem = addFieldCopy();
					if (!mIsFloatMenuAdd) {
						addItem.setParentsId(mCurrentItem.getId());
						if (ExpandableSort.class.isInstance(mCurrentItem)) {
							int i = 0;
							for (i = 0; i < mListAdapter.getShowList().size(); i++) {
								if (mListAdapter.getShowList().get(i).getId() == mCurrentItem.getId()) {
									break;
								}
							}
							if (mIsInsertOperation) {
								addItem.setParentsId(mCurrentItem.getParents_id());
								((ExpandableSort) addItem).setSort(TreePresenter.getInsertSortNum((DataTreeListAdapter<ExpandableSort>)mListAdapter, i));
							} else {
								((ExpandableSort) addItem).setSort(TreePresenter.getAddSortNum((DataTreeListAdapter<ExpandableSort>)mListAdapter, i));
							}
						}
					}
					mServiceImplement.addItem(addItem);
				} else {	// 修改
					updateFieldCopy();
					mServiceImplement.updateItem(mCurrentUpdateItem);
				}
				mDialog.dismiss();
			}        	
        });
        
        doExtraInitDialog();
	}
	
	/**
	 * 内容添加拷贝
	 * @return
	 */
	@SuppressLint("NewApi") 
	private T addFieldCopy() {
		// 内容列表项添加域拷贝
		T listItem = null;
		try {
			listItem = mListItemClass.newInstance();
		} catch (java.lang.InstantiationException 
						| IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Field[] fs = mListItemClass.getDeclaredFields();
		fieldCopy(fs, listItem, mFieldLableMap);
		return listItem;
	}
	
	/**
	 * 更新域拷贝
	 * @param saveData
	 */
	private void updateFieldCopy() {
		// 内容列表项更新域拷贝
		mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
		Field[] fs = mListItemClass.getDeclaredFields();
		fieldCopy(fs, mCurrentUpdateItem, mFieldLableMap);
	}
	
	/**
	 * 将UI添加或修改域拷贝到对应的对象中
	 * @param fs
	 * @param target
	 * @param map
	 * @param saveData
	 */
	private boolean fieldCopy(  Field[] fs, 
								T target, 
								Map<String, String> map) {
		if (mSaveData == null) {
			return false;
		}
		
		// 遍历类域
		for (int i = 0; i < fs.length; i++) {
			Field field = fs[i];
			field.setAccessible(true);
			// 如果映射表中包含了该域的名称
			if (map.containsKey(field.getName())) {
				Class<?> type = field.getType();
				String fieldName = field.getName();
				String value = mSaveData.get(map.get(fieldName));
				if (mUpdateFieldSwitchMap != null 
						&& mUpdateFieldSwitchMap.containsKey(fieldName)) {
					// 如果需要域转换，但用户没有输入有效数据，直接跳过
					if (!value.equals("")) {
						value = mUpdateFieldSwitchMap.get(fieldName).get(value);
					}
				}
				
				try {
					if (type.equals(String.class)) {
						field.set(target, value);
					} else if (type.equals(Date.class)) {
						if (!value.equals("")) {
							field.set(target, DateUtils.stringToDate(DateUtils.FORMAT_SHORT, value));
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
	
	/**
	 * 确认/取消 按钮
	 */
	private void initActionButton() {
	    View buttonLayout = (View) mRootLayout.findViewById(R.id.button_bar);
	    buttonLayout.setVisibility(View.VISIBLE);
	    
        Button ok = (Button) mRootLayout.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
            	Serializable result = null;
                if (mCurrentMode == OperationMode.SINGLE_SELECT) {
                	result = mCurrentItem;
                }
                if (result == null) {
                	Toast.makeText(getActivity(), R.string.pls_select, Toast.LENGTH_SHORT).show();
                	return;
                }
                ((ListSelectActivity) getActivity()).setSeletctedData(result);
            }
        });

        Button cancel = (Button) mRootLayout.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
                ((ListSelectActivity) getActivity()).setSeletctedData(null);
            }
        });
    }
	
	/**
	 * 初始化进度对话框
	 */
	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(getActivity());
		
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
	}
	
	// 加载数据
	protected void loadData() {
		// 延时显示进度对话框
		mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS_DIALOG, 
										 DELAYTIME_FOR_SHOW);
		// 加载列表数据
		mServiceImplement.getListData();
	}
}
