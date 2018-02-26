package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class Tenant implements Serializable {

    private static final long serialVersionUID = 8052288715792421409L;
    private int tenant_id;
    private String code;
    private String name;
    private String key_person;
    private String tel;
    private String address;
    private int type;
    private Date create_time;
    

    private int cooperation_id;

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

    public String getKey_person() {
	return key_person;
    }

    public void setKey_person(String key_person) {
	this.key_person = key_person;
    }

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public int getCooperation_id() {
        return cooperation_id;
    }

    public void setCooperation_id(int cooperation_id) {
        this.cooperation_id = cooperation_id;
    }

    @Override
    public String toString() {
	return "Tenant [tenant_id=" + tenant_id + ", code=" + code + ", name=" + name + ", key_person=" + key_person + ", tel=" + tel + ", address="
	        + address + ", type=" + type + ", create_time=" + create_time + ", cooperation_id=" + cooperation_id + "]";
    }

}
