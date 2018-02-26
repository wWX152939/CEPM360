/*
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */

package com.pm360.cepm360.app.common.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.pm360.cepm360.R;

import java.util.List;

public class FavoriteListAdapter extends ArrayAdapter<FavoriteItem> {
    private Context mContext;

    private LayoutInflater mInflater;

    private FileIconHelper mFileIcon;

    public FavoriteListAdapter(Context context, int resource, List<FavoriteItem> objects, FileIconHelper fileIcon) {
        super(context, resource, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFileIcon = fileIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.favorite_item, parent, false);
        }

        FavoriteItem item = getItem(position);
        FileInfo lFileInfo = item.fileInfo;

        Util.setText(view, R.id.file_name, item.title != null ? item.title : lFileInfo.fileName);
        Util.setText(view, R.id.modified_time, Util.formatDateString(mContext, lFileInfo.ModifiedDate));
        Util.setText(view, R.id.file_size, (lFileInfo.IsDir ? "" : Util.convertStorage(lFileInfo.fileSize)));

        ImageView lFileImage = (ImageView) view.findViewById(R.id.file_image);
        ImageView lFileImageFrame = (ImageView) view.findViewById(R.id.file_image_frame);
        lFileImage.setTag(position);

        if (lFileInfo.IsDir) {
            lFileImageFrame.setVisibility(View.GONE);
            lFileImage.setImageResource(R.drawable.folder_fav);
        } else {
            mFileIcon.setIcon(lFileInfo, lFileImage, lFileImageFrame);
        }

        return view;
    }

}
