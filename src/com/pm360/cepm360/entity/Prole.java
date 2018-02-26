package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Prole implements Serializable {

    private static final long serialVersionUID = 1L;
    private int prole_id;
    private int tenant_id;
    private int project_id;
    private int user_id;
    private String role;
    private int type;
    private int IDU;

    public int getProle_id() {
	return prole_id;
    }

    public void setProle_id(int prole_id) {
	this.prole_id = prole_id;
    }

    public int getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
        this.tenant_id = tenant_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
    }

    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIDU() {
        return IDU;
    }

    public void setIDU(int iDU) {
        IDU = iDU;
    }

    @Override
    public String toString() {
	return "Prole [prole_id=" + prole_id + ", tenant_id=" + tenant_id + ", project_id=" + project_id + ", user_id=" + user_id + ", role=" + role
	        + ", type=" + type + ", IDU=" + IDU + "]";
    }

}
