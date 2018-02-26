package com.pm360.cepm360.core;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.custinterface.TreeExpandInterface;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.ExpandableSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataListTree data progress
 * @author onekey
 *
 */
public class TreePresenter<T extends Expandable> {
	public static final int INTERVAL = 1000;
	
	public Activity mActivity;
	public TreePresenter(Activity activity) {
		mActivity = activity;
	}
	
	public String calculateDuration(Date startTime, Date endTime) {
		String duration = 1 + (endTime.getTime() - startTime.getTime())
				/ (24 * 3600 * 1000)
				+ mActivity.getString(R.string.plan_day);
		return duration;
	}
	
	public static String calculateDuration(Activity activity, Date startTime, Date endTime) {
		String duration = 1 + (endTime.getTime() - startTime.getTime())
				/ (24 * 3600 * 1000)
				+ activity.getString(R.string.plan_day);
		return duration;
	}
	
	public static <T extends Expandable> T initMsgInfo(List<T> tList, int taskId) {
		T t = null;
		if (taskId != 0) {
			for (int i = 0 ; i < tList.size(); i++) {
				if (tList.get(i).getId() == taskId) {
					t = tList.get(i);
					break;
				}
			}
		}
		return t;
	}
	
	/**
	 * 传入所有父组成的list，逐级展开
	 * @param levelList
	 * @param listAdapter
	 */
	public static <T extends Expandable> void updateListView(List<T> levelList, TreeExpandInterface listAdapter) {
		if (levelList == null)
			return;
		int selectLine = 0;
		List<T> taskNode = listAdapter.getShowList();
		for (int i = levelList.size() - 1; i >= 0; i--) {
			for (int j = 0 ; j < taskNode.size(); j++) {
				if (levelList.get(i).getId() == taskNode.get(j).getId()) {
					if (!taskNode.get(j).isExpanded()) {
						listAdapter.updateListView(j);
					}
					selectLine = j;
					break;
				}
			}
		}
		LogUtil.d("wzw selectLine:" + selectLine);
		listAdapter.setSelected(selectLine, true);
		
	}
	
	/**
	 * 初始化levelList,通过传入的对象，获取，它所有的父，组成一个list
	 * @param list
	 * @param curBean
	 * @return
	 */
	@SuppressLint("UseSparseArrays") 
	public static <T extends Expandable> List<T> initLevelTask(List<T> list, T curBean) {
		T t = curBean;
		if (t == null) {
			return null;
		}
		int level = t.getLevel();
		Map<Integer, ArrayList<T>> taskMap = new HashMap<Integer, ArrayList<T>>();
		for (int i = 0; i < level; i++) {
			ArrayList<T> tList = new ArrayList<T>();
			taskMap.put(i, tList);
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
		List<T> levelList = new ArrayList<T>();
		levelList.add(t);
		for (int i = level - 1 ; i >= 0; i--) {
			for (T tasktmp : taskMap.get(i)) {
				if (tasktmp.getId() == levelList.get(level - i - 1).getParents_id()) {
					levelList.add(tasktmp);
					break;
				}
			}
		}
		
		LogUtil.d("wzw curBean" + curBean + "levelList:" + levelList);
		return levelList;
	}

	
	/**
	 * 插入时获取sorNum
	 * @param list
	 * @param currentBean
	 * @param line
	 * @return
	 */
	public static <T extends ExpandableSort> int getInsertSortNum(DataTreeListAdapter<T> listAdapter, int line) {
		int sortNum = 0;
		ArrayList<T> list = (ArrayList<T>) listAdapter.getShowList();
		T currentBean = list.get(line);
		for (int i = line - 1; i >= 0; i--) {
			if (list.get(i).getLevel() <= currentBean.getLevel()) {
				if (list.get(i).getLevel() == currentBean.getLevel()) {
					sortNum = (list.get(i).getSort() + currentBean.getSort()) / 2;
				} else {
					sortNum = currentBean.getSort() / 2;
				}
				break;
			}
		}
		LogUtil.i("wzw sortNum:" + sortNum);
		return sortNum;
	}
	
	/**
	 * 增加时获取sortNum
	 * @param listAdapter
	 * @param currentBean
	 * @param line
	 * @return
	 */
	public static <T extends ExpandableSort> int getAddSortNum(DataTreeListAdapter<T> listAdapter, int line) {
		int sortNum = 0;
		do {
			T currentBean = listAdapter.getShowList().get(line);
			if (!currentBean.isHas_child()) {
				sortNum = INTERVAL;
				break;
	        }

	        if (currentBean.isExpanded()) {
	        	int i = line + 1;
	        	for (; i < listAdapter.getShowList().size(); i++) {
	                if (currentBean.getLevel() >= listAdapter.getShowList().get(i).getLevel()) {
	                    break;
	                }
	            }
	        	sortNum = listAdapter.getShowList().get(i - 1).getSort() + INTERVAL;
	        } else {
	            ArrayList<T> temp = new ArrayList<T>();
	            for (T t : listAdapter.getDataList()) {
	                if (t.getParents_id() == currentBean.getId()) {
	                    temp.add(t);
	                }
	            }
	            
	            sortNum = temp.get(0).getSort();
	            for (int i = 1; i < temp.size(); i++) {
	            	if (sortNum < temp.get(i).getSort()) {
	            		sortNum = temp.get(i).getSort();
	            	}
	            }
	            sortNum += INTERVAL;
	        }
		} while (false);
		
		LogUtil.i("wzw sortNum:" + sortNum);
		return sortNum;
	}
	
	public static <T extends ExpandableSort> void insertToTreeList(DataTreeListAdapter<T> listAdapter, T insertNode, int line) {
		ArrayList<T> list = (ArrayList<T>) listAdapter.getShowList();
		// 1. 数据初始化
		insertNode.setLevel(list.get(line).getLevel());
		insertNode.setHas_child(false);
		insertNode.setExpanded(false);
		
		// 2.插入数据
		insertList((ArrayList<T>)listAdapter.getDataList(), insertNode);
		list.add(line, insertNode);
		listAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 增加一行
	 * @param listAdapter
	 * @param addNode
	 * @param line
	 */
	public static <T extends Expandable> void addToTreeList(DataTreeListAdapter<T> listAdapter, T addNode, int line) {
		insertList((ArrayList<T>)listAdapter.getDataList(), addNode);
		insertNodeList(listAdapter, (ArrayList<T>)listAdapter.getShowList(), addNode, line);
		listAdapter.notifyDataSetChanged();
	}
	
	private static <T extends Expandable> void insertList(ArrayList<T> list, T addNode) {
		list.add(addNode);
	}
	
	private static <T extends Expandable>  void insertNodeList(DataTreeListAdapter<T> listAdapter, ArrayList<T> list, T addNode, int line) 
	{
		if (addNode.getParents_id() == 0) {
            addNode.setLevel(0);
            list.add(addNode);
        } else {
            int i, level = list.get(line).getLevel();
            addNode.setLevel(level + 1);
            // 模拟点击当前节点
            // 如果当前节点有孩子但没有展开，添加目录时需要先展开在添加
            if (list.get(line).isHas_child() 
                    && !list.get(line).isExpanded()) {
                // 模拟点击当前节点
            	listAdapter.updateListView(line);
            } else {
                list.get(line).setHas_child(true);
                list.get(line).setExpanded(true);
                for (i = line + 1; i < list.size(); i++) {
                    if (list.get(i).getLevel() <= level) {
                        break;
                    }
                }
                list.add(i, addNode);
            }
        }
	}
	
	/**
	 * 计算选中对象以及所有选中对象的子，排除重复部分
	 * @param listAdapter
	 * @param selectedList
	 * @return
	 */
	public ArrayList<T> calculateMutilTaskOfChildren(DataTreeListAdapter<T> listAdapter, List<Integer> selectedList) {
		ArrayList<T> retList = new ArrayList<T>();
		List<T> showList = listAdapter.getShowList();
		List<T> dataList = listAdapter.getDataList();
		
		int minNum = selectedList.isEmpty() ? 0 : selectedList.get(0);
		for (int num : selectedList) {
			if (minNum > num) {
				minNum = num;
			}
		}
		
		for (int i = minNum; i < showList.size(); i++) {
			if (selectedList.contains(i)) {
				// 先把当前对象和子加入
				retList.addAll(calculateTaskOfChildren(dataList, showList.get(i), true));
				
				if (showList.get(i).isHas_child() && showList.get(i).isExpanded()) {
					// 如果有子并且展开，需要跳过子 循环直到最后一行高亮
					for (int j = i + 1; j < showList.size(); j++) {
						if (showList.get(j).getLevel() <= showList.get(i).getLevel()) {
							// 跳转到子末尾 i++后跳转到下一个兄弟
							i = i + j - i - 1;
							break;
						}
						// 到末尾了 跳转到末尾 i++后跳转到末尾后一个，直接结束
						if (j == (showList.size() - 1)) {
							i = i + j - i;
						}
					}
				}
				
			}
		}
		
		return retList;
	}
	
	/**
	 * 计算所有的子，包含当前对象
	 * @param list
	 * @param currentBean
	 * @return
	 */
	public ArrayList<Integer> calculateTaskOfChildren(ArrayList<T> list, T currentBean) {
		ArrayList<T> tList = list;
		
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		
		
		for (int i = 0; i < list.size(); i++) {
			if (tList.get(i).getLevel() > currentBean.getLevel()
					&& tList.get(i).getId() > currentBean
							.getId()) {
				sortList.add(i);
			}
		}
		mDeleteList.clear();
		searchChildTask(currentBean, tList, sortList);

		return mDeleteList;
	}
	
	/**
	 * 
	 * @param list allList
	 * @param currentBean
	 * @param addCurrentBean true 添加当前对象
	 * @return
	 */
	public ArrayList<T> calculateTaskOfChildren(List<T> list, T currentBean, boolean addCurrentBean) {

		mChildrenList.clear();
		if (addCurrentBean) {
			mChildrenList.add(currentBean);
			
		}

		if (!currentBean.isHas_child()) {
			return mChildrenList;
		}
		
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLevel() > currentBean.getLevel()
					&& list.get(i).getId() > currentBean
							.getId()) {
				sortList.add(i);
			}
		}
		searchChildTask(currentBean, list, sortList);

		return mChildrenList;
	}

	public ArrayList<T> deleteTaskList(ArrayList<T> list, T currentBean) {
		ArrayList<T> tList = list;

		for (int i = 0; i < list.size(); i++) {
			if (tList.get(i).getId() == currentBean.getId()) {
				tList.remove(i);
				break;
			}
		}
		
		if (!currentBean.isHas_child()) {
			return tList;
		}
		ArrayList<Integer> deleteList = calculateTaskOfChildren(tList, currentBean);
		
		int [] sortList = new int[deleteList.size()];
		for (int i = 0; i < deleteList.size(); i ++) {
			sortList[i] = deleteList.get(i);
		}
		Arrays.sort(sortList);

		for (int i = 0; i < sortList.length; i++) {

			tList.remove(sortList[i] - i);
		}

		return tList;
	}
	
	ArrayList<Integer> mDeleteList = new ArrayList<Integer>();
	ArrayList<T> mChildrenList = new ArrayList<T>();
	
	/**
	 * mDeleteList the serial number 
	 * @param currentTask
	 * @param tList
	 * @param sortList
	 */
	public void searchChildTask(T currentTask, List<T> tList, ArrayList<Integer> sortList) {
		if (currentTask.isHas_child()) {
			for (int i = 0; i < sortList.size(); i++) {
				if (currentTask.getId() == tList.get(sortList.get(i)).getParents_id()) {
					mDeleteList.add(sortList.get(i));
					mChildrenList.add(tList.get(sortList.get(i)));
					searchChildTask(tList.get(sortList.get(i)), tList, sortList);
				}
			}
		}
	}
	
	public ArrayList<T> deleteTaskNodeList(ArrayList<T> list, T currentBean, int currentLine) {
		deleteParentTaskNodeIcon(list, currentBean, currentLine);
		ArrayList<T> tList = list;
		for (int i = 0; i < list.size(); i++) {
			if (tList.get(i).getId() == currentBean.getId()) {
				tList.remove(i);
				break;
			}
		}
		if (!currentBean.isHas_child()) {
			return tList;
		}
		ArrayList<Integer> deleteList = calculateTaskOfChildren(tList, currentBean);

		int [] sortList = new int[deleteList.size()];
		for (int i = 0; i < deleteList.size(); i ++) {
			sortList[i] = deleteList.get(i);
		}
		Arrays.sort(sortList);

		for (int i = 0; i < sortList.length; i++) {

			tList.remove(sortList[i] - i);
		}
		return tList;
	}
	
	public ArrayList<T> deleteParentTaskNodeIcon(ArrayList<T> list, T currentBean, int currentLine) {
		ArrayList<T> tList = list;

		boolean equalLevel = false;
		for (int i = currentLine + 1; i < list.size(); i++) {
//			if (DEBUG)
//				Log.i("dog1", "i:" + i + "currentLine" + currentLine);

			if (tList.get(i).getLevel() == currentBean.getLevel()) {
				equalLevel = true;
				break;
			}
			if (tList.get(i).getLevel() < currentBean.getLevel()) {
				break;
			}
		}
		if (currentLine != 0) {
			for (int i = currentLine - 1; i >= 0; i--) {
				if (tList.get(i).getLevel() == currentBean.getLevel()) {
					equalLevel = true;
					break;
				}
				if (tList.get(i).getLevel() < currentBean.getLevel()) {
					break;
				}
			}
		}

		if (!equalLevel) {
			for (int i = currentLine; i >= 0; i--) {
				if (tList.get(i).getId() == currentBean
						.getParents_id()) {
					tList.get(i).setExpanded(false);
					tList.get(i).setHas_child(false);
					break;
				}
			}
		}

		return tList;
	}

	
}
