package com.pm360.cepm360.app.module.home.portal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.query.RemoteIndexService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FeedbackFragment extends Fragment {
    
    private View mRootView;
    private View mHeaderView;
    private ImageView mQueryMenu;
    private PortalMoreListActivity mActivity;
    private Dialog mQueryDialog;
    
    private ListView mListView;
    private DataListAdapter<Index_feedback> mAdapter;
    private List<Index_feedback> mDataList = new ArrayList<Index_feedback>();
    
    // 列表头显示名称
    private String[] mHeaderNames;
    private int[] mDisplayItemIds;
    
    private TextView mProjectText;
    private TextView mUserText;
    private int mProjectId;
    private int mUserId;
    private Calendar mCalendar = Calendar
            .getInstance(Locale.SIMPLIFIED_CHINESE);
    
    private static final int PROJECT_REQUEST_CODE = 200;
    private static final int USER_REQUEST_CODE = 300;
    
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
        RemoteIndexService.getInstance().getMoreFeedbackList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mDataList.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mDataList.addAll((ArrayList<Index_feedback>) list);                        
                    }
                }                
                mHandler.sendEmptyMessage(0);
            }
            
        }, user.getTenant_id(), user.getUser_id());
    }
    
    private void doQuery(Index_feedback bean) {
        User user = UserCache.getCurrentUser();
        bean.setTenant_id(user.getTenant_id());
        bean.setUser_id(user.getUser_id());
        mActivity.showProgressDialog("MyTask doQuery...");
        RemoteIndexService.getInstance().getFeedbackListByCondition(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mActivity.dismissProgressDialog();
                mDataList.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mDataList.addAll((ArrayList<Index_feedback>) list);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
            
        }, bean);
    }
    
    private void initQueryMenu(View view) {
        mQueryDialog = new Dialog(mActivity, R.style.MyDialogStyle);
        mQueryDialog.setContentView(R.layout.portal_more_feedback_query_dialog);
        mQueryDialog.setCanceledOnTouchOutside(true);
        
        
        final EditText condition1 = (EditText) mQueryDialog.findViewById(R.id.condition1);
        final TextView condition2 = (TextView) mQueryDialog.findViewById(R.id.condition2);
        final TextView condition3 = (TextView) mQueryDialog.findViewById(R.id.condition3);
        mProjectText = (TextView) mQueryDialog.findViewById(R.id.condition4);
        mUserText = (TextView) mQueryDialog.findViewById(R.id.condition5);
        
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
        
        mProjectText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,
                        ProjectSelectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("action", ProjectSelectActivity.ACTION_PICK);
                intent.putExtras(bundle);
                startActivityForResult(intent, PROJECT_REQUEST_CODE);
            }            
        });
        
        mUserText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, OwnerSelectActivity.class);
                intent.putExtra("title", getString(R.string.please_select_user));
                startActivityForResult(intent, USER_REQUEST_CODE);
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
                    mProjectText.setText("");
                    mProjectId = 0;
                    mUserText.setText("");
                    mUserId = 0;
                    break;
                case R.id.button_query:
                    Index_feedback bean = new Index_feedback();
                    bean.setTitle(condition1.getText().toString());
                    bean.setStart_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition2.getText().toString()));
                    bean.setEnd_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition3.getText().toString()));
                    bean.setProject_id(mProjectId);
                    bean.setOwner(mUserId);
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
                .inflate(R.layout.portal_more_feedback_listitem, viewGroup, false);
        viewGroup.addView(mHeaderView);
        
        mHeaderNames = getResources().getStringArray(
                R.array.portal_feedback_names);
        TypedArray typedArray = getResources().obtainTypedArray(
                R.array.table_column_ids_6);

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
        mAdapter = new DataListAdapter<Index_feedback>(mActivity, adapterManager, mDataList);
        mListView.setAdapter(mAdapter);
    }
    
    private ListAdapterInterface<Index_feedback> adapterManager = new DataListAdapter.ListAdapterInterface<Index_feedback>() {

        @Override
        public int getLayoutId() {
            return R.layout.portal_more_feedback_listitem;
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
                        IntentHelper.startActivity(getActivity(), 3, mDataList.get(position));
                    }
                });
            }
        }

        @Override
        public void initListViewItem(View convertView, ViewHolder holder,
                DataListAdapter<Index_feedback> adapter, int position) {
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
        
        private Map<String, String> beanToMap(Index_feedback data, int position) {
            Map<String, String> mapItem = new HashMap<String, String>();  
            mapItem.put(mHeaderNames[0], String.valueOf(position + 1));
            mapItem.put(mHeaderNames[1], ProjectCache.getProjectIdMaps().get(String.valueOf(data.getProject_id())));
            mapItem.put(mHeaderNames[2], data.getTitle());
            mapItem.put(mHeaderNames[3], UserCache.getUserMaps().get(String.valueOf(data.getOwner())));
            if (data.getMessage_type_key() > 0) {
                mapItem.put(mHeaderNames[4], GLOBAL.MSG_TYPE_KEY[data.getMessage_type_key() - 1][1]);
            }
            mapItem.put(mHeaderNames[5], DateUtils.dateToString(DateUtils.FORMAT_SHORT, data.getTime()));
            return mapItem;
        }

        @Override
        public List<Index_feedback> findByCondition(Object... condition) {
            return null;
        }

        @Override
        public boolean isSameObject(Index_feedback t1, Index_feedback t2) {
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
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) return;
        if (requestCode == PROJECT_REQUEST_CODE) {
            Project project = (Project) data.getSerializableExtra("project");
            if (project != null) {
                mProjectText.setText(project.getName());
                mProjectId = project.getProject_id();
            }
        }
        
        if (requestCode == USER_REQUEST_CODE) {
            User user = (User) data.getSerializableExtra("user");
            if (user != null) {
                mUserText.setText(user.getName());
                mUserId = user.getUser_id();
            }
        }
    }
}


