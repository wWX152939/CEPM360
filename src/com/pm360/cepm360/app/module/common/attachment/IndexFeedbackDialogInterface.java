package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.entity.Index_feedback;

import java.util.Map;


public interface IndexFeedbackDialogInterface extends DialogInterface<Index_feedback> {
	
	// 返回类型，false不需要附件信息， true 需要附件信息
	public boolean getServerData(DataManagerInterface managerInterface, Index_feedback t);
	
	public int getDialogArray();
	
	public String[] getBeanAttr();
	
	public Map<String, Map<String, String>> getSwitchMap();
	
	public int getTitleStringArray();
}
