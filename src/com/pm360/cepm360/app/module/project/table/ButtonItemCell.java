
package com.pm360.cepm360.app.module.project.table;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class ButtonItemCell extends TextCell {

    public static final int DEFAULT_WIDTH = 48;
    private int mResId;
    private View.OnClickListener mListener;

    public ButtonItemCell(String cellValue, String head, int width, int resId) {
        super(cellValue, head, width);
        mResId = resId;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public View createView(ViewGroup parent, int rowId, boolean isFolder, int textColor) {
        View child = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.project_table_image_button, parent, false);
        ImageButton imageButton = (ImageButton) child.findViewById(R.id.img);
        TextView textView = (TextView) child.findViewById(R.id.text);
        textView.setTextColor(textColor);
        imageButton.setImageResource(mResId);
        if (mListener != null)
            imageButton.setOnClickListener(mListener);
        if (isFolder) {
            textView.setTextColor(Color.BLACK);
            String text = "<b>" + getCellValue() + "</b>";
            textView.setText(Html.fromHtml(text));
        }
        else {
            textView.setText(getCellValue());
        }
        child.getLayoutParams().width = UtilTools.dp2pxW(parent.getContext(), getWidth());
        child.setTag(getHead());
        return child;
    }
}
