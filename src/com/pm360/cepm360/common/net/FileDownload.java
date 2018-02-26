package com.pm360.cepm360.common.net;

import android.util.Log;

import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.json.JsonUtils;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.services.common.AsyncTaskPM360;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * 文件下载
 * @author yuanlu
 *
 */
public class FileDownload {
	private static final String TAG = "FileDownload";
	private static final boolean DEBUG = true;
	
	private int progress;
	private static AsyncTaskPM360 mAsyncTaskPM360;
	private HttpClient mHttpClient;
	private HttpPost mHttpPost;
	private HttpResponse mHttpResp;
	private HttpEntity mHttpEntity;

	public int getProgress() {
		return progress;
	}
	
	public static void setAsyncTaskPM360(AsyncTaskPM360 asyncTaskPM360) {
		mAsyncTaskPM360 = asyncTaskPM360;
	}

	/**
	 * 无参构造函数
	 */
	public FileDownload() {
		progress = 0;
		mHttpClient = new DefaultHttpClient();
	}
	
	/**
	 * 使用外部的HttpClient对象
	 * @param httpClient
	 */
	public FileDownload(HttpClient httpClient) {
		progress = 0;
		mHttpClient = httpClient;
	}
	
	/** 
	 * 将输入流写入文件
	 * @param fileName
	 * @param is
	 * @param fileSize
	 * @return
	 */
	public boolean writeToFileFromInputStream(String fileName, InputStream is, long fileSize) {
		// 写入文件前的检查
		if (fileName == null 
				|| (fileName = fileName.trim()).equals("") 
				|| is == null 
				|| fileSize <= 0) {
			return false;
		}
		return writeToFileFromInputStream(new File(fileName), is, fileSize);
	}


	public boolean writeToFileFromInputStream(File file, InputStream is, long fileSize) {
		// 获取文件的目录
		File parentFile = file.getParentFile();
		// 判断目录是否存在，如果不存在创建之
		if (!FileUtils.createOrExistsFolder(parentFile)) {						
				return false;
		}

		boolean result = false;
		OutputStream os = null;
		
		try {
			os = new FileOutputStream(file);
			byte buffer[] = new byte[4*1024];
			long inner_progress, downloadFileSize = 0;

			int length = 0;
			while ((length = is.read(buffer)) != -1) {
				os.write(buffer, 0, length);
				downloadFileSize += length;
				
				// 计算写入百分比
				inner_progress = downloadFileSize * 100/fileSize;
				if (inner_progress >= 100) {
					inner_progress = 100;
				}
				progress = (int) inner_progress;
				mAsyncTaskPM360.notifyProgress(progress);
			}
			
			os.flush();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				os.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * 带表单的文件下载
	 * @param urlStr
	 * @param parameters
	 * @param fileName
	 * @return
	 */
	public int downFile(String urlStr, Map<String, Object> parameters, String localFilePath) {  
		if (DEBUG) Log.d(TAG, "Enter downFile, "
						+ "localFilePath = " + localFilePath
						+ "\nparameters = " + parameters);
		String jsonString = JsonUtils.objectToJson(parameters.get("p0"));
		try {
			
			// 如果文件已经存在，直接返回
			if(FileUtils.isFileExists(localFilePath)) {
				if (DEBUG) Log.d(TAG, "The download file has exists!");
				return AnalysisManager.SUCCESS_FILE_EXIST;
			}	
			
			// 构建方法
			mHttpPost = new HttpPost(urlStr);
			// 构建发送实体
			mHttpEntity = new StringEntity(jsonString, HTTP.UTF_8);
			// 设置发送实体
			mHttpPost.setEntity(mHttpEntity);
			// 执行网络请求
			mHttpResp = mHttpClient.execute(mHttpPost);
			// 获取网络状态码
			int statusCode = mHttpResp.getStatusLine().getStatusCode();
			// 获取网络返回状态码
			if (statusCode != HttpStatus.SC_OK) {
				if (DEBUG) Log.d(TAG, "Connection occured error,"
										+ " Please check network!");
				return statusCode;
			}
			// 获取返回实体输入流
			InputStream inputStream = mHttpResp.getEntity().getContent();
			// 将文件流写入文件
			if(!FileUtils.writeToFileFromInputStream(localFilePath, inputStream)) {
				if (DEBUG) Log.d(TAG, "Failed to Write"
										+ " inputStream to the local file!");
				return AnalysisManager.EXCEPTION_DOWNLOAD;
			}				
		} catch(Exception e) {
			e.printStackTrace();
			return AnalysisManager.EXCEPTION_DOWNLOAD;
		}
		return AnalysisManager.SUCCESS_DOWNLOAD;
	}
	
	/**
	 * 带表单的文件下载
	 * @param urlStr
	 * @param parameters
	 * @param fileName
	 * @return
	 */
	public int downFileWithProgress(String urlStr, Map<String, Object> parameters, String localFilePath) {  
		if (DEBUG) Log.d(TAG, "Enter downFileWithProgress, "
						+ "localFilePath = " + localFilePath
						+ "\nparameters = " + parameters);
		String jsonString = JsonUtils.objectToJson(parameters.get("p0"));
		try {
			
			// 如果文件已经存在，直接返回
			if(FileUtils.isFileExists(localFilePath)) {
				if (DEBUG) Log.d(TAG, "The download file has exists!");
				return AnalysisManager.SUCCESS_FILE_EXIST;
			}
			// 构建方法
			mHttpPost = new HttpPost(urlStr);
			// 构建发送实体
			mHttpEntity = new StringEntity(jsonString, HTTP.UTF_8);
			// 设置发送实体
			mHttpPost.setEntity(mHttpEntity);
			// 执行网络请求
			mHttpResp = mHttpClient.execute(mHttpPost);
			// 获取网络状态码
			int statusCode = mHttpResp.getStatusLine().getStatusCode();
			// 获取网络返回状态码
			if (statusCode != HttpStatus.SC_OK) {
				if (DEBUG) Log.d(TAG, "Connection occured error,"
										+ " Please check network!");
				mHttpPost.abort();
				return statusCode;
			}
			// 获取返回实体输入流
			mHttpEntity = mHttpResp.getEntity();
			// 将文件流写入文件
			if(!writeToFileFromInputStream(localFilePath,
									mHttpEntity.getContent(),
									mHttpEntity.getContentLength())) {
				if (DEBUG) Log.d(TAG, "Failed to Write"
										+ " inputStream to the local file!");
				return AnalysisManager.EXCEPTION_DOWNLOAD;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return AnalysisManager.EXCEPTION_DOWNLOAD;
		}
		return AnalysisManager.SUCCESS_DOWNLOAD;
	}
}
