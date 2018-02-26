package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.entity.Expandable;

/**
 * 
 * 标题: DragView 
 * 描述: 拖拽ListView 设计风格，左侧，点击将事件下发给子View处理，右侧，事件将不分发给子View，长按拖动
 * 作者： onekey
 * 日期： 2015年12月23日
 *
 */
public class DragView<T extends Expandable> extends ListView {
	private ImageView imageView; // 被拖动的图片
	private int scaledTouchSlop; // 判断拖动的距离

	private int dragSrcPosition; // 手指在touch事件触摸时候的原始位置
	private int dragPosition; // 手指拖动列表项时候的位置

	private int dragPoint; // 手指点击位置在当前数据项item中的位置,只有Y坐标
	private int dragOffset; // 当前视图listview在屏幕中的位置，只有Y坐标

	private int upScrollBounce; // 向上滑动的边界
	private int downScrollBounce; // 拖动的时候向下滑动的边界

	private WindowManager windowManager = null; // 窗口管理类

    private ViewGroup itemView;
    
    // ActionDown记录坐标，时间
    private int mLastMotionX, mLastMotionY;
    private long mLastDownTime;
    
    private boolean isLongPress;

	private boolean DEBUG = false;
	
	private static int count = 0;
	
	private DragViewInterface<T> mDragViewInterface;
	
	// 窗口参数类
	private WindowManager.LayoutParams layoutParams = null;

	public DragView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// 触发移动事件的最小距离
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}
	
	/** 
     * * 判断是否有长按动作发生 * @param lastX 按下时X坐标 * @param lastY 按下时Y坐标 * 
     *  
     * @param thisX 
     *            移动时X坐标 * 
     * @param thisY 
     *            移动时Y坐标 * 
     * @param lastDownTime 
     *            按下时间 * 
     * @param thisEventTime 
     *            移动时间 * 
     * @param longPressTime 
     *            判断长按时间的阀值 
     */  
    boolean isLongPressed(float lastX, float lastY, float thisX,  
            float thisY, long lastDownTime, long thisEventTime,  
            long longPressTime) {  
        float offsetX = Math.abs(thisX - lastX);  
        float offsetY = Math.abs(thisY - lastY);  
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {  
            return true;  
        }  
        return false;  
    }  

	// 重写于absListView
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			isLongPress = false;
			// 获取的该touch事件的x坐标和y坐标，该坐标是相对于组件的左上角的位置
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			// 赋值手指点击时候的开始坐标
			dragSrcPosition = dragPosition = this.pointToPosition(x, y);
			// 如果点击在列表之外，也就是不允许的位置
			if (dragPosition == AdapterView.INVALID_POSITION) {
				// 直接执行父类，不做任何操作
				return super.onInterceptTouchEvent(ev);
			}
			
			/***
			 * 锁定手指touch的列表item, 参数为屏幕的touch坐标减去listview左上角的坐标
			 * 这里的getChildAt方法参数为相对于组件左上角坐标为00的情况 故有下面的这种参数算法
			 */
			itemView = (ViewGroup) this.getChildAt(dragPosition
					- this.getFirstVisiblePosition());
			/****
			 * 说明：getX Y为touch点相对于组件左上角的距离 getRawX 、Y 为touch点相对于屏幕左上角的距离
			 * 参考http://blog.csdn.net/love_world_/article/details/8164293
			 */
			// touch点的view相对于该childitem的top坐标的距离
			dragPoint = y - itemView.getTop();
			// 为距离屏幕左上角的Y减去距离组件左上角的Y，其实就是
			// 组件上方的view+标题栏+状态栏的Y
			dragOffset = (int) (ev.getRawY() - y);

			// 拿到拖动的imageview对象
			//TODO
			View drager = itemView.findViewById(mDragViewInterface.getViewId());

			// 左侧分发，右侧做处理
			if (drager != null && x > drager.getLeft() + UtilTools.dp2pxW(getContext(), mDragViewInterface.getSegmentationWidth())) {
				mLastMotionX = x;
				mLastMotionY = y;
				mLastDownTime = System.currentTimeMillis();
				return true;
			} else {
				return super.onInterceptTouchEvent(ev);
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	// 重写OnTouchEvent,触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (itemView != null) {
			View drager = itemView.findViewById(mDragViewInterface.getViewId());
			if (dragPosition != INVALID_POSITION) {
				int currentAction = ev.getAction();
				if (ev.getX() > drager.getLeft() && ev.getX() < drager.getRight()) {
					if (currentAction == MotionEvent.ACTION_MOVE) {
						if (!isLongPress && isLongPressed(mLastMotionX, mLastMotionY, ev.getX(), ev.getY(), mLastDownTime, System.currentTimeMillis(), 500)) {
							isLongPress = true;
							upScrollBounce = Math.min(mLastMotionY - scaledTouchSlop, getHeight() / 3);
							downScrollBounce = Math.max(mLastMotionY + scaledTouchSlop,
									getHeight() * 2 / 3);
							// 启用绘图缓存
							itemView.setDrawingCacheEnabled(true);
							// 根据图像缓存拿到对应位图
							Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
							startDrag(bm, mLastMotionY);

							itemView.setDrawingCacheEnabled(false);
						}
						if (isLongPress) {
							if (imageView != null) {
								int moveY = (int) ev.getY();
								onDrag(moveY);
							}
							return true;
						} else {
							return super.onTouchEvent(ev);
						}
					} else if (currentAction == MotionEvent.ACTION_UP) {
						if (isLongPress) {
							int upY = (int) ev.getY();
							// 还有一些操作
							stopDrag();
							onDrop(upY);
							return true;
						} else {
							return super.onTouchEvent(ev);
						}
					}
					
				} else {
					return super.onTouchEvent(ev);
				}
			}
		}
		
		// 决定了选中的效果
		return super.onTouchEvent(ev);
	}

	/****
	 * 准备拖动，初始化拖动时的影像，和一些window参数
	 * 
	 * @param bm
	 *            拖动缓存位图
	 * @param y
	 *            拖动之前touch的位置
	 */
	private void startDrag(Bitmap bm, int y) {
		stopDrag();
		layoutParams = new WindowManager.LayoutParams();
		// 设置重力
		layoutParams.gravity = Gravity.TOP;
		// 横轴坐标不变
		layoutParams.x = 0;
		/**
		 * 
		 * y轴坐标为 视图相对于自身左上角的Y-touch点在列表项中的y +视图相对于屏幕左上角的Y，= 该view相对于屏幕左上角的位置
		 */
		layoutParams.y = y - dragPoint + dragOffset;
		/****
		 * 宽度和高度都为wrapContent
		 */
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/****
		 * 设置该layout参数的一些flags参数
		 */
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		// 设置该window项是半透明的格式
		layoutParams.format = PixelFormat.TRANSLUCENT;
		// 设置没有动画
		layoutParams.windowAnimations = 0;

		// 配置一个影像ImageView
		ImageView imageViewForDragAni = new ImageView(getContext());
		imageViewForDragAni.setImageBitmap(bm);
		imageViewForDragAni.setBackgroundColor(0xFFCCFFFF);
		// 配置该windowManager
		windowManager = (WindowManager) this.getContext().getSystemService(
				"window");
		windowManager.addView(imageViewForDragAni, layoutParams);
		imageView = imageViewForDragAni;
	}

	/***
	 * 停止拖动，去掉拖动时候的影像
	 */
	private void stopDrag() {
		if (imageView != null) {
			windowManager.removeView(imageView);
			imageView = null;
		}
	}

	/****
	 * 拖动方法
	 * 
	 * @param y
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	private void onDrag(int y) {
		Log.i("wzw", "onDrag" + " dragPosition:" + dragPosition + " srcP:" + dragSrcPosition
				+ " F:" + getFirstVisiblePosition() + " L:" + getLastVisiblePosition()
				+ " getChildCount" + getChildCount() + " y:" + y);
		if (y < getChildAt(0).getBottom()) {
			y = getChildAt(0).getBottom();
		}
			
		if (y > getChildAt(getChildCount() - 1).getTop()) {
			y = getChildAt(getChildCount() - 1).getTop();
		}
		if (imageView != null) {
			// 透明度
			layoutParams.alpha = 0.8f;
			layoutParams.y = y - this.dragPoint + this.dragOffset;
			windowManager.updateViewLayout(imageView, layoutParams);
		}

		// 避免拖动到分割线返回-1
		int tempPosition = this.pointToPosition(0, y);
		if (tempPosition != this.INVALID_POSITION) {
			this.dragPosition = tempPosition;
		}

		// 滚动,通过position和y值，如position为8，y值为0，将把position为8的行拉到屏幕第一行
		int scrollHeight = 0;
		if (y < upScrollBounce) {
			scrollHeight = 8;// 定义向上滚动8个像素，如果可以向上滚动的话
		} else if (y > downScrollBounce) {
			scrollHeight = -8;// 定义向下滚动8个像素，，如果可以向上滚动的话
		}

		if (scrollHeight != 0) {
			// 真正滚动的方法setSelectionFromTop()
			if (getChildAt(dragPosition - getFirstVisiblePosition()) != null) {
				setSelectionFromTop(dragPosition,
						getChildAt(dragPosition - getFirstVisiblePosition())
								.getTop() + scrollHeight);
			}
			if (DEBUG)
				Log.i("wzw", "y:" + y + " scrollHeight:" + scrollHeight
						+ " dragPosition:" + dragPosition + " upScrollBounce:"
						+ upScrollBounce + " downScrollBounce:"
						+ downScrollBounce + " scaledTouchSlop:"
						+ scaledTouchSlop);

		}

		// 数据交换
		if (dragPosition > 0 && dragPosition < getAdapter().getCount() && (dragSrcPosition != dragPosition)) {
			
			DataTreeListAdapter adapter = mDragViewInterface.getListAdapter();
			// 原始位置的item
			Log.i("wzw", "dragPosition:" + dragPosition + " dragSrcPosition:"
					+ dragSrcPosition);
			//TODO
			T lastDragItem = (T) adapter.getItem(dragSrcPosition);
			T dragItem = (T) adapter.getItem(dragPosition);
			if (!lastDragItem.isHas_child()) {
				if (dragItem.isHas_child()) {
					count++;
					if (count == 46) {
						count = 0;
						if (lastDragItem.getParents_id() != dragItem.getId()) {
							adapter.updateListView(dragPosition);
						}
					}
						
				} else {
					dragSrcPosition = dragPosition;
					lastDragItem.setParentsId(dragItem.getParents_id());
					lastDragItem.setLevel(dragItem.getLevel());
					mDragViewInterface.sortPeerId(lastDragItem, dragItem);
					
					adapter.getShowList().remove(lastDragItem);
					adapter.getShowList().add(dragPosition, lastDragItem);
					int insertPosition = adapter.getDataList().indexOf(dragItem);
					adapter.getDataList().remove(lastDragItem);
					adapter.getDataList().add(insertPosition, lastDragItem);
					adapter.notifyDataSetChanged();
				}
				
			} else {
				
			}
		}

	}

	/***
	 * 拖动放下的时候 param : y
	 */
	private void onDrop(int y) {
	}
	
	public void setDragViewInterface(DragViewInterface<T> dragViewInterface) {
		mDragViewInterface = dragViewInterface;
	}
	
	public interface DragViewInterface<T> {
		// 给出需要移动的View ID
		public int getViewId();
		// 给出基于1280x736 213dpi屏幕的dip(需要移动的View的宽度)
		public int getSegmentationWidth();
		public DataTreeListAdapter<?> getListAdapter();
		public void sortPeerId(T lastDragItem, T dragItem);
	}
}
