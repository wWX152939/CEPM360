/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.cache;

import com.pm360.cepm360.entity.OBS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 标题: ObsCache 
 * 描述: OBS缓存
 * 作者： Administrator
 * 日期： 2016-1-4
 *
 */
public class ObsCache extends CepmCache {
    private static boolean mDataLoaded = false;
    private static ArrayList<OBS> mObsLists = new ArrayList<OBS>();
    private static Map<String, Integer> mObsNameMaps = new HashMap<String, Integer>();
    private static Map<String, String> mObsIdMaps = new HashMap<String, String>();

    /**
     * 获取所有OBS列表
     * 
     * @return
     */
    public static ArrayList<OBS> getObsLists() {
        return mObsLists;
    }

    /**
     * 设置所有OBS到缓存
     * 
     * @param obsLists
     */
    public static void setObsLists(ArrayList<OBS> obsLists) {
        mObsLists.clear();
        mObsNameMaps.clear();
        mObsIdMaps.clear();
        mDataLoaded = true;

        for (int i = 0; i < obsLists.size(); i++) {
            OBS obs = obsLists.get(i);
            int id = obs.getId();
            String name = obs.getName();
            mObsNameMaps.put(name, id);
            mObsIdMaps.put(Integer.toString(id), name);
            mObsIdMaps.put(name, Integer.toString(id));
            mObsLists.add(obs);
        }
        dumpCache("ObsCache", mObsLists);
    }
    
    /**
     * 添加一个obs 到cache
     * @param obs
     */
    public static void addObs(OBS obs) {
        mObsLists.add(obs);
        mObsNameMaps.put(obs.getName(), obs.getId());
        mObsIdMaps.put(Integer.toString(obs.getId()), obs.getName());
        mObsIdMaps.put(obs.getName(), Integer.toString(obs.getId()));
        dumpCache("ObsCache", mObsLists);
    }
    
    /**
     * 删除ObsCache中的某一个obs
     * @param obs
     */
    public static void removeObs(OBS obs) {
        for (OBS o : mObsLists) {
            if (o.getObs_id() == obs.getObs_id()) {
                mObsLists.remove(o);
                mObsNameMaps.remove(o.getName());
                mObsIdMaps.remove(Integer.toString(o.getId()));
                mObsIdMaps.remove(o.getName());
                break;
            }
        }
        dumpCache("ObsCache", mObsLists);
    }
    
    /**
     * 更新某一个OBS对象
     * @param obs
     */
    public static void updateObs(OBS obs) {
        OBS oldObs = findObsById(obs.getId());
        removeObs(oldObs);
        addObs(obs);
        dumpCache(ObsCache.class.toString(), mObsLists);
    }

    /**
     * 通过项目Id查找OBS
     * 
     * @param obsId
     * @return
     */
    public static OBS findObsById(int obsId) {
        OBS obs = null;
        if (mObsLists != null) {
            for (OBS e : mObsLists) {
                if (e.getId() == obsId) {
                    obs = e;
                    break;
                }
            }
        }
        return obs;
    }

    /**
     * 此Map只为 通过name找id. 如果想通过id找name，
     * 请使用findObsById方法，先找到Obs对象，便找到name.
     * 
     * @return
     */
    public static Map<String, Integer> getObsNameMaps() {
        return mObsNameMaps;
    }
    
    public static Map<String, String> getObsIdMaps() {
    	return mObsIdMaps;
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
        mObsNameMaps.clear();
        mObsIdMaps.clear();
        mObsLists.clear();        
    }
}
