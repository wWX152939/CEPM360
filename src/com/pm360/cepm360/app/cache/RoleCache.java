/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.cache;

import com.pm360.cepm360.entity.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 标题: RoleCache 
 * 描述: 角色缓存
 * 作者： Administrator
 * 日期： 2016-1-4
 *
 */
public class RoleCache extends CepmCache {
    private static boolean mDataLoaded = false;

    private static ArrayList<Role> mRoleLists = new ArrayList<Role>();
    
    /* Key:name, Value:code */
    private static Map<String, String> mRoleNameMaps = new HashMap<String, String>();
    
    /* Key:code, Value:name */
    private static Map<String, String> mRoleCodeMaps = new HashMap<String, String>();
    
    /**
     * 获取所有列表
     * 
     * @return
     */
    public static ArrayList<Role> getRoleLists() {
        return mRoleLists;
    }

    /**
     * 设置所有Role到缓存
     * 
     * @param roleLists
     */
    public static void setRoleLists(ArrayList<Role> roleLists) {
        mRoleLists.clear();
        mRoleNameMaps.clear();
        mRoleCodeMaps.clear();
        mDataLoaded = true;

        for (int i = 0; i < roleLists.size(); i++) {
            Role role = roleLists.get(i);            
            String code = role.getCode();
            String name = role.getName();
            mRoleNameMaps.put(name, code);
            mRoleCodeMaps.put(code, name);
            mRoleLists.add(role);
        }
        dumpCache("RoleCache", mRoleLists);
    }
    
    /**
     * 添加一个role 到cache
     * @param role
     */
    public static void addRole(Role role) {
        mRoleLists.add(role);
        mRoleNameMaps.put(role.getName(), role.getCode());
        mRoleCodeMaps.put(role.getCode(), role.getName());
        dumpCache("RoleCache", mRoleLists);
    }
    
    /**
     * 删除RoleCache中的某一个role
     * @param role
     */
    public static void removeRole(Role role) {
        for (Role r : mRoleLists) {
            if (r.getRole_id() == role.getRole_id()) {
                mRoleLists.remove(r);
                mRoleNameMaps.remove(r.getName());
                mRoleCodeMaps.remove(role.getCode());
                break;
            }
        }
        dumpCache("RoleCache", mRoleLists);
    }
    
    /**
     * 更新某一个EPS对象
     * @param eps
     */
    public static void updateRole(Role role) {
        Role oldRole = findRoleById(role.getRole_id());
        removeRole(oldRole);
        addRole(role);
        dumpCache("RoleCache", mRoleLists);
    }

    /**
     * 通过项目Id查找Role
     * 
     * @param roldId
     * @return
     */
    public static Role findRoleById(int roleId) {
        Role role = null;
        if (mRoleLists != null) {
            for (Role r : mRoleLists) {
                if (r.getRole_id()== roleId) {
                    role = r;
                    break;
                }
            }
        }
        return role;
    }
    
    /**
     * 通过项目Id查找Role
     * 
     * @param role code
     * @return
     */
    public static Role findRoleByCode(String code) {
        Role role = null;
        if (mRoleLists != null) {
            for (Role r : mRoleLists) {
                if (code.equals(r.getCode())) {
                    role = r;
                    break;
                }
            }
        }
        return role;
    }
    
    /**
     * 此Map只为 通过name找id.
     * 
     * @return
     */
    public static Map<String, String> getRoleNameMaps() {
        return mRoleNameMaps;
    }
    
    public static Map<String, String> getRoleCodeMaps() {
        return mRoleCodeMaps;
    }
    
    /**
     * 数据是否已经加载好了
     * @return
     */
    public static boolean isDataLoaded() {
        return mDataLoaded;
    }
    
    /**
     * 清除缓存数据
     */
    public static void clear() {
        mRoleNameMaps.clear();
        mRoleCodeMaps.clear();
        mRoleLists.clear();        
    }
}
