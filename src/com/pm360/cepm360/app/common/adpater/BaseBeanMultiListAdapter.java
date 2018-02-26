package com.pm360.cepm360.app.common.adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.CHScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseBeanMultiListAdapter<T> extends BaseAdapter implements CHScrollView.OnScrollChagnedListener {
	private List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();
	private List<T> mTreeNode;

	private List<Integer> mSelectedPosition = new ArrayList<Integer>();
	private BaseBeanMultiListAdapterManager<T> mManager;
	private ViewHolder mHolderLeft;
	private ViewHolder mHolderRight;
	private Context mContext = null;
	private int mHideFlag = 0;
	
	public final int NO_HIDE = 0;
	public final int HIDE_ALL = 1;
	
	
	public void setHideList(int hideFlag) {
		mHideFlag = hideFlag;
		Log.i("dog2", "set mHideFlag:" + mHideFlag);
		mHandler.sendEmptyMessage(0);
	}
	
    @SuppressLint("HandlerLeak") 
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	notifyDataSetChanged();
        }
    };
    
    public void refreshList() {
    	mHandler.sendEmptyMessage(0);
    }
    
    public void refreshList(int position) {
    	mSelectedPosition.remove((Integer) position);
    	mHandler.sendEmptyMessage(0);
    }
    
    public List<T>  getTreedNode() {
    	return mTreeNode;
    }
	
	public BaseBeanMultiListAdapter(Context context, List<T> list, BaseBeanMultiListAdapterManager<T> manager) {
		mManager = manager;
		mTreeNode = list;
		mContext = context;
		addCHScrollView(null, manager.getHeaderView());	
	}
	

	public void addBeanList(List<T> list) {
		for (T bean : list) {
			mTreeNode.add(bean);
		}
		
	}
    
	public void updateTreeList(List<T> list) {
		mTreeNode.clear();
		if (list != null) {
			addBeanList(list);
		}
		mHandler.sendEmptyMessage(0);
	}
	
	@Override
	public int getCount() {
		return mTreeNode.size();
	}
	
	@Override
	public T getItem(int position) {
		return mTreeNode.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int[] itemIds = mManager.getItemIds();
		if (convertView == null) {
			if (parent.getId() == R.id.left_container_listview) {
				convertView = mManager.getLayoutInflater().inflate(mManager.getLeftListItemLayoutId(),
	                    parent, false);
				mHolderLeft = new ViewHolder();
				mHolderLeft.tvs = new TextView[1];
				mHolderLeft.tvs[0] = (TextView) convertView.findViewById(itemIds[0]);
				
				convertView.setTag(mHolderLeft);
			}
			else {
				convertView = mManager.getLayoutInflater().inflate(mManager.getListItemLayoutId(),
	                    parent, false);
				addCHScrollView((ListView) parent, convertView);
				mHolderRight = new ViewHolder();
				mHolderRight.tvs = new TextView[itemIds.length - 1];
				for(int i = 0; i < itemIds.length - 1; i++) {
					mHolderRight.tvs[i] = (TextView) convertView.findViewById(itemIds[i + 1]);
					convertView.setTag(mHolderRight);
				}
				
			} 
		}
		else {
			if (parent.getId() == R.id.left_container_listview) {
				mHolderLeft = (ViewHolder) convertView.getTag();	
			} else {
				mHolderRight = (ViewHolder) convertView.getTag();
			}
		}

		Map<String, String> item = mManager.beanToMap(getItem(position));
        String[] itemNames = mManager.getDisplayItems();
		if (parent.getId() == R.id.left_container_listview) {
			mHolderLeft.tvs[0].setText(item.get(itemNames[0]));
			if (mContext != null) {
				mHolderLeft.tvs[0].setTextColor(mContext.getResources().getColor(R.color.setting_list_text_color));
			} else {
				mHolderLeft.tvs[0].setTextColor(Color.WHITE);
			}
			initListItem(mHolderLeft, position);
		} 
		else {
			mManager.initListItem(mHolderRight.tvs, position);
	
	        if (item != null) {

	        	for (int i = 0; i < itemNames.length - 1; i++) {
	        		mHolderRight.tvs[i].setText(item.get(itemNames[i + 1]));
	        		if (mContext != null) {
	    				mHolderLeft.tvs[0].setTextColor(mContext.getResources().getColor(R.color.setting_list_text_color));
	    			} else {
	    				mHolderLeft.tvs[0].setTextColor(Color.WHITE);
	    			}
	        		mHolderRight.tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp14_s));
	            }
	        }
		}
		if (mSelectedPosition.contains((Integer) position)) {
        	convertView.setBackgroundResource(R.color.touch_high_light);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
		return convertView;
	}

	
	private void initListItem(ViewHolder holder, final int position) {
		OnClickListener listener = new OnClickListener(){
			@Override
			public void onClick(View view) {
				setSelected(position, true);
				mManager.requestData(position);
			}		
		};
		holder.tvs[0].setOnClickListener(listener);
	}

	
    public void setSelected(int position, boolean isSeleced) {
        if (isSeleced) {
        	mSelectedPosition.clear();
            mSelectedPosition.add((Integer) position);
        } else {
            mSelectedPosition.remove((Integer) position);
        }
        notifyDataSetChanged();
    }


	@Override
	public void onScrollChanged(CHScrollView view, int l, int t, int oldl, int oldt) {
        for (CHScrollView scrollView : mHScrollViews) {
            if (view != scrollView)
                scrollView.smoothScrollTo(l, t);
        }		
	}
	
    public void addCHScrollView(ListView listView, View view) {
        final CHScrollView chScrollView = (CHScrollView) view
                .findViewWithTag(CHScrollView.TAG);
        if (chScrollView == null)
            return;
        chScrollView.setOnScrollChagnedListener(this);
        if (!mHScrollViews.isEmpty()) {
            final int scrollX = mHScrollViews.get(mHScrollViews.size() - 1).getScrollX();
            listView.post(new Runnable() {
                @Override
                public void run() {
                    chScrollView.scrollTo(scrollX, 0);
                }
            });
        }
        mHScrollViews.add(chScrollView);
    }
	
    public interface BaseBeanMultiListAdapterManager<T> {
        LayoutInflater getLayoutInflater();

        int getListItemLayoutId();

        int getLeftListItemLayoutId();

        int[] getItemIds();

        String[] getDisplayItems();

        View getHeaderView();

        void initListItem(TextView[] tvs, int position);
        
        boolean requestData(int position);
        
        Map<String, String> beanToMap(T bean);

    }
    
	class ViewHolder {
		ImageView icon;
		TextView[] tvs;
	}

	@Override
	public boolean getIsDrag() {
		// TODO Auto-generated method stub
		return false;
	}
}
