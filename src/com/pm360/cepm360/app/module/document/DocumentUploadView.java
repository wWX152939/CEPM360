package com.pm360.cepm360.app.module.document;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.fileexplorer.FileExplorerActivity;
import com.pm360.cepm360.app.common.imageexplorer.ImageExplorerActivity;
import com.pm360.cepm360.app.module.common.attachment.UploadAttachManager;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentUploadView {

	/**-- View --*/
	private View mRootView;
    private Button mUploadButton;
    private TextView mTitle;
    private ImageView mCancel;
    private ImageView mAddFile;
    private ProgressBar mProgressBar;
    private Dialog mPickDialog;
    private LinearLayout mFileListLayout;
    private TextView mSelectPathTextView;
    private Activity mActivity;
    private Fragment mFragment;
    private ProgressDialog mLoadingProgressDialog;


	/**-- Cache --*/
    private String mTaskName;
    private String mProjectName;
    private String mTypeName;
    private String mPath;
    private Files mUploadFile;
    private String mSelectIds = "0";
    private List<User> mUserList;
    private List<Files> mAllFileList = new ArrayList<Files>();
    private ArrayList<Files> mDeleteFileList = new ArrayList<Files>();
    private List<Document> mDocList = new ArrayList<Document>();
    private int mLoadDocCount = 0;
    
	/**-- Flag --*/
    private int mRequestCount;
    private boolean mReadOnlyMode;
    
    private static final int TYPE_FILE_TYPE = 1010;
    private static final int TYPE_FILE_TITLE = 1110;
    private static final int TYPE_DEL_ICON = 2010;
    private static final int MAX_UPLOAD_FILES = 5;
    public static final int FILE_REQUEST_CODE = 1;
    public static final int IMAGE_REQUEST_CODE = 2;
    public static final int CAMERA_REQUEST_CODE = 3;
    public static final int SELECT_PATH_REQUEST_CODE = 4;

    private static final int SHOW_TOAST = 101;
    private static final int UPLOAD_FILE_SUCCESS = 102;
    
    // 是否上传
    private boolean mIsUpload;
    private int mFileType;

	/**-- Interface --*/
    private UploadInterface mUploadInterface;
    private UploadAttachManager mUploadAttachManager;
    private GetAttachmentInterface mGetAttachMentInterface;
    private WindowInterface mWindowInterface;
    private SeleteFileInterface mSeleteFileInterface;
    private GetFilesListInterface mGetFilesListInterface;
    
    public DocumentUploadView(Fragment fragment, UploadInterface uploadInterface) {
    	this(fragment, uploadInterface, null);
    }
    
    public DocumentUploadView(Activity activity, UploadInterface uploadInterface) {
        this(activity, uploadInterface, null);
    }
    
    public DocumentUploadView(Fragment fragment, UploadInterface uploadInterface, GetAttachmentInterface uploadWithParentBeanInterface) {
    	this(fragment.getActivity(), uploadInterface, uploadWithParentBeanInterface);
    	mFragment = fragment;
    }
    
    public DocumentUploadView(Activity activity, UploadInterface uploadInterface, GetAttachmentInterface uploadWithParentBeanInterface) {
    	this(activity, uploadInterface, uploadWithParentBeanInterface, null);
    }
    
    public DocumentUploadView(Fragment fragment, UploadInterface uploadInterface, GetAttachmentInterface uploadWithParentBeanInterface,
    		WindowInterface windowInterface) {
    	this(fragment.getActivity(), uploadInterface, uploadWithParentBeanInterface, windowInterface);
    	mFragment = fragment;
    }
    
    public DocumentUploadView(Activity activity, UploadInterface uploadInterface, GetFilesListInterface getFilesListInterface,
    		WindowInterface windowInterface) {
    	mActivity = activity;
    	if (uploadInterface != null) {
    		mIsUpload = uploadInterface.getIsUpload();
            mUploadInterface = uploadInterface;
            
    	}
        
    	mWindowInterface = windowInterface;
    	mGetFilesListInterface = getFilesListInterface;
    }
    
    public DocumentUploadView(Activity activity, UploadInterface uploadInterface, GetAttachmentInterface uploadWithParentBeanInterface,
    		WindowInterface windowInterface) {
    	mActivity = activity;
    	if (uploadInterface != null) {
    		mIsUpload = uploadInterface.getIsUpload();
            mUploadInterface = uploadInterface;
    	}
        
    	mGetAttachMentInterface = uploadWithParentBeanInterface;
    	mWindowInterface = windowInterface;
    }
    
    public void switchReadOnlyMode(boolean readOnlyMode) {
    	mReadOnlyMode = readOnlyMode;
    }
    
    /**
     * 设置选择activity接口
     * @param seleteFileInterface
     */
    public void setSelectFileInterface(SeleteFileInterface seleteFileInterface) {
    	mSeleteFileInterface = seleteFileInterface;
    }
    
	private int getCodeNum() {
    	if (mSeleteFileInterface == null) {
    		return 0;
    	} else {
    		return mSeleteFileInterface.getStartActivityCodeNum();
    	}
    }
    
    /**
     * 获取view
     * @return
     */
    public View getView() {
    	return mRootView;
    }
    
    /**
     * 每次调用界面时清除缓存
     */
    public void clear() {
    	mAllFileList.clear();
    	mDocList.clear();
    	mDeleteFileList.clear();
    	mSelectIds = "0";
    	mSelectPathTextView.setText("");
    	LogUtil.i("wzw file:" + mFileListLayout);
    	if (mFileListLayout != null) {
    		mFileListLayout.removeAllViews();
    	}
    	if (mReadOnlyMode) {
        	if (mUploadButton != null) {
        		mUploadButton.setVisibility(View.GONE);
        	}
        }
        updateAddFileButton();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Toast显示
     */
    @SuppressLint("HandlerLeak")
    public Handler mShowHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_TOAST:
                Toast.makeText(mActivity,
                        (CharSequence) msg.obj,
                            Toast.LENGTH_SHORT).show();
                break;
            case UPLOAD_FILE_SUCCESS:
                uploadFinish();
                break;
            default:
                break;
            }
        }
    };

    /**
     * 界面初始化
     * @param savedInstanceState
     */
    public View initializeWindow() {

    	mRootView = mActivity.getLayoutInflater().inflate(R.layout.document_upload_file_view, null);
        if (mWindowInterface != null) {
        	if (mWindowInterface.noTitleAndButtonWindow()) {
            	LinearLayout title = (LinearLayout) mRootView.findViewById(R.id.title_layout);
            	LinearLayout button = (LinearLayout) mRootView.findViewById(R.id.button_bar);
            	title.setVisibility(View.GONE);
            	button.setVisibility(View.GONE);
        	} else {
        		mUploadButton = (Button) mRootView.findViewById(R.id.btn_upload);
        		mUploadButton.setOnClickListener(mButtonClickListener);
        	}
        } else {
        	mUploadButton = (Button) mRootView.findViewById(R.id.btn_upload);
        	mUploadButton.setOnClickListener(mButtonClickListener);
        }
        
        mTitle = (TextView) mRootView.findViewById(R.id.edit_title);
        mTitle.setText(mActivity.getString(R.string.document_upload));
        mCancel = (ImageView) mRootView.findViewById(R.id.btn_close);
        mAddFile = (ImageView) mRootView.findViewById(R.id.add_file);
        mCancel.setOnClickListener(mButtonClickListener); 
        mAddFile.setOnClickListener(mButtonClickListener);
   
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);

		mUploadAttachManager = new UploadAttachManager(mActivity, mFileManager, null, mUserList);
		mUploadAttachManager.setProgressBar(mProgressBar);
		
        initContentView();
        initPickDialog();
        return mRootView;
    }
    
    /**
     * 在显示前调用该接口
     * @param isAddStatus 是否为添加状态
     */
    public void setDefaultData(boolean isAddStatus) {
    	if (ProgressBarWindowInterface.class.isInstance(mWindowInterface)) {
    		ProgressBar progressBar = ((ProgressBarWindowInterface) mWindowInterface).getProgressBar();
    		if (progressBar != null) {
    			mProgressBar = progressBar;
    			mUploadAttachManager.setProgressBar(mProgressBar);
    		}
		}
		clear();
    	
    	if (mGetFilesListInterface != null && mGetFilesListInterface.getFilesList() != null) {
    		for (int i = 0; i < mGetFilesListInterface.getFilesList().size(); i++) {
    			Files files = (Files) mGetFilesListInterface.getFilesList().get(i);
    			insertAttachment(files);
    		}
    	} else {
    		if (!isAddStatus && mGetAttachMentInterface != null && mGetAttachMentInterface.getAttachment() != null
        			&& !mGetAttachMentInterface.getAttachment().isEmpty()) {
    			if (mReadOnlyMode) {
    				mSelectPathTextView.setHint(mActivity.getString(R.string.cannot_select_path_in_readonly_mode));
    			} else {
    				mSelectPathTextView.setHint(mActivity.getString(R.string.cannot_select_path_in_modify_mode));
    			}
    			
    			mSelectPathTextView.setOnClickListener(null);
        		mLoadingProgressDialog = UtilTools.showProgressDialog(mActivity, false, false);
        		String attachment = mGetAttachMentInterface.getAttachment();
        		if (attachment.endsWith(",")) {
        			attachment = attachment.substring(0, attachment.length() - 1);
        		}
        		RemoteCommonService.getInstance().getAttachments(mQueryInterface, attachment);
        	} else {
    			if (mReadOnlyMode) {
    				mSelectPathTextView.setHint(mActivity.getString(R.string.cannot_select_path_in_readonly_mode));
    				mSelectPathTextView.setOnClickListener(null);
    			} else {
    				mSelectPathTextView.setHint(mActivity.getString(R.string.pls_select_path));
    				mSelectPathTextView.setOnClickListener(mButtonClickListener);
    			}
        	}
    	} 
    	
    	if (mUploadInterface == null) {
    		return;
    	}

    	mUploadFile = mUploadInterface.getPreFile();

        mFileType = mUploadFile.getDir_type();
        
		Project project = ProjectCache.findProjectById(mUploadFile.getProject_id());
		mUploadAttachManager.setProject(project);
		
        mUserList = mUploadInterface.getUserList();
        mUploadAttachManager.setUserList(mUserList);

        if (WaterMarkerUploadInterface.class.isInstance(mUploadInterface)) {
        	mTaskName = ((WaterMarkerUploadInterface) mUploadInterface).getTaskName();
        	mProjectName = ((WaterMarkerUploadInterface) mUploadInterface).getProjectName();
        	mTypeName = ((WaterMarkerUploadInterface) mUploadInterface).getTypeName();
        }
    }
    
    private void initContentView() {
        // 获取列表使用的相关资源
        mFileListLayout = (LinearLayout) mRootView.findViewById(R.id.file_list_layout);
        
        mSelectPathTextView = (TextView) mRootView.findViewById(R.id.select_file_path);
    }
    
	private void viewFile(String path, Files files) {
        try {
            IntentBuilder.viewFile(mActivity, files, path);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mActivity, mActivity.getString(R.string.application_no_found),
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 插入附件
     * @param path
     */
    @SuppressLint("InflateParams") 
    private void insertAttachment(final Files file) {
        if (mAllFileList.size() >= MAX_UPLOAD_FILES) {
            showWarningToast(mActivity.getString(R.string.more_than_max_upload_count));
            return;
        }
        // 检查是否已经添加过该附件
//        for (String filePath : mAllFileList) {
//            if (filePath.equals(path)) {
//                showWarningToast(mActivity.getString(R.string.email_attachment_add_repeat));
//                return ;
//            }
//        }
        
        final View fileItemView = ((Activity) mActivity).getLayoutInflater().inflate(
                                R.layout.attach_upload_file_item_layout, null);
        TextView fileSizeTextView = (TextView) fileItemView.findViewById(R.id.file_size);
        
        EditText fileTitleEditText = (EditText) getViewById(fileItemView, TYPE_FILE_TITLE);
        
        ImageView fileTypeImageView = (ImageView) getViewById(fileItemView, TYPE_FILE_TYPE);
        
        ImageView removeIcon = (ImageView) getViewById(fileItemView, TYPE_DEL_ICON);
        if (mReadOnlyMode) {
        	removeIcon.setVisibility(View.GONE);
        	fileTitleEditText.clearFocus();
        	fileTitleEditText.setBackground(mActivity.getResources().getDrawable(R.drawable.edit_text_normal));
        	fileTitleEditText.setFocusableInTouchMode(false);
        	fileTitleEditText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int i = 0;
					for (; i < mAllFileList.size(); i++) {
						if (getViewById(i, TYPE_FILE_TITLE).equals(v)) {
							break;
						}
					}
					viewFile(mAllFileList.get(i).getPath(), mAllFileList.get(i));
				}
			});
        	if (mUploadButton != null) {
        		mUploadButton.setVisibility(View.GONE);
        	}
        }
        
        String path = file.getPath();
        fileTypeImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int i = 0;
				for (; i < mAllFileList.size(); i++) {
					if (getViewById(i, TYPE_FILE_TYPE).equals(v)) {
						break;
					}
				}
				viewFile(mAllFileList.get(i).getPath(), mAllFileList.get(i));
			}
		});
        
        // setTitle
        String title;
        if (file.getTitle() == null) {
        	title = FileUtils.getFileName(path);
        } else {
        	title = file.getTitle();
        }
        fileTitleEditText.setText(title);
        
        String size = "";
        if (file.getFile_size() == 0) {
        	size = FileUtils.getFileSize(path);
        } else {
        	size = FileUtils.getFileSize(file.getFile_size());
        }
        fileSizeTextView.setText(size);
        fileTypeImageView.setImageDrawable(getFileDrawable(path));
        
        removeIcon.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mFileListLayout.removeView(fileItemView);
            	if (file.getFile_id() != 0) {
                	mDeleteFileList.add(file);
            	}
                mAllFileList.remove(file);
                updateAddFileButton();
            }
        });
        
        mFileListLayout.addView(fileItemView, LayoutParams.MATCH_PARENT, 
        		mActivity.getResources().getDimensionPixelSize(R.dimen.table_height));
        mAllFileList.add(file);
        updateAddFileButton();
    }
    
    private void updateAddFileButton() {

        if (mAllFileList.size() == MAX_UPLOAD_FILES || mReadOnlyMode) {
            mAddFile.setVisibility(View.GONE);
        } else {
            mAddFile.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 初始化选择附件对话框
     */
    private void initPickDialog() {
        mPickDialog = new Dialog(mActivity, R.style.MyDialogStyle);
        mPickDialog.setContentView(R.layout.document_upload_pic_pick_dialog);
        
        Button takePicture = (Button) mPickDialog.findViewById(R.id.take_picture);
        Button localPicture = (Button) mPickDialog.findViewById(R.id.local_picture);        
        Button localFile = (Button) mPickDialog.findViewById(R.id.local_file);
        
        takePicture.setOnClickListener(mButtonClickListener);
        localPicture.setOnClickListener(mButtonClickListener);
        localFile.setOnClickListener(mButtonClickListener);

        mPickDialog.setCanceledOnTouchOutside(true);
    }
    
    /**
     *  在点击【上传】之后， 在上传过程中，冻结UI（除了取消按钮）
     */
    private void setUIClickable(boolean clickable) {
    	if (mUploadButton != null) {
    		mUploadButton.setClickable(clickable);
    	}
    }
    
    private boolean contain(Files files, List<Files> fileList) {
    	if (files != null) {
    		for (Files f : fileList) {
    			if (f.getId() == files.getId()) {
    				return true;
    			}
    		}
    		return false;
    	} else {
    		return false;
    	}
    }
    
    private View getViewById(int line, int type) {
    	View parentView = mFileListLayout.getChildAt(line);
    	return getViewById(parentView, type);
    }
    
    private View getViewById(View parentView, int type) {
    	View view = null;
    	switch(type) {
    	case TYPE_FILE_TYPE:
			view = parentView.findViewById(R.id.thumbnail);
    		break;
    	case TYPE_FILE_TITLE:
			view = parentView.findViewById(R.id.title);
    		break;
    	case TYPE_DEL_ICON:
			view = parentView.findViewById(R.id.remove);
    		break;
    	}
    	return view;
    }
    
    /**
     * 获取需要修改的文件列表
     * @return
     */
    private List<Files> getModifyList() {
    	List<Files> modifyList = new ArrayList<Files>();
    	for (int i = 0; i < mAllFileList.size(); i++) {
    		// 排除添加和删除外
    		if (mAllFileList.get(i).getId() != 0 && !contain(mAllFileList.get(i), mDeleteFileList)) {
    			mFileListLayout.getChildAt(i).findViewById(R.id.title);
				String title = ((EditText)getViewById(i, TYPE_FILE_TITLE)).getText().toString();
				// 标题不相等，表明需要修改
				if (!title.equals(mAllFileList.get(i).getTitle())) {
					mAllFileList.get(i).setTitle(title);
					modifyList.add(mAllFileList.get(i));
				}
    		}
    	}
    	return modifyList;
    }
    
    /**
     * true 内部的button false 外部的button
     * @param innerButton
     */
    public void uploadButtonEvent(boolean innerButton) {
    	List<Files> addList = prepareUploading();
    	List<Files> modifyList = getModifyList();
    	for (Files files : mAllFileList) {
    		if (files.getTitle() == null || files.getTitle().isEmpty()) {
    			showWarningToast(mActivity.getString(R.string.pls_input_all_info));
    			return;
    		}
    	}
		if (addList.isEmpty() && mDeleteFileList.isEmpty() && modifyList.isEmpty()) {
			if (innerButton) {
//				showWarningToast(mActivity.getString(R.string.pls_select_document));
//        		return;
				mUploadInterface.dismiss(mAllFileList);
			} else {
				mUploadInterface.dismiss(mAllFileList);
			}
    	}
		
    	if (mIsUpload) {
            mRequestCount = addList.size() + mDeleteFileList.size() + modifyList.size();
    		for (int i = 0; i < addList.size(); i++) {
    			doUploading(addList.get(i), addList.get(i).getPath());
    		}
    		for (int i = 0; i < mDeleteFileList.size(); i++) {
    			doDeleteFile(mDeleteFileList.get(i));
    		}
    		for (int i = 0; i < modifyList.size(); i++) {
    			doModifyFile(modifyList.get(i));
    		}
    		
    	} else {
    		mUploadInterface.dismiss(mAllFileList);
    	}
    	
    }

    /**
     * 文件添加对话框监听器
     */
    OnClickListener mButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_upload:
            	uploadButtonEvent(true);
                break;
            case R.id.btn_close:
        		mUploadInterface.dismiss(null);
                break;
            case R.id.add_file:
                mPickDialog.show();
                break;
            case R.id.local_file:
                pickLocalFile();
                break;
            case R.id.local_picture:
                pickLocalPicture();
                break;
            case R.id.take_picture:
                pickByCapture();
                break;
            case R.id.select_file_path:
            	selectFilePath();
            	break;
            }
        }
    };

    private void showWarningToast(String obj) {
        Message msg = Message.obtain();
        msg.what = SHOW_TOAST;
        msg.obj = obj;
        mShowHandler.sendMessage(msg);
    }
    
    private List<Files> searchAddFiles() {
    	List<Files> addFile = new ArrayList<Files>();
    	for (int i = 0; i < mAllFileList.size(); i++) {
    		if (mAllFileList.get(i).getId() == 0) {
    			String title = ((EditText)getViewById(i, TYPE_FILE_TITLE)).getText().toString();
				// 标题不相等，表明需要修改
				if (!title.equals(mAllFileList.get(i).getTitle())) {
					mAllFileList.get(i).setTitle(title);
				}
    			addFile.add(mAllFileList.get(i));
    		}
    	}
    	return addFile;
    }

    /**
     * 保存按钮的处理函数，保存录入数据, 首先获取用户列表
     */
    private List<Files> prepareUploading() {
    	List<Files> addFile = searchAddFiles();
        for (int i = 0; i < addFile.size(); i++) {
            Files files = addFile.get(i);
            
            /**-- 设置传入的files对象属性 --*/
            if (mUploadFile.getDirectory_id() == null) {
            	files.setDirectory_id(mSelectIds);
            } else {
            	files.setDirectory_id(mUploadFile.getDirectory_id());
            }
            
            if (PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[54][0])) {
            	if (files.getDirectory_id().equals("0")) {
            		files.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[0][0]));
            	} else {
            		files.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[1][0]));
            	}
    			
    		} else {
    			if (files.getDirectory_id().equals("0")) {
            		files.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[0][0]));
            	} else {
            		files.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[2][0]));
            	}
    		}
            
            files.setDir_type(mUploadFile.getDir_type());
            files.setProject_id(mUploadFile.getProject_id());
            files.setType_id(mUploadFile.getType_id());
            files.setTask_id(mUploadFile.getTask_id());
            
            /**-- 初始化数据 --*/
            files.setStatus(mActivity.getString(R.string.new_create));
            files.setVersion("1.0");
            files.setCreate_time(new Date());
    
            setUIClickable(false);

            String path = addFile.get(i).getPath();
            String[] name = path.split("/");
            String fileName = name[name.length - 1];
            files.setFile_type(fileName.substring(fileName.lastIndexOf('.') + 1));
            
            // 获取缓存数据
            User user = UserCache.getCurrentUser();
            files.setAuthor(user.getUser_id());
            files.setTenant_id(user.getTenant_id());
            
        }
        return addFile;
    }
    
    // return 如 /公共文档/整合管理/../../
    private String getDirectoryRelativePath(int dirId, String paths) {
        String path = paths;
        List<Document> dataList = mDocList;
        for (Document document : dataList) {
            if (dirId == document.getDocument_id()) {
                path = document.getName() + "/" + path;
                int pdirId = document.getParents_id();
                if (pdirId == 0) {
                    path = "/" + path;
                    break;
                } else {
                    path = getDirectoryRelativePath(pdirId, path);
                }
            }
        }
        if (!path.equals("")) {
            return path;
        } else {
            return "";
        }
    }
    
    /**
     * 目录树回调接口
     */
    DataManagerInterface directoryManager = new DataManagerInterface() {
        @SuppressWarnings("unchecked")
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
        	mLoadDocCount--;
        	
            switch (status.getCode()) {
            case AnalysisManager.SUCCESS_DB_QUERY:
            	mDocList.addAll((List<Document>) list);
            	if (mLoadDocCount == 0) {
            		String dirId = mAllFileList.get(0).getDirectory_id();
            		if (dirId != null && !dirId.equals("0")) {
            			String[] idArray = dirId.split(",");
            			String path = "";
            			for (int i = 0; i < idArray.length; i++) {
            				if (!idArray[i].isEmpty()) {
            					path += getDirectoryRelativePath(Integer.parseInt(idArray[i]), "") + "\n";
            				}
            			}
            			
            			while (path.endsWith("\n")) {
            				path = path.substring(0, path.length() - 1);
            			}
            			mSelectPathTextView.setText(path);
            		}
            		
            	}
                break;
            default:
                break;
            }
        }
    };
    
    private void loadDirData() {
        
        // 项目文档
        
        if (mUploadFile != null) {
        	int projectId = mUploadFile.getProject_id();
            if (projectId != 0) {
            	mLoadDocCount = 3;
            	Document directory2 = new Document();
                directory2.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
                directory2.setTenant_id(UserCache.getCurrentUser().getTenant_id());
                directory2.setProject_id(projectId);
                RemoteDocumentsService.getInstance().getDirectoryList(directoryManager, directory2);
            } else {
            	mLoadDocCount = 2;
            }
        } else {
        	mLoadDocCount = 2;
        }
        
        // 个人文档
        Document directory1 = new Document();
        directory1.setDirectory_type(GLOBAL.DIR_TYPE_PERSONAL);
        directory1.setUser_id(UserCache.getCurrentUser().getUser_id());
        directory1.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        RemoteDocumentsService.getInstance().getDirectoryList(directoryManager, directory1);

        
        // 公共文档     
        Document directory3 = new Document();
        directory3.setDirectory_type(GLOBAL.DIR_TYPE_PUBLIC);
        directory3.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        RemoteDocumentsService.getInstance().getDirectoryList(directoryManager, directory3);
    }
    
    DataManagerInterface mQueryInterface = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mLoadingProgressDialog.dismiss();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				
				for (int i = 0; i < list.size(); i++) {
					Files files = (Files) list.get(i);
					insertAttachment(files);
				}
				
				if (list != null && !list.isEmpty()) {
					loadDirData();
				}
				
			} else {
				showWarningToast(status.getMessage());
			}
		}
    
    };

    
    /**
     * 文档回调接口
     */
    DataManagerInterface mFileManager = new DataManagerInterface() {

        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            mRequestCount--;
            
            if (mRequestCount == 0) {
                setUIClickable(true);
            }
            
            if ((status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
                    && list != null && list.size() != 0) {
            	 // 上传成功
            	Files files = (Files) list.get(0);
            	for (int i = 0; i < mAllFileList.size(); i++) {
            		LogUtil.i("wzw path:" + mAllFileList.get(i).getPath()
            				+ " f path:" + mAllFileList.get(i).getPath());
            		if (mAllFileList.get(i).getPath().equals(files.getPath())) {
            			LogUtil.i("wzw equal");
            			mAllFileList.set(i, files);
            		}
            	}
            	
                if (mRequestCount == 0) {
                    Message msg = Message.obtain();
                    msg.what = UPLOAD_FILE_SUCCESS;
                    mShowHandler.sendMessage(msg);
                    showWarningToast(mActivity.getResources().getString(R.string.save_succ));
                }
            } else if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
            	if (mRequestCount == 0) {
                    // 上传成功
                    Message msg = Message.obtain();
                    msg.what = UPLOAD_FILE_SUCCESS;
                    mShowHandler.sendMessage(msg);
                    showWarningToast(mActivity.getResources().getString(R.string.save_succ));
                }
            } else if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
            	if (mRequestCount == 0) {
                    // 上传成功
                    Message msg = Message.obtain();
                    msg.what = UPLOAD_FILE_SUCCESS;
                    mShowHandler.sendMessage(msg);
                    showWarningToast(mActivity.getResources().getString(R.string.save_succ));
                }
            } else {
                // 上传失败
                showWarningToast(mActivity.getResources().getString(R.string.save_fail));
            }
        }
    };

    /**
     * 上传文件
     */
    private void doUploading(Files files, String path) {
        mProgressBar.setVisibility(View.VISIBLE);
        files.setFile_path(UploadAttachManager.getFilePath(mFileType, files.getFile_name()));
		mUploadAttachManager.uploadDocument(files, new File(path), mProjectName, mTaskName, mTypeName);
		
    }
    
    /**
     * 删除文件
     */
    private void doDeleteFile(Files files) {
    	RemoteDocumentsService.getInstance().deleteFile(mFileManager, files, 0);
    }
    
    private void doModifyFile(Files files) {
    	RemoteDocumentsService.getInstance().updateFile(mFileManager, files, new ArrayList<User>());
    }

    private void uploadFinish() {
//		String attachment = "";
//		for (Files files : mAllFileList) {
//			attachment += files.getAttachment() + ",";
//		}
//		LogUtil.i("wzw att:" + attachment);
		mUploadInterface.dismiss(mAllFileList);
		
    }

    public void startActivityForResult(Intent intent, int code) {
    	if (mFragment != null) {
    		mFragment.startActivityForResult(intent, code);
    	} else {
    		mActivity.startActivityForResult(intent, code);
    	}
    }
    
    /**
     * 从文件管理器选择本地文件
     */
    private void pickLocalFile() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(mActivity, FileExplorerActivity.class);
        intent.putExtra(FileExplorerActivity.IS_MUTIL_SELECT_KEY, true);
        intent.putExtra(FileExplorerActivity.MAX_MUTIL_SELECT_KEY, MAX_UPLOAD_FILES - mAllFileList.size());
        startActivityForResult(intent, FILE_REQUEST_CODE + getCodeNum());
    }

    /**
     * 从gallery选择本地图片
     */
    private void pickLocalPicture() {
        Intent intent = new Intent(mActivity, ImageExplorerActivity.class);
        intent.putExtra(ImageExplorerActivity.MAX_PICTURE_COUNT_KEY, MAX_UPLOAD_FILES - mAllFileList.size());
        startActivityForResult(intent, IMAGE_REQUEST_CODE + getCodeNum());
    }
    
    /**
     * 从相机拍摄选择图片
     */
    private void pickByCapture() {
    	File file = new File(UploadAttachManager.getFilePath(mFileType, ""));
        if (!file.exists()) {
            file.mkdirs();
        }
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = null;
        fileName = mFileType == 0 ? "CEPM360" : GLOBAL.FILE_TYPE[mFileType - 1][1];
        fileName += "_" + UtilTools.getCurrentTime() + ".jpg";
        String path = UploadAttachManager.getFilePath(mFileType, fileName);
        
        Uri cameraFileUri = Uri.fromFile(new File(path)); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
		mPath = path;
        startActivityForResult(intent, CAMERA_REQUEST_CODE + getCodeNum());
    }
    
    private void selectFilePath() {
    	Intent intent = new Intent();
		intent.setClass(mActivity, DirectorySelectActivity.class);
		if (!mSelectIds.equals("0")) {
			intent.putExtra(DirectorySelectActivity.TEMP_DIRS_KEY, mSelectIds);
		}
		intent.putExtra(DirectorySelectActivity.PROJECT_ID_KEY, mUploadFile == null ? 0 : mUploadFile.getProject_id());
		startActivityForResult(intent, SELECT_PATH_REQUEST_CODE + getCodeNum());
    }

    /**
     * activity返回的处理
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPickDialog != null && mPickDialog.isShowing())
            mPickDialog.dismiss();

        if (resultCode == 0)
            return;
        
        int codeNum = getCodeNum();
        
        if (requestCode == (FILE_REQUEST_CODE + codeNum)) {
        	List<String> pathList = data.getExtras().getStringArrayList(FileExplorerActivity.MUTIL_SELECT_LIST_KEY);
            for (String path : pathList) {
            	Files files = new Files();
            	files.setPath(path);
                insertAttachment(files);
            }
        } else if (requestCode == (IMAGE_REQUEST_CODE + codeNum)) {
        	List<String> pathList = data.getExtras()
    				.getStringArrayList(ImageExplorerActivity.IMAGE_SELECT_RESULT_KEY);
            	for (String path : pathList) {
            		Files files = new Files();
                	files.setPath(path);
                    insertAttachment(files);
                }
        } else if (requestCode == (CAMERA_REQUEST_CODE + codeNum)) {
        	Files files = new Files();

        	files.setPath(mPath);
        	
        	// 将日期转换为2015-12-12 06:12:12格式
        	String title = FileUtils.getFileName(mPath);
        	title = FileUtils.formatFileDate(title);
        	files.setTitle(title);
            insertAttachment(files);
        } else if (requestCode == (SELECT_PATH_REQUEST_CODE + codeNum)) {
        	mSelectIds = (String) data.getSerializableExtra(DirectorySelectActivity.DIR_SELECTED_ID_KEY);
        	LogUtil.i("wzw ids:" + mSelectIds);
        	@SuppressWarnings("unchecked")
			List<String> pathList = (List<String>) data.getSerializableExtra(DirectorySelectActivity.DIR_SELECTED_PATH_KEY);
        	String path = "";
        	for (String p : pathList) {
        		path += p + "\n";
        	}
        	if (path.endsWith("\n")) {
        		path = path.substring(0, path.length() - 1);
        	}
        	mSelectPathTextView.setText(path);
//        	LinearLayout parentLine = (LinearLayout) mSelectPathTextView.getParent();
//        	LayoutParams params = (LayoutParams) parentLine.getLayoutParams();
//        	params.height = UtilTools.dp2pxH(mActivity, 35) + pathList.size() * UtilTools.dp2pxH(mActivity, 16);
        	for (Files files : mAllFileList) {
        		if (PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[54][0])) {
        			files.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[1][0]));
        		} else {
        			files.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[2][0]));
        		}
        		
        		files.setDirectory_id(mSelectIds);
        	}
        }

    }
    
    /**
     * 获取文件对应的类型图标
     * @param path
     * @return
     */
    private Drawable getFileDrawable(String path) {
        int res = FileIconHelper.getIconByFilename(path);
        Drawable drawable = mActivity.getResources().getDrawable(res);
        return drawable;
    }
    
    public interface SeleteFileInterface {
    	/**
    	 * 获取选择文件的请求码requestCode,针对一个界面有启相同的activity，区别启动不同的activity
    	 * @return
    	 */
    	int getStartActivityCodeNum();
    }
    
    public interface ProgressBarWindowInterface extends WindowInterface {

    	/**
    	 * 没有底部按钮的界面，需要传入progressBar
    	 */
    	ProgressBar getProgressBar();
    }
    
    public interface WindowInterface {
    	/**
    	 * 是否只是需要上传界面，true 去掉title和按钮
    	 * @return
    	 */
    	boolean noTitleAndButtonWindow();
    }
    
    public interface GetFilesListInterface {
    	
    	/**
    	 * 获取附件列表
    	 * @return
    	 */
    	List<Files> getFilesList();
    }

    public interface GetAttachmentInterface {
    	
    	/**
    	 * 获取附件 格式为12,13 用于界面进入显示
    	 * @return
    	 */
    	String getAttachment();
    }
    
    public interface WaterMarkerUploadInterface extends UploadInterface {
    	/**
    	 * 获取水印的任务名称
    	 * @return
    	 */
    	String getTaskName();
    	
    	/**
    	 * 获取水印的项目名称
    	 * @return
    	 */
    	String getProjectName();
    	
    	/**
    	 * 获取类型名称，如安全监督-安全检查中安全检查为typeName
    	 * @return
    	 */
    	String getTypeName();
    }
    
    public interface UploadInterface {
    	/**
    	 * 文件上传成功调用dismiss接口，例dialog需实现消失dialog，activity则需要finish
    	 * @param fileList
    	 */
    	void dismiss(List<Files> fileList);
    	
    	/**
    	 * 是否需要在该界面做上传操作
    	 * @return
    	 */
    	boolean getIsUpload();
    	
    	/**
    	 * 获取上传文件特有的属性字段，如task_id, project_id, type_id, dir_id, dir_type(GLOBAL.FILE_TYPE)等
    	 * @return
    	 */
    	Files getPreFile();
    	
    	/**
    	 * 获取用户列表，用来上传文件后向指定用户发消息,如果为空，请new一个
    	 * @return
    	 */
    	List<User> getUserList();
    }
}
