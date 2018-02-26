package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页-任务
 * 
 * @author Andy
 * 
 */
public class Index_task implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3325420830539950350L;
    private int message_type_key;// 用来区分组合任务和非组合任务（16：组合任务，8：非组合任务）
    private int task_id;
    private String name;
    private int project_id;
    private int zh_group_id;
    private int progress;
    private Date start_time;
    private Date end_time;
    private Date actual_start_time;
    private Date actual_end_time;
    private int number;// 数量
    private String tenant_type;
    private int tenant_id;
    private int user_id;
    private int parents_id;
    private Date system_time;

    public int getMessage_type_key() {
	return message_type_key;
    }

    public void setMessage_type_key(int message_type_key) {
	this.message_type_key = message_type_key;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getZh_group_id() {
	return zh_group_id;
    }

    public void setZh_group_id(int zh_group_id) {
	this.zh_group_id = zh_group_id;
    }

    public int getProgress() {
	return progress;
    }

    public void setProgress(int progress) {
	this.progress = progress;
    }

    public Date getStart_time() {
	return start_time;
    }

    public void setStart_time(Date start_time) {
	this.start_time = start_time;
    }

    public Date getEnd_time() {
	return end_time;
    }

    public void setEnd_time(Date end_time) {
	this.end_time = end_time;
    }

    public Date getActual_start_time() {
	return actual_start_time;
    }

    public void setActual_start_time(Date actual_start_time) {
	this.actual_start_time = actual_start_time;
    }

    public Date getActual_end_time() {
	return actual_end_time;
    }

    public void setActual_end_time(Date actual_end_time) {
	this.actual_end_time = actual_end_time;
    }

    public int getNumber() {
	return number;
    }

    public void setNumber(int number) {
	this.number = number;
    }

    public String getTenant_type() {
	return tenant_type;
    }

    public void setTenant_type(String tenant_type) {
	this.tenant_type = tenant_type;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
    }

    public int getParents_id() {
	return parents_id;
    }

    public void setParents_id(int parents_id) {
	this.parents_id = parents_id;
    }

    public Date getSystem_time() {
        return system_time;
    }

    public void setSystem_time(Date system_time) {
        this.system_time = system_time;
    }

    @Override
    public String toString() {
	return "Index_task [message_type_key=" + message_type_key + ", task_id=" + task_id + ", name=" + name + ", project_id=" + project_id
	        + ", zh_group_id=" + zh_group_id + ", progress=" + progress + ", start_time=" + start_time + ", end_time=" + end_time
	        + ", actual_start_time=" + actual_start_time + ", actual_end_time=" + actual_end_time + ", number=" + number + ", tenant_type="
	        + tenant_type + ", tenant_id=" + tenant_id + ", user_id=" + user_id + ", parents_id=" + parents_id + ", system_time=" + system_time
	        + "]";
    }

}
