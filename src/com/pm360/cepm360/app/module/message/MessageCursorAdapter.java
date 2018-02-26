
package com.pm360.cepm360.app.module.message;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.common.GLOBAL;

public class MessageCursorAdapter extends CursorAdapter {

    private String[] mTypes;
    private ContentObserver mContentObserver;
    private DataSetObserver mDataSetObserver;

    public MessageCursorAdapter(Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        mTypes = context.getResources().getStringArray(R.array.message_types);
        mContentObserver = new ContentObserver(new Handler()) {

            @SuppressWarnings("deprecation")
			@Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                getCursor().requery();
            }

        };
        mDataSetObserver = new DataSetObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                notifyDataSetChanged();
            }

        };
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView index = (TextView) view.findViewById(R.id.message_index);
        index.setText(cursor.getPosition() + 1 + "");
        TextView title = (TextView) view.findViewById(R.id.message_title);
        title.setText(cursor.getString(cursor.getColumnIndex("title")));
        TextView time = (TextView) view.findViewById(R.id.message_time);
        time.setText(cursor.getString(cursor.getColumnIndex("time")));
        TextView type = (TextView) view.findViewById(R.id.message_type);
        type.setText(mTypes[cursor.getInt(cursor.getColumnIndex("type")) - 1]);
        boolean is_read = cursor.getInt(cursor.getColumnIndex("is_read")) == Integer
                .valueOf(GLOBAL.MSG_READ[1][0]);
        index.setTextColor(is_read ? Color.GRAY : Color.BLACK);
        title.setTextColor(is_read ? Color.GRAY : Color.BLACK);
        time.setTextColor(is_read ? Color.GRAY : Color.BLACK);
        type.setTextColor(is_read ? Color.GRAY : Color.BLACK);
        
        if (cursor.getPosition() % 2 == 1) {
            view.setBackgroundResource(R.color.content_listview_single_line_bg);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
    }

    public void register() {
        getCursor().registerContentObserver(mContentObserver);
        getCursor().registerDataSetObserver(mDataSetObserver);
    }

    public void unregister() {
        getCursor().unregisterContentObserver(mContentObserver);
        getCursor().unregisterDataSetObserver(mDataSetObserver);
    }
}
