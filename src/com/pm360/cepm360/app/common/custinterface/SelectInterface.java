package com.pm360.cepm360.app.common.custinterface;

import java.util.List;

public interface SelectInterface {
	
	/**
	 * 获取已选择数据列表
	 * @return
	 */
	public List<?> getSelectedDataList();
	
	/**
	 * 设置过滤数据
	 * @param filters
	 */
	public void setFilterData(List<?> filters);
	
	/**
	 * 显示内部选择按钮
	 * @param enable
	 */
	public void enableInnerButton(boolean enable);
}
