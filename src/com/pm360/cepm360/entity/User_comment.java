package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈
 * 
 * @author Andy
 * 
 */
public class User_comment implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 899221609010099553L;
    private int id;
    private int type;
    private String comments;
    private String attachment;
    private Date feedback_time;
    private int user_id;
    private int tenant_id;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    public Date getFeedback_time() {
	return feedback_time;
    }

    public void setFeedback_time(Date feedback_time) {
	this.feedback_time = feedback_time;
    }

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "User_comments [id=" + id + ", type=" + type + ", comments=" + comments + ", attachment=" + attachment + ", feedback_time="
	        + feedback_time + ", user_id=" + user_id + ", tenant_id=" + tenant_id + "]";
    }

}
