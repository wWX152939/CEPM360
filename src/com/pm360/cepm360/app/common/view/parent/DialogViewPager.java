package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.FloatingMenuView;

import java.util.List;

/*
 * ViewPager基类
 */
@SuppressLint("ResourceAsColor") 
public class DialogViewPager extends BaseViewPager {

	public DialogViewPager(FragmentActivity activity, View view) {
		super(activity, view);
	}
	
	public ViewPager init(int arrayId, final List<View> views, FloatingMenuView[] menus) {
		ViewPager viewPager = super.init(arrayId, views, menus);
		RightImageViewTitle.setVisibility(View.GONE);
		return viewPager;
	}
	
	protected void commonInitFragments(String[] stringArray) {
		super.commonInitFragments(stringArray);
		RightImageViewTitle.setVisibility(View.GONE);
	}
	
	protected void updateResource(int arg0) {
		for (int i = 0; i < mViewPagerNames.length; i++) {
			if (i == arg0) {
				tvs[i].setBackgroundResource(R.color.task_child_list_table);
			} else {
				tvs[i].setBackgroundResource(R.color.view_pager_unselect);
			}
			tvs[i].setGravity(Gravity.CENTER);
		}
	}
}
