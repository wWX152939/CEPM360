package com.pm360.cepm360.app.common.adpater;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.common.custinterface.PageChangeListenerInterface;
import com.pm360.cepm360.app.common.custinterface.RelevanceChildInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager.FragmentViewPagerAdapterInterface;

import java.util.List;

/**
 * 为ViewPager添加布局（Fragment），绑定和处理fragments和viewpager之间的逻辑关系
 * 
 * Created with IntelliJ IDEA. Author: wangjie email:tiantian.china.2@gmail.com
 * Date: 13-10-11 Time: 下午3:03
 */
public class BaseFragmentViewPagerAdapter<T extends Fragment> extends PagerAdapter 
		implements PageChangeListenerInterface, FragmentViewPagerAdapterInterface {
	protected List<Fragment> mFragments; // 每个Fragment对应一个Page
	protected FragmentManager mFragmentManager;
	protected int mCurrentPageIndex = 0; // 当前page索引（切换之前）
	protected FragmentActivity mActivity;
	protected Bundle[] mBundle;
	private FloatingMenuView[] mMenus;
	
	public BaseFragmentViewPagerAdapter(FragmentActivity activity,
			Bundle[] bundle,
			List<Fragment> fragments, FloatingMenuView[] menus) {
		this(activity, bundle);
		mFragments = fragments;
		mMenus = menus;
	}
	
	public BaseFragmentViewPagerAdapter(FragmentActivity activity,
			Bundle[] bundle) {
		mActivity = activity;
		mBundle = bundle;
		mFragmentManager = activity.getSupportFragmentManager();
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mFragments.get(position).getView()); // 移出viewpager两边之外的page布局
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = mFragments.get(position);
		if (!fragment.isAdded()) { // 如果fragment还没有added
			if (mBundle!= null && mBundle[position] != null) {
				fragment.setArguments(mBundle[position]);
			}
			
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.add(fragment, fragment.getClass().getSimpleName());
			ft.commit();
			/**
			 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
			 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
			 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
			 */
			mFragmentManager.executePendingTransactions();
			
		}

		if (fragment.getView().getParent() == null) {
			container.addView(fragment.getView()); // 为viewpager增加布局
		}

		return fragment.getView();
	}

	/**
	 * 当前page索引（切换之前）
	 * 
	 * @return
	 */
	public int getCurrentPageIndex() {
		return mCurrentPageIndex;
	}
	
	
	public Fragment getCurrentFragment() {
		return mFragments.get(mCurrentPageIndex);
	};
	
	public List<? extends Fragment> getListFragment() {
		return mFragments;
	}

	@Override
	public void onExtraPageScrolled() {
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onExtraPageSelected(int position) {
		mFragments.get(mCurrentPageIndex).onPause(); // 调用切换前Fargment的onPause()
		
		if (mFragments.get(position).isAdded()) {

			if (mMenus != null && mMenus[position] == null) {
				mMenus[position] = ((RelevanceChildInterface) mFragments.get(position)).getFloatingMenu();
			}
			mFragments.get(position).onResume(); // 调用切换后Fargment的onResume()
		}
		
		mCurrentPageIndex = position;
	}

	@Override
	public void onExtraPageScrollStateChanged() {
		
	}

	@Override
	public boolean isCurrentPageCanUsed() {
		
		if (mFragments.get(mCurrentPageIndex).isAdded()) {
			return true;
		}
		
		// 为了在界面还没有初始化的时候，屏蔽父调用子加载数据
		return false;
	}

}
