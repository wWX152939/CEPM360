package com.pm360.cepm360.app.common.custinterface;

import android.view.MenuItem;


public interface ActionBarInterface {
	/**
	 * 获取actionBar图标资源ID
	 * @return
	 */
	int getActionBarMenu();
	
	/**
	 * actionBar监听事件
	 * @param item 监听传入的item
	 */
	void onActionItemClicked(MenuItem item);
	
}
