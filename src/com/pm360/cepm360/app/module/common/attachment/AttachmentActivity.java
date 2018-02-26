package com.pm360.cepm360.app.module.common.attachment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView;
import com.pm360.cepm360.app.module.document.DocumentUploadView.GetAttachmentInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.GetFilesListInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.WaterMarkerUploadInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.AttachTaskCell;
import com.pm360.cepm360.entity.AttachWaterMarkCell;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;

import java.util.ArrayList;
import java.util.List;

public class AttachmentActivity<T extends AttachCell> extends Activity {
	/**
	 * 传入bean对象或者只传入bean_id和bean_attachment
	 */
	public final static String KEY_CURRENT_BEAN = "bean";
	public final static String KEY_CURRENT_BEAN_ID = "bean_id";
	public final static String KEY_CURRENT_BEAN_ATTACHMENT = "bean_attachment";
	
	/**
	 * project name id user_list为可填项
	 */
	public final static String KEY_PROJECT = "project";
	public final static String KEY_TASK_NAME = "task_name";
	public final static String KEY_TASK_ID = "task_id";
	public final static String KEY_USER_LIST = "user_list";

	/**
	 * 如果不需要上传功能，填入该值为false
	 */
	public final static String KEY_IS_UPLOAD_STATUS = "is_upload_status";
	
	/**
	 * 文件type，如果需要上传功能，必须填type，不需要上传功能，可以不填，但也应该在各自类中填入文件类型dir_type(GLOBAL.FILE_TYPE中定义)
	 */
	public final static String KEY_TYPE = "type";
	
	/**
	 * 添加或者修改状态，会改变界面按钮
	 */
	public final static String KEY_IS_ADD_STATUS = "is_add_status";
	
	/**
	 * 修改模式下填入fileList
	 */
	public final static String KEY_FILES_LIST = "file_list";
	
	/**
	 * 返回结果
	 */
	public final static String RESULT_ATTACH_SUM = "sum";
	public final static String RESULT_ATTACH_LIST = "list";
	
	protected GridView mGridView;
    
	/**-- 消息传递数据  -**/
	protected List<User> mUserList;
    protected T mCurrentBean;
    protected Project mCurrentProject;
    protected String mTaskName;
    protected int mTaskID;
    // AttachFragment.WORKLOG_TYPE AttachFragment.RISK_TYPE
    protected int mType;
    private boolean mIsAddStatus;
    private boolean mIsUploadStatus = true;
    private List<Files> mFileList;
    private String mAttachmentIDs;
    private int mTypeID;
    
    /**-- 组合对象 --*/
    protected DocumentUploadView mDocumentUploadView;
    private AttachmentUploadInterface<T> mAttachmentUploadInterface;
    @SuppressWarnings("rawtypes")
	private AttachmentUploadFactory mAttachmentUploadFactory;
    
    @SuppressWarnings("unchecked")
    protected Intent initPassData() {
    	Intent intent = getIntent();

    	// 如果不需要上传功能，填入该值
        mIsUploadStatus = intent.getBooleanExtra(KEY_IS_UPLOAD_STATUS, true);
        
    	// mCurrentBean 和 mType 为必填项
        mCurrentBean = (T) intent.getSerializableExtra(KEY_CURRENT_BEAN);
        
        mTypeID = intent.getIntExtra(KEY_CURRENT_BEAN_ID, 0);
        mAttachmentIDs = intent.getStringExtra(KEY_CURRENT_BEAN_ATTACHMENT);
        
        mType = (Integer) intent.getSerializableExtra(KEY_TYPE);
        
        // mCurrentProject， mTaskName mUserList为可填项
        mCurrentProject = (Project) intent.getSerializableExtra(KEY_PROJECT);
        mTaskName = (String) intent.getSerializableExtra(KEY_TASK_NAME);
        mTaskID = intent.getIntExtra(KEY_TASK_ID, 0);
        mUserList = (List<User>) intent.getSerializableExtra(KEY_USER_LIST);
        mFileList = (List<Files>) intent.getSerializableExtra(KEY_FILES_LIST);
        mIsAddStatus = intent.getBooleanExtra(KEY_IS_ADD_STATUS, false);
        if (mUserList == null) {
        	mUserList = new ArrayList<>();
        }
        return intent;
    }
    
    @SuppressWarnings("unchecked")
	protected void initWindow() {
    	setContentView(R.layout.base_activity);

        initPassData();

    	LinearLayout parent = (LinearLayout) findViewById(R.id.parent_id);
    	int width = UtilTools.dp2pxW(getBaseContext(), 588);
    	int height = UtilTools.dp2pxH(getBaseContext(), 456);
    	if (mFileList != null) {
    		mDocumentUploadView = new DocumentUploadView(this, mUploadInterface, mGetFilesListInterface, null);
    	} else {
    		mDocumentUploadView = new DocumentUploadView(this, mUploadInterface, mUploadWithParentBeanInterface);
    	}
    	
    	mAttachmentUploadFactory = new AttachmentUploadFactory<T>();
    	mAttachmentUploadInterface = mAttachmentUploadFactory.createAttachment(mType);
    	
    	parent.addView(mDocumentUploadView.initializeWindow(), width, height);
        
    }
    
    private void initData() {
    	if (mDocumentUploadView != null) {
    		mDocumentUploadView.setDefaultData(mIsAddStatus);
    	}
    }
    
    WaterMarkerUploadInterface mUploadInterface = new WaterMarkerUploadInterface() {

		@Override
		public void dismiss(List<Files> fileList) {
			// 关闭
			if (fileList == null) {
				finish();
				return;
			}
			
			// 现场图文 形象成果
			if (mType == 2 || mType == 3 || mType == 13 ||
					mType == 15) {

				Intent intent = new Intent();
	    		setResult(Activity.RESULT_OK, intent);
				finish();
				return;
			}
			
			// 不需要activity上传，退出
			if (!mIsUploadStatus) {
				Intent intent = new Intent();
				intent.putExtra(RESULT_ATTACH_LIST, (ArrayList<Files>)fileList);
	    		setResult(Activity.RESULT_OK, intent);
				finish();
				return;
			}
			
			String attachment = "";
			for (Files files : fileList) {
				attachment += files.getFile_id() + ",";
			}
			if (attachment.endsWith(",")) {
				attachment = attachment.substring(0, attachment.length() - 1);
			}
			if (mCurrentBean != null) {
				// modify status
				mCurrentBean.setAttachment(attachment);
			}
			
			mAttachmentUploadInterface.updateParentBean(mManagerInterface, mCurrentBean);
			if (!mAttachmentUploadFactory.getFindStatus()) {
				Intent intent = new Intent();
				intent.putExtra(RESULT_ATTACH_LIST, (ArrayList<Files>)fileList);
	    		setResult(Activity.RESULT_OK, intent);
				finish();
				return;
			}
		}

		@Override
		public boolean getIsUpload() {
			return mIsUploadStatus;
		}

		@Override
		public Files getPreFile() {
			int taskId = 0;
			int typeId = 0;
			if (AttachTaskCell.class.isInstance(mCurrentBean)) {
	    		taskId = ((AttachTaskCell) mCurrentBean).getTaskId();
	    	} else {
	    		taskId = mTaskID;
	    	}
			Files document = new Files();
			document.setProject_id(mCurrentProject == null ? 0 : mCurrentProject.getProject_id());
			document.setTask_id(taskId);
			if (mCurrentBean == null) {
				if (mType == 2 || mType == 3 || mType == 13 ||
						mType == 15) {
					typeId = mTaskID;
				} else {
					typeId = mTypeID;
				}
			} else {
				typeId = mCurrentBean.getId();
			}
			document.setType_id(typeId);
			document.setDir_type(mType);
			
			return document;
		}

		@Override
		public List<User> getUserList() {
			return mUserList;
		}

		@Override
		public String getTaskName() {
			return mTaskName;
		}

		@Override
		public String getProjectName() {
			return mCurrentProject == null ? null : mCurrentProject.getName();
		}

		@Override
		public String getTypeName() {
			String typeName = null;
			if (AttachWaterMarkCell.class.isInstance(mCurrentBean)) {
	    		typeName = ((AttachWaterMarkCell) mCurrentBean).getTypeName();
	    	}
			return typeName;
		}
    	
    };
    
    GetAttachmentInterface mUploadWithParentBeanInterface = new GetAttachmentInterface() {

		@Override
		public String getAttachment() {
			return mCurrentBean == null ? mAttachmentIDs : mCurrentBean.getAttachment();
		}
    	
    };
    
    GetFilesListInterface mGetFilesListInterface = new GetFilesListInterface() {

		@Override
		public List<Files> getFilesList() {
			// TODO Auto-generated method stub
			return mFileList;
		}
    	
    };
    
    DataManagerInterface mManagerInterface = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_UPDATE) {
				Toast.makeText(getBaseContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent();
				intent.putExtra(RESULT_ATTACH_SUM, mCurrentBean.getAttachment());
	    		setResult(Activity.RESULT_OK, intent);
			}
			finish();
		}
    	
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWindow();

    	initData();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        mDocumentUploadView.onActivityResult(requestCode, resultCode, data);
    	
    }
}
