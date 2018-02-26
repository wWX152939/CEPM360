package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Templet_document implements Serializable {

    private static final long serialVersionUID = -6188182261822307117L;
    private int templet_document_id;
    private String name;
    private int type;
    private int tenant_id;

    public int getTemplet_document_id() {
	return templet_document_id;
    }

    public void setTemplet_document_id(int templet_document_id) {
	this.templet_document_id = templet_document_id;
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
	return "Templet_document [templet_document_id=" + templet_document_id + ", name=" + name + ", type=" + type + ", tenant_id=" + tenant_id
	        + "]";
    }

}
