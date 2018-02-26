package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 租赁目录维护
 * 
 * @author Andy
 * 
 */
public class P_ZL_DIR extends Expandable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4634886569895306834L;
    private int zl_dir_id;//租赁目录ID
    private String name;//目录名称
    private int p_zl_dir_id;//父目录节点ID
    private int tenant_id;

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

    public int getP_zl_dir_id() {
	return p_zl_dir_id;
    }

    public void setP_zl_dir_id(int p_zl_dir_id) {
	this.p_zl_dir_id = p_zl_dir_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "P_ZL_DIR [zl_dir_id=" + zl_dir_id + ", name=" + name + ", p_zl_dir_id=" + p_zl_dir_id + ", tenant_id=" + tenant_id + "]";
    }

    @Override
    public int getId() {
	// TODO Auto-generated method stub
	return zl_dir_id;
    }

    @Override
    public int getParents_id() {
	// TODO Auto-generated method stub
	return p_zl_dir_id;
    }

    @Override
    public void setParentsId(int parent_id) {
	
    	p_zl_dir_id = parent_id;
    }

}
