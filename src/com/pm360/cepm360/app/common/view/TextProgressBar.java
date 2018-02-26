
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.pm360.cepm360.R;

public class TextProgressBar extends ProgressBar {

    private Paint mPaint;
    private Rect mRect = new Rect();

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CepmView);
        int textColor = a.getColor(R.styleable.CepmView_textColor, Color.WHITE);
        a.recycle();
        mPaint = new Paint();
        mPaint.setColor(textColor);
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.sp18_s)); 
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    public TextProgressBar(Context context) {
        super(context);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String text = getProgress() + "%";
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        int x = (getWidth() / 2) - mRect.centerX();
        int y = (getHeight() / 2) - mRect.centerY();
        canvas.drawText(text, x, y, mPaint);
    }

    public void setTextColor(int textColor) {
        mPaint.setColor(textColor);
    }

}
