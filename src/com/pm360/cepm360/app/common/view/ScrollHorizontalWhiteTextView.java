package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 
 * 标题: NavigationListView 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年3月16日
 *
 * @param <T>
 */
public class ScrollHorizontalWhiteTextView extends ScrollHorizontalTextView {
    
	public ScrollHorizontalWhiteTextView(Context context) {
		this(context, null);
	}

    public ScrollHorizontalWhiteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollHorizontalWhiteTextView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }
    

}
