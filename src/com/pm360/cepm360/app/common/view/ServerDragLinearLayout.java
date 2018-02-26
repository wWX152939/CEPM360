
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.pm360.cepm360.app.common.adpater.BaseDragListAdapter;

import java.util.ArrayList;
import java.util.List;
//import com.pm360.cepm360.app.common.adpater.BaseDragListAdapter;

public class ServerDragLinearLayout extends LinearLayout {
	
	//是否子类处于拖拽状态
	private boolean mIsDrag = false;
    // 用于储存子类的Adapter
    @SuppressWarnings("rawtypes")
	private List<BaseDragListAdapter> mAdapterList = new ArrayList<BaseDragListAdapter>();
	
    public ServerDragLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ServerDragLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ServerDragLinearLayout(Context context) {
        super(context);
    } 
    
    /*
     * 获取当前拖拽状态
     */
	public boolean getDragStatus() {
		return mIsDrag;
	} 
	
    /*
     * 设置当前拖拽状态
     */
	public void setDragStatus(boolean newStatus) {
		mIsDrag = newStatus;
	}	
	
    /*
     * 添加Adapter到列表里
     */
	public void addAdapter(BaseDragListAdapter<?> adapter) {
		mAdapterList.add(adapter);
	}
	
    
    public boolean onTouchEvent(MotionEvent event) {
    	
			 switch (event.getAction()) {
			 case MotionEvent.ACTION_DOWN:    
			     Log.v("ccc","ServerDragLinearLayout onTouchEvent ACTION_DOWN 应该立即退出拖拽模式 mAdapterList="+mAdapterList);
			     for (int i = 0; i < mAdapterList.size(); i++) {
			    	 mAdapterList.get(i).exitDragMode();
			     }
				 break;				 
				 
			 case MotionEvent.ACTION_MOVE:
			     Log.v("ccc","ServerDragLinearLayout onTouchEvent ACTION_MOVE ");
				 break;
				 
			 case MotionEvent.ACTION_UP:
				 Log.v("ccc","ServerDragLinearLayout onTouchEvent ACTION_UP ");
				 break;	
				 
			 case MotionEvent.ACTION_CANCEL:
				 Log.v("ccc","ServerDragLinearLayout onTouchEvent ACTION_CANCEL ");
				 break;
			}
			 return super.onTouchEvent(event);
    }

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		if (getDragStatus()) {
//			Log.v("ccc","ServerDragLinearLayout 正在拖拽状态 ");
//			return true;
//		} else {
//			return super.onInterceptTouchEvent(ev);
//		}
//	}
}
