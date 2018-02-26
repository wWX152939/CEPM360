package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.pm360.cepm360.R;

public class LineProgressBar extends View {
    
    protected Context mContext;
    private RectF mRectF;
    private Typeface mTypeface;
    private Paint mForegroundPaint, mBackgroundPaint, mTextPaint;
    private int mProgress;
    private int MAX_PROGRESS = 100;
    private int mBackgroundColor = Color.WHITE;
    private int mTextColor = Color.WHITE;
    private int mBarColor = Color.RED;    
    private int mHeight, mWidth;
    private float mTextSize = 20f;
    private float mBgStrokeWidth = 4f; 
    private float mStrokeWidth = 5f;
    private boolean isRoundEdge;
    private boolean isShadowed;
    
    private static final String TYPEFACE_PATH = "fonts/Roboto-Light.ttf";

    public LineProgressBar(Context context) {
        super(context);
        mContext = context;
    }

    public LineProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initTypedArray(context, attrs);
        init(context);
    }

    public LineProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setProgress(int progress) {
        setProgressInView(progress);
    }
    
    public void setRoundEdge(boolean isRoundEdge) {
        this.isRoundEdge = isRoundEdge;
        init(mContext);
    }
    
    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.LineProgressBar, 0, 0);
        try {
            mProgress = (int) typedArray.getFloat(
                    R.styleable.LineProgressBar_current_progress, mProgress);
            mStrokeWidth = typedArray.getDimension(
                    R.styleable.LineProgressBar_stroke_width, mStrokeWidth);
            mBgStrokeWidth = typedArray.getDimension(
                    R.styleable.LineProgressBar_background_stroke_width,
                    mBgStrokeWidth);
            mBarColor = typedArray.getInt(
                    R.styleable.LineProgressBar_progress_color, mBarColor);
            mBackgroundColor = typedArray.getInt(
                    R.styleable.LineProgressBar_background_color, mBackgroundColor);
            mTextColor = typedArray.getInt(
                    R.styleable.LineProgressBar_text_color, mTextColor);
            mTextSize = typedArray.getDimension(
                    R.styleable.LineProgressBar_text_size, mTextSize);

        } finally {
            typedArray.recycle();
        }
        this.setLayerType(LAYER_TYPE_SOFTWARE, null);

        mTypeface = Typeface.createFromAsset(context.getAssets(), TYPEFACE_PATH);

    }
    
    private void init(Context context) {       
        mProgress = 0;
        initBackgroundColor();
        initForegroundColor();
        initTextColor();
        mRectF = new RectF();
    }
    
    private synchronized void setProgressInView(int progress) {
        mProgress = (progress <= MAX_PROGRESS) ? progress : MAX_PROGRESS;
        invalidate();
    }
    
    private void initBackgroundColor() {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mBgStrokeWidth);
        if (isRoundEdge) {
            mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        if (isShadowed) {
            mBackgroundPaint.setShadowLayer(3.0f, 0.0f, 2.0f, getResources().getColor(R.color.shader));
        }
    }
    
    private void initForegroundColor() {
        mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mForegroundPaint.setColor(mBarColor);
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setStrokeWidth(mStrokeWidth);
        if (isRoundEdge) {
            mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        if (isShadowed) {
            mForegroundPaint.setShadowLayer(3.0f, 0.0f, 2.0f, getResources().getColor(R.color.shader));
        }
    }
    
    private void initTextColor() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setStrokeWidth(1f);
        mTextPaint.setTextSize(mTextSize);
        mTypeface = Typeface.createFromAsset(mContext.getAssets(), TYPEFACE_PATH);
        mTextPaint.setTypeface(mTypeface);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRects(canvas);
    }
    
    private void drawRects(Canvas canvas) {
        int nMiddle = mHeight / 2;
        Rect bounds = new Rect();
        String text = "" + mProgress + "%";
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        float mt = mTextPaint.measureText(text) + 20 + text.length();

        int progressX = (int) ((mWidth - mt) * mProgress / MAX_PROGRESS);

        mRectF.left = getPaddingLeft();
        mRectF.top = nMiddle;
        mRectF.right = progressX;
        mRectF.bottom = nMiddle;
        if (mProgress > 2)
            canvas.drawRect(mRectF, mForegroundPaint);


        if (mProgress < MAX_PROGRESS)
            canvas.drawRect(mRectF.width() + mt, nMiddle, mWidth - getPaddingRight(), nMiddle, mBackgroundPaint);

        canvas.drawText(text, progressX + 10, nMiddle + mBgStrokeWidth,
                mTextPaint);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        mWidth = (int) (w - xpad);
        mHeight = (int) (h - ypad);
        setMeasuredDimension(mWidth, mHeight);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setPadding(20, 0, 0, 0);
    }
}
