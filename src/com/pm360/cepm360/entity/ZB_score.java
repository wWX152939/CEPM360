package com.pm360.cepm360.entity;

/**
 * 评分
 * 
 * @author Andy
 * 
 */
public class ZB_score extends AttachCell {

    /**
     * 
     */
    private static final long serialVersionUID = -188135446734984143L;
    private int zb_score_id;
    private int zb_quote_id;// 报价ID
    private int zb_plan_id;// 招标计划ID
    private int zb_bid_company_id;// 投标单位ID
    private int review_export;// 评分专家
    private double tec_score;// 技术得分
    private double bus_score;// 商务得分
    private String mark;// 备注
    private String attachment;// 附件

    private String review_export_name;// 评审专家
    private int company_id;// 邀标单位
    private String company_name;// 邀标单位名称

    public int getZb_score_id() {
	return zb_score_id;
    }

    public void setZb_score_id(int zb_score_id) {
	this.zb_score_id = zb_score_id;
    }

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

    public int getReview_export() {
	return review_export;
    }

    public void setReview_export(int review_export) {
	this.review_export = review_export;
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

    public String getReview_export_name() {
	return review_export_name;
    }

    public void setReview_export_name(String review_export_name) {
	this.review_export_name = review_export_name;
    }

    public int getCompany_id() {
	return company_id;
    }

    public void setCompany_id(int company_id) {
	this.company_id = company_id;
    }

    public String getCompany_name() {
	return company_name;
    }

    public void setCompany_name(String company_name) {
	this.company_name = company_name;
    }

    @Override
    public String toString() {
	return "ZB_score [zb_score_id=" + zb_score_id + ", zb_quote_id=" + zb_quote_id + ", zb_plan_id=" + zb_plan_id + ", zb_bid_company_id="
	        + zb_bid_company_id + ", review_export=" + review_export + ", tec_score=" + tec_score + ", bus_score=" + bus_score + ", mark=" + mark
	        + ", attachment=" + attachment + ", review_export_name=" + review_export_name + ", company_id=" + company_id + ", company_name="
	        + company_name + "]";
    }

    @Override
    public int getId() {
	// TODO Auto-generated method stub
	return zb_score_id;
    }

}
