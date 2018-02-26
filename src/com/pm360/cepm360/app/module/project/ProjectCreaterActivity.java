package com.pm360.cepm360.app.module.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;

import java.util.ArrayList;

/**
 * ProjectCreaterActivity 创建一个新的项目， 或修改项目人员
 * 
 * 1. 利用ProjectBaseInfoFregment设置项目基本信息
 * 2. UserDistributionFragment设置项目人员 （如果是修改或分配项目人员，直接切到这个fragment）
 *
 */
public class ProjectCreaterActivity extends FragmentActivity {
    
    private TextView mTitle;
    private ImageView mCancel;
    private Intent mIntent;
    private Project mProject;
    private int mType;
    private int mOperation;
    private int mEpsId;
    
    private FragmentManager mFragmentManager;
    private Fragment mShowingFragment;
    private String mFragmentTag;
    
    private ArrayList<User> mUserList = new ArrayList<User>();
    
    public void setProject(Project project) {
        mProject = project;
    }
    
    public Project getProject() {
        return mProject;
    }
    
    public void setResult(Project project) {
        mIntent.putExtra("project", project);
        setResult(RESULT_OK, mIntent);
        finish();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_creater_activity);
        
        mTitle = (TextView) findViewById(R.id.title);
        mCancel = (ImageView) findViewById(R.id.btn_close);
        mCancel.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();                
            }
        });
        
        mIntent = getIntent();
        mEpsId = mIntent.getIntExtra("eps_id", 0);
        mOperation = mIntent.getIntExtra("operation", -1);
        mProject = (Project) mIntent.getSerializableExtra("project");
        
        mFragmentManager = getSupportFragmentManager();
        
        if (mOperation == 2) { // 创建
            mTitle.setText(getString(R.string.project_add_common_title));
            switchContent(0);
        } else { // 修改或分配      
            mProject = (Project) mIntent.getSerializableExtra("project");
            mType = mIntent.getIntExtra("type", -1);
            mUserList.addAll((ArrayList<User>) mIntent.getSerializableExtra("user_list"));
            switchContent(1);
        }            
    }
    
    public void switchContent(int position) {
        if (position == 1) {
            mTitle.setText(getString(R.string.project_member));
        } else {
            mTitle.setText(getString(R.string.project_add_common_title));
        }
        String tag = String.valueOf(position);
        if (tag.equals(mFragmentTag)) {
            return;
        }

        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        mShowingFragment = mFragmentManager.findFragmentByTag(tag);
        if (mShowingFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("operation", mOperation);
            if (mOperation == 2) {
                bundle.putInt("eps_id", mEpsId);
            } else {
                bundle.putInt("type", mType);
                bundle.putSerializable("project", mProject);
                bundle.putSerializable("user_list", mUserList);
            }            
            mShowingFragment = Fragment.instantiate(this,
                    getFragmentClass(position), bundle);
        }

        if (mFragmentTag != null) {
            Fragment hideFragment = mFragmentManager
                    .findFragmentByTag(mFragmentTag);
            if (hideFragment != null)
                fragmentTransaction.hide(hideFragment);
        }

        if (mShowingFragment.isAdded()) {
            fragmentTransaction.show(mShowingFragment);
        } else {
            fragmentTransaction
                    .add(R.id.content_frame, mShowingFragment, tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
        mFragmentTag = tag;
    }
    
    private String getFragmentClass(int position) {
        String fragmentClass = null;
        switch (position) {
        case 0: // 项目基本信息
            fragmentClass = ProjectBaseInfoFragment.class.getCanonicalName();
            break;
        case 1: // 项目人员
            fragmentClass = UserDistributionFragment.class.getCanonicalName();
            break;
        }
        return fragmentClass;
    }
}
