package com.pm360.cepm360.entity;

import java.io.Serializable;

public class APPProperties implements Serializable {

    private static final long serialVersionUID = -7976246723799467290L;
    private int appVersionCode;
    private String appVersionName;
    private String appDescript;// 版本描述
    private String downloadaddress;//

    public int getAppVersionCode() {
	return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
	this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
	return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
	this.appVersionName = appVersionName;
    }

    public String getAppDescript() {
	return appDescript;
    }

    public void setAppDescript(String appDescript) {
	this.appDescript = appDescript;
    }

    public String getDownloadaddress() {
        return downloadaddress;
    }

    public void setDownloadaddress(String downloadaddress) {
        this.downloadaddress = downloadaddress;
    }

    @Override
    public String toString() {
	return "APPProperties [appVersionCode=" + appVersionCode + ", appVersionName=" + appVersionName + ", appDescript=" + appDescript
	        + ", downloadaddress=" + downloadaddress + "]";
    }

}
