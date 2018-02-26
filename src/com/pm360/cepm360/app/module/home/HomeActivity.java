/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.module.home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.common.activity.PersonalSettings;
import com.pm360.cepm360.app.common.activity.ScreenObserver;
import com.pm360.cepm360.app.common.activity.ScreenObserver.ScreenStateListener;
import com.pm360.cepm360.app.common.adpater.SimplePagerAdapter;
import com.pm360.cepm360.app.common.lockpattern.LockPatternActivity;
import com.pm360.cepm360.app.module.combination.CombinationView;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Project;


/**
 * 
 * 标题: HomeActivity
 * 描述: Home主页界面 
 * 
 *  1.由ViewPager包含3个View组成
 *	2.CacheDataLoader 加载App全局缓存数据
 *	3.启动ScreenObserver监听锁屏
 *
 * 作者： Qiuwei Chen
 * 日期： 2015-12-16
 * 
 */
public class HomeActivity extends ActionBarActivity {

    private ViewPager mTabPager;
    private ArrayList<View> mPagerViews = new ArrayList<View>();    
    private ImageView mPageIndicator1;
    private ImageView mPageIndicator2;
    private ImageView mPageIndicator3;
    private PortalView mPortalView;
    private ModuleView mModuleview;
    private CombinationView mCombinationView;
    
    private SharedPreferences mSharedPreferences;
    private ScreenObserver mScreenObserver; 
       
    private int mEnterpriseType;
    private boolean mLoginLaunch;
    private String mPortalModelSequences;
    
    public static final int MODEL_EDIT_REQUEST_CODE = 1000;
    public static final int PROJECT_MODULE_REQUEST_CODE = 2000;
    
    private OnFreshEpsInterface mOnFreshEpsListener;    
    public interface OnFreshEpsInterface {
        public void onFreshEps();
        public void setProject(Project project);
    }
    
    public void setOnFreshEpsInterface(OnFreshEpsInterface listener) {
        mOnFreshEpsListener = listener;
    } 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //重置actionBar的返回键图标和监听为用户中心
        getBackView().setImageResource(R.drawable.personal);
        getBackView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PersonalSettings.class);
            }
        });

        setContentView(R.layout.home_layout);
        setUserName(UserCache.getCurrentUser().getName());
        
        mPageIndicator1 = (ImageView) findViewById(R.id.home_page_1);
        mPageIndicator2 = (ImageView) findViewById(R.id.home_page_2);
        mPageIndicator3 = (ImageView) findViewById(R.id.home_page_3);

        //获取用户类型和是否需要解锁
        Intent intent = getIntent();
        mEnterpriseType = intent.getIntExtra("type", 0);
        mLoginLaunch = intent.getBooleanExtra("login", false);
        
        //获取本地数据库的门户显示序列
        mSharedPreferences = getSharedPreferences(
                GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String sequence = mSharedPreferences.getString("portal_model_sequence", "");
        if (sequence.equals("")) {
            // 首次登录，默认显示所有门户并更新到本地数据库
            mPortalModelSequences = "0,1,2,3,4,5";
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("portal_model_sequence", mPortalModelSequences);
            editor.commit();
        } else {
            mPortalModelSequences = sequence;
        }
        
        // 门户页
        mPortalView = new PortalView(HomeActivity.this, mPortalModelSequences);
        mPagerViews.add(mPortalView.getView());
        
        if (mEnterpriseType == 0) {
            //管理方才加载组合界面
            mCombinationView = new CombinationView(this);
            mPagerViews.add(mCombinationView.getContentView());
        } else {
            mPageIndicator3.setVisibility(View.GONE);
        }

        // 功能模块页
        mModuleview = new ModuleView(HomeActivity.this, mEnterpriseType);
        mPagerViews.add(mModuleview.getView());
        
        mTabPager = (ViewPager) findViewById(R.id.tabpager1);
        mTabPager.setAdapter(new SimplePagerAdapter(mPagerViews));        
        if (mEnterpriseType == 0) { // 管理方
            mTabPager.setCurrentItem(1); // 默认主页 1
            updatePageIndicator(1);         
        } else {
            mTabPager.setCurrentItem(0); // 默认主页 0
            updatePageIndicator(0);         
        }
        mTabPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            	//页面滑动，同步更新下方小圆点
                updatePageIndicator(position);
            }
        });
        
        // 加载缓存数据
        CacheDataLoader dataHelper = new CacheDataLoader(this);
        dataHelper.setOnDataLoadedListener(new CacheDataLoader.OnDataLoadedListener() {
            
            @Override
            public void onDataLoaded(int flag) {
            	//请求完所有数据，开始刷新EPS
                if (mOnFreshEpsListener != null) {
                    mOnFreshEpsListener.onFreshEps();
                }
                
                setTenantName(TenantCache.getTenantName(UserCache.getCurrentUser().getTenant_id()+""));
            }
        });
        dataHelper.loadData();

        // 锁屏监听
        startScreenObserver();
        
        // 注册消息广播监听
        mPortalView.registerReceiver();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        // 只有在 系统设置 中进行 角色权限分配 时才会执行以下代码。
        boolean permissionChanged = mSharedPreferences.getBoolean(
                "sys_permission_changed", false);
        if (permissionChanged) {
            mModuleview.filterModuleBySysPermission();
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // do nothing.
    }

    @Override  
    protected void onDestroy() {  
        super.onDestroy();
        // 注销消息广播监听
        mPortalView.unregisterReceiver();
        mScreenObserver.stopScreenStateUpdate();
    }
    
    /**
     * 设置主页当前页的图标
     * @param page
     */
    private void updatePageIndicator(int page) {
        if (mEnterpriseType == 0) {
            mPageIndicator1.setImageResource(page == 0 ? R.drawable.icon_page_selected
                    : R.drawable.icon_page_unselect);
            mPageIndicator2.setImageResource(page == 1 ? R.drawable.icon_page_home_selected
                    : R.drawable.icon_page_home_unselect);

            mPageIndicator3.setImageResource(page == 2 ? R.drawable.icon_page_selected
                    : R.drawable.icon_page_unselect);        
        } else {
            mPageIndicator1.setImageResource(page == 0 ? R.drawable.icon_page_home_selected
                    : R.drawable.icon_page_home_unselect);
            mPageIndicator2.setImageResource(page == 1 ? R.drawable.icon_page_selected
                    : R.drawable.icon_page_unselect);
        }
    }
    
    /**
     * 开启锁屏观察者
     */
    private void startScreenObserver() {
        mScreenObserver = new ScreenObserver(this);
        mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
            @Override
            public void onScreenOn() {
                if (mLoginLaunch) {
                    mLoginLaunch = false;
                    return;
                }
                
                boolean state = ((CepmApplication) getApplicationContext()).getScreenLockState();
                if (!state) {
                    ((CepmApplication) getApplicationContext()).setScreenLockState(true);
                    String testChars = mSharedPreferences.getString("lock_pattern", "");
                    char[] savedPattern = testChars.toCharArray();
                    Intent compare = new Intent(
                            LockPatternActivity.ACTION_COMPARE_PATTERN, null,
                            HomeActivity.this, LockPatternActivity.class);
                    //compare.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    compare.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);                    
                    compare.putExtra("current_user", UserCache.getCurrentUser().getName());
                    //startActivityForResult(compare, REQ_COMPARE_PATTERN);
                    startActivity(compare);
                }
            }

            @Override
            public void onScreenOff() {
                // do nothing.
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode < 100) {
                // 主页点击打开某一个功能模块时，需要先选择项目，而弹出项目选择界面后，返回的结果
                mModuleview.doActivityResult(requestCode, resultCode, data);          
            } else {
                // 使用startActivityForResult打开某一个功能模块，返回的结果
                if (requestCode == PROJECT_MODULE_REQUEST_CODE) {
                    // 0 表示为【项目信息】模块，因为【项目信息】模块为第一个
                    // 【项目信息】模块返回， mTabPager 滑动到组合模块页，并定位到打开的项目
                    mTabPager.setCurrentItem(1);
                    Project project = (Project) data.getSerializableExtra("project");
                    mCombinationView.setProject(project);
                } else {
                    mCombinationView.doActivityResult(requestCode, resultCode, data);
                }
            }
        }
        // 首页门户单元块顺序的编辑，返回的结果
        if (requestCode == MODEL_EDIT_REQUEST_CODE) {
            mPortalView.doActivityResult(requestCode, resultCode, data);
        }
      
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
