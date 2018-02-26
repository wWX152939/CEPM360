package com.pm360.cepm360.app.common.custinterface;

public interface SimpleListInterface<T> {
	/**
	 * 获取列表项ID
	 * @param t
	 * @return
	 */
	int getListItemId(T t);
	
	/**
	 * 获取列表项显示域
	 * @return
	 */
	String[] getDisplayFeilds();
}
