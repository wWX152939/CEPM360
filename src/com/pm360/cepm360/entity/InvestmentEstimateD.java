package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 投资估算单
 * 
 * @author Andy
 * 
 */
public class InvestmentEstimateD implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 856508545442097501L;
    private int investment_estimate_d_id;
    private int investment_estimate_id;
    private String cost_name;// 费用名
    private double construction_cost;// 建筑费
    private double install_cost;// 安装费
    private double other_cost;// 其他费用
    private double sum;// 合计
    private String unit;// 单位
    private double amount;// 数量
    private String mark;// 备注
    
    private int IDU;

    public int getInvestment_estimate_d_id() {
	return investment_estimate_d_id;
    }

    public void setInvestment_estimate_d_id(int investment_estimate_d_id) {
	this.investment_estimate_d_id = investment_estimate_d_id;
    }

    public int getInvestment_estimate_id() {
	return investment_estimate_id;
    }

    public void setInvestment_estimate_id(int investment_estimate_id) {
	this.investment_estimate_id = investment_estimate_id;
    }

    public String getCost_name() {
	return cost_name;
    }

    public void setCost_name(String cost_name) {
	this.cost_name = cost_name;
    }

    public double getConstruction_cost() {
	return construction_cost;
    }

    public void setConstruction_cost(double construction_cost) {
	this.construction_cost = construction_cost;
    }

    public double getInstall_cost() {
	return install_cost;
    }

    public void setInstall_cost(double install_cost) {
	this.install_cost = install_cost;
    }

    public double getOther_cost() {
	return other_cost;
    }

    public void setOther_cost(double other_cost) {
	this.other_cost = other_cost;
    }

    public double getSum() {
	return sum;
    }

    public void setSum(double sum) {
	this.sum = sum;
    }

    public String getUnit() {
	return unit;
    }

    public void setUnit(String unit) {
	this.unit = unit;
    }

    public double getAmount() {
	return amount;
    }

    public void setAmount(double amount) {
	this.amount = amount;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public int getIDU() {
        return IDU;
    }

    public void setIDU(int iDU) {
        IDU = iDU;
    }

    @Override
    public String toString() {
	return "InvestmentEstimateD [investment_estimate_d_id=" + investment_estimate_d_id + ", investment_estimate_id=" + investment_estimate_id
	        + ", cost_name=" + cost_name + ", construction_cost=" + construction_cost + ", install_cost=" + install_cost + ", other_cost="
	        + other_cost + ", sum=" + sum + ", unit=" + unit + ", amount=" + amount + ", mark=" + mark + ", IDU=" + IDU + "]";
    }

}
