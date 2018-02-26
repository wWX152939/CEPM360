package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 合同变更
 * 
 * @author Andy
 * 
 */
public class Contract_change implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -9151843104413225818L;
    private int id;// 自增长号
    // private int first_project_id;// 甲方项目ID
    private int project_id;//
    // private int second_project_id;// 乙方项目ID
    private String contract_code;// 合同代码
    private String contract_name;// 合同名称
    // private int first_contract_id;// 甲方合同ID
    // private int second_contract_id;// 乙方合同ID
    private String code;// 变更编号
    private String name;// 变更名称
    private Date apply_date;// 申请日期
    private Date pass_date;// 生效日期
    private int adjust_period;// 调整后工期
    private Date adjust_finish_date;// 调整后完工日期
    private int sender;// 发起方
    private int receiver;// 接收方
    private int sender_contact;// 发起方联系人
    private int receive_contact;// 收方联系人
    private String mark;// 备注
    private String attachments;// 附件，多个附件以逗号分开 1,2
    private int status;// 合同变更状态 GLOBAL.CONTRACT_CHANGE_STATUS
    private Date operate_time;// 操作时间
    private int lw_count;// 变更往来数量

    private double yshtzj;// 原始合同总价
    private double bqbgk;// 本期变更款
    private Date ydhtwgrq;// 原定合同完工日期
    private int owner;// 责任人
    private int first_party;// 甲方
    private int second_party;// 乙方
    
    private int tenant_id;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getProject_id() {
	return project_id;
    }

    public void setProject_id(int project_id) {
	this.project_id = project_id;
    }

    public String getContract_code() {
	return contract_code;
    }

    public void setContract_code(String contract_code) {
	this.contract_code = contract_code;
    }

    public String getContract_name() {
	return contract_name;
    }

    public void setContract_name(String contract_name) {
	this.contract_name = contract_name;
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

    public Date getApply_date() {
	return apply_date;
    }

    public void setApply_date(Date apply_date) {
	this.apply_date = apply_date;
    }

    public Date getPass_date() {
	return pass_date;
    }

    public void setPass_date(Date pass_date) {
	this.pass_date = pass_date;
    }

    public int getAdjust_period() {
	return adjust_period;
    }

    public void setAdjust_period(int adjust_period) {
	this.adjust_period = adjust_period;
    }

    public Date getAdjust_finish_date() {
	return adjust_finish_date;
    }

    public void setAdjust_finish_date(Date adjust_finish_date) {
	this.adjust_finish_date = adjust_finish_date;
    }

    public int getSender() {
	return sender;
    }

    public void setSender(int sender) {
	this.sender = sender;
    }

    public int getReceiver() {
	return receiver;
    }

    public void setReceiver(int receiver) {
	this.receiver = receiver;
    }

    public int getSender_contact() {
	return sender_contact;
    }

    public void setSender_contact(int sender_contact) {
	this.sender_contact = sender_contact;
    }

    public int getReceive_contact() {
	return receive_contact;
    }

    public void setReceive_contact(int receive_contact) {
	this.receive_contact = receive_contact;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(int status) {
	this.status = status;
    }

    public Date getOperate_time() {
	return operate_time;
    }

    public void setOperate_time(Date operate_time) {
	this.operate_time = operate_time;
    }

    public int getLw_count() {
	return lw_count;
    }

    public void setLw_count(int lw_count) {
	this.lw_count = lw_count;
    }

    public double getYshtzj() {
	return yshtzj;
    }

    public void setYshtzj(double yshtzj) {
	this.yshtzj = yshtzj;
    }

    public double getBqbgk() {
	return bqbgk;
    }

    public void setBqbgk(double bqbgk) {
	this.bqbgk = bqbgk;
    }

    public Date getYdhtwgrq() {
	return ydhtwgrq;
    }

    public void setYdhtwgrq(Date ydhtwgrq) {
	this.ydhtwgrq = ydhtwgrq;
    }

    public int getOwner() {
	return owner;
    }

    public void setOwner(int owner) {
	this.owner = owner;
    }

    public int getFirst_party() {
	return first_party;
    }

    public void setFirst_party(int first_party) {
	this.first_party = first_party;
    }

    public int getSecond_party() {
	return second_party;
    }

    public void setSecond_party(int second_party) {
	this.second_party = second_party;
    }

    public int getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
        this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "Contract_change [id=" + id + ", project_id=" + project_id + ", contract_code=" + contract_code + ", contract_name=" + contract_name
	        + ", code=" + code + ", name=" + name + ", apply_date=" + apply_date + ", pass_date=" + pass_date + ", adjust_period="
	        + adjust_period + ", adjust_finish_date=" + adjust_finish_date + ", sender=" + sender + ", receiver=" + receiver
	        + ", sender_contact=" + sender_contact + ", receive_contact=" + receive_contact + ", mark=" + mark + ", attachments=" + attachments
	        + ", status=" + status + ", operate_time=" + operate_time + ", lw_count=" + lw_count + ", yshtzj=" + yshtzj + ", bqbgk=" + bqbgk
	        + ", ydhtwgrq=" + ydhtwgrq + ", owner=" + owner + ", first_party=" + first_party + ", second_party=" + second_party + ", tenant_id="
	        + tenant_id + "]";
    }
}
