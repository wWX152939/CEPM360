package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 流程设置
 * 
 * @author Andy
 *
 */
public class Flow_setting implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 515514252632407715L;
    private int flow_id; // 流程ID
    private String flow_type; // 流程类型
    private String flow_name;// 流程名称
    private int status;// 流程状态 ,对应宏定义里面的FLOW_STATUS（1:启用 2：关闭 ）
    private int level1;// 节点1对应的user_id
    private int level2;
    private int level3;
    private int level4;
    private int level5;
    private int tenant_id;

    public int getFlow_id() {
	return flow_id;
    }

    public void setFlow_id(int flow_id) {
	this.flow_id = flow_id;
    }

    public String getFlow_type() {
	return flow_type;
    }

    public void setFlow_type(String flow_type) {
	this.flow_type = flow_type;
    }

    public String getFlow_name() {
	return flow_name;
    }

    public void setFlow_name(String flow_name) {
	this.flow_name = flow_name;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public int getLevel1() {
	return level1;
    }

    public void setLevel1(int level1) {
	this.level1 = level1;
    }

    public int getLevel2() {
	return level2;
    }

    public void setLevel2(int level2) {
	this.level2 = level2;
    }

    public int getLevel3() {
	return level3;
    }

    public void setLevel3(int level3) {
	this.level3 = level3;
    }

    public int getLevel4() {
	return level4;
    }

    public void setLevel4(int level4) {
	this.level4 = level4;
    }

    public int getLevel5() {
	return level5;
    }

    public void setLevel5(int level5) {
	this.level5 = level5;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "Flow_setting [flow_id=" + flow_id + ", flow_type=" + flow_type + ", flow_name=" + flow_name + ", status=" + status + ", level1="
	        + level1 + ", level2=" + level2 + ", level3=" + level3 + ", level4=" + level4 + ", level5=" + level5 + ", tenant_id=" + tenant_id
	        + "]";
    }

}
