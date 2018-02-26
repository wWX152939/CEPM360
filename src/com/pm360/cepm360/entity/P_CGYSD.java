package com.pm360.cepm360.entity;


/**
 * 采购预算单
 * 
 * @author Andy
 * 
 */
public class P_CGYSD implements MarkFlowDCell {

    /**
     * 
     */
    private static final long serialVersionUID = -2328669749875358996L;

    private int cgysd_id;
    private int cgys_id;
    private int wz_id;
    private int wz_type_1;
    private int wz_type_2;
    private double quantity;
    private double unit_price;
    private double money;

    private String wz_name;
    private String wz_brand;
    private String wz_spec;
    private int wz_unit;
    private String wz_model_number;

    private int IDU;

    public int getCgysd_id() {
	return cgysd_id;
    }

    public void setCgysd_id(int cgysd_id) {
	this.cgysd_id = cgysd_id;
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
	return "P_CGYSD [cgysd_id=" + cgysd_id + ", cgys_id=" + cgys_id + ", wz_id=" + wz_id + ", wz_type_1=" + wz_type_1 + ", wz_type_2="
	        + wz_type_2 + ", quantity=" + quantity + ", unit_price=" + unit_price + ", money=" + money + ", wz_name=" + wz_name + ", wz_brand="
	        + wz_brand + ", wz_spec=" + wz_spec + ", wz_unit=" + wz_unit + ", wz_model_number=" + wz_model_number + ", IDU=" + IDU + "]";
    }

	@Override
	public int getId() {
		return cgysd_id;
	}

}
