
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.pm360.cepm360.app.utils.UtilTools;

public class BaseSlidingPaneLayout extends ScheduleSlidingPaneLayout {

    public BaseSlidingPaneLayout(Context context) {
        this(context, null);
    }

    public BaseSlidingPaneLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void updateFloatingMenu(Context context,
            FloatingMenuView floatingMenu) {
        if (floatingMenu == null) return;
        ViewGroup group = (ViewGroup) floatingMenu.getParent();
        float offset = group.getMeasuredWidth()
                - UtilTools.dp2pxW(context, 16) // margin right 16
                - floatingMenu.getMeasuredWidth();
        if (isOpen()) {
            // navigation width is 116dp
            offset = offset - UtilTools.dp2pxW(context, 100);
        }
        floatingMenu.setX(offset);
    }

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}
    
    
}
