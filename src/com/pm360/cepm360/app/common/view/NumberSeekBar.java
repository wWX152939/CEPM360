package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.pm360.cepm360.R;
import com.pm360.cepm360.common.util.LogUtil;

/**
 * 带数字的滚动条
 * @author yuanlu
 *
 */
public class NumberSeekBar extends SeekBar {
    
    private int mOldPaddingTop;
    private int mOldPaddingLeft;
    private int mOldPaddingRight;
    private int mOldPaddingBottom;
    
    private boolean isMysetPadding = true;
    
    private float mImageWidth;
    private float mImageHeigth;
    
    private Paint mPaint;
    
    private Bitmap mBitmap;
    
    private int mTextSize = 17;
    
    private int mTextPaddingLeft;
    private int mTextPaddingTop;
    
    private int mImagePaddingLeft;
    private int mImagePaddingTop;
    
    public NumberSeekBar(Context context) {
        super(context);
        init();
    }
    
    public NumberSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public NumberSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public int getTextpaddingleft() {
        return mTextPaddingLeft;
    }
    
    public int getTextpaddingtop() {
        return mTextPaddingTop;
    }
    
    public int getImagepaddingleft() {
        return mImagePaddingLeft;
    }
    
    public int getImagepaddingtop() {
        return mImagePaddingTop;
    }
    
    public int getTextsize() {
        return mTextSize;
    }
    
    // 修改setpadding 使其在外部调用的时候无效
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (isMysetPadding) {
            super.setPadding(left, top, right, bottom);
        }
    }
    
    // 初始化
    private void init() {
        initBitmap();
        initDraw();
        setPadding();
    }
    
    /**
     * 初始化位图
     */
    private void initBitmap() {
        setBitmap(R.drawable.popwindow_bg);
    }
    
    /**
     * 初始化画笔
     */
    private void initDraw() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(0xff23fc4f);
    }
    
    /**
     * 绘画方法定义
     */
    protected synchronized void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
            String text = (getProgress() * 100 / getMax()) + "%";
            float textWidth = mPaint.measureText(text);
            Rect bounds = this.getProgressDrawable().getBounds();
            
            float xImg = bounds.width() * getProgress() / getMax() 
            					+ mImagePaddingLeft + mOldPaddingLeft;
            float yImg = mImagePaddingTop + mOldPaddingTop;
            
            float xText = bounds.width() * getProgress() / getMax() + mImageWidth / 2 
            				- textWidth / 2 + mTextPaddingLeft + mOldPaddingLeft;
            float yText = yImg + mTextPaddingTop + mImageHeigth / 2 + getTextHei() / 4;
            
            canvas.drawBitmap(mBitmap, xImg, yImg, mPaint);
            canvas.drawText(text, xText, yText, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 初始化padding 使其左右上 留下位置用于展示进度图片
    private void setPadding() {
        int top = getCeilValue(mImageHeigth) + mOldPaddingTop;
        int left = getCeilValue(mImageWidth) / 2 + mOldPaddingLeft;
        int right = getCeilValue(mImageWidth) / 2 + mOldPaddingRight;
        int bottom = mOldPaddingBottom;
        
        LogUtil.e("top=" + top + " left=" + left + " right=" + right + " bottom" + bottom);
        isMysetPadding = true;
        setPadding(left, top, right, bottom);
        isMysetPadding = false;
    }
    
    /**
     * 设置展示进度背景图片
     * 
     * @param resid
     */
    public void setBitmap(int resid) {
    	mBitmap = BitmapFactory.decodeResource(getResources(), resid);
    	
        if (mBitmap != null) {
            mImageWidth = mBitmap.getWidth();
            mImageHeigth = mBitmap.getHeight();
        } else {
            mImageWidth = 0;
            mImageHeigth = 0;
        }
        
        setPadding();
    }
    
    /**
     * 替代setpadding
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setMyPadding(int left, int top, int right, int bottom) {
        mOldPaddingTop = top;
        mOldPaddingLeft = left;
        mOldPaddingRight = right;
        mOldPaddingBottom = bottom;
        
        isMysetPadding = true;
        setPadding(left + getCeilValue(mImageWidth) / 2,
        			top + getCeilValue(mImageHeigth),
        			right + getCeilValue(mImageWidth) / 2,
        			bottom);
        isMysetPadding = false;
    }
    
    /**
     * 设置进度字体大小
     * 
     * @param mTextSize
     */
    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mPaint.setTextSize(mTextSize);
    }
    
    /**
     * 设置进度字体颜色
     * 
     * @param color
     */
    public void setTextColor(int color) {
        mPaint.setColor(color);
    }
    
    /**
     * 调整进度字体的位置 初始位置为图片的正中央
     * 
     * @param top
     * @param left
     */
    public void setTextPadding(int top, int left) {
        this.mTextPaddingLeft = left;
        this.mTextPaddingTop = top;
    }
    
    /**
     * 调整进图背景图的位置 初始位置为进度条正上方、偏左一半
     * 
     * @param top
     * @param left
     */
    public void setImagePadding(int top, int left) {
        this.mImagePaddingLeft = left;
        this.mImagePaddingTop = top;
    }
    
    private int getCeilValue(float value) {
        return (int) Math.ceil(value);
    }
    
    private float getTextHei() {
        FontMetrics fm = mPaint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.top) + 2;
    }
    
}
