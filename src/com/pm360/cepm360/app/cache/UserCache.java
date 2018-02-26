/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.cache;

import android.annotation.SuppressLint;

import com.pm360.cepm360.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 标题: UserCache 
 * 描述: 用户缓存
 * 作者： Qiuwei Chen
 * 日期： 2015-12-10
 *
 */
@SuppressLint("UseSparseArrays") 
public class UserCache extends CepmCache {

    private static User mCurrentUser;
    private static boolean mDataLoaded = false;
    private static ArrayList<User> mUserLists = new ArrayList<User>();
    private static ArrayList<User> mCooperationUserLists = new ArrayList<User>();
    
    private static Map<String, Integer> mUserListsMap = new HashMap<String, Integer>();
    
    private static Map<String, User> mIdUserMap = new HashMap<String, User>();
    private static Map<String, String> mUserMaps = new HashMap<String, String>();

    /**
     * 获取当前打开的项目
     * 
     * @return current user
     */
    public static User getCurrentUser() {
        return mCurrentUser;
    }
    
    /**
     * 获取当前用户的ID
     * @return
     */
    public static int getCurrentUserId() {
    	return mCurrentUser.getUser_id();
    }
    
    /**
     * 获取公司ID
     * @return
     */
    public static int getTenantId() {
    	return mCurrentUser.getTenant_id();
    }
    
    /**
     * 通过ID获取名字
     * @param id
     * @return
     */
    public static String getNameById(int id) {
    	User user = findUserById(id);
    	return user == null ? "" : user.getName();
    }

    /**
     * 设置打开了的项目到缓存
     * 
     * @param user
     */
    public static void setCurrentUser(User user) {
        mCurrentUser = user;
    }
    
    /**
     * 添加一个user对象到UserCache
     * @param user
     */
    public static void addUser(User user) {
        mUserLists.add(user);
        mUserListsMap.put(user.getName(), user.getUser_id());
        mUserMaps.put(String.valueOf(user.getUser_id()), user.getName());
        
        dumpCache("UserCache", mUserLists);
    }
    
    /**
     * 删除UserCache中的某一个user
     * @param user
     */
    public static void removeUser(User user) {
        for (User u : mUserLists) {
            if (u.getUser_id() == user.getUser_id()) {
                mUserLists.remove(u);
                mUserListsMap.remove(u.getName());
                mUserMaps.remove(String.valueOf(u.getUser_id()));
                break;
            }
        }
        dumpCache("UserCache", mUserLists);
    }
    
    /**
     * 用户信息更新
     * @param user
     */
    public static void updateUser(User user) {
        User oldUser = findUserById(user.getUser_id());
        removeUser(oldUser);
        addUser(user);
        if (isCurrentUser(user)) {
            setCurrentUser(user);
        }
        
        dumpCache("UserCache", mUserLists);
    }

    /**
     * 获取所有项目列表
     * 
     * @return
     */
    public static ArrayList<User> getUserLists() {
        return mUserLists;
    }
    
    /**
     * 设置所有项目到缓存
     * 
     * @param userLists
     */
    public static void setUserLists(ArrayList<User> userLists) {
        mUserLists.clear();
        mUserListsMap.clear();
        mUserMaps.clear();
        mIdUserMap.clear();
        mDataLoaded = true;
        
        for (int i = 0; i < userLists.size(); i++) {
             User user = userLists.get(i);
             int id = user.getUser_id();
             String name = user.getName();
             mUserListsMap.put(name, id);
             mUserMaps.put(String.valueOf(id), name);
             mUserLists.add(user);
        }
        
        updateIdUserMap();
        updateUserIdNameMap();
        
        dumpCache("UserCache", mUserLists);
    }

    /**
     * 通过项目Id查找项目
     * 
     * @param projectId
     * @return
     */
    public static User findUserById(int userId) {
        User user = null;
        if (mUserLists != null) {
            for (User u : mUserLists) {
                if (u.getUser_id() == userId) {
                    user = u;
                    break;
                }
            }
        }
        return user;
    }

    /**
     * 此Map只为 通过name找id. 如果想通过id找name， 请使用findUserById方法，先找到User对象，便找到name.
     * 
     * @return
     */
    public static Map<String, Integer> getUserListsMap() {
        return mUserListsMap;
    }
    
    public static Map<String, String> getUserMaps() {
        return mUserMaps;
    }
    
    /**
     * 清除缓存数据
     */
    public static void clear() {
        mCurrentUser = null;
        mUserListsMap.clear();
        mUserMaps.clear();
        mUserLists.clear();  
    }

    public static boolean isCurrentUser(User user) {
        if (mCurrentUser != null && mCurrentUser.getUser_id() == user.getUser_id()) {
            return true;
        }
        return false;
    }
    
    /**
     * 数据是否已经加载好了
     * @return
     */
    public static boolean isDataLoaded() {
        return mDataLoaded;
    }
    
    public static List<User> getCooperationUsers() {
        return mCooperationUserLists;
    }
    
    public static List<User> getAllRelativeUsers() {
    	List<User> allRelativeUsers = new ArrayList<User>();
    	allRelativeUsers.addAll(mUserLists);
    	allRelativeUsers.addAll(mCooperationUserLists);
    	
        return allRelativeUsers;
    }
    
    public static User getUserById(String userId) {
    	return mIdUserMap.get(userId);
    }
    
    public static void setCooperationUsers(List<User> cooperationUsers) {
    	mCooperationUserLists.clear();
    	mCooperationUserLists.addAll(cooperationUsers);
    	
    	updateIdUserMap();
    	updateUserIdNameMap();
    }
    
    private static void updateIdUserMap() {
    	mIdUserMap.clear();
    	
    	for (User user : mUserLists) {
    		mIdUserMap.put(String.valueOf(user.getUser_id()), user);
    	}
    	
    	for (User user : mCooperationUserLists) {
    		mIdUserMap.put(String.valueOf(user.getUser_id()), user);
    	}
    }
    
    private static void updateUserIdNameMap() {
    	mUserMaps.clear();
    	
    	for (User user : mUserLists) {
    		mUserMaps.put(String.valueOf(user.getUser_id()), user.getName());
    	}
    	
    	for (User user : mCooperationUserLists) {
    		mUserMaps.put(String.valueOf(user.getUser_id()), user.getName());
    	}
    }
}
