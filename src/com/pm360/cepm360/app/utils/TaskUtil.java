package com.pm360.cepm360.app.utils;

import android.database.Cursor;

import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.TaskCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskUtil {
	
	/**
	 * @param displayItems including 13 items which show on the table
	 * @param task of Task type
	 * @return Map<String, Object> list
	 */
	
	public static String int2String(int i) {
		if (i == 0) {
			return "";
		} else {
			return Integer.toString(i);
		}
	}
	public static Map<String, String> taskToMap(Map<Integer, String> OBSMap, Map<Integer, String> userMap, String[] displayItems, TaskCell task) {
		Map<String, String> mapItem = new HashMap<String, String>();
		int count = 0;
		mapItem.put(displayItems[count++], task.getName());
		mapItem.put(displayItems[count++], task.getPlan_duration());
		if (task.getStart_time() != null) {
			mapItem.put(displayItems[count], DateUtils.dateToString(DateUtils.FORMAT_SHORT, task.getStart_time()));
		}
		count++;
		if (task.getEnd_time() != null) {
			mapItem.put(displayItems[count], DateUtils.dateToString(DateUtils.FORMAT_SHORT, task.getEnd_time()));
		}
		count++;

		mapItem.put(displayItems[count++], userMap.get(task.getOwner()));
		mapItem.put(displayItems[count++], OBSMap.get(task.getDepartment()));
		
		if (task.getType() != null) {
			if (task.getType().equals(GLOBAL.TASK_TYPE_MILE_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_MILE_VALUE);
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_TASK_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_TASK_VALUE);
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_WBS_VALUE);
			}  else if (task.getType().equals(GLOBAL.TASK_TYPE_VISA_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_VISA_VALUE);
			}
		}
				
		return mapItem;
	}
	
	public static Map<String, String> taskFeedbackToMap(Map<Integer, String> OBSMap, Map<Integer, String> userMap, String[] displayItems, Task task) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (task instanceof Task) {
			int count = 0;
			mapItem.put(displayItems[count++], task.getName());
			mapItem.put(displayItems[count++], task.getPlan_duration());
			if (task.getStart_time() != null) {
				mapItem.put(displayItems[count], DateUtils.dateToString(DateUtils.FORMAT_SHORT, task.getStart_time()));
			}
			count++;
			if (task.getEnd_time() != null) {
				mapItem.put(displayItems[count], DateUtils.dateToString(DateUtils.FORMAT_SHORT, task.getEnd_time()));
			}
			count++;
			if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_0) {
				mapItem.put(displayItems[count], GLOBAL.FEEDBACK_STATUS_0_VALUE);
			} else if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_1) {
				mapItem.put(displayItems[count], GLOBAL.FEEDBACK_STATUS_1_VALUE);
			} else if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_2) {
				mapItem.put(displayItems[count], GLOBAL.FEEDBACK_STATUS_2_VALUE);
			}
			count++;
			mapItem.put(displayItems[count++], task.getActual_duration());
			if (task.getActual_start_time() != null) {
				mapItem.put(displayItems[count], DateUtils.dateToString(DateUtils.FORMAT_SHORT, task.getActual_start_time()));
			}
			count++;
			if (task.getActual_end_time() != null) {
				mapItem.put(displayItems[count], DateUtils.dateToString(DateUtils.FORMAT_SHORT, task.getActual_end_time()));
				String actualDuration = ((task.getActual_end_time().getTime() 
    					- task.getActual_start_time().getTime()) /(24*3600*1000) + "天");
				mapItem.put(displayItems[count - 2], actualDuration);
			}
			count++;
			
			if (task.getProgress() != 0) {
				mapItem.put(displayItems[count++], task.getProgress() + "%");
			} else {
				mapItem.put(displayItems[count++], "");
			}

			mapItem.put(displayItems[count++], userMap.get(task.getOwner()));
			mapItem.put(displayItems[count++], OBSMap.get(task.getDepartment()));
			
			if (task.getType() != null) {
				if (task.getType().equals(GLOBAL.TASK_TYPE_MILE_KEY)) {
					mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_MILE_VALUE);
				} else if (task.getType().equals(GLOBAL.TASK_TYPE_TASK_KEY)) {
					mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_TASK_VALUE);
				} else if (task.getType().equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
					mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_WBS_VALUE);
				}
			}
				
		}
		return mapItem;
	}
	
	/**
	 * @param cursor query from database
	 * @return ArrayList<Task> list
	 */
	public static ArrayList<Task> cursorToTask(Cursor cursor) {
    	ArrayList<Task> taskList = new ArrayList<Task>();
    	String style = "yyyy-MM-dd";
        cursor.moveToPosition(-1);
        try {
        	while (cursor.moveToNext()) {
        		Task task = new Task();
        		task.setTask_id(cursor.getInt(cursor.getColumnIndex("task_id")));
        		//task.setWbs_id(cursor.getInt(cursor.getColumnIndex("wbs_id")));
        		task.setName(cursor.getString(cursor.getColumnIndex("task_name")));
        		task.setPlan_duration(cursor.getString(cursor.getColumnIndex("plan_duration")));
        		if (!cursor.getString(cursor.getColumnIndex("start_time")).equals(""))
        			task.setStart_time(DateUtils.stringToDate(style, cursor.getString(cursor.getColumnIndex("start_time"))));
        		if (!cursor.getString(cursor.getColumnIndex("end_time")).equals(""))
        			task.setEnd_time(DateUtils.stringToDate(style, cursor.getString(cursor.getColumnIndex("end_time"))));
        		//task.setStatus(cursor.getString(cursor.getColumnIndex("status")));
        		if (!cursor.getString(cursor.getColumnIndex("owner")).equals("")) {
        			task.setOwner(Integer.parseInt(cursor.getString(cursor.getColumnIndex("owner"))));
        		}
        		
        		task.setActual_duration(cursor.getString(cursor.getColumnIndex("actual_duration")));
        		if (!cursor.getString(cursor.getColumnIndex("actual_start_time")).equals(""))
        			task.setActual_start_time(DateUtils.stringToDate(style, cursor.getString(cursor.getColumnIndex("actual_start_time"))));
        		if (!cursor.getString(cursor.getColumnIndex("actual_end_time")).equals(""))
        			task.setActual_end_time(DateUtils.stringToDate(style, cursor.getString(cursor.getColumnIndex("actual_end_time"))));
        		task.setCreater(Integer.parseInt(cursor.getString(cursor.getColumnIndex("creater"))));
        		task.setType(cursor.getString(cursor.getColumnIndex("type")));
        		//task.setProgress(cursor.getString(cursor.getColumnIndex("progress")));
        		if (!cursor.getString(cursor.getColumnIndex("department")).equals("")) {
        			task.setDepartment(Integer.parseInt(cursor.getString(cursor.getColumnIndex("department"))));
        		}
        		
        		task.setParents_id(cursor.getInt(cursor.getColumnIndex("parents_id")));
        		task.setHas_child(Boolean.valueOf(cursor.getInt(cursor.getColumnIndex("has_child"))==1));
        		task.setLevel(cursor.getInt(cursor.getColumnIndex("level")));
        		task.setExpanded(false);
        		taskList.add(task);
        	}
        } finally {
        	 cursor.close();
        }
        return taskList;   	
    }

}
