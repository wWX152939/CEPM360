package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 外包人工内容目录
 * 
 * @author Andy
 * 
 */
public class P_WBRGNR_DIR extends Expandable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6476323805119845566L;

    private int wbrgnr_dir_id;
    private String name;
    private int p_wbrgnr_dir_id;
    private int tenant_id;

    public int getWbrgnr_dir_id() {
	return wbrgnr_dir_id;
    }

    public void setWbrgnr_dir_id(int wbrgnr_dir_id) {
	this.wbrgnr_dir_id = wbrgnr_dir_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getP_wbrgnr_dir_id() {
	return p_wbrgnr_dir_id;
    }

    public void setP_wbrgnr_dir_id(int p_wbrgnr_dir_id) {
	this.p_wbrgnr_dir_id = p_wbrgnr_dir_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public int getId() {
	// TODO Auto-generated method stub
	return wbrgnr_dir_id;
    }

    @Override
    public int getParents_id() {
	// TODO Auto-generated method stub
	return p_wbrgnr_dir_id;
    }

	@Override
	public void setParentsId(int parent_id) {
		p_wbrgnr_dir_id = parent_id;
	}

}
