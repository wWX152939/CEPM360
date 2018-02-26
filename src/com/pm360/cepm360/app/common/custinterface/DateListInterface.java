package com.pm360.cepm360.app.common.custinterface;

/**
 * 带显示日期格式的接口
 * @author onekey
 *
 * @param <T>
 */
public interface DateListInterface<T> extends CommonListInterface<T> {	
	/**
	 * 获取显示日期格式
	 * @return
	 */
	String getDateFormat();
		
}
