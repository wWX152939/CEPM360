
package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pm360.cepm360.R;

/*
 * popupWindow基类，布局文件在各自xml定义
 */
@SuppressLint("ResourceAsColor") public class BasePopupWindow {
	
	private Activity mActivity;
	private PopupWindow mPopupAttr;
	private String[] mTextViewNames;
	private TextView[] mTextViews;
	
	public final int emptyAttr = -1;
	
	public BasePopupWindow(Activity activity) {
		mActivity = activity;
	}
	
	public TextView[] getTextViews() {
		return mTextViews;
	}
	
	public PopupWindow getPopupWindow() {
		return mPopupAttr;
	}

	public void init(int arrayId) {

		if (emptyAttr == arrayId) {
			return;
		}
		
		final View popupView = mActivity.getLayoutInflater().inflate(
				R.layout.base_popup_window, null);
		
		LinearLayout popupLine = (LinearLayout)popupView.findViewById(R.id.popup_line);

		mPopupAttr = new PopupWindow(popupView);
		mPopupAttr.setWidth(LayoutParams.WRAP_CONTENT);
		mPopupAttr.setHeight(LayoutParams.WRAP_CONTENT);

		mPopupAttr.setOutsideTouchable(true);
		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
		mPopupAttr.setBackgroundDrawable(dw);

		mPopupAttr.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
		});	
		
		mTextViewNames = mActivity.getResources().getStringArray(arrayId);
		mTextViews = new TextView[mTextViewNames.length];
		for (int i = 0; i < mTextViewNames.length; i ++) {
			mTextViews[i] = new TextView(mActivity);
			mTextViews[i].setId(i);
			mTextViews[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp20_s));
			mTextViews[i].setText(mTextViewNames[i]);
			mTextViews[i].setTextColor(Color.WHITE);
			LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(  
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tvParams.setMargins(10, 0, 10, 0);
			tvParams.gravity = Gravity.CENTER;
			popupLine.addView(mTextViews[i], tvParams);
			
			if (i != (mTextViewNames.length - 1)) {
				View view = new View(mActivity);
				LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(  
						1, 25);
				view.setBackgroundColor(R.color.popup_divider);
				popupLine.addView(view, viewParams);
			}
			
		}
		
	}
}
