package com.pm360.cepm360.app.module.common.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.ActionBarInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SHCommonListInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleMultiSelectInterface;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagerInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.CEPMDatePickerDialog;
import com.pm360.cepm360.app.common.view.parent.CEPMDatePickerDialog.OnDateSetListener;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.module.common.plan.TemplateDialog.TemplateDialogInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.core.TreePresenter;
import com.pm360.cepm360.core.combination.ZH_TaskPresenter;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.group.RemoteTaskService;


/**
 * @author onekey
 * 计划模块的进入有三种模式，正常模式，消息模式，门户模式
 * 1.正常模式，正常模式进入计划模块，通过权限展示所拥有的信息，包括显示和操作，界面属性一级目录计划编制和反馈，二级目录根据企业类型又有所不同
 * 在非管理方的企业属性项固定，计划有常用、参考文档属性，反馈有进度、现场图文、工作日志、安全监督、质量文明属性，管理房企业计划有常用，风控。反馈有四项属性，并且
 * 可以配置，进度、风险识别、工作日志和形象成果。
 * 正常模式数据流，group_id->taskList taskId->each attr list(documentIds)
 * 2.消息模式，目前消息模式我的任务可以进入计划模块，拥有和正常模式一样的查看界面，但最新反馈不会进入计划模块，只是弹出具体的属性。
 * 消息模式数据流,task_id->group_id->taskList
 * 3.门户模式，门户模式我的任务不会进入计划模块，只是查询该任务的属性（反馈属性），最新反馈同消息类似，只是弹出具体属性。
 * 门户模式数据流，group_id->taskList
 * 
 * 数据格式tenant_id->project_id->group_id->task_id/feedback_id->safe_id/quailty_id...->document
 * @param <T>
 */
public abstract class CommonMakeFragment<T extends TaskCell> extends
	BaseTreePlanFragment<T>{
	// 组合对象
	private ExcelExportDialog mExcelExportDialog;	

	private final String NAME = "name";
	private final String PLAN_DURATION = "plan_duration";
	private final String START_TIME = "start_time";
	private final String END_TIME = "end_time";
	private final String OWNER = "owner";
	private final String DEPARTMENT = "department";
	private final String TYPE = "type";
	private final String STATUS = "publish";

	// startActivityForResult flag
	public static final int OWNER_SELECT_REQUEST = 102;
	public static final int OBS_SELECT_REQUEST = 103;
	public static final int ATTR_OBS_SELECT_REQUEST = 104;
	public static final int MULTI_OWNER_SELECT_REQUEST = 105;

	// view
	private Dialog mInsertDialog;
	
	// 当前行
	protected int mLine;

	private ZH_TaskPresenter<T> mTaskPresenter;
	
	private int mType; // 1 zuhe 2 jihua
	private Class<T> mClassItem;
	private boolean isFirstAddStatus;

	@SuppressWarnings("rawtypes")
	protected List<TaskRelevanceChildInterface> mFragments;
	protected ZH_group mMsgGroupData;
	protected String[] mStringArray;
	
	private boolean mMileStoneFlag = false;
	
	// 消息过来的id
	private int mTaskId;
	
	/**
	 *  多选数据缓存
	 */
	// 所有选中项
	private List<T> mDeleteList;
	private List<T> mFakeDeleteList = new ArrayList<T>();
	private List<T> mReallyDeleteList = new ArrayList<T>();
	private int mDeleteRequestCount = 0;
	
	// 多选模式
	private boolean mIsMultiSelectMode;
	
	// 多选模式类型
	private int mMultiActionBarType = R.menu.operation_menu;

	private RemoteTaskService mGroupService;
	private com.pm360.cepm360.services.plan.RemoteTaskService mTaskService;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initMsgData(initMessageCenterData());
		initEnvironment();
		init(mClassItem, true, listInterface, serviceInterface,
				floatingMenuInterface, mOptionMenuInterface, dialogInterface,
				viewPagerInterface, actionBarInterface);
		setSimpleMultiSelectInterface(new SimpleMultiSelectInterface() {
			
			@Override
			public void setEnableMultiSelectMode(boolean enable) {
				mIsMultiSelectMode = enable;
				if (enable) {
					mFloatingMenu.setVisibility(View.GONE);
				} else {
					mFloatingMenu.setVisibility(View.VISIBLE);
					switchAddFloatingMenu(mListAdapter.getShowList().isEmpty());
				}
			}
			
			@Override
			public int getNoSelectDeleteToastStringId() {
				return R.string.pls_select_task;
			}
		});
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.task_list_options_menu;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {

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
						case 1:		// 插入
							// 任务
							OnClickListener listener1 = new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									mIsAddOperation = true;
									mIsInsertOperation = true;
									mIsFloatMenuAdd = false;
									optionMenuAddFunction();
									mInsertDialog.dismiss();
								}
							};
							// 里程碑
							OnClickListener listener2 = new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									mIsAddOperation = true;
									mIsInsertOperation = true;
									mIsFloatMenuAdd = false;
									mMileStoneFlag = true;
									optionMenuInsertFunction();
									mInsertDialog.dismiss();
								}
							};
							List<OnClickListener> listenerList = new ArrayList<OnClickListener>();
							listenerList.add(listener1);
							listenerList.add(listener2);
							List<String> textList = new ArrayList<String>();
							textList.add(mActivity.getString(R.string.task));
							textList.add(mActivity.getString(R.string.milestone));
							mInsertDialog = UtilTools.initTwoPickDialog(mActivity, listenerList, textList);
							mInsertDialog.show();
							
							break;
						case 2:		// 修改
							mIsAddOperation = false;
							mIsInsertOperation = false;
							showUpdateDialog(true);
							mDialog.setEditTextStyle(1, 0, getDoubleDateListener(), getResources().getString(R.string.parent_start_time) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCurrentItem.getStart_time()));
							mDialog.setEditTextStyle(2, 0, getDoubleDateListener(), getResources().getString(R.string.parent_end_time) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCurrentItem.getEnd_time()));
						
							break;
						case 3:
							commonConfirmDelete();
							break;
					}
				}
				
			};
			return listener;
		}
		
	};

	protected void doExtraEventWithViewPermission() {
		super.doExtraEventWithViewPermission();
		mBaseViewPager.setChildProject(mProject);
		enableNormalMultSelect();
	}
	
	private Bundle initMessageCenterData() {
		Bundle bundle = getArguments();
		if (bundle != null) {
	        mTaskId = bundle.getInt("taskId");
		}
		return bundle;
	}
	
	protected abstract void initMsgData(Bundle bundle);
	
	protected abstract Class<T> getPlanClass();
	
	protected boolean isPlanMakePermission() {
		return false;
	}
	
	private void initClassType(Class<T> cls) {
		mClassItem = cls;
		if (mClassItem == ZH_group_task.class) {
			mType = 1;
		} else if (mClassItem == Task.class) {
			mType = 2;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initEnvironment() {
		initClassType(getPlanClass());
		mExcelExportDialog = new ExcelExportDialog(getActivity());
		mGroupService = RemoteTaskService.getInstance();
		mTaskService = com.pm360.cepm360.services.plan.RemoteTaskService.getInstance();
		
		if (mType == 2 || isPlanMakePermission()) {
			setPermissionIdentity(GLOBAL.SYS_ACTION[3][0],
					GLOBAL.SYS_ACTION[2][0]);
		} else if (mType == 1) {
			setPermissionIdentity(GLOBAL.SYS_ACTION[35][0],
					GLOBAL.SYS_ACTION[34][0]);
		}

		mTaskPresenter = new ZH_TaskPresenter(getActivity());
	}

	SHCommonListInterface<T> listInterface = new SHCommonListInterface<T>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Map<String, String> subMap = new HashMap<String, String>();
			subMap.put(GLOBAL.TASK_TYPE_MILE_KEY, GLOBAL.TASK_TYPE_MILE_VALUE);
			subMap.put(GLOBAL.TASK_TYPE_TASK_KEY, GLOBAL.TASK_TYPE_TASK_VALUE);
			subMap.put(GLOBAL.TASK_TYPE_WBS_KEY, GLOBAL.TASK_TYPE_WBS_VALUE);
			map.put(TYPE, subMap);
			map.put(DEPARTMENT, ObsCache.getObsIdMaps());
			map.put(OWNER, UserCache.getUserMaps());
			
			Map<String, String> subMap2 = new HashMap<String, String>();
			for (int i = 0; i < GLOBAL.PUBLISH_STATUS.length; i++) {
				subMap2.put(GLOBAL.PUBLISH_STATUS[i][0], GLOBAL.PUBLISH_STATUS[i][1]);
			}
			map.put(STATUS, subMap2);
			return map;
		}

		@Override
		public int getListItemId(T t) {
			return t.getTask_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] { NAME, PLAN_DURATION, START_TIME, END_TIME,
					OWNER, DEPARTMENT, TYPE, STATUS };
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.task_make_title_list_item2;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.task_make_list_item2;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.plan_make_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.plan_make_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.plan_make_listids;
		}

		@Override
		public int getListItemDoScrollIds() {
			return R.array.plan_make_doscroll_ids;
		}
	};

	private void deleteTask(int type, String taskId) {

		sendMessage(SHOW_PROGRESS_DIALOG);
		if (type == 1) {
			if (mType == 1) {
				mGroupService.updateTaskForDelete(mMakeManager, taskId);
			} else if (mType == 2) {
				mTaskService.updateTaskForDelete(mMakeManager, taskId);
			}
		} else if (type == 2) {
			if (mType == 1) {
				mGroupService.delTask(mMakeManager, taskId);
			} else if (mType == 2) {
				mTaskService.delTask(mMakeManager, taskId);
			}
		}
			
		LogUtil.i("wzw taskId:" + taskId);
	}

	protected ServiceInterface<T> serviceInterface = new ServiceInterface<T>() {

		@Override
		public void getListData() {
			if (mType == 1) {
				if (mMsgGroupData != null) {
					sendMessage(SHOW_PROGRESS_DIALOG);
					mGroupService.getTaskList(getServiceManager(), mMsgGroupData.getZh_group_id());
				}
			} else if (mType == 2) {
				if (mProject != null) {
					sendMessage(SHOW_PROGRESS_DIALOG);
					mTaskService.getTaskList(getServiceManager(), mProject);
				} else {
					Toast.makeText(getActivity(), R.string.pls_select_project, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void addItem(T t) {
			String duration = mTaskPresenter.calculateDuration(t.getStart_time(), t.getEnd_time());
			t.setPlan_duration(duration);
			if (mMileStoneFlag) {
				mMileStoneFlag = false;
				t.setType(GLOBAL.TASK_TYPE_MILE_KEY);
			} else {
				t.setType(GLOBAL.TASK_TYPE_TASK_KEY);
			}

			sendMessage(SHOW_PROGRESS_DIALOG);
			if (mType == 1) {
				((ZH_group_task) t).setZh_group_id(mMsgGroupData.getZh_group_id());
				t.setTenant_id(mMsgGroupData.getTenant_id());
				if (mIsFloatMenuAdd) {
					mGroupService.addTask(mAddManager, (ZH_group_task) t);
					return;
				}
				mGroupService.setTask((ZH_group_task) mCurrentItem);
				if (mCurrentItem.getType().equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
					mGroupService.addTask(mAddManager, (ZH_group_task) t);
				} else {
					mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
					if (mCurrentUpdateItem.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[0][0])) {
						mCurrentUpdateItem.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0]));
					}
					mCurrentUpdateItem.setType(GLOBAL.TASK_TYPE_WBS_KEY);
					mGroupService.addTaskAndModifyParent(mAddManager, (ZH_group_task) t, (ZH_group_task) mCurrentUpdateItem);
				}
				
			} else if (mType == 2) {
				((Task) t).setProject_id(mProject.getProject_id());
				t.setTenant_id(mProject.getTenant_id());
				mTaskService.setTask((Task) mCurrentItem);
				mTaskService.addTask(mAddManager, (Task) t);
			}
		}

		@Override
		public void deleteItem(T t) {
            
			String taskId = "";
			int type = 0;
			
			// 如果mActionMode不为空，说明是批量删除模式
            if (mActionMode != null) {
            	mFakeDeleteList.clear();
            	mReallyDeleteList.clear();
            	String fakeTaskId = "";
				for (T dir : mDeleteList) {
					if (dir.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[0][0])
							|| dir.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0])) {
						// fake delete
						fakeTaskId += dir.getId() + ",";
						mFakeDeleteList.add(dir);
					} else if (dir.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[1][0])) {
						// really delete
						taskId += dir.getId() + ",";
						mReallyDeleteList.add(dir);
					}
				}
				mDeleteRequestCount = 0;
				if (!fakeTaskId.isEmpty()) {
					deleteTask(1, fakeTaskId.substring(0, fakeTaskId.length() - 1));
					mDeleteRequestCount++;
				}
				
				if (!taskId.isEmpty()) {
					deleteTask(2, taskId.substring(0, taskId.length() - 1));
					mDeleteRequestCount++;
				}
				
				if (mDeleteRequestCount == 0) {
					Toast.makeText(mActivity, R.string.pls_select_task, Toast.LENGTH_SHORT).show();
				}
                return;
            }
            
			if (t.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[0][0])
					|| t.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0])) {
				// fake delete
				type = 1;
			} else if (t.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[1][0])) {
				// really delete
				type = 2;
			}
			
			taskId = t.getId() + "";
			if (t.isHas_child()) {
				ArrayList<Integer> deleteList = mTaskPresenter
						.calculateTaskOfChildren(
								(ArrayList<T>) mListAdapter.getDataList(),
								t);
				for (int i = 0; i < deleteList.size(); i++) {
					taskId += "," + mListAdapter.getDataList().get(deleteList.get(i))
							.getTask_id();
				}
				
				deleteTask(type, taskId);
				
			} else {
				deleteTask(type, taskId);
			}
		}

		@Override
		public void updateItem(T t) {
			String duration = mTaskPresenter.calculateDuration(t.getStart_time(), t.getEnd_time());
			t.setPlan_duration(duration);
			if (t.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[0][0])
					|| t.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0])) {
				t.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0]));
			}
			if (mType == 1) {
				mGroupService.setTask((ZH_group_task) t);
				mGroupService.updateTask(getServiceManager());
			} else if (mType == 2) {
				mTaskService.setTask((Task) t);
				mTaskService.updateTask(getServiceManager());
			}
		}

	};
	
	OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			mFloatingMenu.dismiss();
			switch (position) {
			case 0:
//				if (!PermissionCache
//						.hasProjectPermission(PLAN_MODIFY_PERMISSION)) {
//					BaseToast.show(getActivity(),
//							BaseToast.NO_PERMISSION);
//					break;
//				}
				for (T t: mListAdapter.getDataList()) {
					if (t.getOwner() == 0) {
						Toast.makeText(mActivity, R.string.owner_is_empty, Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if (mTaskPresenter.taskTimeCheck(mListAdapter)) {
					if (mType == 1) {
						mGroupService.publish(getServiceManager(), mMsgGroupData);
					} else if (mType == 2) {
						//TODO project name?
						mTaskService.publish(getServiceManager(), mProject);
					}
				}

				break;
			case 1://export
				
				List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
				for (int i = 0; i < mListAdapter.getDataList().size(); i++) {
					mapList.add(mExcelExportDialog.excelPlanMakeToMap(ObsCache
							.getObsIdMaps(), UserCache.getUserMaps(),
							mListHeaderNames, mListAdapter.getDataList().get(i)));
				}
			
				String filePath = getString(R.string.export_path_content);
				String fileName = mProject.getName() + "_" + getString(R.string.export_filename_make);
				
				mExcelExportDialog.show(filePath, fileName, mapList, mListHeaderNames);

				break;
			case 2: // gantt
				GanttDialog<T> ganttDialog = new GanttDialog<>(mActivity);
				ganttDialog.show(mListAdapter.getDataList());
				break;
			}
			
		}

	}; 
	
	

	FloatingMenuInterface floatingMenuInterface = new FloatingMenuInterface() {

		@Override
		public int[] getFloatingMenuImages() {
			return new int[] {R.drawable.icn_publish, R.drawable.icon_export};
		}

		@Override
		public String[] getFloatingMenuTips() {
			return new String[] { getActivity().getResources().getString(
					R.string.plan_publish), getActivity().getResources().getString(
							R.string.export)};
		}

		@Override
		public OnItemClickListener getFloatingMenuListener() {
			return mOnItemClickListener;
		}

	};

	DialogAdapterInterface dialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.plan_add_dialog;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.plan_make_add_task;
		}

		@Override
		public String[] getUpdateFeilds() {
			return new String[] { NAME, START_TIME, END_TIME, OWNER,
					DEPARTMENT };
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return new Integer[]{0, 1, 3};
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, Integer> getDialogStyles() {

			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(1, BaseDialog.calendarLineStyle);
			buttons.put(2, BaseDialog.calendarLineStyle);
			buttons.put(3, BaseDialog.userSelectLineStyle);
			buttons.put(4, BaseDialog.OBSReadOnlyLineStyle);

			return buttons;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {

			OnClickListener ownerListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(v.getContext(), OwnerSelectActivity.class);
					intent.putExtra("title", getString(R.string.owner));
					Project project = new Project();
					if (mType == 1) {
						project.setProject_id(mMsgGroupData.getProject_id());
						project.setTenant_id(mMsgGroupData.getTenant_id());
					} else if (mType == 2) {
						project.setProject_id(mProject.getProject_id());
						project.setTenant_id(mProject.getTenant_id());
					}
					
					intent.putExtra("project", project);
					startActivityForResult(intent, OWNER_SELECT_REQUEST);
				}
			};

			mDialog.setEditTextStyle(3, 0, ownerListener, null);

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
	
	private DataManagerInterface mOwnerSelectInterface = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				for (int i = 0; i < mMultiTask.size(); i++) {
					for (int j = 0; j < mDeleteList.size(); j++) {
						if (mMultiTask.get(i).getId() == mDeleteList.get(j).getId()) {
							mDeleteList.get(j).setOwner(mMultiTask.get(i).getOwner());
							mDeleteList.get(j).setDepartment(mMultiTask.get(i).getDepartment());
							mDeleteList.get(j).setPublish(mMultiTask.get(i).getPublish());
							break;
						}
					}
				}
				mListAdapter.notifyDataSetChanged();
				mMultiOwnerSelectDialog.dismiss();
				mActionMode.finish();
			}
		}
	};
	
	private DataManagerInterface mTypeSelectInterface = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				for (int i = 0; i < mMultiTask.size(); i++) {
					for (int j = 0; j < mDeleteList.size(); j++) {
						if (mMultiTask.get(i).getId() == mDeleteList.get(j).getId()) {
							mDeleteList.get(j).setType(mMultiTask.get(i).getType());
							mDeleteList.get(j).setPublish(mMultiTask.get(i).getPublish());
							break;
						}
					}
				}
				mListAdapter.notifyDataSetChanged();
				mMultiTypeSelectDialog.dismiss();
				mActionMode.finish();
			}
		}
	};
	
	private BaseDialog mMultiOwnerSelectDialog;
	private BaseDialog mMultiTypeSelectDialog;
	private List<T> mMultiTask = new ArrayList<T>();
	
	ActionBarInterface actionBarInterface = new ActionBarInterface() {

		@Override
		public int getActionBarMenu() {
			return mMultiActionBarType;
		}

		@Override
		public void onActionItemClicked(MenuItem item) {
			switch (item.getItemId()) {
            case R.id.action_edit_owner:	// 编辑责任人
            	if (mListAdapter.getSelected().isEmpty()) {
            		Toast.makeText(getActivity().getBaseContext(), R.string.pls_select_task, Toast.LENGTH_SHORT).show();
            		return;
            	}
            	if (mMultiOwnerSelectDialog == null) {
            		mMultiOwnerSelectDialog = new BaseDialog(mActivity, R.string.owner);
                	Map<Integer, Integer> btns = new HashMap<>();
                	btns.put(0, BaseDialog.userSelectLineStyle);
                	btns.put(1, BaseDialog.OBSReadOnlyLineStyle);
                	final String[] ownerSelectLable = mActivity.getResources().getStringArray(R.array.multi_task_owner_select);
                	mMultiOwnerSelectDialog.init(ownerSelectLable, btns, null);
                	OnClickListener ownerListener = new OnClickListener() {

        				@Override
        				public void onClick(View v) {
        					Intent intent = new Intent();
        					intent.setClass(v.getContext(), OwnerSelectActivity.class);
        					intent.putExtra("title", getString(R.string.owner));
        					Project project = new Project();
        					if (mType == 1) {
        						project.setProject_id(mMsgGroupData.getProject_id());
        						project.setTenant_id(mMsgGroupData.getTenant_id());
        					} else if (mType == 2) {
        						project.setProject_id(mProject.getProject_id());
        						project.setTenant_id(mProject.getTenant_id());
        					}
        					
        					intent.putExtra("project", project);
        					startActivityForResult(intent, MULTI_OWNER_SELECT_REQUEST);
        				}
        			};

        			mMultiOwnerSelectDialog.setEditTextStyle(0, 0, ownerListener, null);
        			mMultiOwnerSelectDialog.setSaveButtonListener(new OnClickListener() {
    					
    					@SuppressWarnings("unchecked")
						@Override
    					public void onClick(View arg0) {
    						if (mDeleteList == null || mDeleteList.isEmpty()) {
    							Toast.makeText(mActivity, R.string.pls_select_task, Toast.LENGTH_SHORT).show();
    							return;
    						}
    						Map<String, String> saveData = mMultiOwnerSelectDialog.SaveData();
    						if (saveData.get(ownerSelectLable[0]).isEmpty()
    								|| saveData.get(ownerSelectLable[1]).isEmpty()) {
    							Toast.makeText(mActivity, R.string.pls_input_all_info, Toast.LENGTH_SHORT).show();
    							return;
    						}
    						int owner = Integer.parseInt(saveData.get(ownerSelectLable[0]));
    						int department = Integer.parseInt(saveData.get(ownerSelectLable[1]));
    						mMultiTask.clear();
    						for (int i = 0; i < mDeleteList.size(); i++) {
    							T t = MiscUtils.clone(mDeleteList.get(i));
    							t.setOwner(owner);
    							t.setDepartment(department);
    							if (t.getPublish() != Integer.parseInt(GLOBAL.PUBLISH_STATUS[1][0])) {
    								t.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0]));
    							}
    							
    							mMultiTask.add(t);
    						}

    		            	mGroupService.updateTaskOwner(mOwnerSelectInterface, (List<ZH_group_task>)mMultiTask);
    					}
    				});
            	}
            	
    			mMultiOwnerSelectDialog.show();
            	break;
            case R.id.action_edit_task_type:	// 编辑任务类型
            	if (mListAdapter.getSelected().isEmpty()) {
            		Toast.makeText(getActivity().getBaseContext(), R.string.pls_select_task, Toast.LENGTH_SHORT).show();
            		return;
            	}
            	if (mMultiTypeSelectDialog == null) {
            		mMultiTypeSelectDialog = new BaseDialog(mActivity, R.string.select_task_type);
                	Map<Integer, Integer> btns2 = new HashMap<>();
                	btns2.put(0, BaseDialog.spinnerLineStyle);
                	Map<Integer, String[]> contents = new HashMap<>();
                	String[] typeContent = new String[3];
                	typeContent[0] = GLOBAL.TASK_TYPE_MILE_VALUE;
                	typeContent[1] = GLOBAL.TASK_TYPE_TASK_VALUE;
                	typeContent[2] = GLOBAL.TASK_TYPE_WBS_VALUE;
                	contents.put(0, typeContent);
                	final String[] typeSelectLable = mActivity.getResources().getStringArray(R.array.multi_task_type_select);
                	mMultiTypeSelectDialog.init(typeSelectLable, btns2, contents);
                	mMultiTypeSelectDialog.setSaveButtonListener(new OnClickListener() {
    					
    					@SuppressWarnings("unchecked")
						@Override
    					public void onClick(View arg0) {
    						if (mDeleteList == null || mDeleteList.isEmpty()) {
    							Toast.makeText(mActivity, R.string.pls_select_task, Toast.LENGTH_SHORT).show();
    							return;
    						}
    						Map<String, String> saveData = mMultiTypeSelectDialog.SaveData();
    						String type = saveData.get(typeSelectLable[0]);
    						if (type.equals(GLOBAL.TASK_TYPE_MILE_VALUE)) {
    							type = GLOBAL.TASK_TYPE_MILE_KEY;
    						} else if (type.equals(GLOBAL.TASK_TYPE_TASK_VALUE)) {
    							type = GLOBAL.TASK_TYPE_TASK_KEY;
    						} else {
    							type = GLOBAL.TASK_TYPE_WBS_KEY;
    						}
    						mMultiTask.clear();
    						for (int i = 0; i < mDeleteList.size(); i++) {
    							T t = MiscUtils.clone(mDeleteList.get(i));
    							t.setType(type);
    							if (t.getPublish() != Integer.parseInt(GLOBAL.PUBLISH_STATUS[1][0])) {
    								t.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0]));
    							}
    							mMultiTask.add(t);
    						}
    						LogUtil.i("wzw mMultiTask:" + mMultiTask);
//    						mGroupService.updateTask(mOwnerSelectInterface);
    		            	mGroupService.updateTaskType(mTypeSelectInterface, (List<ZH_group_task>)mMultiTask);
    					}
    				});
            	}
            	
            	mMultiTypeSelectDialog.show();
            	break;
			}
		}
		
	};
	
	@Override
	protected void commonListClick(int position) {
		super.commonListClick(position);
		mLine = position;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == OWNER_SELECT_REQUEST) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mDialog.setUserTextContent(3, user.getUser_id());
				mDialog.setOBSTextContent(4, user.getObs_id());
			}
		} else if (requestCode == MULTI_OWNER_SELECT_REQUEST) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mMultiOwnerSelectDialog.setUserTextContent(0, user.getUser_id());
				mMultiOwnerSelectDialog.setOBSTextContent(1, user.getObs_id());
			}
			
		}
	}
	
	protected void setChildCurrentBeanList(DataTreeListAdapter<T> listAdapter) {
		mBaseViewPager.setParentList(listAdapter);
	}
	
	private DataManagerInterface mAddManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@SuppressLint("UseSparseArrays") @Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			LogUtil.i("wzw status:" + status.getCode());

			sendMessage(DISMISS_PROGRESS_DIALOG);
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
			}
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				if (mCurrentUpdateItem != null) {
					mCurrentItem.setPublish(mCurrentUpdateItem.getPublish());
					mCurrentItem.setType(mCurrentUpdateItem.getType());
				}
				
				if (mIsInsertOperation) {
					TreePresenter.insertToTreeList(mListAdapter, (T) list.get(0), mLine);
				} else {
					mListAdapter.addTreeNode((T) list.get(0));
				}
				
				
				// 切换mFloatingMenu
				if (isFirstAddStatus) {
					isFirstAddStatus = false;

					if (mFloatingMenu != null) {
						switchAddFloatingMenu(false);
					}
					
				}
				
			}
		}
	};
	
	/**
	 * 切换浮动按钮（增加，发布）
	 * @param type true 则为增加
	 */
	private void switchAddFloatingMenu(boolean type) {
		if (mFloatingMenu == null) {
			return;
		}
		
		if (type) {
			mFloatingMenu.clear();
			mFloatingMenu.addPopItem(getActivity().getResources().getString(R.string.add), R.drawable.icn_add);
			mFloatingMenu.setPopOnItemClickListener(new OnItemClickListener() {
				
				 @Override
		         public void onItemClick(AdapterView<?> parent,
		         						View view,
		         						int position,
		         						long id) {
		             switch (position) {
		                 case 0:
		                	 //TODO
		                 	 RemoteCommonService.getInstance().getWBSTemplet(mWBSManager, UserCache.getCurrentUser());
		                     mFloatingMenu.dismiss();
		                     break;
		                 default:
		                     break;
		             }
		         }
			});
		} else {
			mFloatingMenu.clear();
			mFloatingMenu.addPopItem(getActivity().getResources().getString(R.string.plan_publish), R.drawable.icn_publish);
			mFloatingMenu.addPopItem(getActivity().getResources().getString(R.string.export), R.drawable.icon_export);
			mFloatingMenu.setPopOnItemClickListener(mOnItemClickListener);
		}
	}
	
	private void finishActionMode() {
		mDeleteRequestCount--;
		if (mDeleteRequestCount == 0) {
			mActionMode.finish();
		}
	}
	
	private DataManagerInterface mMakeManager = new DataManagerInterface() {

		@SuppressLint("UseSparseArrays") @Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			LogUtil.i("wzw status:" + status.getCode());

			sendMessage(DISMISS_PROGRESS_DIALOG);
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
			}
			if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
				
				if (mActionMode != null) {
					mListAdapter.deleteMultiTreeNode(mReallyDeleteList);
//					updateActionModeTitle(mActionMode, getActivity().getBaseContext(), 0);
					finishActionMode();
				} else {
					mTaskPresenter.deleteTaskNodeList((ArrayList<T>) mListAdapter.getShowList(), mCurrentItem, mLine);
					mTaskPresenter.deleteTaskList((ArrayList<T>) mListAdapter.getDataList(), mCurrentItem);
					mListAdapter.notifyDataSetChanged();
				}
				
				if (mListAdapter.getShowList().isEmpty() && !mIsMultiSelectMode) {
					isFirstAddStatus = true;
					if (mFloatingMenu != null) {
						switchAddFloatingMenu(true);
					}
				}
				
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				if (mActionMode != null) {
					// 多选删除模式，此时之前任务以发布，因此删除需要先该状态
					for (int i = 0; i < mFakeDeleteList.size(); i++) {
						mFakeDeleteList.get(i).setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0]));
					}
					mListAdapter.clearSelectionAll();
					finishActionMode();
				} else {
					ArrayList<Integer> deleteList = mTaskPresenter
							.calculateTaskOfChildren(
									(ArrayList<T>) mListAdapter.getDataList(),
									mCurrentItem);
					for (int i = 0; i < deleteList.size(); i++) {
						T task = mListAdapter.getDataList().get(deleteList.get(i));
						task.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0]));
					}
					mCurrentItem.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0]));
					mListAdapter.notifyDataSetChanged();
				}
				
			}
		}
	};
	
	private DataManagerInterface mWBSManager = new DataManagerInterface() {

		@SuppressLint("UseSparseArrays") @Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				int id = 0;
				if (mType == 1) {
					id = mMsgGroupData.getZh_group_id();
				} else if (mType == 2) {
					id = mProject.getProject_id();
				}
				
				new TemplateDialog(getActivity(), mType, id, new TemplateDialogInterface() {
					
					@SuppressWarnings("unchecked")
					@Override
					public void createTask() {
						mIsAddOperation = true;
						mIsFloatMenuAdd = true;
						
						if (mType == 1) {
							mCurrentItem = (T) new ZH_group_task();
							mCurrentItem.setTask_id(0);
							mGroupService.setTask((ZH_group_task) mCurrentItem);
						} else if (mType == 2) {
							mCurrentItem = (T) new Task();
							mCurrentItem.setTask_id(0);
							mTaskService.setTask((Task) mCurrentItem);
						}
						mDialog.show();
						
					}
					
					@Override
					public void addTemplateSucc() {
						switchAddFloatingMenu(false);
						loadData();
					}
				});
				
			}
		}
		
	};
	
	protected void loadData() {
		serviceInterface.getListData();
	}
	
	private OnClickListener mDateListener;
	private OnClickListener getDoubleDateListener() {
		if (mDateListener != null) {
			return mDateListener;
		}
		final EditText startEt = (EditText) mDialog.getEditTextView(1);
		final EditText endEt = (EditText) mDialog.getEditTextView(2);
		final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
				getActivity(), null, startEt, endEt, null);
		mDateListener = new OnClickListener() {

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
		return mDateListener;
	}
	
	@Override
	protected void optionMenuAddFunction() {
		mDialog.show(null);
		
		mDialog.setEditTextStyle(1, 0, getDoubleDateListener(), getResources().getString(R.string.parent_start_time) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCurrentItem.getStart_time()));
		mDialog.setEditTextStyle(2, 0, getDoubleDateListener(), getResources().getString(R.string.parent_end_time) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCurrentItem.getEnd_time()));
	}
	
	private void optionMenuInsertFunction() {
		mDialog.show(null);
		mDialog.setEditTextStyle(1, 0, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CEPMDatePickerDialog dialog = new CEPMDatePickerDialog(mActivity, new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker datePicker, int startYear,
							int startMonthOfYear, int startDayOfMonth) {
						String startDate = String.format("%d-%d-%d", startYear,
								startMonthOfYear + 1, startDayOfMonth);
						mDialog.setEditTextContent(1, startDate);
						mDialog.setEditTextContent(2, startDate);
					}
				}, mActivity.getString(R.string.plan_add_dialog));
				dialog.show();
			}
		}, getResources().getString(R.string.parent_start_time) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCurrentItem.getStart_time()));
		mDialog.setEditTextStyle(2, 0, new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				// TODO Auto-generated method stub
				
			}
		}, getResources().getString(R.string.parent_end_time) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCurrentItem.getEnd_time()));
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@Override
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
			if (list == null || list.isEmpty()) {
				//TODO 切换mFloatingMenu
				isFirstAddStatus = true;
				if (mFloatingMenu != null) {
					switchAddFloatingMenu(true);
				}
				
			} else {
				switchAddFloatingMenu(false);
				//处理消息界面进入的情况
				if (mType == 1) {
					T mTaskInfoBean = (T) mTaskPresenter.initMsgInfo((ArrayList<T>)list, mTaskId, 1);
					TreePresenter.updateListView(TreePresenter.initLevelTask((ArrayList<T>)list, mTaskInfoBean), mListAdapter);
				} else if (mType == 2) {
					T mTaskInfoBean = (T) mTaskPresenter.initMsgInfo((ArrayList<T>)list, mTaskId, 2);
					TreePresenter.updateListView(TreePresenter.initLevelTask((ArrayList<T>)list, mTaskInfoBean), mListAdapter);
				}
				
				//提供list给子Fragment处理
				setChildCurrentBeanList(mListAdapter);
			}
		} else if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
			
			// 修改成功后，更新viewPager常用界面
			handleChildEvent();
		} else if (status.getCode() == AnalysisManager.SUCCESS_PUBLISH) {
			mActivity.gIsPublish = true;
			loadData();
		}
	}
	
	@Override
	public void handleClickWithShowOptionMenu(int position, View view) {
		if (mActionMode != null) {
			if (mMultiActionBarType == R.menu.operation_menu) {
				mListAdapter.setPickSelected(position);
	        	mDeleteList = mTaskPresenter.calculateMutilTaskOfChildren(mListAdapter, mListAdapter.getSelected());
				updateActionModeTitle(mActionMode, getActivity().getBaseContext(), mDeleteList.size());
			} else {
				mListAdapter.setPickSelectedInUpdateMode(position);
	        	mDeleteList = mListAdapter.getSelectedData();
				updateActionModeTitle(mActionMode, getActivity().getBaseContext(), mDeleteList.size());
			}
			
			return;
		}
		
		commonListClick(position);
		
		if (mCurrentItem.getParents_id() == 0) {
			if (mCurrentItem.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0])) {
				if (mOptionsMenu != null) {
					mOptionsMenu.setVisibileMenu(mInsertOptionsVisible, false);
					mOptionsMenu.showAsDropDown(view, 0, (-view
							.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
				}
			} else {
				if (mOptionsMenu != null) {
					mOptionsMenu.setVisibileMenu(1, false);
					mOptionsMenu.setVisibileMenu(mOptionsVisible, true);
					mOptionsMenu.showAsDropDown(view, 0, (-view
							.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
				}
			}
		} else {
			boolean parentDeleteStatus = false;
			for (T t : mListAdapter.getShowList()) {
				if (mCurrentItem.getParents_id() == t.getId()) {
					if (t.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0])) {
						parentDeleteStatus = true;
					}
				}
			}
			
			if (!parentDeleteStatus) {
				if (mCurrentItem.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0])) {
					if (mOptionsMenu != null) {
						mOptionsMenu.setVisibileMenu(mInsertOptionsVisible, false);
						mOptionsMenu.showAsDropDown(view, 0, (-view
								.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
					}
				} else {
					if (mOptionsMenu != null) {
						mOptionsMenu.setVisibileMenu(mInsertOptionsVisible, true);
						mOptionsMenu.showAsDropDown(view, 0, (-view
								.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
					}
				}
				
			}
		}
		
		// 点击更新viewPager常用界面
		handleChildEvent();
	}
	private int[] mOptionsVisible = new int[]{0, 3};
	private int[] mInsertOptionsVisible = new int[]{0, 1, 3};
	
	@Override
	protected void deleteCurrentAndChildItem() {
		serviceInterface.deleteItem(mCurrentItem);
	}

	protected boolean enableProjectMenu() {
		if (mType == 2) {
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean hasListItemData() {
		if (mListAdapter.getShowList() != null && !mListAdapter.getShowList().isEmpty()) {
			return true;
		} else {
			Toast.makeText(mActivity, R.string.pls_add_task, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	@Override
	protected void handleLongClick(ViewHolder viewHolder, final int position) {
		// 注册长按监听
        viewHolder.tvs[position].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
            	if (mHasEditPermission) {
            		if (mActionMode == null) {
                    	// 长按进入ActionMode，此时ActionMode应该是NULL

            			if (position == 4 || position == 5) {
            				mMultiActionBarType = R.menu.task_menu_update_owner;
                    		mListAdapter.setMultiMode(true);
            			} else if (position == 6) {
            				mMultiActionBarType = R.menu.task_menu_update_type;
                    		mListAdapter.setMultiMode(true);
            			} else {
            				mMultiActionBarType = R.menu.operation_menu;
                    		mListAdapter.setMultiMode(false);
            			}
                        mActionMode = getActivity().startActionMode(mCallback);
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
}
