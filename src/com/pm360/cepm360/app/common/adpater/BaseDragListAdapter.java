package com.pm360.cepm360.app.common.adpater;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.CHScrollView;
import com.pm360.cepm360.app.common.view.SHLinearLayout;
import com.pm360.cepm360.app.common.view.SHListView;
import com.pm360.cepm360.app.common.view.SHWhiteLinearLayout;
import com.pm360.cepm360.app.common.view.ScheduleSlidingPaneLayout;
import com.pm360.cepm360.app.common.view.ScrollHorizontalTextView;
import com.pm360.cepm360.app.common.view.ScrollHorizontalWhiteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDragListAdapter<T> extends BaseAdapter implements
        	CHScrollView.OnScrollChagnedListener, ScrollHorizontalTextView.OnScrollHorizontalChagnedListener, 
        	SHLinearLayout.OnScrollHorizontalChagnedListener, SHListView.OnScrollHorizontalListener, 
        	ScheduleSlidingPaneLayout.OnSSPScrollHorizontalListener{

	/*							列表单列拖拽使用须知
	 * 1，需要滑动的TextView或者Layout必须使用 ScrollHorizontalTextView、ScrollHorizontalWhiteTextView、
	 * 	  SHLinearLayout、SHWhiteLinearLayout构建单元，
	 * 	    表格单元的width最好是定值，最好不要用Wrap_content或者Fill_parent.否者拖动可能发生突变，
	 *	    若表格单元内有多个View,比如有图标和文字，则直接使用 SHLinearLayout嵌套此单元(在xml里直接修改)。
	 *
	 * 2，需要滑动的ListView必须使用 SHListView 控件，否者会出现手指滑动无法换行(在xml里直接修改)。
	 * 
	 * 3，ListView使用的Adapter必须是BaseDragListAdapter类或其子类。
	 * 
	 * 4，Adapter在构建的时候必须传入R.array.xxx,此数组由一个列表项所有滑动单元的id号组成，
	 * 	     可以是TextView或者LinearLayout两种控件的Id(目前只支持这两种).
	 * 
	 * 5，Adapter在构建后，还必须让其所在的Activity或者Fragment实现OngetBDLASlidePaneListener监听，
	 *    然后执行Adapter.setOngetBDLASlidePaneListener(this),
	 *    (此步骤用于让内部核心获取到SlidePane,之后内部核心实现SlidePane的监听，这样SlidePane就能够感知当前是否在拖拽状态)
	 * 
	 * 6，在多个Fragment共用一个SlidePane的时候需要每个Fragment在OnHiddle里调用Adapter.setSPListener();
	 * 	  (此步骤可防止多个Fragment滑动标志互相影响，发生混乱，原理是当前Fragment的adapter重新占有SlidePane的使用权)
	 *    (BaseTreeContentFragment、BaseListFragment、BaseTreeListFragment已经集成该功能(3、4、5、6))
	 * 
	 * 7，需要在整个大布局最外层改为ServerDragLinearLayout、ServerDragFrameLayout。
	 * 
	 * 8, 需要在Adapter的构建的时候，调用ServerDragLinearLayout.addAdapter(mListAdapter);
	 * 
	 * 8，问题集锦： 一，高亮都不行，那肯定是行布局的xml没有配好。
	 * 			      二，高亮可以，但是滑动时手指一换行就不行了，那应该是主布局xml的ListView没有配好。
	 * 			      三，高亮可以，但是无法滑动，检查第5步是否做好。
	 * 			      四，高亮可以，可以滑动，但是多Fragment切换的时候滑动混乱。检查第6部是否做好。
	 */

	//储存每列控件的宽度值，String为序号,Integer是宽度
    private Map<String, Integer> mHScrollViewsWidthBigMap = new HashMap<String, Integer>();
    //储存每列的所有控件，string为序号,List<View>是每列的所有View
    private Map<String, List<View>> mHScrollViewsBigMap = new HashMap<String, List<View>>();
    // 是否可左右拖动  （表示当前是否在拖动状态，是的话则顶层布局必须下发事件，不许拦截）
//    private boolean mIsDrag = false;  
    // 是否可左右拖动 特殊情况 （例如任务编制的属性界面弹出，导致列表就一列露出，此时不可以再拖拽，否者无法退出拖拽模式）
    private boolean mSpecialIsDrag = true;    
    // 正在拖拽的区域键值
    private String mHighLightKey;   
    // 正在拖拽的区域标题view
    private View mHighLightTitleView;     
    // 每一个TextView的Id
    private int[] scrollViewIds;
    // 树形模式下只能滑动标题
    private boolean mTreeMode = false;
    // 用于获取滑动面板的监听
    private OngetBDLASlidePaneListener mOnBDLAgetSlidePaneListener;
    // 用于获取拖拽状态的监听
    private OnDragStatusListener mOnDragStatusListener;    
    
    public interface OngetBDLASlidePaneListener {
        public View getSlidePane();
    }
    
    public interface OnDragStatusListener {
        public boolean getDragStatus();
        public void setDragStatus(boolean newStatus);
    }    
    
    public void setOngetBDLASlidePaneListener(OngetBDLASlidePaneListener ongetSlidePaneListener) {
    	mOnBDLAgetSlidePaneListener = ongetSlidePaneListener;
    }
    
    public void setOnDragStatusListener(OnDragStatusListener onDragStatusListener) {
    	mOnDragStatusListener = onDragStatusListener;
    }    
    
	@Override
	public boolean isSHListViewDrag() {
		return isDragEnable();
	}
	
	public void setTreeMode() {
		mTreeMode = true;
	}	
	
	public BaseDragListAdapter(Context context, int resourseId) {
		if (resourseId != 0) {
			TypedArray typedArray = context.getResources()
					.obtainTypedArray(resourseId);
	        scrollViewIds = new int[typedArray.length()];
	        for (int i = 0; i < scrollViewIds.length; i++) {
	        	scrollViewIds[i] = typedArray.getResourceId(i, 0);
	        }
	        typedArray.recycle();
		}
    }

	@Override
	public void onScrollHorizontalChanged(View view, int offset) {
		
		for (int i = 0; i < scrollViewIds.length; i++) {

			if (view.getId() == scrollViewIds[i]) {
				if (mHScrollViewsBigMap.get(i+"") != null && mHighLightKey != null) {
					if (mHighLightKey.equals(i+"")) {
						//若点击的区域与正在操作区域一致
						
						int width = mHScrollViewsBigMap.get(i+"").get(0).getLayoutParams().width;
						int height = mHScrollViewsBigMap.get(i+"").get(0).getLayoutParams().height;
						float minwidth = 100;
						if (ScrollHorizontalTextView.class.isInstance(view) || ScrollHorizontalWhiteTextView.class.isInstance(view)) {
							minwidth = new Paint(Paint.ANTI_ALIAS_FLAG).measureText(((TextView) mHScrollViewsBigMap.get(i+"").get(0)).getText()+"")*2;
						}
						mHScrollViewsWidthBigMap.put(i+"", Integer.valueOf(width+offset));

						if (offset > 0) {
							for (View stv : mHScrollViewsBigMap.get(i+"")) {
								if (LinearLayout.LayoutParams.class.isInstance(stv.getLayoutParams())) {
									LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) getLayoutParams(width+offset, height, 1);
									stv.setLayoutParams(params);
								} else if (RelativeLayout.LayoutParams.class.isInstance(stv.getLayoutParams())) {
									RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams(width+offset, height, 2);
									stv.setLayoutParams(params);
								}								
			        			
							}
						} else if (offset < 0 && minwidth < width+offset) {
							for (View stv : mHScrollViewsBigMap.get(i+"")) {
								if (LinearLayout.LayoutParams.class.isInstance(stv.getLayoutParams())) {
									LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) getLayoutParams(width+offset, height, 1);
									stv.setLayoutParams(params);
								} else if (RelativeLayout.LayoutParams.class.isInstance(stv.getLayoutParams())) {
									RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams(width+offset, height, 2);
									stv.setLayoutParams(params);
								}
								
							}							
						}
												
					} else {
						//若点击的区域不在操作区域
						if (mHighLightTitleView != null) {
							setDragEnable(false, mHighLightTitleView);
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public boolean isDragEnable() {
		return mOnDragStatusListener == null ? false : mOnDragStatusListener.getDragStatus();
	}
	
	public void setSpecialIsDrag(boolean newStatus){
		mSpecialIsDrag = newStatus;
	}
	
	public void exitDragMode(){
		if (mHighLightTitleView != null) {
			setDragEnable(false, mHighLightTitleView);
			mHighLightKey = null;
		}
	}

	@Override
	public void setDragEnable(boolean isDrag, View touchView) {
		if (mSpecialIsDrag) {	
			//寻找view所在的List
			for (int i = 0; i < scrollViewIds.length; i++) {
				if (touchView != null && (touchView.getId() == scrollViewIds[i])) {
					if (mHScrollViewsBigMap.get(i+"") != null) {
						if (touchView == mHScrollViewsBigMap.get(i+"").get(0)) {
							mHighLightKey = i+"";
							mHighLightTitleView = touchView;
							if (mOnDragStatusListener != null) {
								mOnDragStatusListener.setDragStatus(isDrag);
							}
							if (!mTreeMode) {
								for (View stv : mHScrollViewsBigMap.get(i+"")) {
									if (isDragEnable()) {
										if (ScrollHorizontalWhiteTextView.class.isInstance(stv) || SHWhiteLinearLayout.class.isInstance(stv)) {
											stv.setBackgroundResource(R.drawable.drag_tree_title_bg);
										} else {
											stv.setBackgroundColor(Color.argb(70, 0xaa, 0xaa, 0xaa));
										}
									} else {
										mHighLightTitleView = null;
										stv.setBackground(null);								
									}
								}
							} else {
								//树形模式下，只要高亮或恢复第一行
								if (isDragEnable()) {
									if (ScrollHorizontalWhiteTextView.class.isInstance(mHighLightTitleView) || SHWhiteLinearLayout.class.isInstance(mHighLightTitleView)) {
										mHighLightTitleView.setBackgroundResource(R.drawable.drag_tree_title_bg);
									} else {
										mHighLightTitleView.setBackgroundColor(Color.argb(70, 0xaa, 0xaa, 0xaa));
									}
								} else {
									mHighLightTitleView.setBackground(null);
									mHighLightTitleView = null;									
								}
							}
							
							return;
						} 
					}
				}
			}
		}
	}
	
	public void setSPListener() {
		if (mOnBDLAgetSlidePaneListener != null) {
        	if (ScheduleSlidingPaneLayout.class.isInstance(mOnBDLAgetSlidePaneListener.getSlidePane())) {
        		((ScheduleSlidingPaneLayout) mOnBDLAgetSlidePaneListener.getSlidePane()).setOnSSPScrollHorizontalListener(this);
        	} 
        } 
	}

	@Override
	public boolean getIsDrag() {
		return isDragEnable();
	}
	
	public void addCHScrollView(ListView listView, View view) {    	
    	setSPListener();
        if (scrollViewIds != null) {
	        for (int i = 0; i < scrollViewIds.length; i++) {
	        	//获取单元格view
	        	View hsView = view.findViewById(scrollViewIds[i]);

	        	if (ScrollHorizontalTextView.class.isInstance(hsView) || SHLinearLayout.class.isInstance(hsView)) {
	        		//从BigMap里获取当前单列view的列表，没有的话就new一个
	        		List<View> shtvList = mHScrollViewsBigMap.get(i+"");
	        		if (shtvList == null) {
	        			shtvList = new ArrayList<View>();
	        		}
	        		//给单元格设置好监听
		            if (ScrollHorizontalTextView.class.isInstance(hsView)) {
		            	((ScrollHorizontalTextView) hsView).setScrollHorizontalChagnedListener(this);
		            } else if (SHLinearLayout.class.isInstance(hsView)){
		            	((SHLinearLayout) hsView).setScrollHorizontalChagnedListener(this);
		            }
		            //若是通过按钮新增的行内容，在此处同步宽度和高亮的颜色
	        		if (mHScrollViewsWidthBigMap.get(i+"") != null) {
	        			if (LinearLayout.LayoutParams.class.isInstance(hsView.getLayoutParams())) {
							LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) getLayoutParams(
									mHScrollViewsWidthBigMap.get(i+""), hsView.getLayoutParams().height, 1);
							hsView.setLayoutParams(params);
						} else if (RelativeLayout.LayoutParams.class.isInstance(hsView.getLayoutParams())) {
							RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams(
									mHScrollViewsWidthBigMap.get(i+""), hsView.getLayoutParams().height, 2);
							hsView.setLayoutParams(params);
						}
						if (isDragEnable() && mHighLightKey != null && mHighLightKey.equals(i+"")) {
							if (ScrollHorizontalWhiteTextView.class.isInstance(hsView) || SHWhiteLinearLayout.class.isInstance(hsView)) {
								hsView.setBackgroundResource(R.drawable.drag_tree_title_bg);
							} else {
								hsView.setBackgroundColor(Color.argb(70, 0xaa, 0xaa, 0xaa));
							}							
						} 	        			
	        		}
		            //把设置好的单元格加入到列表中，并放回到BigMap里
	        		shtvList.add(hsView);  
	    	        mHScrollViewsBigMap.put(i+"", shtvList);	        		
	        	} 
	        }
        }
        if (SHListView.class.isInstance(listView)) {
    		((SHListView) listView).setOnScrollHorizontalListener(this);
    	}
    }
	
	private LayoutParams getLayoutParams(int width, int height, int type) {
		if (type == 1) {
			return new LinearLayout.LayoutParams(width, height);
		} else {
			return new RelativeLayout.LayoutParams(width, height);
		}
		
	}
    
}
