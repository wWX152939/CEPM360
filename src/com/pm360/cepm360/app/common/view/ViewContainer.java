package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pm360.cepm360.app.utils.UtilTools;

public class ViewContainer extends ViewGroup {
    
    private int mWidth;//组件宽
    private int mHeight;//组件高
    private int mChildWidth = 95;//子控件宽
    private int mChildHeight = 34;//子控件高
    private int mOneRowCount; //每行子控件数量
    
    private final int mChildMarginLeft = UtilTools.dp2pxW(getContext(), 4);//子控件相对左边控件的距离
    private final int mChildMarginHorizonTal = UtilTools.dp2pxW(getContext(), 4);//子控件相对最左、最右的距离
    private final int mChildMarginTop = UtilTools.dp2pxH(getContext(), 4);//子控件相对顶部控件的距离

    public ViewContainer(Context context) {       
        super(context);
    }

    public ViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setChildSize(int width, int height) {
        mChildWidth = UtilTools.dp2pxW(getContext(), width);
        mChildHeight = UtilTools.dp2pxW(getContext(), height);
    }

    /**
     * 控制子控件的换行
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /*
         * 遍历所有子控件，并设置它们的位置和大小 每行只能有三个子控件，且高度固定，宽度相同，且每行正好布满
         */
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);// 得到当前子控件            
            childView.layout((i % mOneRowCount) * mChildWidth + (i % mOneRowCount + 1) * mChildMarginLeft,
                    (i / mOneRowCount) * mChildHeight + mChildMarginHorizonTal + (i / mOneRowCount) * mChildMarginTop,
                    (i % mOneRowCount + 1) * mChildWidth + (i % mOneRowCount + 1) * mChildMarginLeft,
                    (i / mOneRowCount + 1) * mChildHeight + mChildMarginHorizonTal + (i / mOneRowCount) * mChildMarginTop);
            
            if (childView instanceof TextView) {
                ((TextView) childView).setGravity(Gravity.CENTER);
            }
        }
    }

    /**
     * 计算控件及子控件所占区域
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int count = getChildCount();// 得到子控件数量
        if (count > 0) {
            mOneRowCount = (mWidth - mChildMarginLeft) / (mChildWidth + mChildMarginLeft);
            // 根据子控件的高和子控件数目得到自身的高
            mHeight = mChildHeight
                    * ((count - 1) / mOneRowCount + 1)
                    + mChildMarginHorizonTal * 2 + mChildMarginTop
                    * ((count - 1) / mOneRowCount);
        } else {
            // 如果木有子控件，自身高度为0，即不显示
            mHeight = 0;
        }
        // 根据自身的宽度约束子控件宽度
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 设置自身宽度
        setMeasuredDimension(mWidth, mHeight);
    }
}
