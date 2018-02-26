package com.pm360.cepm360.app.module.email;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.CacheObs;
import com.pm360.cepm360.app.cache.ContactCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ListNoHeaderInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.CustomTranslateAnimation;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.ViewContainer;
import com.pm360.cepm360.app.module.contract.ContractChangeAttributeActivity;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.expenses.RemoteChangeContractService;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmailFragment<T extends MailBox> extends Fragment {
	
	/** ------------------------- 常量定义 ---------------------*/
	protected static final String RESULT_KEY = "result_key";
	
	// 简单的Int常量定义
	protected static final int BASE_POSITION = 0;
	protected static final int SHOW_TOAST = BASE_POSITION;
	protected static final int SHOW_PROGRESS_DIALOG = BASE_POSITION + 1;
	protected static final int DISMISS_PROGRESS_DIALOG = BASE_POSITION + 2;
	protected static final int DELAYTIME_FOR_SHOW = 500;
	
	protected static final int EMAIL_WRITE = BASE_POSITION;
	protected static final int EMAIL_EDIT = BASE_POSITION + 1;
	protected static final int EMAIL_REPLY = BASE_POSITION + 2;
	protected static final int EMAIL_REPLY_ALL = BASE_POSITION + 3;
	protected static final int EMAIL_FORWORD = BASE_POSITION + 4;
	
	protected static final int EMAIL_INBOX = BASE_POSITION;
	protected static final int EMAIL_OUTBOX = BASE_POSITION + 1;
	protected static final int EMAIL_DRAFTBOX = BASE_POSITION + 2;
	
	protected static final String EMAIL_ATTACHMENT_ROOT_DIR
									= FileUtils.SDPATH + "CEPM360/mailbox/";
	
	// 邮件大小常量设置
	protected static final int SIZE_KB = 1024;
	protected static final int SIZE_MB = SIZE_KB * 1024;
	protected static final int SIZE_GB = SIZE_MB * 1024;
	
	// 合同变更
	private boolean mIsContractChange;
	private Contract_change mContractChange;
	
	/** ------------------------- 视图变量 ---------------------*/
	
	protected LayoutInflater mInflater;
	
	// 布局和视图定义
	protected View mRootLayout;
	
	protected View mListRootLayout;
	
	// 邮件头和实体内容布局
	protected View mContentRootLayout;
	protected View mContentLayout;
	protected View mContentHeaderLayout;
	protected View mContentBodyLayout;
	
	// 邮件标题
	protected TextView mEmailTitle;
	
	// LOGO
	protected ViewGroup mEmailLogo;
	
	// 邮件头
	protected TextView mEmailSender;
	protected ViewContainer mEmailRecievers;
	protected ViewContainer mEmailCcs;
	protected TextView mEmailDate;
	protected TextView mEmailSize;
	
	// 邮件附件
	protected ViewGroup mEmailAttachmentGroup;
	protected ViewGroup mCurrentAttachmentViewGroup;
	protected View mAttacmentBodyCut;
	
	protected PopupWindow mAttachmentPopupWindow;
	protected ViewGroup mAttachmentPopupMenuGroup;
	protected TextView mAttachmentOpen;
	protected TextView mAttachmentDownloadAll;
	
	// 联系人弹出窗口
	protected PopupWindow mPopupWindow;
	protected PopupWindow mDetailPopupWindow;
	protected ViewGroup mPopupViewGroup;
	protected ViewGroup mDetailPopupViewGroup;
	protected User mCurrentSelectUser;
	protected TextView mCurrentSelectTextView;
	
	protected ImageView mContactImage;
	protected TextView mContactName;
	protected TextView mContactCompany;
	protected TextView mContactDepartment;
	
	protected ImageView mDetailContactImage;
	protected TextView mDetailContactName;
	protected TextView mDetailContactCompany;
	protected TextView mDetailContactDepartment;
	protected TextView mDetailContactSex;
	protected TextView mDetailContactEmail;
	protected TextView mDetailContactMobile;
	protected TextView mDetailContactOfficePhone;
	protected TextView mDetailContactQQ;
	
	protected TextView mContactWrite;
	protected TextView mContactRelative;
	protected TextView mContactDetail;
	
	// 邮件正文内容
	protected TextView mEmailBody;
		
	// 列表视图控件
	protected ListView mListView;
	
	// 选项菜单
	protected OptionsMenuView mOptionsMenu;
	
	// 浮动菜单
	protected FloatingMenuView mFloatingMenu;
	
	// 列表适配器，为列表提供数据
	protected DataListAdapter<T> mListAdapter;
	
	// 进度对话框
	protected ProgressDialog mProgressDialog;
	
	// 列表头标题和宽度扩展图标
	protected ImageView mRefreshImage;
	protected TextView mListTitle;
	protected TextView mEmailUnreadCount;
	protected ImageView mListExpand;
	
	/**--------------------- 暂存数据 ---------------------**/
	// 应用对象，用于获取共享数据
	protected CepmApplication mApplication;
		
	// 当前数据
	protected T mCurrentListItem;
	protected List<Files> mAttachmentList;
	
	// 指定列表项
	protected T mSpecifiedItem;
	
	// 用户Id和名称映射
	private Map<String, String> mUserMaps;
	
	// 附件视图和Files映射
	private Map<View, Files> mAttachmentMap = new HashMap<View, Files>();
	
	// 选择列表
	private List<T> mSelectedDataList;
	
	/** ------------------------- 标识变量 ---------------------*/
	
	// 数据是否加载完成
	protected boolean mDataLoaded;
	
	// 是否启用多选
	protected boolean mEnableMultSelect;
	
	// 使能选择按钮
	protected boolean mEnableInnerButton;
	protected boolean mViewMode;
	
	// 模式
	protected OperationMode mCurrentMode;
	
	/** ------------------------- 模式相关 ---------------------*/
	// 模式定义，普通模式和批量操作模式
	protected ActionMode mActionMode;
	protected int mCurrentBox;
	
	/** ------------------------- 资源保存变量 ---------------------*/
	
	// 保存列表显示项的IDS
	protected int[] mListItemIds;
	
	// 列表项显示所需数据资源
	protected String[] mDisplayFields;
	protected Map<String, Map<String, String>> mDisplayFieldSwitchMap;
	
	// 浮动菜单
	protected int[] mFloatingMenuImages;
	protected String[] mFloatingMenuTips;
	
	/**------------------ 删除操作数据 ------------*/
	private List<T> mRemoveList = new ArrayList<T>();
	private int mRemoveCount;
	private int mRemoveFailedCount;
	
	// 下载
	private int mDownloadCount;
	private int mDownloadFailedCount;
	
	/** ------------------------- 接口实现变量 ---------------------*/
	
	// 列表接口实现
	protected ListNoHeaderInterface<T> mListImplement;
	
	// 服务接口实现
	protected ServiceInterface<T> mServiceImplement;
	
	// 选项菜单接口
	protected OptionMenuInterface mOptionMenuImplement;
	
	// 浮动菜单接口
	protected FloatingMenuInterface mFloatingMenuImplement;
	
	// 保存树和内容类型对象
	protected Class<T> mListItemClass;
	
	/** ------------------------- 交互 ----------------------------*/
	
	// 广播接收器
	protected BroadcastReceiver mReceiver;
	
	
	/**
	 * 初始化，如果使用默认的选项菜单和浮动菜单，传入null即可
	 * @param clazz
	 * @param listInterface
	 * @param serviceInterface
	 * @param optionMenuInterface
	 * @param floatingMenuInterface
	 * @param box
	 */
	public void init(Class<T> clazz,
					 ListNoHeaderInterface<T> listInterface,
					 ServiceInterface<T> serviceInterface,
					 OptionMenuInterface optionMenuInterface,
					 FloatingMenuInterface floatingMenuInterface,
					 int box) {
		mListItemClass = clazz;
		mListImplement = listInterface;
		mServiceImplement = serviceInterface;
		
		if (optionMenuInterface != null) {
			mOptionMenuImplement = optionMenuInterface;
		}
		
		if (floatingMenuInterface != null) {
			mFloatingMenuImplement = floatingMenuInterface;
		}
		
		mCurrentBox = box;
	}
	
	/**
	 * 启用多选功能
	 */
	public void enableMultSelect() {
		mEnableMultSelect = true;
	}
	
	/**
	 * 设置列表标题
	 * @param isSearch
	 */
	public void setListTitle(boolean isSearch) {
		
		// 设置树视图域标题
		if (isSearch) {
			mListTitle.setText(R.string.email_search_list);
		} else {
			switch (mCurrentBox) {
				case EMAIL_INBOX:
					mListTitle.setText(R.string.email_receive_list);
					break;
				case EMAIL_OUTBOX:
					mListTitle.setText(R.string.email_send_list);
					break;
				case EMAIL_DRAFTBOX:
					mListTitle.setText(R.string.email_draft_list);
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * 设置未读数量
	 * @param unread
	 */
	public void setUnreadCount(List<T> list) {
		int unread = calcUnreadEmailCount(list);
		
		if (mCurrentBox == EMAIL_INBOX) {
			if (unread == 0) {
				if (mEmailUnreadCount.isShown()) {
					CustomTranslateAnimation.fadeRight(mEmailUnreadCount);
				}
			} else {
				if (!mEmailUnreadCount.isShown()) {
					CustomTranslateAnimation.showRight(mEmailUnreadCount);
				}
				
				mEmailUnreadCount.setText(" (" + unread + ")");
			}
		}
	}
	
	/**
	 * 刷新未读数量
	 */
	public void refreshUnreadCount() {
		setUnreadCount(mListAdapter.getDataShowList());
	}
	
	/**
	 * 计算未读邮件数量
	 * @param mailBoxs
	 * @return
	 */
	public int calcUnreadEmailCount(List<T> mailBoxs) {
		int count = 0;
		
		for (MailBox mailBox : mailBoxs) {
			if (!MailBoxUtils.isMailReaded(mailBox,
					UserCache.getCurrentUser())) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	protected DataManagerInterface getServiceHandler() {
		return mManager;
	}
	
	/**
	 * 设置
	 * @param dataList
	 */
	protected void setShowDataList(List<T> dataList) {
		mListAdapter.setShowDataList(dataList);
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
	 * 发送延时消息
	 * @param what
	 * @param delay
	 */
	public void sendEmptyMessageDelayed(int what, long delay) {
		mHandler.sendEmptyMessageDelayed(what, delay);
	}
	
	/**
	 * 发送延时消息
	 * @param what
	 */
	public void sendEmptyMessageDelayed(int what) {
		mHandler.sendEmptyMessageDelayed(what, DELAYTIME_FOR_SHOW);
	}
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 */
	private void sendMessage(int what) {
		sendMessage(what, null);
	}
	
	/**
	 * 服务返回结果处理
	 */
	private DataManagerInterface mManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			
			// 结束加载进度对话框
			mDataLoaded = true;
			sendMessage(DISMISS_PROGRESS_DIALOG);
			
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_QUERY:
					sortByDate((List<MailBox>) list);
					
					final List<MailBox> mailBoxs = (List<MailBox>) list;
					ContactCache.load(MailBoxUtils.getAllContacts(mailBoxs), 
									new CallBack<Void, Integer>() {
						
						@Override
						public Void callBack(Integer a) {
							mListAdapter.setDataList((List<T>) mailBoxs);
							mListAdapter.setShowDataList((List<T>) mailBoxs);
							
							if (mSpecifiedItem != null) {
								locationSpecifiedItem(mSpecifiedItem);
							} else if (mCurrentListItem != null) {
								locationSpecifiedItem(mCurrentListItem);
							} else if (mailBoxs.size() > 0) {
								handleClick(0);
							}
							
							if (mCurrentBox == EMAIL_INBOX) {
								refreshUnreadCount();
							}
							return null;
						}
					});
					break;
				case AnalysisManager.SUCCESS_DB_ADD:
					mListAdapter.addDataToList((T) list.get(0));
					if (mCurrentBox == EMAIL_INBOX) {
						refreshUnreadCount();
					}
					break;
				case AnalysisManager.SUCCESS_DB_DEL:
					mRemoveCount++;
					if (mRemoveCount == mRemoveList.size()) {
						sendMessage(DISMISS_PROGRESS_DIALOG);
						if (mCurrentBox == EMAIL_INBOX) {
							refreshUnreadCount();
						}
						
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
						sendMessage(DISMISS_PROGRESS_DIALOG);
						if (mCurrentBox == EMAIL_INBOX) {
							refreshUnreadCount();
						}
						
						if (mRemoveCount == mRemoveFailedCount) {	// 全部失败
							status.setMessage(getResources()
										.getString(R.string.delete_all_failed));
						} else {	// 部分失败
							partDeleteSucessful(status);
						}
						deleteDataClear();
					}
					break;
				default:
					break;
			}
			
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
					&& status.getCode() != AnalysisManager.SUCCESS_DB_UPDATE
					&& mRemoveCount == 0) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
		}
		
	};
	
	/**
	 * 邮件按日期排序
	 * @param boxList
	 */
	public void sortByDate(List<MailBox> boxList) {
		Collections.sort(boxList, boxComparator);
	}
	
	/**
	 * 邮件日期排序算法
	 */
	Comparator<MailBox> boxComparator = new Comparator<MailBox>() {

		@Override
		public int compare(MailBox box1, MailBox box2) {
			return (int) (box2.getMail_time().getTime() 
						- box1.getMail_time().getTime());
		}
	};
	
	/**
	 * 定位到指定列表项
	 * @param t
	 */
	private void locationSpecifiedItem(T t) {
		List<T> showList = mListAdapter.getDataShowList();
		int position = 0;
		
		for (; position < showList.size(); position++) {
			if (mListImplement.getListItemId(t) 
					== mListImplement.getListItemId(showList.get(position))) {
				break;
			}
		}
		
		if (position >= showList.size()) {
			mCurrentListItem = null;
			switchContent();
		} else {
			mCurrentListItem = mListAdapter.getItem(position);
		}
		mListAdapter.setSelected(position, true);
	}
	
	/**
	 * 全部删除成功
	 */
	private void allDeleteSuccessful() {
		mListAdapter.clearSelection();
		mListAdapter.deleteData(mRemoveList);
		
		// 将当前项置为空
		mCurrentListItem = null;
		
		// 更新内容
		switchContent();
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
		
		mInflater = inflater;
			
		// 初始化部分成员数据
		initBasicLocalData();
		
		// 初始化布局管理器
		initBasicLayout(inflater, container);
		
		// 初始化树视图
		initListLayout();
		
		// 初始化内容视图
		initContentLayout();
		
		// 初始化进度对话框
		createProgressDialog();
		
		if (!mViewMode) {
			
			// 初始化浮动菜单
			initFloatingMenu();
		}
		
		// 加载数据
		loadData();
		
		return mRootLayout;
	}
	
	/**
	 * 初始化部分成员数据
	 */
	private void initBasicLocalData() {	
		
		// 注册广播接收器
		initBroadCastReceiver();
		
		// 设置选择模式
		getArgumentsInfo();
		
		// 初始化显示，更新域并建立域映射表
		initFeildsAndMap();
	}
	
	/**
	 * 注册广播接收器
	 */
	private void initBroadCastReceiver() {
		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				mServiceImplement.getListData();
			}
		};
		
		IntentFilter filter = new IntentFilter();
		switch (mCurrentBox) {
			case EMAIL_INBOX:
				filter.addAction(ComposeActivity.REFRESH_INBOX);
				break;
			case EMAIL_OUTBOX:
				filter.addAction(ComposeActivity.REFRESH_OUTBOX);
				break;
			case EMAIL_DRAFTBOX:
				filter.addAction(ComposeActivity.REFRESH_DRAFTBOX);
				break;
			default:
				break;
		}
		
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		if (mReceiver != null) { 
			getActivity().unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}

	/**
	 * 设置选择模式
	 */
	protected void getArgumentsInfo() {
		mCurrentMode = OperationMode.NORMAL;
				
		// 设置选择模式
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(ListSelectActivity.SELECT_MODE_KEY)) {
				if ((boolean) bundle.getBoolean(ListSelectActivity.SELECT_MODE_KEY)) {
					mCurrentMode = OperationMode.MULTI_SELECT;
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
		if (mListImplement != null) {
			
			// 初始化显示域
			mDisplayFields = mListImplement.getDisplayFeilds();
			
			// 初始化域切换映射
			mDisplayFieldSwitchMap = mListImplement.getDisplayFieldsSwitchMap();
			
			// 列表项资源Id解析
			TypedArray typedArray = getResources()
					.obtainTypedArray(mListImplement.getListItemIds());
			mListItemIds = new int[typedArray.length()];
			for (int i = 0; i < mListItemIds.length; i++) {
				mListItemIds[i] = typedArray.getResourceId(i, 0);
			}
			typedArray.recycle();
		}
		
		mUserMaps = ContactCache.getContactIdNameMap();
	}
	
	/**
	 * 初始化视图布局
	 * @param inflater
	 * @param container
	 */
	private void initBasicLayout(LayoutInflater inflater, ViewGroup container) {
		
		// 获取fragment的根布局
		mRootLayout = inflater.inflate(R.layout.email_inoutbox_layout, container, false);
		
		// 获取树区域的根布局
		mListRootLayout = mRootLayout.findViewById(R.id.email_list_layout);
		
		if (OperationMode.NORMAL != mCurrentMode
				|| mEnableInnerButton) {
			initActionButton();
		}
	}
	
	/**
	 * 初始化树视图
	 */
	private void initListLayout() {
		// 基本配置
		initBaseData();
		
		// 获取树视图相关资源，并初始化树列表视图
		initListView();
	}
	
	/**
	 * 配置树视图基本信息
	 */
	private void initBaseData() {
		
		// 获取树列表分类标题控件，标题的名称在实现类中设置
		mListTitle = (TextView) mRootLayout.findViewById(R.id.category);
		mEmailUnreadCount = (TextView) mRootLayout.findViewById(R.id.unread_count);
		if (mCurrentBox != EMAIL_INBOX) {
			mEmailUnreadCount.setVisibility(View.GONE);
		}
		
		// 设置列表类型标题
		setListTitle(false);
		
		mRefreshImage = (ImageView) mRootLayout.findViewById(R.id.email_list_refresh);
		mRefreshImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDataLoaded = false;
				sendMessage(SHOW_PROGRESS_DIALOG);
				mServiceImplement.getListData();
			}
		});
		
		// 获取扩展树列表控件
		mListExpand = (ImageView) mRootLayout.findViewById(R.id.expand_icon);
		mListExpand.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				LayoutParams params = (LayoutParams) mListRootLayout.getLayoutParams();
				if (params.weight == BASE_POSITION + 3) { // 未展开
					params.weight = 2;
					mListExpand.setImageResource(R.drawable.arrow_double_left);
				} else { // 已展开
					params.weight = 3;
					mListExpand.setImageResource(R.drawable.arrow_double_right);
				}
				mListRootLayout.setLayoutParams(params);
			}
		});
	}
	
	/**
	 * 获取树视图相关资源，并初始化树列表视图
	 */
	private void initListView() {
		
		// 获取树视图对象
		mListView = (ListView) mRootLayout.findViewById(R.id.email_listview);
		
		// 创建树视图适配器，并将其绑定到树视图列表上
		mListAdapter = new DataListAdapter<T>(getActivity(), false, mListAdapterInterface);
		mListView.setAdapter(mListAdapter);
	}
	
	/**
	 * 实现列表适配器接口
	 */
	private ListAdapterInterface<T> mListAdapterInterface 
										= new ListAdapterInterface<T>() {
		@Override
		public int getLayoutId() {
			return mListImplement.getListLayoutId();
		}

		@Override
		public View getHeaderView() {
			return null;
		}

		@Override
		public void regesterListeners(final ViewHolder viewHolder, final int position) {
			viewHolder.root.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					handleClick(position);
				}
			});
			
			// 注册长按监听
            viewHolder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                	if (mEnableMultSelect) {
						
                    	// 长按进入ActionMode，此时ActionMode应该是NULL
                        mActionMode = getActivity().startActionMode(mCallback);
                        mListAdapter.setPickSelected(position);
                        updateActionModeTitle(mActionMode, getActivity(),
                                				mListAdapter.getSelectedList().size());
                	} else {
                		mCurrentListItem = mListAdapter.getItem(position);
                		mListAdapter.setSelected(position, true);
                	}
                    return true;
                }
            });
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
										DataListAdapter<T> adapter, int position) {
			
			// 获取当前位置对象，然后转换为映射
			T listItem = adapter.getItem(position);
			
			Map<String, String> displayFieldMap = MiscUtils.beanToMap(listItem);
			
			displayFieldRemap(displayFieldMap, listItem, position);
			
			for (int i = 0; i < mDisplayFields.length; i++) {
				if (BASE_POSITION == i) {
					if (mCurrentBox != EMAIL_INBOX) {
						holder.ivs[i].setVisibility(View.INVISIBLE);
						holder.tvs[i].getPaint().setFakeBoldText(false);
						continue;
					}
					
					if (mCurrentBox == EMAIL_INBOX) {
						if (MailBoxUtils.isMailReaded(listItem,
											UserCache.getCurrentUser())) {
							holder.ivs[i].setImageResource(R.drawable.email_read);
							holder.tvs[i].getPaint().setFakeBoldText(false);
						} else {
							holder.ivs[i].setImageResource(R.drawable.email_unread);
							holder.tvs[i].getPaint().setFakeBoldText(true);
						}
					}
					continue;
				}
				
				if (BASE_POSITION + 1 == i) {
					if ((listItem.getAttachment() != null 
							&& !listItem.getAttachment().equals(""))
							|| listItem.getMail_table_id() != 0) {
						Drawable drawable = getResources().getDrawable(R.drawable.email_attachment);
						
						//必须设置图片大小，否则不显示
						drawable.setBounds(0, 0, drawable.getMinimumWidth(),
												 drawable.getMinimumHeight());
						
						// 设置左边的附件文件类型图标
						holder.tvs[i-1].setCompoundDrawables(null, null, drawable, null);
					} else {
						
						// 隐藏附件图标
						holder.tvs[i-1].setCompoundDrawables(null, null, null, null);
					}
				}
				
				if (mCurrentBox != EMAIL_INBOX && BASE_POSITION + 3 == i) {
					LayoutParams params = (LayoutParams)
							((LinearLayout) holder.tvs[i-1].getParent()).getLayoutParams();
					params.setMargins(getResources().getDimensionPixelSize(R.dimen.dp16_w),
							getResources().getDimensionPixelSize(R.dimen.dp2_h), 0, 0);
				}
				
				holder.tvs[i-1].setText(displayFieldMap.get(mDisplayFields[i]));
			}
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.root = convertView;
			
			// 分配引用数组内存
			holder.tvs = new TextView[mListItemIds.length-1];
			holder.ivs = new ImageView[1];
			
			// 为引用数组成员分配视图对象内存
			for (int i = 0; i < mListItemIds.length; i++) {
				View view = convertView.findViewById(mListItemIds[i]);
				
				if (BASE_POSITION == i) {
					holder.ivs[0] = (ImageView) view;
				} else {
					holder.tvs[i-1] = (TextView) view;
				}
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
	 * 处理单击事件
	 * @param position
	 */
	protected void handleClick(int position) {
		
		// ActionMode批量操作模式
		if (mActionMode != null) {
			mListAdapter.setPickSelected(position);
			updateActionModeTitle(mActionMode, getActivity(),
                    			  mListAdapter.getSelectedList().size());
		} else {
			mCurrentListItem = mListAdapter.getItem(position);
			
			// 设置读标识
			if (mCurrentBox == EMAIL_INBOX 
					&& !MailBoxUtils.isMailReaded(mCurrentListItem,
							UserCache.getCurrentUser())) {
				MailBoxUtils.setEmailRead(mCurrentListItem, UserCache
						.getCurrentUser(), GLOBAL.MSG_READ[1][0]);
				((MailServiceInterface) mServiceImplement).read(mCurrentListItem);
			}
			
			mListAdapter.setSelected(position, true);
			
			// 切换邮件内容
			switchContent();
		}
	}
	
	/**
	 * 初始化邮件内容布局
	 */
	private void initContentLayout() {
		
		// 初始化内容视图
		initContentView();
		
		// 设置控件的可视化
		setWidgetVisable();
	}
	
	/**
	 * 初始化内容视图
	 */
	private void initContentView() {
		
		// 获取邮件内容跟布局
		mContentRootLayout = mRootLayout.findViewById(R.id.content_layout);
		mContentLayout = mRootLayout.findViewById(R.id.email_content_layout);
		mContentHeaderLayout = mRootLayout.findViewById(R.id.email_header);
		mContentBodyLayout = mRootLayout.findViewById(R.id.email_body);
		
		// 邮件标题
		mEmailTitle = (TextView) mContentRootLayout.findViewById(R.id.email_content_title);
		
		// 邮件LOGO
		mEmailLogo = (ViewGroup) mRootLayout.findViewById(R.id.email_no_content_layout);
		
		// 邮件正文
		mEmailBody = (TextView) mContentBodyLayout.findViewById(R.id.email_content_body);
		
		// 邮件头
		initEmailHeader();
		
		// 邮件附件
		initEmailAttachment();
		
		// 初始化联系人弹出窗口
		initEmailContactsPopupWindow();
		
		// 初始化联系人详情窗口
		initContactDetailPopupWindow();
	}
	
	/**
	 * 初始化邮件头
	 */
	private void initEmailHeader() {
		
		// 邮件头部
		mEmailSender = (TextView) mContentHeaderLayout.findViewById(R.id.sender);
		
		mEmailRecievers = (ViewContainer) mContentHeaderLayout.findViewById(R.id.reciever);
		mEmailRecievers.setChildSize((int) getResources().getDimension(R.dimen.dp54_w),
									 (int) getResources().getDimension(R.dimen.dp20_h));
		
		mEmailCcs = (ViewContainer) mContentHeaderLayout.findViewById(R.id.cc);
		mEmailCcs.setChildSize((int) getResources().getDimension(R.dimen.dp54_w),
				 				(int) getResources().getDimension(R.dimen.dp20_h));
		
		mEmailDate = (TextView) mContentHeaderLayout.findViewById(R.id.time);
		mEmailSize = (TextView) mContentHeaderLayout.findViewById(R.id.size);
	}
	
	/**
	 * 初始化邮件附件
	 */
	@SuppressWarnings("deprecation")
	private void initEmailAttachment() {
		// 附件
		mEmailAttachmentGroup = (ViewGroup) 
				mContentBodyLayout.findViewById(R.id.email_attachment_layout);
		
		mAttachmentPopupMenuGroup = (ViewGroup)
				mInflater.inflate(R.layout.attachment_popup_menu, null);
		
		// 弹出窗口
		mAttachmentPopupWindow = new PopupWindow(mAttachmentPopupMenuGroup, 180, 44, true);
		mAttachmentPopupWindow.setOutsideTouchable(true);
		mAttachmentPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		//mAttachmentPopupWindow.setAnimationStyle(R.style.popupwindow_animation_downin_rightout);
		
		// 弹出窗口菜单项监听
		mAttachmentOpen = (TextView) mAttachmentPopupMenuGroup
							.findViewById(R.id.email_attachment_open);
		mAttachmentDownloadAll = (TextView) mAttachmentPopupMenuGroup
							.findViewById(R.id.email_attachment_download_all);
		
		mAttachmentOpen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mAttachmentPopupWindow.dismiss();
				
				if (!mIsContractChange) {
					Files files = mAttachmentMap.get(mCurrentAttachmentViewGroup);
					IntentBuilder.viewFile(getActivity(), files, EMAIL_ATTACHMENT_ROOT_DIR 
							+ FileUtils.getFileFullName(files.getPath()));
				} else {
					Intent intent = new Intent(getActivity(), ContractChangeAttributeActivity.class);
					intent.putExtra(ContractChangeAttributeActivity.CONTRACT_CHANGE_KEY, 
							mContractChange);
					intent.putExtra(ContractChangeAttributeActivity.EMAIL_INTER_KEY, true);
					intent.putExtra(ContractChangeAttributeActivity.IS_MODIFY_KEY, false);
					intent.putExtra(ContractChangeAttributeActivity.IS_CHANGE_APPLY_KEY, true);
					startActivity(intent);
				}
			}
		});
		
		mAttachmentDownloadAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mAttachmentPopupWindow.dismiss();
				
				if (mAttachmentList != null) {
					for (final Files files : mAttachmentList) {
						IntentBuilder.download(getActivity(), new DataManagerInterface() {
							
							@Override
							public void getDataOnResult(ResultStatus status, List<?> list) {
								switch (status.getCode()) {
									case AnalysisManager.SUCCESS_DOWNLOAD:
										mDownloadCount++;
										break;
									case AnalysisManager.EXCEPTION_DOWNLOAD:
										mDownloadFailedCount++;
										if (++mDownloadCount == mAttachmentList.size()) {
											sendMessage(SHOW_TOAST, files.getFile_name()
															+ getString(R.string.download_failed));
										}
										break;
									default:
										break;
								}
							}
						}, files, EMAIL_ATTACHMENT_ROOT_DIR 
								+ FileUtils.getFileFullName(files.getPath()), true);
					}
				}
			}
		});
		
		mAttacmentBodyCut = mContentBodyLayout.findViewById(R.id.line_blow_attachment);
	}
	
	/**
	 * 初始化联系人弹出窗口
	 */
	@SuppressWarnings("deprecation")
	private void initEmailContactsPopupWindow() {
		
		// 联系人弹出窗口
		mPopupViewGroup = (ViewGroup) mInflater.inflate(R.layout.email_contacts_info, null);
		
		mContactImage = (ImageView) mPopupViewGroup
							.findViewById(R.id.contacts_image);
		mContactName = (TextView) mPopupViewGroup
							.findViewById(R.id.contacts_name);
		mContactCompany = (TextView) mPopupViewGroup
							.findViewById(R.id.contacts_company);
		mContactDepartment = (TextView) mPopupViewGroup
							.findViewById(R.id.contacts_department);
		
		mContactWrite = (TextView) mPopupViewGroup.findViewById(R.id.operation_write);
		mContactWrite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mPopupWindow.dismiss();
				
				ArrayList<User> users = new ArrayList<User>();
				users.add(mCurrentSelectUser);
				
				Intent intent = new Intent(getActivity(), ComposeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(ComposeActivity.EMAIL_OPERATION_KEY, ComposeActivity.OPERATION_NEW);
				bundle.putSerializable(ComposeActivity.EMAIL_WRITE_TO_KEY, users);
				
				intent.putExtras(bundle);
				startActivityForResult(intent, EMAIL_FORWORD);
			}
		});
		mContactRelative = (TextView) mPopupViewGroup
							.findViewById(R.id.operation_relative_email);
		mContactRelative.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				
				sendMessage(SHOW_TOAST, getResources().getString(R.string.email_no_implement));
			}
		});
		mContactDetail = (TextView) mPopupViewGroup.findViewById(R.id.operation_detail);
		mContactDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				
				mCurrentSelectTextView.setBackground(getResources()
							.getDrawable(R.drawable.email_contact_yellow));
				
				mDetailContactName.setText(mCurrentSelectUser.getName());
				mDetailContactCompany.setText(TenantCache
						.getTenantName(String.valueOf(mCurrentSelectUser.getTenant_id())));
				mDetailContactDepartment.setText(getObsName(mCurrentSelectUser));
				mDetailContactSex.setText(mCurrentSelectUser.getSex());
				mDetailContactEmail.setText(mCurrentSelectUser.getEmail());
				mDetailContactMobile.setText(mCurrentSelectUser.getMobile());
				mDetailContactOfficePhone.setText(mCurrentSelectUser.getOffice_phone());
				mDetailContactQQ.setText("");
				
				mDetailPopupWindow.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						mCurrentSelectTextView.setBackground(getResources()
								.getDrawable(R.drawable.email_contact_gray));
					}
				});
				
				mDetailPopupWindow.showAsDropDown(mCurrentSelectTextView, 
						-((mDetailPopupWindow.getWidth() - mCurrentSelectTextView.getWidth()) / 2) , 10);
			}
		});
		
		mPopupWindow = new PopupWindow(mPopupViewGroup, 360, 180, true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
	/**
	 * 初始化联系人详细信息
	 */
	@SuppressWarnings("deprecation")
	private void initContactDetailPopupWindow() {
		// 联系人弹出窗口
		mDetailPopupViewGroup = (ViewGroup) mInflater.inflate(R.layout.email_contacts_detail_info, null);
		
		mDetailContactImage = (ImageView) mDetailPopupViewGroup
							.findViewById(R.id.contacts_image);
		mDetailContactName = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contacts_name);
		mDetailContactCompany = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contacts_company);
		mDetailContactDepartment = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contacts_department);
		mDetailContactSex = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contact_sex);
		mDetailContactEmail = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contact_mailbox);
		mDetailContactMobile = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contact_mobile);
		mDetailContactOfficePhone = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contact_office_phone);
		mDetailContactQQ = (TextView) mDetailPopupViewGroup
							.findViewById(R.id.contact_qq);
		
		mDetailPopupWindow = new PopupWindow(mDetailPopupViewGroup, 320, 360, true);
		mDetailPopupWindow.setOutsideTouchable(true);
		mDetailPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
	/**
	 * 切换内容视图，当选择邮件时会刷新视图
	 */
	private void switchContent() {
		
		// 清除动态添加的容器子控件
		clearDynamicAddWidget();
		
		// 设置控件可视化
		setWidgetVisable();
		
		// 设置邮件实体内容
		if (mCurrentListItem != null) {
			setEmailContent();
		}
	}
	
	/**
	 * 清除动态添加的容器子控件
	 */
	private void clearDynamicAddWidget() {
		mEmailRecievers.removeAllViews();
		mEmailCcs.removeAllViews();
		mEmailAttachmentGroup.removeAllViews();
	}
	
	/**
	 * 设置控件可视化
	 */
	private void setWidgetVisable() {
		
		if (!mViewMode) {
			// 初始化浮动菜单
			initFloatingMenu();
		}

		// 如果为空表示第一次进来，邮件内容只显示LOGO
		if (mCurrentListItem == null) {
			mContentLayout.setVisibility(View.GONE);
			CustomTranslateAnimation.showRight(mEmailLogo, 200);
			
			if (mFloatingMenu != null) {
				CustomTranslateAnimation.showRight(mFloatingMenu);
			}
		} else {
		
			// 已经选中邮件
			CustomTranslateAnimation.showLeft(mContentLayout);
			
			if (mFloatingMenu != null) {
				CustomTranslateAnimation.showLeft(mFloatingMenu);
			}
		}
	}
	
	/**
	 * 设置邮件实体内容
	 */
	private void setEmailContent() {
		
		// 邮件标题
		mEmailTitle.setText(mCurrentListItem.getTitle());
		
		// 邮件头
		mEmailSender.setText(mUserMaps.get(String.valueOf(mCurrentListItem.getSender())));
		buildRecieversGroup(mCurrentListItem, false);
		buildRecieversGroup(mCurrentListItem, true);
		mEmailDate.setText(DateUtils
				.dateToString(DateUtils.FORMAT_LONG, mCurrentListItem.getMail_time()));
		mEmailSize.setText(getShowSize(MailBoxUtils
				.getEmailSize(mCurrentListItem, mAttachmentList)));
		
		// 附件
		if (mCurrentListItem.getAttachment() != null
				&& !mCurrentListItem.getAttachment().isEmpty()) {
			RemoteCommonService.getInstance().getAttachments(new DataManagerInterface() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
						mAttachmentList = (List<Files>) list;
						
						if (!mAttachmentList.isEmpty()) {
							mAttacmentBodyCut.setVisibility(View.VISIBLE);
							
							LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 
									LayoutParams.MATCH_PARENT);
							
							for (Files files : mAttachmentList) {
								ViewGroup attachmentGroup = buildAttachmentTextView(files);
								mEmailAttachmentGroup.addView(attachmentGroup, params);
								mAttachmentMap.put(attachmentGroup, files);
							}
						}
					}
				}
			}, mCurrentListItem.getAttachment());
		}
		
		// 添加合同变更
		if (mCurrentListItem.getMail_table_id() != 0 && mCurrentListItem.getMail_table_type() 
					== Integer.parseInt(GLOBAL.MAIL_TABLE_TYPE[0][0])) {
			RemoteChangeContractService.getInstance().getChangeContractDetail(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_QUERY:
						if (!list.isEmpty()) {
							mAttacmentBodyCut.setVisibility(View.VISIBLE);
							
							mContractChange = (Contract_change) list.get(0);
							mContractChange.setName(GLOBAL.MAIL_TABLE_TYPE[0][1] + "-" 
									+ mContractChange.getName());
							LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 
										LayoutParams.MATCH_PARENT);
							ViewGroup attachmentGroup = buildContractChangeTextView(mContractChange);
							mEmailAttachmentGroup.addView(attachmentGroup, params);
						} else {
							
							sendMessage(SHOW_TOAST, R.string.request_contract_had_been_deleted);
						}
						break;
					case AnalysisManager.EXCEPTION_DB_QUERY:
						sendMessage(SHOW_TOAST, R.string.request_contract_details_exception);
						break;
					default:
						break;
					}
				}
			}, mCurrentListItem.getMail_table_id(), UserCache.getTenantId());
		}
		
		if ((mCurrentListItem.getAttachment() == null
				|| mCurrentListItem.getAttachment().isEmpty())
				&& mCurrentListItem.getMail_table_id() == 0) {
			mAttacmentBodyCut.setVisibility(View.GONE);
			if (mAttachmentList != null) {
				mAttachmentList.clear();
			}
		}
		
		// 邮件正文
		mEmailBody.setText(mCurrentListItem.getContent());
	}
	
	/**
	 * 构建附件文本视图
	 * @param fileName
	 * @param size
	 * @return
	 */
	private ViewGroup buildAttachmentTextView(Files attachment) {
		final ViewGroup emailAttachment = (ViewGroup) mInflater.inflate(R.layout.email_attachment, null);
		final TextView textView = (TextView) emailAttachment
						.findViewById(R.id.email_attachment_text);
		final TextView attachmentSize = (TextView) emailAttachment
						.findViewById(R.id.email_attachment_size);
		
		// 配置文本视图控件
		textView.setText(attachment.getFile_name());

		// 设置左边的附件文件类型图标
		Drawable drawable = getResources()
				.getDrawable(FileIconHelper.getIconByFilename(attachment.getFile_name()));
		drawable.setBounds(0, 0, drawable.getMinimumWidth() * 2 / 3,
				drawable.getMinimumHeight() * 2 / 3);
		textView.setCompoundDrawables(drawable, null, null, null);
		
		attachmentSize.setText("(" + getShowSize(attachment.getFile_size()) + ")");
		
		emailAttachment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View view) {
				mCurrentAttachmentViewGroup = (ViewGroup) view;
				
				int[] location = new int[2];  
		        view.getLocationOnScreen(location);
		        view.setBackground(getResources()
		        		.getDrawable(R.drawable.email_yellow_2_shap));
		        
		        mAttachmentPopupWindow.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						view.setBackgroundColor(getResources().getColor(R.color.white));
					}
				});
//				mAttachmentPopupWindow.showAtLocation(view, 
//						Gravity.NO_GRAVITY,
//						location[0] + ((view.getWidth() - mAttachmentPopupWindow.getWidth()) / 2),
//						location[1] - mAttachmentPopupWindow.getHeight() - 10);
				mAttachmentPopupWindow.showAsDropDown(view, 0, 4);
			}
		});

		return emailAttachment;
	}
	
	/**
	 * 构建合同变更控件
	 * @param contractChange
	 * @return
	 */
	private ViewGroup buildContractChangeTextView(Contract_change contractChange) {
		final ViewGroup emailAttachment = (ViewGroup) mInflater.inflate(R.layout.email_attachment, null);
		final TextView textView = (TextView) emailAttachment
						.findViewById(R.id.email_attachment_text);
		final TextView attachmentSize = (TextView) emailAttachment
						.findViewById(R.id.email_attachment_size);
		
		// 配置文本视图控件
		textView.setText(contractChange.getName());

		// 设置左边的附件文件类型图标
		Drawable drawable = getResources()
				.getDrawable(FileIconHelper.getIconByFilename(contractChange.getName()));
		drawable.setBounds(0, 0, drawable.getMinimumWidth() * 2 / 3,
				drawable.getMinimumHeight() * 2 / 3);
		textView.setCompoundDrawables(drawable, null, null, null);
		
		attachmentSize.setText("");
		
		emailAttachment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View view) {
				mCurrentAttachmentViewGroup = (ViewGroup) view;
				
				int[] location = new int[2];  
		        view.getLocationOnScreen(location);
		        view.setBackground(getResources()
		        		.getDrawable(R.drawable.email_yellow_2_shap));
		        
		        mAttachmentPopupWindow.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						view.setBackgroundColor(getResources().getColor(R.color.white));
					}
				});
//				mAttachmentPopupWindow.showAtLocation(view, 
//						Gravity.NO_GRAVITY,
//						location[0] + ((view.getWidth() - mAttachmentPopupWindow.getWidth()) / 2),
//						location[1] - mAttachmentPopupWindow.getHeight() - 10);
				mAttachmentPopupWindow.showAsDropDown(view, 0, 4);
				
				mIsContractChange = true;
			}
		});

		return emailAttachment;
	}
	
	/**
	 * 获取显示大小字符串
	 * @param size
	 * @return
	 */
	private String getShowSize(long size) {
		DecimalFormat format = new DecimalFormat(".0");
		if (size >= SIZE_GB) {
			return format.format((double) size / SIZE_GB) + " GB";
		} else if (size >= SIZE_MB) {
			return format.format((double) size / SIZE_MB) + " MB";
		} else {
			return format.format((double) size / SIZE_KB) + " KB";
		}
	}
	
	/**
	 * 构建接收人组件
	 * @param recievers
	 * @param isCc
	 */
	private void buildRecieversGroup(MailBox mailBox, boolean isCc) {
		List<String> userIds = MailBoxUtils.getRecieverIds(mailBox, isCc);
		
 		if (userIds.isEmpty()) {
 			ViewContainer container = isCc ? mEmailCcs : mEmailRecievers;
 			((View) container.getParent()).setVisibility(View.GONE);
		} else {
			TextView textView = null;
			ViewContainer container = isCc ? mEmailCcs : mEmailRecievers;
			((View) container.getParent()).setVisibility(View.VISIBLE);
			
			for (String userId : userIds) {
				User user = ContactCache.getContact(userId);
				if (user != null) {
					textView = buildRecieverTextView(user);
					container.addView(textView);
				}
			}
		}
	}
	
	/**
	 * 构建接收人文本视图
	 * @param user
	 * @return
	 */
	private TextView buildRecieverTextView(final User user) {
		if (user == null) {
			return null;
		}
		
		android.view.ViewGroup.LayoutParams params 
					= new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		final TextView textView = new TextView(getActivity());
		
		// 配置文本控件
		textView.setLayoutParams(params);
		textView.setText(user.getName());
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
                .getDimensionPixelSize(R.dimen.sp16_s));
		textView.setPadding(0, 1, 0, 0);
		textView.setBackground(getResources().getDrawable(R.drawable.email_contact_gray));
		textView.setGravity(Gravity.CENTER);
		
		// 设置监听
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mCurrentSelectUser = user;
				mCurrentSelectTextView = textView;
						
				mContactName.setText(user.getName());
				mContactCompany.setText(TenantCache
						.getTenantName(String.valueOf(user.getTenant_id())));
				mContactDepartment.setText(getObsName(user));
				
				textView.setBackground(getResources()
						.getDrawable(R.drawable.email_contact_yellow));
				mPopupWindow.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						textView.setBackground(getResources()
								.getDrawable(R.drawable.email_contact_gray));
					}
				});
				
				mPopupWindow.showAsDropDown(view, 
						-((mPopupWindow.getWidth() - view.getWidth()) / 2) , 10);
			}
		});
		
		return textView;
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
	 * 初始化浮动菜单，可定制重写该方法
	 */
	protected void initFloatingMenu() {
		
		if (mFloatingMenu != null) {
			mFloatingMenu.clear();
		}
		
		// 获取浮动菜单文字和图标
		TypedArray typedArray = null;
		
		if (mCurrentListItem != null) {
			if (mCurrentBox != EMAIL_DRAFTBOX) {
				mFloatingMenuTips = getResources()
						.getStringArray(R.array.email_floating_menu_tips);
				typedArray = getResources()
		        		.obtainTypedArray(R.array.email_floating_menu_icons);
			} else {
				mFloatingMenuTips = getResources()
						.getStringArray(R.array.email_edit_tips);
				typedArray = getResources()
		        		.obtainTypedArray(R.array.email_edit_icons);
			}
		} else {
			mFloatingMenuTips = getResources()
					.getStringArray(R.array.email_write_tips);
			typedArray = getResources()
	        		.obtainTypedArray(R.array.email_write_icons);
		}
		
        mFloatingMenuImages = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
        	mFloatingMenuImages[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        
		// 提取资源信息
		if (mFloatingMenuImplement != null) {
			setFloatingMenuImages(mFloatingMenuImplement.getFloatingMenuImages());
			setFloatingMenuTips(mFloatingMenuImplement.getFloatingMenuTips());
		}
        mFloatingMenu = (FloatingMenuView) 
        		mRootLayout.findViewById(R.id.floating_menu);
        
        // 初始化浮动菜单项
        for (int i = 0; i < mFloatingMenuTips.length; i++) {
            mFloatingMenu.addPopItem(mFloatingMenuTips[i], mFloatingMenuImages[i]);
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
			mFloatingMenuImages = menuImages;
		}
	}
	
	/**
	 * 设置浮动菜单提示
	 * @param menuTips
	 */
	private void setFloatingMenuTips(String[] menuTips) {
		if (menuTips != null) {
			mFloatingMenuTips = menuTips;
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
			 mFloatingMenu.dismiss();
			 
			 Intent intent = new Intent(getActivity(), ComposeActivity.class);
        	 Bundle bundle = new Bundle();
        	 
    		 switch (position) {
    		 	case EMAIL_WRITE:
	            	 bundle.putInt(ComposeActivity.EMAIL_OPERATION_KEY,
	            			 				ComposeActivity.OPERATION_NEW);
	                 break;
    		 	case EMAIL_EDIT:
                	 bundle.putInt(ComposeActivity.EMAIL_OPERATION_KEY,
                			 				ComposeActivity.OPERATION_EDIT);
                	 bundle.putSerializable(ComposeActivity.EMAIL_ATTACHMENT_KEY,
                			 				(ArrayList<Files>) mAttachmentList);
            	 	break;
                 
                case EMAIL_REPLY:
                	 bundle.putInt(ComposeActivity.EMAIL_OPERATION_KEY,
                			 				ComposeActivity.OPERATION_REPLY);
                	 break;
                case EMAIL_REPLY_ALL:
                	 bundle.putInt(ComposeActivity.EMAIL_OPERATION_KEY,
                			 				ComposeActivity.OPERATION_REPLY_ALL);
                	 break;
                case EMAIL_FORWORD:
                	 bundle.putInt(ComposeActivity.EMAIL_OPERATION_KEY,
                			 				ComposeActivity.OPERATION_FORWORD);
                	 bundle.putSerializable(ComposeActivity.EMAIL_ATTACHMENT_KEY,
 			 								(ArrayList<Files>) mAttachmentList);
                	 break;
                default:
                     break;
    		 }
    		 bundle.putInt(ComposeActivity.EMAIL_MAILBOX_KEY, mCurrentBox);
             bundle.putSerializable(ComposeActivity.EMAIL_OBJECT_BEAN_KEY, mCurrentListItem);
             intent.putExtras(bundle);
             startActivityForResult(intent, position);
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
	 * 显示域重映射
	 * @param displayFieldMap
	 * @param t
	 * @param position
	 */
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
			T t, int position) {
		
		// 重映射状态
		for (int i = 1; i < mDisplayFields.length; i++) {
			if (mDisplayFieldSwitchMap != null
					&& mDisplayFieldSwitchMap.containsKey(mDisplayFields[i])) {
				String value = displayFieldMap.get(mDisplayFields[i]);
				String result = null;
				if (value.contains(",") || mDisplayFields[i].equals("receiver")) {
					List<String> values = null;
					if (mDisplayFields[i].equals("receiver")) {
						if (t.getReceiver().equals("")) {
							displayFieldMap.put(mDisplayFields[i],
									"<" + getString(R.string.email_no_receiver) + ">");
							continue;
						}
						
						values = MailBoxUtils.getRecieverIds(t, false);
						if (values.isEmpty()) {
							values = MailBoxUtils.getRecieverIds(t, true);
						}
					} else {
						values = MailBoxUtils.getLeaderUserIds(value);
					}
					
					StringBuffer sb = new StringBuffer();
					for (String v : values) {
						String res = mDisplayFieldSwitchMap.get(mDisplayFields[i]).get(v);
						sb.append(res + ",");
					}
					result = sb.substring(BASE_POSITION, sb.length() - 1).toString();
				} else {
					result = mDisplayFieldSwitchMap.get(mDisplayFields[i]).get(value);
				}
				displayFieldMap.put(mDisplayFields[i], result);
			}
		}
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
		                
		                // 进度对话框
		                sendMessage(SHOW_PROGRESS_DIALOG);
		                
		                mRemoveList.addAll(mListAdapter.getSelectedDatas());
		                
		                // 开始循环删除材料，普通模式和批量删除模式一样处理
		                for (int i = 0; i < mRemoveList.size(); i++) {
	                    	T listItem = mRemoveList.get(i);
	                    	mServiceImplement.deleteItem(listItem);
	                    }

		                // 将当前列表项置空
		                mCurrentListItem = null;
	                    
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
	 * 加载数据
	 */
	protected void loadData() {
		sendEmptyMessageDelayed(SHOW_PROGRESS_DIALOG);
		mServiceImplement.getListData();
		CacheObs.load(false);
	}
	
	/**
	 * 获取指定OBSid的名称
	 * @param user
	 * @return
	 */
	protected String getObsName(User user) {
		return CacheObs.getObsName(user.getObs_id());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// 当返回OK时，刷新邮件列表
		if (resultCode == Activity.RESULT_OK) {
			mServiceImplement.getListData();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 确认/取消 按钮
	 */
	private void initActionButton() {
	    View buttonLayout = (View) mRootLayout.findViewById(R.id.button_bar);
	    buttonLayout.setVisibility(View.VISIBLE);
	    
	    Button ok = (Button) mRootLayout.findViewById(R.id.btn_ok);
	    Button cancel = (Button) mRootLayout.findViewById(R.id.btn_cancel);
	    
        ok.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
                Serializable result = null;
                List<T> data = null;
                if (mCurrentMode == OperationMode.SINGLE_SELECT) {
                	data = mListAdapter.getSelectedDatas();
                    if (data.size() == 1) {
                        result = data.get(BASE_POSITION);
                    }
                } else {
                	data = (List<T>) mSelectedDataList;
                    if (data != null && data.size() > 0) {
                        result = (Serializable) data;
                    }
                }
                ((ListSelectActivity) getActivity()).setSeletctedData(result);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
            	((ListSelectActivity) getActivity()).setSeletctedData(null);
            }
        });
        
        if (mViewMode) {
        	ok.setVisibility(View.GONE);
        	cancel.setText(getActivity().getString(R.string.close));
        }
    }
	
	/**
	 * 邮件服务接口
	 * @author yuanlu
	 *
	 */
	public interface MailServiceInterface extends ServiceInterface<MailBox> {
		
		/**
		 * 读邮件接口
		 * @param mailBox
		 */
		public void read(MailBox mailBox);
	}
}