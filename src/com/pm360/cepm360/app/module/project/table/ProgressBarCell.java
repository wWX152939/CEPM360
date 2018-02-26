
package com.pm360.cepm360.app.module.project.table;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.TextProgressBar;
import com.pm360.cepm360.app.utils.UtilTools;

public class ProgressBarCell extends TextCell {

    private int mProgress;

    public ProgressBarCell(int progress, String head, int width) {
        super(progress + "%", head, width);
        mProgress = progress;
    }

    @Override
    public View createView(ViewGroup parent, final int rowId, boolean isFolder, int textColor) {
        TextProgressBar child = (TextProgressBar) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.project_table_progressbar, parent, false);
        child.setTextColor(textColor);
        child.setProgress(mProgress);
        child.getLayoutParams().width = UtilTools.dp2pxW(parent.getContext(), getWidth());
        return child;
    }
}
