package com.pm360.cepm360.app.module.home.portal;

import java.util.Date;

public class PortailListItem {
    private int iconId;
    private String title;
    private String tag;
    private Date date;
    
    private int message_id;
    private int type_id;
    private int type;
    
    public int getIconId() {
        return iconId;
    }
    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public int getType_id() {
        return type_id;
    }
    public void setType_id(int type_id) {
        this.type_id = type_id;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getMessage_id() {
        return message_id;
    }
    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
	@Override
	public String toString() {
		return "PortailListItem [iconId=" + iconId + ", title=" + title
				+ ", tag=" + tag + ", date=" + date + ", message_id="
				+ message_id + ", type_id=" + type_id + ", type=" + type + "]";
	}
    
}
