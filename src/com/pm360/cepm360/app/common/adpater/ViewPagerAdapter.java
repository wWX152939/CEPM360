package com.pm360.cepm360.app.common.adpater;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * 
 * 标题: ViewPagerAdapter 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年4月16日
 *
 */
public class ViewPagerAdapter extends PagerAdapter {

	private List<View> mChildViewList;
	public ViewPagerAdapter(List<View> viewList) {
		mChildViewList = viewList;
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public int getCount() {
		return mChildViewList.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(mChildViewList.get(position));
	}
	
	
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(mChildViewList.get(position));
		return mChildViewList.get(position);
	}
	

}
