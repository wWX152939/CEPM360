package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页-代办
 * 
 * @author Andy
 * 
 */
public class Index_daiban implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7394195400576601821L;

    private int type_id;
    private int message_id;
    private int message_type_key;
    private String title;
    private Date time;
    private int is_process;
    private int tenant_id;
    private int user_id;
    private Date start_time;
    private Date end_time;

    public int getType_id() {
	return type_id;
    }

    public void setType_id(int type_id) {
	this.type_id = type_id;
    }

    public int getMessage_id() {
	return message_id;
    }

    public void setMessage_id(int message_id) {
	this.message_id = message_id;
    }

    public int getMessage_type_key() {
	return message_type_key;
    }

    public void setMessage_type_key(int message_type_key) {
	this.message_type_key = message_type_key;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public Date getTime() {
	return time;
    }

    public void setTime(Date time) {
	this.time = time;
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

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
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
	return "Index_daiban [type_id=" + type_id + ", message_id=" + message_id + ", message_type_key=" + message_type_key + ", title=" + title
	        + ", time=" + time + ", is_process=" + is_process + ", tenant_id=" + tenant_id + ", user_id=" + user_id + ", start_time="
	        + start_time + ", end_time=" + end_time + "]";
    }

}
