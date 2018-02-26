package com.pm360.cepm360.app.common.custinterface;

/**
 * 带对话框接口
 * @author yuanlu
 *
 * @param <T>
 */
public interface TicketListInterface<T> extends CommonListInterface<T> {	
	
	/**
	 * 获取列表项控件标识符资源ID
	 * @return
	 */
	int[] getEditTextNums();
	
	/**
	 * 判断数据是否填充
	 * @return
	 */
	boolean isDataValid();
}
