package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * @author ouyanhua
 * 
 */

public class OBS extends Expandable implements Serializable {

    private static final long serialVersionUID = 2506189899128639977L;
    private int obs_id; // 当前节点id
    private int tenant_id;
    private String code;// 当前节点机构代码
    private String name;// 当前节点机构名称
    private String tel;// 当前节点机构电话
    private int parents_id = 0;// 父节点id
    private String user_ids; // 客户端暂存而已

    public int getId() {
    return obs_id;
    }
    
    public int getObs_id() {
	return obs_id;
    }

    public void setObs_id(int obs_id) {
	this.obs_id = obs_id;
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

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    public int getParents_id() {
	return parents_id;
    }

    public void setParents_id(int parents_id) {
	this.parents_id = parents_id;
    }

    @Override
    public String toString() {
        return "OBS [obs_id=" + obs_id + ", tenant_id=" + tenant_id + ", code=" + code + ", name=" + name + ", tel=" + tel + ", parents_id="
                + parents_id + ", has_child=" + isHas_child() + ", level=" + getLevel() + ", expanded=" + isExpanded() + "]";
    }

	@Override
	public void setParentsId(int parent_id) {
		parents_id = parent_id;
	}

    public String getUser_ids() {
        return user_ids;
    }

    public void setUser_ids(String user_ids) {
        this.user_ids = user_ids;
    }

}
