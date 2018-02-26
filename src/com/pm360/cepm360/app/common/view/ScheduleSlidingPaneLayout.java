
package com.pm360.cepm360.app.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.pm360.cepm360.app.utils.UtilTools;

/**
 * Created by wangzhiwei
 */
public class ScheduleSlidingPaneLayout extends SlidingPaneLayout {

    private OnSSPScrollHorizontalListener mOnSSPScrollHorizontalListener;

    private float mInitialMotionX;
    @SuppressWarnings("unused")
	private float mInitialMotionY;
    private float mEdgeSlop;
    private boolean IS_PANEL_OPEN = true;
    
    private float mLastX;
    
    /**
     * 获取panel状态
     * @return true 打开， false 关闭
     */
    public boolean getPanelStatus() {
    	return IS_PANEL_OPEN;
    }

    public ScheduleSlidingPaneLayout(Context context) {
        this(context, null);
    }

    public ScheduleSlidingPaneLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ViewConfiguration config = ViewConfiguration.get(context);
        mEdgeSlop = config.getScaledEdgeSlop();
        mLastX = UtilTools.dp2pxW(context, 100);
        
        SlidingPaneLayout.PanelSlideListener listener =	new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelClosed(View view) {
            	IS_PANEL_OPEN = false;
            }

            @Override
            public void onPanelOpened(View view) {
            	IS_PANEL_OPEN = true;
            }

            @Override
            public void onPanelSlide(View view, float slideOffset) {
            	
            	// slideOffset 偏移百分比
                float x = getChildAt(0).getMeasuredWidth() * slideOffset;

                setFloatingMenuView(view, mLastX - x);
            	mLastX = x;
            }
        };

        setPanelSlideListener(listener);
    }

    private void setFloatingMenuView(View v, float x) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (child instanceof FloatingMenuView) {
                    if (child.getX() + x > 0)
                        child.setX(child.getX() + x);
                }
                setFloatingMenuView(child, x);
            }
        }
    }
    
    public interface OnSSPScrollHorizontalListener {
        public boolean isDragEnable();
    }	
    
    public void setOnSSPScrollHorizontalListener(OnSSPScrollHorizontalListener listener){
    	Log.v("ccc","ScheduleSlidingPaneLayout 设置监听");
    	mOnSSPScrollHorizontalListener = listener;
    }	    

	@SuppressLint("Recycle") @Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mOnSSPScrollHorizontalListener != null && mOnSSPScrollHorizontalListener.isDragEnable()) {
			//若处于拖拽状态，则立即下发到子类View,不得继续操作
//			Log.v("ccc","ScheduleSlidingPaneLayout 若处于拖拽状态，则立即下发到子类View,不得继续操作 ");
			return false;
		} else {
//			Log.v("ccc","ScheduleSlidingPaneLayout 若处于正常模式，则交由父类代码处理"+mOnSSPScrollHorizontalListener);
			//若处于正常模式，则交由父类代码处理
	        switch (MotionEventCompat.getActionMasked(ev)) {
	            case MotionEvent.ACTION_DOWN: {
	                mInitialMotionX = ev.getX();
	                mInitialMotionY = ev.getY();
	                break;
	            }

	            case MotionEvent.ACTION_MOVE: {
	                final float x = ev.getX();
	                final float y = ev.getY();
	                // The user should always be able to "close" the pane, so we
	                // only check
	                // for child scrollability if the pane is currently closed.
	                if (mInitialMotionX > mEdgeSlop && /* !isOpen() && */canScroll(this, false,
	                        Math.round(x - mInitialMotionX), Math.round(x), Math.round(y))) {
	                    // How do we set super.mIsUnableToDrag = true?
	                    // send the parent a cancel event
	                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
	                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
	                    return super.onInterceptTouchEvent(cancelEvent);
	                }
	            }
	        }

	        return super.onInterceptTouchEvent(ev);
		}
	}     
}
