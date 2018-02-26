package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 
 * 组合
 * 
 * @author Andy
 * 
 */
public class ZH_group implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -619804623652851243L;
    private int zh_group_id;
    private int project_id;
    private int tenant_id;
    private int node_id;
    private String node_name;
    private String node_module;
    private int quality_user;
    private int safety_user;

    private String task_name;

    public int getZh_group_id() {
	return zh_group_id;
    }

    public void setZh_group_id(int zh_group_id) {
	this.zh_group_id = zh_group_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int getNode_id() {
	return node_id;
    }

    public void setNode_id(int node_id) {
	this.node_id = node_id;
    }

    public String getNode_name() {
	return node_name;
    }

    public void setNode_name(String node_name) {
	this.node_name = node_name;
    }

    public String getNode_module() {
	return node_module;
    }

    public void setNode_module(String node_module) {
	this.node_module = node_module;
    }

    public String getTask_name() {
	return task_name;
    }

    public void setTask_name(String task_name) {
	this.task_name = task_name;
    }

    public int getQuality_user() {
	return quality_user;
    }

    public void setQuality_user(int quality_user) {
	this.quality_user = quality_user;
    }

    public int getSafety_user() {
	return safety_user;
    }

    public void setSafety_user(int safety_user) {
	this.safety_user = safety_user;
    }

    @Override
    public String toString() {
	return "ZH_group [zh_group_id=" + zh_group_id + ", project_id=" + project_id + ", tenant_id=" + tenant_id + ", node_id=" + node_id
	        + ", node_name=" + node_name + ", node_module=" + node_module + ", quality_user=" + quality_user + ", safety_user=" + safety_user
	        + ", task_name=" + task_name + "]";
    }

}
