package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class Changes implements Serializable {

    private static final long serialVersionUID = 7403910056856047658L;
    private int change_id;
    private int project_id;
    private String code;
    private String title;
    private String content;
    private int creater;
    private Date create_time;
    private String status;
    private String version_number;

    public int getChange_id() {
	return change_id;
    }

    public void setChange_id(int change_id) {
	this.change_id = change_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
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

    public Date getCreate_time() {
	return create_time;
    }

    public void setCreate_time(Date create_time) {
	this.create_time = create_time;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getVersion_number() {
	return version_number;
    }

    public void setVersion_number(String version_number) {
	this.version_number = version_number;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Changes [change_id=" + change_id + ", project_id=" + project_id + ", code=" + code + ", title=" + title + ", content=" + content
		+ ", creater=" + creater + ", create_time=" + create_time + ", status=" + status + ", version_number=" + version_number + "]";
    }
}
