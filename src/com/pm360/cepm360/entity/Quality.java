package com.pm360.cepm360.entity;

import com.pm360.cepm360.common.GLOBAL;

import java.util.Date;

/**
 * 质量文明
 * 
 * @author Andy
 * 
 */
public class Quality extends AttachWaterMarkCell {

    /**
     * 
     */
    private static final long serialVersionUID = 4518706237408793227L;
    private int quality_id;
    private int project_id;
    private int zh_group_id;
    private int task_id;
    private String name;
    private int type;// 质量类型类型
    private String content;
    private Date event_date;// 发生时间
    private Date create_date;// 创建时间
    private int tenant_id;
    private String attachment;// file_id，例如1,2,3

    private String msg_user;// 消息接收者(父节点user以及抄送user,以逗号分开，例如10,20)

    public int getQuality_id() {
	return quality_id;
    }

    public void setQuality_id(int quality_id) {
	this.quality_id = quality_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
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

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public Date getEvent_date() {
	return event_date;
    }

    public void setEvent_date(Date event_date) {
	this.event_date = event_date;
    }

    public Date getCreate_date() {
	return create_date;
    }

    public void setCreate_date(Date create_date) {
	this.create_date = create_date;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    public String getMsg_user() {
	return msg_user;
    }

    public void setMsg_user(String msg_user) {
	this.msg_user = msg_user;
    }

    @Override
    public String toString() {
	return "Quality [quality_id=" + quality_id + ", project_id=" + project_id + ", zh_group_id=" + zh_group_id + ", task_id=" + task_id
	        + ", name=" + name + ", type=" + type + ", content=" + content + ", event_date=" + event_date + ", create_date=" + create_date
	        + ", tenant_id=" + tenant_id + ", attachment=" + attachment + ", msg_user=" + msg_user + "]";
    }

    @Override
    public int getId() {
	return quality_id;
    }

    @Override
    public int getTaskId() {
	return task_id;
    }

    @Override
    public String getTypeName() {
	int id = type == 0 ? 1 : type;
	return GLOBAL.QUALITY_TYPE[id - 1][1];
    }

}
