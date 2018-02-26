package com.pm360.cepm360.app.module.subcontract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.view.FragmentTabHost;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseSearchView;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubcontractManagementActivity extends ActionBarFragmentActivity {

    private TextView mHomeTextView;
    private FragmentTabHost mTabHost;
    private View mTabSelectedIndicator;

    private Class<?>[] classArray;
    private Project mProject;

	private BaseSearchView mBaseSearchView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mProject = ProjectCache.getCurrentProject();
        if (mProject != null) {
            setActionBarTitle(mProject.getName());
        }
		mBaseSearchView = new BaseSearchView(this);
        mHomeTextView = (TextView) findViewById(R.id.home);
        mHomeTextView.setText(getString(R.string.subcontract_management));
        mHomeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            }
        });
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        String[] titles = getResources().getStringArray(
                R.array.subcontrat_navigation_titiles);
        classArray = new Class<?>[] {
                SubcontractManagerFragment.class 
        };
        int[] icons = new int[] {
                R.drawable.ic_navigation_subcontract
        };
        for (int i = 0; i < titles.length; i++) {
            TabSpec spec = mTabHost.newTabSpec(titles[i]);
            spec.setIndicator(createIndicatorView(this, mTabHost,
                    icons[i], titles[i]));
            mTabHost.addTab(spec, classArray[i], null);

        }
        mTabSelectedIndicator = findViewById(R.id.tab_selected_indicator_view);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                float y = mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).getY();
                mTabSelectedIndicator.setY(y);
                mTabSelectedIndicator.requestLayout();
            }
        });
        enableSearchView(true);
        initSearchConditionView(getSearchConditionLayout(), 0);
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
    
	@SuppressLint("UseSparseArrays") 
	private void initSearchConditionView(LinearLayout conditionLayout, int position) {
		List<Integer> list = new ArrayList<Integer>();
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		if (mBaseSearchView != null) {
			conditionLayout.removeView(mBaseSearchView.getView());
		}		
		switch(position) {
		case 0:
			list.add(2);
			list.add(3);
			buttons.put(2, BaseDialog.decimalEditTextLineStyle);
			buttons.put(3, BaseDialog.calendarLineStyle);
			mBaseSearchView.init(R.array.subcontract_search, buttons, null, list);
			break;
		case 1:
			break;
		case 2:
			break;
		}
		View view = mBaseSearchView.getView();
        conditionLayout.addView(view);
        setSearchListener(new SearchListener() {
			
			@Override
			public void doSearch() {
				
			}
			
			@Override
			public void doReset() {
				mBaseSearchView.SetDefaultValue(null);
			}
		});
	}

}
