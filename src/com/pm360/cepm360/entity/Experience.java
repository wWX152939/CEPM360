package com.pm360.cepm360.entity;

/**
 * 风控-经验库
 * 
 * @author Andy
 * 
 */
public class Experience extends AttachCell {

    /**
     * 
     */
    private static final long serialVersionUID = -7864121026288426470L;

    private int id;
    private String title;// 风控标题
    private String content;// 风控内容
    private String step;// 风控措施
    private String result;// 风控效果
    private String attachment;// file_id，例如1,2,3
    private int tenant_id;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public String getStep() {
	return step;
    }

    public void setStep(String step) {
	this.step = step;
    }

    public String getResult() {
	return result;
    }

    public void setResult(String result) {
	this.result = result;
    }

    public String getAttachment() {
	return attachment;
    }

    public void setAttachment(String attachment) {
	this.attachment = attachment;
    }

    public int getTenant_id() {
	return tenant_id;
    }

    public void setTenant_id(int tenant_id) {
	this.tenant_id = tenant_id;
    }

    @Override
    public String toString() {
	return "Experience [id=" + id + ", title=" + title + ", content=" + content + ", step=" + step + ", result=" + result + ", attachment="
	        + attachment + ", tenant_id=" + tenant_id + "]";
    }

}
