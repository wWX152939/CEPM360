package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Logic implements Serializable {

    private static final long serialVersionUID = 3816608488023264339L;
    private int logic_id;
    private int task_id;
    private String task_name;
    private int logic;
    private int logic_type;
    private int period;
    private int type;

    public int getLogic_id() {
	return logic_id;
    }

    public void setLogic_id(int logic_id) {
	this.logic_id = logic_id;
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

    public int getLogic() {
	return logic;
    }

    public void setLogic(int logic) {
	this.logic = logic;
    }

    public int getLogic_type() {
	return logic_type;
    }

    public void setLogic_type(int logic_type) {
	this.logic_type = logic_type;
    }

    public int getPeriod() {
	return period;
    }

    public void setPeriod(int period) {
	this.period = period;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    @Override
    public String toString() {
	return "Logic [logic_id=" + logic_id + ", task_id=" + task_id + ", task_name=" + task_name + ", logic=" + logic + ", logic_type="
	        + logic_type + ", period=" + period + ", type=" + type + "]";
    }

}
