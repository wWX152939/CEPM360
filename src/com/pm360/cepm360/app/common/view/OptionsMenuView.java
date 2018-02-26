package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pm360.cepm360.R;

public class OptionsMenuView extends PopupWindow {
    private LinearLayout mMenuLayout;
    private SubMenuListener mListener;
    private final int BASE_TEXT_ID = 0x1100;
    private final int BASE_LINE_ID = 0x1200;
    private int mMenuLength;

    public static interface SubMenuListener {
        public void onSubMenuClick(View view);
    }
    
    public void setSubMenuListener(SubMenuListener listener) {
        mListener = listener;
    }

    public OptionsMenuView(View contentView) {
        super(contentView);
    }

    public OptionsMenuView(Context context, String[] subMenuName) {
        super(context);
        init(context, subMenuName);
    }

    public void setVisibility(boolean visible) {
    	if (visible) {
    		mMenuLayout.setVisibility(View.VISIBLE);
    	} else {
    		mMenuLayout.setVisibility(View.GONE);
    	}
    }
    
    @SuppressWarnings("deprecation")
	private void init(Context context, String[] subMenuName) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.options_menu_popup_layout, null);
        setContentView(contentView);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSubMenuClick(view);
            }
        };

        mMenuLayout = (LinearLayout) contentView
                .findViewById(R.id.options_menu_popup);
        
        mMenuLength = subMenuName.length;
        for (int i = 0; i < subMenuName.length; i++) {
            TextView submenu = new TextView(context);
            submenu.setTag(i);
            submenu.setId(BASE_TEXT_ID + i);
            submenu.setPadding(10, 0, 10, 0);
            submenu.setGravity(Gravity.CENTER_VERTICAL);
            submenu.setText(subMenuName[i]);
            submenu.setTextColor(Color.WHITE);
            submenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContentView().getResources().getDimension(R.dimen.sp18_s)); 
            submenu.setOnClickListener(clickListener);
            submenu.setBackgroundResource(R.drawable.actionbar_button_bg);
            mMenuLayout.addView(submenu, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));

            if (i != subMenuName.length - 1) {
                // Divider line
                TextView view = new TextView(context);
                view.setId(BASE_LINE_ID + i);
                view.setBackgroundResource(R.color.popup_divider);
                mMenuLayout.addView(view, new LinearLayout.LayoutParams(1, 20));
            }
        }
    }
    
    public void setVisibileMenu(int num, boolean visibility) {
    	if (visibility) {
			View tv = mMenuLayout.findViewById(BASE_TEXT_ID + num);
			if (tv != null) {
    			tv.setVisibility(View.VISIBLE);
			}

			View view = mMenuLayout.findViewById(BASE_LINE_ID + num - 1);
			if (view != null) {
    			view.setVisibility(View.VISIBLE);
			}
        	
    	} else {
			View tv = mMenuLayout.findViewById(BASE_TEXT_ID + num);
			if (tv != null) {
    			tv.setVisibility(View.GONE);
			}

			View view = mMenuLayout.findViewById(BASE_LINE_ID + num - 1);
			if (view != null) {
    			view.setVisibility(View.GONE);
			}
        	
    	}
    }
    
    public void setVisibileMenu(int[] numArray, boolean visibility) {
    	if (visibility) {
    		for (int i = 0; i < numArray.length; i++) {
    			View tv = mMenuLayout.findViewById(BASE_TEXT_ID + numArray[i]);
    			if (tv != null) {
        			tv.setVisibility(View.VISIBLE);
    			}
    			int viewId = BASE_LINE_ID + numArray[i];
    			if (numArray[i] == (mMenuLength - 1)) {
    				viewId -= 1;
    			}
    			View view = mMenuLayout.findViewById(viewId);
    			if (view != null) {
        			view.setVisibility(View.VISIBLE);
    			}
        	}	
    	} else {
    		for (int i = 0; i < numArray.length; i++) {
    			View tv = mMenuLayout.findViewById(BASE_TEXT_ID + numArray[i]);
    			if (tv != null) {
        			tv.setVisibility(View.GONE);
    			}
    			
    			int viewId = BASE_LINE_ID + numArray[i];
    			if (numArray[i] == (mMenuLength - 1)) {
    				viewId -= 1;
    			}
    			View view = mMenuLayout.findViewById(viewId);
    			if (view != null) {
        			view.setVisibility(View.GONE);
    			}
        	}
    	}
    	
    }
}
