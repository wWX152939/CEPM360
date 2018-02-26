package com.pm360.cepm360.app.module.document;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文档上传窗口
 *
 *	注意的是这里的path问题：
 *	1.上传之前需要用到当前的文件在设备中的真实路径  (/sdcard/DCIM/100Camera/xxx.jpg)
 *	2.同时设置了一个未来下载的时候用的存放路径 (/sdcard/CEMP360/download/)
 *
 *	返回上传成功后的Files对象列表
 */
public class DocumentUploadActivity extends Activity {

    private TextView mTitle;
    private TextView mRemoteLocation;
    private Button mUpload;
    private ImageView mCancel;
    private ImageView mAddFile;    
    private ProgressBar mProgressBar;
    private Dialog mPickDialog;
    private ProgressDialog mProgressDialog;

    private Uri mImageFileUri;

    private Files mUploadFile;
    private int mUploadFlag;
    private List<String> mSelectedFileList = new ArrayList<String>();
    private ArrayList<Files> mUploadFileLists = new ArrayList<Files>();
    private ArrayList<Files> mAttachedFileLists = new ArrayList<Files>();
    private List<User> mUserList = new ArrayList<User>();
    
    private LinearLayout mFileListLayout;
    
    private static final int MAX_UPLOAD_FILES = 5;

    private static final int FILE_REQUEST_CODE = 0;
    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private static final int SHOW_TOAST = 100;
    private static final int DO_UPLOAD_FILE = 101;
    private static final int UPLOAD_FILE_SUCCESS = 102;
    private static final int DIR_SELECT_REQUEST = 103;
    
    // 上传之前传入的对象
    public static final String UPLOAD_FILE_KEY = "file_bean";
    // 上传成功之后传回的对象列表
    public static final String UPLOAD_FILE_LIST_KEY = "file_list";
    // 是否需要上传操作
    public static final String NEED_UPLOAD_KEY = "is_upload";
    // 是否需要发消息
    public static final String NEED_SEND_MSG_KEY = "is_send_msg";
    
    // 是否上传
    private boolean mIsUpload;
    // 是否发消息
    private boolean mIsSendMsg;

    /**
     * Toast显示
     */
    @SuppressLint("HandlerLeak")
    public Handler mShowHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_TOAST:
                Toast.makeText(DocumentUploadActivity.this,
                        (CharSequence) msg.obj,
                            Toast.LENGTH_SHORT).show();
                break;
            case DO_UPLOAD_FILE:
                //doUploading();
                break;
            case UPLOAD_FILE_SUCCESS:
                uploadFinish((Files) msg.obj);
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.document_upload_file_activity);
        
        Intent intent = getIntent();
        mUploadFile = (Files) intent.getSerializableExtra(UPLOAD_FILE_KEY);
        mIsUpload = intent.getBooleanExtra(NEED_UPLOAD_KEY, true);
        mIsSendMsg = intent.getBooleanExtra(NEED_SEND_MSG_KEY, false);

        mTitle = (TextView) findViewById(R.id.edit_title);
        mTitle.setText(getString(R.string.document_upload));
        mUpload = (Button) findViewById(R.id.btn_upload);
        mCancel = (ImageView) findViewById(R.id.btn_close);
        mAddFile = (ImageView) findViewById(R.id.add_file);
        mUpload.setOnClickListener(mButtonClickListener);
        mCancel.setOnClickListener(mButtonClickListener); 
        mAddFile.setOnClickListener(mButtonClickListener);
        
        mRemoteLocation = (TextView) findViewById(R.id.upload_path_select);
   
        if (mUploadFile == null || mUploadFile.getPath() == null) { // equals("")
            mRemoteLocation.setOnClickListener(mButtonClickListener);
            mUploadFile.setDirectory_id("0");
        } else {
            mRemoteLocation.setText(mUploadFile.getPath().toString());
            mRemoteLocation.setClickable(false);
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        initContentView();
        initPickDialog();
        
        //如果需要发消息，则或许用户列表
        if (mIsSendMsg) {
            loadUser();
        }
    }
    
    private void loadUser() {        
        if (mUploadFile.getDir_type() == Integer.valueOf(GLOBAL.FILE_TYPE[0][0])) {
            // 公共文档, 获取公司所有成员
            mUserList.addAll(UserCache.getUserLists());        
        } else if (mUploadFile.getDir_type() == Integer.valueOf(GLOBAL.FILE_TYPE[9][0])) {            
            // 项目文档, 获取项目成员
            showProgressDialog("Loading data...");
            RemoteUserService.getInstance().getProjectUsers(new DataManagerInterface() {

                @SuppressWarnings("unchecked")
                @Override
                public void getDataOnResult(ResultStatus status,
                        List<?> list) {
                    dismissProgressDialog();
                    if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                            && list != null && list.size() > 0) {
                        mUserList.addAll((List<User>) list);
                    }
                }
            }, ProjectCache.getCurrentProject());
        } else {
            // 私人文档 , 不发消息
        }
    }
    
    private void initContentView() {
        // 获取列表使用的相关资源
        mFileListLayout = (LinearLayout) findViewById(R.id.file_list_layout);
    }
    
    /**
     * 插入附件
     * @param path
     */
    @SuppressLint("InflateParams") 
    private void insertAttachment(final Files files) {
        final String path = files.getPath();
        if (mSelectedFileList.size() >= MAX_UPLOAD_FILES) {
            showWarningToast(getString(R.string.more_than_max_upload_count));
            return;
        }
        // 检查是否已经添加过该附件
        for (String filePath : mSelectedFileList) {
            if (filePath.equals(path)) {
                showWarningToast(getString(R.string.email_attachment_add_repeat));
                return ;
            }
        }
        
        final View fileItemView = getLayoutInflater().inflate(
                                R.layout.document_upload_file_item_layout, null);
        EditText fileName = (EditText) fileItemView.findViewById(R.id.title);
        TextView fileSize = (TextView) fileItemView.findViewById(R.id.file_size);
        ImageView fileType = (ImageView) fileItemView.findViewById(R.id.thumbnail);
        ImageView romoveIcon = (ImageView) fileItemView.findViewById(R.id.remove);
        
        /**
         * 打开附件监听
         */
        OnClickListener clickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // 修改标题
            }
        };
        
        fileName.setOnClickListener(clickListener);
        
        fileName.setText(files.getTitle());
        fileSize.setText(FileUtils.getFileSize(files.getFile_size()));
        fileType.setImageDrawable(getFileType(path));
        
        romoveIcon.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mFileListLayout.removeView(fileItemView);
                mSelectedFileList.remove(path);
                mUploadFileLists.remove(files);
                updateAddFileButton();
            }
        });
        
        mFileListLayout.addView(fileItemView);
        mSelectedFileList.add(path);
        mUploadFileLists.add(files);
        updateAddFileButton();
    }
    
    private void updateAddFileButton() {
        if (mSelectedFileList.size() == MAX_UPLOAD_FILES) {
            mAddFile.setVisibility(View.GONE);
        } else {
            mAddFile.setVisibility(View.VISIBLE);
        }
    }
    
    private String getFileTitle(int index) {
        String title = "";
        EditText titleEdit = (EditText) mFileListLayout.getChildAt(index).findViewById(R.id.title);
        title = titleEdit.getText().toString();
        return title;
    }
    
    /**
     * 初始化选择附件对话框
     */
    private void initPickDialog() {
        mPickDialog = new Dialog(this, R.style.MyDialogStyle);
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
        mUpload.setClickable(clickable);
    }

    /**
     * 文件添加对话框监听器
     */
    OnClickListener mButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_upload:
                if (!checkTitleValid()) {
                    return;
                }
            	prepareUploading();
            	
            	if (mIsUpload) {
            		for (int i = 0; i < mUploadFileLists.size(); i++) {
                        // 设置Path, 方便后下载存放路径
                        Files files = mUploadFileLists.get(i);
                        String localPath = files.getPath();
                        files.setPath(GLOBAL.FILE_SAVE_PATH + "/Default/download/" + files.getFile_name());
                        doUploading(files, localPath);
            		}
            	} else {
            		Intent intent = new Intent();
            		intent.putExtra(UPLOAD_FILE_LIST_KEY, mUploadFileLists);
            		LogUtil.e("mFileList = " + mUploadFileLists);
            		setResult(Activity.RESULT_OK, intent);
            		finish();
            	}
                break;
            case R.id.btn_close:
            	Intent intent = new Intent();
        		setResult(Activity.RESULT_CANCELED, intent);
        		finish();
                break;
            case R.id.upload_path_select:
                startDirectorySelectActivity();
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
            }
        }
    };

    private void showWarningToast(String obj) {
        Message msg = Message.obtain();
        msg.what = SHOW_TOAST;
        msg.obj = obj;
        mShowHandler.sendMessage(msg);
    }

    /**
     * 保存按钮的处理函数，保存录入数据, 首先获取用户列表
     */
    private void prepareUploading() {
        setUIClickable(false);
        for (int i = 0; i < mUploadFileLists.size(); i++) {
            Files files = mUploadFileLists.get(i);            
            files.setTitle(getFileTitle(i));
            files.setStatus(getString(R.string.new_create));
            files.setVersion("1.0");
            files.setCreate_time(new Date());
            files.setDir_type(mUploadFile.getDir_type());
            files.setType_id(mUploadFile.getType_id());
            files.setArchive(mUploadFile.getArchive());
            if (mUploadFile.getDirectory_id() == null || mUploadFile.getDirectory_id().equals("")) {
                files.setDirectory_id("0");
            } else {
                files.setDirectory_id(mUploadFile.getDirectory_id());
            }
            
            String path = files.getPath();           
            String[] name = path.split("/");
            String fileName = name[name.length - 1];            
            files.setFile_type(fileName.substring(fileName.lastIndexOf('.') + 1));
            files.setFile_name(fileName);
            User user = UserCache.getCurrentUser();
            files.setAuthor(user.getUser_id());
            files.setTenant_id(user.getTenant_id());
            Project project = ProjectCache.getCurrentProject();
            if (project != null) {
                files.setProject_id(project.getProject_id());
            } else {
                files.setProject_id(0);
            }
        }
    }
    
    private boolean checkTitleValid() {
        for (int i = 0; i < mUploadFileLists.size(); i++) {
            String title = getFileTitle(i);
            if (title == null || title.equals("")) {
                showWarningToast(getString(R.string.files_title_empty));
                return false;
            }
        }
        return true;
    }

    /**
     * 上传文件
     */
    private void doUploading(Files files, String path) {
        mProgressBar.setVisibility(View.VISIBLE);
        mUploadFlag = mSelectedFileList.size();        
        RemoteDocumentsService.getInstance().uploadFile(fileManager,
                mProgressBar, files, UserCache.getCurrentUser().getTenant_id(),
                new File(path), mUserList, 
                UserCache.getCurrentUser().getUser_id());
    }

    private void uploadFinish(Files file) {
        Intent intent = new Intent();
        intent.putExtra(UPLOAD_FILE_LIST_KEY, mAttachedFileLists);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 文档回调接口
     */
    DataManagerInterface fileManager = new DataManagerInterface() {

        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            setUIClickable(true);
            if (status.getMessage() != null && !status.getMessage().equals("")) {
                showWarningToast(status.getMessage());
            }
            mUploadFlag--;
            if ((status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
                    && list != null && list.size() != 0) {
                mAttachedFileLists.add((Files) list.get(0));
                if (mUploadFlag == 0) {
                    // 上传成功
                    Message msg = Message.obtain();
                    msg.what = UPLOAD_FILE_SUCCESS;
                    msg.obj = (Files) list.get(0);
                    mShowHandler.sendMessage(msg);
                }
            } else {
                // 上传失败
                showWarningToast(getResources().getString(R.string.upload_fail_retry));
            }
        }
    };

    /**
     * 只为获取用户列表设计
     */
    DataManagerInterface userManager = new DataManagerInterface() {
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            switch (status.getCode()) {
            case AnalysisManager.SUCCESS_DB_QUERY:
                break;
            default:
                break;
            }
            // 显示接口反馈的信息
            Message msg = Message.obtain();
            msg.what = DO_UPLOAD_FILE;
            mShowHandler.sendMessage(msg);
        }
    };
    
    /**
     * 选择上传目录
     */
    public void startDirectorySelectActivity() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(DocumentUploadActivity.this,
                DirectorySelectActivity.class);
        intent.putExtra(DirectorySelectActivity.PROJECT_ID_KEY, mUploadFile.getProject_id());
        startActivityForResult(intent, DIR_SELECT_REQUEST);
    }
    
    /**
     * 从文件管理器选择本地文件
     */
    private void pickLocalFile() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(DocumentUploadActivity.this, FileExplorerActivity.class);
        intent.putExtra(FileExplorerActivity.IS_MUTIL_SELECT_KEY, true);
        intent.putExtra(FileExplorerActivity.MAX_MUTIL_SELECT_KEY, MAX_UPLOAD_FILES - mSelectedFileList.size());
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    /**
     * 从gallery选择本地图片
     */
    private void pickLocalPicture() {
        Intent intent = new Intent(DocumentUploadActivity.this, ImageExplorerActivity.class);
        intent.putExtra(ImageExplorerActivity.MAX_PICTURE_COUNT_KEY, MAX_UPLOAD_FILES - mSelectedFileList.size());
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    /**
     * 从相机拍摄选择图片
     */
    private void pickByCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
        values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");

        mImageFileUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * activity返回的处理
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPickDialog != null && mPickDialog.isShowing())
            mPickDialog.dismiss();

        if (resultCode == 0)
            return;

        switch (requestCode) {
        case FILE_REQUEST_CODE:
            List<String> pathList = data.getExtras().getStringArrayList(FileExplorerActivity.MUTIL_SELECT_LIST_KEY);
            for (String path : pathList) {
                formatAttachment(path);
            }
            break;
        case IMAGE_REQUEST_CODE:
            List<String> imageList = data.getExtras().getStringArrayList(ImageExplorerActivity.IMAGE_SELECT_RESULT_KEY);
            for (String path : imageList) {
                formatAttachment(path);
            }
            break;
        case CAMERA_REQUEST_CODE:
            formatAttachment(URIToURL(mImageFileUri));
            break;
        case DIR_SELECT_REQUEST:
            String dirs = data.getStringExtra(DirectorySelectActivity.DIR_SELECTED_ID_KEY);
            ArrayList<String> pathlists = data.getStringArrayListExtra(DirectorySelectActivity.DIR_SELECTED_PATH_KEY);
            mUploadFile.setDirectory_id(dirs);
            if (pathlists.size() > 0) {
                String paths = pathlists.get(0);
                for (int i = 1; i < pathlists.size(); i++) {
                    paths = pathlists.get(i) + "\n" + paths;
                }
                mRemoteLocation.setText(paths);
                if (PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[54][0])) {
                    mUploadFile.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[1][0]));
                } else {
                    mUploadFile.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[2][0]));
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
//    private String getDirectoryIds(Files files, ArrayList<Document> dirList) {
//        String dirs = null;
//        String[] oldDirs = files.getDirectory_id().split(",");
//        if (oldDirs != null && oldDirs.length > 0) {
//            StringBuilder docBuilder = new StringBuilder();
//            for (Document doc : dirList) {
//                if (!contain(oldDirs, String.valueOf(doc.getDocument_id()))) {
//                    docBuilder.append(doc.getDocument_id() + ",");
//                }
//            }
//            dirs = docBuilder.substring(0, docBuilder.length());
//        }
//        return dirs;
//    }
    
    private void formatAttachment(String path) {
        Files files = new Files();                
        files.setTitle(FileUtils.getFileName(path));
        files.setPath(path);
        
        File file = new File(path);
        files.setFile_size(file.length());
        
        insertAttachment(files);
    }

    @SuppressWarnings("deprecation")
    private String URIToURL(Uri uri) {
        String[] imgs = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.managedQuery(uri, imgs, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(index);
        if (Integer.parseInt(Build.VERSION.SDK) < 14) {
            cursor.close();
        }
        return url;
    }
    
    /**
     * 获取文件对应的类型图标
     * @param path
     * @return
     */
    private Drawable getFileType(String path) {
        int res = FileIconHelper.getIconByFilename(path);
        Drawable drawable = getResources().getDrawable(res);
        return drawable;
    }
    
    public static boolean contain(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }
    
    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(DocumentUploadActivity.this, true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
