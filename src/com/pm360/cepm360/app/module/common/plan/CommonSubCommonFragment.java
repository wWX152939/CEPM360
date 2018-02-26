package com.pm360.cepm360.app.module.common.plan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.core.combination.ZH_TaskPresenter;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.plan.RemoteTaskService;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommonSubCommonFragment<T extends Serializable, B extends TaskCell> extends BaseListRelevanceFragment<T, B> {

	// View
	private BaseWindow mInitViewCommon;
	private Activity mActivity;
	private View viewCommon;
	
	// data
	private DataTreeListAdapter<B> mParentListAdapter;
	private ZH_TaskPresenter<B> mTaskPresenter;
	private B mUpdateBean;
	
	// server
	private com.pm360.cepm360.services.group.RemoteTaskService mGroupService;
	private RemoteTaskService mTaskService;
	
	// status
	private final int OWNER_SELECT_REQUEST = 201;
	
	// 1 combination 2 plan
	private int mType;
	private int mCurrentLine;

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setPermissionIdentity(null, null);

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/**
	 * 自定义布局
	 */
	protected void showNoViewPermission(  LayoutInflater inflater, 
										ViewGroup container) {
		initEnvironment();
		initViewCommon();
		mRootLayout = inflater.inflate(R.layout.base_activity, null, false);
		
		((ViewGroup) mRootLayout).addView(mInitViewCommon.getPopupView());

		setDefaultData();
	}
	
	private void initEnvironment() {
		mType = getType();
		mActivity = getActivity();
		mTaskPresenter = new ZH_TaskPresenter<B>(getActivity());
		mTaskService = RemoteTaskService.getInstance();
		mGroupService = com.pm360.cepm360.services.group.RemoteTaskService.getInstance();
	}
	
	protected abstract int getType();

	@SuppressLint("UseSparseArrays") 
	public void initViewCommon() {
		mInitViewCommon = new BaseWindow(mActivity);

		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(1, BaseDialog.calendarLineStyle);
		buttons.put(2, BaseDialog.calendarLineStyle);
		buttons.put(3, BaseDialog.editTextReadOnlyLineStyle);
		buttons.put(4, BaseDialog.userSelectLineStyle);
		buttons.put(5, BaseDialog.OBSReadOnlyLineStyle);
		buttons.put(6, BaseDialog.spinnerLineStyle);

		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();

		String[] spinnerContent = new String[] { GLOBAL.TASK_TYPE_TASK_VALUE,
				GLOBAL.TASK_TYPE_MILE_VALUE, GLOBAL.TASK_TYPE_WBS_VALUE };

		widgetContent.put(6, spinnerContent);
		mInitViewCommon.init(R.array.task_make_info_attr, buttons,
				widgetContent, false);

		final EditText startEt = (EditText) mInitViewCommon.getEditTextView(1);
		final EditText endEt = (EditText) mInitViewCommon.getEditTextView(2);
		final EditText durationEt = (EditText) mInitViewCommon.getEditTextView(3);
		
		final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
				mActivity, null, startEt, endEt, durationEt);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mParentBean == null) {
					
				} else {
					B taskParent = null;
					if (mParentBean.getParents_id() != 0) {
						
						for (int i = mCurrentLine - 1; i >= 0; i--) {
	
							if (mParentListAdapter.getShowList().get(i).getLevel() < mParentBean.getLevel()) {
								taskParent = (B) mParentListAdapter.getShowList().get(i);
								break;
							}
						}
						if (taskParent.getStart_time() == null) {
							Toast.makeText(
									mActivity,
									mActivity.getString(R.string.plan_time_check_left_symbol)
											+ taskParent.getName()
											+ mActivity.getString(R.string.plan_time_check_right_start_null),
									Toast.LENGTH_SHORT).show();
							return;
						}
//						doubleDatePickerDialog.setDateLimit(taskParent.getStart_time(), taskParent.getEnd_time());
					}
					if (mParentBean.getParents_id() != 0 && mParentBean.getStart_time() == null) {
						doubleDatePickerDialog.show(taskParent.getStart_time(), taskParent.getStart_time());
					} else {
						doubleDatePickerDialog.show(mParentBean.getStart_time(), mParentBean.getEnd_time());
					}
				}
			}
		};
		mInitViewCommon.setEditTextStyle(1, 0, listener, null);
		mInitViewCommon.setEditTextStyle(2, 0, listener, null);
		
		EditText owner = (EditText) mInitViewCommon.getPopupView()
				.findViewById(mInitViewCommon.baseEditTextId + 4);
		owner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), OwnerSelectActivity.class);
				intent.putExtra("title", mActivity.getString(R.string.owner));
				intent.putExtra("project", mCurrentProject);
				startActivityForResult(intent,
						OWNER_SELECT_REQUEST);
			}
		});

		final String[] names = mActivity.getResources().getStringArray(
				R.array.task_make_info_attr);
		viewCommon = mInitViewCommon.getPopupView();
		Button saveImageView = (Button) viewCommon
				.findViewById(R.id.save_Button);
		RelativeLayout.LayoutParams btnParams = (RelativeLayout.LayoutParams) saveImageView.getLayoutParams();
		btnParams.setMarginStart(UtilTools.dp2pxW(mActivity, 480));
		saveImageView.setLayoutParams(btnParams);

		final EditText editText = (EditText) viewCommon
				.findViewById(mInitViewCommon.baseEditTextId + 3);
		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mParentBean == null) {
					BaseToast.show(mActivity.getBaseContext(),
							BaseToast.NO_TASK_EXIST);
					return;
				}

				Map<String, String> saveData = mInitViewCommon.SaveData();
				if (saveData.get(names[0]).equals("")
						|| saveData.get(names[4]).equals("")) {
					// || saveData.get(names[5]).equals("")) {
					BaseToast.show(mActivity, BaseToast.NULL_MSG);
					return;
				}
				
				mUpdateBean = MiscUtils.clone(mParentBean);

				mUpdateBean.setName(saveData.get(names[0]));

				Date startTime = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						saveData.get(names[1]));
				Date endTime = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						saveData.get(names[2]));

				mUpdateBean.setStart_time(startTime);
				mUpdateBean.setEnd_time(endTime);

//				if (!mTaskPresenter.calculatorParentDate(
//						mParentListAdapter.getShowList(),
//						startTime, endTime, mCurrentLine, mParentBean)) {
//					return;
//				}
//
//				if (!mTaskPresenter.calculatorChildrenDate(
//						mParentListAdapter.getDataList(),
//						mUpdateBean, startTime, endTime)) {
//					return;
//				}

				String duration = mTaskPresenter.calculateDuration(startTime, endTime);
				mUpdateBean.setPlan_duration(duration);
				editText.setText(duration);

				if (saveData.get(names[6]).equals(GLOBAL.TASK_TYPE_MILE_VALUE)) {
					mUpdateBean.setType(GLOBAL.TASK_TYPE_MILE_KEY);
				} else if (saveData.get(names[6]).equals(
						GLOBAL.TASK_TYPE_TASK_VALUE)) {
					mUpdateBean.setType(GLOBAL.TASK_TYPE_TASK_KEY);
				} else if (saveData.get(names[6]).equals(
						GLOBAL.TASK_TYPE_WBS_VALUE)) {
					mUpdateBean.setType(GLOBAL.TASK_TYPE_WBS_KEY);
				} else if (saveData.get(names[6]).equals(
						GLOBAL.TASK_TYPE_VISA_VALUE)) {
					mUpdateBean.setType(GLOBAL.TASK_TYPE_VISA_KEY);
				}

				if (!saveData.get(names[4]).equals("")) {
					mUpdateBean.setOwner(Integer.parseInt((saveData.get(names[4]))));
				}

				if (!saveData.get(names[5]).equals("")) {
					mUpdateBean.setDepartment(Integer.parseInt((saveData.get(names[5]))));
				}
				if (mUpdateBean.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[0][0])
						|| mUpdateBean.getPublish() == Integer.parseInt(GLOBAL.PUBLISH_STATUS[3][0])) {
					mUpdateBean.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0]));
				}
				
				if (mType == 1) {
					mGroupService.setTask((ZH_group_task) mUpdateBean);
					mGroupService.updateTask(mTaskInterface);
				} else if (mType == 2) {
					mTaskService.setTask((Task) mUpdateBean);
					mTaskService.updateTask(mTaskInterface);
				}
				
			}
		});
		
		Bundle bundle = getArguments();
		String rolePermission = null;
		if (bundle != null) {
			rolePermission = bundle.getString("permission");
		}
		LogUtil.i("wzw per:" + rolePermission);
		if (rolePermission != null) {
			if (mType == 2 || mIsTaskPermission) {
				// 计划类型或者是使用计划的权限，但实际是组合类型，强制设置为计划权限
				if (rolePermission.contains(GLOBAL.SYS_ACTION[2][0])) {
					saveImageView.setClickable(true);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_blue_bg));
				}  else {
					saveImageView.setClickable(false);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_disable));
				}
			} else if (mType == 1) {
				if (rolePermission.contains(GLOBAL.SYS_ACTION[34][0])) {
					saveImageView.setClickable(true);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_blue_bg));
				}  else {
					saveImageView.setClickable(false);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_disable));
				}
			}
		} else {
			if (mType == 2 || mIsTaskPermission) {
				if (PermissionCache
						.hasSysPermission(GLOBAL.SYS_ACTION[2][0])) {
					saveImageView.setClickable(true);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_blue_bg));
				}  else {
					saveImageView.setClickable(false);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_disable));
				}
			} else if (mType == 1) {
				if (PermissionCache
						.hasSysPermission(GLOBAL.SYS_ACTION[34][0])) {
					saveImageView.setClickable(true);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_blue_bg));
				}  else {
					saveImageView.setClickable(false);
					saveImageView.setBackground(mActivity.getResources().getDrawable(
							R.drawable.button_corners_disable));
				}
			}
		}

	}
	
	private boolean mIsTaskPermission;
	public void setTaskPermission(boolean permission) {
		mIsTaskPermission = permission;
	}
	
	private DataManagerInterface mTaskInterface = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				MiscUtils.clone(mParentBean, mUpdateBean);
				if (mParentListAdapter != null) {
					mParentListAdapter.notifyDataSetChanged();
				}
				
			}
		}
	};
	
	@Override
	public void setParentList(DataTreeListAdapter<B> listAdapter) {
		mParentListAdapter = listAdapter;
	}
	
	private void setDefaultData() {
		
		if (mInitViewCommon == null) {
			return;
		}

		if (mParentBean == null) {
			mInitViewCommon.SetDefaultValue(null);
			return;
		}
		
		// 针对首次创建一条任务成功情况
		if (mParentListAdapter == null) {
			mCurrentLine = 0;
		} else {
			for (int i = 0; i < mParentListAdapter.getShowList().size(); i++) {
				if (mParentBean.getTask_id() == mParentListAdapter.getShowList().get(i).getTask_id()) {
					mCurrentLine = i;
					break;
				}
			}
		}
		
		String[] editTexts = new String[7];
		B task = MiscUtils.clone(mParentBean);
		int valuesCount = 0;
		editTexts[valuesCount++] = task.getName();
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, task.getStart_time());
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, task.getEnd_time());
		editTexts[valuesCount++] = task.getPlan_duration();
		editTexts[valuesCount++] = task.getOwner() == 0 ? "" : Integer.toString(task.getOwner());
		editTexts[valuesCount++] = task.getDepartment() == 0 ? "" : Integer.toString(task.getDepartment());

		if (task.getType() != null) {
			if (task.getType().equals(GLOBAL.TASK_TYPE_MILE_KEY)) {
				editTexts[valuesCount] = GLOBAL.TASK_TYPE_MILE_VALUE;
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_TASK_KEY)) {
				editTexts[valuesCount] = GLOBAL.TASK_TYPE_TASK_VALUE;
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
				editTexts[valuesCount] = GLOBAL.TASK_TYPE_WBS_VALUE;
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_VISA_KEY)) {
				editTexts[valuesCount] = GLOBAL.TASK_TYPE_VISA_VALUE;
			}
		}
		
		mInitViewCommon.SetDefaultValue(editTexts);
		
	}
	
	@Override
	public void handleParentEvent(B b){
		setDefaultData();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.i("reqC:" + requestCode + "resc:" + resultCode);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == OWNER_SELECT_REQUEST) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mInitViewCommon.setUserTextContent(4, user.getUser_id());
				mInitViewCommon.setOBSTextContent(5, user.getObs_id());	
			}
		}
	}
}

