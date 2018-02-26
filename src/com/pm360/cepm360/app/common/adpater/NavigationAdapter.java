
package com.pm360.cepm360.app.common.adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationAdapter extends BaseAdapter {

	private int [] mIcons;
    private String[] mItems;
    private LayoutInflater mInflater;
    private List<Integer> mSelectedPosition = new ArrayList<Integer>();
    
    public NavigationAdapter(Context context, String[] items) {
        this(context, null, items);
    }

    /**
     * 
     * @param context
     * @param normalIcons 正常图标 资源数组
     * @param selectedIcons 选中图标 资源数组
     * @param items 标签名称
     */
    public NavigationAdapter(Context context, int[] icons, String[] items) {
        mInflater = LayoutInflater.from(context);
        mIcons = icons;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.length;
    }

    @Override
    public String getItem(int position) {
        if (position == -1)
            return "home";
        return mItems == null ? null : mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams") @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (mIcons != null) {
            	convertView = mInflater.inflate(R.layout.navigation_item, null);
            	holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            } else {
            	convertView = mInflater.inflate(R.layout.navigation_item2, null);
            }
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mIcons != null) {
        	holder.icon.setImageResource(mIcons[position]);
        }
        holder.title.setText(mItems[position]);
        
        if (mSelectedPosition.contains((Integer) position)) {
            holder.title.setTextColor(Color.parseColor("#2c9bed"));
            if (mIcons.length > mItems.length) {
                holder.icon.setImageResource(mIcons[position + mItems.length]);
            } else {
                holder.icon.setImageResource(mIcons[position]);
            }
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.icon.setImageResource(mIcons[position]);
        }
        
        return convertView;
    }
    
    public void setSelected(int position) {
        mSelectedPosition.clear();
        mSelectedPosition.add(position);
        notifyDataSetChanged();
    }

    public class ViewHolder {
    	ImageView icon;
        TextView title;
    }
}
