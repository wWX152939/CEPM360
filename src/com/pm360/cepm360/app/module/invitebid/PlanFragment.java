package com.pm360.cepm360.app.module.invitebid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseSmartDoubleWidgetWindow;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZB_flow;
import com.pm360.cepm360.entity.ZB_plan;
import com.pm360.cepm360.services.invitebid.RemoteZBPlanService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanFragment extends AttachmentFragment<ZB_plan, ZB_flow> {
	// View
	private BaseSmartDoubleWidgetWindow mSmartWindow;
	private Activity mActivity;
	private View viewCommon;
	
	// data
	private ZB_plan mUpdateBean;
	private String[] mLableNames;
	
	// server
	private RemoteZBPlanService mService;
	
    /* status   */
    protected static int OWNER_SELECT_REQUEST = 101;   
    
    protected Button getAttachUploadButton() {
		return (Button) viewCommon.findViewById(R.id.save_Button);
    }
    
    protected void doAttachOnClickEvent() {
    	
    }
    
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setPermissionIdentity(null, null);

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	protected void saveButtonInit() {
		
	}

	/**
	 * 自定义布局
	 */
	protected void showNoViewPermission(LayoutInflater inflater,
			ViewGroup container) {
		initEnvironment();
		
		mRootLayout = inflater.inflate(R.layout.base_activity, null, false);
		initPlanView();
	}
	
	public void initPlanView() {
		viewHandler.sendMessageDelayed(new Message(), 400);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler viewHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			initViewCommon();
			((ViewGroup) mRootLayout).addView(mSmartWindow.getPopupView());
		}
	};

	private void initEnvironment() {
		mActivity = getActivity();
		mCurrentItem = new ZB_plan();
		mService = RemoteZBPlanService.getInstance();
		setCurrentProject(ProjectCache.getCurrentProject());
    	Bundle data = getArguments();
    	if (data.get(CommonFragment.CURRENT_BEAN_KEY) != null) {
    		mParentBean = (ZB_flow) data.get(CommonFragment.CURRENT_BEAN_KEY);
    	}
    	mProgressDialog = new ProgressDialog(getActivity());
		
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
	}
	
	private void createPriceLine() {
		mChildLine = new LinearLayout(getActivity());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mChildLine.setVisibility(View.GONE);
		params.setLayoutDirection(0);
		mChildLine.setLayoutParams(params);
		mEditText1 = (EditText) LayoutInflater.from(mActivity).inflate(
				R.layout.base_dialog_decimal_edit_text, mChildLine, false);
		mEditText1.setBackground(getResources().getDrawable(R.drawable.bg_edittext));
		mEditText2 = (EditText) LayoutInflater.from(mActivity).inflate(
				R.layout.base_dialog_decimal_edit_text, mChildLine, false);
		mEditText2.setBackground(getResources().getDrawable(R.drawable.bg_edittext));
		mEditText1.setTextColor(Color.BLACK);
		mEditText1.setGravity(Gravity.BOTTOM);
		mEditText2.setTextColor(Color.BLACK);
		mEditText2.setGravity(Gravity.BOTTOM);
		TextView tv = new TextView(getActivity());
		tv.setText("一");
		tv.setGravity(Gravity.CENTER_VERTICAL);

		int etW = UtilTools.dp2pxW(mActivity, 80);
		int tvW = UtilTools.dp2pxW(mActivity, 20);
		LinearLayout.LayoutParams etParams1 = new LinearLayout.LayoutParams(etW, LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams etParams2 = new LinearLayout.LayoutParams(etW, LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(tvW, LayoutParams.MATCH_PARENT);

		int pxW8 = UtilTools.dp2pxW(mActivity.getBaseContext(), 8);
		int pxW4 = UtilTools.dp2pxW(mActivity.getBaseContext(), 4);
		int pxH = UtilTools.dp2pxH(mActivity.getBaseContext(), 8);
		etParams1.setMargins(pxW8, pxH, pxW4, pxH);
		mEditText1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEditText1.setText(Double.toString(mCurrentItem.getPrice1()));
			}
		});
		mEditText2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEditText2.setText(Double.toString(mCurrentItem.getPrice2()));
			}
		});
//		mEditText.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				LogUtil.i("wzw c");
////				mEditText.setText(Double.toString(mCurrentItem.getPrice2()));
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				LogUtil.i("wzw b");
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				LogUtil.i("wzw a");
//			}
//		});
		mChildLine.addView(mEditText1, etParams1);
		mChildLine.addView(tv, tvParams);

		etParams2.setMargins(pxW4, pxH, 0, pxH);
		mChildLine.addView(mEditText2, etParams2);
	}

	private LinearLayout mChildLine;
	private EditText mEditText;
	private EditText mEditText1;
	private EditText mEditText2;
	@SuppressLint("UseSparseArrays")
	private void initViewCommon() {

		mSmartWindow = new BaseSmartDoubleWidgetWindow(mActivity, new BaseWidgetInterface() {
			
			@Override
			public TwoNumber<View, android.widget.LinearLayout.LayoutParams> createExtraLayout()  {
				return new TwoNumber<View, android.widget.LinearLayout.LayoutParams>(mDocumentUploadView.getView(), null);
			}

			@Override
			public Integer[] getImportantColumns() {
				// TODO Auto-generated method stub
				return new Integer[] {1, 4};
			}
		});

		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
		buttons.put(2, BaseDialog.checkboxLineStyle);
		buttons.put(3, BaseDialog.decimalEditTextLineStyle);
		// TODO 4 type 5 mode
		buttons.put(4, BaseDialog.spinnerLineStyle);
		buttons.put(5, BaseDialog.spinnerLineStyle);
		buttons.put(6, BaseDialog.userSelectLineStyle);
		buttons.put(7, BaseDialog.OBSReadOnlyLineStyle);
		buttons.put(9, BaseDialog.radioLineStyle);
		buttons.put(10, BaseDialog.calendarLineStyle);
		buttons.put(11, BaseDialog.calendarLineStyle);
		buttons.put(12, BaseDialog.calendarLineStyle);
		buttons.put(13, BaseDialog.calendarLineStyle);
		buttons.put(14, BaseDialog.calendarLineStyle);
		buttons.put(15, BaseDialog.calendarLineStyle);
		buttons.put(16, BaseDialog.decimalEditTextLineStyle);
		buttons.put(17, BaseDialog.decimalEditTextLineStyle);
		buttons.put(18, BaseDialog.remarkEditTextLineStyle);
		buttons.put(19, BaseDialog.remarkEditTextLineStyle);
		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();
		widgetContent.put(2, new String[] {getString(R.string.secrecy)});
		String[] type = new String[GLOBAL.INVATE_BID_TYPE.length];
		for (int i = 0; i < GLOBAL.INVATE_BID_TYPE.length; i++) {
			type[i] = GLOBAL.INVATE_BID_TYPE[i][1];
		}
		widgetContent.put(4, type);
		String[] mode = new String[GLOBAL.INVATE_BID_MODE.length];
		for (int i = 0; i < GLOBAL.INVATE_BID_MODE.length; i++) {
			mode[i] = GLOBAL.INVATE_BID_MODE[i][1];
		}
		widgetContent.put(5, mode);
		widgetContent.put(9, new String[] {GLOBAL.TASK_STATUS_TYPE[0][1], 
				GLOBAL.TASK_STATUS_TYPE[1][1], GLOBAL.TASK_STATUS_TYPE[2][1]});
		List<Integer> oneList = new ArrayList<Integer>();
		oneList.add(18);
		oneList.add(19);
		mSmartWindow.init(R.array.invitebid_plan, buttons,
				widgetContent, oneList);
		CheckBox cb = (CheckBox) mSmartWindow.getPopupView()
				.findViewById(mSmartWindow.checkBoxId + 20);
		final LinearLayout line = (LinearLayout) mSmartWindow.getPopupView()
				.findViewById(mSmartWindow.baseLineId + 1);
		mEditText = (EditText) mSmartWindow.getPopupView()
				.findViewById(mSmartWindow.baseEditTextId + 3);
		
		mEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LogUtil.i("wzw onClick");
				mEditText.setText("");
//				mEditText.setText(Double.toString(mCurrentItem.getPrice2()));
			}
		});

		createPriceLine();
		line.addView(mChildLine);
		
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					mEditText.setVisibility(View.VISIBLE);
					mChildLine.setVisibility(View.GONE);
				} else {
					mEditText.setVisibility(View.GONE);
					mChildLine.setVisibility(View.VISIBLE);
				}
			}
		});

		EditText owner = (EditText) mSmartWindow.getPopupView()
				.findViewById(mSmartWindow.baseEditTextId + 6);
		owner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), OwnerSelectActivity.class);
				intent.putExtra("title", mActivity.getString(R.string.owner));
				intent.putExtra("project", ProjectCache
						.getCurrentProject());
				getParentFragment().startActivityForResult(intent,
						OWNER_SELECT_REQUEST + getRatio());
			}
		});

		mLableNames = mActivity.getResources().getStringArray(
				R.array.invitebid_plan);
		viewCommon = mSmartWindow.getPopupView();
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
				
				//TODO

				Map<String, String> saveData = mSmartWindow.SaveData();
				if (saveData.get(mLableNames[1]).equals("")
						|| saveData.get(mLableNames[4]).equals("")) {
					// || saveData.get(names[5]).equals("")) {
					BaseToast.show(mActivity, BaseToast.NULL_MSG);
					return;
				}

				mDocumentUploadView.uploadButtonEvent(false);

//					if (!mTaskPresenter.calculatorParentDate(
//							mParentListAdapter.getShowList(), startTime, endTime,
//							mCurrentLine, mParentBean)) {
//						return;
//					}
//
//					if (!mTaskPresenter.calculatorChildrenDate(
//							mParentListAdapter.getDataList(), mUpdateBean,
//							startTime, endTime)) {
//						return;
//					}

				dialogSaveButtonEvent();

			}
		});

		{
			if (PermissionCache.hasSysPermission(
					GLOBAL.SYS_ACTION[50][0])) {
				saveImageView.setClickable(true);
				saveImageView.setBackground(mActivity.getResources()
						.getDrawable(R.drawable.button_normal));
			} else {
				saveImageView.setClickable(false);
				saveImageView.setBackground(mActivity.getResources()
						.getDrawable(R.drawable.button_disabled));
			}
		}
	}
	
	private void saveCurrentBean(Map<String, String> saveData, String[] names) {
		mUpdateBean = MiscUtils.clone(mCurrentItem);
		mUpdateBean.setNumber(saveData.get(names[0]));
		mUpdateBean.setName(saveData.get(names[1]));
		if (saveData.get(names[2]).equals(getString(R.string.secrecy))) {
			mUpdateBean.setSecrecy(1);
			mUpdateBean.setPrice2(UtilTools.backFormatMoney("¥", saveData.get(names[3])));
		} else {
			mUpdateBean.setSecrecy(0);
			mUpdateBean.setPrice1(UtilTools.backFormatMoney("¥", mEditText1.getText().toString()));
			mUpdateBean.setPrice2(UtilTools.backFormatMoney("¥", mEditText2.getText().toString()));
		}
		
		for (int i = 0; i < GLOBAL.INVATE_BID_TYPE.length; i++) {
			if (saveData.get(names[4]).equals(GLOBAL.INVATE_BID_TYPE[i][1])) {
				mUpdateBean.setType(Integer.parseInt(GLOBAL.INVATE_BID_TYPE[i][0]));
				break;
			}
		}
		for (int i = 0; i < GLOBAL.INVATE_BID_MODE.length; i++) {
			if (saveData.get(names[5]).equals(GLOBAL.INVATE_BID_MODE[i][1])) {
				mUpdateBean.setMode(Integer.parseInt(GLOBAL.INVATE_BID_MODE[i][0]));
				break;
			}
		}
		if (!saveData.get(names[6]).isEmpty())
			mUpdateBean.setOperator(Integer.parseInt(saveData.get(names[6])));
		if (!saveData.get(names[7]).isEmpty())
			mUpdateBean.setDepartment(Integer.parseInt(saveData.get(names[7])));
		mUpdateBean.setLicense_number(saveData.get(names[8]));
		for (int i = 0; i < GLOBAL.TASK_STATUS_TYPE.length; i++) {
			if (saveData.get(names[9]).equals(GLOBAL.TASK_STATUS_TYPE[i][1])) {
				mUpdateBean.setStatus(Integer.parseInt(GLOBAL.TASK_STATUS_TYPE[i][0]));
				break;
			}
		}

		mUpdateBean.setPlan_start(String2Date(saveData, names, 10));
		mUpdateBean.setPlan_end(String2Date(saveData, names, 11));
		mUpdateBean.setOpen_start(String2Date(saveData, names, 12));
		mUpdateBean.setOpen_end(String2Date(saveData, names, 13));
		mUpdateBean.setEvaluation_start(String2Date(saveData, names, 14));
		mUpdateBean.setEvaluation_end(String2Date(saveData, names, 15));
		if (!saveData.get(names[16]).isEmpty())
			mUpdateBean.setTec_score(Double.parseDouble(saveData.get(names[16])));
		if (!saveData.get(names[17]).isEmpty())
			mUpdateBean.setBus_score(Double.parseDouble(saveData.get(names[17])));
		mUpdateBean.setPre_condition(saveData.get(names[18]));
		mUpdateBean.setMark(saveData.get(names[19]));
	}

	@Override
	protected void handleServerData(String attachment) {
		Map<String, String> saveData = mSmartWindow.SaveData();
		saveCurrentBean(saveData, mLableNames);
		mUpdateBean.setAttachment(attachment);
		mService.updateZBPlan(mPlanInterface, mUpdateBean);
	}
	
	private Date String2Date(Map<String, String> saveData, String[] names, int i) {
		return DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
				saveData.get(names[i]));
	}

	private DataManagerInterface mPlanInterface = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			sendMessage(DISMISS_PROGRESS_DIALOG);
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT)
				.show();
				
			}
			
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				MiscUtils.clone(mCurrentItem, mUpdateBean);
				setServerData();
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					mCurrentItem = (ZB_plan) list.get(0);
					setServerData();
				}
			}
		}
	};
	
	private void setServerData() {
		String[] editTexts = new String[20];
		int valuesCount = 0;
		editTexts[valuesCount++] = mCurrentItem.getNumber();
		editTexts[valuesCount++] = mCurrentItem.getName();
		editTexts[valuesCount++] = mCurrentItem.getSecrecy() == 1 ? getResources().getString(R.string.secrecy) : "";
		editTexts[valuesCount++] = UtilTools.formatMoney("¥", mCurrentItem.getPrice2(), 2);
		if (mCurrentItem.getType() == 0) {
			editTexts[valuesCount++] = "";
		} else {
			editTexts[valuesCount++] = GLOBAL.INVATE_BID_TYPE[mCurrentItem.getType() - 1][1];
		}
		if (mCurrentItem.getMode() == 0) {
			editTexts[valuesCount++] = "";
		} else {
			editTexts[valuesCount++] = GLOBAL.INVATE_BID_MODE[mCurrentItem.getMode() - 1][1];
		}
		
		String operator = Integer.toString(mCurrentItem.getOperator());
		editTexts[valuesCount++] = operator.equals("0") ? "" : operator;
		String department = Integer.toString(mCurrentItem.getDepartment());
		editTexts[valuesCount++] = department.equals("0") ? "" : department;
		editTexts[valuesCount++] = mCurrentItem.getLicense_number();
		editTexts[valuesCount++] = GLOBAL.TASK_STATUS_TYPE[mCurrentItem.getStatus()][1];
		
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, mCurrentItem.getPlan_start());
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, mCurrentItem.getPlan_end());
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, mCurrentItem.getOpen_start());
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, mCurrentItem.getOpen_end());
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, mCurrentItem.getEvaluation_start());
		editTexts[valuesCount++] = DateUtils.dateToString(
				DateUtils.FORMAT_SHORT, mCurrentItem.getEvaluation_end());
		
		String score = Double.toString(mCurrentItem.getTec_score());
		editTexts[valuesCount++] = score.equals("0.0") ? "" : score;
		score = Double.toString(mCurrentItem.getBus_score());
		editTexts[valuesCount++] = score.equals("0.0") ? "" : score;
		editTexts[valuesCount++] = mCurrentItem.getPre_condition();
		editTexts[valuesCount++] = mCurrentItem.getMark();
		
		if (mCurrentItem.getSecrecy() == 0) {
			mEditText.setVisibility(View.GONE);
			mChildLine.setVisibility(View.VISIBLE);
			mEditText1.setText(UtilTools.formatMoney("¥", mCurrentItem.getPrice1(), 2));
			mEditText2.setText(UtilTools.formatMoney("¥", mCurrentItem.getPrice2(), 2));
		} else {
			mEditText.setVisibility(View.VISIBLE);
			mChildLine.setVisibility(View.GONE);
		}
		
		mSmartWindow.SetDefaultValue(editTexts);

		clearCacheData();
		mDocumentUploadView.setDefaultData(false);
		
	}

	private boolean setDefaultData() {

		if (mSmartWindow == null) {
			return false; 
		}

		if (mParentBean == null) {
			mSmartWindow.SetDefaultValue(null);
			return false;
		}

		mCurrentItem.setZb_plan_id(mParentBean.getZb_plan_id());
		
		mService.getZBPlan(mPlanInterface, mParentBean.getZb_plan_id());	

		return true;
	}

	@Override
	public void handleParentEvent(ZB_flow b) {
		if (setDefaultData()) {
			sendMessage(SHOW_PROGRESS_DIALOG);
		}
		
		if (mParentBean != null) {
			mZBFlowNumber = InviteBidDataCache.getInstance().getPlanIdCache().get(mParentBean.getZb_plan_id());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.i("wzw reqC:" + requestCode + "resc:" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == OWNER_SELECT_REQUEST + getRatio()) {
			User user = (User) data.getSerializableExtra("user");
			mSmartWindow.setUserTextContent(6, user.getUser_id());
			mSmartWindow.setOBSTextContent(7, user.getObs_id());
		}
	}
	
	private int getRatio() {
		return mZBFlowNumber * BaseCommonFragment.RADIO;
	}

	@Override
	protected int getDocumentType() {
		return ZB_PLAN_TYPE;
	}

	@Override
	protected int getAttachPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
    @Override
	protected boolean doExtraRegListener(ViewHolder holder, final int position, int i) {
    	return false;
    }

	@Override
	protected boolean doExtraInitLayout(View convertView, ViewHolder holder, int position) {
		return false;
	}
}

