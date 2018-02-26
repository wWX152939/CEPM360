package com.pm360.cepm360.core.combination;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.core.TreePresenter;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Feedback;
import com.pm360.cepm360.entity.FeedbackCell;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.entity.ZH_group_task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * T Data Progressing System
 * @author onekey
 *
 */
public class ZH_TaskPresenter<T extends TaskCell> extends TreePresenter<T> {

	public ZH_TaskPresenter(Activity activity) {
		super(activity);
	}
	
	public static <T extends Expandable> T initMsgInfo(List<T> tList, int taskId, int flag) {
		T t = initMsgInfo(tList, taskId);
		if (t != null) {
			if (flag == 1) {
				com.pm360.cepm360.services.group.RemoteTaskService.getInstance().setTask((ZH_group_task)t);
			} else if (flag == 2) {
				com.pm360.cepm360.services.plan.RemoteTaskService.getInstance().setTask((Task)t);
			}	
		}
		
		return t;
	}
	

	/**
	 * 计算所有的父并且在合适条件下修改开始完成日期
	 * @param tList
	 * @param currentTask
	 * @param line
	 */
	public static <T extends TaskCell> void updateAllParentTask(List<T> tList, T currentTask, int line) {
		T taskParent;
		if (currentTask.getLevel() == 0) {
			// 第一行,没有父了，直接返回
			return ;
		}
		
		for (int i = line - 1; i >= 0; i--) {
			if (tList.get(i).getLevel() < currentTask.getLevel()) {
				taskParent = tList.get(i);
				boolean updateFlag = false;
				if ((taskParent.getStart_time().getTime() - currentTask.getStart_time().getTime()) > 0) {
					// 如果父的开始日期比较晚
					taskParent.setStart_time(currentTask.getStart_time());
					updateFlag = true;
				}
				if ((taskParent.getEnd_time().getTime() - currentTask.getEnd_time().getTime()) < 0) {
					// 如果父的结束日期比较早
					taskParent.setEnd_time(currentTask.getEnd_time());
					updateFlag = true;
				}
				if (updateFlag) {
					taskParent.setPublish(Integer.parseInt(GLOBAL.PUBLISH_STATUS[2][0]));
				} else {
					break;
				}
				currentTask = taskParent;
			}
		}
	}

	/**
	 * two scenes invoke this function add T and modify T
	 * @param taskNode
	 * @param startTime
	 * @param endTime if add T endTime is set in Dialog, else it is the same as mTaskInfoBean
	 * @param line if add T, transfer -1
	 * @param mTaskInfoBean current select T
	 * @return true if startTime and endTime is valid
	 */
	public boolean calculatorParentDate(List<T> taskNode, Date startTime, Date endTime, int line, T mTaskInfoBean) {
		if (startTime == null) {
			Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_start_null),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (endTime == null) {
			Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_end_null),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (endTime.getTime() - startTime.getTime() < 0) {
			Toast.makeText(mActivity, R.string.plan_time_check_end_cannot_before_start,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		
		T currentTask = mTaskInfoBean;
		
		T taskParent = null;
		
		Date parentStartTime;
		Date parentEndTime;
		
		// 添加当前为父，修改先找父
		if (line == -1) {
			taskParent = currentTask;
			parentStartTime = startTime;
			parentEndTime = endTime;
		} else {
			for (int i = line - 1; i >= 0; i--) {
				if (taskNode.get(i).getLevel() < currentTask.getLevel()) {
					taskParent = taskNode.get(i);
					break;
				}
			}
			
			// 修改 没有父节点
			if (taskParent == null) {
				return true;
			}

			parentStartTime = taskParent.getStart_time();
			parentEndTime = taskParent.getEnd_time();
		}
		
		if (taskParent != null) {
			if (parentStartTime == null) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskParent.getName() + mActivity.getString(R.string.plan_time_check_right_not_start),
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (parentStartTime.getTime() - startTime.getTime() > 0) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_start_early) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, parentStartTime),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			if (parentEndTime == null) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskParent.getName() + mActivity.getString(R.string.plan_time_check_right_not_end),
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (parentEndTime.getTime() - endTime.getTime() < 0) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_end_late) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, parentEndTime),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * update function
	 * @param taskNode
	 * @param startTime
	 * @param endTime
	 * @param mTaskInfoBean
	 * @return true if startTime and endTime is valid
	 */
	public boolean calculatorChildrenDate(List<T> taskNode, T mTaskInfoBean, Date startTime, Date endTime) {
		if (!mTaskInfoBean.isHas_child()) {
			return true;
		}
		List<T> childrenList = calculateTaskOfChildren((ArrayList<T>) taskNode, mTaskInfoBean, false);
		for (int i = 0; i < childrenList.size(); i++) {
			if ((childrenList.get(i).getStart_time() != null) && (!childrenList.get(i).getStart_time().equals("")) && (childrenList.get(i).getStart_time().getTime() - startTime.getTime() < 0)) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_start_late) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, childrenList.get(i).getStart_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			if ((childrenList.get(i).getEnd_time() != null) && (!childrenList.get(i).getEnd_time().equals("")) && (childrenList.get(i).getEnd_time().getTime() - endTime.getTime() > 0)) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_end_early) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, childrenList.get(i).getEnd_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 反馈 计算父开始时间
	 * @param startTime
	 * @param position
	 * @return
	 */
	public boolean calculatorParentStartTime(Date startTime, int position, T mCurrentItem, DataTreeListAdapter<T> mListAdapter) {
		if (startTime == null) {
			Toast.makeText(mActivity,
					R.string.plan_time_check_start_null, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		T currentTask = mCurrentItem;
		if (mCurrentItem.getActual_end_time() != null) {
			LogUtil.i("wzw mTaskInfoBean:" + mCurrentItem.getActual_end_time() + "startTime:" + startTime);
			if (mCurrentItem.getActual_end_time().getTime() - startTime.getTime() < 0) {
				Toast.makeText(mActivity,
						R.string.plan_time_check_end_cannot_before_start,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		if (!calculatorChildrenActualDate(mListAdapter.getDataList(), mCurrentItem, startTime)) {
			return false;
		}
		
		List<T> taskNode = mListAdapter.getShowList();
		T taskParent = null;

		for (int i = position - 1; i >= 0; i--) {
			if (taskNode.get(i) == null) {
				return false;
			}
			if (taskNode.get(i).getLevel() < currentTask.getLevel()) {
				taskParent = taskNode.get(i);
				break;
			}
		}
		if (taskParent != null) {
			if (taskParent.getActual_start_time() == null) {
				Toast.makeText(
						mActivity,
						mActivity.getString(R.string.plan_time_check_left_symbol)
								+ taskParent.getName()
								+ mActivity.getString(R.string.plan_time_check_right_not_start),
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (taskParent.getActual_start_time().getTime()
					- startTime.getTime() > 0) {
				Toast.makeText(
						mActivity,
						mActivity.getString(R.string.plan_time_check_start_early)
								+ DateUtils.dateToString(
										DateUtils.FORMAT_SHORT,
										taskParent.getActual_start_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}

		}

		return true;
	}

	/**
	 * 反馈 计算子的实际结束时间
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public boolean calculatorChildEndTime(Date startTime, Date endTime, T mCurrentItem, DataTreeListAdapter<T> mListAdapter) {
		if (startTime == null) {
			Toast.makeText(mActivity,
					R.string.plan_time_check_start_null, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		if (endTime != null) {
			if (endTime.getTime() - startTime.getTime() < 0) {
				Toast.makeText(mActivity,
						R.string.plan_time_check_end_cannot_before_start,
						Toast.LENGTH_SHORT).show();
				return false;
			}

			T currentTask = mCurrentItem;
			List<T> tasklist = mListAdapter.getDataList();
			if (currentTask.isHas_child()) {
				for (T childTask : tasklist) {
					if (childTask.getParents_id() == currentTask
							.getTask_id()) {
						if (childTask.getActual_end_time() == null) {
							Toast.makeText(
									mActivity,
									mActivity.getString(R.string.plan_time_check_left_symbol)
											+ childTask.getName()
											+ mActivity.getString(R.string.plan_time_check_right_start_null),
									Toast.LENGTH_SHORT).show();
							return false;
						}
						if (endTime.getTime()
								- childTask.getActual_end_time().getTime() < 0) {
							Toast.makeText(
									mActivity,
									mActivity.getString(R.string.plan_time_check_end_late)
											+ DateUtils
													.dateToString(
															DateUtils.FORMAT_SHORT,
															childTask
																	.getActual_end_time()),
									Toast.LENGTH_SHORT).show();
							return false;
						}
					}
				}
			}
		}

		return true;
	}
	
	public <B extends FeedbackCell> void setRemoteFeedbackCache(B currentBean, T task, int type, 
			Project project) {
		currentBean.setChange_id(task.getChange_id());
		if (type == 1) {
			((ZH_group_feedback) currentBean).setZh_group_id(((ZH_group_task) task).getZh_group_id());
			((ZH_group_feedback) currentBean).setProject_id(project.getProject_id());
		} else if (type == 2) {
			((Feedback) currentBean).setProject_id(((Task) task).getProject_id());
		}
		
		currentBean.setTask_id(task.getTask_id());
		currentBean.setTask_name("【"
				+ project.getName() + "】 " + task.getName());
		currentBean.setActual_start_time(task
				.getActual_start_time());
		currentBean.setActual_end_time(task.getActual_end_time());
		currentBean.setCc_user(task.getCc_user());
		currentBean.setProgress(task.getProgress());
		currentBean.setStatus(task.getStatus());
		currentBean.setMark(task.getMark());
		currentBean.setFeedback_creater(task.getOwner());
	}
	
	public <B extends FeedbackCell> void setRemoteFeedbackCache(B currentBean, T task, int type, 
			Project project, List<T> planList, List<B> feedbackList) {
		setRemoteFeedbackCache(currentBean, task, type, project);

		for (int i = 0; i < feedbackList.size(); i++) {
			if (currentBean.getTask_id() == feedbackList.get(i)
					.getTask_id()) {
				currentBean.setFeedback_id(feedbackList.get(i)
						.getFeedback_id());
				break;
			}
		}

		int parentId = task.getParents_id();
		for (int i = 0; i < planList.size(); i++) {
			if (parentId == planList.get(i).getTask_id()) {
				currentBean.setParents_user(planList.get(i)
						.getOwner());
			}
		}
	}
	
	public boolean calculatorChildrenActualDate(List<T> taskNode, T mTaskInfoBean, Date startTime) {
		if (!mTaskInfoBean.isHas_child()) {
			return true;
		}
		List<T> childrenList = calculateTaskOfChildren((ArrayList<T>) taskNode, mTaskInfoBean, false);
		for (int i = 0; i < childrenList.size(); i++) {
			if ((childrenList.get(i).getActual_start_time() != null) && (!childrenList.get(i).getActual_start_time().equals("")) && (childrenList.get(i).getActual_start_time().getTime() - startTime.getTime() < 0)) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_start_late) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, childrenList.get(i).getActual_start_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
	
	public boolean taskTimeCheck(DataTreeListAdapter<T> mListAdapter) {
		List<T> taskList = mListAdapter.getDataList();
		for (int i = 0; i < taskList.size(); i++) {
			Log.i("dog2", "i:" + i + "taskList:" + taskList.get(i).getStart_time() + "size:" + taskList.size() + "name:" + taskList.get(i).getName());
			if (taskList.get(i).getStart_time() == null || taskList.get(i).getEnd_time() == null) {
				if (taskList.get(i).getStart_time() == null) {
					Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskList.get(i).getName() + mActivity.getString(R.string.plan_time_check_right_start_null), Toast.LENGTH_SHORT).show();
				} else if (taskList.get(i).getEnd_time() == null) {
					Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskList.get(i).getName() + mActivity.getString(R.string.plan_time_check_right_end_null), Toast.LENGTH_SHORT).show();
				}
				return false;
			}
			if (taskList.get(i).getStart_time().equals("") || taskList.get(i).getEnd_time().equals("")) {
				if (taskList.get(i).getStart_time().equals("")) {
					Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskList.get(i).getName() + mActivity.getString(R.string.plan_time_check_right_start_null), Toast.LENGTH_SHORT).show();
				} else if (taskList.get(i).getEnd_time().equals("")) {
					Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskList.get(i).getName() + mActivity.getString(R.string.plan_time_check_right_end_null), Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<T> initTaskList(ArrayList<T> list) {
		ArrayList<T> tasklist = list;

		for (int i = 0; i < tasklist.size(); i++) {
			int parent = tasklist.get(i).getParents_id();
			for (int j = 0; j < tasklist.size(); j++) {
				if (parent == tasklist.get(j).getTask_id()) {
					int level = tasklist.get(j).getLevel() + 1;
					tasklist.get(i).setLevel(level);
					tasklist.get(j).setHas_child(true);
				}
			}
		}
		
		return tasklist;
	}

	private T mInsertTaskInfoBean;
	public ArrayList<T> insertTaskNodeList(ArrayList<T> list, T T, T mTaskInfoBean, int mLine) {
		T = Change2Task(T, mTaskInfoBean);
		mInsertTaskInfoBean = T;
		ArrayList<T> tasklist = list;
//		if (DEBUG)
//			Log.i("dog1", "list " + list);
		boolean first = true;
		if (list.isEmpty() || (T.getParents_id() == 0)) {
			tasklist.add(T);
			return tasklist;
		}
		// traverse from select line, and end to the T level below current
		// T, ignore the first T because it may not has child
		for (int i = mLine; i < tasklist.size(); i++) {
//			if (DEBUG)
//				Log.i("dog1", "count:" + i + "size:" + tasklist.size());

			if (first) {
				first = false;
			} else {
				if (tasklist.get(i).getLevel() <= mTaskInfoBean.getLevel()) {
//					if (DEBUG)
//						Log.i("dog1", "i:" + i + " position-id:"
//								+ mTaskInfoBean.getLevel() + " pid:"
//								+ tasklist.get(i).getLevel());
					if (mTaskInfoBean.isExpanded()) {
						tasklist.add(i, T);
					}
					if (!mTaskInfoBean.isHas_child()) {
						mTaskInfoBean.setExpanded(false);
						mTaskInfoBean.setHas_child(true);
					}
					
					break;
				}
			}
//			if (DEBUG)
//				Log.i("dog1", "enter3");

			if (i == (tasklist.size() - 1)) {
				//if (DEBUG)
					Log.i("dog1", "last one i:" + i + "exp:" + mTaskInfoBean.isExpanded());

					if (!mTaskInfoBean.isHas_child()) {
						mTaskInfoBean.setExpanded(false);
						mTaskInfoBean.setHas_child(true);
					}
					if (mTaskInfoBean.isExpanded()) {
						tasklist.add(i + 1, T);
					}
					
							
				break;
			}

		}

		return tasklist;
	}

	public ArrayList<T> insertTaskList(ArrayList<T> list) {
		T T = mInsertTaskInfoBean;
		ArrayList<T> tasklist = list;
		if (list.isEmpty()) {
			tasklist.add(T);
			return tasklist;
		}

		tasklist.add(T);

		return tasklist;
	}

	public ArrayList<T> updateTaskNodeList(ArrayList<T> list, T mTaskInfoBeanCache, int mLine) {
		ArrayList<T> tasklist = list;
		tasklist.set(mLine, mTaskInfoBeanCache);
		return tasklist;
	}

	public ArrayList<T> updateTaskList(ArrayList<T> list, T mTaskInfoBeanCache) {
		ArrayList<T> tasklist = list;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTask_id() == mTaskInfoBeanCache.getTask_id()) {
				tasklist.set(i, mTaskInfoBeanCache);
			}
		}

		return tasklist;
	}
	
	public T Change2Task(T T, T mTaskInfoBean) {
		if (T.getParents_id() == 0) {
			T.setLevel(0);
			T.setHas_child(false);
		} else {
			T.setLevel(mTaskInfoBean.getLevel() + 1);
			T.setHas_child(false);
		}

		Log.i("dog1", "T:" + T);
		return T;
	}
}
