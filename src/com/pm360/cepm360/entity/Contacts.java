package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 联系人
 * 
 * @author Andy
 *
 */
public class Contacts implements Serializable {

    private static final long serialVersionUID = -3464346483980853530L;
    private int contacts_id;
    private int company_id;
    private String name;
    private String tel;
    private String duty;
    private String department;
    private int tenant_id;

    public int getContacts_id() {
	return contacts_id;
    }

    public void setContacts_id(int contacts_id) {
	this.contacts_id = contacts_id;
    }

    public int getCompany_id() {
	return company_id;
    }

    public void setCompany_id(int company_id) {
	this.company_id = company_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    public String getDuty() {
	return duty;
    }

    public void setDuty(String duty) {
	this.duty = duty;
    }

    public String getDepartment() {
	return department;
    }

    public void setDepartment(String department) {
	this.department = department;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "Contacts [contacts_id=" + contacts_id + ", company_id=" + company_id + ", name=" + name + ", tel=" + tel + ", duty=" + duty
	        + ", department=" + department + ", tenant_id=" + tenant_id + "]";
    }

}
