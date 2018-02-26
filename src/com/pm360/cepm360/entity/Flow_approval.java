package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class Flow_approval implements Serializable, Comparable<Flow_approval> {

    /**
     * 
     */
    private static final long serialVersionUID = 3324798979602096464L;

    private int flow_approval_id;// 流程审批ID
    private String flow_type;// 流程类型 对应流程设置表的flow_type
    private int type_id;// 类型ID (例如采购单据ID、计划单据ID)
    private int current_level;// 流程当前审批人ID
    private int next_level;// 流程下一级审批人ID
    private int submiter;// 流程发起提交人
    private int status;// 流程状态，对应宏定义里面的 FLOW_APPROVAL_STATUS (1:审批中 2：通过 3：驳回 )
    private String comment; // 审批意见
    private Date submit_time;// 流程提交时间 也就是add...的时间
    private Date approval_time;// 流程审批时间
    private int tenant_id;
    private int message_id;

    public int getFlow_approval_id() {
	return flow_approval_id;
    }

    public void setFlow_approval_id(int flow_approval_id) {
	this.flow_approval_id = flow_approval_id;
    }

    public String getFlow_type() {
	return flow_type;
    }

    public void setFlow_type(String flow_type) {
	this.flow_type = flow_type;
    }

    public int getType_id() {
	return type_id;
    }

    public void setType_id(int type_id) {
	this.type_id = type_id;
    }

    public int getCurrent_level() {
	return current_level;
    }

    public void setCurrent_level(int current_level) {
	this.current_level = current_level;
    }

    public int getNext_level() {
	return next_level;
    }

    public void setNext_level(int next_level) {
	this.next_level = next_level;
    }

    public int getSubmiter() {
	return submiter;
    }

    public void setSubmiter(int submiter) {
	this.submiter = submiter;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    public Date getSubmit_time() {
	return submit_time;
    }

    public void setSubmit_time(Date submit_time) {
	this.submit_time = submit_time;
    }

    public Date getApproval_time() {
	return approval_time;
    }

    public void setApproval_time(Date approval_time) {
	this.approval_time = approval_time;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int compareTo(Flow_approval flow) {
	return flow.getSubmit_time().compareTo(this.getSubmit_time());
    }

    public int getMessage_id() {
	return message_id;
    }

    public void setMessage_id(int message_id) {
	this.message_id = message_id;
    }


    @Override
    public String toString() {
	return "Flow_approval [flow_approval_id=" + flow_approval_id + ", flow_type=" + flow_type + ", type_id=" + type_id + ", current_level="
	        + current_level + ", next_level=" + next_level + ", submiter=" + submiter + ", status=" + status + ", comment=" + comment
	        + ", submit_time=" + submit_time + ", approval_time=" + approval_time + ", tenant_id=" + tenant_id + ", message_id=" + message_id
	        + "]";
    }

}
