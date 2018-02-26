package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 公告
 * 
 * @author Andy
 * 
 */
public class Announcement implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2733639503649817149L;
    private int announcement_id;
    private String title;
    private String content;
    private int creater;
    private int status;// 发布状态 PUBLISH_STATUS 1或者5
    private int tenant_id;
    private Date publish_time;
    

    private Date start_time;
    private Date end_time;

    public int getAnnouncement_id() {
	return announcement_id;
    }

    public void setAnnouncement_id(int announcement_id) {
	this.announcement_id = announcement_id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public int getCreater() {
	return creater;
    }

    public void setCreater(int creater) {
	this.creater = creater;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public Date getPublish_time() {
	return publish_time;
    }

    public void setPublish_time(Date publish_time) {
	this.publish_time = publish_time;
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

    @Override
    public String toString() {
	return "Announcement [announcement_id=" + announcement_id + ", title=" + title + ", content=" + content + ", creater=" + creater
	        + ", status=" + status + ", tenant_id=" + tenant_id + ", publish_time=" + publish_time + ", start_time=" + start_time + ", end_time="
	        + end_time + "]";
    }
}
