
package com.pm360.cepm360.app.module.project.table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class ListCell extends TextCell {

    private ListItem[] mCellValues;

    public ListCell(ListItem[] cellValues, String head, int width) {
        super("", head, width);
        mCellValues = cellValues;
    }

    @Override
    public View createView(ViewGroup parent, int rowId, boolean isFolder, int textColor) {
        LinearLayout rootView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_table_list_item,
                        parent, false);
        LinearLayout itemCustom = (LinearLayout) rootView.findViewById(R.id.custome_item);
        itemCustom.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < mCellValues.length; i++) {
            View child = mCellValues[i].itemCell.createView(itemCustom, mCellValues[i].rowId,
                    false, textColor);
            itemCustom.addView(child);
            if (i != mCellValues.length - 1)
                itemCustom.addView(createHLine(itemCustom));
        }
        itemCustom.getLayoutParams().height = UtilTools.dp2pxH(parent.getContext(),
                mCellValues.length * 37 - 1);
        rootView.getLayoutParams().width = UtilTools.dp2pxW(parent.getContext(), getWidth());
        rootView.setTag(getHead());
        return rootView;
    }

    public ListItem[] getCellValues() {
        return mCellValues;
    }

    public View createHLine(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(
                R.layout.project_table_line_h_view, parent, false);
    }

    public static class ListItem {
        private int rowId;
        private TextCell itemCell;

        public ListItem(int rowId, TextCell itemCell) {
            this.rowId = rowId;
            this.itemCell = itemCell;
        }

        public TextCell getItemCell() {
            return itemCell;
        }

    }
}
