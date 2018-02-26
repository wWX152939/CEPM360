package com.pm360.cepm360.app.common.view.parent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.module.message.MessageUtil;
import com.pm360.cepm360.app.module.settings.UserSelectFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.app.utils.UtilTools.DeleteConfirmInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_countersign;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteFlowApproveService;

@SuppressLint("InflateParams")
public class FlowApprovalDialog {

	public static final int REQUEST_COUNTER = 0x90; 
	
    private View mRootView;
    private Activity mActivity;
    private Fragment mFragment;
    private BaseDialogStyle mDialog;
    private ProgressDialog mProgressDialog;
    
    private RelativeLayout mNeedApprovalContent;
    private LinearLayout mApprovalContentLayout;
    private LinearLayout mCountersignContent;
    //当前审批栏的用户人表
    private List<Flow_countersign> mCurrentDeleteCountersignListTemp = new ArrayList<Flow_countersign>();
    private Flow_countersign mCurrentNeedCountersign = new Flow_countersign();
    private List<User> mCurrentAddCountersignUserListTemp;
    private List<User> mCurrentApprovalCountersignUserList = new ArrayList<User>();
    //当前审批栏的会签人表
    private List<Flow_countersign> mCurrentApprovalCountersignList = new ArrayList<Flow_countersign>();
    private int mEditFlag = NORMAL_FLAG;
    private static int NORMAL_FLAG = 0;
    private static int EDIT_FLAG = 1;
    private static int COUNTERSIGN_FLAG = 2;
    private boolean mIsCurrentsignDeleteStatus = false;

    private FlowApprovalManager mManager;
    private Flow_approval mFlowApproval;
    private Flow_setting mFlowSetting;
    
    private Button submitButton;
    private Button passButton;
    private Button rejectedButton;
    private Button countersignButton;
    private Button exitButton;
    
    private boolean mIsApprovaling = false;//true 代表审批中;其他状态都是false
    private int mMessageId;

    private List<Flow_approval> mFlowApprovalList;
    private List<Integer> mUserList = new ArrayList<Integer>();

	public FlowApprovalDialog(Activity activity, Fragment fragment, Flow_approval flowApproval,
			Flow_setting flowSetting, FlowApprovalManager flowApprovalManager) {
		mActivity = activity;
		mFragment = fragment;
		mDialog = new BaseFlowApprovalDialog(activity);
		mFlowApproval = flowApproval;
		mManager = flowApprovalManager;
		mFlowSetting = flowSetting;

		handleFlowSetting();
		mRootView = mDialog.init(R.layout.flow_approval_activity, false);
		mDialog.setCanceledOnTouchOutside(true);
	}
	
	/**
	 * 设置flowApproval
	 * @param flowApproval
	 */
	public void setFlowApproval(Flow_approval flowApproval) {
		mFlowApproval = flowApproval;
	}
	
	public FlowApprovalDialog(Activity activity, Flow_approval flowApproval,
			Flow_setting flowSetting, FlowApprovalManager flowApprovalManager) {
		this(activity, null, flowApproval, flowSetting, flowApprovalManager);
	}

	/**
	 * 是不是审批状态
	 * @param isApprovaling
	 */
	public void show(boolean isApprovaling) {
	    mIsApprovaling = isApprovaling; 
	    
	    submitButton = (Button) mRootView.findViewById(R.id.submit_button);
		countersignButton = (Button) mRootView.findViewById(R.id.countersign_button);
		passButton = (Button) mRootView.findViewById(R.id.pass_button);
		rejectedButton = (Button) mRootView.findViewById(R.id.rejected_button);
		
		submitButton.setOnClickListener(submitListener);
		passButton.setOnClickListener(passListener);
		rejectedButton.setOnClickListener(rejectedListener);
		mApprovalContentLayout = (LinearLayout) mRootView.findViewById(R.id.approval_content_layout);	
		
		initButton();
		
		mIsCurrentsignDeleteStatus = false;
		mApprovalContentLayout.removeAllViews();
		mCurrentApprovalCountersignUserList.clear();
		mCurrentApprovalCountersignList.clear();
		
		LayoutInflater inflater1 = (LayoutInflater) mActivity.getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mNeedApprovalContent = (RelativeLayout)inflater1.inflate(R.layout.flow_approval_needapproval_unit, null);	
		TextView approvalPeopleEt = (TextView) mNeedApprovalContent.findViewById(R.id.approval_people);
		approvalPeopleEt.setText(UserCache.getCurrentUser().getName());
		
		mCountersignContent = (LinearLayout) mNeedApprovalContent.findViewById(R.id.countersign_people);
		mCountersignContent.removeAllViews();
		
		showProgressDialog("load flow approval data...");
		loadData(Integer.parseInt(mFlowApproval.getFlow_type()),
				mFlowApproval.getType_id());
	}
	
	private void updateCurrentCountersign() {
		Log.v("chenchen","更新当前审批会签人区域 ,一共有"+mCurrentApprovalCountersignList.size()+"条");
		Log.v("chenchen","mCurrentApprovalCountersignList:"+mCurrentApprovalCountersignList);
		if (mCountersignContent != null) {
			mCountersignContent.removeAllViews();
		}
		for (int i = 0; i < mCurrentApprovalCountersignList.size(); i++) {
			LayoutInflater inflater1 = (LayoutInflater) mActivity.getBaseContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			LinearLayout unitCountersignView = (LinearLayout)inflater1.inflate(R.layout.flow_approval_countersign_unit, null);	
			TextView counterName = (TextView)unitCountersignView.findViewById(R.id.countersign_people_edit);
			TextView counterTimeOrNotices = (TextView)unitCountersignView.findViewById(R.id.countersign_time_or_notices);	
			ImageView deleteImageView = (ImageView)unitCountersignView.findViewById(R.id.countersign_delete);
			
			if (mCurrentApprovalCountersignList.get(i).getCountersign_time() == null) {
				Log.v("chenchen","第"+i+"条会签意见为空");
				deleteImageView.setVisibility(mIsCurrentsignDeleteStatus ? View.VISIBLE: View.GONE);
			}
			
			counterName.setText(UserCache.getUserById(mCurrentApprovalCountersignList.get(i).getCountersign_user()+"").getName());
			if (mCurrentApprovalCountersignList.get(i).getCountersign_time() != null) {
				counterTimeOrNotices.setText(mCurrentApprovalCountersignList.get(i).getCountersign_suggestion()+"("
						+DateUtils.dateToString(DateUtils.FORMAT_LONG,mCurrentApprovalCountersignList.get(i).getCountersign_time())+")");				
			}
			
			unitCountersignView.setTag(mCountersignContent.getChildCount());
			unitCountersignView.setOnLongClickListener(mLongPressListener);
			unitCountersignView.setOnClickListener(mDeleteListener);
			mCountersignContent.addView(unitCountersignView);
		}
		
		updateButton();
	}
	
	private void updateButton() {
		countersignButton.setVisibility(View.VISIBLE);
		passButton.setVisibility(View.VISIBLE);
		rejectedButton.setVisibility(View.VISIBLE);	
		submitButton.setVisibility(View.GONE);
		if (mEditFlag == EDIT_FLAG) {
			Log.v("chenchen","需要审批");
			if (mCountersignContent != null && mCountersignContent.getChildCount() != 0) {
				for (int i = 0; i < mCountersignContent.getChildCount(); i++) {
					TextView timeTv = (TextView) mCountersignContent.getChildAt(i).findViewById(R.id.countersign_time_or_notices);
					//EditText markEt = (EditText) mNeedApprovalContent.findViewById(R.id.edit_mark);
					
					//有一个没有意见就无法审批
					if ((timeTv.getText()+"").equals(mActivity.getResources().getString(R.string.approver_countersign_mark_notices))) {
						passButton.setVisibility(View.GONE);
						rejectedButton.setVisibility(View.GONE);
//						markEt.setClickable(false);
						Log.v("chenchen","有一个没有意见就无法审批");
						break;
					} else if (i == mCountersignContent.getChildCount()-1) {
						Log.v("chenchen","最后一个也已经会签过，可以审批了");
						passButton.setVisibility(View.VISIBLE);
						rejectedButton.setVisibility(View.VISIBLE);	
//						markEt.setClickable(true);
					}
				}
			} else {
				Log.v("chenchen","会签人是空的");
				mIsCurrentsignDeleteStatus = false;
				passButton.setVisibility(View.VISIBLE);
				rejectedButton.setVisibility(View.VISIBLE);					
			}
		} else if (mEditFlag == COUNTERSIGN_FLAG) {
			Log.v("chenchen","需要会签");
			submitButton.setVisibility(View.VISIBLE);
			countersignButton.setVisibility(View.GONE);
			passButton.setVisibility(View.GONE);
			rejectedButton.setVisibility(View.GONE);					
		} else {
			Log.v("chenchen","正常展示");
			submitButton.setVisibility(View.GONE);
			passButton.setVisibility(View.GONE);
			rejectedButton.setVisibility(View.GONE);
			countersignButton.setVisibility(View.GONE);						
		}
	}
	
	private void handleFlowSetting() {
		if (mFlowSetting.getFlow_type().equals(GLOBAL.FLOW_TYPE[0][0])
				|| mFlowSetting.getFlow_type().equals(GLOBAL.FLOW_TYPE[1][0])
				|| mFlowSetting.getFlow_type().equals(GLOBAL.FLOW_TYPE[2][0])
				|| mFlowSetting.getFlow_type().equals(GLOBAL.FLOW_TYPE[3][0])
				|| mFlowSetting.getFlow_type().equals(GLOBAL.FLOW_TYPE[4][0])) {
			do {
				if (mFlowSetting.getLevel1() != 0) {
					mUserList.add(mFlowSetting.getLevel1());
				} else {
					break;
				}
				if (mFlowSetting.getLevel2() != 0) {
					mUserList.add(mFlowSetting.getLevel2());
				} else {
					break;
				}
				if (mFlowSetting.getLevel3() != 0) {
					mUserList.add(mFlowSetting.getLevel3());
				} else {
					break;
				}
				if (mFlowSetting.getLevel4() != 0) {
					mUserList.add(mFlowSetting.getLevel4());
				} else {
					break;
				}
				if (mFlowSetting.getLevel5() != 0) {
					mUserList.add(mFlowSetting.getLevel5());
				}
			} while (false);

		}
	}

	// isedit 是否显示mNeedApprovalContent
	private void updateDisplay(List<Flow_approval> faList, List<Flow_countersign> countersignList, boolean isedit) {

		LinearLayout.LayoutParams lineParams1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lineParams1.setMargins(0, 0, 0, mActivity.getResources()
				.getDimensionPixelSize(R.dimen.flow_approval_unit_bottom));

		for (int i = 0; i < faList.size(); i++) {
			// 若当前View是第一条 且不需要显示
			if (i == 0 && isedit == true) {
				//ImageView markIv = (ImageView)mNeedApprovalContent.findViewById(R.id.mark_image);
				if (mEditFlag == EDIT_FLAG) {
					Log.v("chenchen", "需要审批，加载待审批部分");
					mApprovalContentLayout.addView(mNeedApprovalContent, lineParams1);						
				} else if (mEditFlag == COUNTERSIGN_FLAG) {
					Log.v("chenchen", "需要会签，加载待会签部分");
					TextView countersignTextView = (TextView) mNeedApprovalContent.findViewById(R.id.textView6);
					countersignTextView.setText(R.string.approver_countersign_mark);
					TextView approvalPeopleEt = (TextView) mNeedApprovalContent.findViewById(R.id.approval_people);
					approvalPeopleEt.setText(UserCache.getUserById(mFlowApprovalList.get(0).getCurrent_level()+"").getName());
					mApprovalContentLayout.addView(mNeedApprovalContent, lineParams1);	
				}
			} else {
				mApprovalContentLayout.addView(
						createUnitApprovalView(faList.get(i)), lineParams1);
			}
		}

		LayoutInflater inflater1 = (LayoutInflater) mActivity.getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View submitContent = (RelativeLayout)inflater1.inflate(R.layout.flow_approval_content_submitter, null);
		TextView submitName = (TextView)submitContent.findViewById(R.id.unit_approval_people);
		TextView submitTimeOrNotices = (TextView)submitContent.findViewById(R.id.unit_approval_time);
		submitName.setText(UserCache.getUserById(faList.get(faList.size()-1).getSubmiter()+"").getName());
		if (faList.get(faList.size()-1).getSubmit_time() != null) {
			submitTimeOrNotices.setText(DateUtils.dateToString(DateUtils.FORMAT_LONG,
					faList.get(faList.size()-1).getSubmit_time()));								
		}		
		mApprovalContentLayout.addView(submitContent, lineParams1);
		
		
		if (countersignList != null && !countersignList.isEmpty()) {
			for (int i = 0; i < countersignList.size(); i++) {
				for (int j = 0; j < mFlowApprovalList.size(); j++) {
					if (countersignList.get(i).getFlow_approval_id() == mFlowApprovalList.get(j).getFlow_approval_id()) {
						View view = mApprovalContentLayout.getChildAt(j);
						LinearLayout countersignContent = (LinearLayout) view.findViewById(R.id.countersign_people);
	
						LinearLayout unitCountersignView = (LinearLayout)inflater1.inflate(R.layout.flow_approval_countersign_unit, null);	
						TextView counterName = (TextView)unitCountersignView.findViewById(R.id.countersign_people_edit);
						TextView counterTimeOrNotices = (TextView)unitCountersignView.findViewById(R.id.countersign_time_or_notices);
						
						if (j == 0) {
							//是当前审批的会签人
							Log.v("chenchen","分发会签人给待审批区域 "+i);
							counterName.setText(UserCache.getUserById(countersignList.get(i).getCountersign_user()+"").getName());
							if (countersignList.get(i).getCountersign_time() != null) {
								counterTimeOrNotices.setText(countersignList.get(i).getCountersign_suggestion()+"("
										+DateUtils.dateToString(DateUtils.FORMAT_LONG,countersignList.get(i).getCountersign_time())+")");								
							}
						
							unitCountersignView.setTag(mCountersignContent.getChildCount());
							unitCountersignView.setOnLongClickListener(mLongPressListener);
							unitCountersignView.setOnClickListener(mDeleteListener);
							mCurrentApprovalCountersignList.add(countersignList.get(i));
							mCurrentApprovalCountersignUserList.add(UserCache.
									getUserById(countersignList.get(i).getCountersign_user()+""));
						} else {
							//已经审批的条目
							Log.v("chenchen","分发会签人给已审批区域"+i);
							counterName.setText(UserCache.getUserById(countersignList.get(i).getCountersign_user()+"").getName());
							if (countersignList.get(i).getCountersign_time() != null) {
								counterTimeOrNotices.setText(countersignList.get(i).getCountersign_suggestion()+"("
										+DateUtils.dateToString(DateUtils.FORMAT_LONG,countersignList.get(i).getCountersign_time())+")");								
							}					
						}
						
						countersignContent.addView(unitCountersignView);
					}	
				}
			}
		}
		
		updateButton();
	}
	
	OnLongClickListener mLongPressListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View arg0) {
			if (mEditFlag == EDIT_FLAG) { 
				mIsCurrentsignDeleteStatus = !mIsCurrentsignDeleteStatus;
				updateCurrentCountersign();
			}
			Log.v("chenchen","mIsCurrentsignDeleteStatus:"+mIsCurrentsignDeleteStatus);
			return true;
		}
	};	
	
	OnClickListener mDeleteListener = new View.OnClickListener() {
		
		@Override
		public void onClick(final View arg0) {
			ImageView deleteImageView = (ImageView)arg0.findViewById(R.id.countersign_delete);
			if (deleteImageView.getVisibility() == View.VISIBLE) {

				TextView nameTv = (TextView)arg0.findViewById(R.id.countersign_people_edit);
				UtilTools.deleteConfirm(mActivity, new DeleteConfirmInterface() {
					
					@Override
					public void deleteConfirmCallback() {
						if (mEditFlag == EDIT_FLAG) {
							for (int i = 0; i < mCountersignContent.getChildCount(); i++) {
								if (arg0.getTag() == mCountersignContent.getChildAt(i).getTag()) {
									Flow_approval tmpCurrentApproval = handleFlowDataCore(COUNTER_SIGN);
									mCurrentDeleteCountersignListTemp.clear();
									mCurrentDeleteCountersignListTemp.add(mCurrentApprovalCountersignList.get(i));
									Log.v("chenchen","请求服务器删除数据");
									RemoteFlowApproveService.getInstance().removeCountersign(
											countersignflowApprovalInterface, mCurrentDeleteCountersignListTemp, tmpCurrentApproval);
								}
							}
						}							
					}
				}, mActivity.getString(R.string.confirm)+mActivity.getString(R.string.delete)+":"+nameTv.getText()+"?", 
				mActivity.getString(R.string.approver_countersign_delete_dialog_title));
			}
		}			
	};
	
	OnClickListener rejectedListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			if (mFlowApprovalList != null) {

				Flow_approval flowNextApproval = handleFlowDataCore(REJECT_STATUS);
				
				showProgressDialog("loading flowApproval data...");
				
				RemoteFlowApproveService.getInstance().approval(flowApprovalManagerInterface,
						mFlowApproval, flowNextApproval);
			}
		}
	};
	
	OnClickListener passListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			if (mFlowApprovalList != null) {

				Flow_approval flowNextApproval = handleFlowDataCore(PASS_STATUS);
//				Log.v("chenchen", "flowCurrentApproval:" + mFlowApproval);
//				Log.v("chenchen", "flowNextApproval:" + flowNextApproval);
				showProgressDialog("loading flowApproval data...");
				{
					RemoteFlowApproveService.getInstance().approval(
							flowApprovalManagerInterface, mFlowApproval,
							flowNextApproval);
				}
			}
		}
	};
	
	OnClickListener submitListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {	
			if (mCurrentNeedCountersign != null) {
				Log.v("chenchen","正在会签。。。");
				mCurrentNeedCountersign.setCountersign_suggestion(getApprovalMark());
				RemoteFlowApproveService.getInstance().countersign(countersignflowApprovalInterface, mCurrentNeedCountersign);
			}
		}
	};	

	private void showProgressDialog(String text) {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		mProgressDialog = UtilTools.showProgressDialog(mActivity, true, true);
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	private String getApprovalMark() {
		EditText editMark = (EditText) mRootView.findViewById(R.id.edit_mark);
		return editMark.getText().toString() == null || editMark.getText().toString().length() <= 0 ? 
				mActivity.getString(R.string.confirm) : editMark.getText().toString();
	}
	
	private void setCommonData(Flow_approval currentApproval, Flow_approval flowNextApproval) {
		setCommonData2Server(currentApproval, flowNextApproval);
		setFlowApprovalMessageId(currentApproval);
		setInputType();
	}
	
	private void setCommonData2Server(Flow_approval currentApproval, Flow_approval flowNextApproval) {
		if (mFlowApprovalList.size() != 0) {
			currentApproval.setFlow_approval_id(mFlowApprovalList.get(0)
					.getFlow_approval_id());
		}
		currentApproval.setSubmiter(mFlowApprovalList.get(0)
				.getSubmiter());
		currentApproval.setComment(getApprovalMark());
		currentApproval.setTenant_id(UserCache.getCurrentUser().getTenant_id());
		flowNextApproval.setSubmiter(mFlowApprovalList.get(0)
				.getSubmiter());

		flowNextApproval.setFlow_type(currentApproval.getFlow_type());
		flowNextApproval.setType_id(currentApproval.getType_id());
		flowNextApproval.setTenant_id(UserCache.getCurrentUser().getTenant_id());
	}
	
	private void setFlowApprovalMessageId(Flow_approval currentApproval) {
		int msgTypeId = Integer.parseInt(currentApproval.getFlow_type());
		switch(Integer.parseInt(currentApproval.getFlow_type())) {
		case 1:
			msgTypeId = 1;
			break;
		case 2:
			msgTypeId = 2;
			break;
		case 3:
			msgTypeId = 3;
			break;
		case 4:
			msgTypeId = 4;
			break;
		case 5:
			msgTypeId = 5;
			break;
		case 6:
			msgTypeId = 6;
			break;
		}
		mMessageId = MessageUtil.getMessageId(mActivity, msgTypeId, mFlowApprovalList.get(0).getFlow_approval_id());
		LogUtil.i("wzw detail:" + mMessageId + "msgTypeId:" + msgTypeId);
		if (mMessageId != 0) {
			currentApproval.setMessage_id(mMessageId);
		}
		
	}
	
	private void setInputType() {
		EditText editText = (EditText) mNeedApprovalContent.findViewById(R.id.edit_mark);			
		InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	private final int PASS_STATUS = 1;
	private final int REJECT_STATUS = 2;
	private final int COUNTER_SIGN = 3;
	/**
	 * 
	 * @param status 1:pass 2:reject 3:huiqian
	 * @return
	 */
	private Flow_approval handleFlowDataCore(int status) {
		Flow_approval flowNextApproval = null;
		if (status == PASS_STATUS) {
			flowNextApproval = new Flow_approval();
			mFlowApproval.setStatus(3);

			int currentUserId = UserCache.getCurrentUser().getUser_id();
			mFlowApproval.setCurrent_level(currentUserId);
			for (int i = 0; i < mUserList.size(); i++) {
				if (mUserList.get(i) == currentUserId) {
					if ((i + 2) < mUserList.size()) {
						// has next and next next
						flowNextApproval.setNext_level(mUserList
								.get(i + 2));
						flowNextApproval.setCurrent_level(mUserList
								.get(i + 1));
						mFlowApproval.setNext_level(mUserList.get(i + 1));
					} else if ((i + 1) < mUserList.size()) {
						// has next no next next
						mFlowApproval.setNext_level(mUserList.get(i + 1));
						flowNextApproval.setCurrent_level(mUserList
								.get(i + 1));
						flowNextApproval.setNext_level(mFlowApprovalList
								.get(0).getSubmiter());
					} else if (i == (mUserList.size() - 1)) {
						// no next
						mFlowApproval.setNext_level(mFlowApprovalList
								.get(0).getSubmiter());
						flowNextApproval.setCurrent_level(0);
						flowNextApproval.setNext_level(0);
					}
					break;
				}
			}
			
			flowNextApproval.setStatus(2);
			
			setCommonData(mFlowApproval, flowNextApproval);
		} else if (status == REJECT_STATUS) {
			flowNextApproval = new Flow_approval();
			mFlowApproval.setStatus(4);

			mFlowApproval.setCurrent_level(mFlowApprovalList.get(0)
					.getSubmiter());
			mFlowApproval.setNext_level(mFlowApprovalList.get(0)
					.getCurrent_level());
			flowNextApproval.setCurrent_level(0);
			flowNextApproval.setStatus(4);
			setCommonData(mFlowApproval, flowNextApproval);
		} else if (status == COUNTER_SIGN) {
			Flow_approval flowApproval = MiscUtils.clone(mFlowApproval);

			int currentUserId = UserCache.getCurrentUser().getUser_id();
			flowApproval.setCurrent_level(currentUserId);
			for (int i = 0; i < mUserList.size(); i++) {
				if (mUserList.get(i) == currentUserId) {
					Log.v("chenchen", "i:" + i);
					if ((i + 2) < mUserList.size()) {
						// has next and next next
						flowApproval.setNext_level(mUserList.get(i + 1));
					} else if ((i + 1) < mUserList.size()) {
						// has next no next next
						flowApproval.setNext_level(mUserList.get(i + 1));
					} else if (i == (mUserList.size() - 1)) {
						// no next
						flowApproval.setNext_level(mFlowApprovalList
								.get(0).getSubmiter());
					}
					break;
				}
			}
			
			flowApproval.setStatus(2);
			
			setCommonData(flowApproval, new Flow_approval());

			// @Deprecated 会签必须是审批的ID+1
			flowApproval.setFlow_type((Integer.parseInt(flowApproval.getFlow_type())+1)+"");
			flowNextApproval = flowApproval;
		}
		
		return flowNextApproval;
	}
	
	private void initButton() {
		exitButton = (Button) mRootView.findViewById(R.id.exit_button);
		exitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				mDialog.dismiss();
			}
		});
		countersignButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//过滤自己
				List<User> userListTemp = new ArrayList<User>();
				userListTemp.addAll(mCurrentApprovalCountersignUserList);
				userListTemp.add(UserCache.getCurrentUser());
				Intent intent = new Intent(mActivity, ListSelectActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY, UserSelectFragment.class);
				bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, true);
				intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, ListSelectActivity.MULTI_SELECT);
				bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, (Serializable) userListTemp);

				intent.putExtras(bundle);
                
				if (mFragment != null) {
					mFragment.startActivityForResult(intent, REQUEST_COUNTER);
				} else {
					mActivity.startActivityForResult(intent, REQUEST_COUNTER);
				}
							
			}
		});
	}

	private View createUnitApprovalView(Flow_approval flowBean) {

		LayoutInflater inflater1 = (LayoutInflater) mActivity.getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View unitView = inflater1.inflate(R.layout.flow_approval_content_unit, null);
		TextView approvalPeopleTv = (TextView) unitView.findViewById(R.id.unit_approval_people);
		TextView approvalStatusTv = (TextView) unitView.findViewById(R.id.unit_approval_status);
		TextView approvalMarkTv = (TextView) unitView.findViewById(R.id.unit_approval_mark);
		TextView approvalTimeTv = (TextView) unitView.findViewById(R.id.unit_approval_time);
		ImageView approvalTimeIv = (ImageView) unitView.findViewById(R.id.time_image);
		
        Log.v("chenchen", "level:" + flowBean.getCurrent_level());
        String userName = UserCache.getUserMaps().get(String.valueOf(flowBean.getCurrent_level()));
        approvalPeopleTv.setText(userName == null ? "" : userName);

		approvalTimeTv.setText(DateUtils.dateToString(DateUtils.FORMAT_LONG,
				flowBean.getApproval_time()));
		if (flowBean.getApproval_time() == null) {
			approvalTimeIv.setVisibility(View.GONE);
		} else {
			approvalTimeIv.setVisibility(View.VISIBLE);
		}
		approvalMarkTv.setText(flowBean.getComment());
		approvalMarkTv.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); 
		approvalMarkTv.setGravity(Gravity.TOP);  
		approvalMarkTv.setSingleLine(false);  
		approvalMarkTv.setHorizontallyScrolling(false); 		

		if (flowBean.getStatus() > 0) {
			approvalStatusTv.setText(GLOBAL.FLOW_APPROVAL_STATUS[flowBean
					.getStatus() - 1][1]);
		}
		switch (flowBean.getStatus()) {
		case 2:
			approvalStatusTv.setTextColor(mActivity.getResources().getColor(
					R.color.flow_approvaling));
			break;
		case 3:
			approvalStatusTv.setTextColor(mActivity.getResources().getColor(
					R.color.flow_approval_pass));
			break;
		case 4:
			approvalStatusTv.setTextColor(mActivity.getResources().getColor(
					R.color.flow_approval_rejected));
			break;
		default:
			break;
		}

		return unitView;
	}

	private void loadData(int flow_type_id, int type_id) {
		showProgressDialog("loading flowApproval data...");
		RemoteFlowApproveService.getInstance().getApprovalList(
				flowApprovalManagerInterface,
				UserCache.getCurrentUser()
						.getTenant_id(), flow_type_id, type_id);
	}

	@SuppressLint("HandlerLeak")
	public Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(mActivity.getBaseContext(), (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		}
	};

	DataManagerInterface countersignflowApprovalInterface = new DataManagerInterface() {
		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			Log.v("chenchen","服务器返回数据:"+list);
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				if (list != null) {
					Log.v("chenchen","服务器返回数据,会签人添加成功!");
					mCurrentApprovalCountersignList.addAll((List<Flow_countersign>)list);
					mCurrentApprovalCountersignUserList.addAll(mCurrentAddCountersignUserListTemp);

					Log.v("chenchen","新的会签人存入会签表中和会签用户中!");
					updateCurrentCountersign();
				}
				
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				Log.v("chenchen","查询到会所有的会签数据 !");
				List<Flow_countersign> allFlowCountersignList = (List<Flow_countersign>)list;
				Log.v("chenchen","所以数据全部就位 开始更新界面");

				if (mFlowApprovalList.size() != 0) {
					if (mFlowApprovalList.get(0).getCurrent_level() == UserCache.getCurrentUser().getUser_id()
					        && mIsApprovaling == true 
					        && mFlowApprovalList.get(0).getStatus() != 3 
					        && mFlowApprovalList.get(0).getStatus() != 4) {
						Log.v("chenchen","当前人就是审批人，单据状态是审批中，最新审批也没有通过或者驳回");
						mEditFlag = EDIT_FLAG;
						updateDisplay(mFlowApprovalList, allFlowCountersignList, true);
					} else if (isCurrentCountersign(allFlowCountersignList)
					        && mIsApprovaling == true 
					        && mFlowApprovalList.get(0).getStatus() != 3 
					        && mFlowApprovalList.get(0).getStatus() != 4) {
						Log.v("chenchen","当前人就是会签人，单据状态是审批中，最新审批也没有通过或者驳回");
						mEditFlag = COUNTERSIGN_FLAG;
						updateDisplay(mFlowApprovalList, allFlowCountersignList, true);
					} else {
						Log.v("chenchen","正常展示会签");
						mEditFlag = NORMAL_FLAG;
						updateDisplay(mFlowApprovalList, allFlowCountersignList, false);
					}
				}
					
				mDialog.show(974, 0);
				dismissProgressDialog();								
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
				Log.v("chenchen","数据删除成功！");
				for (int i = 0; i < mCurrentApprovalCountersignList.size(); i++) {
					if (mCurrentApprovalCountersignList.get(i).getId() == mCurrentDeleteCountersignListTemp.get(0).getId()) {
						Log.v("chenchen","本地已经同步删除数据");
						mCurrentApprovalCountersignList.remove(i);
						mCurrentApprovalCountersignUserList.remove(i);
						updateCurrentCountersign();
						updateButton();
						break;
					}
				}
			} else if  (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				Log.v("chenchen","会签成功！");
				show(false);
			} else {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}
		}

		private boolean isCurrentCountersign(List<Flow_countersign> allFlowCountersignList) {
			for (int i = 0; i < allFlowCountersignList.size(); i++) {
				if (allFlowCountersignList.get(i).getFlow_approval_id() == mFlowApprovalList.get(0).getFlow_approval_id()) {
					mCurrentApprovalCountersignList.add(allFlowCountersignList.get(i));
				}
			}

			Log.v("chenchen","mCurrentApprovalCountersignUserList:"+mCurrentApprovalCountersignUserList);			
			for (int i = 0; i < mCurrentApprovalCountersignList.size(); i++) {
				if (UserCache.getCurrentUser().getUser_id() == mCurrentApprovalCountersignList.get(i).getCountersign_user()) {
					mCurrentNeedCountersign = mCurrentApprovalCountersignList.get(i);
					if (mCurrentNeedCountersign.getCountersign_time() == null) {
						return true;
					}
				}
			}
			return false;
		}
		
	};

	DataManagerInterface flowApprovalManagerInterface = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			dismissProgressDialog();
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

				Log.v("chenchen", "list:" + list);
				if (list != null && !list.isEmpty()) {
					mFlowApprovalList = (List<Flow_approval>) list;
					Collections.sort(mFlowApprovalList);
					
					String flowApprovalIdStr = "";
					for (int i = 0; i < mFlowApprovalList.size(); i++) {
						flowApprovalIdStr += mFlowApprovalList.get(i).getFlow_approval_id()+"";
						if (i != mFlowApprovalList.size()-1) {							
							flowApprovalIdStr+=",";
						}
					}
					Log.v("chenchen", "flowApprovalIdStr = " + flowApprovalIdStr);
					RemoteFlowApproveService.getInstance().getCountersignList(
							countersignflowApprovalInterface, flowApprovalIdStr);
				} else {
					// 审批列表为空
					updateButton();
					Toast.makeText(mActivity, R.string.approval_list_is_null, Toast.LENGTH_SHORT).show();
				}
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE
					|| status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				MessageUtil.setMessageToProcessed(mActivity, mMessageId);
				Log.v("chenchen","update or add success!" +list+status);
				//TODO
				mDialog.dismiss();
				if (mFlowApproval.getStatus() == Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])) {
					mManager.rebutFlowData2Server();
				} else if (!mUserList.isEmpty() && (mUserList.get(mUserList.size() - 1) ==  mFlowApproval.getCurrent_level())) {
					mManager.passFlowData2Server();
				}
//				show(false);
			}
		}
	};
	
	private List<Flow_countersign> dataHandleChanged(List<User> userList, int flowApprovalId) {
		Log.v("chenchen","用户表转换成会签表");
		List<Flow_countersign> flowCountersignList = new ArrayList<Flow_countersign>();
		
		for (User user : userList) {
			Flow_countersign flowCountersign = new Flow_countersign();
			flowCountersign.setCountersign_user(user.getUser_id());
			flowCountersign.setFlow_approval_id(flowApprovalId);
			flowCountersignList.add(flowCountersign);
		}
		return flowCountersignList;
	}
	
	/**
	 * onActivityResult需要调用该方法
	 * @param bundle
	 */
	@SuppressWarnings("unchecked")
	public void handleUserSelectResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_COUNTER) {
			if (resultCode == Activity.RESULT_OK) {
				mCurrentAddCountersignUserListTemp = (List<User>)data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
				Flow_approval tmpCurrentApproval = handleFlowDataCore(COUNTER_SIGN);
				List<Flow_countersign> flowCountersignList = dataHandleChanged(mCurrentAddCountersignUserListTemp, tmpCurrentApproval.getFlow_approval_id());
				Log.v("chenchen","添加会签人对话框返回数据，新的会签人已经加入会签User列表");
				Log.v("chenchen","新的会签人上传到服务器");
				RemoteFlowApproveService.getInstance().addCountersign(countersignflowApprovalInterface, flowCountersignList, tmpCurrentApproval);		
			}
		}
	}

	public interface FlowApprovalManager {
		/**
		 * 最后一级审批通过调用该接口
		 * @param businessManagerInterface
		 */
		public void passFlowData2Server();

		/**
		 * 驳回接口
		 * @param businessManagerInterface
		 */
		public void rebutFlowData2Server();
	}
}
