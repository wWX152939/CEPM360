package com.pm360.cepm360.app.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.pm360.cepm360.R;

public class CustomProgressBar extends ProgressBar {
	String text;
    Paint mPaint;
    
    private static final String TYPEFACE_PATH = "fonts/Roboto-Light.ttf";
  
    public CustomProgressBar(Context context) {
    	this(context, null, 0);
    }  
  
    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);   
        initText(context);
    }
  
    @Override  
    public synchronized void setProgress(int progress) {  
        setText(progress);  
        super.setProgress(progress); 
    }  
  
    @SuppressLint("DrawAllocation") 
    @Override  
    protected synchronized void onDraw(Canvas canvas) {  
        super.onDraw(canvas);
        Rect rect = new Rect();  
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);  
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);  
    }
  
    /**
     * 初始化画笔
     */
    private void initText(Context context) {
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mPaint.setStrokeWidth(1f);        
        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setTextSize(getResources().getDimension(R.dimen.sp16_s));
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), TYPEFACE_PATH);
        this.mPaint.setTypeface(typeface);
    }  
  
    /**
     * 设置文字内容
     * 
     * @param progress
     */
    private void setText(int progress) {  ;  
        this.text = String.valueOf(progress) + "%";  
    }  
}
