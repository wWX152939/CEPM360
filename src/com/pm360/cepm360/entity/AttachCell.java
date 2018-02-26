package com.pm360.cepm360.entity;

import java.io.Serializable;


@SuppressWarnings("serial")
public abstract class AttachCell implements Serializable {
	
	public abstract String getAttachment();
	public abstract void setAttachment(String attachment);
	public abstract int getId();
	
}
