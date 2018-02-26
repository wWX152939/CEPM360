package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 澄清答疑
 * 
 * @author Andy
 * 
 */
public class ZB_answer extends AttachCell {

    /**
     * 
     */
    private static final long serialVersionUID = 6823762639219628357L;
    private int zb_answer_id;
    private int zb_plan_id;// 招标计划
    private int zb_pretrial_id;// 预审ID
    private int zb_bid_company_id;// 投标单位ID
    private Date answer_time;// 答疑时间
    private String answer_address;// 答疑地点
    private String partake_person;// 参与人员
    private String answer_content;// 答疑纪要
    private String other;// 其他
    private String attachment;// 附件
    
    private String company_name;// 邀标单位名称

    public int getZb_answer_id() {
	return zb_answer_id;
    }

    public void setZb_answer_id(int zb_answer_id) {
	this.zb_answer_id = zb_answer_id;
    }

    public int getZb_plan_id() {
	return zb_plan_id;
    }

    public void setZb_plan_id(int zb_plan_id) {
	this.zb_plan_id = zb_plan_id;
    }

    public int getZb_pretrial_id() {
	return zb_pretrial_id;
    }

    public void setZb_pretrial_id(int zb_pretrial_id) {
	this.zb_pretrial_id = zb_pretrial_id;
    }

    public int getZb_bid_company_id() {
	return zb_bid_company_id;
    }

    public void setZb_bid_company_id(int zb_bid_company_id) {
	this.zb_bid_company_id = zb_bid_company_id;
    }

    public Date getAnswer_time() {
	return answer_time;
    }

    public void setAnswer_time(Date answer_time) {
	this.answer_time = answer_time;
    }

    public String getAnswer_address() {
	return answer_address;
    }

    public void setAnswer_address(String answer_address) {
	this.answer_address = answer_address;
    }

    public String getPartake_person() {
	return partake_person;
    }

    public void setPartake_person(String partake_person) {
	this.partake_person = partake_person;
    }

    public String getAnswer_content() {
	return answer_content;
    }

    public void setAnswer_content(String answer_content) {
	this.answer_content = answer_content;
    }

    public String getOther() {
	return other;
    }

    public void setOther(String other) {
	this.other = other;
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
	return "ZB_answer [zb_answer_id=" + zb_answer_id + ", zb_plan_id=" + zb_plan_id + ", zb_pretrial_id=" + zb_pretrial_id
	        + ", zb_bid_company_id=" + zb_bid_company_id + ", answer_time=" + answer_time + ", answer_address=" + answer_address
	        + ", partake_person=" + partake_person + ", answer_content=" + answer_content + ", other=" + other + ", attachment=" + attachment
	        + ", company_name=" + company_name + "]";
    }

	@Override
	public int getId() {
		return zb_answer_id;
	}

}
