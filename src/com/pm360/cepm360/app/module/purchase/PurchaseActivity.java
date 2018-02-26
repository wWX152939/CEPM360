package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.adpater.NavigationAdapter;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseSearchView;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseActivity extends ActionBarFragmentActivity {
	
	private SlidingPaneLayout mSlidingPane;
	private ListView mNavigationListView;
	private NavigationAdapter mNavigationAdapter;
	private FragmentManager mFragmentManager;
	private String mCurrentTitle;
	private String[] mItems;
	private int[] mIcons;

	private int startWindowFlow = 0;
	
	private static final int PROJECT_REQUEST_CODE = 100;
	
	public View getSlidingPaneLayout() {
		return mSlidingPane;
	}
	
	private void initScheduleWindow() {
		setContentView(R.layout.inventory_sliding_layout);
		mSlidingPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);

        mNavigationListView = (ListView) findViewById(R.id.navigation_listView);
        TypedArray typedArray = getResources().obtainTypedArray(
                R.array.purchase_navigation_icons);
        mIcons = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            mIcons[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        mItems = getResources().getStringArray(
                R.array.purchase_navigation_titles);
        mNavigationAdapter = new NavigationAdapter(this, mIcons, mItems);
        mNavigationListView.setAdapter(mNavigationAdapter);
        mNavigationListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        Project project = ProjectCache.getCurrentProject();
                        // 外包人工需要先选择项目才能打开
                        if (project == null && position == 3) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClass(PurchaseActivity.this,
                                    ProjectSelectActivity.class);
                            startActivityForResult(intent,
                                    PROJECT_REQUEST_CODE);
                        } else {
                            switchContent(position);                        
                            mNavigationAdapter.setSelected(position);
                        }
                    }
                });
		
		mFragmentManager = getSupportFragmentManager();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
        case PROJECT_REQUEST_CODE:
            switchContent(3);
            mNavigationAdapter.setSelected(3);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	private BaseSearchView mBaseSearchView;
	private Message mMessage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBaseSearchView = new BaseSearchView(this);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		initScheduleWindow();
		
		Intent intent = getIntent();
		if (intent.getAction() != null && intent.getAction().equals(GLOBAL.MSG_EXECUTIVE_ACTION)) {
			startWindowFlow = 2;
			mMessage = (Message) intent.getSerializableExtra("message");
			setActionBarTitle(ProjectCache.getProjectIdMaps().get(Integer.toString(mMessage.getProject_id())));
		} else {
			setActionBarTitle(ProjectCache.getCurrentProject().getName());
		}
		
		switchContent(startWindowFlow);
		mSlidingPane.openPane();
	}
	
	@SuppressLint("UseSparseArrays") 
	private void initSearchConditionView(LinearLayout conditionLayout, int position) {
		enableSearchView(true);
		List<Integer> list = new ArrayList<Integer>();
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();

		if (mBaseSearchView != null) {
			conditionLayout.removeView(mBaseSearchView.getView());
		}
		
		switch(position) {
		case 0:
			list.add(4);
			list.add(5);
			list.add(6);

			buttons.put(4, BaseDialog.numberEditTextLineStyle);
			buttons.put(5, BaseDialog.decimalEditTextLineStyle);
			buttons.put(6, BaseDialog.calendarLineStyle);
			mBaseSearchView.init(R.array.purchase_budget_search, buttons, null, list);
			break;
		case 1:
			list.add(7);
			list.add(5);
			list.add(6);
			buttons.put(5, BaseDialog.decimalEditTextLineStyle);
			buttons.put(6, BaseDialog.calendarLineStyle);
			buttons.put(7, BaseDialog.calendarLineStyle);
			mBaseSearchView.init(R.array.purchase_plan_search, buttons, null, list);
			break;
		case 2:
			list.add(4);
			list.add(5);
			buttons.put(2, BaseDialog.spinnerLineStyle);
			buttons.put(4, BaseDialog.decimalEditTextLineStyle);
			buttons.put(5, BaseDialog.calendarLineStyle);
			Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();
			String[] spinnerContent = new String[]{GLOBAL.CG_TYPE[0][1], GLOBAL.CG_TYPE[1][1]};
			widgetContent.put(2, spinnerContent);
			mBaseSearchView.init(R.array.purchase_executive_search, buttons, widgetContent, list);
			break;
		}
        conditionLayout.addView(mBaseSearchView.getView());
        setSearchListener(new SearchListener() {
			
			@Override
			public void doSearch() {
				
			}
			
			@Override
			public void doReset() {
//				mBaseSearchView.SetDefaultValue(null);
			}
		});
	}

	public void switchContent(int position) {

		initSearchConditionView(getSearchConditionLayout(), position);
		
        String title = mNavigationAdapter.getItem(position);
		if (title.equals(mCurrentTitle)) {
			return;
		}

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,
				R.anim.slide_out_left, R.anim.slide_in_right,
				R.anim.slide_out_right);

		Fragment showFragment = mFragmentManager.findFragmentByTag(title);
		if (showFragment == null) {
			showFragment = Fragment.instantiate(this,
					getFragmentClass(position));
		}
		if (mCurrentTitle != null) {
			Fragment hideFragment = mFragmentManager
					.findFragmentByTag(mCurrentTitle);
			if (hideFragment != null)
				fragmentTransaction.hide(hideFragment);
		}

		if (showFragment.isAdded()) {
			fragmentTransaction.show(showFragment);
		} else {
			if (position == 2) {
				if (startWindowFlow == 2) {
					Bundle args = new Bundle();
					args.putInt(PurchaseExecutiveFragment.PURCHASE_MSG_VALUE, mMessage.getType_id());
					showFragment.setArguments(args);
				}
			}
			fragmentTransaction.add(R.id.content_frame, showFragment, title);
		}
		fragmentTransaction.commitAllowingStateLoss();
		mCurrentTitle = title;
		// mSlidingPane.closePane();

	}

	private String getFragmentClass(int position) {
		String fragmentClass = PurchaseBudgetFragment.class.getCanonicalName();
		switch (position) {
		case 0:
			fragmentClass = PurchaseBudgetFragment.class.getCanonicalName();
			break;
		case 1:
			fragmentClass = PurchasePlanFragment.class.getCanonicalName();
			break;
		case 2:
			fragmentClass = PurchaseExecutiveFragment.class.getCanonicalName();
			break;
		case 3:
            fragmentClass = SubcontractManagerFragment.class.getCanonicalName();
            break;
		}
		return fragmentClass;
	}
}
