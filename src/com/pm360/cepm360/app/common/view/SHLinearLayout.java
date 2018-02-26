
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class SHLinearLayout extends LinearLayout {
	
	private int mLastTouchX ;
	private OnScrollHorizontalChagnedListener mOnScrollHorizontalChagnedListener;
	
    public SHLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SHLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    	setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				Log.v("ccc","SHLinearLayout onLongClick");
				if (mOnScrollHorizontalChagnedListener != null) {
					boolean isdrag = mOnScrollHorizontalChagnedListener.isDragEnable();
					mOnScrollHorizontalChagnedListener.setDragEnable(!isdrag, arg0);
				}
				return false;
			}
		});        
        Log.v("ccc","SHLinearLayout 设置了长按");
    }

    public SHLinearLayout(Context context) {
        super(context);
    }
    
    public interface OnScrollHorizontalChagnedListener {
        public void onScrollHorizontalChanged(View view, int offset);
        public boolean isDragEnable();
        public void setDragEnable(boolean isDrag, View shtv);
    }
    
    public void setScrollHorizontalChagnedListener(OnScrollHorizontalChagnedListener listener) {
        mOnScrollHorizontalChagnedListener = listener;
    }    
    
    public boolean onTouchEvent(MotionEvent event) {
    	
			 switch (event.getAction()) {
			 case MotionEvent.ACTION_DOWN:    
			     mLastTouchX  = (int) event.getRawX();
			     Log.v("ccc","SHLinearLayout onTouchEvent ACTION_DOWN "+mOnScrollHorizontalChagnedListener);
			     if (mOnScrollHorizontalChagnedListener != null && mOnScrollHorizontalChagnedListener.isDragEnable()) {
			    	 //拖拽模式下直接消耗掉down事件防止响应LongClick
			    	 return true;
			     } 
				 break;				 
			 case MotionEvent.ACTION_MOVE:

			     Log.v("ccc","SHLinearLayout onTouchEvent ACTION_MOVE "+mOnScrollHorizontalChagnedListener);
				 if (mOnScrollHorizontalChagnedListener != null && mOnScrollHorizontalChagnedListener.isDragEnable()) {
				     int x = (int) event.getRawX();
				     int offset = x-mLastTouchX;
				     if (mOnScrollHorizontalChagnedListener != null) {
				    	 mOnScrollHorizontalChagnedListener.onScrollHorizontalChanged(this, offset);
				    	 mLastTouchX = x;
				     }
				     //若是拖拽模式，则直接消费该事件
				     return true;
				 }
				 break;
			 case MotionEvent.ACTION_UP:
				 Log.v("ccc","SHLinearLayout onTouchEvent ACTION_UP "+mOnScrollHorizontalChagnedListener);
				 break;	
				 
			 case MotionEvent.ACTION_CANCEL:
				 Log.v("ccc","SHLinearLayout onTouchEvent ACTION_CANCEL "+mOnScrollHorizontalChagnedListener);
				 break;
			}
			 return super.onTouchEvent(event);
    }

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mOnScrollHorizontalChagnedListener != null && mOnScrollHorizontalChagnedListener.isDragEnable()) {
			//若处于拖拽状态，则立即下发到子类View,不得继续操作
//			Log.v("ccc","SHLinearLayout 若处于拖拽状态，则立即下发到子类View,不得继续操作 ");
			return true;
		} else {
//			Log.v("ccc","SHLinearLayout 若处于正常模式，则交由父类代码处理"+mOnScrollHorizontalChagnedListener);		
			return super.onInterceptTouchEvent(ev);
		}
	}
}
