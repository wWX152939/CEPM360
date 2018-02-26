package com.pm360.cepm360.app.module.document;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.view.DatePickText;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.SearchFiles;

import java.util.ArrayList;
import java.util.List;

public class SearchConditionBuilder {
    
    private Context mContext;
    private View mConditionView;
    private ListView mProjectListView;
    private MyProjectListAdapter mProjectListAdapter;
    private EditText mSearchTextInput;
    private ImageView mClearInputView;
    private ImageView mOpenProjectDialog;
    private DatePickText mCreateDateFrom, mCreateDateTo;
    private TextView mSelectedProjectList;

    private SearchFiles mSearchFiles;
    
    private String[] mDocumentTypeKeys;
    private Dialog mProjectSelectDialog;
    private String mSelectProjectId = "";
    private List<CheckBox> mCheckBoxGroup = new ArrayList<CheckBox>();
    private List<Project> mProjectDataLists = new ArrayList<Project>();
    
    public SearchConditionBuilder(Context context) {
        mContext = context;
        mSearchFiles = new SearchFiles();
        initView();
        for (Project project : ProjectCache.getProjectLists()) {
            mProjectDataLists.add(project);
        }
    }
    
    public View getSearchConditionView() {
        return mConditionView;
    }
    
    public EditText getSearchTextInput() {
        return mSearchTextInput;
    }
    
    public SearchFiles getSearchFiles() {
//        if (mCreateDateFrom.getDateSet() != null
//                && mCreateDateTo.getDateSet() != null) {
//            if (!validateDate(mCreateDateFrom.getDateSet(),
//                    mCreateDateTo.getDateSet())) {
//                Toast.makeText(mContext, mContext.getResources().getString(R.string.date_select_error), Toast.LENGTH_SHORT);
//                return;
//            }
//        }
        
        mSearchFiles.setContent(mSearchTextInput.getText().toString());
        mSearchFiles.setFile_type(getDocumentTypeSelected());
        mSearchFiles.setStart_time(mCreateDateFrom.getDateSet());
        mSearchFiles.setEnd_time(mCreateDateTo.getDateSet());
        mSearchFiles.setProject(mSelectProjectId);
        mSearchFiles.setTenant_id(UserCache.getCurrentUser()
                .getTenant_id());
        return mSearchFiles;
    }
    
    // 初始化搜索条件界面
    @SuppressLint("InflateParams")
    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mConditionView = inflater.inflate(
                R.layout.document_search_condition_layout, null);
        
        mClearInputView = (ImageView) mConditionView.findViewById(R.id.clear_input);
        mClearInputView.setOnClickListener(clickListener);        
        mSearchTextInput = (EditText) mConditionView
                .findViewById(R.id.search_content_edit);
        mSearchTextInput.requestFocus();
        mSearchTextInput.addTextChangedListener(mTextWatcher);
        mSearchTextInput
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            if (mSearchTextInput.getText().toString().length() > 0)
                                mClearInputView.setVisibility(View.VISIBLE);
                        }
                    }
                });

        mCreateDateFrom = (DatePickText) mConditionView
                .findViewById(R.id.create_date_from);
        mCreateDateTo = (DatePickText) mConditionView
                .findViewById(R.id.create_date_to);
        
        mSelectedProjectList = (TextView) mConditionView
                .findViewById(R.id.project_list_text);

        mOpenProjectDialog = (ImageView) mConditionView
                .findViewById(R.id.project_select_open);
        mOpenProjectDialog.setOnClickListener(clickListener);
        mDocumentTypeKeys = mContext.getResources().getStringArray(
                R.array.document_search_document_type_keys);

        CheckBox cb1 = (CheckBox) mConditionView.findViewById(R.id.checkbox_1);
        CheckBox cb2 = (CheckBox) mConditionView.findViewById(R.id.checkbox_2);
        CheckBox cb3 = (CheckBox) mConditionView.findViewById(R.id.checkbox_3);
        CheckBox cb4 = (CheckBox) mConditionView.findViewById(R.id.checkbox_4);
        CheckBox cb5 = (CheckBox) mConditionView.findViewById(R.id.checkbox_5);
        CheckBox cb6 = (CheckBox) mConditionView.findViewById(R.id.checkbox_6);
        CheckBox cb7 = (CheckBox) mConditionView.findViewById(R.id.checkbox_7);
        CheckBox cb8 = (CheckBox) mConditionView.findViewById(R.id.checkbox_8);
        mCheckBoxGroup.add(cb1);
        mCheckBoxGroup.add(cb2);
        mCheckBoxGroup.add(cb3);
        mCheckBoxGroup.add(cb4);
        mCheckBoxGroup.add(cb5);
        mCheckBoxGroup.add(cb6);
        mCheckBoxGroup.add(cb7);
        mCheckBoxGroup.add(cb8);
        for (int i = 0; i < mCheckBoxGroup.size(); i++) {
            mCheckBoxGroup.get(i).setTag(mDocumentTypeKeys[i]);
        }
    }
    
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            if (s.length() > 0) {
                mClearInputView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            if (s.length() > 0) {
                mClearInputView.setVisibility(View.VISIBLE);
            } else {
                mClearInputView.setVisibility(View.INVISIBLE);
            }
        }
    };
    
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.clear_input:
                mSearchTextInput.setText(null);
                mClearInputView.setVisibility(View.INVISIBLE);
                break;
            case R.id.project_select_open:
                showProjectListDialog();
                break;
            }
        }
    };
    
    @SuppressLint("InflateParams")
    private void showProjectListDialog() {
        View dialogLayout = LayoutInflater.from(mContext).inflate(
                R.layout.document_search_project_list_dialog, null);

        mProjectListView = (ListView) dialogLayout
                .findViewById(R.id.project_list);
        mProjectListAdapter = new MyProjectListAdapter(mContext,
                mProjectDataLists);
        mProjectListView.setAdapter(mProjectListAdapter);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                case R.id.btn_ok:
                    setSelecedProject(mProjectListAdapter.getSelecedProject());
                    mProjectSelectDialog.dismiss();
                    break;
                case R.id.btn_close:
                    mProjectListAdapter.clearSelecedProject();
                    mProjectSelectDialog.dismiss();
                    break;
                }
            }
        };
        TextView title = (TextView) dialogLayout.findViewById(R.id.edit_title);
        title.setText(R.string.project_list_title);
        Button confirm = (Button) dialogLayout.findViewById(R.id.btn_ok);
        ImageView cancel = (ImageView) dialogLayout.findViewById(R.id.btn_close);
        confirm.setOnClickListener(listener);
        cancel.setOnClickListener(listener);

        CheckBox selectAll = (CheckBox) dialogLayout
                .findViewById(R.id.cb_select_all);
        selectAll
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton button,
                            boolean isChecked) {
                        mProjectListAdapter.setSelectAll(isChecked);
                    }
                });

        mProjectSelectDialog = new AlertDialog.Builder(mContext)
                .setView(dialogLayout).setCancelable(false).show();
    }
    
    private void setSelecedProject(List<Project> selecedProject) {
        String projectNames = "";
        mSelectProjectId = "";
        for (int i = 0; i < selecedProject.size(); i++) {
            mSelectProjectId = mSelectProjectId
                    + selecedProject.get(i).getProject_id() + ",";
            projectNames = projectNames + selecedProject.get(i).getName()
                    + "\n";
        }
        if (mSelectProjectId.length() > 1)
            mSelectProjectId = mSelectProjectId.substring(0,
                    mSelectProjectId.length() - 1);
        mSelectedProjectList.setText(projectNames);
    }
    
    public void resetSearchCondition() {
        mCreateDateFrom.clear();
        mCreateDateTo.clear();
        mSelectedProjectList.setText("");
        for (int i = 0; i < mCheckBoxGroup.size(); i++) {
            mCheckBoxGroup.get(i).setChecked(false);
        }
        mSelectProjectId = "";
        if (mProjectListAdapter != null)
            mProjectListAdapter.clearSelecedProject();
    }
    
    private String getDocumentTypeSelected() {
        String types = "";
        for (int i = 0; i < mCheckBoxGroup.size(); i++) {
            if (mCheckBoxGroup.get(i).isChecked()) {
                types = types
                        + Utils.getFileFormat(mCheckBoxGroup.get(i).getTag()
                                .toString()) + ",";
            }
        }
        if (types.length() > 1)
            types = types.substring(0, types.length() - 1);
        return types;
    }
    
//    private boolean validateDate(Date from, Date to) {
//        if (to.after(from)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    
    // ListAdapter
    class MyProjectListAdapter extends BaseAdapter {
        private Context mContext;
        private List<Project> mDataList;
        private List<Project> mSelecedProject = new ArrayList<Project>();

        private boolean mSelectAll = false;

        public MyProjectListAdapter(Context context, List<Project> data) {
            mContext = context;
            mDataList = data;
            mSelecedProject.clear();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.document_search_project_list_item, parent,
                        false);
                holder = new ViewHolder();
                holder.checkbox = (CheckBox) convertView
                        .findViewById(R.id.checkbox);
                holder.name = (TextView) convertView
                        .findViewById(R.id.project_name);

                convertView.setTag(holder);
            }
            
            if (position % 2 == 1) {
                convertView.setBackgroundResource(R.color.content_listview_single_line_bg);
            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }
            

            regesterListeners(holder.checkbox, holder.name, position);
            holder.checkbox.setChecked(mSelectAll);
            holder.name.setText(getItem(position).getName());

            return convertView;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Project getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void regesterListeners(final CheckBox checkBox,
                TextView textView, final int position) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button,
                        boolean isChecked) {
                    if (isChecked) {
                        mSelecedProject.add(getItem(position));
                    } else {
                        mSelecedProject.remove(getItem(position));

                    }
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBox.setChecked(checkBox.isChecked() ? false : true);
                }
            });
        }

        public List<Project> getSelecedProject() {
            return mSelecedProject;
        }

        public void clearSelecedProject() {
            mSelecedProject.clear();
        }

        public void setSelectAll(boolean isChecked) {
            mSelectAll = isChecked;
            mSelecedProject.clear();
            notifyDataSetChanged();
        }

        class ViewHolder {
            CheckBox checkbox;
            TextView name;
        }
    }
}
