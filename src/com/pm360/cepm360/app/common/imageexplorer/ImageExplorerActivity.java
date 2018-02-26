package com.pm360.cepm360.app.common.imageexplorer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.imageexplorer.ListImageDirPopupWindow.OnImageDirSelected;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ImageExplorerActivity extends Activity implements OnImageDirSelected
{
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private ImageExplorerAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	public static String MAX_PICTURE_COUNT_KEY = "max_picture_count_key";
	public static String IMAGE_SELECT_RESULT_KEY = "image_select_result_key";
	private RelativeLayout mBottomLy;
	private int mMaxCount;
	private TextView mChooseDir;
	private TextView mImageCount;
	private TextView mButtonTextView;
	private ImageView mButtonBack;
	int mTotalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mProgressDialog.dismiss();
			
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
			
			// 为View绑定数据			
			data2View(msg.arg1);
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View(int index)
	{
		if (mImgDir == null)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.no_found_any_picture),
					Toast.LENGTH_SHORT).show();
			return;
		}
		//Log.v("ccc","mImgDir="+mImgDir);
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */

		//ListView listView = mListImageDirPopupWindow.getListView();
		//Log.v("ccc","name == "+listView.getChildAt(index));
		//Log.v("ccc","mImgs="+mImgs);
		mAdapter = new ImageExplorerAdapter(getApplicationContext(), mImgs,
				R.layout.image_explorer_grid_item, mImgDir.getAbsolutePath(), mButtonTextView, mMaxCount);
		if (mAdapter != null) {
			mAdapter.getSelectedImageList().clear();
		 }		
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(getString(R.string.preview_some_pictures, mTotalCount));
		
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	@SuppressLint("InflateParams")
	private void initListDirPopupWindw()
	{
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.image_explorer_list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_explorer_activity_main);
		
		getActionBar().hide();
		
		Intent intent = getIntent();
		mMaxCount = intent.getIntExtra(MAX_PICTURE_COUNT_KEY, -1);
		if (mMaxCount == -1){
			mMaxCount = 9;
		}

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages()
	{
		//Log.v("ccc", "getImages");
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, getString(R.string.no_ext_storage), Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, getString(R.string.loading));

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				String firstImage = null;
				int index = 0;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImageExplorerActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				//Log.v("ccc", mCursor.getCount() + "");
				while (mCursor.moveToNext())
				{
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath))
					{
						continue;
					} else
					{
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = 0;
					String[] files = parentFile.list(new FilenameFilter()
					{
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					});
					if (files != null) {
						picSize = files.length;
					}
					mTotalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);
					index++;
					//Log.v("ccc","path="+path+", "+picSize);
					if (picSize > mPicsSize)
					{
						mPicsSize = picSize;
						mImgDir = parentFile;
						//Log.v("ccc","index="+index);
					}
				}
				//Log.v("ccc","run mImgDir="+mImgDir);
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				Message msg = new Message();
				msg.what = 0x110;
				msg.arg1 = index;
				mHandler.sendMessage(msg);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView()
	{
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mButtonTextView = (TextView) findViewById(R.id.id_chioce_count);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		mButtonBack = (ImageView) findViewById(R.id.id_back);
	}

	private void initEvent()
	{
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
		
		mButtonTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mAdapter.getSelectedImageList().size() != 0) {
					Intent intent = new Intent();
					Bundle resultBundle = new Bundle();
					resultBundle.putSerializable(IMAGE_SELECT_RESULT_KEY, (Serializable) mAdapter.getSelectedImageList());
					intent.putExtras(resultBundle);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});
		
		mButtonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}
	
	

	@Override
	public void selected(ImageFloder floder, View dirItemView)
	{
		//Log.v("ccc","selected:"+floder);
		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new ImageExplorerAdapter(getApplicationContext(), mImgs,
				R.layout.image_explorer_grid_item, mImgDir.getAbsolutePath(), mButtonTextView, mMaxCount);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(getString(R.string.preview_some_pictures, floder.getCount()));
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

}
