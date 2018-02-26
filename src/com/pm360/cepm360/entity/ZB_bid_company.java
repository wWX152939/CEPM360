package com.pm360.cepm360.entity;

/**
 * 投标单位
 * 
 * @author Andy
 * 
 */
public class ZB_bid_company extends AttachCell {

    /**
     * 
     */
    private static final long serialVersionUID = 788616174025826085L;
    private int zb_bid_company_id;
    private int zb_plan_id;
    private int zb_invite_id;// 邀标ID
    private int company_id;// 非邀标的单位
    private String company_name;// 邀标单位名称
    private int is_invite;// 是否邀标
    private int is_qualification;// 是否具备资质
    private String aptitude_desc;// 资质说明
    private String attachment;// 附件
    private String key_person;// 联系人
    private String tel;// 电话

    public int getZb_bid_company_id() {
	return zb_bid_company_id;
    }

    public void setZb_bid_company_id(int zb_bid_company_id) {
	this.zb_bid_company_id = zb_bid_company_id;
    }

    public int getZb_plan_id() {
	return zb_plan_id;
    }

    public void setZb_plan_id(int zb_plan_id) {
	this.zb_plan_id = zb_plan_id;
    }

    public int getZb_invite_id() {
	return zb_invite_id;
    }

    public void setZb_invite_id(int zb_invite_id) {
	this.zb_invite_id = zb_invite_id;
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

    public int getIs_invite() {
	return is_invite;
    }

    public void setIs_invite(int is_invite) {
	this.is_invite = is_invite;
    }

    public int getIs_qualification() {
	return is_qualification;
    }

    public void setIs_qualification(int is_qualification) {
	this.is_qualification = is_qualification;
    }

    public String getAptitude_desc() {
	return aptitude_desc;
    }

    public void setAptitude_desc(String aptitude_desc) {
	this.aptitude_desc = aptitude_desc;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    public String getKey_person() {
	return key_person;
    }

    public void setKey_person(String key_person) {
	this.key_person = key_person;
    }

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    @Override
    public String toString() {
	return "ZB_bid_company [zb_bid_company_id=" + zb_bid_company_id + ", zb_plan_id=" + zb_plan_id + ", zb_invite_id=" + zb_invite_id
	        + ", company_id=" + company_id + ", company_name=" + company_name + ", is_invite=" + is_invite + ", is_qualification="
	        + is_qualification + ", aptitude_desc=" + aptitude_desc + ", attachment=" + attachment + ", key_person=" + key_person + ", tel="
	        + tel + "]";
    }

    @Override
    public int getId() {
	return zb_bid_company_id;
    }

}
