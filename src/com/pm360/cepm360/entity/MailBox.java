package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件
 * 
 * @author Andy
 * 
 */
public class MailBox implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2058033086779622187L;
    private int mail_box_id;
    private int content_id;
    private int sender;// 发送人
    private String receiver;// 接收人 用户ID+公司ID组成[2-8840651],[3-8830457]
    private String receive_type;// 用户ID+接受类型组成 [2-to],[3-cc] GLOBAL.RECEIVE_TYPE
    private String is_send;// 是否发送， 1：已发送 2：未发送（草稿箱） GLOBAL.MSG_SENT
    private String is_read;// 是否读取，用户ID+是否读组成 GLOBAL.MSG_READ
    private String del_in;// 收件箱是否删除，用户ID+是否删除标志组成 1：删除 2：未删除 GLOBAL.MSG_DEL
    private String del_out;// 发件箱是否删除， 1：删除 2：未删除 GLOBAL.MSG_DEL
    private Date mail_time;// 发送和接收时间
    private int loop_id;// 邮件多次回复的标志，mail_box_id为标志

    private String title;// 邮寄标题
    private String content;// 邮寄内容
    private String attachment;// file_id，例如1,2,3

    private int mail_table_type;// 对应GLOBAL.MAIL_TABLE_TYPE，例如将合同变更内容表格作为邮件附件发送
    private int mail_table_id;// 例如合同变更ID

    public int getMail_box_id() {
	return mail_box_id;
    }

    public void setMail_box_id(int mail_box_id) {
	this.mail_box_id = mail_box_id;
    }

    public int getContent_id() {
	return content_id;
    }

    public void setContent_id(int content_id) {
	this.content_id = content_id;
    }

    public int getSender() {
	return sender;
    }

    public void setSender(int sender) {
	this.sender = sender;
    }

    public String getReceiver() {
	return receiver;
    }

    public void setReceiver(String receiver) {
	this.receiver = receiver;
    }

    public String getReceive_type() {
	return receive_type;
    }

    public void setReceive_type(String receive_type) {
	this.receive_type = receive_type;
    }

    public String getIs_send() {
	return is_send;
    }

    public void setIs_send(String is_send) {
	this.is_send = is_send;
    }

    public String getIs_read() {
	return is_read;
    }

    public void setIs_read(String is_read) {
	this.is_read = is_read;
    }

    public String getDel_in() {
	return del_in;
    }

    public void setDel_in(String del_in) {
	this.del_in = del_in;
    }

    public String getDel_out() {
	return del_out;
    }

    public void setDel_out(String del_out) {
	this.del_out = del_out;
    }

    public Date getMail_time() {
	return mail_time;
    }

    public void setMail_time(Date mail_time) {
	this.mail_time = mail_time;
    }

    public int getLoop_id() {
	return loop_id;
    }

    public void setLoop_id(int loop_id) {
	this.loop_id = loop_id;
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

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    public int getMail_table_type() {
	return mail_table_type;
    }

    public void setMail_table_type(int mail_table_type) {
	this.mail_table_type = mail_table_type;
    }

    public int getMail_table_id() {
	return mail_table_id;
    }

    public void setMail_table_id(int mail_table_id) {
	this.mail_table_id = mail_table_id;
    }

    @Override
    public String toString() {
	return "MailBox [mail_box_id=" + mail_box_id + ", content_id=" + content_id + ", sender=" + sender + ", receiver=" + receiver
	        + ", receive_type=" + receive_type + ", is_send=" + is_send + ", is_read=" + is_read + ", del_in=" + del_in + ", del_out=" + del_out
	        + ", mail_time=" + mail_time + ", loop_id=" + loop_id + ", title=" + title + ", attachment=" + attachment
	        + ", mail_table_type=" + mail_table_type + ", mail_table_id=" + mail_table_id + "]";
    }
}
