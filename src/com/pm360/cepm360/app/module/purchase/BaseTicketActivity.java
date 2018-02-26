package com.pm360.cepm360.app.module.purchase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.TickListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.EditableTicketListInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleButtonInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleTicketServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleWindowInterface;
import com.pm360.cepm360.app.common.custinterface.SpecialButtonInterface;
import com.pm360.cepm360.app.common.custinterface.SpecialDialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.TicketListInterface;
import com.pm360.cepm360.app.common.custinterface.TicketServiceInterface;
import com.pm360.cepm360.app.common.custinterface.WindowAdapterInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.FlowCell;
import com.pm360.cepm360.entity.FlowDCell;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.MarkFlowCell;
import com.pm360.cepm360.entity.MarkFlowDCell;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.system.RemoteFlowSettingService;


public abstract class BaseTicketActivity<A extends FlowCell, B extends FlowDCell> extends ActionBarActivity {
	/** -- 常量 -- */
	public static final String MSG_ENTER_KEY = "com.pm360.cepm360.action.purchase_plan";
	public static final String ADD_DATA_KEY = "add_data_key";
	public static final String INFO_DATA_KEY = "info_data_key";
	public static final String MODIFY_DATA_KEY = "modify_data_key";
	
	public static final String SERIAL_NUMBER = "serial";
	
	/** -- 视图控件 -- */
	protected BaseDialog mDialog;
	protected BaseWindow mTopWindow;
	protected FlowApprovalDialog mFlowApprovalDialog;

	protected TextView mTotalTextView;
	protected TextView mMarkTextView;
	
	private View mTopView;
	private ViewGroup mViewPlanList;
	private View mHeaderView;
	private OptionsMenuView mOptionMenuView;
	
	/** -- 数据缓存 -- */
	protected B mCurrentItem;
	protected A mMsgData;
	private Message mMessage;

	protected int[] mListHeaderItemIds;
	private String[] mListHeadNames;
	private int[] mListItemIds;
	private ListView mListView;
	protected DataListAdapter<B> mListAdapter;
	
	// 审批流程数据
	protected boolean hasFlowApproval;
	protected Flow_setting mFlowSetting;
	
	// optionMenuView
	private String[] mSubMenuNames;
	
	// 更新字段映射表
	private Map<String, String> mFieldLableMap;
	private Map<String, String> mTopFieldLableMap;
	
	protected String[] mDialogLableNames;
	protected String[] mDisplayFields;
	protected String[] mUpdateFields;
	protected String[] mTopUpdateFields;
	protected String[] mTopWindowLableNames;
	
	private Map<String, Map<String, String>> mDisplayFieldSwitchMap;
	private Map<String, Map<String, String>> mUpdateFieldSwitchMap;
	private Map<String, Map<String, String>> mTopUpdateFieldSwitchMap;

	/** -- 标识 -- */

	// Flow 类型  GLOBAL.FLOW_TYPE
	private String mFlowType;
	
	// 保存类型对象
	private Class<A> mMsgClass;

	// 保存类型对象
	private Class<B> mListItemClass;
	
	// topWindow处按钮响应是否要另启一行
	private boolean mNewLine;

	// optionMenu消失时间间隔
	private long mDismissTime;
	// 数据是否加载成功
	protected boolean mDataLoaded;
	// 设置保存按钮只响应一次
	private boolean requestServerDataFlag = false;
	// 状态标识
	protected final int MODIFY_STATUS = 1;
	protected final int INFO_STATUS = 2;
	protected final int ADD_STATUS = 3;
	
	// 底部按钮标识
	private final int SUBMIT_BUTTON_STYLE = 1;
	private final int APPROVAL_BUTTON_STYLE = 2;
	private final int REJECT_BUTTON_STYLE = 3;

	// 当前状态
	protected int mCurrentStatus;
	protected boolean mEnableOptionMenu;
	protected boolean mEnableDialog;
	
	/**-- 接口 --*/
	protected SimpleTicketServiceInterface<A, B> mTicketServiceInterface;
	protected TicketListInterface<B> mListImplement;
	protected SimpleDialogInterface mDialogImplement;
	protected SimpleWindowInterface mTopWindowImplement;
	protected OptionMenuInterface mOptionMenuImplement;
	protected SimpleButtonInterface mTopButtonInterface;
	
	
	protected abstract SimpleTicketServiceInterface<A, B> getTicketServiceInterface();
	
	protected abstract TicketListInterface<B> getListInterface();
	
	protected abstract SimpleDialogInterface getDialogInterface();
	
	protected abstract SimpleWindowInterface getTopWindowInterface();
	
	protected abstract OptionMenuInterface getOptionMenuInterface();
	
	protected abstract SimpleButtonInterface getTopButtonInterface();
	
	// 如"CG"
	protected abstract String getNumCode();

	protected abstract Class<A> getTopClass();
	
	protected abstract Class<B> getListClass();
	
	protected abstract String getFlowType();
	
	protected abstract String getActivityAction();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createProgressDialog(true, true);
		setClassType();
		initInterface();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.purchase_plan_ticket_activity);
		msgHandlerProgress();
	}
	
	private void setClassType() {
		mMsgClass = getTopClass();
		mListItemClass = getListClass();
		
		mFlowType = getFlowType();
		if (mFlowType == null) {
			mFlowType = "";
		}
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	private A createA() {
		return (A) new FlowCell() {

			@Override
			public int getId() {
				return 0;
			}

			@Override
			public void setId(int id) {
				
			}

			@Override
			public int getStatus() {
				return 0;
			}

			@Override
			public void setStatus(int status) {
				
			}

			@Override
			public int getPlan_person() {
				return 0;
			}

			@Override
			public void setPlan_person(int plan_person) {
				
			}
			
		};
	}
	
	/**
	 * we should invoke msgHandlerProgress first and get hasFlowApproval flag
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void msgHandlerProgress() {
		Intent intent = getIntent();

		String action = intent.getAction();
		if (action != null && action.equals(getActivityAction())) {
			mMessage = (com.pm360.cepm360.entity.Message) intent
					.getSerializableExtra("message");
			((TicketServiceInterface) mTicketServiceInterface).getDataByMessageId(mCGJHManager, mMessage.getMessage_id());
			return;
		}
		if (intent.getSerializableExtra(MODIFY_DATA_KEY) != null) {
			mCurrentStatus = MODIFY_STATUS;
			mMsgData = (A) intent
					.getSerializableExtra(MODIFY_DATA_KEY);
		} else if (intent.getSerializableExtra(INFO_DATA_KEY) != null) {
			mCurrentStatus = INFO_STATUS;
			mMsgData = (A) intent.getSerializableExtra(INFO_DATA_KEY);
		} else if (intent.getSerializableExtra(ADD_DATA_KEY) != null) {
			mMsgData = (A) intent.getSerializableExtra(ADD_DATA_KEY);
			mCurrentStatus = ADD_STATUS;
		} else {
			mMsgData = (A) createA();
			mCurrentStatus = ADD_STATUS;
		}
		
		if (mFlowType.equals("")) {
			hasFlowApproval = false;
			initWindow();
		} else {
			RemoteFlowSettingService.getInstance().getFlowDetail(
					mFlowManager,
					UserCache.getCurrentUser().getTenant_id(),
					mFlowType);
		}
	}
	
	private DataManagerInterface mCGJHManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					mMsgData = (A) list.get(0);

					RemoteFlowSettingService.getInstance().getFlowDetail(
							mFlowManager,
							UserCache.getCurrentUser().getTenant_id(),
							mFlowType);

				}
			}
		}

	};
	
	private DataManagerInterface mFlowManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Toast.makeText(getBaseContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
			}

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

				if (list != null && list.size() != 0) {
					@SuppressWarnings("unchecked")
					List<Flow_setting> flowList = ((List<Flow_setting>) list);
					if (flowList.get(0).getFlow_type()
							.equals(mFlowType)) {
						if (flowList.get(0).getStatus() == Integer
								.parseInt(GLOBAL.FLOW_STATUS[0][0])) {
							hasFlowApproval = true;

							mFlowSetting = flowList.get(0);
							if (mMessage != null) {
								if (mMsgData.getStatus() == Integer
										.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])) {
									mCurrentStatus = MODIFY_STATUS;
								} else {
									mCurrentStatus = INFO_STATUS;
								}
							}

							initWindow();
						} else if (flowList.get(0).getStatus() == Integer
								.parseInt(GLOBAL.FLOW_STATUS[1][0])) {
							hasFlowApproval = false;
							mFlowSetting = flowList.get(0);
							if (mMessage != null) {
								mCurrentStatus = INFO_STATUS;
							}
							initWindow();
						}
					}

				}
			}
		}
	};
	
	private void initInterface() {
		mTicketServiceInterface = getTicketServiceInterface();
		mListImplement = getListInterface();
		mDialogImplement = getDialogInterface();
		mTopWindowImplement = getTopWindowInterface();
		mOptionMenuImplement = getOptionMenuInterface();
		mTopButtonInterface = getTopButtonInterface();
	}
	
	private void initWindow() {
		// 初始化数据
		initFeildsAndMap();
		
		// 初始化底部按钮
		switchBottomButtons();
		
		// 初始化顶部界面和顶部按钮
		initViewTopWindow();
		
		// 初始化列表的对话框
		initListViewDialog();
		
		// 初始化列表
		initPlanListView();
		
		// 初始化浮动按钮
		initOptionsMenu();
		
		// 初始化界面
		View[] views = new View[2];
		views[0] = mTopView;
		views[1] = mViewPlanList;
		
		if (MarkFlowCell.class.isInstance(mMsgData) || MarkFlowDCell.class.isInstance(mCurrentItem)) {
			initWindows(views, true);
		} else {
			initWindows(views, false);
		}

		if (mCurrentStatus != ADD_STATUS) {
			mTicketServiceInterface.getDetailServerData(mDataManager, mMsgData.getId());
		}
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
		
		mOptionMenuView = new OptionsMenuView(this, mSubMenuNames);
        if (mOptionMenuImplement != null) {
        	setOptionSubMenuListener(mOptionMenuImplement.getOptionMenuClickListener());
        } else {
        	setOptionSubMenuListener(mSubMenuListener);
        }
	}
	
	/**
	 * 设置选项菜单监听器
	 * @param listener
	 */
	protected void setOptionSubMenuListener(SubMenuListener listener) {
		if (listener != null) {
			mSubMenuListener = listener;
		}
		
		mOptionMenuView.setSubMenuListener(mSubMenuListener);
	}
	
	private void initViewTopButtons() {
		if (mTopButtonInterface == null || mTopButtonInterface.getNames() == null
				||  mTopButtonInterface.getNames().length == 0) {
			return;
		}
		
		if (SpecialButtonInterface.class.isInstance(mTopButtonInterface)) {
			((SpecialButtonInterface) mTopButtonInterface).init();
		}
		
		LinearLayout line = getLinearLayout();

		LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(
						R.dimen.ticket_button_width), getResources()
						.getDimensionPixelSize(R.dimen.ticket_button_height));
		layoutParams1.setMargins(0, 10, 10, 10);
		for (int i = 0; i < mTopButtonInterface.getNames().length; i++) {
			Button btn = initViewTopButton(i);
			line.addView(btn, layoutParams1);
		}
	}
	
	private LinearLayout getLinearLayout() {
		int tvLength = mTopUpdateFields.length;
		int topButtonLength = mTopButtonInterface.getNames().length;
		if (tvLength % 3 == 0 || (tvLength % 3 == 1 && topButtonLength > 4
				|| (tvLength % 3 == 2 && topButtonLength > 2))) {

			LinearLayout parentLine = (LinearLayout) mTopWindow.getPopupView()
					.findViewById(R.id.parent_line);
			LinearLayout line = new LinearLayout(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			parentLine.addView(line, layoutParams);
			mNewLine = true;
			return line;
		} else {
			int line = tvLength / 3;
			return (LinearLayout) mTopWindow.getPopupView().findViewById(mTopWindow.baseLineId + line);
		}
		
	}
	
	private Button initViewTopButton(int i) {
		Button btn = new Button(this);
		btn.setPadding(0, 0, 0, 0);
		btn.setBackgroundResource(R.drawable.ticket_button_bg);
		btn.setTextColor(Color.BLACK);
		btn.setText(mTopButtonInterface.getNames()[i]);
		btn.setOnClickListener(mTopButtonInterface.getListeners()[i]);
		return btn;
	}
	
	/**
	 * 初始化显示，更新域并建立域映射表
	 */
	private void initFeildsAndMap() {
		// 初始化显示域
		mDisplayFields = mListImplement.getDisplayFeilds();

		// 初始化域切换映射
		mDisplayFieldSwitchMap = mListImplement.getDisplayFieldsSwitchMap();
		
		// 初始化更新域
		if (mDialogImplement != null) {
			mUpdateFields = mDialogImplement.getUpdateFeilds();
			
			if (DialogAdapterInterface.class.isInstance(mDialogImplement)) {
				mUpdateFieldSwitchMap = ((DialogAdapterInterface) mDialogImplement).getUpdateFieldsSwitchMap();
			}
					
			// 设置域名和标签映射，用于反映射提取保存的界面值
			if (mDialogImplement.getDialogLableNames() != 0) {
				mDialogLableNames = getResources()
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
		
		if (mTopWindowImplement != null) {
			mTopUpdateFields = mTopWindowImplement.getUpdateFeilds();
			
			if (WindowAdapterInterface.class.isInstance(mTopWindowImplement)) {
				mTopUpdateFieldSwitchMap = ((WindowAdapterInterface) mTopWindowImplement).getUpdateFieldsSwitchMap();
			}
					
			// 设置域名和标签映射，用于反映射提取保存的界面值
			if (mTopWindowImplement.getWindowLableNames() != 0) {
				mTopWindowLableNames = getResources()
							.getStringArray(mTopWindowImplement.getWindowLableNames());
				
				// 初始化更新字段映射表
				if (mTopUpdateFields != null) {
					mTopFieldLableMap = new HashMap<String, String>();
					for (int i = 0; i < mTopWindowLableNames.length; i++) {
						mTopFieldLableMap.put(mTopUpdateFields[i], mTopWindowLableNames[i]);
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
	 * 初始化顶部界面
	 */
	private void initViewTopWindow() {
		if (mTopWindowImplement != null) {
			mTopWindow = new BaseWindow(this, new BaseWidgetInterface() {
				
				@Override
				public Integer[] getImportantColumns() {
					return mTopWindowImplement.getImportantColumns(new HashMap<String, String>());
				}
				
				@Override
				public TwoNumber<View, android.widget.LinearLayout.LayoutParams> createExtraLayout() {
					return null;
				}
			});
	
			// 显示内容初始化
			if (WindowAdapterInterface.class.isInstance(mTopWindowImplement)) {
				mTopWindow.init(mTopWindowImplement.getWindowLableNames(), 
								((WindowAdapterInterface) mTopWindowImplement).getWindowStyles(), 
								((WindowAdapterInterface) mTopWindowImplement).getSupplyData(), false, 3);
				((WindowAdapterInterface) mTopWindowImplement).additionalInit(mTopWindow);
			} else {
				mTopWindow.init(mTopWindowImplement.getWindowLableNames(), null, null, false, 3);
			}
			mTopView = mTopWindow.getPopupView();
			if (mCurrentStatus != ADD_STATUS) {
				if (mCurrentStatus == INFO_STATUS) {
					mTopWindow.switchModifyWindow(false);
				} 
				mTopWindow.SetDefaultValue(getDefaultDialogValue(mMsgData, mTopUpdateFields, mTopUpdateFieldSwitchMap));

			} else {
				RemoteCommonService.getInstance().getCodeByDate(mCodeManager,
						getNumCode());
			}
			if (mCurrentStatus != INFO_STATUS) {
				initViewTopButtons();
			}
		}
	}

	private DataManagerInterface mCodeManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				mTopWindow.setEditTextContent(0, status.getMessage());
			}
		}

	};

	protected String[] getDefaultDialogValue(Object item, String[] fields, Map<String, Map<String, String>> switchMap) {

		Map<String, String> listItemMap 
						= MiscUtils.beanToMap(item);
		
		// 构建默认值数组
		String[] defaultValues = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			if (switchMap != null &&
					(switchMap.get(fields[i]) != null)) {
				defaultValues[i] = switchMap.get(fields[i])
										.get(listItemMap.get(fields[i]));
			} else {
				defaultValues[i] = listItemMap.get(fields[i]);
			}
		}
		
		return defaultValues;
	}
	
	/**
	 * 更新域拷贝
	 * @param saveData
	 */
	protected void updateFieldCopy(Field[] fs, Object object, Map<String, String> saveData, Map<String,String> fieldLableMap, Map<String, Map<String, String>> switchMap) {
		fieldCopy(fs, object, fieldLableMap, saveData, switchMap);
	}
	
	/**
	 * 将UI添加或修改域拷贝到对应的对象中
	 * @param fs
	 * @param target
	 * @param map
	 * @param saveData
	 */
	private boolean fieldCopy(  Field[] fs, 
								Object target, 
								Map<String, String> map,
								Map<String, String> saveData,
								Map<String, Map<String, String>> updateFieldSwitchMap) {
		if (saveData == null) {
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
				String value = saveData.get(map.get(fieldName));
				if (updateFieldSwitchMap != null 
						&& (updateFieldSwitchMap.get(fieldName) != null)) {
					// 如果需要域转换，但用户没有输入有效数据，直接跳过
					if (!value.equals("")) {
						value = updateFieldSwitchMap.get(fieldName).get(value);
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
		saveData = null;
		
		return true;		
	}
	
	/**
	 * 重载该方法实现dialog保存处理
	 * @param saveData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean dialogSaveButtonEvent() {
		// 保存界面输入的数据
		Map<String, String> saveData = mDialog.SaveData();
		Integer[] columns = mDialogImplement.getImportantColumns(saveData);
		
		if (columns != null) {
			for (Integer column : columns) {
				if (saveData.get(mDialogLableNames[column]).equals("")) {
					Toast.makeText(getBaseContext(), getString(R.string.pls_input_all_info), Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		}
		updateFieldCopy(mListItemClass.getDeclaredFields(), mCurrentItem, saveData, mFieldLableMap, mUpdateFieldSwitchMap);
		if (SpecialDialogAdapterInterface.class.isInstance(mDialogImplement)) {
			((SpecialDialogAdapterInterface<B>) mDialogImplement).additionalSaveData(mCurrentItem);
		}
		setTotalTextView();

		if (mCurrentItem.getIDU() == 0) {
			mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
		}

		mListAdapter.updateData(mLine, mCurrentItem);
		
		return true;
	}
	
	protected void setTotalTextView() {
		if (MarkFlowDCell.class.isInstance(mCurrentItem)) {
			Double allMoney = 0.0;
			for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
				allMoney += ((MarkFlowDCell) mListAdapter.getDataShowList().get(i)).getMoney();
			}
			mTotalTextView.setText(UtilTools.formatMoney("¥", allMoney, 2));
		}
		
	}
	
	/** 
	 * 初始化内容弹出对话框
	 */
	private void initListViewDialog() {
		
		// 对话框标题
		if (mDialogImplement != null) {
			String title = getResources().getString(mDialogImplement.getDialogTitleId());
			mDialog = new BaseDialog(this, title, new BaseWidgetInterface() {
				
				@Override
				public Integer[] getImportantColumns() {
					return mDialogImplement.getImportantColumns(new HashMap<String, String>());
				}
				
				@Override
				public TwoNumber<View, android.widget.LinearLayout.LayoutParams> createExtraLayout() {
					return null;
				}
			});
	
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
					}
				}        	
	        });
		}
	}

	private void initPlanListView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mViewPlanList = (ViewGroup) inflater.inflate(R.layout.ticket_list_view, null);
		/* 采购列表头布局 */
		
		if (mListImplement != null) {
			ViewGroup headerView = (ViewGroup) mViewPlanList.findViewById(R.id.listHeaderView);
			
			mHeaderView = inflater.inflate(mListImplement.getListHeaderLayoutId(), headerView, true);
			/* 获取列表使用的相关资源 */
			TypedArray typedArray = getResources().obtainTypedArray(
					mListImplement.getListHeaderIds());
			mListHeadNames = getResources().getStringArray(
					mListImplement.getListHeaderNames());
			mListView = (ListView) mViewPlanList.findViewById(R.id.listView);
	
			if (mListHeadNames != null) {
				mListItemIds = new int[typedArray.length()];
				for (int i = 0; i < mListItemIds.length; i++) {
					mListItemIds[i] = typedArray.getResourceId(i, 0);
					TextView tv = (TextView) mHeaderView
							.findViewById(mListItemIds[i]);
					String text = "<b>" + mListHeadNames[i] + "</b>";
					tv.setText(Html.fromHtml(text));
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
							.getDimension(R.dimen.ticket_table_text_size));
					tv.setGravity(Gravity.CENTER);
					tv.setPadding(0, 0, 0, 0);
				}
			}
			typedArray.recycle();
	
			/* ListView适配器初始化 */
			mListAdapter = new DataListAdapter<B>(this, mListAdapterImplement);
			mListView.setAdapter(mListAdapter);
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mListView.getLayoutParams();
			int len = (mTopUpdateFields.length - 1) / 3;
			if (mNewLine) {
				len += 1;
			}
			params.height = UtilTools.dp2pxH(this, 290 - (len) * 50);
			mListView.setLayoutParams(params);
		}
		
	}
	
	private boolean contain(int[] nums, int key) {
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] == key) {
				return true;
			}
		}
		return false;
	}
	
	private TickListAdapterInterface<B> mListAdapterImplement 
		= new TickListAdapterInterface<B>() {
		@Override
		public int getLayoutId() {
			return mListImplement.getListLayoutId();
		}
	
		@Override
		public View getHeaderView() {
			return mHeaderView;
		}
	
		@Override
		public void regesterListeners(final ViewHolder holder, final int position) {
			for (int i = 0; i < holder.tvs.length; i++) {
				// holder.tvs[i].setClickable(false);

				if ((mCurrentStatus != INFO_STATUS)
						&& (contain(getEditTextNums(), i))) {
					holder.tvs[i]
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									mCurrentItem = mListAdapter
											.getItem(position);
									mLine = position;
									modifyTicket();
								}
							});
				} else {
					holder.tvs[i]
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									long minus_time = System
											.currentTimeMillis() - mDismissTime;

									if (minus_time < 300)
										return;
									if (mCurrentStatus != INFO_STATUS) {
										mOptionMenuView.showAsDropDown(
												view,
												0,
												-view.getMeasuredHeight()
														- getResources()
																.getDimensionPixelSize(
																		R.dimen.popup_window_height));
									}

									mListAdapter.setSelected(position, true);
									mCurrentItem = mListAdapter
											.getItem(position);

									mLine = position;
								}
							});
				}

			}
			
		}
	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
										DataListAdapter<B> adapter, int position) {
			// 获取当前位置对象，然后转换为映射
			B listItem = adapter.getItem(position);
			
			Map<String, String> displayFieldMap = MiscUtils.beanToMap(listItem);
			
			displayFieldRemap(displayFieldMap, listItem, position);
			if (EditableTicketListInterface.class.isInstance(mListImplement)) {
				for (int i = 0; i < mDisplayFields.length; i++) {
					((EditableTicketListInterface) mListImplement).modifyFieldMap(displayFieldMap, listItem, mDisplayFields[i]);
				}
			}
			
			for (int i = 0; i < mDisplayFields.length; i++) {
				if ((displayFieldMap.get(mDisplayFields[i]) != null) && !(displayFieldMap.get(mDisplayFields[i])).equals("0")) {
					holder.tvs[i].setText(displayFieldMap.get(mDisplayFields[i]));
					holder.tvs[i].setTextColor(Color.BLACK);
					
					holder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
				} else {
					holder.tvs[i].setText("");
				}

				holder.tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.ticket_table_text_size));
				if ((mCurrentStatus != INFO_STATUS)
						&& (contain(getEditTextNums(), i))) {
					Drawable drawable= getResources().getDrawable(R.drawable.icon_modify);
			        // 这一步必须要做,否则不会显示.
			        drawable.setBounds(0, 0, 25, 25);
			        holder.tvs[i].setCompoundDrawables(null,null,drawable,null);
			        holder.tvs[i].setTextColor(Color.RED);
				}
			}
		}
	
		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			// 分配引用数组内存
			holder.tvs = new TextView[mListItemIds.length];
			
			// 为引用数组成员分配视图对象内存
			for (int i = 0; i < mListItemIds.length; i++) {
					holder.tvs[i] = (TextView) convertView
							.findViewById(mListItemIds[i]);
			}
			
		}
	
		@Override
		public List<B> findByCondition(Object... condition) {
			return null;
		}
	
		@Override
		public boolean isSameObject(B t1, B t2) {
			return t1.getId() == t2.getId();
		}

		@Override
		public int[] getEditTextNums() {
			return mListImplement.getEditTextNums();
		}

		@Override
		public boolean isDataValid() {
			return mListImplement.isDataValid();
		}
	};
	
	/**
	 * 显示域重映射
	 * @param displayFieldMap
	 * @param t
	 * @param position
	 */
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
			B t, int position) {
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
	
	private View initWindows(View[] views, boolean totalFlag) {
		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parent_id);
		int px = UtilTools.dp2pxH(getBaseContext(), 8);
		views[0].setPadding(0, px, 0, px);
		parentLayout.addView(views[0]);
		parentLayout.addView(views[1]);
		if (totalFlag == true) {
			View itemView = LayoutInflater.from(parentLayout.getContext())
					.inflate(R.layout.base_ticket_activity2_bottom_item,
							parentLayout, false);
			parentLayout.addView(itemView);
			mTotalTextView = (TextView) itemView
					.findViewById(R.id.total_content);
			mMarkTextView = (TextView) itemView
					.findViewById(R.id.remark_content_edit);
		}
		if (mCurrentStatus == INFO_STATUS) {
			mMarkTextView.setFocusableInTouchMode(false);
			mMarkTextView.clearFocus();
		} else {
			mMarkTextView.setFocusableInTouchMode(false);
			mMarkTextView.setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mMarkTextView.setFocusableInTouchMode(true);
					return false;
				}  
			});
		}
		return parentLayout;
	}

	private OptionsMenuView.SubMenuListener mSubMenuListener = new OptionsMenuView.SubMenuListener() {
		@Override
		public void onSubMenuClick(View view) {
			mDismissTime = System.currentTimeMillis();
			switch ((Integer) view.getTag()) {
			case 0:
				modifyTicket();
				break;
			case 1:
				deleteTicket();
				break;
			}
			mOptionMenuView.dismiss();
		}
	};
	
	private void setBottomData() {
		if (MarkFlowCell.class.isInstance(mMsgData)) {
			mTotalTextView.setText(UtilTools.formatMoney("¥",
					((MarkFlowCell) mMsgData).getTotal_money(), 2));
			mMarkTextView.setText(((MarkFlowCell) mMsgData).getMark());
		}
	}

	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
					Toast.makeText(getBaseContext(), getString(R.string.add_success), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
			LogUtil.i("wzw status" + status + " list:" + list);
			Intent intent;
			Bundle bundle;
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() != 0) {
					mListAdapter.setShowDataList((List<B>) list);
					setBottomData();
				}
				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:

				intent = new Intent();
				bundle = new Bundle();
				bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE,
						PurchaseDataCache.RESULT_UPDATE_CODE);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
				break;
			case AnalysisManager.SUCCESS_DB_ADD:

				intent = new Intent();
				bundle = new Bundle();
				bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE,
						PurchaseDataCache.RESULT_ADD_CODE);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
				break;
			case AnalysisManager.EXCEPTION_DB_ADD:
			case AnalysisManager.EXCEPTION_DB_UPDATE:
				requestServerDataFlag = false;
				break;
			default:
				break;
			}
		}

	};
	
	private void PassDataToServer() {
		// set list item data
		List<B> listJH = new ArrayList<B>();
		listJH.addAll(mListAdapter.getDataShowList());
		listJH.addAll(mRemoveList);

		if (mCurrentStatus == ADD_STATUS) {
			mMsgData
					.setPlan_person(UserCache.getCurrentUser().getUser_id());
			mTicketServiceInterface.addServerData(mDataManager, mMsgData, listJH);
		} else if (mCurrentStatus == MODIFY_STATUS) {
			mTicketServiceInterface.updateServerData(mDataManager, mMsgData, listJH);
		}
	}
	
	private int submitButtonStyle() {
		if (mMsgData.getStatus() == Integer
				.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0])) {
			return SUBMIT_BUTTON_STYLE;
        } else if ((mMsgData.getStatus() == Integer
                .parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0]) 
                && UserCache.getCurrentUserId() == mMsgData.getPlan_person())) {
            return REJECT_BUTTON_STYLE;
        } else {
            return APPROVAL_BUTTON_STYLE;
        }
	}
	
	// save button can't be live alone, otherwise it is approval button
	private boolean isSaveButtonStyle() {
		if (mMessage != null && mMessage.getIs_process() == 1) {
			return false;
		}
		if (submitButtonStyle() == SUBMIT_BUTTON_STYLE
				|| (submitButtonStyle() == REJECT_BUTTON_STYLE && mCurrentStatus == MODIFY_STATUS)) {
			return true;
		} else {
			// actually it is approval button
			return false;
		}
	}
	
	// 修改
	private void modifyTicket() {
		mDialog.show(getDefaultDialogValue(mCurrentItem, mUpdateFields, null));
	}

	private List<B> mRemoveList = new ArrayList<B>();
	private int mLine;

	// 删除
	private void deleteTicket() {
		UtilTools.deleteConfirm(this, new UtilTools.DeleteConfirmInterface() {

			@Override
			public void deleteConfirmCallback() {
				if (mCurrentItem.getIDU() != GLOBAL.IDU_INSERT) {
					mCurrentItem.setIDU(GLOBAL.IDU_DELETE);
					mRemoveList.add(mCurrentItem);
				}
				mListAdapter.deleteData(mLine);
				
			}
			
		});
	}
	
	private DataManagerInterface mSubmitManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			dismissProgressDialog();
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Toast.makeText(BaseTicketActivity.this, status.getMessage(), Toast.LENGTH_SHORT).show();
			}
			LogUtil.i("wzw status" + status.getCode());
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD
					|| status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE,
						PurchaseDataCache.RESULT_APPROVAL_CODE);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		}
		
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void SubmitFlowDataToServer() {
		List<B> listJH = new ArrayList<B>();
		listJH.addAll(mListAdapter.getDataShowList());
		listJH.addAll(mRemoveList);

		if (hasFlowApproval) {
			mMsgData.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
			Flow_approval flowApproval = new Flow_approval();
			flowApproval.setType_id(mMsgData.getId());
			flowApproval.setFlow_type(mFlowType);
			flowApproval.setSubmiter(UserCache.getCurrentUser().getUser_id());
			flowApproval.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			flowApproval.setStatus(Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
			flowApproval.setCurrent_level(mFlowSetting.getLevel1());
			LogUtil.i("wzw userList" + mFlowSetting);
			if (mFlowSetting.getLevel2() != 0) {
				flowApproval.setNext_level(mFlowSetting.getLevel2());
			} else {
				flowApproval
						.setNext_level(UserCache.getCurrentUser().getUser_id());
			}
			if (mCurrentStatus == ADD_STATUS) {
				mMsgData.setPlan_person(UserCache.getCurrentUser().getUser_id());
				((TicketServiceInterface) mTicketServiceInterface).submitForAdd(mSubmitManager, mMsgData, listJH, flowApproval, GLOBAL.FLOW_STATUS[0][0]);
			} else if (mCurrentStatus == MODIFY_STATUS) {
				((TicketServiceInterface) mTicketServiceInterface).submitForUpdate(mSubmitManager, mMsgData, listJH, flowApproval, GLOBAL.FLOW_STATUS[0][0]);
			}
		} else {
			mMsgData.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0]));
			if (mCurrentStatus == ADD_STATUS) {
				mMsgData.setPlan_person(UserCache.getCurrentUser().getUser_id());
				((TicketServiceInterface) mTicketServiceInterface).submitForAdd(mSubmitManager, mMsgData, listJH, null, GLOBAL.FLOW_STATUS[1][0]);
			} else if (mCurrentStatus == MODIFY_STATUS) {
				((TicketServiceInterface) mTicketServiceInterface).submitForUpdate(mSubmitManager, mMsgData, listJH, null, GLOBAL.FLOW_STATUS[1][0]);
			}
		}

	}
	
	private boolean checkAndSaveData() {
		boolean ret = false;
		do {
			Map<String, String> saveDataMap = mTopWindow.SaveData();
			Integer[] columns = mTopWindowImplement.getImportantColumns(saveDataMap);
			
			if (columns != null) {
				for (Integer column : columns) {
					if (saveDataMap.get(mTopWindowLableNames[column]).equals("")) {
						BaseToast.show(BaseTicketActivity.this, BaseToast.NULL_MSG);
						return ret;
					}
				}
			}
			if (mListAdapter.getDataShowList().size() == 0) {
				Toast.makeText(BaseTicketActivity.this,
						R.string.list_view_is_null, Toast.LENGTH_SHORT).show();
				break;
			}
			if (!mListAdapterImplement.isDataValid()) {
				Toast.makeText(getBaseContext(), R.string.list_view_is_error,
						Toast.LENGTH_SHORT).show();
				break;
			}
			
			updateFieldCopy(mMsgClass.getDeclaredFields(), mMsgData, saveDataMap, mTopFieldLableMap, mTopUpdateFieldSwitchMap);
			if (MarkFlowCell.class.isInstance(mMsgData)) {
				((MarkFlowCell) mMsgData).setMark(mMarkTextView.getText().toString());

				if (MarkFlowDCell.class.isInstance(mListAdapter.getDataShowList().get(0))) {
					double money = 0;
					for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
						money += ((MarkFlowDCell) mListAdapter.getDataShowList().get(i)).getMoney();
					}
					((MarkFlowCell) mMsgData).setTotal_money(money);
				}
				
			}
			
			ret = true;
		} while (false);
		return ret;
	}
	
	private void switchBottomButtons() {
		Button saveBottomButton = (Button) findViewById(R.id.save);
		OnClickListener saveListener = (new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!checkAndSaveData()) {
					return;
				}
				
				// must judge at last
				if (requestServerDataFlag) {
					LogUtil.i("wzw request ServerData");
					return;
				} else {
					requestServerDataFlag = true;
				}

				PassDataToServer();
			}
		});
		
		LogUtil.i("wzw flow:" + hasFlowApproval);

		if (mCurrentStatus == INFO_STATUS) {
			if (!hasFlowApproval
					|| (hasFlowApproval && mMsgData.getStatus() == Integer
							.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0]))) {
				{
					saveBottomButton.setVisibility(View.GONE);
					return;
				}
			}
		} else {
			if (!hasFlowApproval) {
				saveBottomButton.setOnClickListener(saveListener);
				return;
			}
		}

		Button approvalButton = (Button) findViewById(R.id.approval);
		Button submitButton = (Button) findViewById(R.id.submit);
		OnClickListener approvalListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Flow_approval flowApproval = new Flow_approval();
				flowApproval.setFlow_type(mFlowType);
				flowApproval.setType_id(mMsgData.getId());
//				mFlowApprovalDialog = new FlowApprovalDialog(BaseTicketActivity.this, flowApproval, mFlowSetting, new FlowApprovalDialog.FlowApprovalManager() {
//
//					@Override
//					public void passFlowData2Server(
//							DataManagerInterface flowApprovalDataInterface,
//							Flow_approval currentApproval, Flow_approval nextApproval) {
//						mTicketServiceInterface.passApproval(flowApprovalDataInterface, mMsgData, mListAdapter.getDataShowList(), currentApproval, nextApproval);
//					}
//
//					@Override
//					public void rebutFlowData2Server(
//							DataManagerInterface flowApprovalManagerInterface,
//							Flow_approval currentApproval, Flow_approval nextApproval) {
//						mTicketServiceInterface.rebutApproval(flowApprovalManagerInterface, mMsgData, currentApproval, nextApproval);
//					}
//
//					
//				});
				
				if (mMsgData.getStatus() == Integer
						.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0])) {
					mFlowApprovalDialog.show(true);
				} else {
					mFlowApprovalDialog.show(false);
				}
			}
		};

		// first we should check whether open approval flow, and if it is open,
		// we invoke isSubmitButtonStyle function to judge if we need add two
		// buttons submit and save,
		// or add one button approval
		if (hasFlowApproval) {
			// sync GLOBAL.FLOW_APPROVAL_STATUS
			if (mMsgData.getStatus() == 0) {
				mMsgData.setStatus(1);
			}
			if (isSaveButtonStyle()) {

				saveBottomButton.setOnClickListener(saveListener);
				submitButton.setVisibility(View.VISIBLE);
				submitButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (!checkAndSaveData()) {
							return;
						}
						UtilTools.deleteConfirm(BaseTicketActivity.this, new UtilTools.DeleteConfirmInterface() {

							@Override
							public void deleteConfirmCallback() {
								showProgressDialog();
								SubmitFlowDataToServer();

							}
							
						}, getResources().getString(
								R.string.need_upload_change), getResources().getString(R.string.remind));	
					}

				});
				if (submitButtonStyle() == REJECT_BUTTON_STYLE
						&& mCurrentStatus == MODIFY_STATUS) {
					approvalButton.setVisibility(View.VISIBLE);
					approvalButton.setOnClickListener(approvalListener);
				}
			} else {
				saveBottomButton.setText(R.string.approve);
				saveBottomButton.setOnClickListener(approvalListener);
			}
		} else {
			saveBottomButton.setOnClickListener(saveListener);
			submitButton.setVisibility(View.VISIBLE);
			submitButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!checkAndSaveData()) {
						return;
					}
					showProgressDialog();
					SubmitFlowDataToServer();
				}

			});
		}

	}
	
}
