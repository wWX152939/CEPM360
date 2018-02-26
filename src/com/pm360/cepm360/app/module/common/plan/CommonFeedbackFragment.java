package com.pm360.cepm360.app.module.common.plan;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SHCommonListInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagerInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.CEPMDatePickerDialog;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.module.schedule.TaskSharedWithNodeFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.core.combination.ZH_TaskPresenter;
import com.pm360.cepm360.entity.Feedback;
import com.pm360.cepm360.entity.FeedbackCell;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.ShareTask;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.group.RemoteShareTaskService;
import com.pm360.cepm360.services.group.RemoteTaskService;
import com.pm360.cepm360.services.system.RemoteUserService;

/**
 * 重载该类，主要实现initMsgData 初始化project和mStringArray, mFragments数据，调用init方法传入标示1 组合 2
 * 计划 标题: CommonTaskFeedbackFragment 描述: 作者： onekey 日期： 2016年1月9日
 * 
 * @param <T>
 * @param <B>
 */
public abstract class CommonFeedbackFragment<T extends TaskCell & Serializable, B extends FeedbackCell>
		extends BaseTreePlanFragment<T> {
	// 组合对象
	private ExcelExportDialog mExcelExportDialog;
	
	private GanttDialog<T> mDanttDialog;

	// 数据库数据
	private final String NAME = "name";
	private final String PLAN_DURATION = "plan_duration";
	private final String START_TIME = "start_time";
	private final String END_TIME = "end_time";
	private final String STATUS = "status";
	private final String ACT_DURATION = "actual_duration";
	private final String ACT_START_TIME = "actual_start_time";
	private final String ACT_END_TIME = "actual_end_time";
	private final String PROGRESS = "progress";
	private final String OWNER = "owner";
	private final String DEPARTMENT = "department";
	private final String TYPE = "type";
	private final String CC_USER = "cc_user";
	private final String[] mTitleString = new String[] { NAME, PLAN_DURATION,
			START_TIME, END_TIME, STATUS, ACT_DURATION, ACT_START_TIME,
			ACT_END_TIME, PROGRESS, OWNER, DEPARTMENT, TYPE };
	private final String[] mDialogString = new String[] { ACT_START_TIME,
			ACT_END_TIME, PROGRESS, CC_USER };

	protected CepmApplication CepmApp;
	protected ZH_group mMsgGroupData;
	private View mSwitchView;

	// For viewPager
	@SuppressWarnings("rawtypes")
	protected List<TaskRelevanceChildInterface> mFragments;
	protected String[] mStringArray;

	// startActivityForResult flag
	public static final int OWNER_SELECT_REQUEST = 102;
	public static final int OBS_SELECT_REQUEST = 103;
	public static final int ATTR_OBS_SELECT_REQUEST = 104;

	// 当前行
	private int mLine;

	@SuppressWarnings("rawtypes")
	private ZH_TaskPresenter mTaskPresenter;

	// 缓存
	private User mOwner;
	private OBS mOBS;

	private boolean mHideFlag = false;
	private int[] mItemIdsline;

	private int mCurrentFlag = 0;
	private final int FEEDBACK_FLAG = 0x1;
	private final int TASK_MAKE_FLAG = 0x2;
	private final int TASK_SHARED_FLAG = 0x4;
	private List<T> mPlanList;
	private List<ShareTask> mShareList;
	private List<B> mFeedbackList = new ArrayList<B>();
	private CEPMDatePickerDialog mCEPMDatePickerDialog;
	private Class<T> mClassItem;
	private B mUpdateFeedbackInfoBean;
	private OnItemClickListener mOnItemClickListener;

	private int mType; // 1 组合 2 计划

	private RemoteTaskService mGroupService;
	private com.pm360.cepm360.services.plan.RemoteTaskService mTaskService;

	private int mTaskId;
	
	private int mMsgType;
	
	private List<Tenant> mTenantList;
	private List<User> mTenantUserList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initMsgData(initMessageCenterData());
		initEnvironment();
		init(mClassItem, true, listInterface, serviceInterface,
				floatingMenuInterface, null, dialogInterface,
				viewPagerInterface);
		enableOptionMenu(false);
		View view = super.onCreateView(inflater, container, savedInstanceState);
		initLastTextView();
		return view;
	}
	
	public void initLastTextView() {

		if (mListHeaderItemIds == null || mListHeaderItemIds.length == 0) {
			return;
		}
			
		TextView tv = (TextView) 
				mListHeader.findViewById(mListHeaderItemIds[mListHeaderItemIds.length - 1]);
		if (tv != null) {
			tv.setGravity(Gravity.CENTER_VERTICAL);
			int left = UtilTools.dp2pxW(mActivity, 15);
			tv.setPadding(left, 0, 0, 0);
		}
	}

	FloatingMenuInterface floatingMenuInterface = new FloatingMenuInterface() {

		@Override
		public int[] getFloatingMenuImages() {
			return new int[] { R.drawable.icon_export, R.drawable.icon_gantt};
		}

		@Override
		public String[] getFloatingMenuTips() {
			return new String[] { getActivity().getResources().getString(
					R.string.export), getActivity().getResources().getString(
							R.string.gantt_f) };
		}

		@Override
		public OnItemClickListener getFloatingMenuListener() {
			mOnItemClickListener = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					mFloatingMenu.dismiss();
					switch (arg2) {
					case 0:
						List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
						for (int i = 0; i < mListAdapter.getDataList().size(); i++) {
							mapList.add(mExcelExportDialog.excelFeedbackToMap(ObsCache
									.getObsIdMaps(), UserCache.getUserMaps(),
									mListHeaderNames, mListAdapter
											.getDataList().get(i)));
						}
						
						String filePath = "mnt/sdcard/CEPM360/"+"计划导出/";
						String fileName = mProject.getName()+"_计划导出(反馈)";
						
						mExcelExportDialog.show(filePath, fileName, mapList, mListHeaderNames);

						break;
					case 1: // gantt
						if (mDanttDialog != null && !mListAdapter.getDataList().isEmpty()) {
							mDanttDialog.showGanttDialog();
						} else {
							Toast.makeText(mActivity, R.string.pls_publish_task, Toast.LENGTH_SHORT).show();
						}
						
						break;
					default:
						break;
					}
				}

			};
			return mOnItemClickListener;
		}

	};

	protected abstract int getFlag();
	
	protected boolean isFeedbackPermission() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	protected void initClassType(int flag) {
		mType = flag;
		if (mType == 1) {
			mClassItem = (Class<T>) ZH_group_task.class;
		} else if (mType == 2) {
			mClassItem = (Class<T>) Task.class;
		}
	}

	private Bundle initMessageCenterData() {
		Bundle bundle1 = getArguments();
		if (bundle1 != null) {
			LogUtil.i("wzw =======" + bundle1.getInt("taskId"));
			mTaskId = bundle1.getInt("taskId");
			mMsgType = bundle1.getInt("type");
		}
		return bundle1;
	}

	protected abstract void initMsgData(Bundle bundle);

	private Handler mGanttHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			mDanttDialog = new GanttDialog<>(mActivity, mListAdapter.getDataList());
		}
	};
	
	/**
	 * same as doExtraGetServerData
	 * 
	 * @param flag
	 */
	private void handleServerData(int flag) {
		mCurrentFlag |= flag;
		if (mCurrentFlag == (FEEDBACK_FLAG | TASK_MAKE_FLAG | TASK_SHARED_FLAG)) {
			mCurrentFlag = 0;
			formatFeedbackList();
			mListAdapter.setDataList(mPlanList);
			
			mGanttHandler.sendEmptyMessageAtTime(0, 1000);
			
			handlerMessageCenterData();
		}
	}

	@SuppressLint("UseSparseArrays")
	/**
	 * 计算定位的节点所有的父，以父父->父->子关系存入列表
	 * @param list
	 * @return
	 */
	private ArrayList<T> initLevelTask(ArrayList<T> list) {
		T task = null;
		if (mTaskId != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getTask_id() == mTaskId) {
					task = list.get(i);
				}
			}
		}
		if (task == null) {
			return null;
		}
		int level = task.getLevel();
		Map<Integer, ArrayList<T>> taskMap = new HashMap<Integer, ArrayList<T>>();
		for (int i = 0; i < level; i++) {
			ArrayList<T> tasklist = new ArrayList<T>();
			taskMap.put(i, tasklist);
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLevel() < level) {
				for (int j = 0; j < level; j++) {
					if (list.get(i).getLevel() == j) {
						taskMap.get(j).add(list.get(i));
						break;
					}
				}
			}

		}
		ArrayList<T> levelList = new ArrayList<T>();
		levelList.add(task);
		for (int i = level - 1; i >= 0; i--) {
			for (T tasktmp : taskMap.get(i)) {
				if (tasktmp.getTask_id() == levelList.get(level - i - 1)
						.getParents_id()) {
					levelList.add(tasktmp);
					break;
				}
			}
		}

//		if (true) {
//			LogUtil.i("taskMap:" + taskMap);
//			LogUtil.i("levelList:" + levelList);
//		}

		return levelList;
	}

	private void updateListView(ArrayList<T> levelList) {
		if (levelList == null)
			return;
		int selectLine = 0;
		List<T> taskNode = mListAdapter.getShowList();
		for (int i = levelList.size() - 1; i >= 0; i--) {
			for (int j = 0; j < taskNode.size(); j++) {
				if (levelList.get(i).getTask_id() == taskNode.get(j)
						.getTask_id()) {
					mListAdapter.updateListView(j);
					selectLine = j;
					break;
				}
			}
		}
		LogUtil.i("wzw select:" + selectLine);
		mListAdapter.setSelected(selectLine, true);
		handleClickWithTextView(selectLine, null);
	}

	private void handlerMessageCenterData() {
		setChildData(mListAdapter, mFeedbackList);
		if (mTaskId != 0) {
			updateListView(initLevelTask((ArrayList<T>) mPlanList));
		}

	}

	private void setChildData(DataTreeListAdapter<T> listAdapter, List<B> list) {
		mBaseViewPager.setParentList(listAdapter);
		mBaseViewPager.setCurrentList(list);
	}

	@SuppressWarnings("unchecked")
	private void formatFeedbackList() {
		for (int i = 0; i < mFeedbackList.size(); i++) {
			for (int j = 0; j < mPlanList.size(); j++) {
				if (mFeedbackList.get(i).getTask_id() == mPlanList.get(j)
						.getTask_id()) {
					mPlanList.get(j).setActual_start_time(
							mFeedbackList.get(i).getActual_start_time());
					mPlanList.get(j).setActual_end_time(
							mFeedbackList.get(i).getActual_end_time());
					mPlanList.get(j).setProgress(
							mFeedbackList.get(i).getProgress());
					mPlanList.get(j).setCc_user(
							mFeedbackList.get(i).getCc_user());
					mPlanList.get(j)
							.setStatus(mFeedbackList.get(i).getStatus());
					mPlanList.get(j).setMark(mFeedbackList.get(i).getMark());
				}
			}
		}
		if (mShareList != null && !mShareList.isEmpty()) {

			for (ShareTask shareTask : mShareList) {
				ZH_group_task task = TaskSharedWithNodeFragment.switchToGroupTask(shareTask);
				task.setParents_id(shareTask.getMount_id());
				task.setDepartment(shareTask.getOut_company());
				task.setChange_id("-1");
				mPlanList.add((T) task);
			}
		}
	}

	private ArrayList<T> initTaskList(ArrayList<T> list) {
		ArrayList<T> tasklist = list;
		for (int i = 0; i < tasklist.size(); i++) {
			int parent = tasklist.get(i).getParents_id();
			for (int j = 0; j < tasklist.size(); j++) {
				if (parent == tasklist.get(j).getTask_id()) {
					int level = tasklist.get(j).getLevel() + 1;
					tasklist.get(i).setLevel(level);
					tasklist.get(j).setHas_child(true);
				}
			}
		}
		if (mCurrentItem != null) {
			for (int i = 0; i < tasklist.size(); i++) {
				if (tasklist.get(i).getTask_id() == mCurrentItem.getTask_id()) {
					mCurrentItem = tasklist.get(i);
				}
			}
		}
		return tasklist;
	}

	@Override
	protected void doExtraEventWithViewPermission() {
		super.doExtraEventWithViewPermission();
		// mFloatingMenu.setVisibility(View.GONE);
		TypedArray typedArrayline = getResources().obtainTypedArray(
				R.array.task_feedback_line_ids);

		mItemIdsline = new int[4];

		for (int i = 0; i < 4; i++) {
			mItemIdsline[i] = typedArrayline.getResourceId(i, 0);
			TextView tv = (TextView) mListHeader
					.findViewById(mListHeaderItemIds[i + 1]);
			View line = (View) mListHeader.findViewById(mItemIdsline[i]);
			if (mHideFlag) {
				tv.setVisibility(View.GONE);
				line.setVisibility(View.GONE);
			}
		}
		typedArrayline.recycle();
		mSwitchView = mListHeader.findViewById(R.id.switch_button);
		mSwitchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mHideFlag = (mHideFlag == false) ? true : false;

				mListAdapter.notifyDataSetChanged();
				LogUtil.i("wzw mHideFlag:" + mHideFlag);

				for (int i = 0; i < 4; i++) {
					TextView tv = (TextView) mListHeader
							.findViewById(mListHeaderItemIds[i + 1]);
					View line = (View) mListHeader
							.findViewById(mItemIdsline[i]);
					if (mHideFlag) {
						LogUtil.i("wzw gone mHideFlag:" + mHideFlag);
						line.setVisibility(View.GONE);
						tv.setVisibility(View.GONE);

						// actual_button.setImageResource(R.drawable.left_arr);
					} else {

						LogUtil.i("wzw visible mHideFlag:" + mHideFlag);
						line.setVisibility(View.VISIBLE);
						tv.setVisibility(View.VISIBLE);
					}

				}
			}
		});

		mBaseViewPager.setChildProject(mProject);
	}

	@SuppressWarnings("rawtypes")
	private void initEnvironment() {
		initClassType(getFlag());
		mUpdateFeedbackInfoBean = getUpdateFeedback();
		mGroupService = RemoteTaskService.getInstance();
		mTaskService = com.pm360.cepm360.services.plan.RemoteTaskService
				.getInstance();
		
		if (mType == 2 || isFeedbackPermission()) {
			setPermissionIdentity(GLOBAL.SYS_ACTION[5][0],
					GLOBAL.SYS_ACTION[4][0]);
		} else if (mType == 1) {
			setPermissionIdentity(GLOBAL.SYS_ACTION[53][0],
					GLOBAL.SYS_ACTION[52][0]);
		}
		mExcelExportDialog = new ExcelExportDialog(getActivity());
		mTaskPresenter = new ZH_TaskPresenter(getActivity());

		CEPMDatePickerDialog.OnDateSetListener dateSetCallBack = new CEPMDatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
//				final TextView actualDurationTextView = mCurrentViewHolder.tvs[5], tvStatus = mCurrentViewHolder.tvs[4], tvProgress = mCurrentViewHolder.tvs[8];
				final SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd");

				final Calendar setdate = Calendar.getInstance();
				String time;
				setdate.set(Calendar.YEAR, year);
				setdate.set(Calendar.MONTH, monthOfYear);
				setdate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				time = format.format(setdate.getTime());

				if (requestData(mLine, mRequestUpdateType, time)) {

				}
			}
		};

		mCEPMDatePickerDialog = new CEPMDatePickerDialog(getActivity(),
				dateSetCallBack, "");
	}

	SHCommonListInterface<T> listInterface = new SHCommonListInterface<T>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemId(T t) {
			return t.getTask_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.task_feedback_title_list_item2;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.task_feedback_list_item2;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.task_feedback_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.task_feedback_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.task_feedback_listids;
		}

		@Override
		public int getListItemDoScrollIds() {
			// TODO Auto-generated method stub
			return R.array.task_feedback_doscroll_ids;
		}
	};

	private DataManagerInterface mTaskManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mDataLoaded = true;

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					mPlanList = initTaskList((ArrayList<T>) list);
					handleServerData(TASK_MAKE_FLAG);
				}
			}
			dismissDialog();
		}

	};
	
	private int mCount = 0;
	private void dismissDialog() {
		mCount++;
		if (mCount == 3) {
			mCount = 0;
			sendMessage(DISMISS_PROGRESS_DIALOG);
		}
	}

	private DataManagerInterface mFeedbackManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mDataLoaded = true;
			LogUtil.i("wzw status:" + status.getCode());

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				mFeedbackList = (ArrayList<B>) list;
				handleServerData(FEEDBACK_FLAG);

			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				MiscUtils.clone(mCurrentItem, mCurrentUpdateItem);
				// 修改成功后，更新viewPager常用界面
				handleChildEvent();
				mListAdapter.notifyDataSetChanged();
			}
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
			dismissDialog();
		}

	};
	
	private DataManagerInterface mShareManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mDataLoaded = true;

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				mShareList = (List<ShareTask>)list;
				LogUtil.i("wzw list:" + list);
				if (list != null && !list.isEmpty()) {
					String company = "";
					String users = "";
					for (ShareTask task : mShareList) {
						if (!company.contains(task.getOut_company() + "")) {
							company += task.getOut_company() + ",";
						}
						if (!users.contains(task.getOwner() + "")) {
							users += task.getOwner() + ",";
						}
					}
					if (company.endsWith(",")) {
						company = company.substring(0, company.length() - 1);
					}
					if (users.endsWith(",")) {
						users = users.substring(0, users.length() - 1);
					}
					final String tmpUsers = users;
					RemoteCommonService.getInstance().getTenantDetailByTenantIDs(new DataManagerInterface() {
						
						@Override
						public void getDataOnResult(ResultStatus status, List<?> list) {
							mTenantList = (List<Tenant>) list;
							RemoteUserService.getInstance().getUserDetailByUserIDs(new DataManagerInterface() {
								
								@Override
								public void getDataOnResult(ResultStatus status, List<?> list) {
									mTenantUserList = (List<User>) list;

									handleServerData(TASK_SHARED_FLAG);
								}
							}, tmpUsers);
						}
					}, company);
					
				} else {
					handleServerData(TASK_SHARED_FLAG);
				}
			}
			
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
			dismissDialog();
		}

	};

	DialogAdapterInterface dialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.plan_add_dialog;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.task_feedback_modify;
		}

		@Override
		public String[] getUpdateFeilds() {
			return mDialogString;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, Integer> getDialogStyles() {

			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(0, BaseDialog.calendarLineStyle);
			buttons.put(1, BaseDialog.calendarLineStyle);
			// buttons.put(2, BaseDialog.multiAutoCompleteTextViewStyle);
			buttons.put(2, BaseDialog.numberSeekBarLineStyle);

			return buttons;
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, String[]> getSupplyData() {

			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {

			final EditText startEt = (EditText) mDialog.getEditTextView(1);
			final EditText endEt = (EditText) mDialog.getEditTextView(2);
			final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
					getActivity(), null, startEt, endEt, null);
			OnClickListener dateListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mCurrentItem == null) {

					} else {
						if (mCurrentItem.getParents_id() != 0
								&& mCurrentItem.getStart_time() == null) {
							T taskParent = null;
							for (int i = mLine - 1; i >= 0; i--) {

								if (mListAdapter.getShowList().get(i)
										.getLevel() < mCurrentItem.getLevel()) {
									taskParent = mListAdapter.getShowList()
											.get(i);
									break;
								}
							}
							doubleDatePickerDialog.show(
									taskParent.getStart_time(),
									taskParent.getStart_time());
						} else {
							doubleDatePickerDialog.show(
									mCurrentItem.getStart_time(),
									mCurrentItem.getEnd_time());
						}
					}
				}
			};

			mDialog.setEditTextStyle(0, 0, dateListener, null);
			mDialog.setEditTextStyle(1, 0, dateListener, null);

		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {

			return null;
		}

	};

	@SuppressWarnings("rawtypes")
	ViewPagerInterface viewPagerInterface = new ViewPagerInterface() {

		@Override
		public String[] getViewPagerTitle() {
			return mStringArray;
		}

		@Override
		public List<TaskRelevanceChildInterface> getFragment() {
			return mFragments;
		}

	};

	protected abstract B getUpdateFeedback();

	/**
	 * 以下逻辑处理，判断时间
	 */

	@SuppressWarnings("unchecked")
	private void setRemoteFeedbackCache(T task) {

		mTaskPresenter.setRemoteFeedbackCache(mUpdateFeedbackInfoBean, task,
				mType, mProject, mPlanList, mFeedbackList);

		if (mType == 1) {
			mGroupService
					.setZH_group_feedback((ZH_group_feedback) mUpdateFeedbackInfoBean);
			mGroupService.updateFeedback(mFeedbackManager, UserCache.getCurrentUser());
		} else if (mType == 2) {
			mTaskService.setFeedback((Feedback) mUpdateFeedbackInfoBean);
			mTaskService.updateFeedback(mFeedbackManager, UserCache.getCurrentUser());
		}

	}

	protected ServiceInterface<T> serviceInterface = new ServiceInterface<T>() {

		@Override
		public void getListData() {
			if (mType == 1) {
				if (mMsgGroupData != null) {
					RemoteShareTaskService.getInstance().getShareTaskList(mShareManager, mMsgGroupData.getZh_group_id(), mMsgGroupData.getTenant_id(), 1);
					mGroupService.getPublishTaskList(mTaskManager,
							mMsgGroupData.getZh_group_id());
					mGroupService.getFeedbackList(mFeedbackManager,
							mMsgGroupData.getZh_group_id());
				} else {
					sendMessage(DISMISS_PROGRESS_DIALOG);
				}
				
			} else if (mType == 2) {
				if (mProject != null) {
					mTaskService.getPublishTaskList(mTaskManager, mProject);
					mTaskService.getFeedbackList(mFeedbackManager, mProject);	
				} else {
					sendMessage(DISMISS_PROGRESS_DIALOG);
				}
			}

		}

		@Override
		public void addItem(T t) {

		}

		@Override
		public void deleteItem(T t) {

		}

		@Override
		public void updateItem(T t) {

		}

	};

	private boolean requestData(int position, int type, String msg) {
		if (type == REQUEST_UPDATE_TASK) {
			// requestData
			mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
			mCurrentUpdateItem.setProgress(Integer.parseInt(msg));

			setRemoteFeedbackCache(mCurrentUpdateItem);

		} else if (type == REQUEST_UPDATE_ACTUAL_START_TIME) {
			Date startTime = DateUtils
					.stringToDate(DateUtils.FORMAT_SHORT, msg);
//			if (!mTaskPresenter.calculatorParentStartTime(startTime, position,
//					mCurrentItem, mListAdapter)) {
//				return false;
//			}
			mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
			if (mCurrentUpdateItem.getActual_end_time() == null
					|| mCurrentUpdateItem.getActual_end_time().equals("")) {
				mCurrentUpdateItem.setStatus(GLOBAL.FEEDBACK_STATUS_1);
			} else {
				String actualDuration = mTaskPresenter.calculateDuration(mCurrentUpdateItem.getActual_start_time(), mCurrentUpdateItem.getActual_end_time());
				mCurrentUpdateItem.setActual_duration(actualDuration);
			}
			mCurrentUpdateItem.setActual_start_time(startTime);
			setRemoteFeedbackCache(mCurrentUpdateItem);
		} else if (type == REQUEST_UPDATE_ACTUAL_END_TIME) {
			Date endTime = DateUtils.stringToDate(DateUtils.FORMAT_SHORT, msg);
//			boolean ret = mTaskPresenter.calculatorChildEndTime(
//					mCurrentItem.getActual_start_time(), endTime, mCurrentItem,
//					mListAdapter);
//
//			if (!ret) {
//				return false;
//			}
			mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
			mCurrentUpdateItem.setActual_end_time(endTime);
			mCurrentUpdateItem.setStatus(GLOBAL.FEEDBACK_STATUS_2);
			mCurrentUpdateItem.setActual_duration(mTaskPresenter.calculateDuration
					(mCurrentUpdateItem.getActual_start_time(), mCurrentUpdateItem.getActual_end_time()));
			mCurrentUpdateItem.setProgress(100);
			setRemoteFeedbackCache(mCurrentUpdateItem);
		}
		return true;

	}

	@SuppressLint("SimpleDateFormat")
	private void showDialogWindow(final ViewHolder viewHolder,
			final TextView tv, final int position, final int type) {
		// 画图
		Drawable drawable = getActivity().getResources().getDrawable(
				R.drawable.dialog_calendar);

		// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, 25, 25);
		tv.setCompoundDrawables(null, null, drawable, null);

		// 添加响应事件
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// 初始化全局变量
				mRequestUpdateType = type;
				mCurrentViewHolder = viewHolder;
				mLine = position;

				if (mListAdapter.getItem(mLine).getOwner() != UserCache.getCurrentUserId()) {
					BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
					return;
				}

				String dateTitle = "";
				Date initialDate = null;
				if (mRequestUpdateType == REQUEST_UPDATE_ACTUAL_START_TIME) {
					dateTitle = getActivity().getString(
							R.string.actual_start_time);
					initialDate = mListAdapter.getItem(mLine)
							.getActual_start_time();
				} else if (mRequestUpdateType == REQUEST_UPDATE_ACTUAL_END_TIME) {
					dateTitle = getActivity().getString(
							R.string.actual_end_time);
					initialDate = mListAdapter.getItem(mLine)
							.getActual_end_time();
				}

				mCurrentItem = mListAdapter.getItem(mLine);
				mCEPMDatePickerDialog.setTitle(dateTitle);
				mCEPMDatePickerDialog.show(initialDate);
			}
		});
	}

	@SuppressWarnings("unused")
	private void saveFunction(final TextView tvStatus,
			final TextView tvProgress, final int position) {
		Drawable drawable = getActivity().getResources().getDrawable(
				R.drawable.icon_modify);

		// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, 25, 25);
		tvProgress.setCompoundDrawables(null, null, drawable, null);
		tvProgress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mCurrentItem = mListAdapter.getItem(position);

				if (mListAdapter.getItem(position).getOwner() != UserCache.getCurrentUserId()) {
					BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
					return;
				}

				final Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_layout);

				Window dialogWindow = dialog.getWindow();
				dialogWindow.setGravity(Gravity.CENTER);
				dialog.show();

				TextView titleTextView = (TextView) dialog
						.findViewById(R.id.tv_title);
				final EditText dialog_et = (EditText) dialog
						.findViewById(R.id.dialog_edit_text);
				titleTextView.setText(R.string.feedback_edit_progress);

				Drawable drawable = getActivity().getResources().getDrawable(
						R.drawable.dialog_percent);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, 25, 25);
				dialog_et.setCompoundDrawables(null, null, drawable, null);

				if (tvProgress.getText() != null
						&& !tvProgress.getText().toString().equals("")) {
					String etString = tvProgress.getText().toString();
					dialog_et.setText(etString.substring(0,
							etString.length() - 1));
					dialog_et.setSelection(dialog_et.getText().length());
				}
				dialog_et.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
						if (s != null && !s.toString().equals("")) {
							int percent = Integer.parseInt(s.toString());
							if (percent > 100) {
								dialog_et.setText(Integer.toString(100));
							}
						}
					}
				});

				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						switch (arg0.getId()) {
						case R.id.cancel:
							dialog.cancel();
							break;
						case R.id.save:
							String msg = dialog_et.getText().toString();
							if (!msg.equals("")) {
								requestData(position, REQUEST_UPDATE_TASK, msg);
							}
							dialog.dismiss();
							break;
						}
					}
				};

				Button saveButton = (Button) dialog.findViewById(R.id.save);
				Button cacelButton = (Button) dialog.findViewById(R.id.cancel);
				saveButton.setOnClickListener(listener);
				cacelButton.setOnClickListener(listener);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0)
			return;
		if (requestCode == OWNER_SELECT_REQUEST) {
			EditText editText = (EditText) mDialog.getPopupView().findViewById(
					mDialog.baseEditTextId + 4);
			mOwner = (User) data.getSerializableExtra("user");
			editText.setText(mOwner.getName());
			// if (mOwner != null) {
			// if (mSwitchToAttrView) {
			// mMakeAttrSubView.setOwner(user.getName());
			// } else {
			// editText.setText(mOwner.getName());
			// }
			// } else {
			// if (mSwitchToAttrView) {
			// mMakeAttrSubView.setOwner("");
			// } else {
			// editText.setText("");
			// }
			// }
		} else if (requestCode == OBS_SELECT_REQUEST) {
			mOBS = (OBS) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mDialog.setEditTextContent(5, mOBS.getName());
		}
	}

	@Override
	protected void setListItemTextContent(ViewHolder viewHolder, Map<String, String> treeNodeMap, int i, int position) {
		super.setListItemTextContent(viewHolder, treeNodeMap, i, position);
		if (i == 1 || i == 2 || i == 3 || i == 4) {

			if (mHideFlag) {

				if (viewHolder.views[i - 1] != null) {
					viewHolder.views[i - 1].setVisibility(View.GONE);
				}

				viewHolder.tvs[i].setVisibility(View.GONE);

			} else {
				if (viewHolder.views[i - 1] != null) {
					viewHolder.views[i - 1].setVisibility(View.VISIBLE);
				}
				viewHolder.tvs[i].setVisibility(View.VISIBLE);

			}
		} else if (i == 5) {
			if (mListAdapter.getItem(position).getActual_start_time() != null && !mListAdapter.getItem(position).getActual_start_time().equals("")
					&& mListAdapter.getItem(position).getActual_end_time() != null && !mListAdapter.getItem(position).getActual_end_time().equals("")) {
				String duration = mTaskPresenter.calculateDuration(mListAdapter.getItem(position).getActual_start_time(), mListAdapter.getItem(position).getActual_end_time());
				viewHolder.tvs[i].setText(duration);
			}
		} else if (i == 9) {
			// owner
			
			if (mListAdapter.getItem(position).getChange_id().equals("-1")) {
				if (mTenantUserList != null) {
					String userName = "";
					for (User user : mTenantUserList) {
						if (user.getUser_id() == mListAdapter.getItem(position).getOwner()) {
							userName = user.getName();
							break;
						}
					}
					viewHolder.tvs[i].setText(userName);
				}
				
			}
		} else if (i == 10) {
			// tenant
			
			if (mListAdapter.getItem(position).getChange_id().equals("-1")) {
				if (mTenantList != null) {
					String tenantName = "";
					for (Tenant tenant : mTenantList) {
						if (tenant.getTenant_id() == mListAdapter.getItem(position).getDepartment()) {
							tenantName = tenant.getName();
							break;
						}
					}
					viewHolder.tvs[i].setText(tenantName);
				}
			}
		}
				
		if (mListAdapter.getItem(position).getChange_id().equals("-1")) {
			// 表明为shareTask
			viewHolder.tvs[i].setTextColor(Color.BLUE);
		}
		
//		if (UserCache.getCurrentUser().getUser_id() == mListAdapter.getItem(position).getOwner()) {
////			LogUtil.i("wzw r:" + viewHolder.tvs[0].getRootView() + " p:" + viewHolder.tvs[0].getParent().getParent());
//			((View)(viewHolder.tvs[0].getParent())).setBackgroundResource(R.color.aqua);
//			((View)(viewHolder.views[0].getParent())).setBackgroundResource(R.color.aqua);
//			for (int iterate : mListAdapter.getSelected()) {
//				if (iterate == position) {
//					((View)(viewHolder.tvs[0].getParent())).setBackgroundColor(Color.TRANSPARENT);
//					((View)(viewHolder.views[0].getParent())).setBackgroundColor(Color.TRANSPARENT);
//				}
//			}
//		}
	}

	@Override
	protected void doExtraInitLayout(View convertView, ViewHolder viewHolder) {
		viewHolder.views = new View[4];
		viewHolder.views[0] = convertView.findViewById(R.id.plan_duration_line);
		viewHolder.views[1] = convertView.findViewById(R.id.start_time_line);
		viewHolder.views[2] = convertView.findViewById(R.id.end_time_line);
		viewHolder.views[3] = convertView.findViewById(R.id.status_line);
		for (int i = 0; i < 4; i++) {
			viewHolder.tvs[i + 1].setVisibility(View.GONE);
			viewHolder.views[i].setVisibility(View.GONE);
		}

//		Drawable drawable = getResources().getDrawable(
//				R.drawable.dialog_calendar);
//		// x, y, width, height
//		drawable.setBounds(0, 0, 27, 27);
//		viewHolder.tvs[6].setCompoundDrawables(null, null, drawable, null);
//		viewHolder.tvs[7].setCompoundDrawables(null, null, drawable, null);
	}

	@Override
	public void displayFieldRemap(Map<String, String> displayFieldMap, T t,
			int position) {
		LogUtil.i("display T:" + t);
		displayFieldMap.put(
				OWNER,
				UserCache.getUserMaps()
						.get(displayFieldMap.get(OWNER)));
		displayFieldMap.put(DEPARTMENT, ObsCache.getObsIdMaps()
				.get(displayFieldMap.get(DEPARTMENT)));
		String status = "";
		if (t.getStatus() == (GLOBAL.FEEDBACK_STATUS_0)) {
			status = GLOBAL.FEEDBACK_STATUS_0_VALUE;
		} else if (t.getStatus() == (GLOBAL.FEEDBACK_STATUS_1)) {
			status = GLOBAL.FEEDBACK_STATUS_1_VALUE;
		} else if (t.getStatus() == (GLOBAL.FEEDBACK_STATUS_2)) {
			status = GLOBAL.FEEDBACK_STATUS_2_VALUE;
		}
		displayFieldMap.put(STATUS, status);
		String type = "";
		if (displayFieldMap.get(TYPE).equals(GLOBAL.TASK_TYPE_MILE_KEY)) {
			type = GLOBAL.TASK_TYPE_MILE_VALUE;
		} else if (displayFieldMap.get(TYPE).equals(GLOBAL.TASK_TYPE_TASK_KEY)) {
			type = GLOBAL.TASK_TYPE_TASK_VALUE;
		} else if (displayFieldMap.get(TYPE).equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
			type = GLOBAL.TASK_TYPE_WBS_VALUE;
		}
		displayFieldMap.put(TYPE, type);

		displayFieldMap.put(PROGRESS, displayFieldMap.get(PROGRESS) + "%");
	}

	// request data type
	public final int REQUEST_UPDATE_ACTUAL_START_TIME = 0;
	public final int REQUEST_UPDATE_ACTUAL_END_TIME = 1;
	public final int REQUEST_UPDATE_TASK = 2;
	private int mRequestUpdateType;

	@SuppressWarnings("unused")
	private ViewHolder mCurrentViewHolder;

	@Override
	protected void doExtraRegListener(ViewHolder viewHolder, final int position) {

		for (int i = 1; i < viewHolder.tvs.length; i++) {

			if (i == 2 || i == 3 || i == 6 || i == 7) {

			} else {
				viewHolder.tvs[i].setGravity(Gravity.CENTER_VERTICAL);
				viewHolder.tvs[i].setPadding(getResources()
						.getDimensionPixelSize(R.dimen.table_padding_left), 0,
						0, 0);
			}

//			if (mHasEditPermission) {
//				if (i == 6) {
//					showDialogWindow(viewHolder, viewHolder.tvs[6], position,
//							REQUEST_UPDATE_ACTUAL_START_TIME);
//					continue;
//				} else if (i == 7) {
//					showDialogWindow(viewHolder, viewHolder.tvs[7], position,
//							REQUEST_UPDATE_ACTUAL_END_TIME);
//					continue;
//				} else if (i == 8) {
//					saveFunction(viewHolder.tvs[4], viewHolder.tvs[i], position);
//					continue;
//				}
//			}

			viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					mListAdapter.setSelected(position, true);

					mCurrentItem = mListAdapter.getItem(position);

					if (mType == 1) {
						mGroupService.setTask((ZH_group_task) mCurrentItem);
					} else if (mType == 2) {
						mTaskService.setTask((Task) mCurrentItem);
					}
					// TODO

					// 点击更新viewPager反馈界面
					mLine = position;
					handleChildEvent();
				}
			});
		}

	}

	@Override
	/**
	 * 单击处理
	 * @param position
	 */
	public void handleClickWithShowOptionMenu(int position, View view) {

	}

	protected boolean enableProjectMenu() {
		if (mType == 2) {
			return true;
		} else {
			return false;
		}
	}
	
	// 加载数据
	protected void loadData() {
		LogUtil.i("wzw loadData:");
		// 加载数据之前先修改界面

		if (mMsgType == Integer
				.parseInt(GLOBAL.MSG_TYPE_KEY[8][0])
				|| mMsgType == Integer
						.parseInt(GLOBAL.MSG_TYPE_KEY[16][0])) {
			if (mSwitchView != null) {
				mSwitchView.performClick();
			}
		}
		
//		if (mFeedbackMsg.equals(FeedbackMsg.isFeedbackMsg)) {
//			handleMsgForSecondView();
//			mBaseViewPager.getTextView(1).performClick();
//			//pass document time to position
//		}

		sendMessage(SHOW_PROGRESS_DIALOG);
		// 加载列表数据
		serviceInterface.getListData();
	}
	
	protected boolean hasListItemData() {
		if (mListAdapter.getShowList() != null && !mListAdapter.getShowList().isEmpty()) {
			return true;
		} else {
			Toast.makeText(mActivity, R.string.pls_publish_task, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}
