package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 采购计划单
 * 
 * @author Andy
 * 
 */
public class P_CGJHD implements MarkFlowDCell {

    /**
     * 
     */
    private static final long serialVersionUID = -4521972677713516969L;

    private int cgjhd_id;
    private int cgjh_id;
    private int cgys_id;
    private int wz_id;
    private int wz_type_1;
    private int wz_type_2;
    private double quantity;
    private double unit_price;
    private double money;
    private int lwdw_id;
    private Date indate;
    private int task_id;
    private String task_name;
    
    private String wz_name;
    private String wz_brand;
    private String wz_spec;
    private int wz_unit;
    private String wz_model_number;
    private String lwdw_name;
    
    private int IDU;

    public int getCgjhd_id() {
	return cgjhd_id;
    }

    public void setCgjhd_id(int cgjhd_id) {
	this.cgjhd_id = cgjhd_id;
    }

    public int getCgjh_id() {
	return cgjh_id;
    }

    public void setCgjh_id(int cgjh_id) {
	this.cgjh_id = cgjh_id;
    }

    public int getCgys_id() {
	return cgys_id;
    }

    public void setCgys_id(int cgys_id) {
	this.cgys_id = cgys_id;
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

    public double getQuantity() {
	return quantity;
    }

    public void setQuantity(double quantity) {
	this.quantity = quantity;
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

    public int getLwdw_id() {
	return lwdw_id;
    }

    public void setLwdw_id(int lwdw_id) {
	this.lwdw_id = lwdw_id;
    }

    public Date getIndate() {
	return indate;
    }

    public void setIndate(Date indate) {
	this.indate = indate;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    
    public int getIDU() {
        return IDU;
    }

    public void setIDU(int iDU) {
        IDU = iDU;
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

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    @Override
    public String toString() {
	return "P_CGJHD [cgjhd_id=" + cgjhd_id + ", cgjh_id=" + cgjh_id + ", cgys_id=" + cgys_id + ", wz_id=" + wz_id + ", wz_type_1=" + wz_type_1
	        + ", wz_type_2=" + wz_type_2 + ", quantity=" + quantity + ", unit_price=" + unit_price + ", money=" + money + ", lwdw_id=" + lwdw_id
	        + ", indate=" + indate + ", task_id=" + task_id + ", task_name=" + task_name + ", wz_name=" + wz_name + ", wz_brand=" + wz_brand
	        + ", wz_spec=" + wz_spec + ", wz_unit=" + wz_unit + ", wz_model_number=" + wz_model_number + ", lwdw_name=" + lwdw_name + ", IDU="
	        + IDU + "]";
    }

	@Override
	public int getId() {
		return cgjhd_id;
	}

}
