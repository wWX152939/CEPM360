
package com.pm360.cepm360.app.module.project.table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class CheckBoxGroupCell extends TextCell implements CompoundButton.OnCheckedChangeListener {

    private CheckBoxItem[] mCellValues;

    public CheckBoxGroupCell(CheckBoxItem[] cellValues, String head, int width) {
        super("", head, width);
        mCellValues = cellValues;
    }

    @Override
    public View createView(ViewGroup parent, int rowId, boolean isFolder, int textColor) {
        LinearLayout rootView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_table_list_item,
                        parent, false);
        LinearLayout itemCustom = (LinearLayout) rootView.findViewById(R.id.custome_item);
        for (CheckBoxItem item : mCellValues) {
            View child = createChildView(itemCustom, item, false, textColor);
            itemCustom.addView(child);
        }
        rootView.getLayoutParams().width = UtilTools.dp2pxW(parent.getContext(), getWidth());
        rootView.setTag(getHead());
        return rootView;
    }

    private View createChildView(ViewGroup parent, CheckBoxItem cellValue, boolean checked,
            int textColor) {
        View child = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.project_table_checkbox_group_item, parent, false);
        TextView textView = (TextView) child.findViewById(R.id.text);
        CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox);
        textView.setTextColor(textColor);
        textView.setText(cellValue.getTitle());
        checkBox.setChecked(cellValue.isChecked());
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setTag(cellValue);
        return child;
    }

    public String getCellValues() {
        StringBuilder builder = new StringBuilder();
        for (CheckBoxItem item : mCellValues) {
            if (item.isChecked()) {
                builder.append(item.getCode() + ",");
            }
        }
        String res = builder.toString();
        if (res.equals("")) {
            res = "0";
        } else if (res.endsWith(",")) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

    public static class CheckBoxItem {
        private String code;
        private String title;
        private boolean checked;

        public CheckBoxItem(String code, String title, boolean checked) {
            this.code = code;
            this.title = title;
            this.checked = checked;
        }

        public String getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CheckBoxItem item = (CheckBoxItem) buttonView.getTag();
        item.setChecked(isChecked);
    }
}
