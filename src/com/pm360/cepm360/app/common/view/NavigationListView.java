package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * 
 * 标题: NavigationListView 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年3月16日
 *
 * @param <T>
 */
public class NavigationListView extends ListView implements OnScrollListener{
    
	public NavigationListView(Context context) {
		this(context, null);
	}

    public NavigationListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationListView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	super.setOnScrollListener(this);
    }
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		View c = view.getChildAt(0);
        if (c == null) {
            return;
        }
	}
}
