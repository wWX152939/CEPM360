package com.pm360.cepm360.app.module.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.email.ComposeActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.util.ArrayList;
import java.util.List;

public class SendEmailPresenter<B extends TaskCell> {
	private Activity mActivity;
	private B mParentBean;
	private AttachCell mCurrentItem;
	private DataTreeListAdapter<B> mParentListAdapter;
	private String mAttachIds;
	private String[] mDialogValues;
	private String[] mDialogLableNames;
	private int mType;
	private Project mProject;
	
	public SendEmailPresenter(Activity activity, Project project) {
		mActivity = activity;
		mProject = project;
	}
	
	public void setCurrentProject(Project project) {
		mProject = project;
	}
	
	protected String getEmailContent() {
		String content = "";
		String[] values = mDialogValues;
		for (int i = 0; i < mDialogLableNames.length; i++) {
			if (mType == Integer.parseInt(GLOBAL.FILE_TYPE[2][0])
					|| mType == Integer.parseInt(GLOBAL.FILE_TYPE[14][0])) {
				if (mDialogLableNames[i].equals(mActivity.getString(R.string.document_file))) {
					continue;
				}
			}
			String value = values[i];
			if (values[i] == null || values[i].isEmpty()) {
				value = "";
			}
			content += mDialogLableNames[i] + "：" + value + "\n"; 
		}
		return content;
	}
	
	private MailBox getInitialMailBox() {
		MailBox mailBox = new MailBox();
		mailBox.setAttachment(mAttachIds);
		
		String content = "";
    	content += "[" + GLOBAL.FILE_TYPE[mType - 1][1] + "]";
    	String projectName ="";
    	if (mProject != null) {

    		mProject.setName(ProjectCache.getProjectIdMaps().get(Integer.toString(mProject.getProject_id())));
    		projectName = mProject.getName();
    	}
    	
		content += "[" + projectName + "]";
		TextView homeTextView = (TextView) mActivity.findViewById(R.id.home);
		if (homeTextView != null && !homeTextView.getText().toString().equals(mActivity.getResources().getString(R.string.schedule_navigation_title))) {
			content += "[" + homeTextView.getText().toString() + "]";
		}
    	content += "[" + mParentBean.getName() + "]";
		mailBox.setContent(getEmailContent());
		mailBox.setSender(UserCache.getCurrentUser().getUser_id());
		
		mailBox.setTitle(content);
		
		if (mCurrentItem != null) {
			mailBox.setMail_table_id(mCurrentItem.getId());
			
			int mailType = 0;
			if (mType == Integer.parseInt(GLOBAL.FILE_TYPE[2][0])) {
				// xianchangtuwen
				mailType = Integer.parseInt(GLOBAL.MAIL_TABLE_TYPE[1][0]);
			} else if (mType == Integer.parseInt(GLOBAL.FILE_TYPE[14][0])) {
				// xingxiangchengguo
				mailType = Integer.parseInt(GLOBAL.MAIL_TABLE_TYPE[1][0]);
			} if (mType == AttachmentFragment.JH_SAFETY_TYPE) {
				mailType = Integer.parseInt(GLOBAL.MAIL_TABLE_TYPE[2][0]);
			} else if (mType == AttachmentFragment.JH_QUALITY_TYPE) {
				mailType = Integer.parseInt(GLOBAL.MAIL_TABLE_TYPE[3][0]);
			}
			
			mailBox.setMail_table_type(mailType);

		}
		
	    // 接收人 用户ID+公司ID组成[2-8840651],[3-8830457]
	    // 用户ID+接受类型组成 [2-to],[3-cc] GLOBAL.RECEIVE_TYPE
		String receiver = "", receiverType = "";
		
		
		if (mParentListAdapter != null) {
			// 正常模式
			for (B b : mParentListAdapter.getShowList()) {
				if (b.getTask_id() == mParentBean.getParents_id()) {
					receiver += "[" + b.getOwner() + "-" + UserCache.getCurrentUser().getTenant_id() + "],";
					receiverType += "[" + b.getOwner() + "-to],";
					break;
				}
			}
			if (!mParentBean.getCc_user().isEmpty()) {
				String ccString = mParentBean.getCc_user().replace("(", "");
				ccString = ccString.replace(")", "");
				String[] ccUsers = ccString.split(",");
				for (int i = 0; i < ccUsers.length; i++) { 
					receiver += "[" + ccUsers[i] + "-" + UserCache.getCurrentUser().getTenant_id() + "],";
					receiverType += "[" + ccUsers[i] + "-cc],";
				}
			}
		} else {
			// 门户模式
			receiver += "[" + mParentBean.getParents_id() + "-" + UserCache.getCurrentUser().getTenant_id() + "],";
			receiverType += "[" + mParentBean.getParents_id() + "-to],";
			
			if (mParentBean.getCc_user() != null && !mParentBean.getCc_user().isEmpty()) {
				String ccString = mParentBean.getCc_user().replace("(", "");
				ccString = ccString.replace(")", "");
				String[] ccUsers = ccString.split(",");
				for (int i = 0; i < ccUsers.length; i++) {
					receiver += "[" + ccUsers[i] + "-" + UserCache.getCurrentUser().getTenant_id() + "],";
					receiverType += "[" + ccUsers[i] + "-cc],";
				}
			}
		}

		LogUtil.i("wzw receiver:" + receiver + " type:" + receiverType);
		mailBox.setReceiver(receiver);
		mailBox.setReceive_type(receiverType);
		return mailBox;
	}
	
	private void startEmailActivity() {
		Intent intent = new Intent(mActivity, ComposeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(ComposeActivity
				.EMAIL_OPERATION_KEY, ComposeActivity.OPERATION_EDIT);
		bundle.putInt(ComposeActivity
				.EMAIL_MAILBOX_KEY, ComposeActivity.OUTBOX);
		MailBox mailBox = getInitialMailBox();
		
		bundle.putSerializable(ComposeActivity
				.EMAIL_OBJECT_BEAN_KEY, mailBox);
		if (mFileList != null) {
			bundle.putSerializable(ComposeActivity
					.EMAIL_ATTACHMENT_KEY, mFileList);
		}
		
		bundle.putSerializable(ComposeActivity.EMAIL_PROJECT_KEY, mProject);
		intent.putExtras(bundle);
		mActivity.startActivity(intent);
	}
	
	private ArrayList<Files> mFileList;
	private DataManagerInterface mManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					mFileList = (ArrayList<Files>) list;
					startEmailActivity();
				}
			}
		}
		
	};
	
	public <T extends AttachCell> void loadFiles(T t, B b, DataTreeListAdapter<B> bList,
			String[] defaultValue, String[] dialogLableNames, int type, String attachIds) {

		mDialogValues = defaultValue;
		mDialogLableNames = dialogLableNames;
		mParentBean = b;
		mCurrentItem = t;
		mParentListAdapter = bList;
		mType = type;
		
		mAttachIds = attachIds;
		if (mAttachIds != null && !mAttachIds.isEmpty()) {
			if (mAttachIds.endsWith(",")) {
				RemoteCommonService.getInstance().getAttachments(mManager, mAttachIds.substring(0, mAttachIds.length() - 1));
			} else {
				RemoteCommonService.getInstance().getAttachments(mManager, mAttachIds);
			}
			
		} else {
			mFileList = null;
			startEmailActivity();
		}
	}
	

	@SuppressWarnings("serial")
	public void loadFiles(final Files files, B b, DataTreeListAdapter<B> bList,
			String[] defaultValue, String[] dialogLableNames, int type) {

		mDialogValues = defaultValue;
		mDialogLableNames = dialogLableNames;
		mParentBean = b;
		mParentListAdapter = bList;
		mType = type;
		mAttachIds = files.getFile_id() + "";
		mFileList = new ArrayList<Files>();
		mFileList.add(files);
		mCurrentItem = new AttachCell() {
			
			@Override
			public void setAttachment(String attachment) {
				
			}
			
			@Override
			public int getId() {
				return files.getFile_id();
			}
			
			@Override
			public String getAttachment() {
				return null;
			}
		};
		startEmailActivity();
	}

}

