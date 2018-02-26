package com.pm360.cepm360.app.module.document;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看或修改文档属性
 *
 */
public class DocumentPropertiesDialog extends Dialog {
    private Context mContext;
    private Files mFileItem;
    private Button mSave;
    private ImageView mCancel;
    private boolean mCanEditable;
    private PropertiesListAdapter mAdapter;
    

    public DocumentPropertiesDialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public DocumentPropertiesDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DocumentPropertiesDialog(Context context) {
        super(context);
        mContext = context;
    }
    
    public DocumentPropertiesDialog(Context context, boolean editable) {
        super(context, R.style.MyDialogStyle);
        mContext = context;
        mCanEditable = editable;
    }
    
    public void setFileItem(Files fileItem) {
        mFileItem = fileItem;
    }
    
    @SuppressLint("InflateParams")
    public void initLayout() {
        View dialogLayout = LayoutInflater.from(mContext).inflate(
                R.layout.document_search_document_details_dialog, null);
        
        ListView listView = (ListView) dialogLayout
                .findViewById(R.id.listView1);
        List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
        String[] displayItemNames = mContext.getResources().getStringArray(
                R.array.document_properties_common_names);
        Map<String, String> selectedFileMap = FilesToMap(mFileItem,
                displayItemNames);

        for (int i = 0; i < displayItemNames.length; i++) {
            Map<String, Object> itemlist = new HashMap<String, Object>();
            itemlist.put("KEY", displayItemNames[i]);
            itemlist.put("VALUE", selectedFileMap.get(displayItemNames[i]));
            datalist.add(itemlist);
        }

        mAdapter = new PropertiesListAdapter(mContext, dialogLayout, datalist);
        listView.setAdapter(mAdapter);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_close) {
                    dismiss();
                }
                if (view.getId() == R.id.btn_upload) {
                    updateFiles();
                }
            }
        };
        mSave = (Button) dialogLayout.findViewById(R.id.btn_upload);
        mSave.setOnClickListener(listener);
        if (!mCanEditable) mSave.setVisibility(View.INVISIBLE);
        
        mCancel = (ImageView) dialogLayout.findViewById(R.id.btn_close);
        mCancel.setOnClickListener(listener);
        
        setContentView(dialogLayout);
        setCancelable(false);
    }
    
    private Map<String, String> FilesToMap(Files bean, String[] displayItemNames) {
        Map<String, String> mapItem = new HashMap<String, String>();
        if (bean instanceof Files) {
            //mapItem.put(displayItemNames[0], bean.getCode());
            mapItem.put(displayItemNames[0], bean.getTitle());
            mapItem.put(displayItemNames[1], bean.getStatus());
            mapItem.put(displayItemNames[2], bean.getVersion());
            mapItem.put(displayItemNames[3], String.valueOf(bean.getScore()));
            mapItem.put(displayItemNames[4],
                    DateUtils.dateToString(DateUtils.FORMAT_LONG1,
                            bean.getCreate_time()));
            mapItem.put(displayItemNames[5], 
                    DateUtils.dateToString(DateUtils.FORMAT_LONG1, 
                            bean.getModified_time()));
        }
        return mapItem;
    }
    
    private void updateFiles() {
        Files bean = mFileItem;
        //bean.setCode(mAdapter.getItemEditText(0).toString());
        bean.setTitle(mAdapter.getItemEditText(0).toString());
        bean.setStatus(mAdapter.getItemEditText(1).toString());
        bean.setVersion(mAdapter.getItemEditText(2).toString());
        bean.setScore(Integer.parseInt(mAdapter.getItemEditText(3).toString()));
        bean.setModified_time(new Date());
        RemoteDocumentsService.getInstance().updateFile(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                switch (status.getCode()) {            
                case AnalysisManager.SUCCESS_DB_UPDATE:
                    dismiss();
                    break;
                }
            }            
        }, bean, new ArrayList<User>());
    }
  
    public class PropertiesListAdapter extends BaseAdapter {
        private Context mContext;
        List<? extends Map<String, ?>> mDataList;
        private View mView;
        
        public class ViewHolder {
            public TextView key;
            public EditText value;
        }

        public PropertiesListAdapter(Context context, View view,
                List<? extends Map<String, ?>> data) {
            mContext = context;
            mView = view;
            mDataList = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.document_properties_list_item, parent, false);
                holder = new ViewHolder();
                holder.key = (TextView) convertView.findViewById(R.id.key);
                holder.value = (EditText) convertView.findViewById(R.id.value);
                if (position != 0) {
                	holder.value.setFocusable(false);
                }
                convertView.setTag(holder);
            }

            Map<String, ?> item = getItem(position);
            holder.key.setText(item.get("KEY").toString());
            holder.value.setText(item.get("VALUE") != null ? item.get("VALUE")
                    .toString() : "");

            initLayout(convertView, holder, position);

            int index = mDataList.indexOf(item);
            holder.value.setTag(index);

            return convertView;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Map<String, ?> getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public Object getItemEditText(int index) {
            EditText et = (EditText) mView.findViewWithTag(index);
            Object text = et.getText();
            return text;
        }
        
        public void initLayout(View convertView, ViewHolder holder, final int position) {
            // 设置为不可编辑
            holder.value.setEnabled(mCanEditable);
            holder.value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    EditText et = (EditText) v;
                    String text = et.getText().toString();
                    if (!hasFocus) {
                        ((Map<String, String>) mAdapter.getItem(position)).put("VALUE", text);
                    }
                }
            });
        }
    }
}
