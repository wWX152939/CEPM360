
package com.pm360.cepm360.services.common;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.view.LineProgressBar;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.json.AnalysisManager.AnalysisResult;
import com.pm360.cepm360.common.json.GsonConnection;
import com.pm360.cepm360.common.json.JsonConnection;
import com.pm360.cepm360.common.net.FileMTDownload;
import com.pm360.cepm360.common.net.FileUpload;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

public class AsyncTaskPM360 extends AsyncTask<Object, Object, Object> {
    // 调试使用
    private static final String TAG = "AsyncTaskPM360";
    // 调试开关
    private static final boolean DEBUG = true;
    // UI回调接口
    private final DataManagerInterface manager;
    // 对象的类型，用于json解析
    private Type mType;
    // Operation code, QUERY, ADD, DELETE, MODIFY, TEST
    private Operation mOperation;
    // 进度条控件
    private Object mProgress;
    // 进度条对话框
    private ProgressDialog mProgressDialog;
    // 进度条
    private ProgressBar mProgressBar;
    
    // 进度条
    private LineProgressBar mLineProgressBar;

    public AsyncTaskPM360(DataManagerInterface manager,
            Operation operation) {
        this(manager, null, operation);
    }

    /**
     * AsyncTaskPM360全参构造函数
     * 
     * @param manager
     * @param mClass 返回对象类型
     * @param isList 返回是否为列表
     * @param map 对象里的列表类型定义
     * @param operation 增/删/改/查 标识
     */
    public AsyncTaskPM360(DataManagerInterface manager,
            Type type,
            Operation operation) {
        super();
        this.manager = manager;
        this.mType = type;
        this.mOperation = operation;
    }

    /**
     * 设置进度对话框
     * 
     * @param progressDialog
     */
    public AsyncTaskPM360 setProgressObject(Object progress) {
        mProgress = progress;
        return this;
    }

    /*
     * 执行任务前的准备工作
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        switch (mOperation) {
            case DOWNLOAD:
            case UPLOAD:
                if (mProgress != null) {
                    if (mProgress instanceof ProgressDialog) {
                        mProgressDialog = (ProgressDialog) mProgress;
                        mProgressDialog.setMax(100);
                        mProgressDialog.show();
                    } else if (mProgress instanceof ProgressBar) {
                        mProgressBar = (ProgressBar) mProgress;
                        mProgressBar.setMax(100);
                        mProgressBar.setVisibility(View.VISIBLE);
                    } else if (mProgress instanceof LineProgressBar) {
                        mLineProgressBar = (LineProgressBar) mProgress;
                        mLineProgressBar.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
                break;
        }

    }

    /*
     * 进度刷新操作
     */
    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (mProgressDialog != null) {
            mProgressDialog.setProgress((int) values[0]);
        } else if (mProgressBar != null) {
            mProgressBar.setProgress((int) values[0]);
        } else if (mLineProgressBar != null) {
            mLineProgressBar.setProgress((int) values[0]);
        }
        
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object doInBackground(Object... parameters) {
        if (DEBUG)
            Log.d(TAG, "Enter doInbackground!");
        // 网络请求和解析管理抽象类
        AnalysisManager analysisManager = null;
        if (AnalysisManager.GSON) {
            analysisManager = new GsonConnection();
        } else {
            analysisManager = new JsonConnection();
        }
        AnalysisResult result = null;
        try {
            switch (mOperation) {
                case LOGIN:
                case QUERY:
                case ADD:
                case MODIFY:
                case DELETE:
                case PUBLISH:
                    result = analysisManager.getData((Map<String, Object>) parameters[0],
                            mType,
                            mOperation);
                    break;
                case UPLOAD:
                    if (mProgress == null) {
                        // 不带进度条的文件上传
                        	analysisManager.uploadFile( manager, 
					                        			(Map<String, Object>) parameters[0], 
					                        			(File) parameters[1], 
					                        			false);
                    } else {
                        // 带进度条的文件上传
                        FileUpload.setAsyncTaskPM360(this);
	                    analysisManager.uploadFile( manager, 
	                    							(Map<String, Object>) parameters[0], 
	                    							(File) parameters[1], 
	                    							true);
                    }
                    break;
                case DOWNLOAD:
                    if (mProgress == null) {
                    	
                        // 不带进度条的文件下载
                        result = analysisManager.downloadFile(
                        					(Map<String, Object>) parameters[0],
                        					(String) ((File) parameters[1]).getAbsolutePath(),
                        					false);
                    } else {
                        FileMTDownload.setAsyncTaskPM360(this);
                        
                        // 带进度条的文件下载
                        result = analysisManager.downloadFile(
                                			(Map<String, Object>) parameters[0],
                                			(String) ((File) parameters[1]).getAbsolutePath(),
                                			true);
                    }
                    break;
                default:
                    Log.e(TAG, "Operation Code is incorrect, plase check it!");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if ((mOperation == Operation.DOWNLOAD
                || mOperation == Operation.UPLOAD)
                && mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            
            // 清除下载进度回调对象
            FileMTDownload.setAsyncTaskPM360(null);
        }
        
        if(result!=null){
            AnalysisResult analysisResult = (AnalysisResult)result;
            if (manager != null) {
	            manager.getDataOnResult(analysisResult
	            				.getResultStatus(), analysisResult.getList());
            }
        }
    }

    /**
     * 发布进度，触发onProgressUpdate的执行
     * 
     * @param value
     */
    public void notifyProgress(int value) {
        publishProgress(value);
    }
}
