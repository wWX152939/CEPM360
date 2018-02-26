
package com.pm360.cepm360.app.common.adpater;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.app.common.view.CHScrollView;

import java.util.ArrayList;
import java.util.List;

public class BaseCursorAdapter extends CursorAdapter implements CHScrollView.OnScrollChagnedListener {

    private int mItemLayout;
    private int[] mColumnIds;
    private String[] mColumnNames;
    private ItemManager mItemManager;
    
    private List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();

    @SuppressWarnings("deprecation")
	public BaseCursorAdapter(Context context, Cursor c, int itemLayout,
            int[] columnIds, String[] columnNames, ItemManager itemManager) {
        super(context, c);
        mItemLayout = itemLayout;
        mColumnIds = columnIds;
        mColumnNames = columnNames;
        mItemManager = itemManager;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView[] tvs = new TextView[mColumnIds.length];
        for (int i = 0; i < mColumnIds.length; i++) {
            tvs[i] = (TextView) view.findViewById(mColumnIds[i]);
            tvs[i].setText(cursor.getString(cursor.getColumnIndex(mColumnNames[i])));
        }
        if (mItemManager != null)
            mItemManager.initItem(tvs, cursor, view);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(mItemLayout, parent, false);
        addCHScrollView((ListView) parent, view);
        return view;
    }

    public interface ItemManager {
        public void initItem(TextView[] tvs, Cursor cursor, View group);
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

	@Override
	public boolean getIsDrag() {
		// TODO Auto-generated method stub
		return false;
	}

}
