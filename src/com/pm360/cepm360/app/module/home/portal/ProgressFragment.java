package com.pm360.cepm360.app.module.home.portal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.ZoomImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {
    
    private View mRootView;
    
    private GridView mGridView;
    private SimpleGridAdapter mGridViewAdapter;
    private ZoomImageView mContentImageView;
    private ImageView mForwardAction, mNextAction;
    private String mContentImagePath;
    private int mCurrentPosition;
    
    private ArrayList<String> mThumbs = new ArrayList<String>();
    private ArrayList<Bitmap> mBitmaps = new ArrayList<Bitmap>();
    
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            mGridViewAdapter.notifyDataSetChanged();
            return false;
        }
    });
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        mRootView = inflater.inflate(
                R.layout.portal_more_progress_fragment_layout, container, false);

        ArrayList<String> thumbs = (ArrayList<String>) getArguments().getStringArrayList("paths");
        if (thumbs != null && thumbs.size() > 0) {
            mThumbs.addAll(thumbs);
        }
                
        mContentImageView = (ZoomImageView) mRootView.findViewById(R.id.content_imageview);
        mForwardAction = (ImageView) mRootView.findViewById(R.id.forward1);
        mNextAction = (ImageView) mRootView.findViewById(R.id.next1);
        mForwardAction.setOnClickListener(mClicklistener);
        mNextAction.setOnClickListener(mClicklistener);
        
        initGridView(mRootView);
        
        if (mThumbs.size() > 0) {
            createScaledBitmap(mThumbs);
            setContentImageView(mThumbs.get(0));
        }

        return mRootView;
    }
    
    private void initGridView(View view) {
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridViewAdapter = new SimpleGridAdapter(getActivity(), mBitmaps);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                mCurrentPosition = position;
                mContentImagePath = mThumbs.get(position);
                setContentImageView(mContentImagePath);
                updateActionIcons(mThumbs, position);
            }
        });

        if (mThumbs.size() > 1) {
            mCurrentPosition = 0;
            mContentImagePath = mThumbs.get(0);
            setContentImageView(mContentImagePath);
        }
    }
    
    View.OnClickListener mClicklistener = new View.OnClickListener() {           
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.forward1: // 上一张
                mCurrentPosition--;
                if (mCurrentPosition >= 0) {
                    setContentImageView(mThumbs.get(mCurrentPosition));
                }
                break;
            case R.id.next1: // 下一张
                mCurrentPosition++;
                if (mCurrentPosition < mThumbs.size()) {
                    setContentImageView(mThumbs.get(mCurrentPosition));
                }
                break;
            }            
        }
    };
    
    private void setContentImageView(String path) {
        if ((new File(path)).exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(path); 
                if(null != bitmap ) {
                    // int scale =
                    // ImageUtils.reckonThumbnail(cambitmap.getWidth(),
                    // bitmap.getHeight(), 480, 360);
                    // Bitmap b = ImageUtils.PicZoom(bitmap,
                    // bitmap.getWidth() / scale, bitmap.getHeight() /
                    // scale);
                    // bitmap.recycle();
                    mContentImageView.setScaleType(ScaleType.FIT_CENTER);
                    mContentImageView.setImageBitmap(bitmap);
                    mContentImageView.setBackgroundColor(Color.BLACK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 图片解析失败
            mThumbs.remove(path);
            setContentImageView(mThumbs.get(mCurrentPosition > 0 ? mCurrentPosition-1 : 0));
        }
        updateActionIcons(mThumbs, mCurrentPosition);
    }
    
    private void updateActionIcons(List<String> thumbs, int position) {
        if (thumbs.size() > 1) {
            mForwardAction.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
            if (position >= 0 && position < thumbs.size() - 1) {
                mNextAction.setVisibility(View.VISIBLE);
            } else {
                mNextAction.setVisibility(View.GONE);
            }
            if (position == thumbs.size() - 2) {
                if (mThumbs.get(mThumbs.size() - 1).equals("DEFAULT_ICON")) {
                    mNextAction.setVisibility(View.GONE);
                }
            }
        } else {
            mForwardAction.setVisibility(View.GONE);
            mNextAction.setVisibility(View.GONE);    
        }
    }
    
    private void createScaledBitmap(final List<String> paths) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (String path : paths) {
                    Bitmap bitmap = createBitmap(path);
                    mBitmaps.add(bitmap);
                    mHandler.sendEmptyMessage(0);
                }
                
            }
        });
        t.start();
    }
    
    private Bitmap createBitmap(String path) {
        Bitmap thumbnail = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 160, 160);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbnail;
    }
    
    private class SimpleGridAdapter extends BaseAdapter {
        
        private List<Bitmap> mList = new ArrayList<Bitmap>();
        
        public SimpleGridAdapter(Context context, List<Bitmap> bitmapLists) {
            mList = bitmapLists;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Bitmap getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams") @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_gridview_item, null);
                holder.thumb = (ImageView) convertView.findViewById(R.id.img);            
                holder.delete = (ImageView) convertView.findViewById(R.id.delete_markView);
                holder.delete.setVisibility(View.GONE);
                //holder.delete.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //convertView.setLayoutParams(new GridView.LayoutParams(240, 240));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            Bitmap bitmap = getItem(position);
            if (bitmap != null) {
                holder.thumb.setImageBitmap(bitmap);
            } else {
                holder.thumb.setBackgroundColor(Color.TRANSPARENT);
            }
            
            return convertView;
        }
        
    }
    
    class ViewHolder {
        ImageView thumb;
        ImageView delete;
    }

}
