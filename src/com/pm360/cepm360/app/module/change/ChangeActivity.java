package com.pm360.cepm360.app.module.change;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.view.FragmentTabHost;
import com.pm360.cepm360.entity.Project;

import java.io.File;

public class ChangeActivity extends ActionBarFragmentActivity {
    private TextView mHomeView;
    private FragmentTabHost mTabHost;
    private View mTabSelectedIndicator;
    
    private Class<?>[] classArray;
    private Project mProject;
    private static final String CHANGE_DIR = Environment.getExternalStorageDirectory() + "/CEPM360/ChangeDocument/";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.change_activity);
        
        initWindow();
    }
    
    private void initWindow() {
        mProject = ProjectCache.getCurrentProject();
        if (mProject != null) {
            setActionBarTitle(mProject.getName());
        }
        mHomeView = (TextView) findViewById(R.id.home);
        mHomeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            }

        });
        
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(),
                android.R.id.tabcontent);
        String[] titles = getResources().getStringArray(
                R.array.change_navigation_titiles);
        classArray = new Class<?>[] { ChangeDocumentFragment.class };
        int[] icons = new int[] { R.drawable.ic_navigation_inventory_store };
        for (int i = 0; i < titles.length; i++) {
            TabSpec spec = mTabHost.newTabSpec(titles[i]);
            spec.setIndicator(createIndicatorView(this, mTabHost, icons[i],
                    titles[i]));
            mTabHost.addTab(spec, classArray[i], null);
        }
        
        mTabSelectedIndicator = findViewById(R.id.tab_selected_indicator_view);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                float y = mTabHost.getTabWidget()
                        .getChildAt(mTabHost.getCurrentTab()).getY();
                mTabSelectedIndicator.setY(y);
                mTabSelectedIndicator.requestLayout();
            }
        });
        
        File file = new File(CHANGE_DIR);
        if(!file.exists()) {
            file.mkdir();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Project project = ProjectCache.getCurrentProject();
        if (project != null && !project.equals(mProject)) {
            mProject = project;
            setActionBarTitle(mProject.getName());
        }
    }
    
    private View createIndicatorView(Context context, TabHost tabHost,
            int icon, String title) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabIndicator = inflater.inflate(R.layout.cost_navigation_item,
                tabHost.getTabWidget(), false);
        ImageView iconView = (ImageView) tabIndicator
                .findViewById(R.id.icon);
        TextView titleView = (TextView) tabIndicator
                .findViewById(R.id.title);
        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp18_s));
        iconView.setImageResource(icon);
        return tabIndicator;
    }
}
