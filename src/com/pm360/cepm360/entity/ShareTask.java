package com.pm360.cepm360.entity;

import java.util.Date;

public class ShareTask extends ZH_group_task {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int task_id;
    private int zh_group_id;
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
    private int wbs_id;
    private boolean has_child;
    private int level;
    private boolean expanded;
    private String change_id;
    private int pk;
    private int tenant_id;
    private int sort;

    // --------共享表信息------------------
    private int id;// 共享ID
    private int out_project_id;
    private String out_project_name;
    private int in_project_id;
    private String in_project_name;
    private int in_zh_group_id;
    private int out_company;// 从哪个公司共享出来的
    private String out_company_name;// 从哪个公司共享出来的
    private int in_company;// 共享给哪个公司的
    private String in_company_name;// 共享给哪个公司的
    private int accept_person;// 共享给哪个人
    private int mount_id;// 任务挂载节点
    private int out_status;// （出）任务共享状态 (GLOBAL.SHARE_TASK_STATUS)
    private int in_status;// （进）任务共享状态 (GLOBAL.SHARE_TASK_STATUS)

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    public int getZh_group_id() {
	return zh_group_id;
    }

    public void setZh_group_id(int zh_group_id) {
	this.zh_group_id = zh_group_id;
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

    public int getWbs_id() {
	return wbs_id;
    }

    public void setWbs_id(int wbs_id) {
	this.wbs_id = wbs_id;
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

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getOut_project_id() {
		return out_project_id;
	}

	public void setOut_project_id(int out_project_id) {
		this.out_project_id = out_project_id;
	}

	public String getOut_project_name() {
		return out_project_name;
	}

	public void setOut_project_name(String out_project_name) {
		this.out_project_name = out_project_name;
	}

	public int getIn_project_id() {
		return in_project_id;
	}

	public void setIn_project_id(int in_project_id) {
		this.in_project_id = in_project_id;
	}

	public String getIn_project_name() {
		return in_project_name;
	}

	public void setIn_project_name(String in_project_name) {
		this.in_project_name = in_project_name;
	}

	public int getIn_zh_group_id() {
		return in_zh_group_id;
	}

	public void setIn_zh_group_id(int in_zh_group_id) {
		this.in_zh_group_id = in_zh_group_id;
	}

    public int getOut_company() {
	return out_company;
    }

    public void setOut_company(int out_company) {
	this.out_company = out_company;
    }

    public String getOut_company_name() {
	return out_company_name;
    }

    public void setOut_company_name(String out_company_name) {
	this.out_company_name = out_company_name;
    }

    public int getIn_company() {
	return in_company;
    }

    public void setIn_company(int in_company) {
	this.in_company = in_company;
    }

    public String getIn_company_name() {
	return in_company_name;
    }

    public void setIn_company_name(String in_company_name) {
	this.in_company_name = in_company_name;
    }

    public int getAccept_person() {
	return accept_person;
    }

    public void setAccept_person(int accept_person) {
	this.accept_person = accept_person;
    }

    public int getMount_id() {
	return mount_id;
    }

    public void setMount_id(int mount_id) {
	this.mount_id = mount_id;
    }

    public int getOut_status() {
	return out_status;
    }

    public void setOut_status(int out_status) {
	this.out_status = out_status;
    }

    public int getIn_status() {
	return in_status;
    }

    public void setIn_status(int in_status) {
	this.in_status = in_status;
    }

	@Override
	public String toString() {
		return "ShareTask [task_id=" + task_id + ", zh_group_id=" + zh_group_id
				+ ", code=" + code + ", name=" + name + ", start_time="
				+ start_time + ", end_time=" + end_time
				+ ", actual_start_time=" + actual_start_time
				+ ", actual_end_time=" + actual_end_time + ", owner=" + owner
				+ ", type=" + type + ", status=" + status + ", progress="
				+ progress + ", plan_duration=" + plan_duration
				+ ", actual_duration=" + actual_duration + ", cc_user="
				+ cc_user + ", department=" + department + ", parents_id="
				+ parents_id + ", creater=" + creater + ", create_time="
				+ create_time + ", publish=" + publish + ", mark=" + mark
				+ ", feedback_creater=" + feedback_creater + ", wbs_id="
				+ wbs_id + ", has_child=" + has_child + ", level=" + level
				+ ", expanded=" + expanded + ", change_id=" + change_id
				+ ", pk=" + pk + ", tenant_id=" + tenant_id + ", sort=" + sort
				+ ", id=" + id + ", out_project_id=" + out_project_id
				+ ", out_project_name=" + out_project_name + ", in_project_id="
				+ in_project_id + ", in_project_name=" + in_project_name
				+ ", in_zh_group_id=" + in_zh_group_id + ", out_company="
				+ out_company + ", out_company_name=" + out_company_name
				+ ", in_company=" + in_company + ", in_company_name="
				+ in_company_name + ", accept_person=" + accept_person
				+ ", mount_id=" + mount_id + ", out_status=" + out_status
				+ ", in_status=" + in_status + "]";
	}

	@Override
	public void setParentsId(int parent_id) {
		this.parents_id = parents_id;
	}

}
