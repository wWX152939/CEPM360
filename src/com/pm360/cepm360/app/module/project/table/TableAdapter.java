
package com.pm360.cepm360.app.module.project.table;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.SyncScrollView;
import com.pm360.cepm360.app.common.view.swipemenulistview.SwipeMenuCreator;
import com.pm360.cepm360.app.common.view.swipemenulistview.SwipeMenuListView;
import com.pm360.cepm360.app.utils.UtilTools;

import java.util.ArrayList;
import java.util.List;

//import android.text.Html;

public class TableAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<TableRow> mList = new ArrayList<TableRow>();
    private ArrayList<TableRow> mExpandList = new ArrayList<TableRow>();
    private ArrayList<TableRow> mMultiSelectList = new ArrayList<TableRow>();
    private ArrayList<TableRow> mSearchList = new ArrayList<TableRow>();
    private boolean mExpandable;
    private String[] mHeadNames;
    private int[] mArrHeadWidths;
    private int mCellColor;
    private int mHeadColor;
    private SyncScrollView mLeftsv, mRightsv;
    private HorizontalScrollView mRightScrollView;
    private SwipeMenuListView mLeftListView;
    private ListView mRightListView;
    private int mSelectedPosition = -1;
    private boolean mCanHide;
    private ImageView mHeadImageView;
    private boolean mMultiselect;
    private CheckBox mAllCheckBox;
    private FloatingMenuView mFloatingMenuView;
    private int mDividerColor;

    public TableAdapter(Context context, View rootView, int headNamesRes,
            int HeadWidthsRes, int headColor, int cellColor, int dividerColor, boolean expandable,
            boolean canHide, boolean multiselect) {
        mContext = context;
        mHeadNames = context.getResources().getStringArray(headNamesRes);
        mArrHeadWidths = context.getResources().getIntArray(HeadWidthsRes);
        mHeadColor = headColor;
        mCellColor = cellColor;
        mDividerColor = dividerColor;
        mExpandable = expandable;
        mCanHide = canHide;
        mMultiselect = multiselect;
        mInflater = LayoutInflater.from(context);

        mLeftsv = (SyncScrollView) rootView.findViewById(R.id.left_sv);
        mRightsv = (SyncScrollView) rootView.findViewById(R.id.right_sv);
        mLeftsv.setScrollView(mRightsv);
        mRightsv.setScrollView(mLeftsv);
        int width = UtilTools.dp2pxW(context, mArrHeadWidths[0]) + UtilTools.dp2pxW(context, 1);
        if (mMultiselect)
            width = width + UtilTools.dp2pxW(context, CheckBoxCell.DEFAULT_WIDTH); 
                   // + UtilTools.dp2pxW(context, 1);
        
        mLeftsv.getLayoutParams().width = width;
        mRightScrollView = (HorizontalScrollView) rootView.findViewById(R.id.right_hsv);
        mLeftListView = (SwipeMenuListView) rootView.findViewById(R.id.left_container_listview);
        initHead(rootView);
        mLeftListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                mLeftsv.setCanScroll(false);
            }

            @Override
            public void onSwipeEnd(int position) {
                mLeftsv.setCanScroll(true);
            }
        });
        mRightListView = (ListView) rootView.findViewById(R.id.right_container_listview);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedPosition(position);
            }

        };
        mLeftListView.setAdapter(this);
        mRightListView.setAdapter(this);
        mLeftListView.setOnItemClickListener(onItemClickListener);
        mRightListView.setOnItemClickListener(onItemClickListener);
        mFloatingMenuView = (FloatingMenuView) rootView.findViewById(R.id.floating_menu);
    }

    private void initHead(View rootView) {
        LinearLayout leftHeadLayout = (LinearLayout) rootView
                .findViewById(R.id.left_title_container);
        LinearLayout rightHeadLayout = (LinearLayout) rootView
                .findViewById(R.id.right_title_container);

        View line1 = mInflater
                .inflate(R.layout.project_table_line_v_view, leftHeadLayout, false);
              line1.setBackgroundColor(mDividerColor);
              leftHeadLayout.addView(line1);
              
        if (mMultiselect) {
            View headView = mInflater.inflate(R.layout.project_table_check_box,
                    leftHeadLayout, false);
            mAllCheckBox = (CheckBox) headView.findViewById(R.id.checkbox);
            mAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (mMultiSelectList.size() != getCount()) {
                            for (TableRow row : getList()) {
                                if (!mMultiSelectList.contains(row)) {
                                    mMultiSelectList.add(row);
                                }
                            }
                            refresh();
                        }
                    } else {
                        if (mMultiSelectList.size() == getCount()) {
                            for (TableRow row : mMultiSelectList) {
                                row.setChecked(false);
                            }
                            mMultiSelectList.clear();
                            refresh();
                        }
                    }
                }

            });
            
            headView.getLayoutParams().width = UtilTools
                    .dp2pxW(mContext, CheckBoxCell.DEFAULT_WIDTH);
            leftHeadLayout.addView(headView);
//            View line = mInflater
//                    .inflate(R.layout.project_table_line_v_view, leftHeadLayout, false);
//            line.setBackgroundColor(mDividerColor);
//            leftHeadLayout.addView(line);
        }
        if (mCanHide) {
            View headView = mInflater.inflate(R.layout.project_table_header_drawable_textview,
                    leftHeadLayout, false);
            TextView headTextVew = (TextView) headView.findViewById(R.id.table_atom_text);
            headTextVew.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
            headTextVew.setTextColor(mHeadColor);
            headTextVew.setGravity(Gravity.LEFT|Gravity.CENTER);
            //String viewName = "<b>" + mHeadNames[0] + "</b>";
            //headTextVew.setText(Html.fromHtml(viewName));
            headTextVew.setText(mHeadNames[0]);
            mHeadImageView = (ImageView) headView.findViewById(R.id.table_atom_image);
            mHeadImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setVisibleRightScrollView(isVisibleRightScrollView() ? View.GONE : View.VISIBLE);
                }
            });
            headView.getLayoutParams().width = UtilTools.dp2pxW(mContext, mArrHeadWidths[0]);
            leftHeadLayout.addView(headView);
        } else {
            TextView headView = (TextView) mInflater.inflate(R.layout.project_table_text_view,
                    leftHeadLayout, false);
            headView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
            headView.setTextColor(mHeadColor);
            //String viewName = "<b>" + mHeadNames[0] + "</b>";
            //headView.setText(Html.fromHtml(viewName));
            headView.setGravity(Gravity.LEFT|Gravity.CENTER);
            headView.setText(mHeadNames[0]);
            headView.getLayoutParams().width = UtilTools.dp2pxW(mContext, mArrHeadWidths[0]);
            leftHeadLayout.addView(headView);
        }
//        View line = mInflater.inflate(R.layout.project_table_line_v_view, leftHeadLayout, false);
//        line.setBackgroundColor(mDividerColor);
//        leftHeadLayout.addView(line);
        for (int i = 1; i < mArrHeadWidths.length; i++) {
            TextView headView2 = (TextView) mInflater.inflate(R.layout.project_table_text_view,
                    rightHeadLayout, false);
            headView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
            headView2.setTextColor(mHeadColor);
            //String viewName2 = "<b>" + mHeadNames[i] + "</b>";
            //headView2.setText(Html.fromHtml(viewName2));
            headView2.setGravity(Gravity.LEFT|Gravity.CENTER);
            headView2.setText(mHeadNames[i]);
            headView2.getLayoutParams().width = UtilTools.dp2pxW(mContext, mArrHeadWidths[i]);
            rightHeadLayout.addView(headView2);
//            if (i != mArrHeadWidths.length - 1) {
//                View line2 = mInflater.inflate(R.layout.project_table_line_v_view, rightHeadLayout,
//                        false);
//                line2.setBackgroundColor(mDividerColor);
//                rightHeadLayout.addView(line2);
//            }
        }
        View line = mInflater
                .inflate(R.layout.project_table_line_v_view, leftHeadLayout, false);
              line.setBackgroundColor(mDividerColor);
              rightHeadLayout.addView(line);
    }

    public boolean setExpandList(int position, boolean isExpanded) {
        ExpandCell expandParent = (ExpandCell) getItem(position).getValueAt(0);
        if (!expandParent.isFolder())
            return false;
        int parentLevel = expandParent.getLevel();
        ArrayList<TableRow> removeList = new ArrayList<TableRow>();
        for (int i = position + 1; i < getCount(); i++) {
            TableRow itemCell = getItem(i);
            ExpandCell expandCell = (ExpandCell) itemCell.getValueAt(0);
            if (parentLevel >= expandCell.getLevel()) {
                break;
            }
            removeList.add(itemCell);            
        }

        mExpandList.removeAll(removeList);
        notifyDataSetChanged();
        expandParent.setExpanded(isExpanded);
        if (isExpanded) {
            addExpanded(expandParent.getExpandId(), expandParent.getLevel(), position + 1);
        }
        notifyDataSetChanged();
        UtilTools.setListViewHeightBasedOnChildren(mLeftListView);
        UtilTools.setListViewHeightBasedOnChildren(mRightListView);

        return true;
    }

    private int addExpanded(int parentId, int childLevel, int position) {
        for (TableRow row : mList) {
            ExpandCell expandCell = (ExpandCell) row.getValueAt(0);
            if (expandCell.isFolder()) {
                expandCell.setHasChild(hasChildCount(expandCell.getExpandId()));
            }
            if (expandCell.getParentId() == parentId) {
                expandCell.setLevel(childLevel + 1);
                mExpandList.add(position, row);
                position++;
                if (expandCell.isExpanded()) {
                    position = addExpanded(expandCell.getExpandId(), expandCell.getLevel(),
                            position);
                }
            }
        }
        return position;
    }

    private boolean hasChildCount(int parentId) {
        boolean res = false;
        for (TableRow row : mList) {
            ExpandCell expandCell = (ExpandCell) row.getValueAt(0);
            if (expandCell.getParentId() == parentId) {
                res = true;
                break;
            }
        }
        return res;
    }

    public void setSearchText(String text, int[] colums) {
        mSearchList.clear();
        if (text.equals(""))
            return;
        for (TableRow row : mList) {
            for (int index : colums) {
                TextCell itemCell = row.getValueAt(index);
                if (itemCell.getCellValue().contains(text)) {
                    mSearchList.add(row);
                    break;
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TableRow row = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.project_table_list_item, parent, false);
        }
        LinearLayout itemCustom = (LinearLayout) convertView.findViewById(R.id.custome_item);
        itemCustom.getLayoutParams().height = row.getRowHeight();
        itemCustom.removeAllViews();
        if (parent.getId() == R.id.left_container_listview) {
            itemCustom.addView(row.getValueAt(0).createLine(itemCustom, mDividerColor));
            if (mMultiselect) {
                final CheckBoxCell checkBoxCell = new CheckBoxCell("", "multiselect",
                        CheckBoxCell.DEFAULT_WIDTH, false);
                View child = checkBoxCell.createView(itemCustom, row.getRowId(),
                        row.isFolder(), mCellColor);
                CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox);
                if (mMultiSelectList.contains(row)) {
                    checkBox.setChecked(true);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mMultiSelectList.add(row);
                        } else {
                            mMultiSelectList.remove(row);
                        }
                        row.setChecked(isChecked);
                        mAllCheckBox.setChecked(mMultiSelectList.size() == getCount());
                    }
                });
                //itemCustom.addView(row.getValueAt(0).createLine(itemCustom, mDividerColor));
                itemCustom.addView(child);
                //itemCustom.addView(row.getValueAt(0).createLine(itemCustom, mDividerColor));
            }
            View child = row.getValueAt(0).createView(itemCustom, row.getRowId(),
                    row.isFolder(), mCellColor);
            itemCustom.addView(child);
            if ((mMultiselect ? mMultiSelectList.contains(row) : position == mSelectedPosition)) {
                itemCustom.setBackgroundResource(R.color.listview_selected_bg); 
            } else {
                if (position % 2 == 1) {
                    itemCustom.setBackgroundResource(R.color.content_listview_single_line_bg);
                } else {
                    itemCustom.setBackgroundColor(Color.WHITE);
                }
            }
            //itemCustom.addView(row.getValueAt(0).createLine(itemCustom, mDividerColor));
        } else {
            int value_size = row.getValueSize();
            for (int i = 1; i < value_size; i++) {
                itemCustom.addView(row.getValueAt(i).createView(parent, row.getRowId(),
                        row.isFolder(), mCellColor));
                if (i == value_size - 1) {
                    itemCustom.addView(row.getValueAt(i).createLine(itemCustom, mDividerColor));
                }
                if ((mMultiselect ? mMultiSelectList.contains(row) : position == mSelectedPosition)) {
                    itemCustom.setBackgroundResource(R.color.listview_selected_bg); 
                } else {
                    if (position % 2 == 1) {
                        itemCustom.setBackgroundResource(R.color.content_listview_single_line_bg);
                    } else {
                        itemCustom.setBackgroundColor(Color.WHITE);
                    }
                }
            }
        }
//        itemCustom.setBackgroundColor(position == mSelectedPosition ? Color.argb(0x30, 0xc9, 0xa9,
//                0xf8)
//                : Color.TRANSPARENT);
//        itemCustom.setBackgroundColor((mMultiselect ? mMultiSelectList.contains(row) : position == mSelectedPosition) ? 
//                Color.parseColor("#c0def7") : Color.TRANSPARENT);
                
        return convertView;
    }

    public void refresh() {
        if (mExpandable) {
            mExpandList.clear();
            addExpanded(0, 0, 0);
        }
        if (mAllCheckBox != null && getCount() != 0)
            mAllCheckBox.setChecked(mMultiSelectList.size() == getCount());
        notifyDataSetChanged();
        UtilTools.setListViewHeightBasedOnChildren(mLeftListView);
        UtilTools.setListViewHeightBasedOnChildren(mRightListView);
    }

    @Override
    public int getCount() {
        return mExpandable ? mExpandList.size() : mSearchList.size() == 0 ? mList.size()
                : mSearchList.size();
    }

    @Override
    public TableRow getItem(int position) {
        return mExpandable ? mExpandList.get(position) : mSearchList.size() == 0 ? mList
                .get(position) : mSearchList.get(position);
    }

    public TableRow getSelectItem() {
        if (mSelectedPosition == -1)
            return null;
        return mExpandable ? mExpandList.get(mSelectedPosition) : mSearchList.size() == 0 ? mList
                .get(mSelectedPosition) : mSearchList.get(mSelectedPosition);
    }

    @Override
    public long getItemId(int position) {
        return mExpandable ? mExpandList.get(position).getRowId() : mSearchList.size() == 0 ? mList
                .get(position).getRowId() : mSearchList.get(position).getRowId();
    }

    @Override
    public int getViewTypeCount() {
        return mExpandable ? 2 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isFolder() ? 1 : 0;
    }

    public long getSelectItemId() {
        if (mSelectedPosition == -1)
            return -1;
        return mExpandable ? mExpandList.get(mSelectedPosition).getRowId()
                : mSearchList.size() == 0 ? mList
                        .get(mSelectedPosition).getRowId() : mSearchList.get(mSelectedPosition)
                        .getRowId();
    }

    public ArrayList<TableRow> getList() {
        return mExpandable ? mExpandList : mSearchList.size() == 0 ? mList :
                mSearchList;
    }

    public boolean addItem(int rowId, List<TextCell> rowValues) {
        return addItem(rowId, rowValues, UtilTools.dp2pxH(mContext, 36));
    }

    public boolean addItem(int rowId, List<TextCell> rowValues, int rowHeight) {
        return addItem(rowId, rowValues, false, false, rowHeight);
    }

    public boolean addItem(int rowId, List<TextCell> rowValues, boolean isFolder, boolean checked) {
        return addItem(rowId, rowValues, isFolder, checked, UtilTools.dp2pxH(mContext, 36));
    }

    public boolean addItem(int rowId, List<TextCell> rowValues, boolean isFolder, boolean checked,
            int rowHeight) {
        TableRow row = new TableRow(rowId, rowValues, isFolder, rowHeight);
        mList.add(row);
        if (mExpandable && mSelectedPosition != -1) {
            ExpandCell parent = (ExpandCell) getItem(mSelectedPosition).getValueAt(0);
            ExpandCell expandCell = (ExpandCell) row.getValueAt(0);
            expandCell.setLevel(parent.getLevel() + 1);
            mExpandList.add(mSelectedPosition + 1, row);
        }
        if (checked) {
            row.setChecked(true);
            mMultiSelectList.add(row);
        }
        return false;
    }

    public boolean setItem(int rowId, List<TextCell> rowValues) {
        return setItem(rowId, rowId, rowValues);
    }

    public boolean setItem(int old_rowId, int new_rowId, List<TextCell> rowValues) {
        for (TableRow row : mList) {
            if (row.getRowId() == old_rowId && !row.isFolder()) {
                if (mExpandable) {
                    ExpandCell oldExpandCell = (ExpandCell) row.getValueAt(0);
                    ExpandCell expandCell = (ExpandCell) rowValues.get(0);
                    expandCell.setLevel(oldExpandCell.getLevel());
                }
                row.setRowId(new_rowId);
                row.setRowValues(rowValues);
            }
        }
        return true;
    }

    public int removeSelectItem(int position) {
        TableRow row = null;
        if (mExpandable) {
            row = mExpandList.remove(position);
            mList.remove(row);
        } else {
            row = mList.remove(position);
        }
        mSelectedPosition = -1;
        return row.getRowId();
    }

    public boolean removeItemById(int rowId) {
        boolean res = false;
        for (TableRow row : mList) {
            if (row.getRowId() == rowId) {
                res = mList.remove(row);
                break;
            }
        }
        return res;
    }

    public void clear() {
        mList.clear();
        mExpandList.clear();
        mMultiSelectList.clear();
        mSearchList.clear();
        mSelectedPosition = -1;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        if (mMultiselect) {
            TableRow row = getItem(position);
            if (!mMultiSelectList.contains(row)) {            
                mMultiSelectList.add(row);
            } else {
                mMultiSelectList.remove(row);
            }
        }
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public String[] getHeadNames() {
        return mHeadNames;
    }

    public int[] getArrHeadWidths() {
        return mArrHeadWidths;
    }

    public void setMultiSelectList(ArrayList<TableRow> list) {
        mMultiSelectList.clear();
        if (list != null)
            mMultiSelectList.addAll(list);
    }

    public ArrayList<TableRow> getMultiSelectList() {
        return mMultiSelectList;
    }

    public boolean isVisibleRightScrollView() {
        return mRightScrollView.getVisibility() == View.VISIBLE;
    }

    public void setVisibleRightScrollView(int visibility) {
        mRightScrollView.setVisibility(visibility);
        mHeadImageView
                .setImageResource(visibility == View.VISIBLE ? R.drawable.arrow_double_left_white
                        : R.drawable.arrow_double_right_white);
    }

    public void setLeftMenuCreator(SwipeMenuCreator menuCreator) {
        mLeftListView.setMenuCreator(menuCreator);
    }

    public void setLeftOnMenuItemClickListener(
            SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener) {
        mLeftListView.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    public FloatingMenuView getFloatingMenuView() {
        return mFloatingMenuView;
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        mLeftListView.setOnItemClickListener(listener);
        mRightListView.setOnItemClickListener(listener);
    }
}
