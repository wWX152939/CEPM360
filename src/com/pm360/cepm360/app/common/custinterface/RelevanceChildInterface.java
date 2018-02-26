package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.app.common.view.FloatingMenuView;


/**
 * 父子关系界面子需要实现的接口
 * 标题: RelevanceChildInterface 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年4月15日
 *
 */
public interface RelevanceChildInterface<B> extends SimpleRelevanceChildInterface<B>{
	
	/**
	 * 传递当前界面的浮动按钮，如果没有，返回NULL
	 * @return
	 */
	FloatingMenuView getFloatingMenu();
	
	/**
	 * 设置父对象
	 * @param b
	 */
	void setCurrentParentBean(B b);
	
	/**
	 * 是否需要viewPager处理当前界面的浮动按钮
	 * @return true 不需要 false 需要
	 */
	boolean isChildHandleFloatingMenuOnly();
}
