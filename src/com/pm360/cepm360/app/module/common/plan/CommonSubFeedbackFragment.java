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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.view.NumberSeekBar;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.CEPMDatePickerDialog;
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
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.services.plan.RemoteTaskService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommonSubFeedbackFragment<T extends FeedbackCell , B extends TaskCell> extends BaseListRelevanceFragment<T, B> {

	private BaseWindow mInitViewFeedback;
	private Activity mActivity;
	private View viewFeedback;
	
	private DataTreeListAdapter<B> mListAdapter; //正常模式需要设置该内容，门户模式进入不设置或设置为null
	private List<T> mFeedbackList;
	
	private T mFeedback;
	// 更新成功后，刷新列表数据
	private B mUpdateBean;
	private ZH_TaskPresenter<B> mTaskPresenter;
	
	private com.pm360.cepm360.services.group.RemoteTaskService mGroupService;
	private RemoteTaskService mTaskService;
	
	private final int OWNER_SELECT_REQUEST = 201;
	private int mType;
	
	private boolean mIsIndexMode;
	
	// id list split by ,
	private String mUserIds;
	
	private boolean mHasEditPermissions;

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
		initViewFeedback();

		mRootLayout = inflater.inflate(R.layout.base_activity, null, false);
		
		((ViewGroup) mRootLayout).addView(mInitViewFeedback.getPopupView());

		setDefaultData();
	}
	
	public void setIndexMode(boolean indexMode) {
		mIsIndexMode = indexMode;
	}
	
	private void initEnvironment() {
		mType = getType();
		mActivity = getActivity();
		mTaskPresenter = new ZH_TaskPresenter<B>(getActivity());
		mFeedback = getCommonBean();
		mTaskService = RemoteTaskService.getInstance();
		mGroupService = com.pm360.cepm360.services.group.RemoteTaskService.getInstance();
		
		// 反馈默认有编辑权限
		mHasEditPermissions = true;
//		if (rolePermission != null) {
//			if (mType == 1) {
//				if (rolePermission.contains(GLOBAL.SYS_ACTION[52][0])) {
//					mHasEditPermissions = true;
//				}  else {
//					mHasEditPermissions = false;
//				}
//			} else if (mType == 2) {
//				if (rolePermission.contains(GLOBAL.SYS_ACTION[4][0])) {
//					mHasEditPermissions = true;
//				}  else {
//					mHasEditPermissions = false;
//				}
//			}
//		} else {
//			if (mType == 1) {
//				if (PermissionCache
//						.hasSysPermission(GLOBAL.SYS_ACTION[52][0])) {
//					mHasEditPermissions = true;
//				}  else {
//					mHasEditPermissions = false;
//				}
//			} else if (mType == 2) {
//				if (PermissionCache
//						.hasSysPermission(GLOBAL.SYS_ACTION[4][0])) {
//					mHasEditPermissions = true;
//				}  else {
//					mHasEditPermissions = false;
//				}
//
////				LogUtil.i("wzw mHasEditPermissions:" + mHasEditPermissions
////						+ "PermissionCache:" + Arrays.toString(PermissionCache.getProjectPermissions()));
//			}
//		}
		
	}
	
	protected abstract int getType();
	
	protected abstract T getCommonBean();

	private int mCurrentProgress;
	@SuppressLint("UseSparseArrays") 
	public void initViewFeedback() {
		// init feedback window
		mInitViewFeedback = new BaseWindow(mActivity);

		Map<Integer, Integer> buttons1 = new HashMap<Integer, Integer>();
		buttons1.put(2, BaseDialog.numberSeekBarLineStyle);
		buttons1.put(3, BaseDialog.editTextClickLineStyle);
		buttons1.put(4, BaseDialog.editTextReadOnlyLineStyle);
		//buttons1.put(3, BaseDialog.multiAutoCompleteTextViewStyle);
		buttons1.put(5, BaseDialog.remarkEditTextLineStyle);

		Map<Integer, String[]> widgetContent1 = new HashMap<Integer, String[]>();
		String[] radioContent1 = mActivity.getResources().getStringArray(R.array.feedback_status);
		widgetContent1.put(4, radioContent1);
		//widgetContent1.put(3, mUserNames);
		mInitViewFeedback.init(R.array.task_feedback_tab_attr, buttons1,
				widgetContent1, false);

		viewFeedback = mInitViewFeedback.getPopupView();
		
		if (!mHasEditPermissions) {
//			mInitViewFeedback.switchModifyWindow(false);
			return;
		}
		
		final NumberSeekBar seekBar = (NumberSeekBar) viewFeedback.findViewById(mInitViewFeedback.baseLineStyleId + 2);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (mCurrentProgress == 100) {
					if (mInitViewFeedback.getEditTextView(0).getText().toString().isEmpty()) {
						mInitViewFeedback.setEditTextContent(0, DateUtils.getToday());
					}

					if (mInitViewFeedback.getEditTextView(1).getText().toString().isEmpty()) {
						mInitViewFeedback.setEditTextContent(1, DateUtils.getToday());
						mInitViewFeedback.setEditTextContent(4, GLOBAL.FEEDBACK_STATUS_2_VALUE);
					}
					
				} else if (mCurrentProgress != 0) {
					if (mInitViewFeedback.getEditTextView(0).getText().toString().isEmpty()) {
						mInitViewFeedback.setEditTextContent(0, DateUtils.getToday());
						mInitViewFeedback.setEditTextContent(4, GLOBAL.FEEDBACK_STATUS_1_VALUE);
					}
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					mCurrentProgress = progress;
				}
			}
		});
		
		final CEPMDatePickerDialog startDialog = new CEPMDatePickerDialog(mActivity,  
				new CEPMDatePickerDialog.OnDateSetListener() {
					
					@SuppressLint("DefaultLocale") @Override
					public void onDateSet(DatePicker datePicker, int startYear,
							int startMonthOfYear, int startDayOfMonth) {
						String startDate = String.format("%d-%d-%d", startYear,
								startMonthOfYear + 1, startDayOfMonth);

						if (mInitViewFeedback.getEditTextView(0).getText().toString().isEmpty()) {
							mInitViewFeedback.setEditTextContent(4, GLOBAL.FEEDBACK_STATUS_1_VALUE);
						}
						mInitViewFeedback.setEditTextContent(0, startDate);
						
					}
				}, mActivity.getString(R.string.select_time));
		mInitViewFeedback.setEditTextStyle(0, R.drawable.dialog_calendar, new OnClickListener() {

			@Override
			public void onClick(View v) {
				startDialog.show();
			}
			
		}, "");
		
		final CEPMDatePickerDialog endDialog = new CEPMDatePickerDialog(mActivity,  
				new CEPMDatePickerDialog.OnDateSetListener() {
					
					@SuppressLint("DefaultLocale") @Override
					public void onDateSet(DatePicker datePicker, int startYear,
							int startMonthOfYear, int startDayOfMonth) {
						String endDate = String.format("%d-%d-%d", startYear,
								startMonthOfYear + 1, startDayOfMonth);

						mInitViewFeedback.setEditTextContent(4, GLOBAL.FEEDBACK_STATUS_2_VALUE);
						
						seekBar.setProgress(100);
						mInitViewFeedback.setEditTextContent(1, endDate);
						
					}
				}, mActivity.getString(R.string.select_time));
		mInitViewFeedback.setEditTextStyle(1, R.drawable.dialog_calendar, new OnClickListener() {

			@Override
			public void onClick(View v) {
				String startTime = mInitViewFeedback.getEditTextView(0).getText().toString();
				if (!startTime.isEmpty()) {
					Date startDate = DateUtils.stringToDate(DateUtils.FORMAT_SHORT, startTime);
					endDialog.setDateLimit(startDate, null);
					endDialog.show();
				} else {
					Toast.makeText(mActivity, getString(R.string.pls_select_start_time), Toast.LENGTH_SHORT).show();
				}
			}
			
		}, "");
		
		
		EditText editText = (EditText) viewFeedback.findViewById(mInitViewFeedback.baseEditTextId + 3);
		editText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
                intent.setClass(getActivity(), OwnerSelectActivity.class);
                intent.putExtra("title", mActivity.getString(R.string.cc_user));
                intent.putExtra("multiselect" ,true);
				if (mUserIds != null && !mUserIds.equals("")) {
	                intent.putExtra("user_list", mUserIds);
	                LogUtil.i("wzw userList:" + mUserIds);
				}
                intent.putExtra("project", mCurrentProject);
                startActivityForResult(intent, OWNER_SELECT_REQUEST);
				
			}
		});

		Button saveImageView = (Button) viewFeedback
				.findViewById(R.id.save_Button);
		RelativeLayout.LayoutParams btnParams = (RelativeLayout.LayoutParams) saveImageView.getLayoutParams();
		if (mIsIndexMode) {
			btnParams.setMarginStart(UtilTools.dp2pxW(mActivity, 540));
		} else {
			btnParams.setMarginStart(UtilTools.dp2pxW(mActivity, 480));
		}
		
		saveImageView.setLayoutParams(btnParams);
		
		final String[] names = mActivity.getResources().getStringArray(
				R.array.task_feedback_tab_attr);

		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mParentBean == null) {
					BaseToast.show(mActivity.getBaseContext(),
							BaseToast.NO_TASK_EXIST);
					return;
				}
				Map<String, String> saveData = mInitViewFeedback.SaveData();
				mUpdateBean = MiscUtils.clone(mParentBean);

				Date startTime = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						saveData.get(names[0]));
				Date endTime = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						saveData.get(names[1]));

//				int position = 0;
//				for (int i = 0; i < mListAdapter.getShowList().size(); i++) {
//					if (mParentBean.getId() == mListAdapter.getShowList().get(i).getId()) {
//						position = i;
//						break;
//					}
//				}
//				if (!mTaskPresenter.calculatorParentStartTime(startTime, position, mParentBean, mListAdapter)) {
//					return;
//				}
//
//				if (endTime != null) {
//					if (!mTaskPresenter.calculatorChildEndTime(startTime,
//							endTime, mParentBean, mListAdapter)) {
//						return;
//					}
//				}

				int progress = 0;
				if (!saveData.get(names[2]).equals("")) {
					progress = Integer.parseInt(saveData.get(names[2]));
				}

				mUpdateBean.setActual_start_time(startTime);
				mUpdateBean.setActual_end_time(endTime);

				if (saveData.get(names[2]) != null && !saveData.get(names[2]).equals("")) {
					mUpdateBean.setProgress(progress);
				}
				
				String ccUsers = saveData.get(names[3]);
				LogUtil.i("ccUser:" + ccUsers);
				if (!ccUsers.equals("")) {
					String ccUser[] = ccUsers.split(",");
					for (int i = 0; i < ccUser.length; i++) {
						for (int j = i + 1; j < ccUser.length; j++) {
							if (ccUser[i].equals(ccUser[j])) {
								Toast.makeText(mActivity, R.string.feedback_equal_cc, Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}
					int flag = 0;
					String ccUserIds = "";
					Map<String, Integer> userMap = UserCache.getUserListsMap();
					for (int i = 0; i < ccUser.length; i++) {
						if(userMap.containsKey(ccUser[i])) {
							ccUserIds += "(" + userMap.get(ccUser[i]) + "),";
							flag++;
						}
					}
					LogUtil.i("ccUserIds:" + ccUserIds);
					
					if (flag == ccUser.length) {
						mUpdateBean.setCc_user(ccUserIds.substring(0, ccUserIds.length() - 1));
					} else {
						Toast.makeText(mActivity, R.string.feedback_cc_null, Toast.LENGTH_SHORT).show();
						return;
					}
				}
				
				if (progress == 0) {
					if (startTime == null) {
						mUpdateBean.setStatus(GLOBAL.FEEDBACK_STATUS_0);
					} else {
						mUpdateBean.setStatus(GLOBAL.FEEDBACK_STATUS_1);
					}
				} else if (progress == 100) {
					mUpdateBean.setStatus(GLOBAL.FEEDBACK_STATUS_2);
				} else {
					mUpdateBean.setStatus(GLOBAL.FEEDBACK_STATUS_1);
				} 
				
				mUpdateBean.setMark(saveData.get(names[5]));
				
				mProgressDialog = UtilTools.showProgressDialog(mActivity, false, false);
				
				if (mListAdapter != null) {
					//正常模式
					mTaskPresenter.setRemoteFeedbackCache(mFeedback, mUpdateBean, mType, mCurrentProject, mListAdapter.getDataList(), mFeedbackList);
				} else {
					//门户进入模式 
					mFeedback = mFeedbackList.get(0);
					mTaskPresenter.setRemoteFeedbackCache(mFeedback, mUpdateBean, mType, mCurrentProject);
				}
				
				if (mFeedback.getFeedback_id() != 0) {
					if (mType == 1) {
						mGroupService.setZH_group_feedback((ZH_group_feedback)mFeedback);
						mGroupService.updateFeedback(mFeedbackManager, UserCache.getCurrentUser());
					} else if (mType == 2) {
						mTaskService.setFeedback((Feedback)mFeedback);
						mTaskService.updateFeedback(mFeedbackManager, UserCache.getCurrentUser());
					}	
				} else {
					if (mType == 1) {
						mGroupService.setZH_group_feedback((ZH_group_feedback)mFeedback);
						mGroupService.addFeedback(mFeedbackManager, UserCache.getCurrentUser());
					} else if (mType == 2) {
						mTaskService.setFeedback((Feedback)mFeedback);
						mTaskService.addFeedback(mFeedbackManager, UserCache.getCurrentUser());
					}
				}
			}
		});
	}
	
	private DataManagerInterface mFeedbackManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE
					|| status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				if (list != null && !list.isEmpty()) {
					// 增加模式，有返回数据 门户模式，不需要添加到feedbackList列表
					if (mListAdapter != null) {
						mFeedbackList.add((T)list.get(0));
					}
					
					Toast.makeText(mActivity, R.string.feedback_successful, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
				}
				MiscUtils.clone(mParentBean, mUpdateBean);

				if (mListAdapter != null) {
					mListAdapter.notifyDataSetChanged();
				}
				
				UtilTools.sendBroadcast(getActivity(), Integer.parseInt(GLOBAL.MSG_TYPE_KEY[8][0]));
			}
			mProgressDialog.dismiss();
		}
	};
	
	@Override
	public void setParentList(DataTreeListAdapter<B> listAdapter) {
		mListAdapter = listAdapter;
	}
	
	@Override
	public void setCurrentList(List<T> list) {
		mFeedbackList = list;
	}
	
	private void setDefaultData() {

		if (mInitViewFeedback == null) {
			return;
		}
		
		if (mParentBean == null) {
			mInitViewFeedback.SetDefaultValue(null);
			return;
		}

		String[] editTexts = new String[6];
		B task = mParentBean;
		if (task.getActual_start_time() != null) {
			editTexts[0] = DateUtils
					.dateToString(DateUtils.FORMAT_SHORT,
							task.getActual_start_time());
		} else {
			mInitViewFeedback.setEditTextStyle(0, 0, null, getString(R.string.plan_start_time) + ":" + DateUtils.dateToString(
					DateUtils.FORMAT_SHORT, task.getStart_time()));
		}
		if (task.getActual_end_time() != null) {
			editTexts[1] = DateUtils.dateToString(
					DateUtils.FORMAT_SHORT, task.getActual_end_time());
		} else {
			mInitViewFeedback.setEditTextStyle(1, 0, null, getString(R.string.plan_end_time) + ":" + DateUtils.dateToString(
					DateUtils.FORMAT_SHORT, task.getEnd_time()));
		}

		editTexts[2] = Integer.toString(task.getProgress());
		
		String initCcUserIds = task.getCc_user();
		Map<String, String> mUserMap = UserCache.getUserMaps();
		if (initCcUserIds != null && !initCcUserIds.equals("")) {
			String ccUserIds = initCcUserIds.replaceAll("\\(|\\)", "");
			mUserIds = ccUserIds;
			String ccUserId[] = ccUserIds.split(",");
			String ccUserNames = "";
			for (int i = 0; i < ccUserId.length; i++) {
				if (mUserMap.containsKey(ccUserId[i])) {
					ccUserNames += mUserMap.get(ccUserId[i]) + ",";
				}
			}
			LogUtil.i("ccUserNames:" + ccUserNames);
			editTexts[3] = ccUserNames.substring(0, ccUserNames.length() - 1);
		} else {
			mUserIds = "";
		}
		
		if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_0) {
			editTexts[4] = GLOBAL.FEEDBACK_STATUS_0_VALUE;
		} else if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_1) {
			editTexts[4] = GLOBAL.FEEDBACK_STATUS_1_VALUE;
		} else if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_2) {
			editTexts[4] = GLOBAL.FEEDBACK_STATUS_2_VALUE;
		}
		
		editTexts[5] = task.getMark();
		
		mInitViewFeedback.SetDefaultValue(editTexts);
		
		Button saveImageView = (Button) viewFeedback
				.findViewById(R.id.save_Button);
		
		if (mHasEditPermissions && UserCache.getCurrentUser().getUser_id() == task.getOwner()) {
			saveImageView.setClickable(true);
			saveImageView.setBackground(mActivity.getResources().getDrawable(R.drawable.button_corners_blue_bg));
		} else {
			saveImageView.setClickable(false);
			saveImageView.setBackground(mActivity.getResources().getDrawable(R.drawable.button_corners_disable));
		}
	}
	
	@Override
	public void handleParentEvent(B b){
		setDefaultData();
	}
	
	private void setUserList(Intent data) {
		@SuppressWarnings("unchecked")
		List<User> userList = (List<User>) data.getSerializableExtra("user_list");
		EditText editText = (EditText) mInitViewFeedback.getPopupView().findViewById(mInitViewFeedback.baseEditTextId + 3);
		if (userList != null) {
			String names = "";
			mUserIds = "";
			for (int i = 0; i < userList.size(); i++) {
				names += userList.get(i).getName() + ",";
				mUserIds += userList.get(i).getUser_id() + ",";
			}
			editText.setText(names.equals("") ? "" : names.substring(0, names.length() - 1));
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.i("reqC:" + requestCode + "resc:" + resultCode);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
			
		if (requestCode == OWNER_SELECT_REQUEST) {
			setUserList(data);
		} 
	}
}

