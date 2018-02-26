package com.pm360.cepm360.app.common.custinterface;

import android.view.View;

public interface ViewPagersInterface<T> extends SimpleRelevanceChildInterface<T> {

	/**
	 * 获取页实现的根布局视图，BaseViewPager通过该函数获取
	 * 每个页填充的根视图
	 * @return
	 */
	public View getRootView();
}
