package com.pm360.cepm360.entity;

import java.util.Date;

public class Task extends TaskCell {

    private static final long serialVersionUID = 7812249025160571866L;
    private int task_id;
    private int project_id;
    private String code;
    private String name;
    private Date start_time;
    private Date end_time;
    private Date actual_start_time;
    private Date actual_end_time;
    private int owner;
    private String type;
    private int status;
    private int progress;
    private String plan_duration;
    private String actual_duration;
    private String cc_user;
    private int department;
    private int parents_id;
    private int creater;
    private Date create_time;
    private int publish;
    private String mark;
    private int feedback_creater;
    private int templet_wbs_dir_id;
    private boolean has_child;
    private int level;
    private boolean expanded;
    private String change_id;
    private int pk;
    private int tenant_id;
    private int sort;

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
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

    public int getOwner() {
	return owner;
    }

    public void setOwner(int owner) {
	this.owner = owner;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
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

    public String getCc_user() {
	return cc_user;
    }

    public void setCc_user(String cc_user) {
	this.cc_user = cc_user;
    }

    public int getDepartment() {
	return department;
    }

    public void setDepartment(int department) {
	this.department = department;
    }

    public int getParents_id() {
	return parents_id;
    }

    public void setParents_id(int parents_id) {
	this.parents_id = parents_id;
    }

    public int getCreater() {
	return creater;
    }

    public void setCreater(int creater) {
	this.creater = creater;
    }

    public Date getCreate_time() {
	return create_time;
    }

    public void setCreate_time(Date create_time) {
	this.create_time = create_time;
    }

    public int getPublish() {
	return publish;
    }

    public void setPublish(int publish) {
	this.publish = publish;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public int getFeedback_creater() {
	return feedback_creater;
    }

    public void setFeedback_creater(int feedback_creater) {
	this.feedback_creater = feedback_creater;
    }

    public int getTemplet_wbs_dir_id() {
        return templet_wbs_dir_id;
    }

    public void setTemplet_wbs_dir_id(int templet_wbs_dir_id) {
        this.templet_wbs_dir_id = templet_wbs_dir_id;
    }

    public boolean isHas_child() {
	return has_child;
    }

    public void setHas_child(boolean has_child) {
	this.has_child = has_child;
    }

    public int getLevel() {
	return level;
    }

    public void setLevel(int level) {
	this.level = level;
    }

    public boolean isExpanded() {
	return expanded;
    }

    public void setExpanded(boolean expanded) {
	this.expanded = expanded;
    }

    public String getChange_id() {
	return change_id;
    }

    public void setChange_id(String change_id) {
	this.change_id = change_id;
    }

    public int getPk() {
	return pk;
    }

    public void setPk(int pk) {
	this.pk = pk;
    }

    public int getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
        this.tenant_id = tenant_id;
    }


    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
	return "Task [task_id=" + task_id + ", project_id=" + project_id + ", code=" + code + ", name=" + name + ", start_time=" + start_time
	        + ", end_time=" + end_time + ", actual_start_time=" + actual_start_time + ", actual_end_time=" + actual_end_time + ", owner=" + owner
	        + ", type=" + type + ", status=" + status + ", progress=" + progress + ", plan_duration=" + plan_duration + ", actual_duration="
	        + actual_duration + ", cc_user=" + cc_user + ", department=" + department + ", parents_id=" + parents_id + ", creater=" + creater
	        + ", create_time=" + create_time + ", publish=" + publish + ", mark=" + mark + ", feedback_creater=" + feedback_creater
	        + ", templet_wbs_dir_id=" + templet_wbs_dir_id + ", has_child=" + has_child + ", level=" + level + ", expanded=" + expanded
	        + ", change_id=" + change_id + ", pk=" + pk + ", tenant_id=" + tenant_id + ", sort=" + sort + "]";
    }

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return task_id;
	}

	@Override
	public void setParentsId(int parent_id) {
		parents_id = parent_id;
	}

	@Override
	public int getZh_group_id() {
		return 0;
	}

	@Override
	public void setZh_group_id(int ground_id) {
		// TODO Auto-generated method stub
		
	}

}
