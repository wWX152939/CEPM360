package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public abstract class FeedbackCell implements Serializable {

    public abstract int getFeedback_id();
    
    public abstract void setFeedback_id(int feedback_id);
    
    //Task 的逻辑处理
    public abstract Date getActual_start_time();
    
    public abstract void setActual_start_time(Date actual_start_time);

    public abstract Date getActual_end_time();
    
    public abstract void setActual_end_time(Date actual_end_time);
    
    public abstract int getStatus();

    public abstract void setStatus(int status);
    
    public abstract int getProgress();

    public abstract void setProgress(int progress);
    
    public abstract String getMark();

    public abstract void setMark(String mark);
    
    public abstract void setCc_user(String cc_user);

    public abstract String getCc_user();
    
    public abstract int getTask_id();
    
    public abstract void setTask_id(int task_id);
    
    public abstract void setChange_id(String change_id);
    
    public abstract void setTask_name(String task_name);
    
    public abstract void setFeedback_creater(int feedback_creater);

    public abstract void setParents_user(int parents_user);
    
    public abstract int getParents_user();
}
