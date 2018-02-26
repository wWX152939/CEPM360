package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView; 
/**
 * 
 * 标题: AlwaysMarqueeTextView 
 * 描述: 标题一直滚动
 * 作者： 陈春亮
 * 日期： 2016年3月16日
 *
 * @param <T>
 */ 
public class AlwaysMarqueeTextView extends TextView {  
	
	public AlwaysMarqueeTextView(Context context) {  
	 super(context);  
	}  
	
	public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {  
	 super(context, attrs);  
	}  
	
	public AlwaysMarqueeTextView(Context context, AttributeSet attrs,  
	  int defStyle) {  
	 super(context, attrs, defStyle);  
	}  
	
	@Override  
	public boolean isFocused() {  
		return true;  
	}  
}  