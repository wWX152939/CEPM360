package com.pm360.cepm360.entity;

import java.util.Date;

public class ZH_group_worklog extends WorkLogCell {

    /**
     * 
     */
    private static final long serialVersionUID = 8322318888755857396L;
    private int zh_group_worklog_id;
    private int zh_group_id;
    private int task_id;
    private int creater;

	private String content;
    private Date date;
    private String attachment;// file_id，例如1,2,3

    public int getZh_group_worklog_id() {
	return zh_group_worklog_id;
    }

    public void setZh_group_worklog_id(int zh_group_worklog_id) {
	this.zh_group_worklog_id = zh_group_worklog_id;
    }
    
	public int getCreater() {
		return creater;
	}

	public void setCreater(int creater) {
		this.creater = creater;
	}

    public int getZh_group_id() {
	return zh_group_id;
    }

    public void setZh_group_id(int zh_group_id) {
	this.zh_group_id = zh_group_id;
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
		return "ZH_group_worklog [zh_group_worklog_id=" + zh_group_worklog_id
				+ ", zh_group_id=" + zh_group_id + ", task_id=" + task_id
				+ ", creater=" + creater + ", content=" + content + ", date="
				+ date + ", attachment=" + attachment + "]";
	}

	@Override
	public int getId() {
		return zh_group_worklog_id;
	}

}
