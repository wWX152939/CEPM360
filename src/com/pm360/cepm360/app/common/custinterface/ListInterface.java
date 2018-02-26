package com.pm360.cepm360.app.common.custinterface;

import java.util.Map;

/**
 * 无对话框的列表接口
 * @author yuanlu
 *
 * @param <T>
 */
public interface ListInterface<T> extends SimpleListInterface<T> {
	
	/**
	 * 指定特定域的映射表
	 * @return
	 */
	Map<String, Map<String, String>> getDisplayFieldsSwitchMap();
}
