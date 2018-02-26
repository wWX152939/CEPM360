package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 采购预算
 * 
 * @author Andy
 * 
 */
public class P_CGYS implements MarkFlowCell {

    /**
     * 
     */
    private static final long serialVersionUID = -7565314259534958898L;

    private int cgys_id;
    private String cgys_number;
    private String cgys_name;
    private int task_id;
    private String task_name;
    private int workload;
    private double total_money;
    private int purchase_item;
    private int creator;
    private Date create_date;
    private int project_id;
    private int tenant_id;
    private int status;

    public int getCgys_id() {
	return cgys_id;
    }

    public void setCgys_id(int cgys_id) {
	this.cgys_id = cgys_id;
    }

    public String getCgys_number() {
	return cgys_number;
    }

    public void setCgys_number(String cgys_number) {
	this.cgys_number = cgys_number;
    }

    public String getCgys_name() {
	return cgys_name;
    }

    public void setCgys_name(String cgys_name) {
	this.cgys_name = cgys_name;
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

    public int getWorkload() {
	return workload;
    }

    public void setWorkload(int workload) {
	this.workload = workload;
    }

    public double getTotal_money() {
	return total_money;
    }

    public void setTotal_money(double total_money) {
	this.total_money = total_money;
    }

    public int getPurchase_item() {
	return purchase_item;
    }

    public void setPurchase_item(int purchase_item) {
	this.purchase_item = purchase_item;
    }

    public int getCreator() {
	return creator;
    }

    public void setCreator(int creator) {
	this.creator = creator;
    }

    public Date getCreate_date() {
	return create_date;
    }

    public void setCreate_date(Date create_date) {
	this.create_date = create_date;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
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
	return "P_CGYS [cgys_id=" + cgys_id + ", cgys_number=" + cgys_number + ", cgys_name=" + cgys_name + ", task_id=" + task_id + ", task_name="
	        + task_name + ", workload=" + workload + ", total_money=" + total_money + ", purchase_item=" + purchase_item + ", creator=" + creator
	        + ", create_date=" + create_date + ", project_id=" + project_id + ", tenant_id=" + tenant_id + ", status=" + status + "]";
    }

	@Override
	public int getId() {
		return cgys_id;
	}

	@Override
	public void setId(int id) {
		cgys_id = id;
	}

	@Override
	public int getPlan_person() {
		return creator;
	}

	@Override
	public void setPlan_person(int plan_person) {
		creator = plan_person;
	}

	@Override
	public String getMark() {
		return "";
	}

	@Override
	public void setMark(String mark) {
		
	}

}
