package com.pm360.cepm360.app.common.view.parent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareContent {
	private Context mContext;
	private String mShareListTitle;
	
	// 是否使用过滤应用功能
	private final boolean mUseFilter = false; 
	private boolean mHasApplication;
	
	public ShareContent(Context mContext) {
		super();
		this.mContext = mContext;
		this.mShareListTitle = mContext.getString(R.string.share_to);
	}

	/**
	 * 分享文字
	 * @param text
	 */
	public void sendText(String text) {
		sendText(null, null, text);
	}
	
	/**
	 * 分享文字
	 * @param title
	 * @param text
	 */
	public void sendText(String title, String text) {
		sendText(null, title, text);
	}
	
	/**
	 * 分享文字
	 * @param subject
	 * @param title
	 * @param text
	 */
	public void sendText(String subject, String title, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		
		// 设置主题和标题
		setSubjectTitle(subject, title, intent);
    	
    	intent.putExtra(Intent.EXTRA_TEXT, text);
    	intent.setType("text/plain");
    	
    	hendleIntent(intent);
	}
	
	/**
	 * 分享图片
	 * @param imagePath
	 */
	public void sendImage(String imagePath) {
		sendImage(null, null, imagePath);
	}
	
	/**
	 * 带标题的分享图片
	 * @param title
	 * @param imagePath
	 */
	public void sendImage(String title, String imagePath) {
		sendImage(null, title, imagePath);
	}
	
	/**
	 * 发送带主题和标题的单图片分享
	 * @param subject
	 * @param title
	 * @param imagePath
	 */
	public void sendImage(String subject, String title, String imagePath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		
		// 设置主题和标题
		setSubjectTitle(subject, title, intent);
    	
		File file = new File(imagePath);
		if (file.exists()) {
			Uri imageUri = Uri.fromFile(file);
			intent.putExtra(Intent.EXTRA_STREAM, imageUri);  
	    	intent.setType("image/*");
	    	
	    	hendleIntent(intent);
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.file_no_exist) 
					+ ":" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 分享多个图片
	 * @param imagePaths
	 */
	public void sendImage(ArrayList<String> imagePaths) {
		sendImage(null, null, imagePaths);
	}
	
	/**
	 * 带标题的多文件分享
	 * @param title
	 * @param imagePaths
	 */
	public void sendImage(String title, ArrayList<String> imagePaths) {
		sendImage(null, title, imagePaths);
	}
	
	/**
	 * 带主题和标题的多文件分享
	 * @param subject
	 * @param title
	 * @param imagePaths
	 */
	public void sendImage(String subject, String title, ArrayList<String> imagePaths) {
		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		
		// 设置主题和标题
		setSubjectTitle(subject, title, intent);
    	
		ArrayList<Uri> imageUris = new ArrayList<Uri>();
		for (int i = 0; i < imagePaths.size(); i++) {
			Uri imageUri = Uri.fromFile(new File(imagePaths.get(i)));
			imageUris.add(imageUri);
		}
		
    	intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris); 
    	intent.setType("image/*");
    	
    	hendleIntent(intent);
	}
	
	/**
	 * 设置主题和标题
	 * @param subject
	 * @param title
	 * @param intent
	 */
	private void setSubjectTitle(String subject, String title, Intent intent) {
		if (subject != null) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);  
		}
		
		if (title != null) {
			intent.putExtra(Intent.EXTRA_TITLE, title);  
		}
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	/**
	 * 处理组装好的Intent
	 * @param intent
	 */
	private void hendleIntent(Intent intent) {
		if (mUseFilter) {
			List<Intent> intents = filterNeedApplication(intent);
			
			if (!intents.isEmpty()) {
				Intent chooserIntent = 
						Intent.createChooser(intents.remove(0), mShareListTitle);
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						intents.toArray(new Parcelable[] {}));
	    		mContext.startActivity(chooserIntent);
			} else {
				Toast.makeText(mContext, mContext.getString(R.string.no_receiver_component),
	    				Toast.LENGTH_LONG).show();
			}
		} else if (mHasApplication) {
			mContext.startActivity(Intent.createChooser(intent, mShareListTitle));
		} else {
			PackageManager pm = mContext.getPackageManager();
			List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
			
			if (!resolveInfos.isEmpty()) {
				mHasApplication = true;
			}
			
			if (mHasApplication) {
				mContext.startActivity(Intent.createChooser(intent, mShareListTitle));
			} else {
				Toast.makeText(mContext, mContext.getString(R.string.no_receiver_component),
	    				Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * 过滤接收的组件
	 */
	private List<Intent> filterNeedApplication(Intent intent) {
		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> resolveInfos = 
				pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		
		List<Intent> intents = null;
		if (!resolveInfos.isEmpty()) {
			intents = new ArrayList<Intent>();
			
			for (int i = 0; i < resolveInfos.size(); i++) {
				ActivityInfo activityInfo = resolveInfos.get(i).activityInfo;
				if (activityInfo.packageName.contains("com.tencent")
						|| activityInfo.packageName.contains("com.alibaba.android.rimet")) {
					Intent intent2 = new Intent(intent);
					intent2.setComponent(new ComponentName(activityInfo.packageName,
															activityInfo.name));
					intents.add(intent2);
					LogUtil.e("PackageName = " + intent2.getPackage() + " ClassName = " + activityInfo.name);
				}
			}
		}
		
		return intents;
	}
	
	/**
	 * 截屏并获取图片路径
	 * @param view
	 * @return
	 */
	public String shotScreen(View view) {
		String path = FileUtils.SDPATH + "Pictures/Screenshots/";
		String filePath = path + DateUtils.dateToString(DateUtils.FORMAT_LONG, new Date());
		
		// 创建目录
		FileUtils.createOrExistsFolder(path);
		
		View rootView = view.getRootView();
		rootView.setDrawingCacheEnabled(true);
		rootView.buildDrawingCache();
		
		Bitmap bitmap = rootView.getDrawingCache();
		if (bitmap != null) {
			
			try {
				bitmap.compress(Bitmap.CompressFormat.PNG, 
								100, new FileOutputStream(filePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return filePath;
	}
	
	/**
	 * 截屏并分享
	 * @param view
	 */
	public void shareShotScreen(View view) {
		sendImage(shotScreen(view));
	}
}
