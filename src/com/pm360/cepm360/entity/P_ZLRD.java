package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 租赁-租入单
 * 
 * @author Andy
 * 
 */
public class P_ZLRD implements MarkFlowDCell {

    /**
     * 
     */
    private static final long serialVersionUID = 4615465539760898058L;
    private int zlrd_id;
    private int zlr_id;
    private int zl_id;
    private double number;// 数量
    private double remainder_number;// 剩余数量
    private double rent;// 日租金
    private Date lease_date;// 租赁日期
    private Date end_date;// 结束日期
    private double sum;// 金额
    private int tenant_id;

    private String project_name;// 项目名字
    private String zl_name;// 租赁资源名称
    private int zl_type;// 租赁资源类型
    private String zl_spec;// 租赁资源规格
    private int zl_unit;// 租赁资源单位

    private String zlr_number;// 租入单号
    private int operator;// 经办人
    private int zl_company;// 租赁单位ID
    private String zl_company_name;// 租赁单位名称

    private int IDU;

    public int getZlrd_id() {
	return zlrd_id;
    }

    public void setZlrd_id(int zlrd_id) {
	this.zlrd_id = zlrd_id;
    }

    public int getZlr_id() {
	return zlr_id;
    }

    public void setZlr_id(int zlr_id) {
	this.zlr_id = zlr_id;
    }

    public int getZl_id() {
	return zl_id;
    }

    public void setZl_id(int zl_id) {
	this.zl_id = zl_id;
    }

    public double getNumber() {
	return number;
    }

    public void setNumber(double number) {
	this.number = number;
    }

    public double getRemainder_number() {
	return remainder_number;
    }

    public void setRemainder_number(double remainder_number) {
	this.remainder_number = remainder_number;
    }

    public double getRent() {
	return rent;
    }

    public void setRent(double rent) {
	this.rent = rent;
    }

    public Date getLease_date() {
	return lease_date;
    }

    public void setLease_date(Date lease_date) {
	this.lease_date = lease_date;
    }

    public Date getEnd_date() {
	return end_date;
    }

    public void setEnd_date(Date end_date) {
	this.end_date = end_date;
    }

    public double getSum() {
	return sum;
    }

    public void setSum(double sum) {
	this.sum = sum;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public String getProject_name() {
	return project_name;
    }

    public void setProject_name(String project_name) {
	this.project_name = project_name;
    }

    public String getZl_name() {
	return zl_name;
    }

    public void setZl_name(String zl_name) {
	this.zl_name = zl_name;
    }

    public int getZl_type() {
	return zl_type;
    }

    public void setZl_type(int zl_type) {
	this.zl_type = zl_type;
    }

    public String getZl_spec() {
	return zl_spec;
    }

    public void setZl_spec(String zl_spec) {
	this.zl_spec = zl_spec;
    }

    public int getZl_unit() {
	return zl_unit;
    }

    public void setZl_unit(int zl_unit) {
	this.zl_unit = zl_unit;
    }

    public String getZlr_number() {
	return zlr_number;
    }

    public void setZlr_number(String zlr_number) {
	this.zlr_number = zlr_number;
    }

    public int getOperator() {
	return operator;
    }

    public void setOperator(int operator) {
	this.operator = operator;
    }

    public int getZl_company() {
	return zl_company;
    }

    public void setZl_company(int zl_company) {
	this.zl_company = zl_company;
    }

    public String getZl_company_name() {
	return zl_company_name;
    }

    public void setZl_company_name(String zl_company_name) {
	this.zl_company_name = zl_company_name;
    }

    public int getIDU() {
	return IDU;
    }

    public void setIDU(int iDU) {
	IDU = iDU;
    }

    @Override
    public String toString() {
	return "P_ZLRD [zlrd_id=" + zlrd_id + ", zlr_id=" + zlr_id + ", zl_id=" + zl_id + ", number=" + number + ", remainder_number="
	        + remainder_number + ", rent=" + rent + ", lease_date=" + lease_date + ", end_date=" + end_date + ", sum=" + sum + ", tenant_id="
	        + tenant_id + ", project_name=" + project_name + ", zl_name=" + zl_name + ", zl_type=" + zl_type + ", zl_spec=" + zl_spec
	        + ", zl_unit=" + zl_unit + ", zlr_number=" + zlr_number + ", operator=" + operator + ", zl_company=" + zl_company
	        + ", zl_company_name=" + zl_company_name + ", IDU=" + IDU + "]";
    }

	@Override
	public int getId() {
		return zlrd_id;
	}

	@Override
	public double getMoney() {
		return sum;
	}

}
