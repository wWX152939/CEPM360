package com.pm360.cepm360.entity;

import java.io.Serializable;

public class P_final_cost implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6718735032688301729L;

    private int type_id_1;
    private String type_1;
    private int type_id_2;
    private String type_2;
    private double money;
    private String mark;
    private int flag; // 0：可编辑

    public int getType_id_1() {
	return type_id_1;
    }

    public void setType_id_1(int type_id_1) {
	this.type_id_1 = type_id_1;
    }

    public String getType_1() {
	return type_1;
    }

    public void setType_1(String type_1) {
	this.type_1 = type_1;
    }

    public int getType_id_2() {
	return type_id_2;
    }

    public void setType_id_2(int type_id_2) {
	this.type_id_2 = type_id_2;
    }

    public String getType_2() {
	return type_2;
    }

    public void setType_2(String type_2) {
	this.type_2 = type_2;
    }

    public double getMoney() {
	return money;
    }

    public void setMoney(double money) {
	this.money = money;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public int getFlag() {
	return flag;
    }

    public void setFlag(int flag) {
	this.flag = flag;
    }

    @Override
    public String toString() {
	return "P_final_cost [type_id_1=" + type_id_1 + ", type_1=" + type_1 + ", type_id_2=" + type_id_2 + ", type_2=" + type_2 + ", money=" + money
	        + ", mark=" + mark + ", flag=" + flag + "]";
    }

}
