
package com.pm360.cepm360.app.module.project.table;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pm360.cepm360.R;

public class ExpandCell extends TextCell {

    public static final int DEFAULT_IMAGE_WIDTH = 60;
    private boolean mExpanded;
    private int mExpandId;
    private int mParentId;
    private boolean mIsFolder;
    private int mLevel;
    private boolean mHasChild;

    public ExpandCell(String cellValue, String head, int width, int expandId, int parentId,
            boolean isFolder) {
        super(cellValue, head, width);
        mExpandId = expandId;
        mParentId = parentId;
        mIsFolder = isFolder;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public int getExpandId() {
        return mExpandId;
    }

    public int getParentId() {
        return mParentId;
    }

    public boolean isFolder() {
        return mIsFolder;
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    public boolean getExpanded() {
        return mExpanded;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setHasChild(boolean hasChild) {
        mHasChild = hasChild;
    }

    @Override
    public View createView(ViewGroup parent, int rowId, boolean isFolder, int textColor) {
        TextView child = (TextView) super.createView(parent, rowId, isFolder, textColor);
        if (mIsFolder) {
            String text = "<b>" + getCellValue() + "</b>";
            child.setText(Html.fromHtml(text));

            Drawable drawableLeft = parent.getContext().getResources().getDrawable(
                    mExpanded ? R.drawable.item_expand_1 : R.drawable.item_collapse_2);
            drawableLeft.setBounds(0, 0, 50, 22);
            if (mHasChild) {
                child.setCompoundDrawables(drawableLeft, null, null, null);
            } else {
                Drawable drawableIcon = parent.getContext().getResources().getDrawable(R.drawable.eps_menu_2);
                drawableIcon.setBounds(0, 0, 50, 22);
                child.setCompoundDrawables(drawableIcon, null, null, null);
            }

        }
        child.setGravity(Gravity.LEFT);
        child.setPadding(20 * mLevel, 0, 0, 0);
        return child;
    }
}
