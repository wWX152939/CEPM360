package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 报价
 * 
 * @author Andy
 * 
 */
public class ZB_quote extends AttachCell {

    /**
     * 
     */
    private static final long serialVersionUID = -8746717687010425937L;
    private int zb_quote_id;
    private int zb_plan_id;// 招标计划ID
    private int zb_bid_company_id;// 投标单位ID
    private String bid_dep;// 投标部门
    private String bid_person;// 投标负责人
    private String tel;// 电话
    private String address;// 地址
    private Date quote_date;// 报价日期
    private Date bid_date;// 投标日期
    private int project_period;// 工程周期
    private double bid_price;// 投标价格
    private String unit;// 单位
    private String mark;// 备注
    private String attachment;// 附件
   
    private String company_name;// 邀标单位名称

    public int getZb_quote_id() {
	return zb_quote_id;
    }

    public void setZb_quote_id(int zb_quote_id) {
	this.zb_quote_id = zb_quote_id;
    }

    public int getZb_plan_id() {
	return zb_plan_id;
    }

    public void setZb_plan_id(int zb_plan_id) {
	this.zb_plan_id = zb_plan_id;
    }

    public int getZb_bid_company_id() {
	return zb_bid_company_id;
    }

    public void setZb_bid_company_id(int zb_bid_company_id) {
	this.zb_bid_company_id = zb_bid_company_id;
    }

    public String getBid_dep() {
	return bid_dep;
    }

    public void setBid_dep(String bid_dep) {
	this.bid_dep = bid_dep;
    }

    public String getBid_person() {
	return bid_person;
    }

    public void setBid_person(String bid_person) {
	this.bid_person = bid_person;
    }

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public Date getQuote_date() {
	return quote_date;
    }

    public void setQuote_date(Date quote_date) {
	this.quote_date = quote_date;
    }

    public Date getBid_date() {
	return bid_date;
    }

    public void setBid_date(Date bid_date) {
	this.bid_date = bid_date;
    }

    public int getProject_period() {
	return project_period;
    }

    public void setProject_period(int project_period) {
	this.project_period = project_period;
    }

    public double getBid_price() {
	return bid_price;
    }

    public void setBid_price(double bid_price) {
	this.bid_price = bid_price;
    }

    public String getUnit() {
	return unit;
    }

    public void setUnit(String unit) {
	this.unit = unit;
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

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    @Override
    public String toString() {
	return "ZB_quote [zb_quote_id=" + zb_quote_id + ", zb_plan_id=" + zb_plan_id + ", zb_bid_company_id=" + zb_bid_company_id + ", bid_dep="
	        + bid_dep + ", bid_person=" + bid_person + ", tel=" + tel + ", address=" + address + ", quote_date=" + quote_date + ", bid_date="
	        + bid_date + ", project_period=" + project_period + ", bid_price=" + bid_price + ", unit=" + unit + ", mark=" + mark
	        + ", attachment=" + attachment + ", company_name=" + company_name + "]";
    }

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return zb_quote_id;
	}

}
