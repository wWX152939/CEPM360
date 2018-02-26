package com.pm360.cepm360.app.module.home.portal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.query.RemoteIndexService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnnouncementsFragment extends Fragment {
    
    private View mRootView;
    private View mHeaderView;
    private ImageView mQueryMenu;
    private PortalMoreListActivity mActivity;
    private Dialog mQueryDialog;
    
    private Calendar mCalendar = Calendar
            .getInstance(Locale.SIMPLIFIED_CHINESE);
    
    private ListView mListView;
    private DataListAdapter<Announcement> mAdapter;
    private List<Announcement> mDataList = new ArrayList<Announcement>();
    
    // 列表头显示名称
    private String[] mHeaderNames;
    private int[] mDisplayItemIds;
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mAdapter.notifyDataSetChanged();
            return false;
        }
    });
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        mActivity = (PortalMoreListActivity) getActivity();
        mRootView = inflater.inflate(
                R.layout.portal_more_common_fragment_layout, container, false);
              
        initContentView(mRootView);
        initQueryMenu(mRootView);
               
        loadData();
        return mRootView;
    }
    
    private void loadData() {
        User user = UserCache.getCurrentUser();
        RemoteIndexService.getInstance().getMoreAnnouncementList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mDataList.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mDataList.addAll((ArrayList<Announcement>) list);                        
                    }
                }                
                mHandler.sendEmptyMessage(0);
            }
            
        }, user.getTenant_id());
    }
    
    private void doQuery(Announcement bean) {
        User user = UserCache.getCurrentUser();
        bean.setTenant_id(user.getTenant_id());
        mActivity.showProgressDialog("MyTask doQuery...");
        RemoteIndexService.getInstance().getAnnouncementListByCondition(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mActivity.dismissProgressDialog();
                mDataList.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mDataList.addAll((ArrayList<Announcement>) list);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
            
        }, bean);
    }
    
    private void showDatePickerDialog(final TextView textView) {
        new DatePickerDialog(mActivity,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        textView.setText(DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCalendar.getTime()));
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private void initQueryMenu(View view) {
        mQueryDialog = new Dialog(mActivity, R.style.MyDialogStyle2);
        mQueryDialog.setContentView(R.layout.portal_more_document_query_dialog);
        mQueryDialog.setCanceledOnTouchOutside(true);
        
        final EditText condition1 = (EditText) mQueryDialog.findViewById(R.id.condition1);
        final TextView condition2 = (TextView) mQueryDialog.findViewById(R.id.condition2);
        final TextView condition3 = (TextView) mQueryDialog.findViewById(R.id.condition3);
        
        condition2.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                showDatePickerDialog((TextView) view);
            }
        });
        condition3.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                showDatePickerDialog((TextView) view);
            }
        });
        
        Button queryButton = (Button) mQueryDialog.findViewById(R.id.button_query);
        Button cancelButton = (Button) mQueryDialog.findViewById(R.id.button_clear);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.button_clear:
                    condition1.setText("");
                    condition2.setText("");
                    condition3.setText("");
                    break;
                case R.id.button_query:
                    Announcement bean = new Announcement();
                    bean.setTitle(condition1.getText().toString());
                    bean.setStart_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition2.getText().toString()));
                    bean.setEnd_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition3.getText().toString()));
                    doQuery(bean);
                    mQueryDialog.dismiss();
                    break;
                }
            }
        };
        queryButton.setOnClickListener(listener);
        cancelButton.setOnClickListener(listener);
        
        mQueryMenu = (ImageView) view.findViewById(R.id.floating_query_menu);
        mQueryMenu.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mQueryDialog.show();           
            }
        });
    }
    
    private void initContentView(View view) {
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.header_layout);
        mHeaderView = LayoutInflater.from(mActivity)
                .inflate(R.layout.portal_more_documents_listitem, viewGroup, false);
        viewGroup.addView(mHeaderView);
        
        mHeaderNames = getResources().getStringArray(
                R.array.portal_documents_names);
        TypedArray typedArray = getResources().obtainTypedArray(
                R.array.table_column_ids_3);

        if (mHeaderNames != null) {
            mDisplayItemIds = new int[typedArray.length()];
            for (int i = 0; i < mDisplayItemIds.length; i++) {
                mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
                // 配置列表头项相关字段
                TextView tv = (TextView) mHeaderView
                        .findViewById(mDisplayItemIds[i]);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity
                        .getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
                tv.setText(mHeaderNames[i]);
                tv.setTextColor(getActivity().getResources().getColor(R.color.dialog_list_title_text_color));
            }
        }
        typedArray.recycle();

        mListView = (ListView) mRootView.findViewById(R.id.listview);
        mAdapter = new DataListAdapter<Announcement>(mActivity, adapterManager, mDataList);
        mListView.setAdapter(mAdapter);
    }
    
    private ListAdapterInterface<Announcement> adapterManager = new DataListAdapter.ListAdapterInterface<Announcement>() {

        @Override
        public int getLayoutId() {
            return R.layout.portal_more_documents_listitem;
        }

        @Override
        public View getHeaderView() {
            return mHeaderView;
        }

        @Override
        public void regesterListeners(ViewHolder viewHolder, final int position) {
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.setSelected(position, true);
                        // goto details
                        IntentHelper.startActivity(getActivity(), 4, mDataList.get(position));
                    }
                });
            }
        }

        @Override
        public void initListViewItem(View convertView, ViewHolder holder,
                DataListAdapter<Announcement> adapter, int position) {
            Map<String, String> listViewItem = beanToMap(adapter
                    .getItem(position), position);
            for (int i = 0; i < mHeaderNames.length; i++) {
                holder.tvs[i].setText(listViewItem.get(mHeaderNames[i]));
            }
        }

        @Override
        public void initLayout(View convertView, ViewHolder holder) {
            holder.tvs = new TextView[mDisplayItemIds.length];
            for (int i = 0; i < mDisplayItemIds.length; i++) {                
                holder.tvs[i] = (TextView) convertView
                        .findViewById(mDisplayItemIds[i]);
            }
        }
        
        private Map<String, String> beanToMap(Announcement data, int position) {
            Map<String, String> mapItem = new HashMap<String, String>();  
            mapItem.put(mHeaderNames[0], String.valueOf(position + 1));
            mapItem.put(mHeaderNames[1], data.getTitle());
            mapItem.put(mHeaderNames[2], DateUtils.dateToString(DateUtils.FORMAT_LONG, data.getPublish_time()));
            return mapItem;
        }

        @Override
        public List<Announcement> findByCondition(Object... condition) {
            return null;
        }

        @Override
        public boolean isSameObject(Announcement t1, Announcement t2) {
            return false;
        }
        
    };
}

