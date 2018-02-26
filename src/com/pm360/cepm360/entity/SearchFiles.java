package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class SearchFiles implements Serializable {

    private static final long serialVersionUID = -8026699003323776825L;
    private String content;
    private String file_type;
    private Date start_time;
    private Date end_time;
    private String project;
    private int tenant_id;

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public String getFile_type() {
	return file_type;
    }

    public void setFile_type(String file_type) {
	this.file_type = file_type;
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

    public String getProject() {
	return project;
    }

    public void setProject(String project) {
	this.project = project;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "SearchFiles [content=" + content + ", file_type=" + file_type + ", start_time=" + start_time + ", end_time=" + end_time
	        + ", project=" + project + ", tenant_id=" + tenant_id + "]";
    }

}
