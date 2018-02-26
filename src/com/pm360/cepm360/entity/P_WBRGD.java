package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 外包人工单
 * 
 * @author Andy
 * 
 */
public class P_WBRGD implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1106504356694763609L;

    private int wbrgd_id;
    private int wbrg_id;
    private int wbrgnr_id;
    private double quantity;
    private double unit_price;
    private double money;
    private int lwdw_id;
    private Date indate;
    private Date outdate;
    private int task_id;
    private String task_name;
    
    private int rg_type;//工种
    private String rg_work;//分包内容
    private String dw_name;//来往单位
    
    private int IDU;

    public int getWbrgd_id() {
	return wbrgd_id;
    }

    public void setWbrgd_id(int wbrgd_id) {
	this.wbrgd_id = wbrgd_id;
    }

    public int getWbrg_id() {
	return wbrg_id;
    }

    public void setWbrg_id(int wbrg_id) {
	this.wbrg_id = wbrg_id;
    }

    public int getWbrgnr_id() {
	return wbrgnr_id;
    }

    public void setWbrgnr_id(int wbrgnr_id) {
	this.wbrgnr_id = wbrgnr_id;
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

    public Date getOutdate() {
	return outdate;
    }

    public void setOutdate(Date outdate) {
	this.outdate = outdate;
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

    public int getRg_type() {
        return rg_type;
    }

    public void setRg_type(int rg_type) {
        this.rg_type = rg_type;
    }

    public String getRg_work() {
        return rg_work;
    }

    public void setRg_work(String rg_work) {
        this.rg_work = rg_work;
    }

    public String getDw_name() {
        return dw_name;
    }

    public void setDw_name(String dw_name) {
        this.dw_name = dw_name;
    }

    public int getIDU() {
        return IDU;
    }

    public void setIDU(int iDU) {
        IDU = iDU;
    }

    @Override
    public String toString() {
	return "P_WBRGD [wbrgd_id=" + wbrgd_id + ", wbrg_id=" + wbrg_id + ", wbrgnr_id=" + wbrgnr_id + ", quantity=" + quantity + ", unit_price="
	        + unit_price + ", money=" + money + ", lwdw_id=" + lwdw_id + ", indate=" + indate + ", outdate=" + outdate + ", task_id=" + task_id
	        + ", task_name=" + task_name + ", rg_type=" + rg_type + ", rg_work=" + rg_work + ", dw_name=" + dw_name + ", IDU=" + IDU + "]";
    }

}
