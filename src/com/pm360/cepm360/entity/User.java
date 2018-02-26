package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable, Cloneable {
    private static final long serialVersionUID = -5485985188749162339L;
    private int user_id;
    private int tenant_id;
    private String login_name;
    private String pwd;
    private String name;
    private String sex;
    private Date birth;
    private String tel;
    private String mobile;
    private String address;
    private String office_phone;
    private String email;
    private String email_pwd;
    private String role;
    private String prole;
    private String action;
    private String permission;
    private int obs_id;
    private String obs_name;
    private Date create_time;
    
    private int type;//公司类型

    public Object clone() {
	Object o = null;
	try {
	    o = super.clone();
	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	}
	return o;
    }

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public String getLogin_name() {
	return login_name;
    }

    public void setLogin_name(String login_name) {
	this.login_name = login_name;
    }

    public String getPwd() {
	return pwd;
    }

    public void setPwd(String pwd) {
	this.pwd = pwd;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSex() {
	return sex;
    }

    public void setSex(String sex) {
	this.sex = sex;
    }

    public Date getBirth() {
	return birth;
    }

    public void setBirth(Date birth) {
	this.birth = birth;
    }

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    public String getMobile() {
	return mobile;
    }

    public void setMobile(String mobile) {
	this.mobile = mobile;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public String getOffice_phone() {
	return office_phone;
    }

    public void setOffice_phone(String office_phone) {
	this.office_phone = office_phone;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getEmail_pwd() {
	return email_pwd;
    }

    public void setEmail_pwd(String email_pwd) {
	this.email_pwd = email_pwd;
    }

    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public String getProle() {
	return prole;
    }

    public void setProle(String prole) {
	this.prole = prole;
    }

    public String getAction() {
	return action;
    }

    public void setAction(String action) {
	this.action = action;
    }

    public String getPermission() {
	return permission;
    }

    public void setPermission(String permission) {
	this.permission = permission;
    }

    public int getObs_id() {
	return obs_id;
    }

    public void setObs_id(int obs_id) {
	this.obs_id = obs_id;
    }

    public Date getCreate_time() {
	return create_time;
    }

    public void setCreate_time(Date create_time) {
	this.create_time = create_time;
    }

    public String getObs_name() {
	return obs_name;
    }

    public void setObs_name(String obs_name) {
	this.obs_name = obs_name;
    }

    public int getTenant_type() {
        return type;
    }

    public void setTenant_type(int tenant_type) {
        this.type = tenant_type;
    }

    @Override
    public String toString() {
	return "User [user_id=" + user_id + ", tenant_id=" + tenant_id + ", login_name=" + login_name + ", pwd=" + pwd + ", name=" + name + ", sex="
	        + sex + ", birth=" + birth + ", tel=" + tel + ", mobile=" + mobile + ", address=" + address + ", office_phone=" + office_phone
	        + ", email=" + email + ", email_pwd=" + email_pwd + ", role=" + role + ", prole=" + prole + ", action=" + action + ", permission="
	        + permission + ", obs_id=" + obs_id + ", obs_name=" + obs_name + ", create_time=" + create_time + ", tenant_type=" + type
	        + "]";
    }

}
