package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = 7852510491824913005L;

    private int message_id;
    private int type_id;
    private String title;
    private int type;
    private int user_id;
    private Date time;
    private int is_read;
    private int is_push;
    private int is_process;// 消息事务处理标志（0：未处理 1：已处理）
    private int tenant_id;

    private int project_id;
    private int task_id;

    public int getMessage_id() {
	return message_id;
    }

    public void setMessage_id(int message_id) {
	this.message_id = message_id;
    }

    public int getType_id() {
	return type_id;
    }

    public void setType_id(int type_id) {
	this.type_id = type_id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
    }

    public Date getTime() {
	return time;
    }

    public void setTime(Date time) {
	this.time = time;
    }

    public int getIs_read() {
	return is_read;
    }

    public void setIs_read(int is_read) {
	this.is_read = is_read;
    }

    public int getIs_push() {
	return is_push;
    }

    public void setIs_push(int is_push) {
	this.is_push = is_push;
    }

    public int getIs_process() {
	return is_process;
    }

    public void setIs_process(int is_process) {
	this.is_process = is_process;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    @Override
    public String toString() {
	return "Message [message_id=" + message_id + ", type_id=" + type_id + ", title=" + title + ", type=" + type + ", user_id=" + user_id
	        + ", time=" + time + ", is_read=" + is_read + ", is_push=" + is_push + ", is_process=" + is_process + ", tenant_id=" + tenant_id
	        + ", project_id=" + project_id + ", task_id=" + task_id + "]";
    }

}
