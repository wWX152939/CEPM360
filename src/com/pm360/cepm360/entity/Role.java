package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Role implements Serializable {
    private static final long serialVersionUID = 6744876181427724919L;
    private int role_id;
    private int tenant_id;
    private String type;
    private String code;
    private String name;
    private String action;

    public int getRole_id() {
	return role_id;
    }

    public void setRole_id(int role_id) {
	this.role_id = role_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
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

  

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
	return "Role [role_id=" + role_id + ", tenant_id=" + tenant_id + ", type=" + type + ", code=" + code + ", name=" + name + ", action="
	        + action + "]";
    }

}
