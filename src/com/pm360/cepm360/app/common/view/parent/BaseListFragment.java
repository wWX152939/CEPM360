package com.pm360.cepm360.app.common.view.parent;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.BaseDragListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DateListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SelectInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.ServerDragFrameLayout;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.common.util.TwoNumber;

public class BaseListFragment<T extends Serializable> extends CommonFragment
					implements SelectInterface {
	
	
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
	protected LinearLayout mContentLayout;
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
	protected ProgressDialog mProgressDialog;
	
	// 列表头的复选框
	private CheckBox mHeaderCheckBox;
	
	// 适配器adapter定义
	protected DataListAdapter<T> mListAdapter;
	
	private View mButtonBar;
	
	/**------------------ 保存资源数据 -------------*/
	// 保存对话框标签名数组
	protected String[] mDialogLableNames;
	
	// 通过xml文件提供内容表头控件ID数组
	protected int[] mListHeaderItemIds;
	protected int[] mListItemIds;
	// 通过xml文件提供内容表头字符串数组
	private String[] mListItemNames;
	
	/**------------------ 暂存数据 -----------------*/
	// 当前更新列表项
	protected T mCurrentUpdateItem;
	// 当前数据
	protected T mCurrentItem;
	// 指定列表项
	protected T mSpecifiedItem;
	
	// 界面数据保存
	protected Map<String, String> mSaveData;
	
	// 保存类型对象
	private Class<T> mListItemClass;
	
	private Map<String, Map<String, String>> mDisplayFieldSwitchMap;
	private Map<String, Map<String, String>> mUpdateFieldSwitchMap;
	
	private List<T> mLoadedDataList;
	
	/**------------------ 继承者提供数据 ----------*/
	// 内容显示的域
	protected String[] mDisplayFields;
	protected String[] mUpdateFields;
	
	private int[] mMenuImages;
	private String[] mMenuTips;
	
	// 选择菜单
	private String[] mSubMenuNames;
	
	
	/**------------------ 删除操作数据 ------------*/
	protected List<T> mRemoveList = new ArrayList<T>();
	protected int mRemoveCount;
	protected int mRemoveFailedCount;
	
	/** ----------------- 映射表定义 --------------*/
	// 更新字段映射表
	private Map<String, String> mFieldLableMap;
	
	/** ----------------- 模式操作 --------------- */
		
	// 操作模式定义
	protected enum OperationMode {
		NORMAL,
		SINGLE_SELECT,
		MULTI_SELECT
	}
	
	// 过滤列表项
	protected List<T> mFilterList;
	// 多选模式选中的项
	private List<T> mSelectedDataList = new ArrayList<T>();
	// 当前操作模式
	protected OperationMode mCurrentMode;
	
	// 模式定义，普通模式和批量操作模式
	private ActionMode mActionMode;
	
	/** ------------------- 标识 -------------------- */
	// 数据是否加载成功
	protected boolean mDataLoaded;
	
	// 是否禁用列表头
	private boolean mEnableListHeader = true;
	
	// 增加修改操作，是否是增加操作
	protected boolean mIsAddOperation;
	protected boolean mIsFloatMenuAdd;
	protected boolean mEnableInnerButton = true;
	
	// 如需要对dialog做额外的界面操作，设置该接口
	protected BaseWidgetInterface mBaseWidgetInterface;
	
	// 是否启用浮动菜单
	private boolean mEnableFloatingMenu;
	// 使能选择菜单功能
	private boolean mEnableOptionMenu;
	
	// 正常模式下是否启用多选功能
	private boolean mEnableNormalMultSelect;
	
	private boolean mForeceEnableOption;
	private boolean mForeceEnableFloating;
	
	// 列表显示日期格式
	private String mDateFormat;
	
	/** ------------------ 接口 ----------------- */
	protected CommonListInterface<T> mListImplement;
	protected ServiceInterface<T> mServiceImplement;
	protected SimpleDialogInterface mDialogImplement;
	protected OptionMenuInterface mOptionMenuImplement;
	protected FloatingMenuInterface mFloatingMenuImplement;
	
	
	/** --------------------------------- 方法定义 ------------------------ */
	
	/**
	 * 初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 */
	protected void init(Class<T> listItemClass,
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface) {
		mListItemClass = listItemClass;
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
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						FloatingMenuInterface floatingMenuInterface,
						SimpleDialogInterface dialogInterface) {
		init(listItemClass, listInterface, serviceInterface);
		mFloatingMenuImplement = floatingMenuInterface;
		mDialogImplement = dialogInterface;
		enableFloatingMenu();
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
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						OptionMenuInterface optionMenuInterface,
						SimpleDialogInterface dialogInterface) {
		init(listItemClass, listInterface, serviceInterface);
		mOptionMenuImplement = optionMenuInterface;
		mDialogImplement = dialogInterface;
		enableOptionMenu();
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
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						FloatingMenuInterface floatingMenuInterface,
						OptionMenuInterface optionMenuInterface,
						SimpleDialogInterface dialogInterface) {
		init( listItemClass, 
			  listInterface, 
			  serviceInterface, 
			  floatingMenuInterface, 
			  dialogInterface);
		mOptionMenuImplement = optionMenuInterface;
		enableFloatingMenu();
		enableOptionMenu();
	}
	
	/**
	 * 如需要，设置该接口
	 * @param baseWidgetInterface
	 */
	protected void setBaseWidgetInterface(BaseWidgetInterface baseWidgetInterface) {
		mBaseWidgetInterface = baseWidgetInterface;
	}
	
	/**
	 * 设置显示列表
	 * @param list
	 */
	protected void setDataShowList(List<T> list) {
		mListAdapter.setDataList(list);
	}
	
	/**
	 * 设置选项菜单接口，前提是已经调用过init方法设置了Dialog接口
	 * @param optionMenuInterface
	 */
	protected void setOptionMenuInterface(OptionMenuInterface optionMenuInterface) {
		mOptionMenuImplement = optionMenuInterface;
		
		enableOptionMenu();
		initOptionsMenu();
	}
	
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	protected DataManagerInterface getServiceManager() {
		return mListManager;
	}
	
	/**
	 * 禁用列表头
	 */
	protected void disableListHeader() {
		mEnableListHeader = false;
	}
	
	/**
	 * 使能浮动按钮
	 */
	private void enableFloatingMenu() {
		mEnableFloatingMenu = true;
	}
	
	/**
	 * 启用正常模式下多选功能
	 */
	protected void enableNormalMultSelect() {
		mEnableNormalMultSelect = true;
	}
	
	protected void forceEnableOption(boolean enable) {
		mForeceEnableOption = enable;
	}
	
	protected void forceEnableFloating(boolean enable) {
		mForeceEnableFloating = enable;
	}
	
	/**
	 * 启用选择菜单功能
	 */
	private void enableOptionMenu() {
		mEnableOptionMenu = true;
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
					}
					if (mDataLoaded) {
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
					mLoadedDataList = (List<T>) list;
					
					if (OperationMode.MULTI_SELECT == mCurrentMode 
							&& mFilterList != null 
							&& mFilterList.size() > 0) {
							filterSelectedDatas((List<T>) list);
					}
					
					mListAdapter.setDataList((List<T>) list);
					mListAdapter.setShowDataList((List<T>) list);
					
					// 结束加载进度对话框
					mDataLoaded = true;
					sendMessage(DISMISS_PROGRESS_DIALOG);
					
					if (mSpecifiedItem != null) {
						locationSpecifiedItem(mSpecifiedItem);
					}
					
					if (mHeaderCheckBox != null && mHeaderCheckBox.isChecked()) {
						mHeaderCheckBox.setChecked(false);
					}
					break;
					
				case AnalysisManager.SUCCESS_DB_ADD:
					if (list != null && !list.isEmpty()) {
						mListAdapter.addDataToList((T) list.get(0));
					}
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
			
			doExtraGetServerData(status, list);
			
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
					&& mRemoveCount == 0) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
		}
		
	};
	
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		
	}
	
	/**
	 * 定位到指定列表项
	 * @param t
	 */
	protected void locationSpecifiedItem(T t) {
		List<T> showList = mListAdapter.getDataShowList();
		int position = 0;
		for (; position < showList.size(); position++) {
			if (mListImplement.getListItemId(t) 
					== mListImplement.getListItemId(showList.get(position))) {
				break;
			}
		}
		if (position == showList.size()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.select_item_no_found));
		} else {
			mCurrentItem = mListAdapter.getItem(position);
			mListAdapter.setSelected(position, true);
		}
	}
	
	/**
	 * 全部删除成功
	 */
	protected void allDeleteSuccessful() {
		mListAdapter.clearSelection();
		mListAdapter.deleteData(mRemoveList);
		// 将当前项置为空
		mCurrentItem = null;
	}
	
	/**
	 * 部分删除成功
	 * @param status
	 */
	protected void partDeleteSucessful(ResultStatus status) {
		status.setMessage(getResources()
				.getString(R.string.delete_part_failed));
		mListAdapter.clearAll();
		mServiceImplement.getListData();
	}
	
	/**
	 * 清理删除操作使用的数据
	 */
	protected void deleteDataClear() {
		mRemoveList.clear();
		mRemoveCount = 0;
		mRemoveFailedCount = 0;
	}

	/**
	 * 多选模式中，过滤掉已选择的内容
	 * @param list
	 */
	protected void filterSelectedDatas(List<T> list) {
		Iterator<T> iterator = list.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			for (T filter : mFilterList) {
				if (mListImplement.getListItemId(filter) 
								== mListImplement.getListItemId(t)) {
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
			
			// 初始化内容视图
			initContentView();
			
			// 初始化进度对话框
			createProgressDialog();
			
			// 加载数据
			loadData();
			
			if (mHasEditPermission) {
				// 额外操作
				initExtraEvent();
			}
		} else {
			// 没有任何权限，显示无权限信息
			showNoViewPermission(inflater, container);
		}
		
		return mRootLayout;
	}
	
	/**
	 * 重载该方法实现额外操作
	 */
	protected void initExtraEvent() {
		
	}
	
	/**
	 * 显示无查看权限信息
	 * @param inflater
	 * @param container
	 */
	protected void showNoViewPermission(  LayoutInflater inflater, 
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
	@SuppressWarnings("unchecked")
	private void initOperationMode() {
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
						mFilterList = (List<T>) 
								bundle.getSerializable(ListSelectActivity.FILTER_DATA_KEY);
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
	@SuppressWarnings("rawtypes")
	private void initFeildsAndMap() {
		// 初始化显示域
		mDisplayFields = mListImplement.getDisplayFeilds();

		// 初始化域切换映射
		mDisplayFieldSwitchMap = mListImplement.getDisplayFieldsSwitchMap();
		
		// 初始化列表日期格式
		if (DateListInterface.class.isInstance(mListImplement)) {
			mDateFormat = ((DateListInterface) mListImplement).getDateFormat();
		}
		
		// 初始化更新域
		if (mDialogImplement != null) {
			mUpdateFields = mDialogImplement.getUpdateFeilds();
			
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
		
		// 设置内容列表背景色
		mContentLayout = (LinearLayout) 
				mRootLayout.findViewById(R.id.content_list_layout);
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
				
				if (mEnableOptionMenu ) {
					// 初始化内容弹出菜单
					initOptionsMenu();
				}
				
				if (mEnableFloatingMenu 
						|| mEnableOptionMenu) {
					// 初始化内容弹出对话框
					initDialog();
				}
			} else {
				if (mForeceEnableFloating) {
					
					// 初始化浮动菜单
					initFloatingMenu();
				}
				
				if (mForeceEnableOption ) {
					
					// 初始化内容弹出菜单
					initOptionsMenu();
				}
				
				if (mForeceEnableFloating 
						|| mForeceEnableOption) {
					
					// 初始化内容弹出对话框
					initDialog();
				}
			}
		} else if (mEnableInnerButton) { // 选择模式
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
		
		// 如果禁用列表头，设置为不可见
		if (!mEnableListHeader) {
			parent.setVisibility(View.GONE);
			return;
		}
		
		mListHeader = LayoutInflater.from(getActivity())
                	.inflate(mListImplement.getListHeaderLayoutId(), parent, false);
		
		if (OperationMode.MULTI_SELECT == mCurrentMode) {
		    mHeaderCheckBox = (CheckBox) mListHeader.findViewById(R.id.mult_select);
		    mHeaderCheckBox.setVisibility(View.VISIBLE);
		    mHeaderCheckBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (((CheckBox) view).isChecked()) {
						mSelectedDataList.removeAll(mListAdapter.getSelectedDatas());
						mListAdapter.setSelectedAll();
						mSelectedDataList.addAll(mListAdapter.getSelectedDatas());
					} else {
						mSelectedDataList.removeAll(mListAdapter.getSelectedDatas());
						mListAdapter.clearSelection();
						mListAdapter.notifyDataSetChanged();
					}
				}
			});
			//mListHeader.findViewById(R.id.select_right_view).setVisibility(View.VISIBLE);
		}
		
		// 获取列表使用的相关资源
		mListItemNames = getResources()
						.getStringArray(mListImplement.getListHeaderNames());
		TypedArray typedArray = getResources()
						.obtainTypedArray(mListImplement.getListHeaderIds());

		if (mListItemNames != null) {
			mListHeaderItemIds = new int[typedArray.length()];
			for (int i = 0; i < mListHeaderItemIds.length; i++) {
				mListHeaderItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListItemNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) 
							mListHeader.findViewById(mListHeaderItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.table_title_textsize));
					tv.setText(mListItemNames[i]);
				}
			}
		}
		
		typedArray.recycle();
		parent.addView(mListHeader);
	}
	
	/**
	 * 为内容列表设置适配器   
	 */
	private void setListAdapter() {
		
		// 获取内容列表视图控件
		mListView = (ListView) mRootLayout.findViewById(R.id.content_listview);
		
		// 初始化适配器并与内容列表视图控件绑定
		mListAdapter = new DataListAdapter<T>(getActivity(), mListAdapterImplement, mListImplement.getListHeaderIds());
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
		mListView.setAdapter(mListAdapter);
	}
	
	/**
	 * 实现列表适配器接口
	 */
	private ListAdapterInterface<T> mListAdapterImplement 
										= new ListAdapterInterface<T>() {
		@Override
		public int getLayoutId() {
			return mListImplement.getListLayoutId();
		}

		@Override
		public View getHeaderView() {
			return mListHeader;
		}

		@Override
		public void regesterListeners(final ViewHolder viewHolder, final int position) {

			for (int i = 0; i < mListItemIds.length; i++ ) {
				if (doExtraRegListener(viewHolder, position, i)) {
					
				} else {
					viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View view) {
							// ActionMode批量操作模式
							if (mActionMode != null) {
								mListAdapter.setPickSelected(position);
								updateActionModeTitle(mActionMode, getActivity(),
	                                    			  mListAdapter.getSelectedList().size());
							// 非多选模式，弹出选项菜单，但如果是单选模式也不会弹出
							} else if (OperationMode.MULTI_SELECT != mCurrentMode) {
								mCurrentItem = mListAdapter.getItem(position);
								mListAdapter.setSelected(position, true);
								
								// 切换选项菜单
								switchOptionMenu();
								
								// 如果是选择模式和无编辑权限下，mContentOptionsMenu不被初始化，为空
								if (mOptionsMenu != null) {
									mOptionsMenu.showAsDropDown(view, 0, (-view
											.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
								}
							} else { // 多选择模式
								if (viewHolder.cbs[0].isChecked()) {
									viewHolder.cbs[0].setChecked(false);
									if (mHeaderCheckBox.isChecked()) {
										mHeaderCheckBox.setChecked(false);
									}
									
									T t = mListAdapter.getItem(position);
									mSelectedDataList.remove(t);
								} else {
									viewHolder.cbs[0].setChecked(true);
									mCurrentItem = mListAdapter.getItem(position);
									
									mSelectedDataList.add(mCurrentItem);
								}
								mListAdapter.setPickSelected(position);
							}
						}
					});
					
					// 多选模式，启动多选功能（注册复选框的单击监听器）
					if (OperationMode.MULTI_SELECT == mCurrentMode) {
						if (mListAdapter.isContainPosition(position)) {
							viewHolder.cbs[0].setChecked(true);
						} else {
							viewHolder.cbs[0].setChecked(false);
						}
						
						viewHolder.cbs[0].setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View view) {
								if (((CheckBox) view).isChecked()) {
									mCurrentItem = mListAdapter.getItem(position);
									
									mSelectedDataList.add(mCurrentItem);
								} else {
									if (mHeaderCheckBox.isChecked()) {
										mHeaderCheckBox.setChecked(false);
									}
									
									T t = mListAdapter.getItem(position);
									mSelectedDataList.remove(t);
								}
								mListAdapter.setPickSelected(position);
							}
						});
					}
					
					// 正常模式下，注册长按监听器
					if (OperationMode.NORMAL == mCurrentMode) {
						// 注册长按监听
		                viewHolder.tvs[i].setOnLongClickListener(new View.OnLongClickListener() {
		                    @Override
		                    public boolean onLongClick(View view) {
		                    	if (mHasEditPermission && mEnableNormalMultSelect) {
			                    	// 长按进入ActionMode，此时ActionMode应该是NULL
			                        mActionMode = getActivity().startActionMode(mCallback);
			                        mListAdapter.setPickSelected(position);
			                        updateActionModeTitle(mActionMode, getActivity(),
			                                				mListAdapter.getSelectedList().size());
		                    	} else {
		                    		mCurrentItem = mListAdapter.getItem(position);
		                    		mListAdapter.setSelected(position, true);
		                    	}
		                        return true;
		                    }
		                });
					}	
				}
				
			}
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
										DataListAdapter<T> adapter, int position) {
			// 获取当前位置对象，然后转换为映射
			T listItem = adapter.getItem(position);
			
			if (OperationMode.MULTI_SELECT == mCurrentMode
					&& mSelectedDataList.contains(listItem)) {
				if (!mListAdapter.isContainPosition(position)) {
					holder.cbs[0].setChecked(true);
					mListAdapter.setPickSelected(position);
				}
			}
			
			Map<String, String> displayFieldMap = MiscUtils.beanToMap(listItem, mDateFormat);
			
			displayFieldRemap(displayFieldMap, listItem, position);
			
			for (int i = 0; i < mDisplayFields.length; i++) {
				
				// holder可能不仅仅是tvs，需要额外处理
				if (doExtraInitListViewItem(position, i, holder)) {
					
				} else {
					if ((displayFieldMap.get(mDisplayFields[i]) != null) && !(displayFieldMap.get(mDisplayFields[i])).equals("0")) {
						holder.tvs[i].setText(displayFieldMap.get(mDisplayFields[i]));
						if (OperationMode.NORMAL != mCurrentMode) {
							holder.tvs[i].setTextColor(Color.BLACK);
						}
						holder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
					} else {
						holder.tvs[i].setText("");
					}
				}
			}
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			// 分配引用数组内存
			holder.tvs = new TextView[mListItemIds.length];
			holder.cbs = new CheckBox[1];
			
			// 为引用数组成员分配视图对象内存
			for (int i = 0; i < mListItemIds.length; i++) {
				if (doExtraInitLayout(convertView, holder, i)) {
					
				} else {
					holder.tvs[i] = (TextView) convertView
							.findViewById(mListItemIds[i]);
				}
				
			}
			
			if (OperationMode.MULTI_SELECT == mCurrentMode) {
				// 显示复选框和分界线
				holder.cbs[0] = (CheckBox) convertView.findViewById(R.id.mult_select);
				holder.cbs[0].setVisibility(View.VISIBLE);
				//convertView.findViewById(R.id.select_right_view).setVisibility(View.VISIBLE);
			}
			
		}

		@Override
		public List<T> findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(T t1, T t2) {
			return mListImplement.getListItemId(t1) == 
							mListImplement.getListItemId(t2);
		}
	};
	
	/**
	 * 切换选项菜单
	 */
	protected void switchOptionMenu() {
		
	}
	
	protected boolean doExtraInitListViewItem(int line, int position, ViewHolder holder) {
		return false;
	}
	
	protected boolean doExtraInitLayout(View convertView, ViewHolder holder, int position) {
		return false;
	}
	
	protected boolean doExtraRegListener(ViewHolder holder, int position, int i) {
		return false;
	}
	

	/**
	 * 显示域重映射
	 * @param displayFieldMap
	 * @param t
	 * @param position
	 */
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
			T t, int position) {
		// 重映射序号
		displayFieldMap.put(SERIAL_NUMBER, position + 1 + "");
		
		// 重映射状态
		for (int i = 1; i < mDisplayFields.length; i++) {
			if (mDisplayFieldSwitchMap != null
					&& (mDisplayFieldSwitchMap.get(mDisplayFields[i]) != null)) {
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
	protected OnItemClickListener mItemClickListener = new OnItemClickListener() {
		
		 @Override
         public void onItemClick(AdapterView<?> parent,
         						View view,
         						int position,
         						long id) {
             switch (position) {
                 case 0:
                 	 mIsAddOperation = mIsFloatMenuAdd = true;
                 	 if (!doExtraAddFloatingMenuEvent()) {
                 		mDialog.show(null);
                 	 }
                     mFloatingMenu.dismiss();
                     break;
                 default:
                     break;
             }
         }
	};
	
	/**
	 * 重载该方法实现浮动按钮的添加操作
	 * @return
	 */
	protected boolean doExtraAddFloatingMenuEvent() {
		return false;
	}
	
	/**
	 * 设置浮动菜单的监听器
	 * @param listener
	 */
	protected void setItemClickListener(OnItemClickListener listener) {
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
				.getStringArray(R.array.list_options_menu);
		
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
        }
	}
	
	/**
	 * 选项菜单监听器
	 */
	private SubMenuListener mSubMenuListener = new SubMenuListener() {
		
		@Override
		public void onSubMenuClick(View view) {
			mOptionsMenu.dismiss();
			switch ((Integer) view.getTag()) {
				case 0:		// 修改
					mIsAddOperation = false;
					showUpdateDialog(true);
					break;
				case 1:
					commonConfirmDelete();
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
		                
		                mRemoveList.addAll(mListAdapter.getSelectedDatas());
		                
		                // 开始循环删除材料，普通模式和批量删除模式一样处理
		                for (int i = 0; i < mRemoveList.size(); i++) {
	                    	T listItem = mRemoveList.get(i);
	                    	mServiceImplement.deleteItem(listItem);
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
	
	protected String[] getDefaultDialogValue() {

		Map<String, String> listItemMap 
						= MiscUtils.beanToMap(mCurrentItem);
		
		// 构建默认值数组
		String[] defaultValues = new String[mUpdateFields.length];
		for (int i = 0; i < mUpdateFields.length; i++) {
			if (mUpdateFieldSwitchMap != null &&
					(mUpdateFieldSwitchMap.get(mUpdateFields[i]) != null)) {
				defaultValues[i] = mUpdateFieldSwitchMap.get(mUpdateFields[i])
										.get(listItemMap.get(mUpdateFields[i]));
			} else {
				defaultValues[i] = listItemMap.get(mUpdateFields[i]);
			}
		}
		
		return defaultValues;
	}
	
	/**
	 * 修改一条
	 */
	protected void showUpdateDialog(boolean isEdit) {
		// 设置是否可以修改
		mDialog.switchModifyDialog(isEdit);
		
		// 显示内容对话框
		mDialog.show(getDefaultDialogValue());
	}
	
	/** 
	 * 初始化内容弹出对话框
	 */
	private void initDialog() {
		
		// 对话框标题
		if (mDialogImplement != null) {
			String title = getResources().getString(mDialogImplement.getDialogTitleId());
			mDialog = new BaseDialog(getActivity(), title, mBaseWidgetInterface == null ? new BaseWidgetInterface() {
				
				@Override
				public Integer[] getImportantColumns() {
					return mDialogImplement.getImportantColumns(new HashMap<String, String>());
				}
				
				@Override
				public TwoNumber<View, LayoutParams> createExtraLayout() {
					return null;
				}
			} : mBaseWidgetInterface);
	
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
	
				@Override
				public void onClick(View view) {
					if (dialogSaveButtonEvent()) {
						mDialog.dismiss();
						
						if (mFloatingMenu != null) {
							mFloatingMenu.dismiss();
						}

					}
				}        	
	        });
		}
	}
	
	/**
	 * 重载该方法实现dialog保存处理
	 * @param saveData
	 * @return
	 */
	protected boolean dialogSaveButtonEvent() {
		// 保存界面输入的数据
		mSaveData = mDialog.SaveData();
		Integer[] columns = mDialogImplement.getImportantColumns(mSaveData);
		
		if (columns != null) {
			for (Integer column : columns) {
				if (column == -1) {
					return false;
				}
				if (mSaveData.get(mDialogLableNames[column]).equals("")) {
					sendMessage(SHOW_TOAST, 
							getResources().getString(R.string.pls_input_all_info));
					return false;
				}
			}
		}
		
		if (mIsAddOperation) {	// 添加
			// 获取界面保存的数据			
			mServiceImplement.addItem(addFieldCopy());
		} else {	// 修改
			updateFieldCopy();
			mServiceImplement.updateItem(mCurrentUpdateItem);
		}
		return true;
	}
	
	/**
	 * 内容添加拷贝
	 * @return
	 */
	@SuppressLint("NewApi") 
	protected T addFieldCopy() {
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
	protected void updateFieldCopy() {
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
						&& (mUpdateFieldSwitchMap.get(fieldName) != null)) {
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
	    mButtonBar = (View) mRootLayout.findViewById(R.id.button_bar);
	    mButtonBar.setVisibility(View.VISIBLE);
	    
        Button ok = (Button) mRootLayout.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
                Serializable result = null;
                List<T> data = null;
                if (mCurrentMode == OperationMode.SINGLE_SELECT) {
                	data = getSelectedDataList();
                    if (data.size() == 1) {
                        result = data.get(BASE_POSITION);
                    }
                } else {
                	data = (List<T>) mSelectedDataList;
                    if (data.size() > 0) {
                        result = (Serializable) data;
                    }
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
	 * 用于返回内容适配器中已经选择的列表项
	 * @return
	 */
	public List<T> getSelectedDataList() {
		return mListAdapter.getSelectedDatas();
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
            mListAdapter.setSelected(-1, false);
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
	                commonConfirmDelete();
	                break;
	                
	            case R.id.action_select_all:	// 权限操作
	                mListAdapter.setSelectedAll();
	                updateActionModeTitle(  mActionMode, 
    						getActivity(), 
    						mListAdapter.getSelectedList().size());
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
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
		if (!hidden) {
			if (mListAdapter != null) {
				mListAdapter.setSPListener();
			}
		}        
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
	private void loadData() {
		// 延时显示进度对话框
//		mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS_DIALOG, 
//										 DELAYTIME_FOR_SHOW);
		// 加载资源类型数据
		mServiceImplement.getListData();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFilterData(List<?> filters) {
		mFilterList = (List<T>) filters;
		
		
		if (mFilterList != null && !mFilterList.isEmpty()) {
			mListAdapter.setDataList(mLoadedDataList);
			
			filterSelectedDatas(mListAdapter.getDataList());
			filterSelectedDatas(mListAdapter.getDataShowList());
			
			mListAdapter.notifyDataSetChanged();
		}
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
