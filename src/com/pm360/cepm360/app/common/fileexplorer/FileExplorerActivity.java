package com.pm360.cepm360.app.common.fileexplorer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.fileexplorer.FileViewInteractionHub.Mode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FileExplorerActivity extends Activity implements IFileInteractionListener {
    
    public static final String IS_MUTIL_SELECT_KEY = "mutil_select";
    public static final String MAX_MUTIL_SELECT_KEY = "max_select";
    public static final String MUTIL_SELECT_LIST_KEY = "mutil_select_list";
    public static final String FILE_PATH_KEY = "FILE_PATH";
    
    public static final String PROJECT_FOLDER = "root_directory";
    public static final String SD_FOLDER = "pick_folder";
    private ListView mFileListView;
    
    private ImageView mBackIcon;
    private TextView mBack;
    private TextView mCancel;
    private TextView mConfirm;
    private TextView mProjectFolder;
    private TextView mSDFolder;
    private TextView mFileCount;
    private TextView mUSBFolder;

    private LinearLayout mFileListLayout;
    private LinearLayout mFolderCategoryLayout;
    private LinearLayout mUsbOtgView;
    
    private FileViewInteractionHub mFileViewInteractionHub;
    private FileCategoryHelper mFileCagetoryHelper;
    private FileIconHelper mFileIconHelper;
    private FileListAdapter mAdapter;
    private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();
    
    private ArrayList<PathScrollPositionItem> mScrollPositionList = new ArrayList<PathScrollPositionItem>();
    private String mPreviousPath;
    @SuppressWarnings("unused")
	private String mSelectedPath;
    private String mRootDir;
    
    private int mMaxSelectCount;
    private boolean mMultiSelect;
    private ArrayList<String> mFilePathList = new ArrayList<String>();
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                    || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                    || action.equals(Intent.ACTION_MEDIA_REMOVED)
                    || action.equals(Intent.ACTION_MEDIA_EJECT)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        }
    };
       
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_file_explorer);
        
        mBackIcon = (ImageView) findViewById(R.id.back_icon);
        mBack = (TextView) findViewById(R.id.back);
        mCancel = (TextView) findViewById(R.id.cancel);
        mConfirm = (TextView) findViewById(R.id.confirm);        
        mSDFolder = (TextView) findViewById(R.id.sdcard_folder);
        mProjectFolder = (TextView) findViewById(R.id.project_folder);
        mUSBFolder = (TextView) findViewById(R.id.usb_folder);
        mFileCount = (TextView) findViewById(R.id.selected_file_size);
        mBackIcon.setOnClickListener(mClickListener);
        mBack.setOnClickListener(mClickListener);
        mCancel.setOnClickListener(mClickListener);
        mConfirm.setOnClickListener(mClickListener);
        mSDFolder.setOnClickListener(mClickListener);
        mUSBFolder.setOnClickListener(mClickListener);
        mProjectFolder.setOnClickListener(mClickListener);
        
        mUsbOtgView = (LinearLayout) findViewById(R.id.usb_otg_layout);
        
        mConfirm.setClickable(false);

        mFileListLayout = (LinearLayout) findViewById(R.id.file_list_layout);
        mFolderCategoryLayout = (LinearLayout) findViewById(R.id.folder_category_layout);
        
        mFileCagetoryHelper = new FileCategoryHelper(this);
        mFileViewInteractionHub = new FileViewInteractionHub(this, this);
        mFileViewInteractionHub.setMode(Mode.View);
        
        mFileListView = (ListView) findViewById(R.id.file_path_list);
        mFileIconHelper = new FileIconHelper(this);
        mAdapter = new FileListAdapter(this, R.layout.file_browse_item, mFileNameList, mFileViewInteractionHub,
                mFileIconHelper);
        
        mRootDir = Util.getSdDirectory();
        File file = new File(mRootDir);
        if (!file.exists()) {
            file.mkdir();
        }
        mFileViewInteractionHub.setRootPath(mRootDir);
        //mFileViewInteractionHub.setCurrentPath(rootDir);
        
        mFileListView.setAdapter(mAdapter);
        mFileViewInteractionHub.refreshFileList();
        mMultiSelect = getIntent().getBooleanExtra(IS_MUTIL_SELECT_KEY, false);
        if (mMultiSelect) {
            mMaxSelectCount = getIntent().getIntExtra(MAX_MUTIL_SELECT_KEY, 1);
            mFileViewInteractionHub.setMultiSelect(mMultiSelect, mMaxSelectCount);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT); 
        intentFilter.addDataScheme("file");
        registerReceiver(mReceiver, intentFilter);

        updateUI();
        
    }
    
    View.OnClickListener mClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.back_icon:
            case R.id.back:
                switchContentPage(true);                
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (mMultiSelect) {
                    bundle.putStringArrayList(MUTIL_SELECT_LIST_KEY, getSelectedAllPath());
                } else {
                    bundle.putString(FILE_PATH_KEY, getSelectedPath());
                }
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.sdcard_folder:
                switchContentPage(false);
                mRootDir = Util.getSdDirectory();
                mFileViewInteractionHub.setRootPath(mRootDir);
                //mFileViewInteractionHub.setCurrentPath(rootDir);
                mFileViewInteractionHub.refreshFileList();
                break;
            case R.id.project_folder:
                switchContentPage(false);
                mRootDir = Util.getProjectDirectory();
                mFileViewInteractionHub.setRootPath(mRootDir);
                //mFileViewInteractionHub.setCurrentPath(projectDir);
                mFileViewInteractionHub.refreshFileList();
                break;
            case R.id.usb_folder:
                switchContentPage(false);
                if (Util.isUsbOtgReady()) {
                    mRootDir = Util.getUsbOtgDirectory();
                    mFileViewInteractionHub.setRootPath(mRootDir);
                    mFileViewInteractionHub.refreshFileList();
                }
                break;
            }           
        }
    };
    
    private void switchContentPage(boolean homePage) {
        if (homePage) {
            mFileListLayout.setVisibility(View.GONE);
            mFolderCategoryLayout.setVisibility(View.VISIBLE);
            mBack.setVisibility(View.INVISIBLE);
            mBackIcon.setVisibility(View.INVISIBLE);
        } else {
            mFileListLayout.setVisibility(View.VISIBLE);
            mFolderCategoryLayout.setVisibility(View.GONE);
            mBack.setVisibility(View.VISIBLE);
            mBackIcon.setVisibility(View.VISIBLE);
        }
    }
    
    private String getSelectedPath() {
        if (mFilePathList.size() > 0) {
            return mFilePathList.get(0);
        }
        return "";    
    }
    
    private ArrayList<String> getSelectedAllPath() {
        return mFilePathList;
    }
    
//    private void setSelectedPath(String path) {
//        mFilePathList = path;
//    }
    
    private class PathScrollPositionItem {
        String path;
        int pos;
        PathScrollPositionItem(String s, int p) {
            path = s;
            pos = p;
        }
    }
    
    private int computeScrollPosition(String path) {
        int pos = 0;
        if(mPreviousPath!=null) {
            if (path.startsWith(mPreviousPath)) {
                int firstVisiblePosition = mFileListView.getFirstVisiblePosition();
                if (mScrollPositionList.size() != 0
                        && mPreviousPath.equals(mScrollPositionList.get(mScrollPositionList.size() - 1).path)) {
                    mScrollPositionList.get(mScrollPositionList.size() - 1).pos = firstVisiblePosition;
                    pos = firstVisiblePosition;
                } else {
                    mScrollPositionList.add(new PathScrollPositionItem(mPreviousPath, firstVisiblePosition));
                }
            } else {
                int i;
                for (i = 0; i < mScrollPositionList.size(); i++) {
                    if (!path.startsWith(mScrollPositionList.get(i).path)) {
                        break;
                    }
                }
                // navigate to a totally new branch, not in current stack
                if (i > 0) {
                    pos = mScrollPositionList.get(i - 1).pos;
                }

                for (int j = mScrollPositionList.size() - 1; j >= i-1 && j>=0; j--) {
                    mScrollPositionList.remove(j);
                }
            }
        }

        mPreviousPath = path;
        return pos;
    }
    
    @Override
    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }
        final int pos = computeScrollPosition(path);
        ArrayList<FileInfo> fileList = mFileNameList;
        fileList.clear();

        File[] listFiles = file.listFiles(mFileCagetoryHelper.getFilter());
        if (listFiles == null)
            return true;

        for (File child : listFiles) {
            // do not show selected file if in move state
            if (mFileViewInteractionHub.isMoveState() && mFileViewInteractionHub.isFileSelected(child.getPath()))
                continue;

            String absolutePath = child.getAbsolutePath();
            if (Util.isNormalFile(absolutePath) && Util.shouldShowFile(absolutePath)) {
                FileInfo lFileInfo = Util.GetFileInfo(child,
                        mFileCagetoryHelper.getFilter(), false/*Settings.instance().getShowDotAndHiddenFiles()*/);
                if (lFileInfo != null) {
                    fileList.add(lFileInfo);
                }
            }
        }

        sortCurrentList(sort);
        showEmptyView(fileList.size() == 0);
        mFileListView.post(new Runnable() {
            @Override
            public void run() {
                mFileListView.setSelection(pos);
            }
        });
        return true;
    }
    
    private void updateUI() {                
        mUsbOtgView.setVisibility(Util.isUsbOtgReady() ? View.VISIBLE : View.GONE);
        
        boolean sdCardReady = Util.isSDCardReady();
        View navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
        mFileListView.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

        if(sdCardReady) {
            mFileViewInteractionHub.refreshFileList();
        }
    }

    private void showEmptyView(boolean show) {
        View emptyView = findViewById(R.id.empty_view);
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public View getViewById(int id) {
        return findViewById(id);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onDataChanged() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void onPick(FileInfo f, int position, boolean checked) {
        String path = new File(f.filePath).toString();
        if (checked) {
            if (!mMultiSelect) {
                mFilePathList.clear();
            }
            mFilePathList.add(path);        
//            mConfirm.setClickable(true);
//            mConfirm.setVisibility(View.VISIBLE);
//            mConfirm.setBackgroundColor(Color.parseColor("#41b89c"));
//            
//            String path = new File(f.filePath).toString();
//            setSelectedPath(path);
//            mFileSize.setVisibility(View.VISIBLE);
//            mFileSize.setText("已选" + Util.convertStorage(f.fileSize));
//            
//            onDataChanged();
        } else {
            mFilePathList.remove(path);
//            mConfirm.setVisibility(View.INVISIBLE);
//            mFileSize.setVisibility(View.INVISIBLE);
//            return;
        }
        updatePickStatus();
    }
    
    private void updatePickStatus() {
        ArrayList<FileInfo> selectedList = mFileViewInteractionHub.getSelectedFileList();
        if (selectedList.size() == 0) {
            mConfirm.setVisibility(View.INVISIBLE);
            mFileCount.setVisibility(View.INVISIBLE);
        } else {
            mConfirm.setClickable(true);
            mConfirm.setVisibility(View.VISIBLE);
            mConfirm.setBackgroundColor(Color.parseColor("#41b89c"));
            mFileCount.setVisibility(View.VISIBLE);
            //int totalSize = 0;
            //for (FileInfo f : selectedList) {
            //    totalSize += f.fileSize;
            //}
            mFileCount.setText(getString(R.string.multi_select_title, mFilePathList.size()));      
        }
    }
    

    @Override
    public boolean shouldShowOperationPane() {
        return true;
    }

    @Override
    public boolean onOperation(int id) {
        return false;
    }

    @Override
    public String getDisplayPath(String path) {
        String root = mFileViewInteractionHub.getRootPath();
        if (root.equals(path)) {
            if (mRootDir.equals(Util.getSdDirectory())) {
                return getString(R.string.sd_folder);
            } else if (mRootDir.equals(Util.getUsbOtgDirectory())) {
                return getString(R.string.usb_folder);
            } else {
                return getString(R.string.cepm360_folder);
            }
        } 
        
        if (!root.equals("/")) {
            int pos = path.indexOf(root);
            if (pos == 0) {
                path = path.substring(root.length());
            }
        }
        //return getString(R.string.sd_folder) + path;
        if (mRootDir.equals(Util.getSdDirectory())) {
            return getString(R.string.sd_folder) + path;
        } else if (mRootDir.equals(Util.getUsbOtgDirectory())) {
            return getString(R.string.usb_folder);
        } else {
            return getString(R.string.cepm360_folder) + path;
        }
    }

    @Override
    public String getRealPath(String displayPath) {
        String root = mFileViewInteractionHub.getRootPath();
        if (displayPath.equals(getString(R.string.sd_folder))) {
        //if (displayPath.equals(getString(R.string.cepm360_folder))){
            return root;
        }
            

        String ret = displayPath.substring(displayPath.indexOf("/"));
        if (!root.equals("/")) {
            ret = root + ret;
        }
        return ret;
    }

    @Override
    public boolean onNavigation(String path) {
        return false;
    }

    @Override
    public boolean shouldHideMenu(int menu) {
        return false;
    }
    
    public void copyFile(ArrayList<FileInfo> files) {
        mFileViewInteractionHub.onOperationCopy(files);
    }

    public void refresh() {
        mFileViewInteractionHub.refreshFileList();
    }

    public void moveToFile(ArrayList<FileInfo> files) {
        mFileViewInteractionHub.moveFileFrom(files);
    }

    public interface SelectFilesCallback {
        // files equals null indicates canceled
        void selected(ArrayList<FileInfo> files);
    }

    public void startSelectFiles(SelectFilesCallback callback) {
        mFileViewInteractionHub.startSelectFiles(callback);
    }

    @Override
    public FileIconHelper getFileIconHelper() {
        return mFileIconHelper;
    }

    @Override
    public FileInfo getItem(int pos) {
        if (pos < 0 || pos > mFileNameList.size() - 1)
            return null;

        return mFileNameList.get(pos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sortCurrentList(FileSortHelper sort) {
        Collections.sort(mFileNameList, sort.getComparator());
        onDataChanged();
    }

    @Override
    public Collection<FileInfo> getAllFiles() {
        return mFileNameList;
    }

    @Override
    public void addSingleFile(FileInfo file) {
        mFileNameList.add(file);
        onDataChanged();
    }

    @Override
    public int getItemCount() {
        return mFileNameList.size();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    
    
    
}
