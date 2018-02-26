package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 招标流程
 * 
 * @author Andy
 * 
 */
public class ZB_flow extends Expandable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5802647276164216667L;

    private int zb_flow_id;
    private int project_id;
    private int zb_plan_id;
    private int period;// 周期
    private Date start_date;// 开始时间
    private Date end_date;// 结束时间
    private int progress;// 进度
    private int owner;// 责任人
    private int dep;// 责任部门
    private int status;// 状态
    private int flow_type;// GLOBAL.BID_TYPE
    private int p_id;// 父节点
    private int tenent_id;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;// 招标名称

    public int getZb_flow_id() {
	return zb_flow_id;
    }

    public void setZb_flow_id(int zb_flow_id) {
	this.zb_flow_id = zb_flow_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getZb_plan_id() {
	return zb_plan_id;
    }

    public void setZb_plan_id(int zb_plan_id) {
	this.zb_plan_id = zb_plan_id;
    }

    public int getPeriod() {
	return period;
    }

    public void setPeriod(int period) {
	this.period = period;
    }

    public Date getStart_date() {
	return start_date;
    }

    public void setStart_date(Date start_date) {
	this.start_date = start_date;
    }

    public Date getEnd_date() {
	return end_date;
    }

    public void setEnd_date(Date end_date) {
	this.end_date = end_date;
    }

    public int getProgress() {
	return progress;
    }

    public void setProgress(int progress) {
	this.progress = progress;
    }

    public int getOwner() {
	return owner;
    }

    public void setOwner(int owner) {
	this.owner = owner;
    }

    public int getDep() {
	return dep;
    }

    public void setDep(int dep) {
	this.dep = dep;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public int getFlow_type() {
	return flow_type;
    }

    public void setFlow_type(int flow_type) {
	this.flow_type = flow_type;
    }

    public int getP_id() {
	return p_id;
    }

    public void setP_id(int p_id) {
	this.p_id = p_id;
    }

    public int getTenent_id() {
	return tenent_id;
    }

    public void setTenent_id(int tenent_id) {
	this.tenent_id = tenent_id;
    }

    @Override
    public String toString() {
	return "ZB_flow [zb_flow_id=" + zb_flow_id + ", project_id=" + project_id + ", zb_plan_id=" + zb_plan_id + ", period=" + period
	        + ", start_date=" + start_date + ", end_date=" + end_date + ", progress=" + progress + ", owner=" + owner + ", dep=" + dep
	        + ", status=" + status + ", flow_type=" + flow_type + ", p_id=" + p_id + ", tenent_id=" + tenent_id + ", name=" + name + "]";
    }

	@Override
	public int getId() {
		return zb_flow_id;
	}

	@Override
	public int getParents_id() {
		return p_id;
	}

	@Override
	public void setParentsId(int parent_id) {
		p_id = parent_id;
	}

}
