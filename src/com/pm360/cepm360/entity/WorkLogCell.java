package com.pm360.cepm360.entity;

@SuppressWarnings("serial")
public abstract class WorkLogCell extends AttachCell {

    public abstract void setTask_id(int task_id);

    public abstract void setContent(String content);
    
    public abstract String getContent();

}
