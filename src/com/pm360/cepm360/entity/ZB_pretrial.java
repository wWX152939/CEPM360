package com.pm360.cepm360.entity;

/**
 * 预审
 * 
 * @author Andy
 * 
 */
public class ZB_pretrial extends AttachCell {

    /**
     * 
     */
    private static final long serialVersionUID = -8011883405758835633L;
    private int zb_pretrial_id;
    private int zb_plan_id;
    private int zb_bid_company_id;
    private int review_export;// 评审专家
    private String review_export_name;// 评审专家
    private int auditing_status;// 审核状态
    private String auditing_content;// 预审文件主要内容
    private String attachment;// 附件
    private int company_id;// 邀标单位
    private String company_name;// 邀标单位名称

    public int getZb_pretrial_id() {
	return zb_pretrial_id;
    }

    public void setZb_pretrial_id(int zb_pretrial_id) {
	this.zb_pretrial_id = zb_pretrial_id;
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

    public String getReview_export_name() {
	return review_export_name;
    }

    public void setReview_export_name(String review_export_name) {
	this.review_export_name = review_export_name;
    }

    public int getAuditing_status() {
	return auditing_status;
    }

    public void setAuditing_status(int auditing_status) {
	this.auditing_status = auditing_status;
    }

    public String getAuditing_content() {
	return auditing_content;
    }

    public void setAuditing_content(String auditing_content) {
	this.auditing_content = auditing_content;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
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
	return "ZB_pretrial [zb_pretrial_id=" + zb_pretrial_id + ", zb_plan_id=" + zb_plan_id + ", zb_bid_company_id=" + zb_bid_company_id
	        + ", review_export=" + review_export + ", review_export_name=" + review_export_name + ", auditing_status=" + auditing_status
	        + ", auditing_content=" + auditing_content + ", attachment=" + attachment + ", company_id=" + company_id + ", company_name="
	        + company_name + "]";
    }

    @Override
    public int getId() {
	return zb_pretrial_id;
    }

}
