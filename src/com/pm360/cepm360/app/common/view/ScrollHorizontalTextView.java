package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * 标题: NavigationListView 
 * 描述: 
 * 作者： ccl
 * 日期： 2016年3月16日
 *
 * @param <T>
 */
public class ScrollHorizontalTextView extends TextView {
	
	public static String TAG = "ScrollHorizontalTextView";
	private int mLastTouchX ;
	private OnScrollHorizontalChagnedListener mOnScrollHorizontalChagnedListener;
	private OnScrollHorizontalChagnedLayoutListener mOnScrollHorizontalChagnedLayoutListener;	
    
	public ScrollHorizontalTextView(Context context) {
		this(context, null);
	}

    public ScrollHorizontalTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollHorizontalTextView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	Log.v("ccc","ScrollHorizontalTextView 设置了长按");
    	setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				Log.v("ccc","onLongClick");
				if (mOnScrollHorizontalChagnedListener != null) {
					boolean isdrag = mOnScrollHorizontalChagnedListener.isDragEnable();
					mOnScrollHorizontalChagnedListener.setDragEnable(!isdrag, arg0);
				}
				return false;
			}
		});
    }
    
    public interface OnScrollHorizontalChagnedListener {
        public void onScrollHorizontalChanged(View view, int offset);
        public boolean isDragEnable();
        public void setDragEnable(boolean isDrag, View shtv);
    }
    
    public interface OnScrollHorizontalChagnedLayoutListener {
    	public void doHandler(int offset);
    }
    
    public void setScrollHorizontalChagnedLayoutListener(OnScrollHorizontalChagnedLayoutListener listener) {
        mOnScrollHorizontalChagnedLayoutListener = listener;
    }    
    			
    public void setScrollHorizontalChagnedListener(OnScrollHorizontalChagnedListener listener) {
        mOnScrollHorizontalChagnedListener = listener;
    }    
    
    public boolean onTouchEvent(MotionEvent event) {
    	
			 switch (event.getAction()) {
			 case MotionEvent.ACTION_DOWN:  
				 Log.v("ccc","ScrollHorizontalTextView MotionEvent.ACTION_DOWN ");
			     mLastTouchX  = (int) event.getRawX();
			     if (mOnScrollHorizontalChagnedListener != null && mOnScrollHorizontalChagnedListener.isDragEnable()) {
			    	 //拖拽模式下直接消耗掉down事件防止响应LongClick
			    	 return true;
			     } 
				 break;				 
			 case MotionEvent.ACTION_MOVE:

				 if (mOnScrollHorizontalChagnedListener != null && mOnScrollHorizontalChagnedListener.isDragEnable()) {
				     int x = (int) event.getRawX();
				     int offset = x-mLastTouchX;
				     if (mOnScrollHorizontalChagnedListener != null) {
				    	 mOnScrollHorizontalChagnedListener.onScrollHorizontalChanged(this, offset);
				    	 Log.v("ccc","ScrollHorizontalTextView MotionEvent.ACTION_MOVE 2 "+offset);
				    	 if (mOnScrollHorizontalChagnedLayoutListener != null) {
//				    		 //同步缩放定制的Layout
				    	 	mOnScrollHorizontalChagnedLayoutListener.doHandler(offset);
				    	 }
				    	 mLastTouchX = x;
				     }
				     //若是拖拽模式，则直接消费该事件
				     return true;
				 }
				 break;
			 case MotionEvent.ACTION_UP:
				 Log.v("ccc","ScrollHorizontalTextView MotionEvent.ACTION_UP ");
				 break;	
				 
			 case MotionEvent.ACTION_CANCEL:
				 Log.v("ccc","ScrollHorizontalTextView MotionEvent.ACTION_CANCEL ");
				 break;
			}
			 return super.onTouchEvent(event);
    }
}
