package com.pm360.cepm360.app.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.LineProgressBar;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.io.File;
import java.util.List;

public class ApplicationUpdateActivity extends Activity {

    private TextView mTitle;
    private TextView mVersionDescription;
    private LineProgressBar mDownloadProgress;
    private Button mCancel, mUpdate;
    private String mVersionName;
    private String mRemotePath;
    
    private AsyncTask<Object, Object, Object> mDownloadAsyncTask;
    
    private static final int DOWNLOAD_COMPLETED = 100;
    private static final String APP_FILE_PATH = Environment
            .getExternalStorageDirectory() + "/CEPM360/CEPM360.apk";

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DOWNLOAD_COMPLETED:
                updatePackage();
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_update_activity);


        Intent intent = getIntent();
        mVersionName = intent.getStringExtra("name");
        mRemotePath = intent.getStringExtra("remote_path");
        String description = intent.getStringExtra("description");

        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(getResources().getString(R.string.new_version_found)
                + " " + mVersionName);
        mVersionDescription = (TextView) findViewById(R.id.app_version_description);
        mVersionDescription.setText(description);
        mCancel = (Button) findViewById(R.id.btn_cancel);
        mUpdate = (Button) findViewById(R.id.btn_update);
        mCancel.setOnClickListener(mListener);
        mUpdate.setOnClickListener(mListener);
        mDownloadProgress = (LineProgressBar) findViewById(R.id.progress_bar);
        mDownloadProgress.setRoundEdge(true);
    }

    View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_cancel:
                if (mDownloadAsyncTask != null
                        && mDownloadAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                    mDownloadAsyncTask.cancel(true);
                }
                finish();
                break;
            case R.id.btn_update:
                downloadPackage();
                mUpdate.setClickable(false);
                break;
            default:
                break;
            }
        }
    };

    private void downloadPackage() {
        File oldAPK = new File(APP_FILE_PATH);
        if (oldAPK.exists()) {
            oldAPK.delete();
        }
        Files bean = new Files();
        bean.setAppUpdateFlag(1);
        bean.setFile_path(mRemotePath);

        mDownloadAsyncTask = RemoteDocumentsService.getInstance().downloadFile(
                new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status,
                            List<?> list) {
                        if (status.getCode() == AnalysisManager.SUCCESS_DOWNLOAD) {
                            mHandler.sendEmptyMessage(DOWNLOAD_COMPLETED);
                        }
                    }

                }, mDownloadProgress, bean, new File(APP_FILE_PATH));
    }

    private void updatePackage() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        intent.setDataAndType(Uri.fromFile(new File(APP_FILE_PATH)),
                "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }
}
