package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class Project implements Serializable, Cloneable {

    private static final long serialVersionUID = 5656614395127569059L;
    private int project_id;
    private int eps_id;
    private int tenant_id;
    private String project_number;
    private String name;
    private Date start_time;
    private Date end_time;
    private Date actual_start_time;
    private Date actual_end_time;
    private int status;
    private int progress;
    private String plan_duration;
    private String actual_duration;
    private int department;
    private String location;
    private String mark;
    private String construction_unit;
    private int templet_document_id;
    private int templet_wbs_id;
    private int creater;
    private int owner;
    private int tab_flag;
    private double control_cost;//材料控制费用
    private double other_control_cost;//其它控制费用
    private int work_load;//工程量
    
    private String storehouse;
    private String task_name;

    // 非该表的字段
    private int user_id;

    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && toString().equals(o.toString());
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getEps_id() {
	return eps_id;
    }

    public void setEps_id(int eps_id) {
	this.eps_id = eps_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }


    public String getProject_number() {
	return project_number;
    }

    public void setProject_number(String project_number) {
	this.project_number = project_number;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
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

    public Date getActual_start_time() {
	return actual_start_time;
    }

    public void setActual_start_time(Date actual_start_time) {
	this.actual_start_time = actual_start_time;
    }

    public Date getActual_end_time() {
	return actual_end_time;
    }

    public void setActual_end_time(Date actual_end_time) {
	this.actual_end_time = actual_end_time;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public int getProgress() {
	return progress;
    }

    public void setProgress(int progress) {
	this.progress = progress;
    }

    public String getPlan_duration() {
	return plan_duration;
    }

    public void setPlan_duration(String plan_duration) {
	this.plan_duration = plan_duration;
    }

    public String getActual_duration() {
	return actual_duration;
    }

    public void setActual_duration(String actual_duration) {
	this.actual_duration = actual_duration;
    }

    public int getDepartment() {
	return department;
    }

    public void setDepartment(int department) {
	this.department = department;
    }


    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public String getConstruction_unit() {
	return construction_unit;
    }

    public void setConstruction_unit(String construction_unit) {
	this.construction_unit = construction_unit;
    }




    public int getTemplet_document_id() {
        return templet_document_id;
    }

    public void setTemplet_document_id(int templet_document_id) {
        this.templet_document_id = templet_document_id;
    }

    public int getTemplet_wbs_id() {
        return templet_wbs_id;
    }

    public void setTemplet_wbs_id(int templet_wbs_id) {
        this.templet_wbs_id = templet_wbs_id;
    }

    public int getCreater() {
	return creater;
    }

    public void setCreater(int creater) {
	this.creater = creater;
    }

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
    }

    public int getTab_flag() {
	return tab_flag;
    }

    public void setTab_flag(int tab_flag) {
	this.tab_flag = tab_flag;
    }

    public String getStorehouse() {
        return storehouse;
    }

    public void setStorehouse(String storehouse) {
        this.storehouse = storehouse;
    }

    public double getControl_cost() {
        return control_cost;
    }

    public void setControl_cost(double control_cost) {
        this.control_cost = control_cost;
    }

    public double getOther_control_cost() {
        return other_control_cost;
    }

    public void setOther_control_cost(double other_control_cost) {
        this.other_control_cost = other_control_cost;
    }

    public int getWork_load() {
        return work_load;
    }

    public void setWork_load(int work_load) {
        this.work_load = work_load;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    @Override
    public String toString() {
	return "Project [project_id=" + project_id + ", eps_id=" + eps_id + ", tenant_id=" + tenant_id + ", project_number=" + project_number
	        + ", name=" + name + ", start_time=" + start_time + ", end_time=" + end_time + ", actual_start_time=" + actual_start_time
	        + ", actual_end_time=" + actual_end_time + ", status=" + status + ", progress=" + progress + ", plan_duration=" + plan_duration
	        + ", actual_duration=" + actual_duration + ", department=" + department + ", location=" + location + ", mark=" + mark
	        + ", construction_unit=" + construction_unit + ", templet_document_id=" + templet_document_id + ", templet_wbs_id=" + templet_wbs_id
	        + ", creater=" + creater + ", owner=" + owner + ", tab_flag=" + tab_flag + ", control_cost=" + control_cost + ", other_control_cost="
	        + other_control_cost + ", work_load=" + work_load + ", storehouse=" + storehouse + ", task_name=" + task_name + ", user_id="
	        + user_id + "]";
    }

}
