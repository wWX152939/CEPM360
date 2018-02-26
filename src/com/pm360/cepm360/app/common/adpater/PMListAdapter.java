
package com.pm360.cepm360.app.common.adpater;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.CHScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PMListAdapter extends BaseAdapter implements CHScrollView.OnScrollChagnedListener {

    private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
    private List<Integer> mSelectedPosition = new ArrayList<Integer>();
    private List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();
    private PMListAdapterManager mManager;

    public PMListAdapter(PMListAdapterManager manager) {
        mManager = manager;
        addCHScrollView(null, manager.getHeaderView());
    }

    public int getCount() {
        return mData.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView[] tvs;
        
        if (convertView == null) {
            convertView = mManager.getLayoutInflater().inflate(mManager.getListItemLayoutId(),
                    parent, false);
            addCHScrollView((ListView) parent, convertView);
            int[] itemIds = mManager.getItemIds();
            tvs = new TextView[itemIds.length];
            for (int i = 0; i < itemIds.length; i++) {
                tvs[i] = (TextView) convertView.findViewById(itemIds[i]);
            }
            convertView.setTag(tvs);
        } else {
            tvs = (TextView[]) convertView.getTag();

        }
        mManager.initListItem(tvs, position);
        Map<String, String> item = getItem(position);
        String[] itemNames = mManager.getItemNames();

        
        for (int i = 0; i < itemNames.length; i++) {
//        	Log.i("dog1", "i:" + i + " itemNames" + item.get(itemNames[i]));
            tvs[i].setText(item.get(itemNames[i]));
        }
        if (mSelectedPosition.contains((Integer) position)) {
        	convertView.setBackgroundResource(R.color.touch_high_light);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        /*
         * int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };
         * convertView.setBackgroundColor(colors[position % 2]);
         */
        return convertView;
    }

    @Override
    public Map<String, String> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    public List<Map<String, String>> getList() {
        return mData;
    }

    public void setSelected(int position, boolean isSeleced) {
        if (isSeleced)
            mSelectedPosition.add((Integer) position);
        else
            mSelectedPosition.remove((Integer) position);
        notifyDataSetChanged();
    }

    public interface PMListAdapterManager {
        LayoutInflater getLayoutInflater();

        int getListItemLayoutId();

        int[] getItemIds();

        String[] getItemNames();

        View getHeaderView();

        void initListItem(TextView[] tvs, int position);

    }

	@Override
	public boolean getIsDrag() {
		// TODO Auto-generated method stub
		return false;
	}

}
