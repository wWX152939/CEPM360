package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 最新反馈
 * 
 * @author Andy
 * 
 */
public class Index_feedback implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5287554818525666752L;
    private int message_type_key;// (9,10,17,18,19,20,21)
    private int type_id;
    private int project_id;
    private String project_name;
    private int zh_group_id;
    private String group_name;
    private int task_id;
    private String title;
    private String attachment;
    private int tenant_id;
    private int user_id;// 当前用户
    private int owner;// 该反馈是谁反馈的，即责任人
    private Date start_time;
    private Date end_time;
    private Date time;

    public int getMessage_type_key() {
	return message_type_key;
    }

    public void setMessage_type_key(int message_type_key) {
	this.message_type_key = message_type_key;
    }

    public int getType_id() {
	return type_id;
    }

    public void setType_id(int type_id) {
	this.type_id = type_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public String getProject_name() {
	return project_name;
    }

    public void setProject_name(String project_name) {
	this.project_name = project_name;
    }

    public int getZh_group_id() {
	return zh_group_id;
    }

    public void setZh_group_id(int zh_group_id) {
	this.zh_group_id = zh_group_id;
    }

    public String getGroup_name() {
	return group_name;
    }

    public void setGroup_name(String group_name) {
	this.group_name = group_name;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
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

    public int getOwner() {
	return owner;
    }

    public void setOwner(int owner) {
	this.owner = owner;
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

    public Date getTime() {
	return time;
    }

    public void setTime(Date time) {
	this.time = time;
    }

    @Override
    public String toString() {
	return "Index_feedback [message_type_key=" + message_type_key + ", type_id=" + type_id + ", project_id=" + project_id + ", project_name="
	        + project_name + ", zh_group_id=" + zh_group_id + ", group_name=" + group_name + ", task_id=" + task_id + ", title=" + title
	        + ", attachment=" + attachment + ", tenant_id=" + tenant_id + ", user_id=" + user_id + ", owner=" + owner + ", start_time="
	        + start_time + ", end_time=" + end_time + ", time=" + time + "]";
    }

}
