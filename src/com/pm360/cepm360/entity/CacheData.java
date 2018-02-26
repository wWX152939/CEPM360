package com.pm360.cepm360.entity;

import java.io.Serializable;

public class CacheData implements Serializable {

    private static final long serialVersionUID = -3346044312539017050L;

    // 某项目上的所有人员（包含项目人员、CC人员、用户），以逗号形式分开
    private String project_member;

    // 该公司下的所有人员，以逗号形式分开
    private String tenant_member;

    public String getProject_member() {
	return project_member;
    }

    public void setProject_member(String project_member) {
	this.project_member = project_member;
    }

    public String getTenant_member() {
	return tenant_member;
    }

    public void setTenant_member(String tenant_member) {
	this.tenant_member = tenant_member;
    }

    @Override
    public String toString() {
	return "CacheData [project_member=" + project_member + ", tenant_member=" + tenant_member + "]";
    }

}
