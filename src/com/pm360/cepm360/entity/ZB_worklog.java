package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 招标工作日志
 * 
 * @author Andy
 * 
 */
public class ZB_worklog extends WorkLogCell {

    /**
     * 
     */
    private static final long serialVersionUID = -3169710091071280697L;
    private int zb_worklog_id;
    private int zb_plan_id;
    private int zb_flow_id;
    private String content;
    private Date date;
    private String attachment;// file_id，例如1,2,3

    public int getZb_worklog_id() {
	return zb_worklog_id;
    }

    public void setZb_worklog_id(int zb_worklog_id) {
	this.zb_worklog_id = zb_worklog_id;
    }

    public int getZb_plan_id() {
	return zb_plan_id;
    }

    public void setZb_plan_id(int zb_plan_id) {
	this.zb_plan_id = zb_plan_id;
    }

    public int getZb_flow_id() {
	return zb_flow_id;
    }

    public void setZb_flow_id(int zb_flow_id) {
	this.zb_flow_id = zb_flow_id;
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
	return "ZB_worklog [zb_worklog_id=" + zb_worklog_id + ", zb_plan_id=" + zb_plan_id + ", zb_flow_id=" + zb_flow_id + ", content=" + content
	        + ", date=" + date + ", attachment=" + attachment + "]";
    }

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return zb_worklog_id;
	}

	@Override
	public void setTask_id(int task_id) {
		this.zb_plan_id = task_id;
	}

}
