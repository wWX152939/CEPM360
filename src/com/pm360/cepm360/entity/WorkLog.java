package com.pm360.cepm360.entity;

import java.util.Date;

public class WorkLog extends WorkLogCell {

    /**
     * 
     */
    private static final long serialVersionUID = 1385304066604015733L;
    private int worklog_id;
    private int project_id;
    private int task_id;
    private String content;
    private Date date;
    private String attachment;// file_id，例如1,2,3

    public int getWorklog_id() {
	return worklog_id;
    }

    public void setWorklog_id(int worklog_id) {
	this.worklog_id = worklog_id;
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

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    @Override
    public String toString() {
	return "WorkLog [worklog_id=" + worklog_id + ", project_id=" + project_id + ", task_id=" + task_id + ", content=" + content + ", date="
	        + date + ", attachment=" + attachment + "]";
    }

	@Override
	public int getId() {
		return worklog_id;
	}
}
