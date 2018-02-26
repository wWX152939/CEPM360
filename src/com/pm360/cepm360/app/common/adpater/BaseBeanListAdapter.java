package com.pm360.cepm360.app.common.adpater;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.entity.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseBeanListAdapter<T> extends BaseAdapter implements CHScrollView.OnScrollChagnedListener {
	private List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();
	private List<T> mTreeNode;

	private List<Integer> mSelectedPosition = new ArrayList<Integer>();
	private BaseBeanListAdapterManager<T> mManager;
	private ViewHolder holder;
	private Context mContext;
	
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
	
	public BaseBeanListAdapter(Context context, List<T> list, BaseBeanListAdapterManager<T> manager) {
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
			convertView = mManager.getLayoutInflater().inflate(mManager.getListItemLayoutId(),
                    parent, false);
			addCHScrollView((ListView) parent, convertView);
			holder = new ViewHolder();
			holder.tvs = new TextView[itemIds.length];
			for(int i = 0; i < itemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView.findViewById(itemIds[i]);
				if (i == 0) {
					holder.tvs[i].setWidth(LayoutParams.WRAP_CONTENT);
				}		
			}
			holder.icon = (ImageView) convertView.findViewById(R.id.file_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (holder.icon != null) {
			holder.icon.setImageResource(FileIconHelper.getIcon((Files)getItem(position)));
		}
		
		
		initListItem(holder, position);
		mManager.initListItem(holder.tvs, position);

		Map<String, String> item = mManager.beanToMap(getItem(position));
        String[] itemNames = mManager.getDisplayItems();
        if (item != null) {
        	holder.tvs[0].setText("" + (position + 1));
			holder.tvs[0].setTextColor(mContext.getResources().getColor(R.color.task_window_text));
			holder.tvs[0].setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp14_s));
        	for (int i = 1; i < itemNames.length; i++) {
    			holder.tvs[i].setText(item.get(itemNames[i]));
    			holder.tvs[i].setTextColor(mContext.getResources().getColor(R.color.task_window_text));
    			holder.tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp14_s));
            }
        } else {
        	for (int i = 0; i < itemNames.length; i++) {
            	holder.tvs[i].setText(null);
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
		new OnClickListener(){
			@Override
			public void onClick(View view) {
				setSelected(position, true);
			}		
		};
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
	
    public interface BaseBeanListAdapterManager<T> {
        LayoutInflater getLayoutInflater();

        int getListItemLayoutId();

        int[] getItemIds();

        String[] getDisplayItems();

        View getHeaderView();

        void initListItem(TextView[] tvs, int position);
        
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
