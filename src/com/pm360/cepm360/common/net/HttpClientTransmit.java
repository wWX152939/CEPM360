package com.pm360.cepm360.common.net;

import android.util.Log;

import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.json.JsonUtils;
import com.pm360.cepm360.common.util.CipherUtils;
import com.pm360.cepm360.common.util.LogUtil;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientTransmit {
	private static final String TAG = "HttpClientTransmit";
	private static final boolean DEBUG = true;
	private static String serverIp = "16v59y6243.imwork.net";
//	private static String serverIp = "121.43.200.112";
//	private static String serverIp = "192.168.18.100";
	private static String urlbase = "http://"+ serverIp + ":3090/cepm360/";
	private static String urlAddress = urlbase + "service";
	private static String urlUpload = urlbase + "upload";
	private static String urlDownload = urlbase + "download";

	private static HttpClientTransmit gHttpClientTransmit = null;
	// 编码方式 
	private static final String mEncoding = HTTP.UTF_8;
	// 全局唯一HttpClient对象，自动管理Cookie
	private HttpClient gHttpClient;
	
	private HttpClientTransmit() {
		// 创建和初始化Http参数
        HttpParams params = new BasicHttpParams();
        
        // 设置基本参数，HTTP版本、编码方式，认证请求
        // （先发头认证，认证通过再发数据，这里一般用不上，文件上传时可用）
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
        HttpProtocolParams.setContentCharset(params, mEncoding);  
        HttpProtocolParams.setUseExpectContinue(params, false); 
        
        //从连接池中取连接的超时时间1s，设置连接超时2s，设置数据读取超时10s
        ConnManagerParams.setTimeout(params, 2000);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
			
			@Override
			public int getMaxForRoute(HttpRoute route) {
				return 256;
			}
		});
        ConnManagerParams.setMaxTotalConnections(params, 1024);
        HttpConnectionParams.setConnectionTimeout(params, 2000);
//        HttpConnectionParams.setSoTimeout(params, 10000);
        
        //设置HttpClient支持HTTp和HTTPS两种模式  
        SchemeRegistry schReg = new SchemeRegistry();  
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        
        //使用线程安全的连接管理来创建HttpClient
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		gHttpClient = new DefaultHttpClient(conMgr, params);
	}
	
	/**
	 * 重新设置服务器IP地址
	 * @param ip
	 */
	public static void resetServerIp(String ip) {
		serverIp = ip;
		urlbase = "http://"+ serverIp + ":3090/cepm360/";
		urlAddress = urlbase + "service";
		urlUpload = urlbase + "upload";
		urlDownload = urlbase + "download";
	}
	
	/**
	 * 获取全局HttpClientTransmit对象，这样可以保证只有一个类对象被创建，提高系统性能
	 * @return
	 */
	public static synchronized HttpClientTransmit getInstance() {
		if (gHttpClientTransmit == null) {
			gHttpClientTransmit = new HttpClientTransmit();
		}
		return gHttpClientTransmit;
	}
	
	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gHttpClientTransmit = null;
	}
	
	/**
	 * 获取全局唯一HttpClient实例
	 * @return
	 */
	public HttpClient getHttpClient() {
		return gHttpClient;
	}
	
	/**
	 * 获取服务器IP地址
	 * @return
	 */
	public static String getServerIp() {
		return serverIp;
	}
	
	/**
	 * 设置服务器IP地址
	 * @return
	 */
	public static void setServerIp(String ip) {
		serverIp = ip;
	}
	
	/**
	 * post的接口 
	 * @params obj any object subclass
	 * @return json string 
	 */
	public Object post(List<NameValuePair> parameters) {
		return postFromHttpClient(parameters);
	}
	
	/**
	 * http post方法
	 * @params parameters
	 * @return json string
	 */
	public Object postFromHttpClient(List<NameValuePair> parameters){
		String path = urlAddress;
		HttpPost httpPost = new HttpPost(path);

		HttpEntity httpEntity = null;
		try {
			// 构建实体
			httpEntity = new UrlEncodedFormEntity(parameters, mEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		httpPost.setEntity(httpEntity);
		// 发起http连接并返回
		return httpConnection(httpPost);
	}
	
	/**
	 * Http Post 方法接口
	 * @params obj any object subclass
	 * @return json string 
	 */
	public Object post(Map<String, Object> params) {
		return postFromHttpClient(params);
	}
	
	/**
	 * 通过HttpClient post传输map参数
	 * @param params
	 * @return
	 * @throws JSONException
	 */
	public Object postFromHttpClient(Map<String, Object> params) {
		if (DEBUG) Log.d(TAG, "Enter postFromHttpClient map!");
		if (DEBUG) Log.d(TAG, "params = " + params);
		
		// 这里需要边界处理，否则不能获取到正确的数据
		String boundary = "-------------" + System.currentTimeMillis();
		HttpPost httpPost = new HttpPost(urlAddress);
		httpPost.addHeader("Content-Type", "multipart/form-data; "
												+ "boundary=" + boundary);
		Log.d(TAG, "before builder");
		// 构建MultipartEntityBuilder对象，添加map到实体
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		// 设置Entity的编码方式
		builder.setCharset(Consts.UTF_8);
		// 设置边界，否则不能通过
		builder.setBoundary(boundary);
		Log.d(TAG, "before for");
		// 循环添加传输的参数
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Log.d(TAG, entry.getKey() + " " + JsonUtils.objectToJson(entry.getValue()));
			builder.addTextBody(entry.getKey(),
								JsonUtils.objectToJson(entry.getValue()));
		}
		Log.d(TAG, "before build");
		// 构建一个HttpEntity对象
		HttpEntity httpEntity = builder.build();
		Log.d(TAG, "after build");
		httpPost.setEntity(httpEntity);
		Log.d(TAG, "setEntity");
		// 发起http连接并返回
		return httpConnection(httpPost);
	}
	
	/**
	 * 将所有参数作为一个json字符串post到服务器
	 * @param params
	 * @return
	 * @throws JSONException
	 */
	public Object post(String params) {
		return postFromHttpClient(params);
	}
	
	/**
	 * 通过HttpClient post传输map参数
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws UnsupportedEncodingException 
	 */
	public Object postFromHttpClient(String params) {
		if (DEBUG) Log.d(TAG, "params = " + params);
		// 发送网络请求
        Key key = null;
        String ciphertext = "";
		try {
			key = CipherUtils.getDESKey(GLOBAL.CIPHERTEXT);
			ciphertext = CipherUtils.encrypt(params, key, "DES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpPost httpPost = new HttpPost(urlAddress);
		// 构建请求实体
		HttpEntity httpEntity = null;
		try {
			httpEntity = new StringEntity(ciphertext, mEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		// 设置请求方法
		httpPost.setEntity(httpEntity);
		// 发起http连接并返回
		return httpConnection(httpPost);
	}
	
	/**
	 * Http请求
	 * @param httpRequestBase
	 * @return
	 */
	public Object httpConnection(HttpRequestBase httpRequestBase) {
		// 参数检查
		if (httpRequestBase == null) {
			Log.e(TAG, "The request method is null!");
			return null;
		}	
		// 网络请求和处理
		try {
			if (DEBUG) Log.d(TAG, "Start request to service!");
			// 执行网络请求
			HttpResponse httpResponse = gHttpClient.execute(httpRequestBase);			
			if (DEBUG) {
				Header[] heads = httpResponse.getAllHeaders();  
				// 打印所有头部信息
				for(Header h : heads){  
					if (DEBUG) Log.d(TAG, h.getName() + ":" + h.getValue());  
				}
			}
			// 获取服务器相应状态码
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			// 服务器正常响应
			if (statusCode == HttpStatus.SC_OK) {
				// 获取相应的实体对象
				HttpEntity httpEntity = httpResponse.getEntity();
				String recvSting = EntityUtils.toString(httpEntity, HTTP.UTF_8);
				// 打印相应的实体，Json字符串和长度
				if (DEBUG)
					Log.d(TAG, "Recieve mJsonStr: " + recvSting);
				// 返回服务器的内容
				return recvSting;
			} else {
				if (DEBUG) Log.d(TAG, "Connection occured error, "
										+ "Please check calling method or network!");
				httpRequestBase.abort();
				// 发送网络异常，将网络状态码返回
				return statusCode;
			} 
		} catch(ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 发生如错误URL异常，执行该返回
		return AnalysisManager.EXCEPTION_RESOURCE_NO_EXIST;
	}
	
	/**
	 * 发送文件数据
	 * @param parameters 表单数据
	 * @param file 文件
	 * @return
	 */
	public Object postUploadFile(int tanent_id, File file) {
		Map<String, Object> tanentMap = new HashMap<String, Object>();
		tanentMap.put("tenant_id", tanent_id);
		return postUploadFile(tanentMap, file);
	}
	
	/**
	 * 带进度的发送文件数据
	 * @param parameters 表单数据
	 * @param file 文件
	 * @return
	 */
	public Object postUploadFileWithProgress(int tanent_id, File file) {
		Map<String, Object> tanentMap = new HashMap<String, Object>();
		tanentMap.put("tenant_id", tanent_id);
		return new FileUpload().uploadWithProgress(urlUpload, tanentMap, file);
	}
	
	/**
	 * 发送表单和文件数据
	 * @param params
	 * @param file
	 * @return
	 */
	public Object postUploadFile(Map<String, Object>params, File file) {
		if (DEBUG) Log.d(TAG, "fileName = " + file.getAbsolutePath() + "params = " + params);
		return new FileUpload().upload(urlUpload, params, file);
	}
	
	/**
	 * 下载文件
	 * @param parameters
	 * @param fileName
	 * @return
	 */
	public int postDownloadFile(Map<String, Object> parameters, String fileName) {
		FileDownload fileDownload = new FileDownload(gHttpClient);
		
		// 通过FileUpload接口实现文件和表单数据的上传
		return fileDownload.downFile(urlDownload, parameters, fileName);
	}
	
	/**
	 * 带进度条的下载文件
	 * @param parameters
	 * @param fileName
	 * @return
	 */
	public int postDownloadFileWithProgress(Map<String, Object> parameters, String fileName) {
		FileDownload fileDownload = new FileDownload(gHttpClient);
		
		// 通过FileUpload接口实现文件和表单数据的上传
		return fileDownload.downFileWithProgress(urlDownload, parameters, fileName);
	}
	
	/**
	 * 带进度的多线程文件下载
	 * @param fileUrl	远程文件路径
	 * @param localFilePath	本地文件路径
	 * @return
	 */
	public int downloadFile(String fileUrl, String localFilePath) {
		String remoteFilePath = urlbase.substring(0, urlbase.length() - 9) + fileUrl;
		return download(remoteFilePath, localFilePath);
	}
	
	/**
	 * 带进度的多线程文件下载
	 * @param fileUrl	远程文件路径
	 * @param localFilePath	本地文件路径
	 * @return
	 */
	public int download(String fileUrl, String localFilePath) {
		String remoteFilePath = fileUrl.replaceAll(" ", "%20");
		LogUtil.d("remoteFilePath = " + remoteFilePath);
		LogUtil.d("localFilePath = " + localFilePath);
		FileMTDownload fileMTDownload = new FileMTDownload(gHttpClient, 
				remoteFilePath, localFilePath);
		
		int result = 0;
		try {
			result = fileMTDownload.download();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * HttpGet获取表单信息
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Object get(Map<String, Object> params) throws UnsupportedEncodingException {
		return getFromHttpClient(params);
	}
	
	/**
	 * HttpGet获取表单信息
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Object getFromHttpClient(Map<String, Object> params)
								throws UnsupportedEncodingException {
		String path = urlAddress;
		StringBuilder paramsBuilder = new StringBuilder(path + "?");
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				paramsBuilder.append(entry.getKey())
							 .append("=")
							 .append(URLEncoder.encode(entry.getValue().toString(), mEncoding))
							 .append("&");
			}
		}
		
		// 删除最后一个&字符
		paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);
		// 构建get请求方法
		HttpGet httpGet = new HttpGet(paramsBuilder.toString());
		// 发起http连接
		return httpConnection(httpGet);
	}
}
