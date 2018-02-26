package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 合同信息（收入和支出）
 * 
 * @author Andy
 * 
 */
public class Contract implements Serializable, Cloneable {

    private static final long serialVersionUID = 4199264110661400014L;
    private int contract_id;// 合同ID
    private int project_id;// 项目ＩＤ
    private int task_id;// 任务
    private String task_name;// 合同所对应的不问
    private String code;// 合同编号
    private String name;// 合同名称
    private double total;// 合同总价
    private int pay_type;// 支付形式
    private int coin;// 货币类型
    private int owner;// 责任人
    private String p_adress;// 工程地点
    private Date sign_date;// 签署日期
    private String sign_adress;// 签署地点
    private Date availability_date;// 生效日期
    private Date start_date;// 开工日期
    private Date end_date;// 竣工日期
    private int duration;// 总工期
    private String p_content;// 工程内容
    private String quality_standard;// 质量标准
    private int progress;// 工程进度（20%）
    private int first_party;// 甲方信息
    private int second_party;// 乙方信息
    private String second_party_name;// 乙方单位名字
    private String second_party_address;// 乙方单位地址
    private String second_party_keyperson;// 乙方单位联系人
    private String second_party_keyperson_tel;// 乙方单位联系人电话
    private Date create_date;// 变更时间
    private String c_group;// 原始合同与变更合同的分组标志
    private int tenant_id;
    private int version;// 合同版本（初始版本为0）
    private int type;// 合同类型（1：工程合同 2：物资采购合同）
    private String mark;// 变更备注
    private double total_change;// 变更记录
    private double paid;// 已支付或者回款
    private String attachments;// 附件，多个附件以逗号分开 1,2

    public Object clone() {
	Object o = null;
	try {
	    o = super.clone();
	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	}
	return o;
    }

    public int getContract_id() {
	return contract_id;
    }

    public void setContract_id(int contract_id) {
	this.contract_id = contract_id;
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

    public String getTask_name() {
	return task_name;
    }

    public void setTask_name(String task_name) {
	this.task_name = task_name;
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

    public double getTotal() {
	return total;
    }

    public void setTotal(double total) {
	this.total = total;
    }

    public int getPay_type() {
	return pay_type;
    }

    public void setPay_type(int pay_type) {
	this.pay_type = pay_type;
    }

    public int getCoin() {
	return coin;
    }

    public void setCoin(int coin) {
	this.coin = coin;
    }

    public int getOwner() {
	return owner;
    }

    public void setOwner(int owner) {
	this.owner = owner;
    }

    public String getP_adress() {
	return p_adress;
    }

    public void setP_adress(String p_adress) {
	this.p_adress = p_adress;
    }

    public Date getSign_date() {
	return sign_date;
    }

    public void setSign_date(Date sign_date) {
	this.sign_date = sign_date;
    }

    public String getSign_adress() {
	return sign_adress;
    }

    public void setSign_adress(String sign_adress) {
	this.sign_adress = sign_adress;
    }

    public Date getAvailability_date() {
	return availability_date;
    }

    public void setAvailability_date(Date availability_date) {
	this.availability_date = availability_date;
    }

    public Date getStart_date() {
	return start_date;
    }

    public void setStart_date(Date start_date) {
	this.start_date = start_date;
    }

    public Date getEnd_date() {
	return end_date;
    }

    public void setEnd_date(Date end_date) {
	this.end_date = end_date;
    }

    public int getDuration() {
	return duration;
    }

    public void setDuration(int duration) {
	this.duration = duration;
    }

    public String getP_content() {
	return p_content;
    }

    public void setP_content(String p_content) {
	this.p_content = p_content;
    }

    public String getQuality_standard() {
	return quality_standard;
    }

    public void setQuality_standard(String quality_standard) {
	this.quality_standard = quality_standard;
    }

    public int getProgress() {
	return progress;
    }

    public void setProgress(int progress) {
	this.progress = progress;
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

    public String getSecond_party_name() {
        return second_party_name;
    }

    public void setSecond_party_name(String second_party_name) {
        this.second_party_name = second_party_name;
    }

    public String getSecond_party_address() {
        return second_party_address;
    }

    public void setSecond_party_address(String second_party_address) {
        this.second_party_address = second_party_address;
    }

    public String getSecond_party_keyperson() {
        return second_party_keyperson;
    }

    public void setSecond_party_keyperson(String second_party_keyperson) {
        this.second_party_keyperson = second_party_keyperson;
    }

    public String getSecond_party_keyperson_tel() {
        return second_party_keyperson_tel;
    }

    public void setSecond_party_keyperson_tel(String second_party_keyperson_tel) {
        this.second_party_keyperson_tel = second_party_keyperson_tel;
    }

    public Date getCreate_date() {
	return create_date;
    }

    public void setCreate_date(Date create_date) {
	this.create_date = create_date;
    }

    public String getC_group() {
	return c_group;
    }

    public void setC_group(String c_group) {
	this.c_group = c_group;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    public int getVersion() {
	return version;
    }

    public void setVersion(int version) {
	this.version = version;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    public String getMark() {
	return mark;
    }

    public void setMark(String mark) {
	this.mark = mark;
    }

    public double getTotal_change() {
	return total_change;
    }

    public void setTotal_change(double total_change) {
	this.total_change = total_change;
    }

    public double getPaid() {
	return paid;
    }

    public void setPaid(double paid) {
	this.paid = paid;
    }

    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Override
    public String toString() {
	return "Contract [contract_id=" + contract_id + ", project_id=" + project_id + ", task_id=" + task_id + ", task_name=" + task_name
	        + ", code=" + code + ", name=" + name + ", total=" + total + ", pay_type=" + pay_type + ", coin=" + coin + ", owner=" + owner
	        + ", p_adress=" + p_adress + ", sign_date=" + sign_date + ", sign_adress=" + sign_adress + ", availability_date=" + availability_date
	        + ", start_date=" + start_date + ", end_date=" + end_date + ", duration=" + duration + ", p_content=" + p_content
	        + ", quality_standard=" + quality_standard + ", progress=" + progress + ", first_party=" + first_party + ", second_party="
	        + second_party + ", second_party_name=" + second_party_name + ", second_party_address=" + second_party_address
	        + ", second_party_keyperson=" + second_party_keyperson + ", second_party_keyperson_tel=" + second_party_keyperson_tel
	        + ", create_date=" + create_date + ", c_group=" + c_group + ", tenant_id=" + tenant_id + ", version=" + version + ", type=" + type
	        + ", mark=" + mark + ", total_change=" + total_change + ", paid=" + paid + ", attachments=" + attachments + "]";
    }

}
