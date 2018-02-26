package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 采购
 * 
 * @author Andy
 * 
 */
public class P_CG implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3584753244646214722L;

    private int cg_id;
    private String cg_number;
    private int cgjh_id;
    private int cght_id;
    private String cgjh_name;
    private String cght_name;
    private double cg_money;
    private String storehouse;
    private int storeman;
    private int cg_status;
    private int project_id;
    private int tenant_id;
    private Date create_date;
    private int execute_person;

    public int getCg_id() {
	return cg_id;
    }

    public void setCg_id(int cg_id) {
	this.cg_id = cg_id;
    }

    public String getCg_number() {
	return cg_number;
    }

    public void setCg_number(String cg_number) {
	this.cg_number = cg_number;
    }

    public int getCgjh_id() {
	return cgjh_id;
    }

    public void setCgjh_id(int cgjh_id) {
	this.cgjh_id = cgjh_id;
    }

    public int getCght_id() {
	return cght_id;
    }

    public void setCght_id(int cght_id) {
	this.cght_id = cght_id;
    }

    public String getCgjh_name() {
	return cgjh_name;
    }

    public void setCgjh_name(String cgjh_name) {
	this.cgjh_name = cgjh_name;
    }

    public String getCght_name() {
	return cght_name;
    }

    public void setCght_name(String cght_name) {
	this.cght_name = cght_name;
    }

    public double getCg_money() {
	return cg_money;
    }

    public void setCg_money(double cg_money) {
	this.cg_money = cg_money;
    }

    public String getStorehouse() {
	return storehouse;
    }

    public void setStorehouse(String storehouse) {
	this.storehouse = storehouse;
    }

    public int getStoreman() {
	return storeman;
    }

    public void setStoreman(int storeman) {
	this.storeman = storeman;
    }

    public int getCg_status() {
	return cg_status;
    }

    public void setCg_status(int cg_status) {
	this.cg_status = cg_status;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public Date getCreate_date() {
	return create_date;
    }

    public void setCreate_date(Date create_date) {
	this.create_date = create_date;
    }

    public int getExecute_person() {
        return execute_person;
    }

    public void setExecute_person(int execute_person) {
        this.execute_person = execute_person;
    }

    @Override
    public String toString() {
	return "P_CG [cg_id=" + cg_id + ", cg_number=" + cg_number + ", cgjh_id=" + cgjh_id + ", cght_id=" + cght_id + ", cgjh_name=" + cgjh_name
	        + ", cght_name=" + cght_name + ", cg_money=" + cg_money + ", storehouse=" + storehouse + ", storeman=" + storeman + ", cg_status="
	        + cg_status + ", project_id=" + project_id + ", tenant_id=" + tenant_id + ", create_date=" + create_date + ", execute_person="
	        + execute_person + "]";
    }

}
