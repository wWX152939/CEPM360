package com.pm360.cepm360.app.module.schedule;

import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Logic;
import com.pm360.cepm360.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class LogicController {
	
	/**
	 * calculate valid task for mTaskInfoBean
	 * 
	 * @param logicList
	 *            filter the logciList
	 * @return
	 */
	public ArrayList<Task> CalculateLogicTask(ArrayList<Logic> logicList, Task mTaskInfoBean, List<Task> mPlanList, boolean mLogicFrontTaskFlag) {
		Task currentTask = mTaskInfoBean;
		if (currentTask == null) {
			return null;
		}

		ArrayList<Task> task = new ArrayList<Task>();

		if (mLogicFrontTaskFlag) {
			for (int i = 0; i < mPlanList.size(); i++) {
				if (currentTask.getStart_time() == null
						|| mPlanList.get(i).getStart_time() == null) {
					continue;
				}
				if (currentTask.getStart_time().equals("")
						|| mPlanList.get(i).getStart_time().equals("")) {
					continue;
				}
				if (currentTask.getStart_time().getTime() >= mPlanList.get(i)
						.getStart_time().getTime()) {
					if (!mPlanList.get(i).getType()
							.equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
						if (currentTask.getTask_id() != mPlanList.get(i)
								.getTask_id()) {
							if (logicList != null && logicList.size() != 0) {
								int j = 0;
								for (; j < logicList.size(); j++) {
									if (logicList.get(j).getTask_name()
											.equals(mPlanList.get(i).getName())) {
										break;
									}
								}
								if (j == logicList.size()) {
									task.add(mPlanList.get(i));
								}
							} else {
								task.add(mPlanList.get(i));
							}
						}
					}
				}
			}
		} else {
			for (int i = 0; i < mPlanList.size(); i++) {
				if (currentTask.getStart_time() == null
						|| mPlanList.get(i).getStart_time() == null) {
					continue;
				}
				if (currentTask.getStart_time().equals("")
						|| mPlanList.get(i).getStart_time().equals("")) {
					continue;
				}
				if (currentTask.getStart_time().getTime() < mPlanList.get(i)
						.getStart_time().getTime()) {
					if (!mPlanList.get(i).getType()
							.equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
						if (currentTask.getTask_id() != mPlanList.get(i)
								.getTask_id()) {
							if (logicList != null && logicList.size() != 0) {
								for (int j = 0; j < logicList.size(); j++) {
									if (!logicList.get(j).getTask_name()
											.equals(mPlanList.get(i).getName())) {
										task.add(mPlanList.get(i));
										break;
									}
								}
							} else {
								task.add(mPlanList.get(i));
							}
						}
					}
				}
			}
		}
		return task;
	}
}
