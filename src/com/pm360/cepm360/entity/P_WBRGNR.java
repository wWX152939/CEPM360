package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * 外包人工内容
 * 
 * @author Andy
 * 
 */
public class P_WBRGNR implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5949450210493504012L;

    private int wbrgnr_id;
    private int wbrgnr_dir_id;
    private String work;
    private int type;

    public int getWbrgnr_id() {
	return wbrgnr_id;
    }

    public void setWbrgnr_id(int wbrgnr_id) {
	this.wbrgnr_id = wbrgnr_id;
    }

    public int getWbrgnr_dir_id() {
	return wbrgnr_dir_id;
    }

    public void setWbrgnr_dir_id(int wbrgnr_dir_id) {
	this.wbrgnr_dir_id = wbrgnr_dir_id;
    }

    public String getWork() {
	return work;
    }

    public void setWork(String work) {
	this.work = work;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    @Override
    public String toString() {
	return "P_WBRGNR [wbrgnr_id=" + wbrgnr_id + ", wbrgnr_dir_id=" + wbrgnr_dir_id + ", work=" + work + ", type=" + type + "]";
    }

}
