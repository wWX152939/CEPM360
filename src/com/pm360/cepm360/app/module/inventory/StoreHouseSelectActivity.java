package com.pm360.cepm360.app.module.inventory;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.SimpleListViewAdapter;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.project.RemoteProjectService;

import java.util.List;

/**
 * 库房选择窗口
 * 
 * - 如果该项目的库房列表为空，支持创建新库房
 * - 返回库房名称
 *
 */
public class StoreHouseSelectActivity extends Activity {
    private int mProjectId;
    private Project mProject;
    private Button mConfirmButton;
    private ImageView mCancelButton;
    private Button mNewStoreHouse;
    private TextView mEmptyView;
    private ListView mListView;
    private SimpleListViewAdapter mAdapter;
    private String[] mStoreHouseLists = new String[]{};
    
    private Dialog mStorehouseDialog;
    private ProgressDialog mProgressDialog;
    private TextView mStorehouseTitle;
    private EditText mStorehouse;
    private Button mStorehouseOK;
    private ImageView mStorehouseCancel;
    
    private static final int DATA_LOADED = 100;
    private static final int DATA_EMPTY = 101;
    
    public static final String PROJECT_ID = "project_id";
    public static final String RESULT_KEY = "result_key";
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case DATA_EMPTY:
                mEmptyView.setVisibility(View.VISIBLE);
                mNewStoreHouse.setVisibility(View.VISIBLE);
                break;
            case DATA_LOADED:
                mEmptyView.setVisibility(View.GONE);
                mNewStoreHouse.setVisibility(View.GONE);
                mStoreHouseLists = (String[]) msg.obj;
                mAdapter.setDataList(mStoreHouseLists);
                mAdapter.notifyDataSetChanged();
                break;
            }            
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_house_select_activity);
        
        Intent intent = getIntent();
        mProjectId = intent.getIntExtra(PROJECT_ID, 0);
        
        mEmptyView = (TextView) findViewById(R.id.empty_text);
        ((TextView) findViewById(R.id.edit_title))
                .setText(getString(R.string.select_store_house));
        
        mListView = (ListView) findViewById(R.id.listView1);
        mAdapter = new SimpleListViewAdapter(this, mStoreHouseLists);
        mListView.setAdapter(mAdapter);
        
        initButtons();
        initNewStoreHouseDialog();
        loadData();
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_left:
                setResult();
                break;
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_new:
                mStorehouseDialog.show();
                break;
            }
        }
    };
    
    private void initButtons() {
        mConfirmButton = (Button) findViewById(R.id.btn_left);
        mConfirmButton.setText(R.string.confirm);
        mConfirmButton.setOnClickListener(mClickListener);
        mCancelButton = (ImageView) findViewById(R.id.btn_close);     
        mCancelButton.setOnClickListener(mClickListener);
        mNewStoreHouse = (Button) findViewById(R.id.btn_new);
        mNewStoreHouse.setOnClickListener(mClickListener);
    }
    
    private void loadData() {
        if (mProjectId == 0) return;
        
        mProject = ProjectCache
                .findProjectById(mProjectId);
        if (mProject != null) {
            String store_houses = mProject.getStorehouse();
            Message msg = Message.obtain();
            if (store_houses != null && !store_houses.equals("")) {
                msg.what = DATA_LOADED;
                msg.obj = store_houses.split(",");
            } else {
                msg.what = DATA_EMPTY;
            }
            mHandler.sendMessage(msg);
        }
    }
    
    private void setResult() {
        if (mAdapter.getSelectedItem().equals("")) {
            Toast.makeText(StoreHouseSelectActivity.this,
                    getString(R.string.select_store_house), 
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESULT_KEY, mAdapter.getSelectedItem());
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    private void initNewStoreHouseDialog() {
        mStorehouseDialog = new Dialog(this, R.style.MyDialogStyle);
        mStorehouseDialog.setContentView(R.layout.project_storehouse_dialog);
        mStorehouseDialog.setCanceledOnTouchOutside(false);
        mStorehouseTitle = (TextView) mStorehouseDialog.findViewById(R.id.edit_title);
        mStorehouseTitle.setText(R.string.project_storehouse_add);
        mStorehouse = (EditText) mStorehouseDialog.findViewById(R.id.storehouse);
        mStorehouseOK = (Button) mStorehouseDialog.findViewById(R.id.ok);
        mStorehouseCancel = (ImageView) mStorehouseDialog.findViewById(R.id.btn_close);
        
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok:
                        String new_storehouse = mStorehouse.getText().toString();
                        if (new_storehouse.equals("")) {
                            return;
                        }
                        if (mProject != null) {
                            updateProject(mProject);
                        } else {
                            Project project = new Project();
                            project.setProject_id(mProjectId);
                            getProject(project);
                        }
                        mStorehouseDialog.dismiss();
                        break;
                    case R.id.btn_close:
                        mStorehouseDialog.dismiss();
                        break;
                }
            }            
        };
        mStorehouseOK.setOnClickListener(listener);
        mStorehouseCancel.setOnClickListener(listener);
    }
    
    private void getProject(Project project) {
        showLoadingProgress("Get a Project...");
        RemoteProjectService.getInstance().getProject(new DataManagerInterface() {
            
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissLoadingProgress();
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null
                        && !status.getMessage().equals("")) {
                    UtilTools.showToast(StoreHouseSelectActivity.this, status.getMessage());
                }
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    mProject = (Project)list.get(0);
                    updateProject(mProject);
                }                
            }
        }, project);
    }
    
    private void updateProject(Project project) {
        mProject.setStorehouse(mStorehouse.getText().toString());
        final Project updateProject = (Project) project.clone();
        showLoadingProgress("Update a Project...");
        RemoteProjectService.getInstance().updateProject(
                new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(
                            ResultStatus status, List<?> list) {
                        dismissLoadingProgress();
                        if (status.getCode() != AnalysisManager.SUCCESS_DB_UPDATE
                                && status.getMessage() != null
                                && !status.getMessage().equals("")) {
                            UtilTools.showToast(StoreHouseSelectActivity.this, status.getMessage());
                        }
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                            ProjectCache.updateProject(mProject);
                            String store_houses = mProject.getStorehouse();
                            Message msg = Message.obtain();
                            if (store_houses != null && !store_houses.equals("")) {                        
                                msg.what = DATA_LOADED;
                                msg.obj = store_houses.split(",");                        
                            }
                            mHandler.sendMessage(msg);
                        }
                    }
                    
                }, updateProject);
    }
    
    private void showLoadingProgress(String text) {
        dismissLoadingProgress();
        mProgressDialog = UtilTools.showProgressDialog(StoreHouseSelectActivity.this, true, false);
    }

    private void dismissLoadingProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
