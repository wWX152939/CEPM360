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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Index_daiban;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.query.RemoteIndexService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TodoFragment extends Fragment {
    
    private View mRootView;
    private View mHeaderView;
    private ImageView mQueryMenu;
    private PortalMoreListActivity mActivity;
    private Dialog mQueryDialog;
    
    private ListView mListView;
    private DataListAdapter<Index_daiban> mAdapter;
    private List<Index_daiban> mDataList = new ArrayList<Index_daiban>();
    
    // 列表头显示名称
    private String[] mHeaderNames;
    private int[] mDisplayItemIds;
    
    private Calendar mCalendar = Calendar
            .getInstance(Locale.SIMPLIFIED_CHINESE);
    
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
        RemoteIndexService.getInstance().getMoreToDoList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mDataList.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                    	List<Index_daiban> daibanList = new ArrayList<Index_daiban>();
                    	for (Index_daiban daiban : (ArrayList<Index_daiban>)list) {
                    		if (daiban.getIs_process() == 0) {
                    			daibanList.add(daiban);
                    		}
                    	}
                        mDataList.addAll(daibanList);
                    }
                }                
                mHandler.sendEmptyMessage(0);
            }
            
        }, user.getTenant_id(), user.getUser_id());
    }
    
    private void doQuery(Index_daiban bean) {
        User user = UserCache.getCurrentUser();
        bean.setTenant_id(user.getTenant_id());
        bean.setUser_id(user.getUser_id());
        mActivity.showProgressDialog("MyTask doQuery...");
        RemoteIndexService.getInstance().getToDoListByCondition(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mActivity.dismissProgressDialog();
                mDataList.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mDataList.addAll((ArrayList<Index_daiban>) list);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
            
        }, bean);
    }

    private void initQueryMenu(View view) {
        mQueryDialog = new Dialog(mActivity, R.style.MyDialogStyle);
        mQueryDialog.setContentView(R.layout.portal_more_todo_query_dialog);
        mQueryDialog.setCanceledOnTouchOutside(true);
        
        final EditText condition1 = (EditText) mQueryDialog.findViewById(R.id.condition1);
        final Spinner condition2 = (Spinner) mQueryDialog.findViewById(R.id.condition2);
        final Spinner condition3 = (Spinner) mQueryDialog.findViewById(R.id.condition3);
        final TextView condition4 = (TextView) mQueryDialog.findViewById(R.id.condition4);
        final TextView condition5 = (TextView) mQueryDialog.findViewById(R.id.condition5);
        
        String[] condition2Items = new String[] {
                getResources().getString(R.string.all),
                GLOBAL.MSG_IS_PROCESS[0][1], GLOBAL.MSG_IS_PROCESS[1][1] };
        ArrayAdapter<String> condition2Adapter = new ArrayAdapter<String>(
                mActivity, R.layout.spinner_item, condition2Items);
        condition2Adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        condition2.setAdapter(condition2Adapter);
        
        // 我的待办：(1,2,3,4,5,6,7,13,14,22,23,24)
        String[] condition3Items = getResources().getStringArray(R.array.portal_todo_spinner_items);
        ArrayAdapter<String> condition3Adapter = new ArrayAdapter<String>(
                mActivity, R.layout.spinner_item, condition3Items);
        condition3Adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        condition3.setAdapter(condition3Adapter);
        
        condition4.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                showDatePickerDialog((TextView) view);
            }
        });
        condition5.setOnClickListener(new View.OnClickListener() {
            
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
                    condition2.setSelection(0);
                    condition3.setSelection(0);
                    condition4.setText("");
                    condition5.setText("");
                    break;
                case R.id.button_query:
                    Index_daiban bean = new Index_daiban();
                    bean.setTitle(condition1.getText().toString());
                    bean.setIs_process(condition2.getSelectedItemPosition() - 1);
                    bean.setMessage_type_key(getTypeSelected(condition3.getSelectedItemPosition()));
                    bean.setStart_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition4.getText().toString()));
                    bean.setEnd_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition5.getText().toString()));
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
                .inflate(R.layout.portal_more_todo_listitem, viewGroup, false);
        viewGroup.addView(mHeaderView);
        
        mHeaderNames = getResources().getStringArray(
                R.array.portal_todo_names);
        TypedArray typedArray = getResources().obtainTypedArray(
                R.array.table_column_ids_4);

        if (mHeaderNames != null) {
            mDisplayItemIds = new int[typedArray.length()];
            for (int i = 0; i < mDisplayItemIds.length; i++) {
                mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
                // 配置列表头项相关字段
                TextView tv = (TextView) view
                        .findViewById(mDisplayItemIds[i]);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity
                        .getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
                tv.setText(mHeaderNames[i]);
                tv.setTextColor(getActivity().getResources().getColor(R.color.dialog_list_title_text_color));
            }
        }
        typedArray.recycle();

        mListView = (ListView) mRootView.findViewById(R.id.listview);
        mAdapter = new DataListAdapter<Index_daiban>(mActivity, adapterManager, mDataList);
        mListView.setAdapter(mAdapter);
    }
    
    private ListAdapterInterface<Index_daiban> adapterManager = new DataListAdapter.ListAdapterInterface<Index_daiban>() {

        @Override
        public int getLayoutId() {
            return R.layout.portal_more_todo_listitem;
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
                        // goto details. 0: 代办事项
                        IntentHelper.startActivity(getActivity(), 0, mDataList.get(position));
                    }
                });
            }
        }

        @Override
        public void initListViewItem(View convertView, ViewHolder holder,
                DataListAdapter<Index_daiban> adapter, int position) {
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
        
        private Map<String, String> beanToMap(Index_daiban data, int position) {
            Map<String, String> mapItem = new HashMap<String, String>();  
            mapItem.put(mHeaderNames[0], String.valueOf(position + 1));
            mapItem.put(mHeaderNames[1], data.getTitle());
            mapItem.put(mHeaderNames[2], DateUtils.dateToString(DateUtils.FORMAT_LONG, data.getTime()));
            if (data.getMessage_type_key() > 0) {
                mapItem.put(mHeaderNames[3], GLOBAL.MSG_TYPE_KEY[data.getMessage_type_key() - 1][1]);
            }
            return mapItem;
        }

        @Override
        public List<Index_daiban> findByCondition(Object... condition) {
            return null;
        }

        @Override
        public boolean isSameObject(Index_daiban t1, Index_daiban t2) {
            return false;
        }
        
    };
        
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
    
    // 我的待办：(all, 1,2,3,4,5,6,7,13,14,22,23,24)
    private int getTypeSelected(int selectedItemPosition) {
        int type;
        switch (selectedItemPosition) {
        case 0:
            type = 0;
            break;
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
            type = selectedItemPosition;
            break;
        case 8:
        case 9:
            type = selectedItemPosition + 5;
            break;
        case 10:
        case 11:
        case 12:
            type = selectedItemPosition + 12;
            break;
        default:
            type = 0;
            break;
        }
        return type;
    }
    
}
