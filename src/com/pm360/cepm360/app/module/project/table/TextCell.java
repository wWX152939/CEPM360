
package com.pm360.cepm360.app.module.project.table;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class TextCell {

    private String mCellValue = "";
    private String mHead;
    private int mWidth;
    private int mGravity = Gravity.LEFT;
    private int mBackgroundResId;
    private Drawable mCompoundDrawable;
    private boolean mFakeBoldText = false;

    private View.OnClickListener mListener;

    
    /**
     * 构造函数
     * @param cellValue value 
     * @param head Tag
     * @param width
     */
    public TextCell(String cellValue, String head, int width) {
        mCellValue = cellValue;
        mWidth = width;
        mHead = head;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    public String getCellValue() {
        return mCellValue;
    }

    public String getHead() {
        return mHead;
    }

    public int getWidth() {
        return mWidth;
    }

    public View createView(ViewGroup parent, int rowId, boolean isFolder, int textColor) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_table_text_view, parent, false);
        textView.setTextColor(textColor);
        if (isFolder) {
            String text = "<b>" + mCellValue + "</b>";
            textView.setText(Html.fromHtml(text));
        }
        else {
            textView.setText(mCellValue);
        }
        textView.getLayoutParams().width = UtilTools.dp2pxW(parent.getContext(), mWidth);
        textView.setTag(mHead);
        textView.setGravity(mGravity);
        if (mBackgroundResId > 0) {
            textView.setBackgroundResource(mBackgroundResId);
        }
        
        if (mCompoundDrawable != null) {
            textView.setCompoundDrawables(mCompoundDrawable, null, null, null);
        }
        
        if (mListener != null) {
            textView.setOnClickListener(mListener);
        }
        
        TextPaint textPaint = textView.getPaint();
        textPaint.setFakeBoldText(mFakeBoldText);
        return textView;
    }

    public View createLine(ViewGroup parent, int divierColor) {
        View line = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.project_table_line_v_view, parent, false);
        line.setBackgroundColor(divierColor);
        return line;
    }
    
    public void setBackground(int resId) {
        mBackgroundResId = resId;
    }
    
    /**
     * 此处默认添加在左边， 如需要添加其他位置，则需要再扩展此方法
     * @param left
     */
    public void setCompoundDrawables(Drawable left) {
        mCompoundDrawable = left;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public int getGravity() {
        return mGravity;
    }

    public boolean isFakeBoldText() {
        return mFakeBoldText;
    }

    public void setFakeBoldText(boolean fakeBoldText) {
        mFakeBoldText = fakeBoldText;
    }

}
