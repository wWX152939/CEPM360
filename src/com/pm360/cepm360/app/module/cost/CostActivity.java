
package com.pm360.cepm360.app.module.cost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.view.FragmentTabHost;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseSearchView;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostActivity extends ActionBarFragmentActivity {

    private FragmentTabHost mTabHost;
    private View mTabSelectedIndicator;

    private Class<?>[] classArray;
    private Project mProject;
    private BaseSearchView mBaseSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cost_activity);
        
        mBaseSearchView = new BaseSearchView(this);
        
        mProject = ProjectCache.getCurrentProject();
        setActionBarTitle(mProject.getName());
        ((TextView) findViewById(R.id.home)).setText(getString(R.string.cost_manager));
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        String[] titles = getResources().getStringArray(
                R.array.cost_navigation_titiles);
        int[] icons = new int[] {
                R.drawable.cost_accounting,
                /* R.drawable.cost_budget */
        };
        classArray = new Class<?>[] {
                AccountingFragment.class/*, BudgetFragment.class, */
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
            	initSearchConditionView(getSearchConditionLayout(), mTabHost.getCurrentTab());
                float y = mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).getY();
                mTabSelectedIndicator.setY(y);
                mTabSelectedIndicator.requestLayout();
            }
        });
        initSearchConditionView(getSearchConditionLayout(), mTabHost.getCurrentTab());
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
		case 1:
			enableSearchView(true);
			
			list.add(4);
			list.add(5);

			buttons.put(0, BaseDialog.spinnerLineStyle);
			buttons.put(4, BaseDialog.numberEditTextLineStyle);
			buttons.put(5, BaseDialog.calendarLineStyle);
			Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();
			String[] spinnerContent = new String[]{GLOBAL.CONTRACT_TYPE[0][1],
					GLOBAL.CONTRACT_TYPE[1][1], GLOBAL.CONTRACT_TYPE[2][1]};
			widgetContent.put(0, spinnerContent);
			mBaseSearchView.init(R.array.contract_search, buttons, widgetContent, list);			
			break;
		case 2:
		case 3:
			enableSearchView(false);
			break;
		}
        conditionLayout.addView(mBaseSearchView.getView());
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

    @Override
    protected void onResume() {
        super.onResume();
        Project project = ProjectCache.getCurrentProject();
        if (!project.equals(mProject)) {
            if (PermissionCache.hasSysModulePermission(4)) {
                mProject = project;
                setActionBarTitle(mProject.getName());
            } else {
                UtilTools.showToast(this, R.string.no_edit_permission);
                finish();
            }
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
        iconView.setImageResource(icon);
        return tabIndicator;
    }

}
