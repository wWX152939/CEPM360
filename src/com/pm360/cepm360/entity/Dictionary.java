package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Dictionary implements Serializable {

    private static final long serialVersionUID = -6983711120684679468L;
    private int dictionary_id;
    private String code;
    private String name;
    private String type;
    private String module;

    public int getDictionary_id() {
	return dictionary_id;
    }

    public void setDictionary_id(int dictionary_id) {
	this.dictionary_id = dictionary_id;
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

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getModule() {
	return module;
    }

    public void setModule(String module) {
	this.module = module;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Dictionary [dictionary_id=" + dictionary_id + ", code=" + code + ", name=" + name + ", type=" + type + ", module=" + module + "]";
    }
}
