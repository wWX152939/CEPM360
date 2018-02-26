package com.pm360.cepm360.app.common.custinterface;

public interface ServiceInterface<T> extends SimpleServiceInterface<T> {
	
	/**
	 *  添加一个内容列表项
	 * @param c
	 */
	void addItem(T t);
	
	/**
	 *  删除一个内容列表项
	 */
	void deleteItem(T t);
	
	/**
	 *  修改一个内容列表项
	 * @param c
	 */
	void updateItem(T t);
}
