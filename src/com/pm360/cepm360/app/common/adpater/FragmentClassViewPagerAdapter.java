package com.pm360.cepm360.app.common.adpater;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 为ViewPager添加布局（Fragment），绑定和处理fragments和viewpager之间的逻辑关系
 * 
 * Created with IntelliJ IDEA. Author: wangjie email:tiantian.china.2@gmail.com
 * Date: 13-10-11 Time: 下午3:03
 */
public class FragmentClassViewPagerAdapter<T extends Fragment> extends BaseFragmentViewPagerAdapter<T> {

	private Class<? extends Fragment>[] mTypeClass;
	private String[] mTitles;

	public FragmentClassViewPagerAdapter(FragmentActivity activity,
			Bundle[] bundle,
			Class<? extends Fragment>[] mFragments, String[] titles) {
		super(activity, bundle);
		mTypeClass = mFragments;
		mTitles = titles;
		initFragment();
	}
	
	private void initFragment() {
		mFragments = new ArrayList<Fragment>();
		for (int i = 0; i < mTitles.length; i++) {
			String title = mTitles[i];
			Fragment fragment = mFragmentManager.findFragmentByTag(title);
			if (fragment == null) {
				if (mBundle == null || mBundle[i] == null) {
					fragment = Fragment
							.instantiate(mActivity, mTypeClass[i].getCanonicalName());
				} else {
					fragment = Fragment
							.instantiate(mActivity, mTypeClass[i].getCanonicalName(), mBundle[i]);
				}
				
				mFragments.add(fragment);	
			}
		}
	}
	
	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return super.isViewFromObject(view, o);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		String title = mTitles[position];
		Fragment fragment = mFragments.get(position);

		if (!fragment.isAdded()) { // 如果fragment还没有added

			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.add(fragment, title);
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

}
