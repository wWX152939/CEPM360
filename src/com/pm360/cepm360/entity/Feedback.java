package com.pm360.cepm360.entity;

import java.util.Date;

public class Feedback extends FeedbackCell {

    private static final long serialVersionUID = -8543373134651720235L;
    private int feedback_id;
    private int project_id;
    private int task_id;
    private String task_name;
    private String change_id;
    private Date actual_start_time;
    private Date actual_end_time;
    private int status;
    private int progress;
    private String mark;
    private int feedback_creater;
    private String cc_user;
    private int parents_user;
    private Date feedback_time;

    public int getFeedback_id() {
	return feedback_id;
    }

    public void setFeedback_id(int feedback_id) {
	this.feedback_id = feedback_id;
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

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getChange_id() {
	return change_id;
    }

    public void setChange_id(String change_id) {
	this.change_id = change_id;
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

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public int getProgress() {
	return progress;
    }

    public void setProgress(int progress) {
	this.progress = progress;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public int getFeedback_creater() {
	return feedback_creater;
    }

    public void setFeedback_creater(int feedback_creater) {
	this.feedback_creater = feedback_creater;
    }

    public String getCc_user() {
	return cc_user;
    }

    public void setCc_user(String cc_user) {
	this.cc_user = cc_user;
    }

    public int getParents_user() {
	return parents_user;
    }

    public void setParents_user(int parents_user) {
	this.parents_user = parents_user;
    }

    public Date getFeedback_time() {
        return feedback_time;
    }

    public void setFeedback_time(Date feedback_time) {
        this.feedback_time = feedback_time;
    }

    @Override
    public String toString() {
	return "Feedback [feedback_id=" + feedback_id + ", project_id=" + project_id + ", task_id=" + task_id + ", task_name=" + task_name
	        + ", change_id=" + change_id + ", actual_start_time=" + actual_start_time + ", actual_end_time=" + actual_end_time + ", status="
	        + status + ", progress=" + progress + ", mark=" + mark + ", feedback_creater=" + feedback_creater + ", cc_user=" + cc_user
	        + ", parents_user=" + parents_user + ", feedback_time=" + feedback_time + "]";
    }

}
