
package com.pm360.cepm360.app.module.project.table;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class CheckBoxCell extends TextCell {

    public static final int DEFAULT_WIDTH = 36;
    private boolean mChecked;

    public CheckBoxCell(String cellValue, String head, int width, boolean checked) {
        super(cellValue, head, width);
        mChecked = checked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public View createView(ViewGroup parent, final int rowId, boolean isFolder, int textColor) {
        View child = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.project_table_check_box, parent, false);
        CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox);
        TextView textView = (TextView) child.findViewById(R.id.text);
        textView.setTextColor(textColor);
        checkBox.setChecked(mChecked);
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
