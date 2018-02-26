package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 租赁资源信息维护
 * 
 * @author Andy
 * 
 */
public class P_ZL implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7555929463954288106L;
    private int zl_id;//租赁信息ID
    private int zl_dir_id;//租赁目录ID
    private String name;//租赁资源名称
    private int zl_type;//租赁资源类型
    private String spec;//租赁资源规格
    private int unit;//租赁资源单位
    private int tenant_id;

    public int getZl_id() {
	return zl_id;
    }

    public void setZl_id(int zl_id) {
	this.zl_id = zl_id;
    }

    public int getZl_dir_id() {
	return zl_dir_id;
    }

    public void setZl_dir_id(int zl_dir_id) {
	this.zl_dir_id = zl_dir_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getZl_type() {
	return zl_type;
    }

    public void setZl_type(int zl_type) {
	this.zl_type = zl_type;
    }

    public String getSpec() {
	return spec;
    }

    public void setSpec(String spec) {
	this.spec = spec;
    }

    public int getUnit() {
	return unit;
    }

    public void setUnit(int unit) {
	this.unit = unit;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "P_ZL [zl_id=" + zl_id + ", zl_dir_id=" + zl_dir_id + ", name=" + name + ", zl_type=" + zl_type + ", spec=" + spec + ", unit=" + unit
	        + ", tenant_id=" + tenant_id + "]";
    }
}
