package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 入库
 * 
 * @author Andy
 * 
 */
public class P_RK implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4079121447697681302L;

    private int rk_id;
    private int wz_id;
    private int wz_type_1;
    private int wz_type_2;
    private double in_quantity;
    private double cg_quantity;
    private double ys_quantity;
    private Date in_date;
    private Date cg_date;
    private int task_id;
    private String task_name;
    private String storehouse;
    private int project_id;
    private int tenant_id;
    private int cgd_id;
    private String cg_number;
    private int status;

    private String wz_name;
    private String wz_brand;
    private String wz_spec;
    private int wz_unit;
    private String wz_model_number;

    private int contract_id;
    private String contract_name;
    private int contract_change_id;
    private int contract_expenses_list_id;
    private int contract_change_list_id;

    private String attachment;
    private int operator;

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

    public double getIn_quantity() {
	return in_quantity;
    }

    public void setIn_quantity(double in_quantity) {
	this.in_quantity = in_quantity;
    }

    public double getCg_quantity() {
	return cg_quantity;
    }

    public void setCg_quantity(double cg_quantity) {
	this.cg_quantity = cg_quantity;
    }

    public Date getIn_date() {
	return in_date;
    }

    public void setIn_date(Date in_date) {
	this.in_date = in_date;
    }

    public Date getCg_date() {
	return cg_date;
    }

    public void setCg_date(Date cg_date) {
	this.cg_date = cg_date;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    public String getTask_name() {
	return task_name;
    }

    public void setTask_name(String task_name) {
	this.task_name = task_name;
    }

    public String getStorehouse() {
	return storehouse;
    }

    public void setStorehouse(String storehouse) {
	this.storehouse = storehouse;
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

    public int getCgd_id() {
	return cgd_id;
    }

    public void setCgd_id(int cgd_id) {
	this.cgd_id = cgd_id;
    }

    public String getCg_number() {
	return cg_number;
    }

    public void setCg_number(String cg_number) {
	this.cg_number = cg_number;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
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

    public String getContract_name() {
        return contract_name;
    }

    public void setContract_name(String contract_name) {
        this.contract_name = contract_name;
    }

    public int getContract_id() {
	return contract_id;
    }

    public void setContract_id(int contract_id) {
	this.contract_id = contract_id;
    }

    public int getContract_change_id() {
	return contract_change_id;
    }

    public void setContract_change_id(int contract_change_id) {
	this.contract_change_id = contract_change_id;
    }

    public int getContract_expenses_list_id() {
	return contract_expenses_list_id;
    }

    public void setContract_expenses_list_id(int contract_expenses_list_id) {
	this.contract_expenses_list_id = contract_expenses_list_id;
    }

    public int getContract_change_list_id() {
	return contract_change_list_id;
    }

    public void setContract_change_list_id(int contract_change_list_id) {
	this.contract_change_list_id = contract_change_list_id;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    public int getOperator() {
	return operator;
    }

    public void setOperator(int operator) {
	this.operator = operator;
    }

    public double getYs_quantity() {
        return ys_quantity;
    }

    public void setYs_quantity(double ys_quantity) {
        this.ys_quantity = ys_quantity;
    }

    @Override
    public String toString() {
	return "P_RK [rk_id=" + rk_id + ", wz_id=" + wz_id + ", wz_type_1=" + wz_type_1 + ", wz_type_2=" + wz_type_2 + ", in_quantity=" + in_quantity
	        + ", cg_quantity=" + cg_quantity + ", ys_quantity=" + ys_quantity + ", in_date=" + in_date + ", cg_date=" + cg_date + ", task_id="
	        + task_id + ", task_name=" + task_name + ", storehouse=" + storehouse + ", project_id=" + project_id + ", tenant_id=" + tenant_id
	        + ", cgd_id=" + cgd_id + ", cg_number=" + cg_number + ", status=" + status + ", wz_name=" + wz_name + ", wz_brand=" + wz_brand
	        + ", wz_spec=" + wz_spec + ", wz_unit=" + wz_unit + ", wz_model_number=" + wz_model_number + ", contract_id=" + contract_id
	        + ", contract_name=" + contract_name + ", contract_change_id=" + contract_change_id + ", contract_expenses_list_id="
	        + contract_expenses_list_id + ", contract_change_list_id=" + contract_change_list_id + ", attachment=" + attachment + ", operator="
	        + operator + "]";
    }

}
