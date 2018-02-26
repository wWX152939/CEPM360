package com.pm360.cepm360.entity;

import java.util.Date;

public class ZH_group_risk extends AttachTaskCell {

    /**
     * 
     */
    private static final long serialVersionUID = -5060988848050356063L;
    private int zh_group_risk_id;
    private int zh_group_id;
    private int task_id;
    private String risk_number;// 风险号
    private String risk_name;
    private String problem_description;//问题描述
    private String fix_result;// 处理意见
    private int fix_company;// 处理单位
    private int check_user;// 核查人
    private Date check_date;// 核查时间
    private String attachment;// file_id，例如1,2,3

    public int getZh_group_risk_id() {
	return zh_group_risk_id;
    }

    public void setZh_group_risk_id(int zh_group_risk_id) {
	this.zh_group_risk_id = zh_group_risk_id;
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

    public String getRisk_number() {
	return risk_number;
    }

    public void setRisk_number(String risk_number) {
	this.risk_number = risk_number;
    }

    public String getRisk_name() {
	return risk_name;
    }

    public void setRisk_name(String risk_name) {
	this.risk_name = risk_name;
    }

    public String getProblem_description() {
        return problem_description;
    }

    public void setProblem_description(String problem_description) {
        this.problem_description = problem_description;
    }

    public String getFix_result() {
	return fix_result;
    }

    public void setFix_result(String fix_result) {
	this.fix_result = fix_result;
    }

    public int getFix_company() {
	return fix_company;
    }

    public void setFix_company(int fix_company) {
	this.fix_company = fix_company;
    }

    public int getCheck_user() {
	return check_user;
    }

    public void setCheck_user(int check_user) {
	this.check_user = check_user;
    }

    public Date getCheck_date() {
	return check_date;
    }

    public void setCheck_date(Date check_date) {
	this.check_date = check_date;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    @Override
    public String toString() {
	return "ZH_group_risk [zh_group_risk_id=" + zh_group_risk_id + ", zh_group_id=" + zh_group_id + ", task_id=" + task_id + ", risk_number="
	        + risk_number + ", risk_name=" + risk_name + ", problem_description=" + problem_description + ", fix_result=" + fix_result
	        + ", fix_company=" + fix_company + ", check_user=" + check_user + ", check_date=" + check_date + ", attachment=" + attachment + "]";
    }

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return zh_group_risk_id;
	}

	@Override
	public int getTaskId() {
		return task_id;
	}

}
