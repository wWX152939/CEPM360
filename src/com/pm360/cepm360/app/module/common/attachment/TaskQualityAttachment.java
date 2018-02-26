package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.module.schedule.QualityFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Quality;
import com.pm360.cepm360.services.plan.RemoteQualityService;

import java.util.HashMap;
import java.util.Map;


public class TaskQualityAttachment implements IndexFeedbackDialogInterface {

	@Override
	public boolean getServerData(DataManagerInterface managerInterface,
			Index_feedback indexFeedback) {
		RemoteQualityService.getInstance().getQualityList(managerInterface, indexFeedback.getTask_id());
		return true;
	}
	
	@Override
	public boolean isSameObject(Object t, int id) {
		if (((Quality) t).getId() == id) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getDialogArray() {
		return R.array.safety_dialog_names;
	}

	@Override
	public String[] getBeanAttr() {
		return QualityFragment.mDialogString;
	}

	@Override
	public Map<String, Map<String, String>> getSwitchMap() {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		Map<String, String> subMap2 = new HashMap<String, String>();
		for (int i = 0; i < GLOBAL.QUALITY_TYPE.length; i++) {
			subMap2.put(GLOBAL.QUALITY_TYPE[i][0], GLOBAL.QUALITY_TYPE[i][1]);
			subMap2.put(GLOBAL.QUALITY_TYPE[i][1], GLOBAL.QUALITY_TYPE[i][0]);
		}
		
		map.put("type", subMap2);
		return map;
	}

	@Override
	public int getTitleStringArray() {
		return R.string.quality_info;
	}

}
