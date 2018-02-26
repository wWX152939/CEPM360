package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.app.common.view.parent.BaseWindow;

import java.util.Map;


public interface WindowAdapterInterface extends SimpleWindowInterface {
	
	/**
	 * 获取界面中每项的风格
	 * @return
	 */
	Map<Integer, Integer> getWindowStyles();
	
	/**
	 * 获取特定风格的可选数据
	 * @return
	 */
	Map<Integer, String[]> getSupplyData();
	
	/**
	 * 额外初始化
	 */
	void additionalInit(BaseWindow window);
	
	/**
	 * 指定特定域的映射表
	 * @return
	 */
	Map<String, Map<String, String>> getUpdateFieldsSwitchMap();
}
