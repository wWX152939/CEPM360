package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 出库
 * 
 * @author Andy
 * 
 */
public class P_CK implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3624536594071347984L;

    private int ck_id;
    private String ck_number;
    private int task_id;
    private String task_name;
    private String storehouse;
    private int storeman;
    private int use_person;
    private Date out_date;
    private int project_id;
    private String project_name;
    private int tenant_id;

    public int getCk_id() {
	return ck_id;
    }

    public void setCk_id(int ck_id) {
	this.ck_id = ck_id;
    }

    public String getCk_number() {
	return ck_number;
    }

    public void setCk_number(String ck_number) {
	this.ck_number = ck_number;
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

    public String getStorehouse() {
	return storehouse;
    }

    public void setStorehouse(String storehouse) {
	this.storehouse = storehouse;
    }

    public int getStoreman() {
	return storeman;
    }

    public void setStoreman(int storeman) {
	this.storeman = storeman;
    }

    public int getUse_person() {
	return use_person;
    }

    public void setUse_person(int use_person) {
	this.use_person = use_person;
    }

    public Date getOut_date() {
	return out_date;
    }

    public void setOut_date(Date out_date) {
	this.out_date = out_date;
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

    @Override
    public String toString() {
	return "P_CK [ck_id=" + ck_id + ", ck_number=" + ck_number + ", task_id=" + task_id + ", task_name=" + task_name + ", storehouse="
	        + storehouse + ", storeman=" + storeman + ", use_person=" + use_person + ", out_date=" + out_date + ", project_id=" + project_id
	        + ", project_name=" + project_name + ", tenant_id=" + tenant_id + "]";
    }

}
