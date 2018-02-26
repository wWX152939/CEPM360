/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.cache;

import com.pm360.cepm360.entity.EPS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * 标题: EpsCache 
 * 描述: EPS缓存
 * 作者： Qiuwei Chen
 * 日期： 2015-12-16
 *
 */
public class EpsCache extends CepmCache {
    private static boolean mDataLoaded = false;
    private static ArrayList<EPS> mEpsLists = new ArrayList<EPS>();
    private static Map<String, Integer> mEpsNameMaps = new HashMap<String, Integer>();
    private static Map<String, String> mEpsIdMaps = new HashMap<String, String>();

    /**
     * 获取所有EPS列表
     * 
     * @return
     */
    public static ArrayList<EPS> getEpsLists() {
        return mEpsLists;
    }

    /**
     * 设置所有EPS到缓存
     * 
     * @param epsLists
     */
    public static void setEpsLists(ArrayList<EPS> epsLists) {
        mEpsLists.clear();
        mEpsIdMaps.clear();
        mEpsNameMaps.clear();
        mDataLoaded = true;
        
        formatEPSList(epsLists);

        for (int i = 0; i < epsLists.size(); i++) {
            EPS eps = epsLists.get(i);
            int id = eps.getId();
            String name = eps.getName();
            mEpsIdMaps.put(String.valueOf(id), name);
            mEpsNameMaps.put(name, id);
            mEpsLists.add(eps);
        }
        dumpCache("EpsCache", mEpsLists);
    }
    
    /**
     * 添加一个eps 到cache
     * @param eps
     */
    public static void addEps(EPS eps) {
        mEpsLists.add(eps);
        mEpsIdMaps.put(String.valueOf(eps.getEps_id()),eps.getName());
        mEpsNameMaps.put(eps.getName(), eps.getId());
        dumpCache("EpsCache", mEpsLists);
    }
    
    /**
     * 删除EpsCache中的某一个eps
     * @param eps
     */
    public static void removeEps(EPS eps) {
        for (EPS e : mEpsLists) {
            if (e.getEps_id() == eps.getEps_id()) {
                mEpsLists.remove(e);
                mEpsIdMaps.remove(String.valueOf(eps.getEps_id()));
                mEpsNameMaps.remove(e.getName());
                break;
            }
        }
        dumpCache("EpsCache", mEpsLists);
    }
    
    /**
     * 更新某一个EPS对象
     * @param eps
     */
    public static void updateEps(EPS eps) {
        EPS oldEps = findEpsById(eps.getId());
        removeEps(oldEps);
        addEps(eps);
        dumpCache("EpsCache", mEpsLists);
    }

    /**
     * 通过项目Id查找EPS
     * 
     * @param epsId
     * @return
     */
    public static EPS findEpsById(int epsId) {
        EPS eps = null;
        if (mEpsLists != null) {
            for (EPS e : mEpsLists) {
                if (e.getId() == epsId) {
                    eps = e;
                    break;
                }
            }
        }
        return eps;
    }
    
    /**
     * 此Map只为 通过id找name
     * 
     * Key: eps id
     * Value: eps name
     */
    public static Map<String, String> getEpsIdMaps() {
        return mEpsIdMaps;
    }

    /**
     * 此Map只为 通过name找id
     * 
     * @return
     */
    public static Map<String, Integer> getEpsNameMaps() {
        return mEpsNameMaps;
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
        mEpsIdMaps.clear();
        mEpsNameMaps.clear();
        mEpsLists.clear();        
    }
    
    private static ArrayList<EPS> formatEPSList(ArrayList<EPS> list) {
        ArrayList<EPS> epslist = list;

        for (int i = 0; i < epslist.size(); i++) {
            int parent = epslist.get(i).getParents_id();
            for (int j = 0; j < i; j++) {
                if (parent == epslist.get(j).getEps_id()) {
                    int level = epslist.get(j).getLevel() + 1;
                    epslist.get(i).setLevel(level);
                    epslist.get(j).setHas_child(true);
                }
            }
        }
        return epslist;
    }
}

