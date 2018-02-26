package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 会签
 * 
 * @author Andy
 * 
 */
public class Flow_countersign implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6274167861231816605L;
    private int id;// 流程审批ID
    private int flow_approval_id;// 流程审批ID
    private int countersign_user;// 会签人
    private String countersign_suggestion;// 会签意见
    private Date countersign_time;// 会签时间

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getFlow_approval_id() {
	return flow_approval_id;
    }

    public void setFlow_approval_id(int flow_approval_id) {
	this.flow_approval_id = flow_approval_id;
    }

    public int getCountersign_user() {
	return countersign_user;
    }

    public void setCountersign_user(int countersign_user) {
	this.countersign_user = countersign_user;
    }

    public String getCountersign_suggestion() {
	return countersign_suggestion;
    }

    public void setCountersign_suggestion(String countersign_suggestion) {
	this.countersign_suggestion = countersign_suggestion;
    }

    public Date getCountersign_time() {
	return countersign_time;
    }

    public void setCountersign_time(Date countersign_time) {
	this.countersign_time = countersign_time;
    }

    @Override
    public String toString() {
	return "Flow_countersign [id=" + id + ", flow_approval_id=" + flow_approval_id + ", countersign_user=" + countersign_user
	        + ", countersign_suggestion=" + countersign_suggestion + ", countersign_time=" + countersign_time + "]";
    }
}
