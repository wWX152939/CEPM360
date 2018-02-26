/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.cache;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 标题: PermissionCache
 * 描述: 权限缓存 
 * 作者： Qiuwei Chen 
 * 日期： 2015-12-10
 * 
 */
public class PermissionCache extends CepmCache {
    private static String[] mSysPermissions;
//    private static String[] mProjectPermissions;

    /**
     * 设置该用户拥有的所有系统权限
     * 
     * @param the
     *            system permission list
     */
    public static void setSysPermissions(String[] permissions) {
    	mSysPermissions = permissions;
        if (mSysPermissions != null && mSysPermissions.length > 0) {
            dumpCache("PermissionCache-SYS",
                    new ArrayList<String>(Arrays.asList(mSysPermissions)));
        }
    }

    public static String[] getSysPermissions() {
        return mSysPermissions;
    }

    /**
     * 是否有该系统权限
     * 
     * @param permission
     *            需要判断的系统权限
     * @return
     */
    public static boolean hasSysPermission(String permission) {
        if (mSysPermissions == null)
            return false;
        for (String s : mSysPermissions) {
            if (s.equals(permission))
                return true;
        }
        return false;
    }
    
    /**
     * 是否拥有该模块的权限
     * @param module
     * @return
     */
    public static boolean hasSysModulePermission(int module) {
        if (mSysPermissions == null)
            return false;
        for (String s : mSysPermissions) {
            if (s.startsWith((module) + "_"))
                return true;
        }
        return false;
    }

    /**
     * 设置该用户拥有的所有项目权限
     * 
     * @param the
     *            project permission list
     */
//    public static void setProjectPermissions(String[] permissions) {
//        mProjectPermissions = permissions;
//        if (mProjectPermissions != null && mProjectPermissions.length > 0) {
//            dumpCache("PermissionCache-Project",
//                    new ArrayList<String>(Arrays.asList(mProjectPermissions)));
//        }
//    }

//    public static String[] getProjectPermissions() {
//        return mProjectPermissions;
//    }

    /**
     * 是否有该项目权限
     * 
     * @param permission
     *            需要判断的项目权限
     * @return
     */
//    public static boolean hasProjectPermission(String permission) {
//        if (mProjectPermissions == null)
//            return false;
//        for (String s : mProjectPermissions) {
//            if (s.equals(permission))
//                return true;
//        }
//        return false;
//    }
    
    /**
     * 是否拥有该模块的权限
     * @param module
     * @return
     */
//    public static boolean hasProjectModulePermission(int module) {
//        if (mProjectPermissions == null)
//            return false;
//        for (String s : mProjectPermissions) {
//            if (s.startsWith((module) + "_"))
//                return true;
//        }
//        return false;
//    }
    
    /**
     * 清除缓存数据
     */
    public static void clear() {
        mSysPermissions = null;
//        mProjectPermissions = null;
    }
    
}
