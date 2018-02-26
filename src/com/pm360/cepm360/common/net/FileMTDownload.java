package com.pm360.cepm360.common.net;

import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.services.common.AsyncTaskPM360;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * 多线程下载文件
 * @author yuanlu
 *
 */
public class FileMTDownload {
	
	// 每个线程下载下载最大字节数
	private static final long SIZE_PER_THREAD = 5 * 1024 * 1024;
	
    /** 远程文件URL路径 */
    private String mPath;
    
    /** 目标文件 */
    private String mLocalFilePath;
    
    /** 线程数量*/
    private int mThreadNum;
    
    /** 线程数组 */
    private DownThread[] mThreadArray;
    
    /** 文件大小 */
    private long mFileSize;
    
    // 用于进度回调
    private static AsyncTaskPM360 mAsyncTaskPM360;
    private static CountDownLatch mCountDownLatch;
    private int mStatus = AnalysisManager.SUCCESS_DOWNLOAD;
    
    private HttpClient mHttpClient;
    private HttpGet mHttpGet;
    private HttpEntity mHttpEntity;
    private HttpResponse mHttpResponse;
    
    /**
     * 设置回调对象
     * @param asyncTaskPM360
     */
    public static void setAsyncTaskPM360(AsyncTaskPM360 asyncTaskPM360) {
		mAsyncTaskPM360 = asyncTaskPM360;
	}
  
    /**
     * 构造函数
     * @param httpClient 文件上上传的HTTP客户端
     * @param path
     * @param localFilePath
     * @param threadNum
     */
    public FileMTDownload(HttpClient httpClient, String path, String localFilePath) {
    	mHttpClient = httpClient;
        mPath = path;
        mLocalFilePath = localFilePath;
    }
  
    /**
     * 下载方法
     * @throws Exception
     */
    public int download() throws Exception {
    	
    	// 如果文件已经存在，直接返回
		if(FileUtils.isFileExists(mLocalFilePath)) {
			LogUtil.d("文件已经存在！");
			return AnalysisManager.SUCCESS_FILE_EXIST;
		} else {
			File parentFile = new File(mLocalFilePath).getParentFile();
			FileUtils.createOrExistsFolder(parentFile);
		}
    				
        mHttpGet = new HttpGet(mPath);
        mHttpResponse = mHttpClient.execute(mHttpGet);
        
        // 获取网络状态码
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		
		// 获取网络返回状态码
		if (statusCode != HttpStatus.SC_OK) {
			LogUtil.d("网络连接出错，请检查网络环境!");
			mHttpGet.abort();
			return statusCode;
		}
		
        mHttpEntity = mHttpResponse.getEntity();
        
        // 获取文件大小
        mFileSize = mHttpEntity.getContentLength();
        
        mThreadNum = (int) ((mFileSize % SIZE_PER_THREAD) == 0 
        		? mFileSize / SIZE_PER_THREAD : mFileSize / SIZE_PER_THREAD + 1);
        mThreadArray = new DownThread[mThreadNum];
        mCountDownLatch = new CountDownLatch(mThreadNum);
        
        // 计算每个线程下载部分的大小
        long currentPartSize = (mFileSize % mThreadNum) == 0
        		? mFileSize / mThreadNum : mFileSize / mThreadNum + 1;
        
        LogUtil.e("mFileSize = " + mFileSize + " currentPartSize = " + currentPartSize);
        
        RandomAccessFile file = new RandomAccessFile(mLocalFilePath, "rw");
        
        /* 设置文件长度 */
        file.setLength(mFileSize);
        file.close();
        
        for (int i = 0; i < mThreadNum; i++) {
        	
            // 下载的偏移
            long startPos = i * currentPartSize;
            RandomAccessFile currentPart = new RandomAccessFile(mLocalFilePath, "rw");
            
            // 首先移动读指针到指定偏移
            currentPart.seek(startPos);
            
            // 构造下载线程实例，开始下载
            mThreadArray[i] = new DownThread(startPos, 
            		currentPartSize, currentPart, "thread" + (i + 1));
            mThreadArray[i].start();
        }
        
        // 主线程等待子线程结束
        mCountDownLatch.await();
        
        // 如果下载失败，删除本地文件
        if (mStatus != AnalysisManager.SUCCESS_DOWNLOAD) {
        	FileUtils.deleteFile(mLocalFilePath);
        }
        
        return mStatus;
    }
  
    /**
     * 获取当前时间的下载进度
     */ 
    public int getCompleteRate() {
    	
        /* 计算完成进度 */
        long sumSize = 0;
        for (int i = 0; i < mThreadNum; i++) {
            sumSize += mThreadArray[i].mLength;
        }
        
        return (int) (sumSize * 100 / mFileSize);
    }
  
    /**
     * 下载线程
     * @author yuanlu
     *
     */
    private class DownThread extends Thread {
    	
        /** 起始位置 */
        private long  mStartPos;
        
        /** 读取大小**/
        private long mCurrentPartSize;
        
        /** 要下载到的文件*/
        private RandomAccessFile mCurrentPart;
        
        /** 实际写入的长度 */
        public long mLength;
        
        private String mThreadName;
        
        // 请求服务器相关对象
        private HttpGet httpGet;
        private HttpResponse httpResponse;
        private HttpEntity httpEntity;
  
        /**
         * 构造函数
         * @param startPos
         * @param currentPartSize
         * @param currentPart
         */
        public DownThread(long startPos, long currentPartSize,
        						RandomAccessFile currentPart, String threadName) {
            mStartPos = startPos;
            mCurrentPartSize = currentPartSize;
            mCurrentPart = currentPart;
            mThreadName = threadName;
        }
        
        /**
         * 打印线程信息
         */
        public void printThread() {
        	LogUtil.d("mThreadName = " + mThreadName + " mLength =  " + mLength);
        }
  
        @Override
        public void run() {
            try {
                httpGet = new HttpGet(mPath);
                long endPos = mStartPos + mCurrentPartSize - 1;
                httpGet.setHeader("Range", "bytes=" + mStartPos + "-" + endPos);
                httpResponse = mHttpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                
                // 读缓存
                byte[] buffer = new byte[1024];
                int hasRead = 0;
                
                // 读取流并写入文件
                InputStream inputStream = httpEntity.getContent();
                while ((hasRead = inputStream.read(buffer)) != -1) {
                	mCurrentPart.write(buffer, 0, hasRead);
                	mLength += hasRead;
                    
                    // 更新进度
                    if (mAsyncTaskPM360 != null) {
                    	mAsyncTaskPM360.notifyProgress(getCompleteRate());
                    }
                }
                
                printThread();
                
                /* 线程工作完成归还资源 */
                httpEntity.consumeContent();
                mCurrentPart.close();
                inputStream.close();
                
                // 下载完本线程部分，将计数减一
                mCountDownLatch.countDown();
            } catch (Exception e) {
            	mStatus = AnalysisManager.EXCEPTION_DOWNLOAD;
                e.printStackTrace();
            } finally {
            	httpGet.abort();
            }
        }
    }
}  
