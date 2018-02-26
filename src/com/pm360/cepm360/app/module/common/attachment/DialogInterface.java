package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;

import java.util.Map;


public interface DialogInterface<T> {
	
	// 返回类型，false不需要附件信息， true 需要附件信息
	public boolean getServerData(DataManagerInterface managerInterface, T t);
	
	public boolean isSameObject(Object t, int id);
	
	public int getDialogArray();
	
	public String[] getBeanAttr();
	
	public Map<String, Map<String, String>> getSwitchMap();
	
	public int getTitleStringArray();
}
