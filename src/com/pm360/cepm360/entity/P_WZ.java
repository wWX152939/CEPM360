package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 物资
 * 
 * @author Andy
 * 
 */
public class P_WZ implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4277378196084179865L;

    private int wz_id;
    private int wz_dir_id;
    private String name;
    private int wz_type_2;
    private String brand;
    private String spec;
    private int unit;
    private String model_number;
    private int tenant_id;

    public int getWz_id() {
	return wz_id;
    }

    public void setWz_id(int wz_id) {
	this.wz_id = wz_id;
    }

    public int getWz_dir_id() {
	return wz_dir_id;
    }

    public void setWz_dir_id(int wz_dir_id) {
	this.wz_dir_id = wz_dir_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getWz_type_2() {
	return wz_type_2;
    }

    public void setWz_type_2(int wz_type_2) {
	this.wz_type_2 = wz_type_2;
    }

    public String getBrand() {
	return brand;
    }

    public void setBrand(String brand) {
	this.brand = brand;
    }

    public String getSpec() {
	return spec;
    }

    public void setSpec(String spec) {
	this.spec = spec;
    }

    public int getUnit() {
	return unit;
    }

    public void setUnit(int unit) {
	this.unit = unit;
    }

    public String getModel_number() {
	return model_number;
    }

    public void setModel_number(String model_number) {
	this.model_number = model_number;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "P_WZ [wz_id=" + wz_id + ", wz_dir_id=" + wz_dir_id + ", name=" + name + ", wz_type_2=" + wz_type_2 + ", brand=" + brand + ", spec="
	        + spec + ", unit=" + unit + ", model_number=" + model_number + ", tenant_id=" + tenant_id + "]";
    }

}
