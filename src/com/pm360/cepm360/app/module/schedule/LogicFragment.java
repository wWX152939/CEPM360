package com.pm360.cepm360.app.module.schedule;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Logic;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.services.plan.RemoteLogicService;
import com.pm360.cepm360.services.plan.RemoteTaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicFragment extends BaseListRelevanceFragment<Logic, Task> {

	private RemoteLogicService mService;
	private RemoteTaskService mTaskService;
	private Activity mActivity;
	private LogicController mLogicController;
	
	private final String P_TASK = "task_name";
	private final String TYPE = "logic_type";
	private final String DURATION = "period";
	
	private DataTreeListAdapter<Task> mParentListAdapter;
	private ArrayList<Logic> mLogicListFrontCache;
	private ArrayList<Logic> mLogicListRearCache;
	private ArrayList<Task> mDialogTaskList;
	private boolean mLogicFrontTaskFlag = true;

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(Logic.class, mListInterface, mRequestInterface, null, null,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[25][0],
				GLOBAL.SYS_ACTION[24][0]);
		initEnvironment();

		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		return view;
	}
	
	private void initEnvironment() {
		mService = RemoteLogicService.getInstance();
		mTaskService = RemoteTaskService.getInstance(); 
		mApplication = (CepmApplication) getActivity().getApplication();
		mLogicController = new LogicController();
		mActivity = getActivity();
		
	}
	
	protected void initExtraEvent() {
		final TextView tv = (TextView) mListHeader.findViewById(mListHeaderItemIds[1]);	
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mParentBean == null) {
					Toast.makeText(mActivity, R.string.plan_is_null,
							Toast.LENGTH_SHORT).show();
					return;
				}
				mLogicFrontTaskFlag = mLogicFrontTaskFlag ? false : true;
				if (mLogicFrontTaskFlag) {
					tv.setText(R.string.plan_front_logic);
					switchLogicTypeDialog(CalculateLogicTask(null));
					mService.getLogicList(
							mLogicInterface, 0);
				} else {
					tv.setText(R.string.plan_rear_logic);
					switchLogicTypeDialog(CalculateLogicTask(null));
					mService.getLogicList(
							mLogicInterface, 1);
				}
			}
		});
	}

	CommonListInterface<Logic> mListInterface = new CommonListInterface<Logic>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> switchMap = new HashMap<String, Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			map.put(Integer.toString(GLOBAL.LOGIC_F_TYPE_0), GLOBAL.LOGIC_F_TYPE_0_VALUE);
			map.put(Integer.toString(GLOBAL.LOGIC_F_TYPE_1), GLOBAL.LOGIC_F_TYPE_1_VALUE);
			map.put(Integer.toString(GLOBAL.LOGIC_F_TYPE_2), GLOBAL.LOGIC_F_TYPE_2_VALUE);
			map.put(Integer.toString(GLOBAL.LOGIC_F_TYPE_3), GLOBAL.LOGIC_F_TYPE_3_VALUE);
			switchMap.put(TYPE, map);
			return switchMap;
		}

		@Override
		public int getListItemId(Logic t) {
			return t.getLogic_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER,
					P_TASK,
					TYPE,
					DURATION
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.task_make_logic_title;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.task_make_logic_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.task_make_logic_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.task_make_logic_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.task_make_logic_ids;
		}

	};
	ServiceInterface<Logic> mRequestInterface = new ServiceInterface<Logic>() {

		@Override
		public void getListData() {
			if (mParentBean != null) {
				mTaskService.setTask(mParentBean);
				if (mLogicFrontTaskFlag) {
					mService.getLogicList(mLogicInterface, 0);
				} else {
					mService.getLogicList(mLogicInterface, 1);
				}
				
			}
		}

		@Override
		public void addItem(Logic t) {
			mService.addFLogic(getServiceManager(), t);
		}

		@Override
		public void deleteItem(Logic t) {
			mService.setLogic(t);
			mService.deleteLogic(getServiceManager());
		}

		@Override
		public void updateItem(Logic t) {
			mService.setLogic(t);
			mService.updateLogic(getServiceManager());
		}

	};
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.plan_add_logic_dialog;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.plan_make_logic_dialog_names;
		}

		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					P_TASK,
					TYPE,
					DURATION
			};
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}

		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(0, BaseDialog.spinnerLineStyle);
			buttons.put(1, BaseDialog.spinnerLineStyle);
			return buttons;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();
			String[] spinnerContent = new String[4];
			spinnerContent[0] = GLOBAL.LOGIC_F_TYPE_0_VALUE;
			spinnerContent[1] = GLOBAL.LOGIC_F_TYPE_1_VALUE;
			spinnerContent[2] = GLOBAL.LOGIC_F_TYPE_2_VALUE;
			spinnerContent[3] = GLOBAL.LOGIC_F_TYPE_3_VALUE;
			widgetContent.put(1, spinnerContent);
			String[] planContent = new String[mParentListAdapter.getShowList().size()];
			for (int j = 0; j < mParentListAdapter.getShowList().size(); j++) {
				planContent[j] = mParentListAdapter.getShowList().get(j).getName();
			}
			widgetContent.put(0, planContent);
			return widgetContent;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			// TODO Auto-generated method stub
			return null;
		}

	};
	
	@Override
	public void handleParentEvent(Task b){
		mRequestInterface.getListData();
	}
	
	@Override
	public void setParentList(DataTreeListAdapter<Task> listAdapter) {
		mParentListAdapter = listAdapter;
	}
	
	private void switchLogicTypeDialog(ArrayList<Task> planList) {
		mDialogTaskList = planList;
		
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(1, BaseDialog.spinnerLineStyle);
		buttons.put(0, BaseDialog.spinnerLineStyle);
		buttons.put(2, BaseDialog.numberEditTextLineStyle);
		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();
		String[] spinnerContent = new String[4];
		spinnerContent[0] = GLOBAL.LOGIC_F_TYPE_0_VALUE;
		spinnerContent[1] = GLOBAL.LOGIC_F_TYPE_1_VALUE;
		spinnerContent[2] = GLOBAL.LOGIC_F_TYPE_2_VALUE;
		spinnerContent[3] = GLOBAL.LOGIC_F_TYPE_3_VALUE;

		widgetContent.put(1, spinnerContent);

		String[] planContent = new String[planList.size()];
		for (int j = 0; j < planList.size(); j++) {
			planContent[j] = planList.get(j).getName();
		}
		widgetContent.put(0, planContent);

		mDialog.init(R.array.plan_make_logic_dialog_names, buttons,
				widgetContent);
		Button saveImageView = (Button) mDialog.getPopupView()
				.findViewById(R.id.save_Button);
		saveImageView.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View view) {
				mFloatingMenu.dismiss();
				if (dialogSaveButtonEvent()) {
					mDialog.dismiss();
				}
			}        	
		});

	}
	
	private String logicTypeInt2String(int type) {
		String ret = null;
		switch (type) {
		case 0:
			ret = GLOBAL.LOGIC_F_TYPE_0_VALUE;
			break;
		case 1:
			ret = GLOBAL.LOGIC_F_TYPE_1_VALUE;
			break;
		case 2:
			ret = GLOBAL.LOGIC_F_TYPE_2_VALUE;
			break;
		case 3:
			ret = GLOBAL.LOGIC_F_TYPE_3_VALUE;
			break;
		}
		return ret;
	}
	
	private int logicTypeString2Int(String type) {
		int ret = -1;
		switch (type) {
		case GLOBAL.LOGIC_F_TYPE_0_VALUE:
			ret = 0;
			break;
		case GLOBAL.LOGIC_F_TYPE_1_VALUE:
			ret = 1;
			break;
		case GLOBAL.LOGIC_F_TYPE_2_VALUE:
			ret = 2;
			break;
		case GLOBAL.LOGIC_F_TYPE_3_VALUE:
			ret = 3;
			break;
		}
		return ret;
	}
	
	/**
	 * 返回false 显示dialog，返回true，不显示dialog
	 */
	@Override
	protected boolean doExtraAddFloatingMenuEvent() {
		if (mDialogTaskList == null || mDialogTaskList.size() == 0) {

			if (mLogicFrontTaskFlag) {
				Toast.makeText(mActivity, R.string.task_logic_check_1,
						Toast.LENGTH_SHORT).show();
				return true;
			} else {
				Toast.makeText(mActivity, R.string.task_logic_check_2,
						Toast.LENGTH_SHORT).show();
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	protected void showUpdateDialog(boolean isEdit) {
		if (mDialogTaskList == null || mDialogTaskList.size() == 0) {
			mDialogTaskList = new ArrayList<Task>();
			Task task = new Task();
			task.setTask_id(mCurrentItem.getTask_id());
			task.setName(mCurrentItem.getTask_name());
			mDialogTaskList.add(task);
			switchLogicTypeDialog(mDialogTaskList);
		} else {
			if (mLogicFrontTaskFlag) {
				if (mLogicListFrontCache != null
						&& mLogicListFrontCache.size() != 0) {
					switchLogicTypeDialog(CalculateLogicTask(mLogicListFrontCache, mCurrentItem));
				} else {
					switchLogicTypeDialog(CalculateLogicTask(null, mCurrentItem));
				}
			} else {
				if (mLogicListRearCache != null
						&& mLogicListRearCache.size() != 0) {
					switchLogicTypeDialog(CalculateLogicTask(mLogicListRearCache, mCurrentItem));
				} else {
					switchLogicTypeDialog(CalculateLogicTask(null, mCurrentItem));
				}
			}
		}
		
		String[] defaultValues = new String[mUpdateFields.length];
		defaultValues[0] = mCurrentItem.getTask_name();
		defaultValues[1] = logicTypeInt2String(mCurrentItem.getLogic_type());
		defaultValues[2] = Integer.toString(mCurrentItem.getPeriod());
		mDialog.show(defaultValues);
	}
	
	@Override
	protected boolean dialogSaveButtonEvent() {
		Map<String, String> items = mDialog.SaveData();
		if (items.get(mDialogLableNames[2]).equals("")) {
			BaseToast.show(mActivity, BaseToast.NULL_MSG);
			return false;
		}
		Logic logic;
		if (mIsAddOperation) {
			logic = new Logic();
			if (mLogicFrontTaskFlag) {
				logic.setType(0);
			} else {
				logic.setType(1);
			}

			logic.setTask_id(mParentBean.getTask_id());
		} else {
			logic = mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
		}

		for (int i = 0; i < mParentListAdapter.getDataList().size(); i++) {
			if (items.get(mDialogLableNames[0]).equals(
					mParentListAdapter.getDataList().get(i).getName())) {
				logic.setLogic(mParentListAdapter.getDataList().get(i).getTask_id());
				break;
			}
		}
		logic.setTask_name(items.get(mDialogLableNames[0]));
		logic.setLogic_type(logicTypeString2Int(items
				.get(mDialogLableNames[1])));
		logic.setPeriod(Integer.parseInt(items
				.get(mDialogLableNames[2])));
		
		if (mIsAddOperation) {
			mService.addFLogic(mLogicInterface,
					logic);
		} else {
			mService.setLogic(mCurrentUpdateItem);
			mService.updateLogic(mLogicInterface);
		}
		
		return true;
	}
	
	private DataManagerInterface mLogicInterface = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status,
				@SuppressWarnings("rawtypes") List list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
			}
			if ((status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
					|| (status.getCode() == AnalysisManager.SUCCESS_DB_DEL)) {
				if (mLogicFrontTaskFlag) {
					mService.getLogicList(
							mLogicInterface, 0);
				} else {
					mService.getLogicList(
							mLogicInterface, 1);
				}

			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				if (!mCurrentItem.getTask_name().equals(mCurrentUpdateItem.getTask_name())) {
					mLogicRefreshHandler.sendEmptyMessage(0);
				}
				
				MiscUtils.clone(mCurrentItem, mCurrentUpdateItem);
				mListAdapter.notifyDataSetChanged();
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				
				// 三步操作 1.add/update 2.父点击事件 3.子点击事件 都会响应这里 修改dialog内容
				if (mLogicFrontTaskFlag) {
					mLogicListFrontCache = (ArrayList<Logic>) list;
				} else {
					mLogicListRearCache = (ArrayList<Logic>) list;
				}

				mLogicRefreshHandler.sendEmptyMessage(0);
				mListAdapter.setShowDataList(list);
			}

		}

	};
	
	private Handler mLogicRefreshHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mLogicFrontTaskFlag) {
				if (mLogicListFrontCache != null
						&& mLogicListFrontCache.size() != 0) {
					switchLogicTypeDialog(CalculateLogicTask(mLogicListFrontCache));
				} else {
					switchLogicTypeDialog(CalculateLogicTask(null));
				}
			} else {
				if (mLogicListRearCache != null
						&& mLogicListRearCache.size() != 0) {
					switchLogicTypeDialog(CalculateLogicTask(mLogicListRearCache));
				} else {
					switchLogicTypeDialog(CalculateLogicTask(null));
				}
			}

		}
	};
	
	private ArrayList<Task> CalculateLogicTask(ArrayList<Logic> logicList) {
		return mLogicController.CalculateLogicTask(logicList, mParentBean, mParentListAdapter.getDataList(), mLogicFrontTaskFlag);
	}
	
	private ArrayList<Task> CalculateLogicTask(ArrayList<Logic> logicList, Logic logic) {
		ArrayList<Task> taskList = mLogicController.CalculateLogicTask(logicList, mParentBean, mParentListAdapter.getDataList(), mLogicFrontTaskFlag);
		for (Task task : mParentListAdapter.getShowList()) {
			if (task.getTask_id() == logic.getLogic()) {
				taskList.add(task);
			}
		}
		return taskList;
	}
}

