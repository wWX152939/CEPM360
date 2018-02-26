package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 组合统计信息
 * 
 * @author Andy
 * 
 */
public class ZH_count_info implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1582614887065626744L;
    private int project_id;
    private int zh_group_id;
    private String node_name;
    private int finish_rate;// 完成率
    private int owner;// 责任人
    private String period;// 周期
    private Date start_date;// 开始时间
    private Date end_date;// 结束时间
    private int total_task_count;// 总任务数
    private int finished_count;// 已完成
    private int in_progress_count;// 进行中
    private int undoing_count;// 未开始
    private int in_progress_workload;// 进行中的总工作量

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getZh_group_id() {
	return zh_group_id;
    }

    public void setZh_group_id(int zh_group_id) {
	this.zh_group_id = zh_group_id;
    }

    public String getNode_name() {
	return node_name;
    }

    public void setNode_name(String node_name) {
	this.node_name = node_name;
    }

    public int getFinish_rate() {
	return finish_rate;
    }

    public void setFinish_rate(int finish_rate) {
	this.finish_rate = finish_rate;
    }

    public int getOwner() {
	return owner;
    }

    public void setOwner(int owner) {
	this.owner = owner;
    }

    public String getPeriod() {
	return period;
    }

    public void setPeriod(String period) {
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

    public int getTotal_task_count() {
	return total_task_count;
    }

    public void setTotal_task_count(int total_task_count) {
	this.total_task_count = total_task_count;
    }

    public int getFinished_count() {
	return finished_count;
    }

    public void setFinished_count(int finished_count) {
	this.finished_count = finished_count;
    }

    public int getIn_progress_count() {
	return in_progress_count;
    }

    public void setIn_progress_count(int in_progress_count) {
	this.in_progress_count = in_progress_count;
    }

    public int getUndoing_count() {
	return undoing_count;
    }

    public void setUndoing_count(int undoing_count) {
	this.undoing_count = undoing_count;
    }

    public int getIn_progress_workload() {
	return in_progress_workload;
    }

    public void setIn_progress_workload(int in_progress_workload) {
	this.in_progress_workload = in_progress_workload;
    }

    @Override
    public String toString() {
	return "ZH_count_info [project_id=" + project_id + ", zh_group_id=" + zh_group_id + ", node_name=" + node_name + ", finish_rate="
	        + finish_rate + ", owner=" + owner + ", period=" + period + ", start_date=" + start_date + ", end_date=" + end_date
	        + ", total_task_count=" + total_task_count + ", finished_count=" + finished_count + ", in_progress_count=" + in_progress_count
	        + ", undoing_count=" + undoing_count + ", in_progress_workload=" + in_progress_workload + "]";
    }

}
