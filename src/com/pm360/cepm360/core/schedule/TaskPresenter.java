package com.pm360.cepm360.core.schedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.services.plan.RemoteTaskService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task Data Progressing System
 * @author onekey
 *
 */
public class TaskPresenter{
	public Activity mActivity;
	public TaskPresenter(Activity activity) {
		mActivity = activity;
	}
	
	/**
	 * two scenes invoke this function add Task and modify Task
	 * @param taskNode
	 * @param startTime
	 * @param endTime if add Task endTime is set in Dialog, else it is the same as mTaskInfoBean
	 * @param line if add Task, transfer -1
	 * @param mTaskInfoBean current select task
	 * @return true if startTime and endTime is valid
	 */
	public boolean calculatorParentDate(List<Task> taskNode, Date startTime, Date endTime, int line, Task mTaskInfoBean) {
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
		
		Task currentTask = mTaskInfoBean;
		
		Task taskParent = null;
		
		if (line == -1) {
			taskParent = currentTask;
		} else {
			for (int i = line - 1; i >= 0; i--) {
				if (taskNode.get(i).getLevel() < currentTask.getLevel()) {
					taskParent = taskNode.get(i);
					break;
				}
			}
		}
		
		if (taskParent != null) {
			if (taskParent.getStart_time() == null) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskParent.getName() + mActivity.getString(R.string.plan_time_check_right_not_start),
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (taskParent.getStart_time().getTime() - startTime.getTime() > 0) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_start_early) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, taskParent.getStart_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			if (taskParent.getEnd_time() == null) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_left_symbol) + taskParent.getName() + mActivity.getString(R.string.plan_time_check_right_not_end),
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (taskParent.getEnd_time().getTime() - endTime.getTime() < 0) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_end_late) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, taskParent.getEnd_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param taskNode
	 * @param startTime
	 * @param endTime
	 * @param mTaskInfoBean
	 * @return true if startTime and endTime is valid
	 */
	public boolean calculatorChildrenDate(List<Task> taskNode, Task mTaskInfoBean) {
		if (!mTaskInfoBean.isHas_child()) {
			return true;
		}
		List<Task> childrenList = calculateTaskOfChildren((ArrayList<Task>) taskNode, mTaskInfoBean, 0);
		for (int i = 0; i < childrenList.size(); i++) {
			if ((childrenList.get(i).getStart_time() != null) && (!childrenList.get(i).getStart_time().equals("")) && (childrenList.get(i).getStart_time().getTime() - mTaskInfoBean.getStart_time().getTime() < 0)) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_start_late) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, childrenList.get(i).getStart_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			if ((childrenList.get(i).getEnd_time() != null) && (!childrenList.get(i).getEnd_time().equals("")) && (childrenList.get(i).getEnd_time().getTime() - mTaskInfoBean.getEnd_time().getTime() > 0)) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_end_early) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, childrenList.get(i).getEnd_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
	
	public boolean calculatorChildrenActualDate(List<Task> taskNode, Task mTaskInfoBean, Date startTime) {
		if (!mTaskInfoBean.isHas_child()) {
			return true;
		}
		List<Task> childrenList = calculateTaskOfChildren((ArrayList<Task>) taskNode, mTaskInfoBean, 0);
		for (int i = 0; i < childrenList.size(); i++) {
			if ((childrenList.get(i).getActual_start_time() != null) && (!childrenList.get(i).getActual_start_time().equals("")) && (childrenList.get(i).getActual_start_time().getTime() - startTime.getTime() < 0)) {
				Toast.makeText(mActivity, mActivity.getString(R.string.plan_time_check_start_late) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, childrenList.get(i).getActual_start_time()),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
	
	public boolean taskTimeCheck(DataTreeListAdapter<Task> mListAdapter) {
		List<Task> taskList = mListAdapter.getDataList();
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
	
	public void updateListView(ArrayList<Task> levelList, DataTreeListAdapter<Task> mListAdapter) {
		if (levelList == null)
			return;
		int selectLine = 0;
		List<Task> taskNode = mListAdapter.getShowList();
		for (int i = levelList.size() - 1; i >= 0; i--) {
			for (int j = 0 ; j < taskNode.size(); j++) {
				if (levelList.get(i).getTask_id() == taskNode.get(j).getTask_id()) {
					mListAdapter.updateListView(j);
					selectLine = j;
					break;
				}
			}
		}
		LogUtil.d("wzw selectLine:" + selectLine);
		mListAdapter.setSelected(selectLine, true);
		
	}

	@SuppressLint("UseSparseArrays") 
	public ArrayList<Task> initLevelTask(ArrayList<Task> list, Task mTaskInfoBean) {
		Task task = mTaskInfoBean;
		if (task == null) {
			return null;
		}
		int level = task.getLevel();
		Map<Integer, ArrayList<Task>> taskMap = new HashMap<Integer, ArrayList<Task>>();
		for (int i = 0; i < level; i++) {
			ArrayList<Task> tasklist = new ArrayList<Task>();
			taskMap.put(i, tasklist);
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLevel() < level) {
				for (int j = 0; j < level; j++) {
					if (list.get(i).getLevel() == j) {
						taskMap.get(j).add(list.get(i));
						break;
					}
				}
			}

		}
		ArrayList<Task> levelList = new ArrayList<Task>();
		levelList.add(task);
		for (int i = level - 1 ; i >= 0; i--) {
			for (Task tasktmp : taskMap.get(i)) {
				if (tasktmp.getTask_id() == levelList.get(level - i - 1).getParents_id()) {
					levelList.add(tasktmp);
					break;
				}
			}
		}
		
//		if (DEBUG) {
//			Log.i("dog1", "taskMap:" + taskMap);
//			Log.i("dog1", "levelList:" + levelList);	
//		}
		LogUtil.d("wzw mTaskInfoBean" + mTaskInfoBean + "levelList:" + levelList);
		return levelList;
	}

	public ArrayList<Task> initTaskList(ArrayList<Task> list) {
		ArrayList<Task> tasklist = list;

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
	
	public Task initMsgInfo(ArrayList<Task> tasklist, Task mTaskInfoBean) {
		if (mTaskInfoBean != null) {
			for (int i = 0 ; i < tasklist.size(); i++) {
				if (tasklist.get(i).getTask_id() == mTaskInfoBean.getTask_id()) {
					mTaskInfoBean = tasklist.get(i);
					RemoteTaskService.getInstance().setTask(mTaskInfoBean);

				}
			}
		}
		LogUtil.d("wzw 11mTaskInfoBean:" + mTaskInfoBean);
		return mTaskInfoBean;
	}

	private Task mInsertTaskInfoBean;
	public ArrayList<Task> insertTaskNodeList(ArrayList<Task> list, Task task, Task mTaskInfoBean, int mLine) {
		task = Change2Task(task, mTaskInfoBean);
		mInsertTaskInfoBean = task;
		ArrayList<Task> tasklist = list;
//		if (DEBUG)
//			Log.i("dog1", "list " + list);
		boolean first = true;
		if (list.isEmpty() || (task.getParents_id() == 0)) {
			tasklist.add(task);
			return tasklist;
		}
		// traverse from select line, and end to the task level below current
		// task, ignore the first task because it may not has child
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
						tasklist.add(i, task);
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
						tasklist.add(i + 1, task);
					}
					
							
				break;
			}

		}

		return tasklist;
	}

	public ArrayList<Task> insertTaskList(ArrayList<Task> list) {
		Task task = mInsertTaskInfoBean;
		ArrayList<Task> tasklist = list;
		if (list.isEmpty()) {
			tasklist.add(task);
			return tasklist;
		}

		tasklist.add(task);

		return tasklist;
	}

	public ArrayList<Task> updateTaskNodeList(ArrayList<Task> list, Task mTaskInfoBeanCache, int mLine) {
		ArrayList<Task> tasklist = list;
		tasklist.set(mLine, mTaskInfoBeanCache);
		return tasklist;
	}

	public ArrayList<Task> updateTaskList(ArrayList<Task> list, Task mTaskInfoBeanCache) {
		ArrayList<Task> tasklist = list;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTask_id() == mTaskInfoBeanCache.getTask_id()) {
				tasklist.set(i, mTaskInfoBeanCache);
			}
		}

		return tasklist;
	}
	
	public ArrayList<Task> deleteParentTaskNodeIcon(ArrayList<Task> list, Task mTaskInfoBean, int mLine) {
		ArrayList<Task> tasklist = list;

		boolean equalLevel = false;
		for (int i = mLine + 1; i < list.size(); i++) {
//			if (DEBUG)
//				Log.i("dog1", "i:" + i + "mLine" + mLine);

			if (tasklist.get(i).getLevel() == mTaskInfoBean.getLevel()) {
				equalLevel = true;
				break;
			}
			if (tasklist.get(i).getLevel() < mTaskInfoBean.getLevel()) {
				break;
			}
		}
		if (mLine != 0) {
			for (int i = mLine - 1; i >= 0; i--) {
				if (tasklist.get(i).getLevel() == mTaskInfoBean.getLevel()) {
					equalLevel = true;
					break;
				}
				if (tasklist.get(i).getLevel() < mTaskInfoBean.getLevel()) {
					break;
				}
			}
		}

		// Log.i("dog1", "equalLevel:" + equalLevel);
		if (!equalLevel) {
			for (int i = mLine; i >= 0; i--) {
				if (tasklist.get(i).getTask_id() == mTaskInfoBean
						.getParents_id()) {
					// Log.i("dog1", "equalLevel, i" + i);
					tasklist.get(i).setExpanded(false);
					tasklist.get(i).setHas_child(false);
					break;
				}
			}
		}

		return tasklist;
	}

	public ArrayList<Task> deleteTaskNodeList(ArrayList<Task> list, Task mTaskInfoBean, int mLine) {
		deleteParentTaskNodeIcon(list, mTaskInfoBean, mLine);
		ArrayList<Task> tasklist = list;
		for (int i = 0; i < list.size(); i++) {
			if (tasklist.get(i).getTask_id() == mTaskInfoBean.getTask_id()) {
				tasklist.remove(i);
				break;
			}
		}
		if (!mTaskInfoBean.isHas_child()) {
			return tasklist;
		}
		ArrayList<Integer> deleteList = calculateTaskOfChildren(tasklist, mTaskInfoBean);

		int [] sortList = new int[deleteList.size()];
		for (int i = 0; i < deleteList.size(); i ++) {
			sortList[i] = deleteList.get(i);
		}
		Arrays.sort(sortList);

		for (int i = 0; i < sortList.length; i++) {

			tasklist.remove(sortList[i] - i);
		}
		return tasklist;
	}
	
	
	public ArrayList<Integer> calculateTaskOfChildren(ArrayList<Task> list, Task mTaskInfoBean) {
		ArrayList<Task> tasklist = list;
		
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		
		
		for (int i = 0; i < list.size(); i++) {
			if (tasklist.get(i).getLevel() > mTaskInfoBean.getLevel()
					&& tasklist.get(i).getTask_id() > mTaskInfoBean
							.getTask_id()) {
				sortList.add(i);
			}
		}
		mDeleteList.clear();
		searchChildTask(mTaskInfoBean, tasklist, sortList);

		return mDeleteList;
	}
	
	public ArrayList<Task> calculateTaskOfChildren(ArrayList<Task> list, Task mTaskInfoBean, int line) {
		ArrayList<Task> tasklist = list;
		
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		
		
		for (int i = 0; i < list.size(); i++) {
			if (tasklist.get(i).getLevel() > mTaskInfoBean.getLevel()
					&& tasklist.get(i).getTask_id() > mTaskInfoBean
							.getTask_id()) {
				sortList.add(i);
			}
		}
		mChildrenList.clear();
		searchChildTask(mTaskInfoBean, tasklist, sortList);

		return mChildrenList;
	}

	public ArrayList<Task> deleteTaskList(ArrayList<Task> list, Task mTaskInfoBean) {
		ArrayList<Task> tasklist = list;

		for (int i = 0; i < list.size(); i++) {
			if (tasklist.get(i).getTask_id() == mTaskInfoBean.getTask_id()) {
				tasklist.remove(i);
				break;
			}
		}
		
		if (!mTaskInfoBean.isHas_child()) {
			return tasklist;
		}
		ArrayList<Integer> deleteList = calculateTaskOfChildren(tasklist, mTaskInfoBean);
		
		int [] sortList = new int[deleteList.size()];
		for (int i = 0; i < deleteList.size(); i ++) {
			sortList[i] = deleteList.get(i);
		}
		Arrays.sort(sortList);

		for (int i = 0; i < sortList.length; i++) {

			tasklist.remove(sortList[i] - i);
		}

		return tasklist;
	}
	
	ArrayList<Integer> mDeleteList = new ArrayList<Integer>();
	ArrayList<Task> mChildrenList = new ArrayList<Task>();
	/**
	 * mDeleteList the serial number 
	 * @param currentTask
	 * @param tasklist
	 * @param sortList
	 */
	public void searchChildTask(Task currentTask, ArrayList<Task> tasklist, ArrayList<Integer> sortList) {
		if (currentTask.isHas_child()) {
			for (int i = 0; i < sortList.size(); i++) {
				if (currentTask.getTask_id() == tasklist.get(sortList.get(i)).getParents_id()) {
					mDeleteList.add(sortList.get(i));
					mChildrenList.add(tasklist.get(sortList.get(i)));
					searchChildTask(tasklist.get(sortList.get(i)), tasklist, sortList);
				}
			}
		}
	}
	

	public Task Change2Task(Task task, Task mTaskInfoBean) {
		if (task.getParents_id() == 0) {
			task.setLevel(0);
			task.setHas_child(false);
		} else {
			task.setLevel(mTaskInfoBean.getLevel() + 1);
			task.setHas_child(false);
		}

		Log.i("dog1", "task:" + task);
		return task;
	}
}
