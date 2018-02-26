package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 工程签证单
 * 
 * @author Andy
 * 
 */
public class P_GCQZ implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1770050193773280362L;

    private int gcqz_id;
    private int project_id;
    private String code;
    private String name;
    private int tenant_id;

    public int getGcqz_id() {
	return gcqz_id;
    }

    public void setGcqz_id(int gcqz_id) {
	this.gcqz_id = gcqz_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
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

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "P_GCQZ [gcqz_id=" + gcqz_id + ", project_id=" + project_id + ", code=" + code + ", name=" + name + ", tenant_id=" + tenant_id + "]";
    }

}
