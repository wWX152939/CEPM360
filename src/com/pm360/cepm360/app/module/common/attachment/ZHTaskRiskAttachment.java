package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.module.combination.RiskFragment;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.ZH_group_risk;
import com.pm360.cepm360.services.group.RemoteRiskService;

import java.util.Map;


public class ZHTaskRiskAttachment implements IndexFeedbackDialogInterface {

	@Override
	public boolean getServerData(DataManagerInterface managerInterface,
			Index_feedback indexFeedback) {
		RemoteRiskService.getInstance().getRiskList(managerInterface, indexFeedback.getTask_id());
		return true;
	}
	
	@Override
	public boolean isSameObject(Object t, int id) {
		if (((ZH_group_risk) t).getId() == id) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getDialogArray() {
		return R.array.combination_risk_dialog;
	}

	@Override
	public String[] getBeanAttr() {
		return RiskFragment.mDialogString;
	}

	@Override
	public Map<String, Map<String, String>> getSwitchMap() {
		return null;
	}
	
	@Override
	public int getTitleStringArray() {
		return R.string.risk_info;
	}
}
