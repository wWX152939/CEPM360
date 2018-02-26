package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Templet_WBS implements Serializable {

    private static final long serialVersionUID = 6022857697545126428L;
    private int templet_wbs_id;
    private String name;
    private int type;
    private int tenant_id;

    public int getTemplet_wbs_id() {
	return templet_wbs_id;
    }

    public void setTemplet_wbs_id(int templet_wbs_id) {
	this.templet_wbs_id = templet_wbs_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "Templet_WBS [templet_wbs_id=" + templet_wbs_id + ", name=" + name + ", type=" + type + ", tenant_id=" + tenant_id + "]";
    };

}
