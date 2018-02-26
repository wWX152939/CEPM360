package com.pm360.cepm360.entity;

/**
 * 邀标单位
 * 
 * @author Andy
 * 
 */
public class ZB_invite extends AttachCell {

    /**
     * 
     */
    private static final long serialVersionUID = -2540227773551772561L;
    private int zb_invite_id;
    private int zb_plan_id;//
    private int company_id;// 邀标单位
    private String company_name;// 邀标单位名称
    private String key_person;// 联系人
    private String tel;// 联系电话
    private String aptitude_desc;// 资质说明
    private String attachment;// 附件

    public int getZb_invite_id() {
	return zb_invite_id;
    }

    public void setZb_invite_id(int zb_invite_id) {
	this.zb_invite_id = zb_invite_id;
    }

    public int getZb_plan_id() {
	return zb_plan_id;
    }

    public void setZb_plan_id(int zb_plan_id) {
	this.zb_plan_id = zb_plan_id;
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

    @Override
    public String toString() {
	return "ZB_invite [zb_invite_id=" + zb_invite_id + ", zb_plan_id=" + zb_plan_id + ", company_id=" + company_id + ", company_name="
	        + company_name + ", key_person=" + key_person + ", tel=" + tel + ", aptitude_desc=" + aptitude_desc + ", attachment=" + attachment
	        + "]";
    }

    @Override
    public int getId() {
        return zb_invite_id;
    }
}
