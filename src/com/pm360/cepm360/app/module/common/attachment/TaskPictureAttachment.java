package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.plan.RemoteTaskService;

import java.util.Map;


public class TaskPictureAttachment implements IndexFeedbackDialogInterface {
	private int mType; // 1: xianchangtupianPicture 2: XingxiangchengguoPicture
	
	public TaskPictureAttachment(int type) {
		mType = type;
	}
	
	@Override
	public boolean getServerData(DataManagerInterface managerInterface,
			Index_feedback indexFeedback) {
		LogUtil.i("wzw indexFeedback:" + indexFeedback);
		if (mType == 1) {
			Task task = new Task();
			task.setTask_id(indexFeedback.getTask_id());
			RemoteTaskService.getInstance().setTask(task).getXianChangFiles(managerInterface, indexFeedback.getTenant_id());
		} else {
			ZH_group_task task = new ZH_group_task();
			task.setTask_id(indexFeedback.getTask_id());
			com.pm360.cepm360.services.group.RemoteTaskService.getInstance().setTask(task).getXingXiangFiles(managerInterface, indexFeedback.getTenant_id());
		}
		return true;
	}
	
	@Override
	public boolean isSameObject(Object t, int id) {
		if (((Files) t).getFile_id() == id) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getDialogArray() {
		return R.array.feedback_xc_dialog_lable_names;
	}

	@Override
	public String[] getBeanAttr() {
		return new String[] {"title", "mark"};
	}

	@Override
	public Map<String, Map<String, String>> getSwitchMap() {
		return null;
	}
	
	@Override
	public int getTitleStringArray() {
		if (mType == 1) {
			return R.string.xianchang_info;
		} else {
			return R.string.xingxiang_info;
		}
		
	}

}
