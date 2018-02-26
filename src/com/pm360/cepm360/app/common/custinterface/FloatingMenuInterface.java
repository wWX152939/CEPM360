package com.pm360.cepm360.app.common.custinterface;

import android.widget.AdapterView.OnItemClickListener;

public interface FloatingMenuInterface {
	/**
	 * 获取浮动菜单图片资源ID数组
	 * @return
	 */
	int[] getFloatingMenuImages();
	
	/**
	 * 获取浮动菜单提示字符串数组
	 * @return
	 */
	String[] getFloatingMenuTips();
	
	/**
	 * 获取浮动菜单监听器
	 * @return
	 */
	OnItemClickListener getFloatingMenuListener();
}
