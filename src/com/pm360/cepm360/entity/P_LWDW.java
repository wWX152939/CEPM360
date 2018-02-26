package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 来往单位
 * 
 * @author Andy
 * 
 */
public class P_LWDW implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2376094379594713248L;
    private int lwdw_id;
    private int lwdw_dir_id;
    private String name;
    private String key_person;
    private String address;
    private String tel;
    private String lwdw_dir_name;

    public int getLwdw_id() {
	return lwdw_id;
    }

    public void setLwdw_id(int lwdw_id) {
	this.lwdw_id = lwdw_id;
    }

    public int getLwdw_dir_id() {
	return lwdw_dir_id;
    }

    public void setLwdw_dir_id(int lwdw_dir_id) {
	this.lwdw_dir_id = lwdw_dir_id;
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

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public String getTel() {
	return tel;
    }

    public void setTel(String tel) {
	this.tel = tel;
    }

    public String getLwdw_dir_name() {
        return lwdw_dir_name;
    }

    public void setLwdw_dir_name(String lwdw_dir_name) {
        this.lwdw_dir_name = lwdw_dir_name;
    }

    @Override
    public String toString() {
	return "P_LWDW [lwdw_id=" + lwdw_id + ", lwdw_dir_id=" + lwdw_dir_id + ", name=" + name + ", key_person=" + key_person + ", address="
	        + address + ", tel=" + tel + ", lwdw_dir_name=" + lwdw_dir_name + "]";
    }

}
