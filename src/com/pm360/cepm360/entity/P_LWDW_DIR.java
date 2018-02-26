package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 来往单位目录
 * 
 * @author Andy
 * 
 */
public class P_LWDW_DIR extends Expandable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3781701600545443466L;

    private int lwdw_dir_id;
    private String name;
    private int p_lwdw_dir_id;
    private int tenant_id;

    public int getLwdw_dir_id() {
	return lwdw_dir_id;
    }

    public void setLwdw_dir_id(int lwdw_dir_id) {
	this.lwdw_dir_id = lwdw_dir_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getP_lwdw_dir_id() {
	return p_lwdw_dir_id;
    }

    public void setP_lwdw_dir_id(int p_lwdw_dir_id) {
	this.p_lwdw_dir_id = p_lwdw_dir_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

	@Override
	public String toString() {
		return "P_LWDW_DIR [lwdw_dir_id=" + lwdw_dir_id + ", name=" + name
				+ ", p_lwdw_dir_id=" + p_lwdw_dir_id + ", tenant_id="
				+ tenant_id + "]";
	}

	@Override
	public int getId() {
		return lwdw_dir_id;
	}

	@Override
	public int getParents_id() {
		return p_lwdw_dir_id;
	}

	@Override
	public void setParentsId(int parent_id) {
		p_lwdw_dir_id = parent_id;
	}
}
