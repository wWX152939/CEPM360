package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 
 * 标题: NavigationListView 
 * 描述: 
 * 作者：ccl
 * 日期： 2016年3月16日
 *
 * @param <T>
 */
public class SHListView extends ListView {
    
	private OnScrollHorizontalListener mOnScrollHorizontalListener;
	public SHListView(Context context) {
		this(context, null);
	}

    public SHListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SHListView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }
	
    public interface OnScrollHorizontalListener {
        public boolean isSHListViewDrag();
    }	
    
    public void setOnScrollHorizontalListener(OnScrollHorizontalListener listener){
    	mOnScrollHorizontalListener = listener;
    }	    

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mOnScrollHorizontalListener != null && mOnScrollHorizontalListener.isSHListViewDrag()) {
			//若处于拖拽状态，则立即下发到子类View,不得继续操作
			return false;
		} else {
			//若处于正常模式，则交由父类代码处理
			return super.onInterceptTouchEvent(ev);
		}
	}
	
}
