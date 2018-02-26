package com.pm360.cepm360.app.cache;

import android.annotation.SuppressLint;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 联系人缓存，用户使用该类时注意使用方法：
 * 1. 如要得到想要的数据，必须首先调用load方法，否则不能保证一定获取到
 * 	    数据，就是说可能为空
 * 2. 在调用1步中的load方法后，本缓存可以确保用户得到想要的数据
 * 
 * 	    之所以没有将load做成本地自动加载的方法，考虑到如下情况：
 * 1. 如果有n个用户要显示，那么最坏的情况下会请求服务n次，所以考虑性能问题
 * 	    这里必须先调用load方法
 * @author yuanlu
 *
 */
@SuppressLint("UseSparseArrays") 
public class ContactCache {
	
	// 联系人列表，可能包含任何一个协作单位的联系人
	private static List<User> mContactList;
	
	// 联系人ID到联系人的映射
	private static Map<String, User> mIdContactMap;
	
	// 联系人ID到联系人名称的映射
	private static Map<String, String> mIdNameMap;
	
	// 联系人ID列表
	private static List<Integer> mContactIdList;
	
	// 分配空间
	static {
		mContactList = new ArrayList<User>();
		mIdContactMap = new HashMap<String, User>();
		mIdNameMap = new HashMap<String, String>();
		mContactIdList = new ArrayList<Integer>();
	}
	
	/**
	 * 获取联系人ID和名称映射表
	 * @return
	 */
	public static Map<String, String> getContactIdNameMap() {
		return mIdNameMap;
	}
	
	/**
	 * 获取指定联系人信息
	 * @param contactId 联系人ID
	 * @return
	 */
	public static User getContact(int contactId) {
		return getContact(contactId + "");
	}
	
	/**
	 * 获取指定联系人信息
	 * @param contactId 联系人ID
	 * @return
	 */
	public static User getContact(String contactId) {
		return mIdContactMap.get(contactId);
	}
	
	/**
	 * 获取联系名字
	 * @param contactId 联系ID
	 * @return
	 */
	public static String getContactName(int contactId) {
		return getContactName(contactId + "");
	}
	
	/**
	 * 获取联系名字
	 * @param contactId 联系ID
	 * @return
	 */
	public static String getContactName(String contactId) {
		return mIdNameMap.get(contactId);
	}
	
	/**
	 * 加载新的联系人信息，并加入本地缓存
	 * @param ids	新的联系人IDS字符串
	 * @param callBack	本地回调接口，用于通知数据加载完毕
	 */
	public static void load(String ids, CallBack<Void, Integer> callBack) {
		
		// 排除已经缓存的数据
		loadContacts(getContactIds(ids), callBack);
	}
	
	/**
	 * 加载新的联系人信息，并加入本地缓存
	 * @param ids	新的联系人IDS字符串
	 * @param callBack	本地回调接口，用于通知数据加载完毕
	 */
	private static void loadContacts(String ids, final CallBack<Void, Integer> callBack) {
		if (ids.isEmpty()) {
			callBack.callBack(AnalysisManager.SUCCESS_DB_QUERY);
		} else {
			RemoteUserService.getInstance()
					.getUserDetailByUserIDs(new DataManagerInterface() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_QUERY:
							addContacts((List<User>) list);
							break;
						default:
							break;
					}
					
					/*
					 * 这里使用回调来提醒用户数据加载成功，注意这里传递过去的是状态码
					 */
					if (callBack != null) {
						callBack.callBack(status.getCode());
					}
				}
			}, ids);
		}
	}
	
	/**
	 * 添加新的联系人信息
	 * @param users 联系人列表
	 */
	private static void addContacts(List<User> users) {
		
		// 添加到联系人列表
		mContactList.addAll(users);
		
		// 不解释
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			
			// 添加到联系人ID到联系人的映射
			mIdContactMap.put(user.getUser_id() + "", user);
			
			// 添加到联系人ID到联系人名称的映射
			mIdNameMap.put(user.getUser_id() + "", user.getName());
			
			// 添加到联系人ID列表
			mContactIdList.add(user.getUser_id());
		}
	}
	
	/**
	 * 获取未被缓存的联系人IDS字符串
	 * @param ids
	 * @return
	 */
	private static String getContactIds(String ids) {
		StringBuilder sb = new StringBuilder();
		
		// 删除已缓存ID部分
		if (!ids.isEmpty()) {
			String[] parts = ids.split(",");
			for (int i = 0; i < parts.length; i++) {
				if (!mContactIdList.contains(Integer.parseInt(parts[i]))) {
					sb.append(parts[i] + ",");
				}
			}
		}
		
		return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
	}
}
