/**
 * 
 */
package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * @author cruise
 * 
 */

public class EPS extends Expandable implements Serializable {
    
    private static final long serialVersionUID = -7973737667094171726L;
    
    private int eps_id; // 当前节点id
    private int tenant_id;
    private String code;// 当前节点机构代码
    private String name;// 当前节点机构名称
    private int parents_id;// 父节点id

    public int getId() {
        return eps_id;
    }
    
    public int getEps_id() {
	return eps_id;
    }

    public void setEps_id(int eps_id) {
	this.eps_id = eps_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getParents_id() {
	return parents_id;
    }

    public void setParents_id(int parents_id) {
	this.parents_id = parents_id;
    }

    @Override
    public String toString() {
        return "EPS [eps_id=" + eps_id + ", tenant_id=" + tenant_id + ", code=" + code + ", name=" + name + ", parents_id=" + parents_id
                + ", has_child=" + isHas_child() + ", level=" + getLevel() + ", expanded=" + isExpanded() + "]";
    }

	@Override
	public void setParentsId(int parent_id) {
		parents_id = parent_id;
	}
}
