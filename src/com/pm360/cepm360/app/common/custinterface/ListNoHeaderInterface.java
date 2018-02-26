package com.pm360.cepm360.app.common.custinterface;

/**
 * 带对话框接口
 * @author yuanlu
 *
 * @param <T>
 */
public interface ListNoHeaderInterface<T> extends ListInterface<T> {
	
	/**
	 * 获取列表头布局ID
	 * @return
	 */
	int getListLayoutId();
	
	/**
	 * 获取列表项控件标识符资源ID
	 * @return
	 */
	int getListItemIds();
}
