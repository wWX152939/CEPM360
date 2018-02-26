package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 出库单
 * 
 * @author Andy
 * 
 */
public class P_CKD implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2305034965808581524L;

    private int ckd_id;
    private int rk_id;
    private int wz_id;
    private int wz_type_1;
    private int wz_type_2;
    private double out_quantity;
    private double store_quantity;

    private String wz_name;
    private String wz_brand;
    private String wz_spec;
    private int wz_unit;
    private String wz_model_number;
    
    private int IDU;

    public int getCkd_id() {
	return ckd_id;
    }

    public void setCkd_id(int ckd_id) {
	this.ckd_id = ckd_id;
    }

    public int getRk_id() {
	return rk_id;
    }

    public void setRk_id(int rk_id) {
	this.rk_id = rk_id;
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

    public double getOut_quantity() {
	return out_quantity;
    }

    public void setOut_quantity(double out_quantity) {
	this.out_quantity = out_quantity;
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

    public int getIDU() {
        return IDU;
    }

    public void setIDU(int iDU) {
        IDU = iDU;
    }

    @Override
    public String toString() {
	return "P_CKD [ckd_id=" + ckd_id + ", rk_id=" + rk_id + ", wz_id=" + wz_id + ", wz_type_1=" + wz_type_1 + ", wz_type_2=" + wz_type_2
	        + ", out_quantity=" + out_quantity + ", wz_name=" + wz_name + ", wz_brand=" + wz_brand + ", wz_spec=" + wz_spec + ", wz_unit="
	        + wz_unit + ", wz_model_number=" + wz_model_number + ", IDU=" + IDU + "]";
    }

    public double getStore_quantity() {
        return store_quantity;
    }

    public void setStore_quantity(double store_quantity) {
        this.store_quantity = store_quantity;
    }

}
