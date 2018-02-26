package com.pm360.cepm360.app.common.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.FileIconHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SimpleGridViewAdapter extends BaseAdapter {
	public final static String DEFAULT_ICON = "DEFAULT_ICON";
    private Context mContext;
    private List<String> mThumbs = new ArrayList<String>();
    private int mMaxItemSize;
    private boolean mViewOnly;
    private GridViewInterface mGridViewInterface;

    public SimpleGridViewAdapter(Context context, List<String> thumbs) {
        this.mContext = context;
        this.mThumbs = thumbs;
    }
    
    public SimpleGridViewAdapter(Context context, List<String> thumbs, GridViewInterface gridViewInterface) {
        this.mContext = context;
        this.mThumbs = thumbs;
        mGridViewInterface = gridViewInterface;
    }
    
    public void notityRefresh() {
        if (mThumbs.size() > mMaxItemSize) {
            mThumbs.remove(mThumbs.size() - 1);
        }
        if (mThumbs.size() < mMaxItemSize && !mViewOnly) {
            if (!mThumbs.get(mThumbs.size() - 1).equals(DEFAULT_ICON)) {
                mThumbs.add(DEFAULT_ICON);
            }
        }
        notifyDataSetChanged();
    }
    
    public void setMaxItemCount(int max) {
        mMaxItemSize = max;
    }
    
    public void setViewOnly(boolean viewOnly) {
        mViewOnly = viewOnly;
    }

    @Override
    public int getCount() {
        return mThumbs.size();
    }

    @Override
    public String getItem(int positon) {
        return mThumbs.get(positon);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_gridview_item, null);
            holder.thumb = (ImageView) convertView.findViewById(R.id.img);            
            holder.delete = (ImageView) convertView.findViewById(R.id.delete_markView);
            holder.delete.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setLayoutParams(new GridView.LayoutParams(140, 140));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (getItem(position).equals(DEFAULT_ICON) || mViewOnly) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
            setDeleteMode(holder.delete, position);
        }
        
        Drawable drawable = createDrawable(getItem(position));
        //Bitmap bitmap = createBitmap(getItem(position));
        if (drawable != null) {
        //if (bitmap != null) {
            holder.thumb.setImageDrawable(drawable);
            //holder.thumb.setImageBitmap(bitmap);
        } else {
            //holder.thumb.setBackgroundDrawable(createDrawable(getItem(position)));
            holder.thumb.setBackgroundColor(Color.TRANSPARENT);
        }
        
        return convertView;

    }
    
    @SuppressWarnings("unused")
	private Bitmap createBitmap(String path) {
        Bitmap thumbnail = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbnail;
    }

    @SuppressWarnings("deprecation")
    private Drawable createDrawable(String path) {
        Drawable drawable = null;
        if (path.equals(DEFAULT_ICON)) {
            // 默认 + 号
            drawable = mContext.getResources().getDrawable(R.drawable.picture_add);
            return drawable;
        }
        
        int res = FileIconHelper.getIconByFilename(path);
        if (res == R.drawable.file_documents_picture) {
            // 属于图片的，显示图片缩略图
            if ((new File(path)).exists()) {
                try {       
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    Bitmap bitmap = Bitmap.createScaledBitmap(bmp, 80, 80, true);
                    drawable = new BitmapDrawable(bitmap);
                    return drawable;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                drawable = mContext.getResources().getDrawable(R.drawable.picture_add);
            }
        } else {
            // 非图片的，显示形象图标
            drawable = mContext.getResources().getDrawable(res);
        }
        return drawable;
    }
    
    private void setDeleteMode(ImageView view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	if (mGridViewInterface != null) {
            		mGridViewInterface.deleteItem(position);
            	} else {
            		mThumbs.remove(position);
                    notityRefresh();	
            	}
            }
        });
    }
    
    public static interface GridViewInterface {
    	public void deleteItem(int position);
    }
    
    class ViewHolder {
        ImageView thumb;
        ImageView delete;
    }
}
