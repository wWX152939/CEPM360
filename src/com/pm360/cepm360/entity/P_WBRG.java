package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 外包人工
 * 
 * @author Andy
 * 
 */
public class P_WBRG implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7558059002537926322L;

    private int wbrg_id;
    private String wbrg_number;
    private int wbht_id;
    private String wbht_name;
    private double total_money;
    private String mark;
    private int project_id;
    private int tenant_id;
    private Date create_date;
    private int status;
    private int creater;

    public int getWbrg_id() {
	return wbrg_id;
    }

    public void setWbrg_id(int wbrg_id) {
	this.wbrg_id = wbrg_id;
    }

    public String getWbrg_number() {
	return wbrg_number;
    }

    public void setWbrg_number(String wbrg_number) {
	this.wbrg_number = wbrg_number;
    }

    public int getWbht_id() {
	return wbht_id;
    }

    public void setWbht_id(int wbht_id) {
	this.wbht_id = wbht_id;
    }

    public String getWbht_name() {
	return wbht_name;
    }

    public void setWbht_name(String wbht_name) {
	this.wbht_name = wbht_name;
    }

    public double getTotal_money() {
	return total_money;
    }

    public void setTotal_money(double total_money) {
	this.total_money = total_money;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
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

    public Date getCreate_date() {
	return create_date;
    }

    public void setCreate_date(Date create_date) {
	this.create_date = create_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreater() {
        return creater;
    }

    public void setCreater(int creater) {
        this.creater = creater;
    }

    @Override
    public String toString() {
	return "P_WBRG [wbrg_id=" + wbrg_id + ", wbrg_number=" + wbrg_number + ", wbht_id=" + wbht_id + ", wbht_name=" + wbht_name + ", total_money="
	        + total_money + ", mark=" + mark + ", project_id=" + project_id + ", tenant_id=" + tenant_id + ", create_date=" + create_date
	        + ", status=" + status + ", creater=" + creater + "]";
    }

}
