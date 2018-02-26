
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class CHScrollView extends HorizontalScrollView {
    
    public static final String TAG = "CHScrollView";

    private OnScrollChagnedListener mOnScrollChagnedListener;

    public CHScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CHScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mOnScrollChagnedListener != null) {
            mOnScrollChagnedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public interface OnScrollChagnedListener {
        public void onScrollChanged(CHScrollView view, int l, int t, int oldl, int oldt);
        public boolean getIsDrag();
    }

    public void setOnScrollChagnedListener(OnScrollChagnedListener listener) {
        mOnScrollChagnedListener = listener;
    }

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mOnScrollChagnedListener != null && mOnScrollChagnedListener.getIsDrag()) {
			//若处于拖拽状态，则立即下发到子类View,不得继续操作
			Log.v("ccc","CHScrollView 若处于拖拽状态，则立即下发到子类View,不得继续操作");
			return false;
		} else {
			Log.v("ccc","CHScrollView 若处于正常模式，则交由父类代码处理");
			//若处于正常模式，则交由父类代码处理
			return super.onInterceptTouchEvent(ev);
		}
	}   
}
