package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 物资目录
 * 
 * @author Andy
 * 
 */
public class P_WZ_DIR extends Expandable implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -379500001403625843L;

    private int wz_dir_id;
    private int wz_type_1;
    private String name;
    private int p_wz_dir_id;
    private int tenant_id;

    public int getWz_dir_id() {
	return wz_dir_id;
    }

    public void setWz_dir_id(int wz_dir_id) {
	this.wz_dir_id = wz_dir_id;
    }

    public int getWz_type_1() {
	return wz_type_1;
    }

    public void setWz_type_1(int wz_type_1) {
	this.wz_type_1 = wz_type_1;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getP_wz_dir_id() {
	return p_wz_dir_id;
    }

    public void setP_wz_dir_id(int p_wz_dir_id) {
	this.p_wz_dir_id = p_wz_dir_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "P_WZ_DIR [wz_dir_id=" + wz_dir_id + ", wz_type_1=" + wz_type_1 + ", name=" + name + ", p_wz_dir_id=" + p_wz_dir_id + ", tenant_id="
	        + tenant_id + "]";
    }

	@Override
	public int getId() {
		return wz_dir_id;
	}

	@Override
	public int getParents_id() {
		return p_wz_dir_id;
	}

	@Override
	public void setParentsId(int parent_id) {
		p_wz_dir_id = parent_id;
	}

}
