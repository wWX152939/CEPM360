package com.pm360.cepm360.entity;

import java.util.Date;

public class Files extends AttachTaskCell {

    private static final long serialVersionUID = -6965508115042679908L;
    private int file_id;
    private String directory_id;// 对应document对象的document_id，有多个document_id组成，以逗号分开
    private int type_id;
    private String code;
    private String title;
    private int author;
    private String status;
    private String version;
    private int score;
    private int dir_type;
    private String path;
    private String file_name;
    private String file_type;
    private long file_size;
    private String file_path;
    private Date create_time;
    private Date modified_time;
    private String mark;
    private int archive;// 是否归档
    private int top;// 置顶标志（正对现场图片和形象成果设置为首页显示）

    private int tenant_id;
    private int project_id;
    private int task_id;

    // --------------------------

    private String project_name;
    private int appUpdateFlag;// 1：表示下载APP最新版本 2：其他文件的下载

    public int getFile_id() {
	return file_id;
    }

    public void setFile_id(int file_id) {
	this.file_id = file_id;
    }

    public String getDirectory_id() {
	return directory_id;
    }

    public void setDirectory_id(String directory_id) {
	this.directory_id = directory_id;
    }

    public int getType_id() {
	return type_id;
    }

    public void setType_id(int type_id) {
	this.type_id = type_id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public int getAuthor() {
	return author;
    }

    public void setAuthor(int author) {
	this.author = author;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getVersion() {
	return version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public int getScore() {
	return score;
    }

    public void setScore(int score) {
	this.score = score;
    }

    public int getDir_type() {
	return dir_type;
    }

    public void setDir_type(int dir_type) {
	this.dir_type = dir_type;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getFile_name() {
	return file_name;
    }

    public void setFile_name(String file_name) {
	this.file_name = file_name;
    }

    public String getFile_type() {
	return file_type;
    }

    public void setFile_type(String file_type) {
	this.file_type = file_type;
    }

    public long getFile_size() {
	return file_size;
    }

    public void setFile_size(long file_size) {
	this.file_size = file_size;
    }

    public String getFile_path() {
	return file_path;
    }

    public void setFile_path(String file_path) {
	this.file_path = file_path;
    }

    public Date getCreate_time() {
	return create_time;
    }

    public void setCreate_time(Date create_time) {
	this.create_time = create_time;
    }

    public Date getModified_time() {
	return modified_time;
    }

    public void setModified_time(Date modified_time) {
	this.modified_time = modified_time;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public String getProject_name() {
	return project_name;
    }

    public void setProject_name(String project_name) {
	this.project_name = project_name;
    }

    public int getAppUpdateFlag() {
	return appUpdateFlag;
    }

    public void setAppUpdateFlag(int appUpdateFlag) {
	this.appUpdateFlag = appUpdateFlag;
    }

    public int getArchive() {
	return archive;
    }

    public void setArchive(int archive) {
	this.archive = archive;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int getTop() {
	return top;
    }

    public void setTop(int top) {
	this.top = top;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public int getTask_id() {
	return task_id;
    }

    public void setTask_id(int task_id) {
	this.task_id = task_id;
    }

    @Override
    public String toString() {
	return "Files [file_id=" + file_id + ", directory_id=" + directory_id + ", type_id=" + type_id + ", code=" + code + ", title=" + title
	        + ", author=" + author + ", status=" + status + ", version=" + version + ", score=" + score + ", dir_type=" + dir_type + ", path="
	        + path + ", file_name=" + file_name + ", file_type=" + file_type + ", file_size=" + file_size + ", file_path=" + file_path
	        + ", create_time=" + create_time + ", modified_time=" + modified_time + ", mark=" + mark + ", archive=" + archive + ", top=" + top
	        + ", tenant_id=" + tenant_id + ", project_id=" + project_id + ", task_id=" + task_id + ", project_name=" + project_name
	        + ", appUpdateFlag=" + appUpdateFlag + "]";
    }

    @Override
    public String getAttachment() {
	return Integer.toString(file_id);
    }

    @Override
    public void setAttachment(String attachment) {
	file_id = Integer.parseInt(attachment);
    }

    @Override
    public int getId() {
	return file_id;
    }

    @Override
    public int getTaskId() {
	return task_id;
    }
    
    public Object clone() {
    Object o = null;
    try {
        o = super.clone();
    } catch (CloneNotSupportedException e) {
        e.printStackTrace();
    }
    return o;
    }
}
