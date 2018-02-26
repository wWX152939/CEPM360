package com.pm360.cepm360.app.module.home.portal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class PortalMoreListActivity extends FragmentActivity {
    
    private int mModelIndex;
    private FragmentManager mFragmentManager;
    private Fragment mShowFragment;
    
    private ProgressDialog mProgressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_more_list_activity);
        
        mModelIndex = getIntent().getIntExtra("index", -1);
        mFragmentManager = getSupportFragmentManager();
        setContentFragment(mModelIndex);
        initTitleLayout();
    }
    
    private void initTitleLayout() {
    	TextView tv = (TextView) findViewById(R.id.edit_title);
    	String[] titleArray = getResources().getStringArray(R.array.home_portal_model_titles);
    	if (mModelIndex != -1) {
    		tv.setText(titleArray[mModelIndex]);
    	}
		
		ImageView iv = (ImageView) findViewById(R.id.btn_close);
    	iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }
    
    private void setContentFragment(int type) {
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        // 材料和设备为同一个fragment，为了区分
        Bundle args = new Bundle();
        args.putInt(MyTaskFragment.TASK_POSITION_KEY, getIntent().getIntExtra(MyTaskFragment.TASK_POSITION_KEY, -1));
        args.putSerializable(MyTaskFragment.TASK_INFO_KEY, getIntent().getSerializableExtra(MyTaskFragment.TASK_INFO_KEY));
        args.putStringArrayList("paths", getIntent().getStringArrayListExtra("paths"));
        mShowFragment = Fragment.instantiate(this,
                getFragmentClass(mModelIndex), args);

        if (mShowFragment.isAdded()) {
            fragmentTransaction.show(mShowFragment);
        } else {
            fragmentTransaction.add(R.id.content_fragment, mShowFragment);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }
    
    private String getFragmentClass(int type) {
        String fragmentClass = null;
        switch (type) {
        case 0: // 代办事项
            fragmentClass = TodoFragment.class.getCanonicalName();
            break;
        case 1: // 我的任务
            fragmentClass = MyTaskFragment.class.getCanonicalName();
            break;
        case 2: // 形象进展
            fragmentClass = ProgressFragment.class.getCanonicalName();
            break;
        case 3: // 最新反馈
            fragmentClass = FeedbackFragment.class.getCanonicalName();
            break;
        case 4: // 新闻公告
            fragmentClass = AnnouncementsFragment.class.getCanonicalName();
            break;
        case 5: // 最新文档
            fragmentClass = DocumentsFragment.class.getCanonicalName();
            break;
        }
        return fragmentClass;
    }
    
    public void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(PortalMoreListActivity.this, true, false);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
