package com.pm360.cepm360.app.module.project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.view.DatePickText;
import com.pm360.cepm360.app.common.view.RightButtonEditText;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.util.Date;
import java.util.List;

/**
 * 设置项目基本信息
 *
 */
public class ProjectBaseInfoFragment extends Fragment {
    
    private ProjectCreaterActivity mActivity;
    
    private TextView mNumber;
    private EditText mName, mLocation, mMark;
    private DatePickText mStartDate, mEndDate;
    private RightButtonEditText mOwner;
    private Button mButtonMiddle;
    
    private int mOwnerId;
    private int mUser_id;
    private int mEpsId = 0;
    private ProgressDialog mProgressDialog;
    
    private static final int OWNER_DISTRIBUTION_REQUEST = 100;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        View view = inflater.inflate(
                R.layout.project_base_info_fragment, container,
                false);
        
        mActivity = (ProjectCreaterActivity) getActivity();
        mUser_id = UserCache.getCurrentUser().getUser_id();
        mEpsId = getArguments().getInt("eps_id");
        
        initView(view);
        setupButtonBar(view);
        getProjectNumber();
        
        return view;
    }

    private void initView(View view) {
        mNumber = (TextView) view
                .findViewById(R.id.project_add_number);
        mNumber.setEnabled(false);
        mName = (EditText) view.findViewById(R.id.project_add_name);

        Date date = new Date(System.currentTimeMillis());
        mStartDate = (DatePickText) view
                .findViewById(R.id.project_add_start_date);
        mStartDate.setDate(date);
        mEndDate = (DatePickText) view
                .findViewById(R.id.project_add_end_date);
        mEndDate.setDate(date);
        mLocation = (EditText) view
                .findViewById(R.id.project_add_location);
        mOwner = (RightButtonEditText) view
                .findViewById(R.id.project_add_department);
        mOwner.getEditText().setEnabled(false);
        mOwner.getRightButton().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), OwnerSelectActivity.class);
                intent.putExtra("title", getString(R.string.project_department));
                startActivityForResult(intent, OWNER_DISTRIBUTION_REQUEST);
            }
        });
        mMark = (EditText) view.findViewById(R.id.project_add_mark);
    }
    
    private void setupButtonBar(View view) {
        mButtonMiddle = (Button) view.findViewById(R.id.button_next);
        mButtonMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    setProject();
                    mActivity.switchContent(1);
                }
            }
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case OWNER_DISTRIBUTION_REQUEST:
                User user = (User) data.getSerializableExtra("user");
                if (user != null) {
                    mOwner.getEditText().setText(user.getName());
                    mOwnerId = user.getUser_id();
                }
                break;
            }
        }
    }
    
    private void getProjectNumber() {
        showProgressDialog("Getting Code ...");
        RemoteCommonService.getInstance().getCodeByDate(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    mNumber.setText(status.getMessage().toString());
                }
            }
        }, "XM");
    }
    
    private void setProject() {
        Project project = new Project();
        project.setEps_id(mEpsId);
        project.setProject_number(mNumber.getText().toString());
        project.setName(mName.getText().toString());
        project.setActual_start_time(mStartDate.getDate());
        project.setActual_end_time(mEndDate.getDate());
        project.setLocation(mLocation.getText().toString());
        project.setOwner(mOwnerId);
        project.setMark(mMark.getText().toString());
        project.setCreater(mUser_id);
        project.setUser_id(mUser_id);
        project.setTenant_id(UserCache.getCurrentUser()
                .getTenant_id());
        mActivity.setProject(project);
    }
    
    private boolean validateData() {
        if (mNumber.getText().toString().equals("")) {
            UtilTools.showToast(mActivity, R.string.project_add_err1);
            return false;
        }

        if (mName.getText().toString().equals("")) {
            UtilTools.showToast(mActivity, R.string.project_add_err2);
            return false;
        }
        return true;
    }
    
    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(mActivity, true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
