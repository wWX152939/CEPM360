package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 协作
 * 
 * @author Andy
 * 
 */
public class Cooperation extends Expandable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3718296039958478854L;
    private int cooperation_id;
    private int launch_project_id;// 发起方项目ID
    private int accept_project_id;// 接收方项目
    private String project_name;
    private int accept_company;// 协作接受单位
    private Date accept_date;// 协作接受时间
    private int accept_person;// 协作接受人
    private int launch_company;// 协作发起单位
    private Date launch_date;// 协作发起时间
    private int launch_person;// 协作发起人
    private int status;// 协作状态
    private int tenant_id;
    private int company_type;
    private String launch_contact_window;// 协作发起联系窗口，联系人1,2
    private String accept_contact_window;// 协作接收联系窗口，联系人1,2

    private String notice_person;// 协作通知人
    private int message_id;

    public int getCompany_type() {
	return company_type;
    }

    public void setCompany_type(int company_type) {
	this.company_type = company_type;
    }

    public int getCooperation_id() {
	return cooperation_id;
    }

    public void setCooperation_id(int cooperation_id) {
	this.cooperation_id = cooperation_id;
    }

    public int getLaunch_project_id() {
	return launch_project_id;
    }

    public void setLaunch_project_id(int launch_project_id) {
	this.launch_project_id = launch_project_id;
    }

    public int getAccept_project_id() {
	return accept_project_id;
    }

    public void setAccept_project_id(int accept_project_id) {
	this.accept_project_id = accept_project_id;
    }

    public String getProject_name() {
	return project_name;
    }

    public void setProject_name(String project_name) {
	this.project_name = project_name;
    }

    public int getAccept_company() {
	return accept_company;
    }

    public void setAccept_company(int accept_company) {
	this.accept_company = accept_company;
    }

    public Date getAccept_date() {
	return accept_date;
    }

    public void setAccept_date(Date accept_date) {
	this.accept_date = accept_date;
    }

    public int getAccept_person() {
	return accept_person;
    }

    public void setAccept_person(int accept_person) {
	this.accept_person = accept_person;
    }

    public int getLaunch_company() {
	return launch_company;
    }

    public void setLaunch_company(int launch_company) {
	this.launch_company = launch_company;
    }

    public Date getLaunch_date() {
	return launch_date;
    }

    public void setLaunch_date(Date launch_date) {
	this.launch_date = launch_date;
    }

    public int getLaunch_person() {
	return launch_person;
    }

    public void setLaunch_person(int launch_person) {
	this.launch_person = launch_person;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public String getLaunch_contact_window() {
	return launch_contact_window;
    }

    public void setLunch_contact_window(String launch_contact_window) {
	this.launch_contact_window = launch_contact_window;
    }

    public String getAccept_contact_window() {
	return accept_contact_window;
    }

    public void setAccept_contact_window(String accept_contact_window) {
	this.accept_contact_window = accept_contact_window;
    }

    public String getNotice_person() {
	return notice_person;
    }

    public void setNotice_person(String notice_person) {
	this.notice_person = notice_person;
    }

    @Override
    public int getId() {
	return cooperation_id;
    }

    @Override
    public int getParents_id() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public void setParentsId(int parent_id) {
	// TODO Auto-generated method stub

    }

    public int getMessage_id() {
	return message_id;
    }

    public void setMessage_id(int message_id) {
	this.message_id = message_id;
    }

    @Override
    public String toString() {
	return "Cooperation [cooperation_id=" + cooperation_id + ", launch_project_id=" + launch_project_id + ", accept_project_id="
	        + accept_project_id + ", project_name=" + project_name + ", accept_company=" + accept_company + ", accept_date=" + accept_date
	        + ", accept_person=" + accept_person + ", launch_company=" + launch_company + ", launch_date=" + launch_date + ", launch_person="
	        + launch_person + ", status=" + status + ", tenant_id=" + tenant_id + ", company_type=" + company_type + ", launch_contact_window="
	        + launch_contact_window + ", accept_contact_window=" + accept_contact_window + ", notice_person=" + notice_person + ", message_id="
	        + message_id + "]";
    }

}
