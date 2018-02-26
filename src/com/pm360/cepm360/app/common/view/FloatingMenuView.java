
package com.pm360.cepm360.app.common.view;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

import java.util.ArrayList;

public class FloatingMenuView extends ImageView {

    private static final ClipData EMPTY_CLIP_DATA = ClipData.newPlainText("", "");

    private ListPopupWindow mListPopupWindow;
    private FloatingMenuAdapter mAdapter;
    private DragShadowBuilder mShadowBuilder;
    private MyDragEventListener mMyDragEventListener;
    private Context mContext;

    public FloatingMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FloatingMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatingMenuView(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
    	mContext = context;
        setLongClickable(true);
        mMyDragEventListener = new MyDragEventListener();
        mShadowBuilder = new DragShadowBuilder(this);
        mAdapter = new FloatingMenuAdapter();
        mListPopupWindow = new ListPopupWindow(context);
        mListPopupWindow.setAnchorView(this);
        mListPopupWindow.setAdapter(mAdapter);
        mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                setImageResource(R.drawable.btn_add);
                postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        setEnabled(true);
                    }
                }, 300);
            }
        });
        setVisibility(View.GONE);
    }

    @Override
    public boolean performClick() {
        if (mAdapter.getCount() > 0) {
            setEnabled(false);
            mListPopupWindow.show();
            mListPopupWindow.getListView().setDivider(
                    new ColorDrawable(Color.argb(0xc5, 0xd1, 0xd3, 0xd3)));
            mListPopupWindow.getListView().setDividerHeight(UtilTools.dp2pxH(getContext(), 1));
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.dp4_w);
            mListPopupWindow.getListView().setPaddingRelative(padding, 0, padding, 0);
            setImageResource(R.drawable.btn_add_cancel);
        }
        return super.performClick();
    }

    @Override
    public boolean performLongClick() {
        View parent = (View) getParent();
        parent.setOnDragListener(mMyDragEventListener);
        startDrag(EMPTY_CLIP_DATA, mShadowBuilder, null, 0);
        return super.performLongClick();
    }

    public void setPopOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        mListPopupWindow.setOnItemClickListener(clickListener);
        setVisibility(View.VISIBLE);
    }

    public void addPopItem(String text, int resId) {
        mAdapter.addItem(text, resId);
    }

    public void clear() {
        mAdapter.clear();
    }

    public void dismiss() {
        mListPopupWindow.dismiss();
    }

    public class FloatingMenuAdapter extends BaseAdapter {

        private ArrayList<Item> items = new ArrayList<Item>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String text, int resId) {
            Item item = new Item();
            item.text = text;
            item.resId = resId;
            items.add(item);
        }

        public void clear() {
            items.clear();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.project_floating_menu_item, parent, false);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.icon.setImageResource(items.get(position).resId);
            holder.title.setText(items.get(position).text);
            return convertView;
        }

        private class ViewHolder {
            TextView title;
            ImageView icon;
        }

        private class Item {
            String text;
            int resId;
        }
    }

    private class MyDragEventListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            View parent = (View) getParent();
            int[] location = new int[2];
            parent.getLocationInWindow(location);
            if (event.getX() > getMeasuredWidth() / 2
                    && event.getY() > getMeasuredHeight() / 2
                    && event.getX() < ScreenUtils.getScreenWidth(v.getContext())- location[0] - getMeasuredWidth() / 2
                    && event.getY() < parent.getMeasuredHeight() - getMeasuredHeight() / 2) {
                setX(event.getX() - getMeasuredWidth() / 2);
                setY(event.getY() - getMeasuredHeight() / 2);
            }
            return true;
        };
    }
}
