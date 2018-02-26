
package com.pm360.cepm360.app.module.project.table;

import java.util.List;

public class TableRow {
    private int mRowId;
    private List<TextCell> mRowValues;
    private boolean mIsFolder;
    private boolean mChecked;
    private int mRowHeight;

    public TableRow(int rowId, List<TextCell> rowValues, boolean isFolder, int rowHeight) {
        mRowId = rowId;
        mRowValues = rowValues;
        mIsFolder = isFolder;
        mRowHeight = rowHeight;
    }

    public void setRowId(int rowId) {
        mRowId = rowId;
    }

    public int getRowId() {
        return mRowId;
    }

    public TextCell getValueAt(int position) {
        return mRowValues.get(position);
    }

    public int getValueSize() {
        return mRowValues.size();
    }

    public void setRowValues(List<TextCell> rowValues) {
        mRowValues = rowValues;
    }

    public boolean isFolder() {
        return mIsFolder;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public int getRowHeight() {
        return mRowHeight;
    }

}
