package com.pm360.cepm360.entity;

import java.util.Date;

/**
 * 租赁-租入
 * 
 * @author Andy
 * 
 */
public class P_ZLR implements MarkFlowCell {

    /**
     * 
     */
    private static final long serialVersionUID = 4080689414498050926L;
    private int zlr_id;// 租入单ID
    private String zlr_number;// 租入单号
    private int project_id;// 进场项目ID
    private String project_name;// 进场项目名称
    private int contract_id;// 执行合同
    private String zlht_name;// 合同名称
    private double total;// 合同金额
    private int zl_company;// 租赁单位ID
    private String zl_company_name;// 租赁单位名称
    private int item;// 单据项
    private int operator;// 经办人
    private Date create_date;// 创建时间
    private String storehouse;// 库房地点
    private int storeman;// 库管员
    private int status;
    private int tenant_id;
    private String mark;// 备注

    public int getZlr_id() {
	return zlr_id;
    }

    public void setZlr_id(int zlr_id) {
	this.zlr_id = zlr_id;
    }

    public String getZlr_number() {
	return zlr_number;
    }

    public void setZlr_number(String zlr_number) {
	this.zlr_number = zlr_number;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public String getProject_name() {
	return project_name;
    }

    public void setProject_name(String project_name) {
	this.project_name = project_name;
    }

    public int getContract_id() {
	return contract_id;
    }

    public void setContract_id(int contract_id) {
	this.contract_id = contract_id;
    }

    public String getZlht_name() {
	return zlht_name;
    }

    public void setZlht_name(String zlht_name) {
	this.zlht_name = zlht_name;
    }

    public double getTotal() {
	return total;
    }

    public void setTotal(double total) {
	this.total = total;
    }

    public int getZl_company() {
	return zl_company;
    }

    public void setZl_company(int zl_company) {
	this.zl_company = zl_company;
    }

    public String getZl_company_name() {
	return zl_company_name;
    }

    public void setZl_company_name(String zl_company_name) {
	this.zl_company_name = zl_company_name;
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

    public String getStorehouse() {
	return storehouse;
    }

    public void setStorehouse(String storehouse) {
	this.storehouse = storehouse;
    }

    public int getStoreman() {
	return storeman;
    }

    public void setStoreman(int storeman) {
	this.storeman = storeman;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    @Override
    public String toString() {
	return "P_ZLR [zlr_id=" + zlr_id + ", zlr_number=" + zlr_number + ", project_id=" + project_id + ", project_name=" + project_name
	        + ", contract_id=" + contract_id + ", zlht_name=" + zlht_name + ", total=" + total + ", zl_company=" + zl_company
	        + ", zl_company_name=" + zl_company_name + ", item=" + item + ", operator=" + operator + ", create_date=" + create_date
	        + ", storehouse=" + storehouse + ", storeman=" + storeman + ", status=" + status + ", tenant_id=" + tenant_id + ", mark=" + mark
	        + "]";
    }

	@Override
	public int getId() {
		return zlr_id;
	}

	@Override
	public void setId(int id) {
		zlr_id = id;
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
		total = money;
	}

	@Override
	public double getTotal_money() {
		return total;
	}

}
