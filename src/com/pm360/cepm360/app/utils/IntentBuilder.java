package com.pm360.cepm360.app.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.io.File;
import java.util.List;

public class IntentBuilder {
    
    /**
     * 
     * 打开文件，如果文件不存在，则先下载再打开。
     * 
     * @param context
     * @param bean: Files 对象
     * @param downloadPath: 本地下载存放路径
     * 
     */
    public static void viewFile(Context context, Files bean, String downloadPath) {
        viewFile(context, bean, downloadPath, true);
    }
    
    /**
     * 
     * 打开文件，如果文件不存在，则先下载再打开。
     * 
     * @param context
     * @param bean: Files 对象
     * @param downloadDir: 本地下载存放目录
     * 
     */
    public static void view(Context context, Files bean, String downloadDir) {
    	if (!downloadDir.endsWith("/") && !downloadDir.endsWith("\\")) {
    		viewFile(context, bean, downloadDir + "/" + bean.getFile_name(), true);
    	} else {
    		viewFile(context, bean, downloadDir + bean.getFile_name(), true);
    	}
    }
    
    /**
     * 
     * 打开文件，如果文件不存在，则先下载再打开。
     * 
     * @param context
     * @param bean: Files 对象
     * @param downloadPath: 本地下载存放路径
     * @param showProgress: 是否弹出下载进度条
     * 
     */
    public static void viewFile(final Context context, final Files bean,
            final String downloadPath, boolean showProgress) {
        File file = new File(downloadPath);
        if (file.exists()) {
            viewFile(context, downloadPath);
        } else {
            download(context, new DataManagerInterface() {
                @Override
                public void getDataOnResult(ResultStatus status,
                        List<?> list) {
                    if (status.getCode() == AnalysisManager.SUCCESS_DOWNLOAD) {
                        viewFile(context, downloadPath);
                    } else {
                        Toast.makeText(
                                context,
                                context.getResources()
                                        .getString(R.string.document_download_fail),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }, bean, downloadPath, showProgress);
        }
    }

    /**
     * @param context
     * @param filePath: 文件路径
     * 
     */
    public static void viewFile(final Context context, final String filePath) {
        String type = getMimeType(filePath);

        if (!TextUtils.isEmpty(type) && !TextUtils.equals(type, "*/*")) {
            /* 设置intent的file与MimeType */
            startActivity(context, Uri.fromFile(new File(filePath)), type);
        } else {
            // unknown MimeType
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle(R.string.dialog_select_type);

            CharSequence[] menuItemArray = new CharSequence[] {
                    context.getString(R.string.dialog_type_text),
                    context.getString(R.string.dialog_type_audio),
                    context.getString(R.string.dialog_type_video),
                    context.getString(R.string.dialog_type_image) };
            dialogBuilder.setItems(menuItemArray,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selectType = "*/*";
                            switch (which) {
                            case 0:
                                selectType = "text/plain";
                                break;
                            case 1:
                                selectType = "audio/*";
                                break;
                            case 2:
                                selectType = "video/*";
                                break;
                            case 3:
                                selectType = "image/*";
                                break;
                            }
                            startActivity(context, Uri.fromFile(new File(filePath)), selectType);
                        }
                    });
            dialogBuilder.show();
        }
    }
    
    private static void startActivity(Context context, Uri uri, String type) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    context,
                    context.getResources().getString(
                            R.string.application_no_found), Toast.LENGTH_SHORT)
                    .show();
        }
    }
    
    public static void download(Context context, 
    		DataManagerInterface manager, Files bean, String downloadPath, boolean showProgress) {

        bean.setTenant_id(UserCache.getCurrentUser()
                .getTenant_id());

        if (showProgress) {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(context
            		.getResources().getString(R.string.document_downloading));
            progressDialog.setCancelable(true);
            RemoteDocumentsService.getInstance()
            .downloadFile(manager, progressDialog, bean, new File(downloadPath));
        } else {
            RemoteDocumentsService.getInstance()
            .downloadFile(manager, null, bean, new File(downloadPath));
        }    
    }

    @SuppressLint("DefaultLocale") 
    private static String getMimeType(String filePath) {
        int dotPosition = filePath.lastIndexOf('.');
        if (dotPosition == -1)
            return "*/*";

        String ext = filePath.substring(dotPosition + 1, filePath.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(ext);

        return mimeType != null ? mimeType : "*/*";
    }
}
