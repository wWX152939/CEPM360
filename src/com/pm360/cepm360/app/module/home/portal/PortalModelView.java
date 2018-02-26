package com.pm360.cepm360.app.module.home.portal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.module.home.portal.PortailListItem;
import com.pm360.cepm360.common.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class PortalModelView extends LinearLayout {

    private TextView mLabelText;
    private TextView mMoreButton;
    private ListView mListView;
    private DataListAdapter<PortailListItem> mAdapter;
    private List<PortailListItem> mListItem = new ArrayList<PortailListItem>();
    
    private int mViewTag;
    private OnItemClickListener mListener;
    
    public static interface OnItemClickListener {
        public void onListViewItemClick(int viewTag, AdapterView<?> parent, View view,
                int position, long id);
        public void onMoreButtonClick(int viewTag, View view);
    };
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PortalModelView(Context context) {
        super(context);
    }
    
    public PortalModelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public PortalModelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public PortalModelView(Context context, int viewTag, String label) {
        super(context);        
        initView(context, viewTag, label);
        mViewTag = viewTag;
    }
    
    public void setDataList(ArrayList<PortailListItem> listItem) {
        mListItem.addAll(listItem);
        mAdapter.setShowDataList(mListItem);
    }
    
    public void initView(Context context, int viewTag, String label) {
        mViewTag = viewTag;
        mLabelText = (TextView) this.findViewById(R.id.model_label_name);
        mLabelText.setText(label);
        
        mMoreButton = (TextView) this.findViewById(R.id.more);
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMoreButtonClick(mViewTag, view);
                }
            }
        });
        
        mListView = (ListView) this.findViewById(R.id.model_content_list);
        mAdapter = new DataListAdapter<PortailListItem>(context, false, adapterInterface);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                if (mListener != null) {
                    mListener.onListViewItemClick(mViewTag, parent, view, position, id);
                }
            }
            
        });
    }
    
    ListAdapterInterface<PortailListItem> adapterInterface = new ListAdapterInterface<PortailListItem>() {

        @Override
        public int getLayoutId() {
            return R.layout.portal_model_list_item;
        }

        @Override
        public View getHeaderView() {
            return null;
        }

        @Override
        public void regesterListeners(ViewHolder viewHolder, int position) {
            // TODO Auto-generated method stub           
        }

        @Override
        public void initListViewItem(View convertView, ViewHolder holder,
                DataListAdapter<PortailListItem> adapter, int position) {
            holder.ivs[0].setBackgroundResource(adapter.getItem(position).getIconId());
            holder.tvs[0].setText(adapter.getItem(position).getTitle());
            holder.tvs[1].setText(DateUtils.dateToString(DateUtils.FORMAT_SHORT, adapter.getItem(position).getDate()));
        }

        @Override
        public void initLayout(View convertView, ViewHolder holder) {
            holder.ivs = new ImageView[1];
            holder.tvs = new TextView[2];
            
            holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
            holder.tvs[0] = (TextView) convertView.findViewById(R.id.title);
            holder.tvs[1] = (TextView) convertView.findViewById(R.id.date);
        }

        @Override
        public List<PortailListItem> findByCondition(Object... condition) {
            return null;
        }

        @Override
        public boolean isSameObject(PortailListItem t1, PortailListItem t2) {
            return false;
        }        
    };
    
}
