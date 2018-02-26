package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 采购单
 * 
 * @author Andy
 * 
 */
public class P_CGD implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9026114901732281238L;

    private int cgd_id;
    private int cg_id;
    private int cgjh_id;
    private String cgjh_name;
    private int cgjhd_id;
    private String cg_number;
    private Date arrival_date;
    private int cgd_status;

    private int wz_id;
    private int wz_type_1;
    private int wz_type_2;
    private String wz_name;
    private String wz_brand;
    private String wz_spec;
    private int wz_unit;
    private String wz_model_number;
    private String lwdw_name;
    private double cg_quantity;

    private int task_id;
    private int project_id;
    private int tenant_id;

    private double unit_price;
    private double money;

    private String storehouse;
    private int storeman;

    private int IDU;

    public int getCgd_id() {
	return cgd_id;
    }

    public void setCgd_id(int cgd_id) {
	this.cgd_id = cgd_id;
    }

    public int getCg_id() {
	return cg_id;
    }

    public void setCg_id(int cg_id) {
	this.cg_id = cg_id;
    }

    public int getCgjh_id() {
	return cgjh_id;
    }

    public void setCgjh_id(int cgjh_id) {
	this.cgjh_id = cgjh_id;
    }

    public String getCgjh_name() {
	return cgjh_name;
    }

    public void setCgjh_name(String cgjh_name) {
	this.cgjh_name = cgjh_name;
    }

    public int getCgjhd_id() {
	return cgjhd_id;
    }

    public void setCgjhd_id(int cgjhd_id) {
	this.cgjhd_id = cgjhd_id;
    }

    public String getCg_number() {
	return cg_number;
    }

    public void setCg_number(String cg_number) {
	this.cg_number = cg_number;
    }

    public Date getArrival_date() {
	return arrival_date;
    }

    public void setArrival_date(Date arrival_date) {
	this.arrival_date = arrival_date;
    }

    public int getCgd_status() {
	return cgd_status;
    }

    public void setCgd_status(int cgd_status) {
	this.cgd_status = cgd_status;
    }

    public int getWz_id() {
	return wz_id;
    }

    public void setWz_id(int wz_id) {
	this.wz_id = wz_id;
    }

    public int getWz_type_1() {
	return wz_type_1;
    }

    public void setWz_type_1(int wz_type_1) {
	this.wz_type_1 = wz_type_1;
    }

    public int getWz_type_2() {
	return wz_type_2;
    }

    public void setWz_type_2(int wz_type_2) {
	this.wz_type_2 = wz_type_2;
    }

    public String getWz_name() {
	return wz_name;
    }

    public void setWz_name(String wz_name) {
	this.wz_name = wz_name;
    }

    public String getWz_brand() {
	return wz_brand;
    }

    public void setWz_brand(String wz_brand) {
	this.wz_brand = wz_brand;
    }

    public String getWz_spec() {
	return wz_spec;
    }

    public void setWz_spec(String wz_spec) {
	this.wz_spec = wz_spec;
    }

    public int getWz_unit() {
	return wz_unit;
    }

    public void setWz_unit(int wz_unit) {
	this.wz_unit = wz_unit;
    }

    public String getWz_model_number() {
	return wz_model_number;
    }

    public void setWz_model_number(String wz_model_number) {
	this.wz_model_number = wz_model_number;
    }

    public String getLwdw_name() {
	return lwdw_name;
    }

    public void setLwdw_name(String lwdw_name) {
	this.lwdw_name = lwdw_name;
    }

    public double getCg_quantity() {
	return cg_quantity;
    }

    public void setCg_quantity(double cg_quantity) {
	this.cg_quantity = cg_quantity;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
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

    public double getUnit_price() {
	return unit_price;
    }

    public void setUnit_price(double unit_price) {
	this.unit_price = unit_price;
    }

    public double getMoney() {
	return money;
    }

    public void setMoney(double money) {
	this.money = money;
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

    public int getIDU() {
	return IDU;
    }

    public void setIDU(int iDU) {
	IDU = iDU;
    }

    @Override
    public String toString() {
	return "P_CGD [cgd_id=" + cgd_id + ", cg_id=" + cg_id + ", cgjh_id=" + cgjh_id + ", cgjh_name=" + cgjh_name + ", cgjhd_id=" + cgjhd_id
	        + ", cg_number=" + cg_number + ", arrival_date=" + arrival_date + ", cgd_status=" + cgd_status + ", wz_id=" + wz_id + ", wz_type_1="
	        + wz_type_1 + ", wz_type_2=" + wz_type_2 + ", wz_name=" + wz_name + ", wz_brand=" + wz_brand + ", wz_spec=" + wz_spec + ", wz_unit="
	        + wz_unit + ", wz_model_number=" + wz_model_number + ", lwdw_name=" + lwdw_name + ", cg_quantity=" + cg_quantity + ", task_id="
	        + task_id + ", project_id=" + project_id + ", tenant_id=" + tenant_id + ", unit_price=" + unit_price + ", money=" + money
	        + ", storehouse=" + storehouse + ", storeman=" + storeman + ", IDU=" + IDU + "]";
    }

}
