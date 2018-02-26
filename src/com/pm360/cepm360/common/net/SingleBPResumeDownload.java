package com.pm360.cepm360.common.net;

import android.os.AsyncTask;

import com.pm360.cepm360.common.util.FileUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class SingleBPResumeDownload extends AsyncTask<String, Integer, Integer> {
		
	public static final int RESULT_DOWNLOAD_OK = 1;
	public static final int RESULT_DOWNLOAD_FAILED = -1;
	public static final int RESULT_DOWNLOAD_CANCEL = 0;

	private boolean isPause = false;
		  
	private static final int SLEEP_TIME = 500;

	private int curSize = 0; 
	private int length = 0; 
	
	private HttpClient mHttpClient;
	private String urlStr;
	private String mFileName;
	
	public SingleBPResumeDownload(String urlStr, String fileName, HttpClient httpClient) {
		this.urlStr = urlStr;
		this.mHttpClient = httpClient;
	}
	
	@SuppressWarnings("resource")
	@Override
	protected Integer doInBackground(String... params) {

		int result = RESULT_DOWNLOAD_FAILED;
	
		InputStream in = null;
		HttpGet request = null;
		RandomAccessFile raf = null;
	
		try {
			request = new HttpGet(urlStr);
			    
			if(curSize > 0) {
				request.setHeader("RANGE", "bytes=" + String.valueOf(curSize-1) + "-");
			}
			    
			HttpResponse response = mHttpClient.execute(request);
			HttpEntity entity = response.getEntity();
	
			length = (int) entity.getContentLength() + curSize; 
	
			in = entity.getContent();
			
			File file = FileUtils.createOrExistsSDFile(mFileName);
			raf = new RandomAccessFile(file, "rw");
			raf.seek(curSize);
			    
			byte[] buffer = new byte[128];
			int ch = -1;
	
			while ((ch = in.read(buffer)) != -1) {
				curSize += ch;
				raf.write(buffer, 0, ch);
	
				publishProgress((int) ((curSize / (float) length) * 100)); 
	
				while (isPause) {
					Thread.currentThread();
					Thread.sleep(SLEEP_TIME);
			    }
	
				if (isCancelled()) {
					//saveInfo(curSize);
					return RESULT_DOWNLOAD_CANCEL;
				}
			}
	
			entity.consumeContent();
			    
			result = RESULT_DOWNLOAD_OK;
		} catch (Exception e) {
			e.printStackTrace();
		} finally { 
			try {if (in != null) {in.close();}
				if (raf != null) {raf.close();}
				if (request != null) {request.abort();}
		    } catch (Exception e2) {
		    	e2.printStackTrace();
		    }
		}

		return result;
	}

	@Override
	protected void onPreExecute() {
		//info.setText("");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		//progressBar.setProgress(values[0]);
		//progress.setText("onProgressUpdate: " + values[0] + " %");
	}

	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
			case RESULT_DOWNLOAD_OK:
				//info.setText("");
				break;

			case RESULT_DOWNLOAD_FAILED:
			    //info.setText("");
			    break;

			case RESULT_DOWNLOAD_CANCEL:
			    //info.setText("");
			    break;

			default:
				break;
		}
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}
	
	public void setCurSize(int curSize) {
		this.curSize = curSize;
	}
}
