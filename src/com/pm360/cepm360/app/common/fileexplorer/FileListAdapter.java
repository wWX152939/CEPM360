package com.pm360.cepm360.app.common.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pm360.cepm360.R;

import java.util.List;

public class FileListAdapter extends ArrayAdapter<FileInfo> {
    private LayoutInflater mInflater;

    private FileViewInteractionHub mFileViewInteractionHub;

    private FileIconHelper mFileIcon;
    
//    private int mSelectedPosition = -1;

    public FileListAdapter(Context context, int resource, List<FileInfo> objects,
            FileViewInteractionHub f, FileIconHelper fileIcon) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mFileViewInteractionHub = f;
        mFileIcon = fileIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.file_browse_item, parent, false);
        }

        FileListItem listItem = (FileListItem) view;
        FileInfo lFileInfo = mFileViewInteractionHub.getItem(position);
        listItem.bind(lFileInfo, mFileViewInteractionHub, mFileIcon);
        
//        if (mSelectedPosition == position) {
//        	view.setBackgroundColor(Color.rgb(12, 175, 135));
//        } else {
//        	view.setBackgroundColor(Color.TRANSPARENT);
//        }

        return view;
    }
    
//    public void setSelected(int position) {
//    	mSelectedPosition = position;
//        notifyDataSetChanged();
//    }
}
