package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Templet_document_dir extends Expandable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8848026832752132629L;
    private int templet_document_dir_id;
    private int templet_document_id;
    private String name;
    private int p_templet_document_dir_id;

    public int getTemplet_document_dir_id() {
	return templet_document_dir_id;
    }

    public void setTemplet_document_dir_id(int templet_document_dir_id) {
	this.templet_document_dir_id = templet_document_dir_id;
    }

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

    public int getP_templet_document_dir_id() {
	return p_templet_document_dir_id;
    }

    public void setP_templet_document_dir_id(int p_templet_document_dir_id) {
	this.p_templet_document_dir_id = p_templet_document_dir_id;
    }

    @Override
    public String toString() {
	return "Templet_document_dir [templet_document_dir_id=" + templet_document_dir_id + ", templet_document_id=" + templet_document_id
	        + ", name=" + name + ", p_templet_document_dir_id=" + p_templet_document_dir_id + "]";
    }

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return templet_document_dir_id;
	}
	
	public void setId(int templet_document_dir_id) {
		// TODO Auto-generated method stub
		this.templet_document_dir_id = templet_document_dir_id;
	}

	@Override
	public int getParents_id() {
		// TODO Auto-generated method stub
		return p_templet_document_dir_id;
	}
	
    public void setParents_id(int parents_id) {
	this.p_templet_document_dir_id = parents_id;
    }

	@Override
	public void setParentsId(int parent_id) {
		p_templet_document_dir_id = parent_id;
	}	

}
