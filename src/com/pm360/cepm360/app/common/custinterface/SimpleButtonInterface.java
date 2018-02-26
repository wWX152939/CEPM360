package com.pm360.cepm360.app.common.custinterface;

import android.view.View.OnClickListener;

public interface SimpleButtonInterface {
	/**
	 * 获取一组button显示内容
	 * @return
	 */
	String[] getNames();
	
	/**
	 * 获取一组button监听事件
	 * @return
	 */
	OnClickListener[] getListeners();
}
