package com.pm360.cepm360.app.common.custinterface;

import java.util.Map;

public interface SimpleWindowInterface {
	
	/**
	 * 获取对话框标签名数组资源ID
	 * @return
	 */
	int getWindowLableNames();
	
	/**
	 * 获取添加修改对话框显示域（更新域）
	 * @return
	 */
	String[] getUpdateFeilds();
	
	/**
	 * 界面中必须填写的项
	 * @return
	 */
	Integer[] getImportantColumns(Map<String, String> saveDataMap);
}
