package com.pm360.cepm360.common.net;

import android.util.Log;

import com.pm360.cepm360.services.common.AsyncTaskPM360;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class FileUpload {
	private static final String TAG = "FileUpload";

	private static final boolean DEBUG = true;
	
	private HttpEntity mHttpEntity;
	private HttpPost mHttpPost;
	private long totalSize;
	private static AsyncTaskPM360 mAsyncTaskPM360;
	
	public FileUpload() {

	}
	
	/**
	 * 通过文件路径上传文件
	 * @param url
	 * @param filePath
	 * @return
	 */
	public Object upload(String url, String filePath){		
		return upload(url, new File(filePath));
	}
	
	/**
	 * 传输文件
	 * @param url
	 * @param file
	 * @return
	 */
	public Object upload(String url, File file) {			
		return upload(url, null, file);
	}
	
	/**
	 * 带进度的传输文件
	 * @param url
	 * @param file
	 * @return
	 */
	public Object uploadWithProgress(String url, File file) {			
		return uploadWithProgress(url, null, file);
	}
	
	/**
	 * 设置异步任务
	 * @param task
	 */
	public static void setAsyncTaskPM360(AsyncTaskPM360 task) {
		mAsyncTaskPM360 = task;
	}
	/**
	 * 上传表单数据和文件
	 * @param url
	 * @param parameters
	 * @param file
	 * @return
	 */
	public Object upload(String url, Map<String, Object> parameters, File file) {
		if (DEBUG) Log.d(TAG, "Enter upload, paramters = " + parameters 
								+ "\nfileName = " + file.getAbsolutePath());
		String boundary = "-------------" + System.currentTimeMillis();
		// 首先创建一个HttpPost对象
		mHttpPost = new HttpPost(url);
		// 设置内容类型
		mHttpPost.addHeader("Content-Type", "multipart/form-data; "
								+ "boundary=" + boundary);
		
		// 构建一个发送实体，并将文件内容add进去
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		// 设置Entity的编码方式
		builder.setCharset(Charset.forName(HTTP.UTF_8))
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.setBoundary(boundary);	// 设置边界，否则不能通过
		
		// 迭代添加NameValuePair
		if (parameters != null) {
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				mHttpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		// 添加文件实体
		builder.addPart(file.getAbsolutePath(), new FileBody(file));
		mHttpPost.setEntity(builder.build());
		// 调用统一发送接口
		return HttpClientTransmit.getInstance().httpConnection(mHttpPost);
	}
	
	/**
	 * 上传表单数据和文件
	 * @param url
	 * @param parameters
	 * @param file
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Object uploadWithProgress(String url, Map<String, Object> parameters, File file) {
		if (DEBUG) Log.d(TAG, "Enter uploadWithProgress, paramters = " + parameters 
								+ "\nfileName = " + file.getAbsolutePath());
		// 首先创建一个HttpPost对象
		mHttpPost = new HttpPost(url);
		// 设置内容类型
		mHttpPost.addHeader("Content-Type", "multipart/form-data; "
								+ "boundary=" + CustomMultipartEntity.mBoundary);
		
		CustomMultipartEntity custom = new CustomMultipartEntity(
				new CustomMultipartEntity.ProgressListener() {
					@Override
					public void transferred(long num) {
						mAsyncTaskPM360.notifyProgress((int) ((num / (float) totalSize) * 100));
					}
				});
		
		// 迭代添加NameValuePair
		if (parameters != null) {
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				mHttpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		
		// 添加文件实体
		custom.addPart(file.getAbsolutePath(), new FileBody(file));
		
		totalSize = custom.getContentLength();
		mHttpPost.setEntity(custom);
		// 调用统一发送接口
		return HttpClientTransmit.getInstance().httpConnection(mHttpPost);
	}
	
	/**
	 * 多文件上传
	 * @param url 上传路径
	 * @param files 发送文件数组
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Object uploads(String url, File[] files) {
		StringBuilder sb = new StringBuilder("");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setCharset(Charset.forName("uft-8"))
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		FileType fileType = new FileType();
		int count = 0;
		for (File file : files) {
			sb.append(fileType.getTypeName(file.getName()));
			// 添加文件实体
		    builder.addBinaryBody("file"+count, file);
		    count++;
		}
		
		// 添加文件类型字符串
		builder.addTextBody("fileTypes", sb.toString());
		// 首先创建一个HttpPost对象
		HttpPost mHttpPost = new HttpPost(url);
		// 构建发送实体
		mHttpEntity = builder.build();
		mHttpPost.setEntity(mHttpEntity);

		return HttpClientTransmit.getInstance().httpConnection(mHttpPost);
	}
	
	/**
	 * 文件类型鉴别类
	 * @author yuanlu
	 *
	 */
	public static class FileType {
		
		public String getTypeName(String fileName) {
			String fileStr = fileName.substring(fileName.indexOf(".") + 1,
											fileName.length());		
			if(fileStr.indexOf(".") >= 0){
				fileName = fileStr;
				FileType ft = new FileType();
				fileName = ft.getTypeName(fileName);
			}
			
			return fileName.substring(fileName.indexOf("."),
								fileName.length());
		}
	}
}
