
package com.pm360.cepm360.app.common.adpater;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;

public class ProjectSelectCursorAdapter extends CursorAdapter {

    @SuppressWarnings("deprecation")
	public ProjectSelectCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.project_select_title);
        ImageView iv = (ImageView) view.findViewById(R.id.project_select_checkbox);
        tv.setText(cursor.getString(cursor.getColumnIndex("project_name")));
        String compare = cursor.getString(cursor.getColumnIndex("project_compare"));
        iv.setBackgroundResource(compare.equals("compare") ? R.drawable.project_checked
                : R.drawable.project_unchecked);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_select_list_item, parent,
                false);
        return view;
    }
}
