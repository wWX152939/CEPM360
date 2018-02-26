package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.FeedbackCell;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.plan.RemoteTaskService;

import java.util.HashMap;
import java.util.Map;


public class TaskFeedbackAttachment implements SetValueDialogInterface {
	
	private int mType; // type 1: jihuan 2: zuhe
	public TaskFeedbackAttachment(int type) {
		mType = type;
	}
	
	@Override
	public int getDialogArray() {
		return R.array.task_feedback_attachment_dialog_label_name;
	}

	@Override
	public String[] getBeanAttr() {
		return new String[] {"actual_start_time", "actual_end_time" , "progress", "feedback_creater", "cc_user" , "status", "mark"};
	}

	@Override
	public Map<String, Map<String, String>> getSwitchMap() {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		Map<String, String> submap1 = new HashMap<String, String>();
		for (int i = 0; i < GLOBAL.TASK_STATUS_TYPE.length; i++) {
			submap1.put(GLOBAL.TASK_STATUS_TYPE[i][0], GLOBAL.TASK_STATUS_TYPE[i][1]);
		}
		map.put("status", submap1);
		Map<String, String> submap2 = new HashMap<String, String>();
		for (int i = 0; i <= 100; i++) {
			submap2.put(i + "", i + "%");
		}
		map.put("progress", submap2);
		map.put("feedback_creater", UserCache.getUserMaps());
		
		return map;
	}

	@Override
	public boolean getServerData(DataManagerInterface managerInterface,
			Index_feedback indexFeedback) {
		Project project = new Project();
		project.setProject_id(indexFeedback.getProject_id());
		if (mType == 1) {
			RemoteTaskService.getInstance().getFeedbackList(managerInterface, project);
		} else {
			com.pm360.cepm360.services.group.RemoteTaskService.getInstance().getFeedbackList(managerInterface, indexFeedback.getZh_group_id());
		}
		
		return false;
	}
	
	@Override
	public boolean isSameObject(Object t, int id) {
		if (((FeedbackCell) t).getTask_id() == id) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getTitleStringArray() {
		return R.string.feedback_info;
	}

	@Override
	public String getStringValue(String key, String value) {
		String retValue = null;
		if (key.equals("cc_user")) {
			Map<String, String> mUserMap = UserCache.getUserMaps();
			if (value != null && !value.equals("")) {
				String ccUserIds = value.replaceAll("\\(|\\)", "");
				String ccUserId[] = ccUserIds.split(",");
				String ccUserNames = "";
				for (int i = 0; i < ccUserId.length; i++) {
					if (mUserMap.containsKey(ccUserId[i])) {
						ccUserNames += mUserMap.get(ccUserId[i]) + ",";
					}
				}
				retValue = ccUserNames.substring(0, ccUserNames.length() - 1);
			}
		}
		return retValue;
	}
	
}
