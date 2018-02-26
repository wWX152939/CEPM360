package com.pm360.cepm360.app.common.custinterface;

/**
 * 带对话框接口
 * @author yuanlu
 *
 * @param <T>
 */
public interface CommonListInterface<T> extends ListInterface<T> {	
	/**
	 * 获取列表头布局ID
	 * @return
	 */
	int getListHeaderLayoutId();
	
	/**
	 * 获取列表头布局ID
	 * @return
	 */
	int getListLayoutId();
	
	/**
	 * 获取列表头名字资源ID
	 * @return
	 */
	int getListHeaderNames();

	/**
	 * 获取列表头控件标识符资源ID
	 * @return
	 */
	int getListHeaderIds();
	
	/**
	 * 获取列表项控件标识符资源ID
	 * @return
	 */
	int getListItemIds();	
}
