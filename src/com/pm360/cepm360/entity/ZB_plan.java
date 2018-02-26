package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 招标策划
 * 
 * @author Andy
 * 
 */
public class ZB_plan extends AttachCell implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1213869769055931566L;

    private int zb_plan_id;
    private int project_id;
    private String number;// 招标编号
    private String name;// 招标名称
    private int type;// 招标类型
    private int secrecy;// 概算保密
    private int mode;// 招标方式
    private int operator;// 负责人
    private int department;// 招标部门
    private String license_number;// 批准文号
    private int status;// 招标状态
    private double price1;// 概算价
    private double price2;// 概算价
    private Date plan_start;// 计划启动
    private Date plan_end;// 计划结束
    private Date open_start;// 开标时间
    private Date open_end;// 开标结束
    private Date evaluation_start;// 评标时间
    private Date evaluation_end;// 评标结束
    private double tec_score;// 技术标权重
    private double bus_score;// 商务标权重
    private String pre_condition;// 前置条件
    private String mark;// 备注
    private String attachment;// 招标文件 file_id 1,2组成
    private int tenant_id;

    public int getZb_plan_id() {
	return zb_plan_id;
    }

    public void setZb_plan_id(int zb_plan_id) {
	this.zb_plan_id = zb_plan_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public String getNumber() {
	return number;
    }

    public void setNumber(String number) {
	this.number = number;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getSecrecy() {
	return secrecy;
    }

    public void setSecrecy(int secrecy) {
	this.secrecy = secrecy;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public int getOperator() {
	return operator;
    }

    public void setOperator(int operator) {
	this.operator = operator;
    }

    public int getDepartment() {
	return department;
    }

    public void setDepartment(int department) {
	this.department = department;
    }

    public String getLicense_number() {
	return license_number;
    }

    public void setLicense_number(String license_number) {
	this.license_number = license_number;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public double getPrice1() {
	return price1;
    }

    public void setPrice1(double price1) {
	this.price1 = price1;
    }

    public double getPrice2() {
	return price2;
    }

    public void setPrice2(double price2) {
	this.price2 = price2;
    }

    public Date getPlan_start() {
	return plan_start;
    }

    public void setPlan_start(Date plan_start) {
	this.plan_start = plan_start;
    }

    public Date getPlan_end() {
	return plan_end;
    }

    public void setPlan_end(Date plan_end) {
	this.plan_end = plan_end;
    }

    public Date getOpen_start() {
	return open_start;
    }

    public void setOpen_start(Date open_start) {
	this.open_start = open_start;
    }

    public Date getOpen_end() {
	return open_end;
    }

    public void setOpen_end(Date open_end) {
	this.open_end = open_end;
    }

    public Date getEvaluation_start() {
	return evaluation_start;
    }

    public void setEvaluation_start(Date evaluation_start) {
	this.evaluation_start = evaluation_start;
    }

    public Date getEvaluation_end() {
	return evaluation_end;
    }

    public void setEvaluation_end(Date evaluation_end) {
	this.evaluation_end = evaluation_end;
    }

    public double getTec_score() {
	return tec_score;
    }

    public void setTec_score(double tec_score) {
	this.tec_score = tec_score;
    }

    public double getBus_score() {
	return bus_score;
    }

    public void setBus_score(double bus_score) {
	this.bus_score = bus_score;
    }

    public String getPre_condition() {
	return pre_condition;
    }

    public void setPre_condition(String pre_condition) {
	this.pre_condition = pre_condition;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "ZB_plan [zb_plan_id=" + zb_plan_id + ", project_id=" + project_id + ", number=" + number + ", name=" + name + ", type=" + type
	        + ", secrecy=" + secrecy + ", mode=" + mode + ", operator=" + operator + ", department=" + department + ", license_number="
	        + license_number + ", status=" + status + ", price1=" + price1 + ", price2=" + price2 + ", plan_start=" + plan_start + ", plan_end="
	        + plan_end + ", open_start=" + open_start + ", open_end=" + open_end + ", evaluation_start=" + evaluation_start + ", evaluation_end="
	        + evaluation_end + ", tec_score=" + tec_score + ", bus_score=" + bus_score + ", pre_condition=" + pre_condition + ", mark=" + mark
	        + ", attachment=" + attachment + ", tenant_id=" + tenant_id + "]";
    }

	@Override
	public int getId() {
		return zb_plan_id;
	}

}
