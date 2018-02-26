package com.pm360.cepm360.app.module.common.attachment;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.view.parent.InformationView;
import com.pm360.cepm360.app.module.document.DocumentUploadView;
import com.pm360.cepm360.app.module.document.DocumentUploadView.GetAttachmentInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.UploadInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.WindowInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.AttachTaskCell;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.util.List;
import java.util.Map;

public class AttachmentViewActivity<T extends AttachCell> extends AttachmentReadOnlyActivity<T>{

	public final static String KEY_INDEX_FEEDBACK = "index_feedback";
	public final static String KEY_MSG = "message";
	private Index_feedback mIndexFeedback;
	private Message mMessage;
	@SuppressWarnings("rawtypes")
	private DialogInterface mAttachmentInterface;
	private boolean mIsAttachmentExist;
	private Object mCurrentItem;
	private boolean isZHTask;
	
	@Override
	protected void initWindow() {
		setContentView(R.layout.base_activity);

    	Intent intent = getIntent();
    	mMessage = (Message) intent.getSerializableExtra(KEY_MSG);
    	if (mMessage != null) {
    		loadTaskData();
    		return;
    	}
    	mIndexFeedback = (Index_feedback) intent.getSerializableExtra(KEY_INDEX_FEEDBACK);
    	if (mIndexFeedback != null) {
    		loadFeedbackData();
    	}
	}
	
	private void loadTaskData() {
		
		RemoteCommonService.getInstance().getGroupIDBYTaskID(mZHTaskManager, mMessage);
		
//		int type = isZHMsgTypeKey(mMessage.getType());
//		if (type == 1) {
//			// zuhe
//			RemoteCommonService.getInstance().getGroupIDBYTaskID(mZHTaskManager, mMessage);
//		} else if (type == 0) {
//			// jihuan
//			RemoteCommonService.getInstance().getProjectIDBYTaskID(mTaskManager, mMessage);
//		}
	}
	
//	private DataManagerInterface mTaskManager = new DataManagerInterface() {
//
//		@Override
//		public void getDataOnResult(ResultStatus status, List<?> list) {
//			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
//				if (list != null && !list.isEmpty()) {
//					Project project = (Project) list.get(0);
//					mIndexFeedback = new Index_feedback();
//					mIndexFeedback.setProject_id(project.getProject_id());
//					mIndexFeedback.setProject_name(project.getName());
//					mIndexFeedback.setMessage_type_key(mMessage.getType());
//					mIndexFeedback.setTask_id(mMessage.getTask_id());
//					mIndexFeedback.setType_id(mMessage.getType_id());
//					mIndexFeedback.setTitle(project.getTask_name());
//					mIndexFeedback.setTenant_id(project.getTenant_id());
//					loadFeedbackData();
//				}
//			}
//			
//		}
//		
//	};
	
	private DataManagerInterface mZHTaskManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					ZH_group group = (ZH_group) list.get(0);
					mIndexFeedback = new Index_feedback();
					mIndexFeedback.setZh_group_id(group.getZh_group_id());
					mIndexFeedback.setMessage_type_key(mMessage.getType());
					mIndexFeedback.setTask_id(mMessage.getTask_id());
					mIndexFeedback.setTitle(group.getTask_name());
					mIndexFeedback.setGroup_name(group.getNode_name());
					mIndexFeedback.setType_id(mMessage.getType_id());
					mIndexFeedback.setTenant_id(group.getTenant_id());
					mIndexFeedback.setProject_name(ProjectCache.findProjectById(group.getProject_id()).getName());
					loadFeedbackData();
				} else {
					Toast.makeText(AttachmentViewActivity.this, R.string.no_zh_group_id, Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}
		
	};
	
	private int isZHMsgTypeKey(int type) {
		if (type == Integer.parseInt(GLOBAL.MSG_TYPE_KEY[8][0])
				|| type == Integer.parseInt(GLOBAL.MSG_TYPE_KEY[9][0])) {
			// jihuan
			isZHTask = false;
			return 0;
		} else if (type == Integer.parseInt(GLOBAL.MSG_TYPE_KEY[16][0])
				|| type == Integer.parseInt(GLOBAL.MSG_TYPE_KEY[19][0])
				|| type == Integer.parseInt(GLOBAL.MSG_TYPE_KEY[20][0])
				|| type == Integer.parseInt(GLOBAL.MSG_TYPE_KEY[17][0])
				|| type == Integer.parseInt(GLOBAL.MSG_TYPE_KEY[18][0])) {
			// zuhe
			isZHTask = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadFeedbackData() {
		isZHMsgTypeKey(mIndexFeedback.getMessage_type_key());
		AttachmentFactory factory = new AttachmentFactory();
		mAttachmentInterface = factory.createAttachment(mIndexFeedback.getMessage_type_key());
		mIsAttachmentExist = mAttachmentInterface.getServerData(mManagerInterface, mIndexFeedback);
	}
	
	protected void initWidget() {
		
    }
	
	private void initDefaultData() {

        Project project = new Project();
        project.setProject_id(mIndexFeedback.getProject_id());
        mCurrentProject = project;
        mType = mIndexFeedback.getMessage_type_key();
	}
	

	UploadInterface mViewUploadInterface = new UploadInterface() {

		@Override
		public void dismiss(List<Files> fileList) {
			
		}

		@Override
		public boolean getIsUpload() {
			return false;
		}

		@Override
		public Files getPreFile() {
			Files files = new Files();
			files.setProject_id(mIndexFeedback.getProject_id());
			return files;
		}

		@Override
		public List<User> getUserList() {
			return null;
		}
    	
    };

	private DocumentUploadView mUploadView;
	private void initAttachmentWindow() {
		InformationView view;
		BaseWidgetInterface widgetInterface = new BaseWidgetInterface() {

			@Override
			public TwoNumber<View, LayoutParams> createExtraLayout() {
				mUploadView = new DocumentUploadView(AttachmentViewActivity.this, mViewUploadInterface, mUploadWithParentBeanInterface, new WindowInterface() {
					
					@Override
					public boolean noTitleAndButtonWindow() {
						return true;
					}
					
				});
				mUploadView.initializeWindow();
				mUploadView.switchReadOnlyMode(true);
				return new TwoNumber<View, LayoutParams>(mUploadView.getView(), null);
			}

			@Override
			public Integer[] getImportantColumns() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		String title = "info";
		if (mAttachmentInterface.getTitleStringArray() != 0) {
			title = getString(mAttachmentInterface.getTitleStringArray());
		}
		
		if (mIsAttachmentExist) {
			view = new InformationView(this, title, widgetInterface);
		} else {
			view = new InformationView(this, title);
		}
		
		if (isZHTask) {
			mLableNames = getResources().getStringArray(R.array.zuhe_info);
		} else {
			mLableNames = getResources().getStringArray(R.array.project_info);
		}
		
		String[] dialogLableNames = getResources().getStringArray(mAttachmentInterface.getDialogArray());
		String[] inputNames = MiscUtils.concat(mLableNames, dialogLableNames);
		view.init(inputNames, null, null);

		view.switchModifyDialog(false);
		view.getExitView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		view.SetDefaultValue(getDefaultValues());
		LinearLayout parent = (LinearLayout) findViewById(R.id.parent_id);

    	int width = UtilTools.dp2pxW(getBaseContext(), 588);
    	android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) parent.getLayoutParams();
    	params.width = width;
    	
    	parent.setLayoutParams(params);
//    	int height = UtilTools.dp2pxH(getBaseContext(), 456);
    	parent.addView(view.getPopupView(), width, LayoutParams.MATCH_PARENT);
		
		// 加载附件
    	if (mIsAttachmentExist) {
    		mUploadView.setDefaultData(false);
    	}
    	
	}
	
	GetAttachmentInterface mUploadWithParentBeanInterface = new GetAttachmentInterface() {

		@Override
		public String getAttachment() {
			return mCurrentBean == null ? null : mCurrentBean.getAttachment();
		}
    	
    };
	
	private String[] mLableNames;
	
	@SuppressWarnings("unchecked")
	private String[] getDefaultValues() {
		mUpdateFields = mAttachmentInterface.getBeanAttr();

		if (mCurrentItem == null || mUpdateFields == null) {
			return null;
		}
		
		mUpdateFieldSwitchMap = mAttachmentInterface.getSwitchMap();
		
		Map<String, String> listItemMap = MiscUtils.beanToMap(mCurrentItem);

		// 构建默认值数组
		String[] defaultValues = new String[mUpdateFields.length];
		for (int i = 0; i < mUpdateFields.length; i++) {
			if (mUpdateFieldSwitchMap != null &&
				mUpdateFieldSwitchMap.containsKey(mUpdateFields[i])) {
				defaultValues[i] = mUpdateFieldSwitchMap.get(mUpdateFields[i])
									.get(listItemMap.get(mUpdateFields[i]));
			} else {
				defaultValues[i] = listItemMap.get(mUpdateFields[i]);
				if (SetValueDialogInterface.class.isInstance(mAttachmentInterface)) {
					String value = ((SetValueDialogInterface) mAttachmentInterface).getStringValue(mUpdateFields[i], listItemMap.get(mUpdateFields[i]));
					if (value != null) {
						defaultValues[i] = value;
					}
				}
			}
			
			if (("0").equals(defaultValues[i])) {
				defaultValues[i] = "";
			}
		}
		String[] dialogValues = new String[mLableNames.length];
		dialogValues[0] = mIndexFeedback.getProject_name();
		if (isZHTask) {
			dialogValues[1] = mIndexFeedback.getGroup_name();
			dialogValues[2] = mIndexFeedback.getTitle();
		} else {
			dialogValues[1] = mIndexFeedback.getTitle();
		}
		String[] retValues = MiscUtils.concat(dialogValues, defaultValues);
		return retValues;
	}
	
	private String[] mUpdateFields;
	private Map<String, Map<String, String>> mUpdateFieldSwitchMap;
	
	private DataManagerInterface mManagerInterface = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				for (Object o : list) {
					if (mAttachmentInterface.isSameObject(o, mIndexFeedback.getType_id())) {
						mCurrentItem = o;
						break;
					}
				}
				
				if (mCurrentItem != null && AttachTaskCell.class.isInstance(mCurrentItem)) {
					mCurrentBean = (T) mCurrentItem;
				}
				initDefaultData();
				initAttachmentWindow();
			} else {
				Toast.makeText(AttachmentViewActivity.this, status.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
	};

}
