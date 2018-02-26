package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 合同清单项（收入和支出）
 * 
 * @author Andy
 * 
 */
public class Contract_list implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1479056342621092739L;

    private int id;
    private int project_id;//
    private int contract_id;// 对应合同ID
    private String contract_code;// 对应合同编号
    private int contract_change_id;// 合同变更ID
    private String contract_change_code;// 合同变更号
    private int qd_id;// 清单ID(物资、租赁、外包内容)
    private String qd_name;// 清单名称
    private String qd_spec;// 清单规格
    private int qd_unit;// 清单单位
    private double quantity;// 数量
    private double unit_price;// 单价
    private String attachments;// 附件，多个附件以逗号分开 1,2
    private double total;// 总价
    private int tenant_id;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getContract_id() {
	return contract_id;
    }

    public void setContract_id(int contract_id) {
	this.contract_id = contract_id;
    }

    public String getContract_code() {
	return contract_code;
    }

    public void setContract_code(String contract_code) {
	this.contract_code = contract_code;
    }

    public int getContract_change_id() {
	return contract_change_id;
    }

    public void setContract_change_id(int contract_change_id) {
	this.contract_change_id = contract_change_id;
    }

    public String getContract_change_code() {
	return contract_change_code;
    }

    public void setContract_change_code(String contract_change_code) {
	this.contract_change_code = contract_change_code;
    }

    public int getQd_id() {
	return qd_id;
    }

    public void setQd_id(int qd_id) {
	this.qd_id = qd_id;
    }

    public String getQd_name() {
	return qd_name;
    }

    public void setQd_name(String qd_name) {
	this.qd_name = qd_name;
    }

    public String getQd_spec() {
	return qd_spec;
    }

    public void setQd_spec(String qd_spec) {
	this.qd_spec = qd_spec;
    }

    public int getQd_unit() {
	return qd_unit;
    }

    public void setQd_unit(int qd_unit) {
	this.qd_unit = qd_unit;
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

    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    public double getTotal() {
	return total;
    }

    public void setTotal(double total) {
	this.total = total;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "Contract_list [id=" + id + ", project_id=" + project_id + ", contract_id=" + contract_id + ", contract_code=" + contract_code
	        + ", contract_change_id=" + contract_change_id + ", contract_change_code=" + contract_change_code + ", qd_id=" + qd_id + ", qd_name="
	        + qd_name + ", qd_spec=" + qd_spec + ", qd_unit=" + qd_unit + ", quantity=" + quantity + ", unit_price=" + unit_price
	        + ", attachments=" + attachments + ", total=" + total + ", tenant_id=" + tenant_id + "]";
    }

}
