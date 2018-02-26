package com.pm360.cepm360.app.module.common.attachment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.app.module.document.DocumentUploadView;
import com.pm360.cepm360.app.module.document.DocumentUploadView.GetAttachmentInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.ProgressBarWindowInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.SeleteFileInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.WaterMarkerUploadInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AttachmentFragment<T extends AttachCell, B extends Expandable>
		extends BaseListRelevanceFragment<T, B> {
	
	protected DocumentUploadView mDocumentUploadView;
	
	public static final int ZH_WORKLOG_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[10][0]);
	public static final int ZH_RISK_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[11][0]);
	public static final int JH_WORKLOG_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[13][0]);
	public static final int JH_SAFETY_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[15][0]);
	public static final int JH_QUALITY_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[16][0]);
	public static final int MAILBOX_ATTACH_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[17][0]);
	public static final int ZB_WORKLOG_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[18][0]);
	public static final int ZB_INVATE_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[19][0]);
	public static final int ZB_BID_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[20][0]);
	public static final int ZB_PRETRIAL_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[21][0]);
	public static final int ZB_ANSWER_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[22][0]);
	public static final int ZB_QUOTE_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[23][0]);
	public static final int ZB_SCORE_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[24][0]);
	public static final int ZB_PLAN_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[25][0]);
	public static final int TEMPLATE_TYPE = Integer.parseInt(GLOBAL.FILE_TYPE[26][0]);
	
	protected ArrayList<String> mThumbs = new ArrayList<String>();
	protected ArrayList<String> mAddAttachmentPath = new ArrayList<String>();
	protected ArrayList<Files> mDeleteAttachmentFiles = new ArrayList<Files>();

	private ArrayList<Files> mAttachmentFiles = new ArrayList<Files>();

	private final int BASE_ATTACH_POSITION = 1000;
	
	/**-- View --*/
	private ProgressBar mProgressBar;
	private ServiceInterface<T> mServiceInterface;
	private boolean mProgressDialogStatus = false;
	
	/**-- Flag --*/
	private int mDocumentType;
	protected int mZBFlowNumber;
	
	/**-- Cache --*/
	private String mTypeName;
	
	protected abstract int getDocumentType();

	// 获取attach在每一行中的第几列
	protected abstract int getAttachPosition();

	protected Intent setAttachmentActivity() {
		Intent intent = new Intent();
		
		if (OperationMode.NORMAL == mCurrentMode && mHasEditPermission) {
			intent.setClass(getActivity(), AttachmentActivity.class);
		} else {
			intent.setClass(getActivity(), AttachmentReadOnlyActivity.class);
		}
		
		return intent;
	}
	
	protected void startAttachActivity(int position) {
		Intent intent = setAttachmentActivity();
		mCurrentItem = mListAdapter.getItem(position);
		intent.putExtra(AttachmentActivity.KEY_CURRENT_BEAN, mCurrentItem);
		intent.putExtra(AttachmentActivity.KEY_PROJECT, mCurrentProject);
		intent.putExtra(AttachmentActivity.KEY_TYPE, mDocumentType);
		if (TaskCell.class.isInstance(mParentBean)) {
			intent.putExtra(AttachmentActivity.KEY_TASK_NAME, ((TaskCell) mParentBean).getName());
		}

		startActivitys(intent, BASE_ATTACH_POSITION + getCodeNumber());
	}
	
	private int getCodeNumber() {
		return mZBFlowNumber * GLOBAL.FILE_TYPE.length + mDocumentType;
	}
	
	private Fragment getFragment() {
		Fragment fragment;
		if (mDocumentType == ZH_WORKLOG_TYPE || mDocumentType == ZH_RISK_TYPE
				|| mDocumentType == JH_WORKLOG_TYPE
				|| mDocumentType == JH_SAFETY_TYPE
				|| mDocumentType == JH_QUALITY_TYPE
				|| mDocumentType == MAILBOX_ATTACH_TYPE) {
			fragment = this;
		} else if (mDocumentType == ZB_WORKLOG_TYPE
				|| mDocumentType == ZB_INVATE_TYPE 
				|| mDocumentType == ZB_BID_TYPE 
				|| mDocumentType == ZB_PRETRIAL_TYPE
				|| mDocumentType == ZB_ANSWER_TYPE 
				|| mDocumentType == ZB_QUOTE_TYPE 
				|| mDocumentType == ZB_SCORE_TYPE 
				|| mDocumentType == ZB_PLAN_TYPE 
				|| mDocumentType == TEMPLATE_TYPE){
			fragment = getParentFragment();
		} else {
			fragment = this;
		}
		return fragment;
	}

	private void startActivitys(Intent intent, int key) {
		getFragment().startActivityForResult(intent, key);
	}

	@Override
	protected boolean doExtraRegListener(ViewHolder holder, final int position,
			int i) {
		if (i == getAttachPosition()) {
			T item = mListAdapter.getItem(position);
			if (item != null && item.getAttachment() != null
					&& !item.getAttachment().isEmpty()) {
				String attach = item.getAttachment();
				int num = attach.substring(0, attach.length() - 1).split(",").length;
				holder.tvs[i].setText(Integer.toString(num));
			}
			holder.tvs[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startAttachActivity(position);
				}
			});
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean doExtraInitLayout(View convertView, ViewHolder holder,
			int position) {
		if (position == getAttachPosition()) {
			holder.tvs[position] = (TextView) convertView
					.findViewById(mListItemIds[position]);
			
			Drawable drawable;
			if (OperationMode.NORMAL != mCurrentMode) {
				drawable = getResources().getDrawable(
						R.drawable.email_attachment);
			} else {
				drawable = getResources().getDrawable(
						R.drawable.email_attachment);
			}
			
			// x, y, width, height
			drawable.setBounds(0, 0, 27, 27);
			holder.tvs[position].setCompoundDrawables(null, null, drawable,
					null);
			return true;
		}

		return false;
	}
	
	WaterMarkerUploadInterface mUploadInterface = new WaterMarkerUploadInterface() {

		@Override
		public void dismiss(List<Files> fileList) {
			String attachment = "";
			for (Files files : fileList) {
				attachment += files.getFile_id() + ",";
			}
			if (attachment.endsWith(",")) {
				attachment = attachment.substring(0, attachment.length() - 1);
			}

	    	mProgressDialogStatus = true;
			mDataLoaded = false;
			sendMessage(SHOW_PROGRESS_DIALOG);

			handleServerData(attachment);
		}

		@Override
		public boolean getIsUpload() {
			return true;
		}

		@Override
		public Files getPreFile() {
			Files document = new Files();
			document.setProject_id(mCurrentProject == null ? 0 : mCurrentProject.getProject_id());
			document.setTask_id(mParentBean == null ? 0 : mParentBean.getId());
			document.setType_id(mCurrentItem == null ? 0 : mCurrentItem.getId());
			document.setDir_type(mDocumentType);
			
			return document;
		}

		@Override
		public List<User> getUserList() {
			return new ArrayList<User>();
		}

		@Override
		public String getTaskName() {
			String taskName = null;
			if (TaskCell.class.isInstance(mParentBean)) {
	    		taskName = ((TaskCell) mParentBean).getName();
	    	}
			return taskName;
		}

		@Override
		public String getProjectName() {
			return mCurrentProject == null ? null : mCurrentProject.getName();
		}

		@Override
		public String getTypeName() {
			return mTypeName;
		}
    	
    };
    
    GetAttachmentInterface mUploadWithParentBeanInterface = new GetAttachmentInterface() {

		@Override
		public String getAttachment() {
			return mCurrentItem == null ? null : mCurrentItem.getAttachment();
		}
    	
    };
    
    protected void doAttachOnClickEvent() {
		handleAttachSaveRequest();
    }
    
    protected ProgressBarWindowInterface mWindowInterface = new ProgressBarWindowInterface() {

		@Override
		public boolean noTitleAndButtonWindow() {
			return true;
		}

		@Override
		public ProgressBar getProgressBar() {
			return mProgressBar;
		}
    	
    };
    
    protected boolean dialogSaveButtonEvent() {
    	doAttachOnClickEvent();
    	mDialog.getWhichButton(2).setClickable(false);
    	mDocumentUploadView.uploadButtonEvent(false);
    	return false;
    }

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mDocumentType = getDocumentType();

		mDocumentUploadView = new DocumentUploadView(getFragment(), mUploadInterface, mUploadWithParentBeanInterface, mWindowInterface);
		mDocumentUploadView.initializeWindow();
		setBaseWidgetInterface(new BaseWidgetInterface() {

			@Override
			public TwoNumber<View, LayoutParams> createExtraLayout() {
				View view = mDocumentUploadView.getView();
				return new TwoNumber<View, LinearLayout.LayoutParams>(view, null);
			}

			@Override
			public Integer[] getImportantColumns() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		mDocumentUploadView.setSelectFileInterface(new SeleteFileInterface() {
			
			@Override
			public int getStartActivityCodeNum() {
				return getCodeNumber();
			}
		});

		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (mDialog != null) {
			mProgressBar = mDialog.insertProgressBar();
		}
		
		return view;
	}

	@Override
	protected void init(Class<T> listItemClass,
			CommonListInterface<T> listInterface,
			ServiceInterface<T> serviceInterface,
			FloatingMenuInterface floatingMenuInterface,
			OptionMenuInterface optionMenuInterface,
			SimpleDialogInterface dialogInterface) {
		super.init(listItemClass, listInterface, serviceInterface,
				floatingMenuInterface, optionMenuInterface, dialogInterface);
		mServiceInterface = serviceInterface;
	}

	private boolean checkAttachResult(String sum) {
		if (mCurrentItem.getAttachment() == null) {
			if (sum.isEmpty()) {
				return true;
			} else {
				return false;
			}
		}
		if (mCurrentItem.getAttachment().equals(sum)) {
			return true;
		} else {
			return false;
		}

		// //都为0
		// if (sum == 0 && mCurrentItem.getAttachment().isEmpty()) {
		// return false;
		// }
		//
		// //都不为0 且相等
		// if (!mCurrentItem.getAttachment().isEmpty() && sum ==
		// Integer.parseInt(mCurrentItem.getAttachment())) {
		// return false;
		// }
		// return true;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	LogUtil.i("wzw requestCode:" + requestCode + " resultCode:"
				+ resultCode + " mCurrentItem" + mCurrentItem);
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == BASE_ATTACH_POSITION + getCodeNumber()) {
			String sum = data
					.getStringExtra(AttachmentActivity.RESULT_ATTACH_SUM);
			LogUtil.i("wzw mCur:-------enter");
			if (mCurrentItem == null)
				return;
			if (!checkAttachResult(sum)) {
				mCurrentItem.setAttachment(sum);
				mListAdapter.notifyDataSetChanged();
			}
		} else {
			mDocumentUploadView.onActivityResult(requestCode, resultCode, data);
		}

	}

	@Override
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		if (mDialog != null) {
			if (mProgressDialogStatus) {
				mProgressDialogStatus = false;
				sendMessage(DISMISS_PROGRESS_DIALOG);
			}

	    	mDialog.getWhichButton(2).setClickable(true);
			mDialog.dismiss();
		}
	}



	@Override
	protected void showUpdateDialog(boolean isEdit) {
		clearCacheData();
		mDocumentUploadView.setDefaultData(false);

		super.showUpdateDialog(isEdit);
	}

	@Override
	public void handleParentEvent(B b) {
		if (checkParentBeanForQuery()) {
			mServiceInterface.getListData();
		}
	}

	private void handleAttachSaveRequest() {
    	
    	if (mDocumentType == JH_SAFETY_TYPE
    			|| mDocumentType == JH_QUALITY_TYPE) {
        	Map<String, String> saveData = mDialog.SaveData();
    		mTypeName = saveData.get(mDialogLableNames[1]);
    	}
			
	}

	/**
	 * 针对planFragment bidCompany
	 */
	protected void handleServerData(String attachment) {
		mSaveData = mDialog.SaveData();
		if (mIsAddOperation) {
			T t = addFieldCopy();
			if (attachment != null) {
				t.setAttachment(attachment);
			}
			
			mServiceInterface.addItem(t);
		} else {
			updateFieldCopy();
			if (attachment != null) {
				mCurrentUpdateItem.setAttachment(attachment);
			}
			mServiceInterface.updateItem(mCurrentUpdateItem);
		}
	}

	protected void clearCacheData() {
		mThumbs.clear();
		mAddAttachmentPath.clear();
		mDeleteAttachmentFiles.clear();
		mAttachmentFiles.clear();
	}

	@Override
	protected boolean doExtraAddFloatingMenuEvent() {
		clearCacheData();
		mDocumentUploadView.setDefaultData(true);
		if (mDocumentType == Integer.parseInt(GLOBAL.FILE_TYPE[28][0])) {
			return false;
		}
		return super.doExtraAddFloatingMenuEvent();
	}

}
