package com.pm360.cepm360.app.common.custinterface;

import android.support.v4.widget.SlidingPaneLayout;

import com.pm360.cepm360.app.common.view.FloatingMenuView;

import java.util.List;

public interface ListViewPagerInterface {
	
	/**
	 * 获取标题数组资源ID
	 * @return
	 */
	public int getTitleResourceId();
	
	/**
	 * 获取标签页视图
	 * @return
	 */
	public List<ViewPagersInterface<?>> getViewPagers();
	
	/**
	 * 获取所有页的浮动菜单控件
	 * @return
	 */
	public FloatingMenuView[] getFloatingMenuViews();
	
	/**
	 * 获取滑动面板
	 * @return
	 */
	public SlidingPaneLayout getSlidingPaneLayout();
}
