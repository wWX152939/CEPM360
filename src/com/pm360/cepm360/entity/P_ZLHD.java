package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 租赁-还租单
 * 
 * @author Andy
 * 
 */
public class P_ZLHD implements MarkFlowDCell {

    /**
     * 
     */
    private static final long serialVersionUID = 5467281095798173235L;
    private int zlhd_id;
    private int zlh_id;// 还租单ID
    private int zl_id;// 租赁资源ID
    private String zlr_number;// 租入单号
    private int zlr_id;// 租入ID
    private int zlrd_id;// 租入单据ID
    private double number;// 租还数量
    private int zl_company;// 租赁单位
    private String zl_company_name;// 租赁单位名称
    private int hz_person;// 还租人
    private Date hz_date;// 还租时间
    private int tenant_id;

    private String zl_name;// 租赁资源名称
    private int zl_type;// 租赁资源类型
    private String zl_spec;// 租赁资源规格
    private int zl_unit;// 租赁资源单位

    private int IDU;

    private double primeval_number;// 上一次还租数量（如果基于上次的还租数量做修改，则用到此临时值）

    public int getZlhd_id() {
	return zlhd_id;
    }

    public void setZlhd_id(int zlhd_id) {
	this.zlhd_id = zlhd_id;
    }

    public int getZlh_id() {
	return zlh_id;
    }

    public void setZlh_id(int zlh_id) {
	this.zlh_id = zlh_id;
    }

    public int getZl_id() {
	return zl_id;
    }

    public void setZl_id(int zl_id) {
	this.zl_id = zl_id;
    }

    public String getZlr_number() {
	return zlr_number;
    }

    public void setZlr_number(String zlr_number) {
	this.zlr_number = zlr_number;
    }

    public int getZlr_id() {
	return zlr_id;
    }

    public void setZlr_id(int zlr_id) {
	this.zlr_id = zlr_id;
    }

    public int getZlrd_id() {
	return zlrd_id;
    }

    public void setZlrd_id(int zlrd_id) {
	this.zlrd_id = zlrd_id;
    }

    public double getNumber() {
	return number;
    }

    public void setNumber(double number) {
	this.number = number;
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

    public int getHz_person() {
	return hz_person;
    }

    public void setHz_person(int hz_person) {
	this.hz_person = hz_person;
    }

    public Date getHz_date() {
	return hz_date;
    }

    public void setHz_date(Date hz_date) {
	this.hz_date = hz_date;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
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

    public int getIDU() {
	return IDU;
    }

    public void setIDU(int iDU) {
	IDU = iDU;
    }

    public double getPrimeval_number() {
	return primeval_number;
    }

    public void setPrimeval_number(double primeval_number) {
	this.primeval_number = primeval_number;
    }

    @Override
    public String toString() {
	return "P_ZLHD [zlhd_id=" + zlhd_id + ", zlh_id=" + zlh_id + ", zl_id=" + zl_id + ", zlr_number=" + zlr_number + ", zlr_id=" + zlr_id
	        + ", zlrd_id=" + zlrd_id + ", number=" + number + ", zl_company=" + zl_company + ", zl_company_name=" + zl_company_name
	        + ", hz_person=" + hz_person + ", hz_date=" + hz_date + ", tenant_id=" + tenant_id + ", zl_name=" + zl_name + ", zl_type=" + zl_type
	        + ", zl_spec=" + zl_spec + ", zl_unit=" + zl_unit + ", IDU=" + IDU + ", primeval_number=" + primeval_number + "]";
    }

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return zlhd_id;
	}

	@Override
	public double getMoney() {
		// TODO Auto-generated method stub
		return 0;
	}
}
