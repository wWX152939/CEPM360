package com.pm360.cepm360.app.common.custinterface;

import java.util.Map;

/**
 * 带对话框接口
 * @author yuanlu
 *
 * @param <T>
 */
public interface EditableTicketListInterface<T> extends TicketListInterface<T> {	
	
	/**
	 * 修改displayFieldMap适应显示区域
	 * @return
	 */
	void modifyFieldMap(Map<String, String> displayFieldMap, T t, String field);
}
