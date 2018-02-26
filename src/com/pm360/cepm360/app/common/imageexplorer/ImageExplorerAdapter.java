package com.pm360.cepm360.app.common.imageexplorer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;

import java.util.LinkedList;
import java.util.List;

public class ImageExplorerAdapter extends CommonAdapter<String>
{

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	private TextView mButtonTextView;
	private int mMaxCount;

	public ImageExplorerAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath, TextView buttonTextView, int maxCount)
	{
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		mButtonTextView = buttonTextView;
		mMaxCount = maxCount;
	}
	
	public List<String> getSelectedImageList() {
		return mSelectedImage;
	}

	@Override
	public void convert(final com.pm360.cepm360.app.common.imageexplorer.ViewHolder helper, final String item)
	{
//		Log.v("ccc","convert "+ item);
		//设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.image_explorer_pictures_no);
		//设置no_selected
				helper.setImageResource(R.id.id_item_select,
						R.drawable.image_explorer_picture_unselected);
		//设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener()
		{
			//选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v)
			{
//				Log.v("ccc","onClick "+ v);
				
				// 已经选择过该图片
				if (mSelectedImage.contains(mDirPath + "/" + item))
				{
					//同步添加到popupwindows 的备注上
					//to do something
					
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.image_explorer_picture_unselected);
					mImageView.setColorFilter(null);
				} else
				// 未选择该图片
				{
					if (mSelectedImage.size() < mMaxCount) {
						//同步添加到popupwindows 的备注上
						//to do something
						
						mSelectedImage.add(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.image_explorer_pictures_selected);
						mImageView.setColorFilter(Color.parseColor("#77000000"));
					} else {
						Toast.makeText(mContext, mContext.getString(R.string.max_select_tip, mMaxCount), Toast.LENGTH_SHORT).show();
					}
				}
				if (mSelectedImage.size() > 0) {
					mButtonTextView.setText(mContext.getString(R.string.upload) + "("+mSelectedImage.size()+"/"+mMaxCount+")");
					mButtonTextView.setBackgroundResource(R.drawable.image_explorer_corners_button_selected);				
				} else {
					mButtonTextView.setText(mContext.getString(R.string.upload));
					mButtonTextView.setBackgroundResource(R.drawable.image_explorer_corners_button_noselected);				
				}
			}
		});
		
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item))
		{
			Log.v("ccc","mSelectedImage:::"+mSelectedImage);
			mSelect.setImageResource(R.drawable.image_explorer_pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}		

	}
}
