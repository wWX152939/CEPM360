
package com.pm360.cepm360.app.common.adpater;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;

public class ProjectTypeListAdapter extends BaseAdapter {

    private String[] mItems;
    private LayoutInflater mInflater;
    private int mSelectedPosition = -1;

    public ProjectTypeListAdapter(Context context, String[] items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.length;
    }

    @Override
    public String getItem(int position) {
        return mItems == null ? null : mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.project_type_list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.project_type_title);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(mItems[position]);
        if (position == mSelectedPosition)
            // holder.layout.setBackgroundColor(Color.rgb(219, 238, 244));
        	convertView.setBackgroundResource(R.color.touch_high_light);
        else
            convertView.setBackgroundColor(Color.rgb(48, 52, 78));
        return convertView;
    }

    public class ViewHolder {
        TextView title;
        ImageView arrow;
    }
}
