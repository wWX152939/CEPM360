/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.cache;

import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 标题: ProjectCache 
 * 描述: 项目缓存
 * 作者： Qiuwei Chen
 * 日期： 2015-12-10
 *
 */
public class ProjectCache extends CepmCache {
    private static Project mCurrentProject;
    private static boolean mDataLoaded = false;
    private static ArrayList<Project> mProjectLists = new ArrayList<Project>();
    private static Map<String, String> mProjectIdMaps = new HashMap<String, String>();
    private static Map<String, Integer> mProjectNameMaps = new HashMap<String, Integer>();

    /**
     * 获取当前打开的项目
     * 
     * @return current Project
     */
    public static Project getCurrentProject() {
        return mCurrentProject;
    }
    
    /**
     * 添加一个project对象到ProjectCache中
     * @param project
     */
    public static void addProject(Project project) {
        mDataLoaded = true;
        mProjectLists.add(project);
        mProjectNameMaps.put(project.getName(), project.getProject_id());
        mProjectIdMaps.put(String.valueOf(project.getProject_id()), project.getName());
        dumpCache("ProjectCache-addProject", mProjectLists);
    }
    
    /**
     * 从ProjectCache中删除某一个project对象
     * @param project
     */
    public static void removeProject(Project project) {
        for (Project p : mProjectLists) {
            if (p.getProject_id() == project.getProject_id()) {
                mProjectLists.remove(p);
                mProjectNameMaps.remove(p.getName());
                mProjectIdMaps.remove(String.valueOf(project.getProject_id()));
                if (isCurrentProject(p)) {
                    setCurrentProject(null);
                }
                break;
            }
        }
        dumpCache("ProjectCache-removeProject", mProjectLists);
    }
    
    /**
     * 更新某一个project到ProjectCache
     * @param project
     */
    public static void updateProject(Project project) {
        Project oldProject = findProjectById(project.getProject_id());
        removeProject(oldProject);
        addProject(project);
        if (isCurrentProject(project)) {
            setCurrentProject(project);
        }
        dumpCache("ProjectCache-updateProject", mProjectLists);
    }

    /**
     * 设置打开了的项目到缓存
     * 
     * @param project
     */
    public static void setCurrentProject(Project project) {
        mCurrentProject = project;
    }

    /**
     * 获取所有项目列表
     * 
     * @return
     */
    public static ArrayList<Project> getProjectLists() {
        return mProjectLists;
    }

    /**
     * 设置所有项目到缓存
     * 
     * @param projectLists
     */
    public static void setProjectLists(ArrayList<Project> projectLists) {
        mProjectLists.clear();
        mProjectNameMaps.clear();
        mProjectIdMaps.clear();
        mDataLoaded = true;

        for (int i = 0; i < projectLists.size(); i++) {
            Project project = projectLists.get(i);
            int id = project.getProject_id();
            String name = project.getName();
            mProjectNameMaps.put(name, id);
            mProjectIdMaps.put(String.valueOf(id), name);
            mProjectLists.add(project);
        }
        dumpCache("ProjectCache-setProjectLists", mProjectLists);
    }

    /**
     * 通过项目Id查找项目
     * 
     * @param projectId
     * @return
     */
    public static Project findProjectById(int projectId) {
        Project project = null;
        if (mProjectLists != null) {
            for (Project p : mProjectLists) {
                if (p.getProject_id() == projectId) {
                    project = p;
                    break;
                }
            }
        }
        return project;
    }

    /**
     * 此Map只为 通过name找id. 如果想通过id找name，
     * 
     * @return
     */
    public static Map<String, Integer> getProjectNameMaps() {
        return mProjectNameMaps;
    }
    
    public static Map<String, String> getProjectIdMaps() {
        return mProjectIdMaps;
    }
    
    /**
     * 清除缓存数据
     */
    public static void clear() {
        mCurrentProject = null;
        mProjectNameMaps.clear();
        mProjectIdMaps.clear();
        mProjectLists.clear();
    }
    
    /*
     * 是否为当前打开的项目
     */
    private static boolean isCurrentProject(Project project) {
        if (mCurrentProject != null
                && mCurrentProject.getProject_id() == project.getProject_id()) {
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
}
