package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 投资估算
 * 
 * @author Andy
 * 
 */
public class InvestmentEstimate implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2124571676639630559L;
    private int investment_estimate_id;
    private int project_id;//
    private String estimate_number;// 估算编号
    private String estimate_name;// 估算名称
    private String estimate_period;// 估算年度
    private double estimate_money;// 估算额
    private int item;// 项
    private int status;// 状态
    private String mark;// 备注
    private int creater;// 创建人
    private Date create_time;// 创建时间
    private int tenant_id;

    public int getInvestment_estimate_id() {
	return investment_estimate_id;
    }

    public void setInvestment_estimate_id(int investment_estimate_id) {
	this.investment_estimate_id = investment_estimate_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public String getEstimate_number() {
	return estimate_number;
    }

    public void setEstimate_number(String estimate_number) {
	this.estimate_number = estimate_number;
    }

    public String getEstimate_name() {
	return estimate_name;
    }

    public void setEstimate_name(String estimate_name) {
	this.estimate_name = estimate_name;
    }

    public String getEstimate_period() {
	return estimate_period;
    }

    public void setEstimate_period(String estimate_period) {
	this.estimate_period = estimate_period;
    }

    public double getEstimate_money() {
	return estimate_money;
    }

    public void setEstimate_money(double estimate_money) {
	this.estimate_money = estimate_money;
    }

    public int getItem() {
	return item;
    }

    public void setItem(int item) {
	this.item = item;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
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

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "InvestmentEstimate [investment_estimate_id=" + investment_estimate_id + ", project_id=" + project_id + ", estimate_number="
	        + estimate_number + ", estimate_name=" + estimate_name + ", estimate_period=" + estimate_period + ", estimate_money="
	        + estimate_money + ", item=" + item + ", status=" + status + ", mark=" + mark + ", creater=" + creater + ", create_time="
	        + create_time + ", tenant_id=" + tenant_id + "]";
    }

}
