
package com.pm360.cepm360.app.module.project.table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class ImageCell extends TextCell {
    private int mResId;
    private int mImageWidth, mImageHeight;

    public ImageCell(String cellValue, String head, int width, int resId, int imageWidth,
            int imageHeight) {
        super(cellValue, head, width);
        mResId = resId;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
    }

    @Override
    public View createView(ViewGroup parent, final int rowId, boolean isFolder, int textColor) {
        View child = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.project_table_image_view, parent, false);
        ImageView imageView = (ImageView) child.findViewById(R.id.image_view);
        imageView.setImageResource(mResId);
        imageView.getLayoutParams().width = mImageWidth;
        imageView.getLayoutParams().height = mImageHeight;
        child.getLayoutParams().width = UtilTools.dp2pxW(parent.getContext(), getWidth());
        child.setTag(getHead());
        return child;
    }
}
