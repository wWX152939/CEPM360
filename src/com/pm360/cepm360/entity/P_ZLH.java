package com.pm360.cepm360.entity;

import java.util.Date;


/**
 * 租赁-还租
 * 
 * @author Andy
 * 
 */
public class P_ZLH implements MarkFlowCell {

    /**
     * 
     */
    private static final long serialVersionUID = -3329692879308763529L;
    private int zlh_id;
    private String zlh_number;// 还租单号
    private int project_id;// 进场项目
    private int item;// 单据项
    private int operator;// 经办人
    private Date create_date;// 经办时间
    private String mark;// 备注
    private int tenant_id;

    public int getZlh_id() {
	return zlh_id;
    }

    public void setZlh_id(int zlh_id) {
	this.zlh_id = zlh_id;
    }

    public String getZlh_number() {
	return zlh_number;
    }

    public void setZlh_number(String zlh_number) {
	this.zlh_number = zlh_number;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getItem() {
	return item;
    }

    public void setItem(int item) {
	this.item = item;
    }

    public int getOperator() {
	return operator;
    }

    public void setOperator(int operator) {
	this.operator = operator;
    }

    public Date getCreate_date() {
	return create_date;
    }

    public void setCreate_date(Date create_date) {
	this.create_date = create_date;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "P_ZLH [zlh_id=" + zlh_id + ", zlh_number=" + zlh_number + ", project_id=" + project_id + ", item=" + item + ", operator=" + operator
	        + ", create_date=" + create_date + ", mark=" + mark + ", tenant_id=" + tenant_id + "]";
    }

	@Override
	public int getId() {
		return getZlh_id();
	}

	@Override
	public void setId(int id) {
		setZlh_id(id);
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStatus(int status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPlan_person() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPlan_person(int plan_person) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTotal_money(double money) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getTotal_money() {
		// TODO Auto-generated method stub
		return 0;
	}
}
