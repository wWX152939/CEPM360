package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 采购计划
 * 
 * @author Andy
 * 
 */
public class P_CGJH implements MarkFlowCell {

    /**
     * 
     */
    private static final long serialVersionUID = 1057838293657009387L;

    private int cgjh_id;
    private String cgjh_number;
    private String cgjh_name;
    private int task_id;
    private String task_name;
    private double total_money;
    private int plan_person;
    private int execute_person;
    private Date report_date;
    private Date plan_date;
    private String mark;
    private int project_id;
    private String project_name;
    private int tenant_id;
    private int status;//公用GLOBAL.FLOW_APPROVAL_STATUS

    public int getCgjh_id() {
	return cgjh_id;
    }

    public void setCgjh_id(int cgjh_id) {
	this.cgjh_id = cgjh_id;
    }

    public String getCgjh_number() {
	return cgjh_number;
    }

    public void setCgjh_number(String cgjh_number) {
	this.cgjh_number = cgjh_number;
    }

    public String getCgjh_name() {
	return cgjh_name;
    }

    public void setCgjh_name(String cgjh_name) {
	this.cgjh_name = cgjh_name;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public double getTotal_money() {
	return total_money;
    }

    public void setTotal_money(double total_money) {
	this.total_money = total_money;
    }

    public int getPlan_person() {
	return plan_person;
    }

    public void setPlan_person(int plan_person) {
	this.plan_person = plan_person;
    }

    public int getExecute_person() {
	return execute_person;
    }

    public void setExecute_person(int execute_person) {
	this.execute_person = execute_person;
    }

    public Date getReport_date() {
	return report_date;
    }

    public void setReport_date(Date report_date) {
	this.report_date = report_date;
    }

    public Date getPlan_date() {
	return plan_date;
    }

    public void setPlan_date(Date plan_date) {
	this.plan_date = plan_date;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
	return "P_CGJH [cgjh_id=" + cgjh_id + ", cgjh_number=" + cgjh_number + ", cgjh_name=" + cgjh_name + ", task_id=" + task_id + ", task_name="
	        + task_name + ", total_money=" + total_money + ", plan_person=" + plan_person + ", execute_person=" + execute_person
	        + ", report_date=" + report_date + ", plan_date=" + plan_date + ", mark=" + mark + ", project_id=" + project_id + ", project_name="
	        + project_name + ", tenant_id=" + tenant_id + ", status=" + status + "]";
    }

	@Override
	public int getId() {
		return cgjh_id;
	}

	@Override
	public void setId(int typeId) {
		this.cgjh_id = typeId;
	}

}
