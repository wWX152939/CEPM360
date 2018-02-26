package com.pm360.cepm360.app.module.home.portal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.common.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class PortalGridAdpater extends BaseAdapter {
    
    private Context mContext;
    private int mLayoutId;
    private ArrayList<PortalModelBean> mModelLists = new ArrayList<PortalModelBean>();
    private List<Integer> mMyTaskList = new ArrayList<Integer>();
    
    private OnListItemClickListener mListener;
    
    public void setOnListItemClickListener(OnListItemClickListener listener) {
        mListener = listener;
    }
    
    public interface OnListItemClickListener {
        public void onListViewItemClick(int viewTag, AdapterView<?> parent, View view,
                int position, long id);
        public void onMoreButtonClick(int viewTag, View view);
        public void onItemLongClick(View view);
        public void onCheckBoxClick(int viewTag, boolean isChecked);
        public void onImageClick(int viewTag, View view);
    }
    
    public PortalGridAdpater(Context context, int layoutId, ArrayList<PortalModelBean> itemLists) {
        mContext = context;
        mLayoutId = layoutId;
        mModelLists.clear();
        mModelLists = itemLists;
    }
    
    public ArrayList<PortalModelBean> getModelLists() {
    	return mModelLists;
    }

    @Override
    public int getCount() {
        return mModelLists.size();
    }

    @Override
    public PortalModelBean getItem(int position) {
        return mModelLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public void updateMyTask(List<Integer> list) {
        mMyTaskList.clear();
        mMyTaskList.addAll(list);
        notifyDataSetChanged();
    }    

    @SuppressLint("InflateParams") @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            holder.title = (TextView) convertView.findViewById(R.id.model_label_name);
            holder.button = (TextView) convertView.findViewById(R.id.more);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.model_select_icon);
            holder.listView = (ListView) convertView.findViewById(R.id.model_content_list);
            holder.imageViews = new ImageView[2];
            holder.imageViews[0] = (ImageView) convertView.findViewById(R.id.image1);
            holder.imageViews[1] = (ImageView) convertView.findViewById(R.id.image2);
            holder.mytaskLayout = (LinearLayout) convertView.findViewById(R.id.portal_model_my_task);
            holder.imageLayout = (LinearLayout) convertView.findViewById(R.id.image_Layout);
            if (holder.mytaskLayout != null) {
                holder.mytaskItems = new TextView[6];
                holder.mytaskItems[0] = (TextView) convertView.findViewById(R.id.nostart_task_number);
                holder.mytaskItems[1] = (TextView) convertView.findViewById(R.id.starting_task_number);
                holder.mytaskItems[2] = (TextView) convertView.findViewById(R.id.delay_task_number);
                holder.mytaskItems[3] = (TextView) convertView.findViewById(R.id.today_expire_number);
                holder.mytaskItems[4] = (TextView) convertView.findViewById(R.id.week_expire_number);
                holder.mytaskItems[5] = (TextView) convertView.findViewById(R.id.month_expire_number);
                
                LinearLayout nostartTaskLy = (LinearLayout) convertView.findViewById(R.id.nostart_task_layout);
                LinearLayout startingTaskLy = (LinearLayout) convertView.findViewById(R.id.starting_task_layout);
                LinearLayout endTaskLy = (LinearLayout) convertView.findViewById(R.id.delay_task_layout);   
                LinearLayout todayExpireTaskLy = (LinearLayout) convertView.findViewById(R.id.today_expire_layout);
                LinearLayout weekExpireTaskLy = (LinearLayout) convertView.findViewById(R.id.week_expire_layout);
                LinearLayout monthExpireTaskLy = (LinearLayout) convertView.findViewById(R.id.month_expire_layout);
                
                OnClickListener listener = new OnClickListener() {
                    
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onMoreButtonClick(getItem(position).getIndex(), view);
                        }
                    }
                };          
                nostartTaskLy.setOnClickListener(listener);
                startingTaskLy.setOnClickListener(listener);
                endTaskLy.setOnClickListener(listener);     
                todayExpireTaskLy.setOnClickListener(listener);
                weekExpireTaskLy.setOnClickListener(listener);
                monthExpireTaskLy.setOnClickListener(listener);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        /*若是在正常模式，全部控件恢复显示*/
        if (holder.checkBox == null) { 
        	holder.listView.setVisibility(View.VISIBLE);
        	holder.imageViews[0].setVisibility(View.VISIBLE);
        	holder.imageViews[1].setVisibility(View.VISIBLE);
        	holder.mytaskLayout.setVisibility(View.VISIBLE);
        	holder.imageLayout.setVisibility(View.VISIBLE);
        }
        
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View view) {
                mListener.onItemLongClick(view);
                return false;
            }
        });

        /* 获取小面板的绝对序号（0：代办事项， 1：我的任务， 2：形象进展， 3：最新反馈， 4：公司通告， 5：最新文档  ）*/
        int index = getItem(position).getIndex();
        
        if (holder.imageViews[0] != null) {            
            for (int i = 0; i < holder.imageViews.length; i++) {
                holder.imageViews[i].setVisibility(index == 2 ? View.VISIBLE : View.GONE);
                holder.imageViews[i].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onImageClick(getItem(position).getIndex(), v);
                        }
                    }
                });
            }
            
            if (index == 2) {
                if (getItem(position).getBitmaps().size() > 0) {
                    holder.imageViews[0].setVisibility(View.VISIBLE);
                    holder.imageViews[0].setImageBitmap(getItem(position).getBitmaps().get(0));
                    if (getItem(position).getBitmaps().size() > 1) {
                        holder.imageViews[1].setVisibility(View.VISIBLE);
                        holder.imageViews[1].setImageBitmap(getItem(position).getBitmaps().get(1));
                    } else {
                        holder.imageViews[1].setVisibility(View.INVISIBLE);
                    }                    
                } else {
                    holder.imageViews[0].setVisibility(View.GONE);
                    holder.imageViews[1].setVisibility(View.GONE);
                }
            }            
            
        }

        /*我的任务面板 特殊处理*/        
        if (index == 1) {
            /*判断是否是edit模式界面, 不为null应该是普通界面*/
            if (holder.mytaskLayout != null) {
                holder.listView.setVisibility(View.GONE);
                holder.imageLayout.setVisibility(View.GONE);  
                holder.mytaskLayout.setVisibility(View.VISIBLE);
                if (mMyTaskList.size() > 0) {
                    holder.mytaskItems[0].setText(mMyTaskList.get(0)+"");
                    holder.mytaskItems[1].setText(mMyTaskList.get(1)+"");
                    holder.mytaskItems[2].setText(mMyTaskList.get(2)+"");
                    holder.mytaskItems[3].setText(mMyTaskList.get(3)+"");
                    holder.mytaskItems[4].setText(mMyTaskList.get(4)+"");                
                    holder.mytaskItems[5].setText(mMyTaskList.get(5)+"");
                }
            }
        } else {
            if (holder.mytaskLayout != null) {
                holder.listView.setVisibility(View.VISIBLE);
                holder.imageLayout.setVisibility(View.VISIBLE);
                holder.mytaskLayout.setVisibility(View.GONE);
            }
        }
         
                
        holder.title.setText(getItem(position).getTitle());        
        holder.button.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMoreButtonClick(getItem(position).getIndex(), view);
                }
            }
        });
        
        if (holder.checkBox != null) {
            holder.checkBox.setChecked(getItem(position).isChecked());
            holder.checkBox.setOnClickListener(new View.OnClickListener() {            
                @Override
                public void onClick(View view) {
                    boolean isChecked = ((CheckBox) view).isChecked();
                    getItem(position).setChecked(isChecked);
                    mListener.onCheckBoxClick(getItem(position).getIndex(), isChecked);
                }
            });
        }
        
        if (holder.listView != null) {
            holder.adapter = new DataListAdapter<PortailListItem>(mContext, false, adapterInterface);
            holder.listView.setAdapter(holder.adapter);
            holder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos,
                        long id) {
                    if (mListener != null) {
                    	holder.adapter.setSelected(pos, true);
                        mListener.onListViewItemClick(getItem(position).getIndex(), parent, view, pos, id);
                    }
                }
                
            });
            
            holder.adapter.setShowDataList(getItem(position).getDataList());
            holder.adapter.notifyDataSetChanged();
        }
        
        return convertView;
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
        public List<PortailListItem> findByCondition(Object... condition) {
            return null;
        }

        @Override
        public boolean isSameObject(PortailListItem t1, PortailListItem t2) {
            return false;
        }

        @Override
        public void regesterListeners(
                com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder viewHolder,
                int position) {          
        }

        @Override
        public void initListViewItem(
                View convertView,
                com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder holder,
                DataListAdapter<PortailListItem> adapter, int position) {
            holder.tvs[0].setText(adapter.getItem(position).getTitle());
            holder.tvs[2].setText(DateUtils.dateToString(DateUtils.FORMAT_SHORT, adapter.getItem(position).getDate()));
            holder.tvs[3].setText(DateUtils.dateToString(DateUtils.FORMAT_SHORT_TIME, adapter.getItem(position).getDate()));
            if (adapter.getItem(position).getTag() != null) {
                holder.tvs[1].setText(adapter.getItem(position).getTag());
                if (adapter.getItem(position).getType() == 1) {
                    holder.tvs[1].setTextColor(Color.rgb(40, 44, 51));
                    holder.tvs[1].setBackgroundResource(R.drawable.portal_tag_blue);
                } else {
                    holder.tvs[1].setTextColor(Color.RED);
                    holder.tvs[1].setBackgroundResource(R.drawable.portal_tag_red);
                }
                holder.tvs[2].setVisibility(View.GONE);
                holder.tvs[3].setVisibility(View.GONE);
            }

            // 将选中的列表项高亮
            if (adapter.getSelectedList().contains((Integer) position)) {
                convertView.setBackgroundResource(R.color.touch_high_light);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }            
        }

        @Override
        public void initLayout(
                View convertView,
                com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder holder) {
            holder.tvs = new TextView[4];            
            holder.tvs[0] = (TextView) convertView.findViewById(R.id.title);
            holder.tvs[1] = (TextView) convertView.findViewById(R.id.tag);
            holder.tvs[2] = (TextView) convertView.findViewById(R.id.date);
            holder.tvs[3] = (TextView) convertView.findViewById(R.id.time);
            
        }        
    };
    
    class ViewHolder {
        TextView title;
        TextView button;
        ListView listView;
        CheckBox checkBox;
        ImageView[] imageViews;
        LinearLayout mytaskLayout;
        TextView[] mytaskItems;
        LinearLayout imageLayout;
        DataListAdapter<PortailListItem> adapter;
    }
}
