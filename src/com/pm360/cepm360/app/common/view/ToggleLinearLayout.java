
package com.pm360.cepm360.app.common.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

public class ToggleLinearLayout extends LinearLayout {

    private boolean mIsOpen = false;
    private ObjectAnimator mAnimator;

    public ToggleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ToggleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleLinearLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofFloat(this,
                    "pMHeight", 0, getMeasuredHeight());
            mAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mAnimator.setRepeatCount(0);
            mAnimator.setInterpolator(new AccelerateInterpolator());
            mAnimator.setDuration(500);
            handler.sendEmptyMessage(0);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getLayoutParams().height = msg.what;
            requestLayout();
        }
    };

    public void setPMHeight(float height) {
        handler.sendEmptyMessage((int) height);
    }

    public void toggle() {
        if (mAnimator == null)
            return;
        if (mIsOpen) {
            mAnimator.reverse();
        } else {
            mAnimator.start();
        }
        mIsOpen = !mIsOpen;
    }
}
