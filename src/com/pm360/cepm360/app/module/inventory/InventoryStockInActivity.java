package com.pm360.cepm360.app.module.inventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.fileexplorer.FileExplorerActivity;
import com.pm360.cepm360.app.common.imageexplorer.ImageExplorerActivity;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_RK;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.storehouse.RemoteInStoreService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * 标题: InventoryStockInActivity 
 * 描述: 入库 - 弹出窗口
 *      传入当前要入库的对象-mCurrentBean， 
 *      上传附件， 更新（入库）对象， 成功后，返回对象。
 * 
 * 作者： qiuwei
 * 日期： 2016-5-16
 *
 */
public class InventoryStockInActivity extends Activity {

    private Button mConfirm;
    private ImageView mCancel;
    private ImageView mAddFile;
    private Dialog mPickDialog;
    private ProgressBar mProgressBar;
    
    private TextView mPlanAmount;
    private EditText mPurchaseAmount;
    private EditText mStockInAmount;
    private EditText mStoreHouse;
    private ImageView mmStoreHousePick;

    private Uri mImageFileUri;

    private P_RK mCurrentBean;
    private int mUploadFlag;
    private int mRemoveFlag;
    private User mCurrentUser;
    private List<String> mSelectedPaths = new ArrayList<String>();
    private ArrayList<Files> mUploadFileLists = new ArrayList<Files>();
    private ArrayList<Files> mRemoveFileLists = new ArrayList<Files>();
    private ArrayList<Files> mAttachedFileLists = new ArrayList<Files>();
    
    private LinearLayout mFileListLayout;
    private LinearLayout mContentHeader;

    private static final int FILE_REQUEST_CODE = 0;
    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    
    private static final int MAX_UPLOAD_FILES = 5;

    private static final int SHOW_TOAST = 100;
    private static final int UPLOAD_FILE_DONE = 102;
    private static final int REMOVE_FILE_DONE = 103;
    private static final int STOCK_IN_SUCCESS = 104;
    private static final int STOREHOUSE_REQUEST_CODE = 200;
    
    public static final String VIEW_ATTACHMENT = "view_attachment";
    private boolean isViewMode;

    /**
     * Toast显示
     */
    @SuppressLint("HandlerLeak")
    public Handler mShowHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_TOAST:
                Toast.makeText(InventoryStockInActivity.this,
                        (CharSequence) msg.obj,
                            Toast.LENGTH_SHORT).show();
                break;
            case UPLOAD_FILE_DONE:
                if (mRemoveFileLists.size() > 0) {
                    // 有附件需要删除
                    mRemoveFlag = mRemoveFileLists.size();
                    for (Files files : mRemoveFileLists) {
                        deleteFiles(files);
                    }
                } else {
                    // 对象入库。
                    doStockIn();
                }
                break;
            case REMOVE_FILE_DONE:
                doStockIn();
                break;
            case STOCK_IN_SUCCESS:          
                stockInFinish();
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inventory_stock_in_activity);
        
        Intent intent = getIntent(); 
        mCurrentBean = (P_RK) intent.getSerializableExtra("bean");
        isViewMode = (boolean) intent.getBooleanExtra(VIEW_ATTACHMENT, false);
        mCurrentUser = UserCache.getCurrentUser();

        
        mConfirm = (Button) findViewById(R.id.btn_upload);
        mCancel = (ImageView) findViewById(R.id.btn_close);
        mAddFile = (ImageView) findViewById(R.id.add_file);
        mConfirm.setOnClickListener(mButtonClickListener);
        mCancel.setOnClickListener(mButtonClickListener); 
        mAddFile.setOnClickListener(mButtonClickListener); 
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        
        mContentHeader = (LinearLayout) findViewById(R.id.content_header);
        mFileListLayout = (LinearLayout) findViewById(R.id.file_list_layout);
        
        if (isViewMode) {            
			((TextView) findViewById(R.id.edit_title))
					.setText(getString(R.string.inventory_storein_attachment));
			mContentHeader.setVisibility(View.GONE);
			mAddFile.setVisibility(View.GONE);
			mConfirm.setVisibility(View.GONE);
        } else {
			((TextView) findViewById(R.id.edit_title))
					.setText(getString(R.string.inventory_store_in));
			mContentHeader.setVisibility(View.VISIBLE);
			mAddFile.setVisibility(View.VISIBLE);
        }
        
        initContentView();
        initPickDialog();
        loadBeanAttachments(mCurrentBean);
    }
    
    private void initContentView() {
        mPlanAmount = (TextView) findViewById(R.id.plan_amount);
        mPurchaseAmount = (EditText) findViewById(R.id.purchase_amount);
        mStockInAmount = (EditText) findViewById(R.id.stockin_amount);
        mStoreHouse = (EditText) findViewById(R.id.store_house);
        mmStoreHousePick  = (ImageView) findViewById(R.id.select_store_house);
        
        // 设置文本内容变化监听器
        mPurchaseAmount.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                mStockInAmount.setText(mPurchaseAmount.getText().toString());
            }
        });

        double planAmount = mCurrentBean.getYs_quantity();
        double purchaseAmount = mCurrentBean.getCg_quantity();
        double stockInAmount = mCurrentBean.getIn_quantity();
        mPlanAmount.setText(String.valueOf(planAmount));
        mPurchaseAmount.setText(purchaseAmount == 0 ? String
                .valueOf(planAmount) : String.valueOf(purchaseAmount));
        mStockInAmount.setText(stockInAmount == 0 ? String.valueOf(planAmount)
                : String.valueOf(stockInAmount));

        mStoreHouse.setText(mCurrentBean.getStorehouse());
        mmStoreHousePick.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                int projectId = mCurrentBean.getProject_id();
                Intent intent = new Intent(InventoryStockInActivity.this,
                        StoreHouseSelectActivity.class);
                intent.putExtra(StoreHouseSelectActivity.PROJECT_ID, projectId);
                startActivityForResult(intent, STOREHOUSE_REQUEST_CODE);
                
            }
        });
    }
    
    /**
     * 插入附件
     * @param path
     */
    @SuppressLint("InflateParams") 
    private void insertAttachment(final Files files) {
        final String path = files.getPath();
        if (mSelectedPaths.size() >= MAX_UPLOAD_FILES) {
            showWarningToast(getString(R.string.more_than_max_upload_count));
            return;
        }
        // 检查是否已经添加过该附件
        for (String filePath : mSelectedPaths) {
            if (filePath.equals(path)) {
                showWarningToast(getString(R.string.email_attachment_add_repeat));
                return ;
            }
        }
        
        final View fileItemView = getLayoutInflater().inflate(
                                R.layout.document_upload_file_item_layout, null);
        EditText fileTitle = (EditText) fileItemView.findViewById(R.id.title);
        TextView fileSize = (TextView) fileItemView.findViewById(R.id.file_size);
        ImageView fileType = (ImageView) fileItemView.findViewById(R.id.thumbnail);
        ImageView romoveIcon = (ImageView) fileItemView.findViewById(R.id.remove);
        

        fileTitle.setText(files.getTitle());
        fileSize.setText(FileUtils.getFileSize(files.getFile_size()));
        fileType.setImageDrawable(getFileType(path));
        mFileListLayout.addView(fileItemView);
        
        /**
         * 打开附件监听
         */
        OnClickListener clickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.title:
                case R.id.file_size:
                case R.id.thumbnail:
                case R.id.file_item_view:
                    IntentBuilder.viewFile(getApplication(), files, files.getPath(), false);
                    break;
                case R.id.remove:
                    mFileListLayout.removeView(fileItemView);
                    mSelectedPaths.remove(path);
                    if (files.getFile_id() > 0) {
                        mRemoveFileLists.add(files);
                        mAttachedFileLists.remove(files);
                    }
                    updateAddFileButton();
                    break;
                default:
                    break;
                }
            }
        };

        if (isViewMode) {
            fileTitle.setFocusable(false);
            fileTitle.setBackgroundColor(Color.WHITE);
            mAddFile.setVisibility(View.GONE);
            romoveIcon.setVisibility(View.GONE);
            
            fileItemView.setOnClickListener(clickListener);
            fileTitle.setOnClickListener(clickListener);
            fileSize.setOnClickListener(clickListener);
            fileType.setOnClickListener(clickListener);
        } else {
            romoveIcon.setOnClickListener(clickListener);
            if (files.getFile_id() == 0) {
                mUploadFileLists.add(files);
            }
            mSelectedPaths.add(path);
            updateAddFileButton();
        }
    }
    
    private void updateAddFileButton() {
        if (mSelectedPaths.size() == MAX_UPLOAD_FILES) {
            mAddFile.setVisibility(View.GONE);
        } else {
            mAddFile.setVisibility(View.VISIBLE);
        }
    }
    
    private String getFileTitle(String path) {
        String title = "";
        for (int i = 0; i < mSelectedPaths.size(); i++) {
            if (path.equals(mSelectedPaths.get(i))) {
                EditText titleEdit = (EditText) mFileListLayout.getChildAt(i)
                        .findViewById(R.id.title);
                title = titleEdit.getText().toString();
                break;
            }
        }
        return title;
    }
    
    private boolean checkTitleValid() {
        for (int i = 0; i < mUploadFileLists.size(); i++) {
            String title = mUploadFileLists.get(i).getTitle();
            if (title == null || title.equals("")) {
                showWarningToast(getString(R.string.files_title_empty));
                return false;
            }
        }
        return true;
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
        mConfirm.setClickable(clickable);
    }

    /**
     * 文件添加对话框监听器
     */
    OnClickListener mButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_upload:
                if (mUploadFileLists.size() > 0) {
                    // 上传选择的文件， 先格式化
                    prepareUploading();
                    if (!checkTitleValid()) {
                        return;
                    }
                    mUploadFlag = mUploadFileLists.size();
                    for (int i = 0; i < mUploadFileLists.size(); i++) {
                        // 设置Path, 方便后下载存放路径
                        Files files = mUploadFileLists.get(i);
                        String localPath = files.getPath();
                        files.setPath(GLOBAL.FILE_SAVE_PATH + "/inventory/store_in/" + files.getFile_name());
                        doUploading(files, localPath);
                    }
                } else {
                    doStockIn();
                }
                break;
            case R.id.btn_close:
            	Intent intent = new Intent();
        		setResult(Activity.RESULT_CANCELED, intent);
        		finish();
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
            String path = files.getPath();
            files.setDirectory_id("0");
            files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[34][0]));
            files.setType_id(mCurrentBean.getRk_id());
            files.setAuthor(mCurrentUser.getUser_id());
            files.setTenant_id(mCurrentUser.getTenant_id());
            files.setCreate_time(new Date());            
            files.setTitle(getFileTitle(path));
            
            File file = new File(path);
            files.setFile_size(file.length());
            
            String[] name = path.split("/");
            String fileName = name[name.length - 1];
            files.setFile_type(fileName.substring(fileName.lastIndexOf('.') + 1));
            files.setFile_name(fileName);

            Project project = ProjectCache.getCurrentProject();
            if (project != null) {
                files.setProject_id(project.getProject_id());
            } else {
                files.setProject_id(0);
            }
        }
    }

    /**
     * 上传文件
     */
    private void doUploading(Files files, String path) {
        mProgressBar.setVisibility(View.VISIBLE);
        RemoteDocumentsService.getInstance().uploadFile(
                new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status,
                            List<?> list) {
                        mUploadFlag--;
                        if ((status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
                                && list != null && list.size() != 0) {
                            mAttachedFileLists.add((Files) list.get(0));
                        } else {
                            // 上传失败
                            showWarningToast(getResources().getString(R.string.upload_fail_retry));
                        }
                        if (mUploadFlag == 0) {
                            // 上传成功
                            Message msg = Message.obtain();
                            msg.what = UPLOAD_FILE_DONE;
                            mShowHandler.sendMessage(msg);
                        }

                    }

                }, mProgressBar, files,
                UserCache.getCurrentUser().getTenant_id(),
                new File(path), new ArrayList<User>(),
                UserCache.getCurrentUser().getUser_id());
    }
    
    /**
     * 删除老的附件
     * @param files
     */
    private void deleteFiles(Files files) {
        RemoteDocumentsService.getInstance().deleteFile(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mRemoveFlag--;
                if (mRemoveFlag == 0) {
                    // 上传成功
                    Message msg = Message.obtain();
                    msg.what = REMOVE_FILE_DONE;
                    mShowHandler.sendMessage(msg);
                }                
            }            
        }, files, 0);
    } 
    
    /**
     * 入库
     */
    private void doStockIn() {
        StringBuilder attachmentBuilder = new StringBuilder();
        if (mAttachedFileLists.size() > 0) {
            for (int i = 0; i < mAttachedFileLists.size(); i++) {
                attachmentBuilder.append(mAttachedFileLists.get(i).getFile_id() + ",");
            }
            String attachments = attachmentBuilder.toString();
            if (attachments.endsWith(",")) {
                attachments = attachments.substring(0, attachments.length() - 1);
                mCurrentBean.setAttachment(attachments);
            };
        } else {
            mCurrentBean.setAttachment("");
        }
        
        mCurrentBean.setCg_quantity(Double.parseDouble(mPurchaseAmount.getText().toString()));
        mCurrentBean.setIn_quantity(Double.parseDouble(mStockInAmount.getText().toString()));
        mCurrentBean.setStorehouse(mStoreHouse.getText().toString());
        mCurrentBean.setOperator(mCurrentUser.getUser_id());
        mCurrentBean.setIn_date(new Date());
        
        RemoteInStoreService.getInstance().checkIn(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                if (status.getMessage() != null && !status.getMessage().equals("")) {
                    showWarningToast(status.getMessage());
                }
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE)) {
                    mCurrentBean.setStatus(Integer.valueOf(GLOBAL.RK_TYPE[1][0]));
                    
                    Message msg = Message.obtain();
                    msg.what = STOCK_IN_SUCCESS;
                    mShowHandler.sendMessage(msg);
                } else { // 入库失败                    
                    showWarningToast(getResources().getString(R.string.upload_fail_retry));
                }              
            }            
        }, mCurrentBean);
    }

    private void stockInFinish() {
        Intent intent = new Intent();
        intent.putExtra("bean", mCurrentBean);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    /**
     * 从文件管理器选择本地文件
     */
    private void pickLocalFile() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(InventoryStockInActivity.this, FileExplorerActivity.class);
        intent.putExtra(FileExplorerActivity.IS_MUTIL_SELECT_KEY, true);
        intent.putExtra(FileExplorerActivity.MAX_MUTIL_SELECT_KEY, MAX_UPLOAD_FILES - mSelectedPaths.size());
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    /**
     * 从gallery选择本地图片
     */
    private void pickLocalPicture() {
        Intent intent = new Intent(InventoryStockInActivity.this, ImageExplorerActivity.class);
        intent.putExtra(ImageExplorerActivity.MAX_PICTURE_COUNT_KEY, MAX_UPLOAD_FILES - mSelectedPaths.size());
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
        case STOREHOUSE_REQUEST_CODE:
            String storeHouse = data.getStringExtra(StoreHouseSelectActivity.RESULT_KEY);
            mStoreHouse.setText(storeHouse);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
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
    
    /**
     * 获取对象的所有的附件列表
     * @param Bean
     */
    private void loadBeanAttachments(P_RK bean) {
        mSelectedPaths.clear();
        mAttachedFileLists.clear();
        
        String attachments = bean.getAttachment();
        if (attachments != null && !attachments.equals("")) {
            RemoteInStoreService.getInstance().getRKFiles(new DataManagerInterface() {
    
                @Override
                public void getDataOnResult(ResultStatus status, List<?> list) {                
                    if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                        for(int i = 0; i < list.size(); i++) {                        
                            Files files = (Files) list.get(i);
                            mAttachedFileLists.add(files);
                            insertAttachment(files);
                        }
                    }                
                }           
            }, attachments);
        }
    }
}
