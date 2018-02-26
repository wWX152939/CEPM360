package com.pm360.cepm360.entity;

import java.io.Serializable;

public class Document extends Expandable implements Serializable {

    private static final long serialVersionUID = -8680732968063209920L;
    private int document_id;
    private int templet_document_dir_id;
    private String name;
    private int parents_id;
    private String path;
    private int directory_type;
    private int user_id;
    private int project_id;
    private int tenant_id;
    private int file_count;
    private String relevance_path_code;

    public int getDocument_id() {
	return document_id;
    }

    public void setDocument_id(int document_id) {
	this.document_id = document_id;
    }

    public int getTemplet_document_dir_id() {
	return templet_document_dir_id;
    }

    public void setTemplet_document_dir_id(int templet_document_dir_id) {
	this.templet_document_dir_id = templet_document_dir_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getParents_id() {
	return parents_id;
    }

    public void setParents_id(int parents_id) {
	this.parents_id = parents_id;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public int getDirectory_type() {
	return directory_type;
    }

    public void setDirectory_type(int directory_type) {
	this.directory_type = directory_type;
    }

    public int getUser_id() {
	return user_id;
    }

    public void setUser_id(int user_id) {
	this.user_id = user_id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int getFile_count() {
	return file_count;
    }

    public void setFile_count(int file_count) {
	this.file_count = file_count;
    }

    public String getRelevance_path_code() {
	return relevance_path_code;
    }

    public void setRelevance_path_code(String relevance_path_code) {
	this.relevance_path_code = relevance_path_code;
    }

    @Override
    public String toString() {
	return "Document [document_id=" + document_id + ", templet_document_dir_id=" + templet_document_dir_id + ", name=" + name + ", parents_id="
	        + parents_id + ", path=" + path + ", directory_type=" + directory_type + ", user_id=" + user_id + ", project_id=" + project_id
	        + ", tenant_id=" + tenant_id + ", file_count=" + file_count + ", relevance_path_code=" + relevance_path_code + "]";
    }

    @Override
    public int getId() {
	return document_id;
    }

	@Override
	public void setParentsId(int parent_id) {
		parents_id = parent_id;
	}

}
