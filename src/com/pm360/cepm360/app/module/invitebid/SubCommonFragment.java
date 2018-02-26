package com.pm360.cepm360.app.module.invitebid;

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
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZB_flow;
import com.pm360.cepm360.services.invitebid.RemoteZBFlowService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommonFragment extends
		BaseListRelevanceFragment<ZB_flow, ZB_flow> {

	// View
	private BaseWindow mInitViewCommon;
	private Activity mActivity;
	private View viewCommon;

	// data
	private DataTreeListAdapter<ZB_flow> mParentListAdapter;
	private ZB_flow mUpdateBean;

	// server
	private RemoteZBFlowService mService;

	// status
	private final int OWNER_SELECT_REQUEST = 201;
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
	protected void showNoViewPermission(LayoutInflater inflater,
			ViewGroup container) {
		initEnvironment();
		initViewCommon();
		mRootLayout = inflater.inflate(R.layout.base_activity, null, false);

		((ViewGroup) mRootLayout).addView(mInitViewCommon.getPopupView());

		setDefaultData();
	}

	private void initEnvironment() {
		mActivity = getActivity();
		mService = RemoteZBFlowService.getInstance();
	}

	@SuppressLint("UseSparseArrays")
	public void initViewCommon() {
		mInitViewCommon = new BaseWindow(mActivity);

		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseDialog.calendarLineStyle);
		buttons.put(1, BaseDialog.calendarLineStyle);
		buttons.put(2, BaseDialog.editTextReadOnlyLineStyle);
		buttons.put(3, BaseDialog.numberSeekBarLineStyle);
		buttons.put(4, BaseDialog.userSelectLineStyle);
		buttons.put(5, BaseDialog.OBSReadOnlyLineStyle);

		mInitViewCommon.init(R.array.invitebid_flow_common, buttons,
				null, false);

		final EditText startEt = (EditText) mInitViewCommon.getEditTextView(0);
		final EditText endEt = (EditText) mInitViewCommon.getEditTextView(1);
		final EditText durationEt = (EditText) mInitViewCommon
				.getEditTextView(2);

		final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
				mActivity, null, startEt, endEt, durationEt);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mParentBean == null) {

				} else {
					ZB_flow taskParent = null;
					if (mParentBean.getParents_id() != 0) {

						for (int i = mCurrentLine - 1; i >= 0; i--) {

							if (mParentListAdapter.getShowList().get(i)
									.getLevel() < mParentBean.getLevel()) {
								taskParent = (ZB_flow) mParentListAdapter
										.getShowList().get(i);
								break;
							}
						}
						if (taskParent == null || taskParent.getStart_date() == null) {
							return;
						} else {
							doubleDatePickerDialog.setDateLimit(
									taskParent.getStart_date(),
									taskParent.getEnd_date());
						}
						
					} else {
						doubleDatePickerDialog.clearDateLimit();
					}
					// 父节点存在，并且子节点开始日期为空
					if (mParentBean.getParents_id() != 0
							&& mParentBean.getStart_date() == null) {
						doubleDatePickerDialog.show(taskParent.getStart_date(),
								taskParent.getStart_date());
					} else {
						doubleDatePickerDialog.show(
								mParentBean.getStart_date(),
								mParentBean.getEnd_date());
					}
				}
			}
		};
		mInitViewCommon.setEditTextStyle(0, 0, listener, null);
		mInitViewCommon.setEditTextStyle(1, 0, listener, null);

		EditText owner = (EditText) mInitViewCommon.getPopupView()
				.findViewById(mInitViewCommon.baseEditTextId + 4);
		owner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), OwnerSelectActivity.class);
				intent.putExtra("title", mActivity.getString(R.string.owner));
				intent.putExtra("project", ProjectCache
						.getCurrentProject());
				startActivityForResult(intent, OWNER_SELECT_REQUEST);
			}
		});

		final String[] names = mActivity.getResources().getStringArray(
				R.array.invitebid_flow_common);
		viewCommon = mInitViewCommon.getPopupView();
		Button saveImageView = (Button) viewCommon
				.findViewById(R.id.save_Button);

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
					BaseToast.show(mActivity, BaseToast.NULL_MSG);
					return;
				}

				mUpdateBean = MiscUtils.clone(mParentBean);

				Date startTime = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						saveData.get(names[0]));
				Date endTime = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						saveData.get(names[1]));

				mUpdateBean.setStart_date(startTime);
				mUpdateBean.setEnd_date(endTime);

//				if (!mTaskPresenter.calculatorParentDate(
//						mParentListAdapter.getShowList(), startTime, endTime,
//						mCurrentLine, mParentBean)) {
//					return;
//				}
//
//				if (!mTaskPresenter.calculatorChildrenDate(
//						mParentListAdapter.getDataList(), mUpdateBean,
//						startTime, endTime)) {
//					return;
//				}
				
				mUpdateBean.setProgress(Integer.parseInt(saveData.get(names[3])));
				
				if (mUpdateBean.getProgress() == 0) {
					mUpdateBean.setStatus(Integer.parseInt(GLOBAL.TASK_STATUS_TYPE[0][0]));
				} else if (mUpdateBean.getProgress() == 100) {
					mUpdateBean.setStatus(Integer.parseInt(GLOBAL.TASK_STATUS_TYPE[2][0]));
				} else {
					mUpdateBean.setStatus(Integer.parseInt(GLOBAL.TASK_STATUS_TYPE[1][0]));
				}

				mUpdateBean.setPeriod(DateUtils.calculateDuration(startTime, endTime));

				if (!saveData.get(names[4]).equals("")) {
					mUpdateBean.setOwner(Integer.parseInt((saveData.get(names[4]))));
					
				}
				

				if (!saveData.get(names[5]).equals("")) {
					mUpdateBean.setDep(Integer.parseInt((saveData.get(names[5]))));
				}
				
				mService.updateZBFlow(mTaskInterface, mUpdateBean);

			}
		});

		{
			if (PermissionCache.hasSysPermission(
					GLOBAL.SYS_ACTION[50][0])) {
				saveImageView.setClickable(true);
				saveImageView.setBackground(mActivity.getResources()
						.getDrawable(R.drawable.button_corners_blue_bg));
			} else {
				saveImageView.setClickable(false);
				saveImageView.setBackground(mActivity.getResources()
						.getDrawable(R.drawable.button_disabled));
			}
		}
	}

	private DataManagerInterface mTaskInterface = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT)
					.show();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				MiscUtils.clone(mParentBean, mUpdateBean);
				mParentListAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void setParentList(DataTreeListAdapter<ZB_flow> listAdapter) {
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
				if (mParentBean.getId() == mParentListAdapter
						.getShowList().get(i).getId()) {
					mCurrentLine = i;
					break;
				}
			}
		}

		String[] editTexts = new String[6];
		ZB_flow flow = MiscUtils.clone(mParentBean);
		int valuesCount = 0;
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, flow.getStart_date());
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, flow.getEnd_date());
		editTexts[valuesCount++] = flow.getPeriod() == 0 ? "" : flow.getPeriod() + getString(R.string.plan_day);
		editTexts[valuesCount++] = Integer.toString(flow.getProgress());
		editTexts[valuesCount++] = flow.getOwner() == 0 ? "" : Integer.toString((flow.getOwner()));
		editTexts[valuesCount++] = flow.getDep() == 0 ? "" : Integer.toString((flow.getDep()));

		mInitViewCommon.SetDefaultValue(editTexts);

	}

	@Override
	public void handleParentEvent(ZB_flow b) {
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
			mInitViewCommon.setUserTextContent(4, user.getUser_id());
			mInitViewCommon.setOBSTextContent(5, user.getObs_id());
		}
	}
}
