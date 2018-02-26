package com.pm360.cepm360.app.common.adpater;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pm360.cepm360.R;

public class SimpleListViewAdapter extends BaseAdapter {
    private String[] mItems;
    private LayoutInflater mInflater;
    private int mSelectedPosition = -1;
	
    public SimpleListViewAdapter(Context context, String[] items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }
    
    public void setDataList(String[] items) {
        mItems = items;
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
        TextView text;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.simple_listview_item, null);
            text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(text);
        }
        else {
        	text = (TextView) convertView.getTag();
        }
        text.setText(mItems[position]);
        text.setTextColor(Color.BLACK);
        regesterListener(text, position);
        
        if (mSelectedPosition == position) {
            convertView.setBackgroundResource(R.color.listview_selected_bg); 
        } else {
            if (position % 2 == 1) {
                convertView.setBackgroundResource(R.color.content_listview_single_line_bg);
            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }
        }

        return convertView;
	}
	
    private void regesterListener(TextView textView, final int position) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedPosition = position;
                notifyDataSetChanged();
            }
        });
    }
	
	public String getSelectedItem() {
	    if (mSelectedPosition == -1) return "";	        
	    return getItem(mSelectedPosition);
	}
}
