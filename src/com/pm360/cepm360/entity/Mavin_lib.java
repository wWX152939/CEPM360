package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 专家库
 * 
 * @author Andy
 * 
 */
public class Mavin_lib implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -293069309717942714L;
    private int mavin_lib_id;
    private String name;// 姓名
    private int sex;// 性别
    private Date birthday;// 出生日期
    private Date work_start;// 工作时间
    private String work_company;// 工作单位
    private int type;// 专家类别
    private String tel;// 电话
    private String title;// 职称
    private String major;// 专业
    private String degree;// 文化程度
    private String mark;// 备注
    private String attachment;// 附件
    private int tenant_id;

    public int getMavin_lib_id() {
	return mavin_lib_id;
    }

    public void setMavin_lib_id(int mavin_lib_id) {
	this.mavin_lib_id = mavin_lib_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getSex() {
	return sex;
    }

    public void setSex(int sex) {
	this.sex = sex;
    }

    public Date getBirthday() {
	return birthday;
    }

    public void setBirthday(Date birthday) {
	this.birthday = birthday;
    }

    public Date getWork_start() {
	return work_start;
    }

    public void setWork_start(Date work_start) {
	this.work_start = work_start;
    }

    public String getWork_company() {
	return work_company;
    }

    public void setWork_company(String work_company) {
	this.work_company = work_company;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getMajor() {
	return major;
    }

    public void setMajor(String major) {
	this.major = major;
    }

    public String getDegree() {
	return degree;
    }

    public void setDegree(String degree) {
	this.degree = degree;
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
	return "Mavin_lib [mavin_lib_id=" + mavin_lib_id + ", name=" + name + ", sex=" + sex + ", birthday=" + birthday + ", work_start="
	        + work_start + ", work_company=" + work_company + ", type=" + type + ", tel=" + tel + ", title=" + title + ", major=" + major
	        + ", degree=" + degree + ", mark=" + mark + ", attachment=" + attachment + ", tenant_id=" + tenant_id + "]";
    }

}
