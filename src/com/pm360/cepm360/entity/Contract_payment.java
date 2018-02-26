package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 合同付款项（支付和回款）
 * 
 * @author Andy
 * 
 */
public class Contract_payment implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5967794933417210285L;
    private int id;
    private int contract_id;// 对应合同ID
    private String code;// 编号
    private String name;// 支付或者回款名称
    private double payable;// 应支付或者回款
    private Date expect_date;// 预计支付或者回款日期
    private double actual_pay;// 实际支付或者回款
    private Date actual_date;// 实际支付或者回款日期
    private String attachments;//附件，多个附件以逗号分开 1,2
    private int status;//审批状态

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getContract_id() {
	return contract_id;
    }

    public void setContract_id(int contract_id) {
	this.contract_id = contract_id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public double getPayable() {
	return payable;
    }

    public void setPayable(double payable) {
	this.payable = payable;
    }

    public Date getExpect_date() {
	return expect_date;
    }

    public void setExpect_date(Date expect_date) {
	this.expect_date = expect_date;
    }

    public double getActual_pay() {
	return actual_pay;
    }

    public void setActual_pay(double actual_pay) {
	this.actual_pay = actual_pay;
    }

    public Date getActual_date() {
	return actual_date;
    }

    public void setActual_date(Date actual_date) {
	this.actual_date = actual_date;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
	return "Contract_payment [id=" + id + ", contract_id=" + contract_id + ", code=" + code + ", name=" + name + ", payable=" + payable
	        + ", expect_date=" + expect_date + ", actual_pay=" + actual_pay + ", actual_date=" + actual_date + ", attachments=" + attachments
	        + ", status=" + status + "]";
    }

}
